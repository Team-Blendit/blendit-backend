package kr.blendit.api.blending.service;

import kr.blendit.api.blending.constant.BlendingUserGrade;
import kr.blendit.api.blending.constant.BlendingStatus;
import kr.blendit.api.blending.constant.JoinStatus;
import kr.blendit.api.blending.domain.Blending;
import kr.blendit.api.blending.domain.BlendingBookmark;
import kr.blendit.api.blending.domain.BlendingUser;
import kr.blendit.api.blending.dto.request.BlendingCreateRequest;
import kr.blendit.api.blending.dto.response.BlendingDetailResponse;
import kr.blendit.api.blending.dto.request.BlendingUpdateRequest;
import kr.blendit.api.blending.repository.BlendingBookmarkRepository;
import kr.blendit.api.blending.repository.BlendingRepository;
import kr.blendit.api.blending.repository.BlendingUserRepository;
import kr.blendit.api.keyword.domain.Keyword;
import kr.blendit.api.keyword.repository.KeywordRepository;
import kr.blendit.api.user.domain.User;
import kr.blendit.common.exception.BaseErrorCode;
import kr.blendit.common.exception.BaseException;
import kr.blendit.common.security.jwt.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BlendingService {

    private final BlendingRepository blendingRepository;
    private final BlendingUserRepository blendingUserRepository;
    private final KeywordRepository keywordRepository;
    private final BlendingBookmarkRepository blendingBookmarkRepository;

    /**
     * 블렌딩 생성
     */
    public void create(User user, BlendingCreateRequest blendingCreateRequest, List<Keyword> keywordList) {

        Blending blending = Blending.create(blendingCreateRequest);
        blending.addKeywords(keywordList);

        BlendingUser blendingUser = blending.addParticipant(
                user,
                BlendingUserGrade.HOST,
                null,
                JoinStatus.APPROVED
        );

        blendingRepository.save(blending);
        blendingUserRepository.save(blendingUser);
    }


    /**
     * 블렌딩 논리 삭제
     */
    public void delete(String userUuid, String blendingUuid) {

        Blending blending = getBlending(blendingUuid);

        // Host 인지 검증
        if(!blendingUserRepository.existsByBlendingAndUser_UuidAndBlendingUserGrade(blending, userUuid, BlendingUserGrade.HOST)) {
            throw new BaseException(BaseErrorCode.BLENDING_PERMISSION_DENIED);
        }

        blending.delete();
    }


    /**
     * 블렌딩 정보 수정
     */
    public void update(String userUuid, String blendingUuid, BlendingUpdateRequest blendingUpdateRequest) {

        Blending blending = getBlending(blendingUuid);

        if(!blendingUserRepository.existsByBlendingAndUser_UuidAndBlendingUserGrade(blending, userUuid, BlendingUserGrade.HOST)) {
            throw new BaseException(BaseErrorCode.BLENDING_PERMISSION_DENIED);
        }

        if(blendingUpdateRequest.getCapacity() != null) {
            long participantCount = blendingUserRepository.countByBlendingAndJoinStatus(blending, JoinStatus.APPROVED);
            if(participantCount > blendingUpdateRequest.getCapacity()) {
                throw new BaseException(BaseErrorCode.BLENDING_INVALID_CAPACITY);
            }
        }

        if(blendingUpdateRequest.getSchedule() != null
                && blendingUpdateRequest.getSchedule().isBefore(LocalDateTime.now())) {
            throw new BaseException(BaseErrorCode.BLENDING_INVALID_SCHEDULE_TIME);
        }

        if (blendingUpdateRequest.getKeywordUuidList() != null) {
            List<Keyword> newKeywords = keywordRepository.findAllByUuidIn(blendingUpdateRequest.getKeywordUuidList());
            blending.updateKeyword(newKeywords);
        }

        blending.update(blendingUpdateRequest);
    }


    /**
     * 블렌딩 상태 변경
     *
     * @apiNote 아직 피그마 디자인에 상태 변경 기능이 나오지 않았습니다.
     *          보통 상태 변경은 수정 창에서 함께 하는 것보다는
     *          블렌딩 목록 화면에서 토글로 수정할 수 있게 하는게 좋지 않을까해서 정보 수정과 API 분리했습니다.
     */
    public void updateStatus(String userUuid, String blendingUuid, BlendingStatus blendingStatus) {

        Blending blending = getBlending(blendingUuid);

        // Host 인지 검증
        if(!blendingUserRepository.existsByBlendingAndUser_UuidAndBlendingUserGrade(blending, userUuid, BlendingUserGrade.HOST)) {
            throw new BaseException(BaseErrorCode.BLENDING_PERMISSION_DENIED);
        }

        blending.updateStatus(blendingStatus);
    }


    /**
     * 블렌딩 상세 정보 조회
     *
     * @return BlendingDetailResponse 참여자를 포함한 블렌딩 관련 모든 데이터
     */
    public BlendingDetailResponse getBlendingDetail(CurrentUser currentUser, String blendingUuid) {
        String userUuid = currentUser != null ? currentUser.getUserUuid() : null;

        // todo: 추후 currentUser가 해당 블렌딩의 참여자가 아니라면 BlendingStatus가 COMPLETED, CANCELED는 볼 수 없도록 수정 필요

        Blending blending = blendingRepository.findByUuidWithKeywords(blendingUuid)
                .orElseThrow(() -> new BaseException(BaseErrorCode.BLENDING_NOT_FOUND));

        boolean isBookmark = false;
        boolean isHost = false;
        long bookmarkCount = blendingBookmarkRepository.countByBlending(blending);

        if (userUuid != null && !userUuid.equals("anonymousUser")) {
            isBookmark = blendingBookmarkRepository.existsByBlendingAndUser_Uuid(blending, userUuid);
            isHost = blendingUserRepository.existsByBlendingAndUser_UuidAndBlendingUserGrade(
                    blending, userUuid, BlendingUserGrade.HOST);
        }

        List<JoinStatus> joinStatuses = new ArrayList<>(List.of(JoinStatus.APPROVED));
        if(isHost) {
            joinStatuses.add(JoinStatus.PENDING);
        }

        List<BlendingUser> blendingUsers = blendingUserRepository.findByBlendingAndJoinStatusInWithUser(
                blending, joinStatuses);

        String currentUserJoinStatus = "GUEST";
        if(userUuid != null) {
            currentUserJoinStatus = getCurrentUserJoinStatus(blending, userUuid);
        }

        return BlendingDetailResponse.from(blending, blendingUsers, bookmarkCount, isBookmark, isHost, currentUserJoinStatus);
    }


    public Blending getBlending(String blendingUuid) {
        return blendingRepository.findByUuid(blendingUuid)
                .orElseThrow(() -> new BaseException(BaseErrorCode.BLENDING_NOT_FOUND));
    }

    private String getCurrentUserJoinStatus(Blending blending, String userUuid) {
        BlendingUser CurrentBlendingUser = blendingUserRepository.findByBlendingAndUser_Uuid(blending, userUuid)
                .orElse(null);

        if(CurrentBlendingUser == null) {
            return "NOT_APPLIED";
        } else {
            return String.valueOf(CurrentBlendingUser.getJoinStatus());
        }
    }

    /**
     * 블렌딩 북마크 추가
     */
    public void addBookmark(User user, Blending blending) {

        if(blendingBookmarkRepository.existsByBlendingAndUser_Uuid(blending, user.getUuid())) {
            throw new BaseException(BaseErrorCode.ALREADY_BOOKMARKED);
        }

        BlendingBookmark blendingBookmark = BlendingBookmark.create(user, blending);
        blendingBookmarkRepository.save(blendingBookmark);
    }

    /**
     * 블렌딩 북마크 제거
     */
    public void removeBookmark(User user, Blending blending) {

        BlendingBookmark blendingBookmark = blendingBookmarkRepository.findByBlendingAndUser(blending, user)
                .orElseThrow(() -> new BaseException(BaseErrorCode.BOOKMARK_NOT_FOUND));

        blendingBookmarkRepository.delete(blendingBookmark);
    }

}


