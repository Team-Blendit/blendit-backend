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

@Service
@RequiredArgsConstructor
public class BlendingService {

    private final BlendingRepository blendingRepository;
    private final BlendingUserRepository blendingUserRepository;
    private final UserRepository userRepository;
    private final KeywordRepository keywordRepository;

    public void create(String userUuid, BlendingRequest blendingRequest) {

        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new BaseException(BaseErrorCode.USER_NOT_FOUND));

        Blending blending = Blending.create(
                blendingRequest.getTitle(),
                blendingRequest.getContent(),
                blendingRequest.getCapacity(),
                blendingRequest.getRegion(),
                blendingRequest.getPlace(),
                blendingRequest.getOpenChattingUrl(),
                blendingRequest.getSchedule()
        );

        for(String keywordStr : blendingRequest.getKeywords()) {
            Keyword keyword = keywordRepository.findByName(keywordStr)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.KEYWORD_NOT_FOUND));
            blending.addKeyword(keyword);
        }

        BlendingUser blendingUser = blending.addParticipant(
                user,
                Grade.HOST,
                null,
                JoinStatus.HOST
        );

        blendingRepository.save(blending);
        blendingUserRepository.save(blendingUser);
    }
}
