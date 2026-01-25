package kr.blendit.api.blending.service;

import kr.blendit.api.blending.constant.BlendingGrade;
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
    }


}
