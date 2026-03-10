package kr.blendit.api.blending.service;

import kr.blendit.api.blending.constant.BlendingStatus;
import kr.blendit.api.blending.domain.Blending;
import kr.blendit.api.blending.dto.request.BlendingApplyRequest;
import kr.blendit.api.blending.repository.BlendingRepository;
import kr.blendit.api.blending.repository.BlendingUserRepository;
import kr.blendit.api.common.constant.Position;
import kr.blendit.api.user.constant.LoginType;
import kr.blendit.api.user.domain.User;
import kr.blendit.api.user.repository.UserRepository;
import kr.blendit.common.constant.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class BlendingServiceConcurrencyTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BlendingRepository blendingRepository;

  @Autowired
  private BlendingUserRepository blendingUserRepository;

  @Autowired
  private BlendingParticipationService blendingParticipationService;

  @AfterEach
  void tearDown() {
    blendingUserRepository.deleteAll();
    blendingRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  @DisplayName("정원 5명인 자동승인 블렌딩에 100명이 동시에 신청하면, 정확히 5명만 승인되어야 한다.")
  void apply_concurrency_test() throws InterruptedException {
    // 블렌딩 생성 (정원 5명, 자동 승인)
    Blending blending = createBlendingByReflection(5, true);
    blendingRepository.save(blending);

    // 유저 100명 생성 및 저장
    int threadCount = 100;
    List<User> users = new ArrayList<>();
    for (int i = 0; i < threadCount; i++) {
      users.add(createUserByReflection("user" + i));
    }
    userRepository.saveAll(users);

    //  동시성 요청 준비
    ExecutorService executorService = Executors.newFixedThreadPool(32);
    CountDownLatch latch = new CountDownLatch(threadCount);

    // 성공/실패 횟수 카운팅
    AtomicInteger successCount = new AtomicInteger();
    AtomicInteger failCount = new AtomicInteger();

    BlendingApplyRequest request = new BlendingApplyRequest();
    ReflectionTestUtils.setField(request, "message", "참여하고 싶습니다!");

    for (int i = 0; i < threadCount; i++) {
      User user = users.get(i);

      executorService.submit(() -> {
        try {
          blendingParticipationService.apply(user, blending, request);
          successCount.getAndIncrement();
        } catch (Exception e) {
          failCount.getAndIncrement();
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await();

    // 실제 DB에 저장된 BlendingUser 개수 확인
    long finalCount = blendingUserRepository.count();

    // 블렌딩 최신 상태 조회
    Blending finalBlending = blendingRepository.findById(blending.getId()).orElseThrow();

    System.out.println("성공 횟수: " + successCount.get());
    System.out.println("실패 횟수: " + failCount.get());

    // 검증 1: DB에 저장된 인원은 정원(5명)과 같아야 한다.
    assertThat(finalCount).isEqualTo(5);

    // 검증 2: 블렌딩 상태는 마감(CLOSED)이어야 한다.
    assertThat(finalBlending.getStatus()).isEqualTo(BlendingStatus.RECRUITMENT_CLOSED);
  }


  private User createUserByReflection(String nickname) {
    try {
      Constructor<User> constructor = User.class.getDeclaredConstructor();
      constructor.setAccessible(true); // private/protected 뚫기
      User user = constructor.newInstance();

      ReflectionTestUtils.setField(user, "email", nickname + "@test.com");
      ReflectionTestUtils.setField(user, "nickname", nickname);
      ReflectionTestUtils.setField(user, "loginType", LoginType.KAKAO);
      ReflectionTestUtils.setField(user, "role", UserRole.USER);
      ReflectionTestUtils.setField(user, "tokenVersion", 0);

      return user;

    } catch (Exception e) {
      throw new RuntimeException("유저 생성 실패", e);
    }
  }

  private Blending createBlendingByReflection(int capacity, boolean autoApproval) {
    try {
      Constructor<Blending> constructor = Blending.class.getDeclaredConstructor();
      constructor.setAccessible(true);
      Blending blending = constructor.newInstance();

      ReflectionTestUtils.setField(blending, "title", "테스트 모임");
      ReflectionTestUtils.setField(blending, "content", "테스트 내용");
      ReflectionTestUtils.setField(blending, "position", Position.BACKEND);
      ReflectionTestUtils.setField(blending, "capacity", capacity);
      ReflectionTestUtils.setField(blending, "region", "SEOUL");
      ReflectionTestUtils.setField(blending, "schedule", LocalDateTime.now().plusDays(1));
      ReflectionTestUtils.setField(blending, "autoApproval", autoApproval);
      ReflectionTestUtils.setField(blending, "status", BlendingStatus.RECRUITING);

      return blending;
    } catch (Exception e) {
      throw new RuntimeException("블렌딩 생성 실패", e);
    }
  }
}