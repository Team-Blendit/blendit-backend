package kr.blendit.api.user.service;

import kr.blendit.api.user.controller.dto.GetUserSearchRequest;
import kr.blendit.api.user.controller.dto.GetUserSearchResponse;
import kr.blendit.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserSearchService {

  private final UserRepository userRepository;

  public Page<GetUserSearchResponse> searchUsers(GetUserSearchRequest request, String currentUserUuid, Pageable pageable) {
    return userRepository.searchUsers(request, currentUserUuid, pageable);
  }
}
