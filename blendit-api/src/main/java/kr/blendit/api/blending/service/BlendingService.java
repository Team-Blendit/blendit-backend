package kr.blendit.api.blending.service;

import kr.blendit.api.blending.constant.BlendingGrade;
import kr.blendit.api.blending.constant.BlendingStatus;
import kr.blendit.api.blending.constant.JoinStatus;
import kr.blendit.api.blending.domain.Blending;
import kr.blendit.api.blending.domain.BlendingUser;
import kr.blendit.api.blending.dto.request.BlendingCreateRequest;
import kr.blendit.api.blending.dto.response.BlendingDetailResponse;
import kr.blendit.api.blending.dto.request.BlendingUpdateRequest;
import kr.blendit.api.blending.repository.BlendingBookmarkRepository;
import kr.blendit.api.blending.repository.BlendingRepository;
import kr.blendit.api.blending.repository.BlendingUserRepository;
import kr.blendit.api.blending.repository.KeywordRepository;
import kr.blendit.api.common.domain.Keyword;
import kr.blendit.api.user.domain.User;
import kr.blendit.api.user.repository.UserRepository;
import kr.blendit.common.exception.BaseErrorCode;
import kr.blendit.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlendingService {

    private final BlendingRepository blendingRepository;
    private final BlendingUserRepository blendingUserRepository;
    private final UserRepository userRepository;
    private final KeywordRepository keywordRepository;
    private final BlendingBookmarkRepository blendingBookmarkRepository;

    /**
     * 블렌딩 생성
     *
     * @apiNote 모임장은 참여인원에 HOST로 추가됩니다.
     */
    public void create(String userUuid, BlendingCreateRequest blendingCreateRequest) {

        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new BaseException(BaseErrorCode.USER_NOT_FOUND));

        Blending blending = Blending.create(blendingCreateRequest);

        List<Keyword> keywords = keywordRepository.findAllByNameIn(blendingCreateRequest.getKeywords());
        for(Keyword keyword : keywords) {
            blending.addKeyword(keyword);
        }

        BlendingUser blendingUser = blending.addParticipant(
                user,
                BlendingGrade.HOST,
                null,
                JoinStatus.HOST
        );

        blendingRepository.save(blending);
        blendingUserRepository.save(blendingUser);
    }


    /**
     * 블렌딩 논리 삭제
     */
    public void delete(String userUuid, String blendingUuid) {

        Blending blending = blendingRepository.findByUuid(blendingUuid)
                .orElseThrow(() -> new BaseException(BaseErrorCode.BLENDING_NOT_FOUND));

        // Host 인지 검증
        if(!blendingUserRepository.existsByBlendingAndUser_UuidAndBlendingGrade(blending, userUuid, BlendingGrade.HOST)) {
            throw new BaseException(BaseErrorCode.BLENDING_PERMISSION_DENIED);
        }

        blending.delete();
    }


    /**
     * 블렌딩 정보 수정
     */
    public void update(String userUuid, String blendingUuid, BlendingUpdateRequest blendingUpdateRequest) {

        Blending blending = blendingRepository.findByUuid(blendingUuid)
                .orElseThrow(() -> new BaseException(BaseErrorCode.BLENDING_NOT_FOUND));

        if(!blendingUserRepository.existsByBlendingAndUser_UuidAndBlendingGrade(blending, userUuid, BlendingGrade.HOST)) {
            throw new BaseException(BaseErrorCode.BLENDING_PERMISSION_DENIED);
        }

        if(blendingUpdateRequest.getCapacity() != null) {
            long participantCount = blendingUserRepository.countByBlendingAndJoinStatusIn(
                    blending, List.of(JoinStatus.HOST, JoinStatus.APPROVED));
            if(participantCount > blendingUpdateRequest.getCapacity()) {
                throw new BaseException(BaseErrorCode.BLENDING_INVALID_CAPACITY);
            }
        }

        if(blendingUpdateRequest.getSchedule() != null
                && blendingUpdateRequest.getSchedule().isBefore(LocalDateTime.now())) {
            throw new BaseException(BaseErrorCode.BLENDING_INVALID_SCHEDULE_TIME);
        }

        if (blendingUpdateRequest.getKeywords() != null) {
            List<Keyword> newKeywords = keywordRepository.findAllByNameIn(blendingUpdateRequest.getKeywords());
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

        Blending blending = blendingRepository.findByUuid(blendingUuid)
                .orElseThrow(() -> new BaseException(BaseErrorCode.BLENDING_NOT_FOUND));

        // Host 인지 검증
        if(!blendingUserRepository.existsByBlendingAndUser_UuidAndBlendingGrade(blending, userUuid, BlendingGrade.HOST)) {
            throw new BaseException(BaseErrorCode.BLENDING_PERMISSION_DENIED);
        }

        blending.updateStatus(blendingStatus);
    }


    /**
     * 블렌딩 상세 정보 조회
     *
     * @return BlendingDetailResponse 참여자를 포함한 블렌딩 관련 모든 데이터
     */
    public BlendingDetailResponse find(String userUuid, String blendingUuid) {

        Blending blending = blendingRepository.findByUuidWithUsers(blendingUuid)
                .orElseThrow(() -> new BaseException(BaseErrorCode.BLENDING_NOT_FOUND));

        long bookmarkCount = blendingBookmarkRepository.countByBlending(blending);
        boolean isBookmarked = false;

        if (userUuid != null && !userUuid.equals("anonymousUser")) {
            isBookmarked = blendingBookmarkRepository.existsByBlendingAndUser_Uuid(blending, userUuid);
        }

        // 접근한 유저가 해당 블렌딩의 호스트인지 검증
        boolean isHost = blending.isHost(userUuid);

        return BlendingDetailResponse.from(blending, bookmarkCount, isBookmarked, isHost);
    }
}
