package kr.blendit.api.blending.service;

import kr.blendit.api.blending.constant.BlendingGrade;
import kr.blendit.api.blending.constant.BlendingStatus;
import kr.blendit.api.blending.constant.JoinStatus;
import kr.blendit.api.blending.domain.Blending;
import kr.blendit.api.blending.domain.BlendingUser;
import kr.blendit.api.blending.dto.request.BlendingApplyRequest;
import kr.blendit.api.blending.repository.BlendingRepository;
import kr.blendit.api.blending.repository.BlendingUserRepository;
import kr.blendit.api.user.domain.User;
import kr.blendit.api.user.repository.UserRepository;
import kr.blendit.common.exception.BaseErrorCode;
import kr.blendit.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlendingParticipationService {

    private final BlendingRepository blendingRepository;
    private final UserRepository userRepository;
    private final BlendingUserRepository blendingUserRepository;

    /**
     * 블렌딩 참여 신청
     *
     * @apiNote MEMBER 권한으로 저장되며 JoinStatus 값은 autoApproval에 따라 결정됩니다.
     */
    public void apply(String userUuid, String blendingUuid, BlendingApplyRequest blendingApplyRequest) {

        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new BaseException(BaseErrorCode.USER_NOT_FOUND));;

        Blending blending = blendingRepository.findByUuid(blendingUuid)
                .orElseThrow(() -> new BaseException(BaseErrorCode.BLENDING_NOT_FOUND));

        // 모집중이 아닐 경우 예외
        if(!blending.getStatus().equals(BlendingStatus.RECRUITING)) {
            throw new BaseException(BaseErrorCode.BLENDING_NOT_RECRUITING);
        }

        // 참여 이력 검증 (중복 신청 방지)
        if(blendingUserRepository.existsByBlendingAndUser(blending, user)) {
            throw new BaseException(BaseErrorCode.BLENDING_ALREADY_APPLIED);
        }

        // todo: 동시성 문제 고려
        // 인원 수 검증
        long currentUserCount = blendingUserRepository.countByBlendingAndJoinStatusIn(blending, List.of(JoinStatus.HOST, JoinStatus.APPROVED));
        if(blending.getCapacity() <= currentUserCount) {
            throw new BaseException(BaseErrorCode.BLENDING_FULL);
        }
        
        // 자동 승인 여부에 따른 상태값 설정
        JoinStatus joinStatus = JoinStatus.PENDING;
        if(blending.getAutoApproval()) {
            joinStatus = JoinStatus.APPROVED;
        }

        BlendingUser blendingUser = blending.addParticipant(
                user,
                BlendingGrade.MEMBER,
                blendingApplyRequest.getMessage(),
                joinStatus);

        blendingUserRepository.save(blendingUser);

        if(joinStatus.equals(JoinStatus.APPROVED)) {
            long finalUserCount = blendingUserRepository.countByBlendingAndJoinStatusIn(blending, List.of(JoinStatus.HOST, JoinStatus.APPROVED));

            // 저장 후 정원이 초과된 경우 예외 -> 롤백
            if (finalUserCount > blending.getCapacity()) {
                throw new BaseException(BaseErrorCode.BLENDING_FULL);
            }

            // 저장 후 정원이 가득찬 경우 마감으로 변경
            if (finalUserCount == blending.getCapacity()) {
                blending.updateStatus(BlendingStatus.RECRUITMENT_CLOSED);
            }
        }
    }


    /**
     * 블렌딩 참여 신청 취소
     *
     * @apiNote 현재는 신청 취소 후 참여인원이 정원보다 적을 시 무조건 모집중으로 변경됩니다.
     */
    public void cancel(String userUuid, String blendingUuid) {

        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new BaseException(BaseErrorCode.USER_NOT_FOUND));

        Blending blending = blendingRepository.findByUuid(blendingUuid)
                .orElseThrow(() -> new BaseException(BaseErrorCode.BLENDING_NOT_FOUND));


        BlendingUser blendingUser = blendingUserRepository.findByBlendingAndUser(blending, user)
                .orElseThrow(() -> new BaseException(BaseErrorCode.BLENDING_NOT_APPLIED));

        JoinStatus joinStatus = blendingUser.getJoinStatus();
        blending.deleteParticipant(blendingUser);

        // 승인된 유저가 탈퇴 시, 현재 참여 인원이 정원보다 적으면서, 마감된 상태라면 모집 중으로 자동 상태 변경
        if(joinStatus.equals(JoinStatus.APPROVED) && blending.getStatus().equals(BlendingStatus.RECRUITMENT_CLOSED)) {

            long currentUserCount = blendingUserRepository.countByBlendingAndJoinStatusIn(blending, List.of(JoinStatus.HOST, JoinStatus.APPROVED));
            if(blending.getCapacity() > currentUserCount) {
                blending.updateStatus(BlendingStatus.RECRUITING);
            }
        }

    }


}
