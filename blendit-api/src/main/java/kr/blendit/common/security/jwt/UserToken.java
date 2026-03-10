package kr.blendit.common.security.jwt;

import kr.blendit.common.constant.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserToken {

    private String userUuid;
    private List<UserRole> roles;
}
