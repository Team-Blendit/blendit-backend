package kr.blendit.api.blending.service;

import kr.blendit.api.blending.constant.Grade;
import kr.blendit.api.blending.constant.JoinStatus;
import kr.blendit.api.blending.domain.Blending;
import kr.blendit.api.blending.domain.BlendingUser;
import kr.blendit.api.blending.domain.Keyword;
import kr.blendit.api.blending.dto.BlendingRequest;
import kr.blendit.api.blending.repository.BlendingRepository;
import kr.blendit.api.blending.repository.BlendingUserRepository;
import kr.blendit.api.blending.repository.KeywordRepository;
import kr.blendit.api.user.domain.User;
import kr.blendit.api.user.repository.UserRepository;
import kr.blendit.common.exception.BaseErrorCode;
import kr.blendit.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlendingService {

    private final BlendingRepository blendingRepository;
    private final BlendingUserRepository blendingUserRepository;
    private final UserRepository userRepository;
    private final KeywordRepository keywordRepository;

    @Transactional
    public void create(String userUuid, BlendingRequest blendingRequest) {

        Blending blending = Blending.create(
                blendingRequest.getTitle(),
                blendingRequest.getContent(),
                blendingRequest.getCapacity(),
                blendingRequest.getRegion(),
                blendingRequest.getPlace(),
                blendingRequest.getOpenChattingUrl(),
                blendingRequest.getSchedule()
        );

        List<Keyword> keywords = keywordRepository.findAllByNameIn(blendingRequest.getKeywords());
        for(Keyword keyword : keywords) {
            blending.addKeyword(keyword);
        }
        if(blending.getKeywords().isEmpty()) {
            throw new BaseException(BaseErrorCode.KEYWORD_IS_EMPTY);
        }
        if(blending.getKeywords().size() >= 4) {
            throw new BaseException(BaseErrorCode.KEYWORD_LIMIT_EXCEEDED);
        }

        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new BaseException(BaseErrorCode.USER_NOT_FOUND));

        BlendingUser blendingUser = blending.addParticipant(
                user,
                Grade.HOST,
                null,
                JoinStatus.HOST
        );

        blendingRepository.save(blending);
        blendingUserRepository.save(blendingUser);
    }


    @Transactional
    public void delete(String userUuid, String blendingUuid) {

        Blending blending = blendingRepository.findByUuid(blendingUuid)
                .orElseThrow(() -> new BaseException(BaseErrorCode.BLENDING_NOT_FOUND));

        validateHostPermission(userUuid, blending);

        blending.delete();
    }


    @Transactional
    public void update(String userUuid, String blendingUuid, BlendingRequest blendingRequest) {

        Blending blending = blendingRepository.findByUuid(blendingUuid)
                .orElseThrow(() -> new BaseException(BaseErrorCode.BLENDING_NOT_FOUND));

        validateHostPermission(userUuid, blending);

        if(blending.getParticipants().size() > blendingRequest.getCapacity()) {
            throw new BaseException(BaseErrorCode.BLENDING_INVALID_CAPACITY);
        }

        LocalDateTime schedule = blendingRequest.getSchedule();
        if(schedule != null) {
            if(schedule.isBefore(LocalDateTime.now())) {
                throw new BaseException(BaseErrorCode.BLENDING_INVALID_SCHEDULE_TIME);
            }
        }


        List<Keyword> newKeywords = keywordRepository.findAllByNameIn(blendingRequest.getKeywords());
        blending.updateKeyword(newKeywords);

        blending.update(
                blendingRequest.getTitle(),
                blendingRequest.getContent(),
                blendingRequest.getCapacity(),
                blendingRequest.getRegion(),
                blendingRequest.getPlace(),
                blendingRequest.getOpenChattingUrl(),
                blendingRequest.getSchedule()
        );
    }


    private void validateHostPermission(String userUuid, Blending blending) {
        BlendingUser blendingHost = blendingUserRepository.findByBlendingAndGrade(blending, Grade.HOST)
                .orElseThrow(() -> new BaseException(BaseErrorCode.USER_NOT_FOUND));

        if(!userUuid.equals(blendingHost.getUser().getUuid())) {
            throw new BaseException(BaseErrorCode.BLENDING_PERMISSION_DENIED);
        }
    }
}
