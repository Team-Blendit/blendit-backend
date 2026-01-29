package kr.blendit.api.user.repository;

import kr.blendit.api.user.controller.dto.GetUserSearchRequest;
import kr.blendit.api.user.controller.dto.GetUserSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepositoryCustom {

  Page<GetUserSearchResponse> searchUsers(GetUserSearchRequest request, String currentUserUuid, Pageable pageable);
}
