package kr.blendit.api.user.controller.dto;

import java.util.Comparator;
import java.util.List;
import kr.blendit.api.common.constant.Position;
import kr.blendit.api.user.constant.Experience;
import kr.blendit.api.user.domain.User;
import kr.blendit.api.user.domain.UserLink;
import kr.blendit.api.user.domain.UserSkill;

public record GetMyProfileResponse(
    String nickName,
    Position position,
    Experience experience,
    String affiliation,
    String province,
    String district,
    String email,
    String description,
    List<String> keywordList,
    List<String> skills,
    List<UserLink> links
) {

  public static GetMyProfileResponse create(User user) {
    return new GetMyProfileResponse(
        user.getNickname(),
        user.getPosition(),
        user.getExperience(),
        user.getAffiliation(),
        user.getProvince(),
        user.getDistrict(),
        user.getEmail(),
        user.getDescription(),
        user.getUserKeywords().stream()
            .map(uk -> uk.getKeyword().getName())
            .toList(),
        user.getSkills().stream()
            .sorted(Comparator.comparingInt(UserSkill::getOrderNum))
            .map(UserSkill::getTitle)
            .toList(),
        user.getLinks().stream()
            .sorted(Comparator.comparingInt(UserLink::getOrderNum))
            .toList()
    );
  }
}
