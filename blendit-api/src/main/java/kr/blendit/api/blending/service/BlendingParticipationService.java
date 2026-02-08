package kr.blendit.api.blending.service;

import kr.blendit.api.blending.constant.BlendingUserGrade;
import kr.blendit.api.blending.constant.BlendingStatus;
import kr.blendit.api.blending.constant.JoinStatus;
import kr.blendit.api.blending.domain.Blending;
import kr.blendit.api.blending.domain.BlendingUser;
import kr.blendit.api.blending.dto.request.BlendingApplyRequest;
import kr.blendit.api.blending.repository.BlendingUserRepository;
import kr.blendit.api.user.domain.User;
import kr.blendit.common.exception.BaseErrorCode;
import kr.blendit.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BlendingParticipationService {

    private final BlendingUserRepository blendingUserRepository;

    /**
     * 블렌딩 참여 신청
     *
     * @apiNote MEMBER 권한으로 저장되며 JoinStatus 값은 autoApproval에 따라 결정됩니다.
     * @apiNote 신청이 거절되면 다시 신청할 수 없습니다.
     */
    public void apply(User user, Blending blending, BlendingApplyRequest blendingApplyRequest) {

        // 모집중이 아닐 경우 예외
        if(!blending.getStatus().equals(BlendingStatus.RECRUITING)) {
            throw new BaseException(BaseErrorCode.BLENDING_NOT_RECRUITING);
        }

        // 인원 수 검증
        long currentUserCount = blendingUserRepository.countByBlendingAndJoinStatus(blending, JoinStatus.APPROVED);
        if(blending.getCapacity() <= currentUserCount) {
            throw new BaseException(BaseErrorCode.BLENDING_FULL);
        }
        
        // 자동 승인 여부에 따른 상태값 설정
        JoinStatus joinStatus = JoinStatus.PENDING;
        if(blending.getAutoApproval()) {
            joinStatus = JoinStatus.APPROVED;
        }

        BlendingUser blendingUser = blendingUserRepository.findByBlendingAndUser(blending, user)
                        .orElse(null);

        // 취소 상태라면 상태 변경, 신청 내역이 없다면 데이터 생성
        if(blendingUser != null) {
            if(blendingUser.getJoinStatus().equals(JoinStatus.CANCEL)) { // 참여 이력 검증
                blendingUser.updateJoinStatus(joinStatus);
            } else {
                throw new BaseException(BaseErrorCode.BLENDING_ALREADY_APPLIED);
            }
        } else {
            BlendingUser newBlendingUser = blending.addParticipant(
                    user,
                    BlendingUserGrade.MEMBER,
                    blendingApplyRequest.getMessage(),
                    joinStatus);

            blendingUserRepository.save(newBlendingUser);
        }

        if(joinStatus.equals(JoinStatus.APPROVED)) {
            long finalUserCount = blendingUserRepository.countByBlendingAndJoinStatus(blending, JoinStatus.APPROVED);

            // Todo: 추후 락으로 동시성 관리
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
     * 블렌딩 참여 신청 취소 (JoinStatus.CANCEL로 변경합니다.)
     *
     * @apiNote 신청 취소 후 참여인원이 정원보다 적더라도 마감 상태가 유지됩니다.
     */
    public void cancel(String userUuid, Blending blending) {

        BlendingUser blendingUser = blendingUserRepository.findByBlendingAndUser_Uuid(blending, userUuid)
                .orElseThrow(() -> new BaseException(BaseErrorCode.BLENDING_NOT_APPLIED));

        // 호스트는 탈퇴 불가
        if(blendingUser.getBlendingUserGrade().equals(BlendingUserGrade.HOST)) {
            throw new BaseException(BaseErrorCode.BLENDING_HOST_CANNOT_LEAVE);
        }

        // 이미 거절된 신청의 경우 취소 불가
        if(blendingUser.getJoinStatus().equals(JoinStatus.REJECTED)) {
            throw new BaseException(BaseErrorCode.BLENDING_ALREADY_PROCESSED);
        }

        blendingUser.cancelParticipant();
    }


    /**
     * 블렌딩 신청 내역 삭제(BlendingUser 데이터를 삭제합니다.)
     */
    public void delete(String userUuid, Blending blending) {

        BlendingUser blendingUser = blendingUserRepository.findByBlendingAndUser_Uuid(blending, userUuid)
                .orElseThrow(() -> new BaseException(BaseErrorCode.BLENDING_NOT_APPLIED));

        // 취소 상태의 신청 내역이 아니라면 예외
        if(!blendingUser.getJoinStatus().equals(JoinStatus.CANCEL)) {
            throw new BaseException(BaseErrorCode.BLENDING_APPLICATION_NOT_CANCELLED);
        }

        blending.deleteParticipant(blendingUser);
    }


    /**
     * 블렌딩 참여 승인
     */
    public void approve(String userUuid, Blending blending, String participantUuid) {

        // Host 검증
        if(!blendingUserRepository.existsByBlendingAndUser_UuidAndBlendingUserGrade(blending, userUuid, BlendingUserGrade.HOST)) {
            throw new BaseException(BaseErrorCode.BLENDING_PERMISSION_DENIED);
        }

        // 모집중인 블렌딩인지 검증
        if(!blending.getStatus().equals(BlendingStatus.RECRUITING)) {
            throw new BaseException(BaseErrorCode.BLENDING_NOT_RECRUITING);
        }

        // 요청한 사용자가 해당 블렌딩에 신청했는지 검증
        BlendingUser participantUser = blendingUserRepository.findByBlendingAndUser_Uuid(blending, participantUuid)
                .orElseThrow(() -> new BaseException(BaseErrorCode.BLENDING_NOT_APPLIED));

        // 승인 대기 상태인지 검증
        if(!participantUser.getJoinStatus().equals(JoinStatus.PENDING)) {
            throw new BaseException(BaseErrorCode.BLENDING_ALREADY_PROCESSED);
        }

        // 블렌딩 정원이 남아있는지 검증
        long currentUserCount = blendingUserRepository.countByBlendingAndJoinStatus(blending, JoinStatus.APPROVED);
        if(blending.getCapacity() <= currentUserCount) {
            throw new BaseException(BaseErrorCode.BLENDING_FULL);
        }

        participantUser.updateJoinStatus(JoinStatus.APPROVED);

        // 정원이 다 찼다면 마감 상태로 변경
        if (currentUserCount + 1 >= blending.getCapacity()) {
            blending.updateStatus(BlendingStatus.RECRUITMENT_CLOSED);
        }
    }


    /**
     * 블렌딩 참여 거부
     */
    public void reject(String userUuid, Blending blending, String participantUuid) {

        // Host 검증
        if(!blendingUserRepository.existsByBlendingAndUser_UuidAndBlendingUserGrade(blending, userUuid, BlendingUserGrade.HOST)) {
            throw new BaseException(BaseErrorCode.BLENDING_PERMISSION_DENIED);
        }

        // 요청한 사용자가 해당 블렌딩에 신청했는지 검증
        BlendingUser participantUser = blendingUserRepository.findByBlendingAndUser_Uuid(blending, participantUuid)
                .orElseThrow(() -> new BaseException(BaseErrorCode.BLENDING_NOT_APPLIED));

        // 승인 대기 상태인지 검증
        if(!participantUser.getJoinStatus().equals(JoinStatus.PENDING)) {
            throw new BaseException(BaseErrorCode.BLENDING_ALREADY_PROCESSED);
        }

        participantUser.updateJoinStatus(JoinStatus.REJECTED);
    }


}
