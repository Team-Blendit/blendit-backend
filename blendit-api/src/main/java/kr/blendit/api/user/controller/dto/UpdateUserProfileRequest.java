package kr.blendit.api.user.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import kr.blendit.api.common.constant.Position;
import kr.blendit.api.user.constant.Experience;
import kr.blendit.api.user.domain.UserLink;
import kr.blendit.api.user.domain.UserSkill;
import org.springframework.web.multipart.MultipartFile;

public record UpdateUserProfileRequest(
    @NotBlank
    String nickname,

    @NotBlank
    String description,

    @NotNull
    Experience experience,

    @NotNull
    Position position,

    @NotBlank
    String province,

    @NotBlank
    String district,

    @NotBlank
    @Email
    String email,

    @NotNull
    @Size(min = 1, max = 3)
    List<String> keywordUuidList,

    String affiliation,

    List<UserSkill> skills,

    List<UserLink> links,

    MultipartFile profileImage
) {

}
