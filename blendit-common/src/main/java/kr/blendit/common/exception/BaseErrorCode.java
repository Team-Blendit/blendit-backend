package kr.blendit.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BaseErrorCode implements ErrorCode {

  // JWT 관련
  INVALID_JWT_SECRET_KEY("JWT 시크릿 키는 최소 32바이트(256비트) 이상이어야 합니다."),
  INVALID_ACCESS_TOKEN("유효하지 않은 액세스 토큰입니다."),
  INVALID_REFRESH_TOKEN("유효하지 않은 리프레시 토큰입니다."),
  EXPIRED_ACCESS_TOKEN("만료된 액세스 토큰입니다."),
  EXPIRED_REFRESH_TOKEN("만료된 리프레시 토큰입니다."),
  REFRESH_TOKEN_NOT_FOUND("리프레시 토큰을 찾을 수 없습니다."),
  REFRESH_TOKEN_REUSED("이미 사용된 리프레시 토큰입니다. 보안을 위해 재로그인이 필요합니다."),
  TOKEN_REVOKED("무효화된 토큰입니다. 다시 로그인해주세요."),

  // 공통
  INVALID_PARAMETER("유효하지 않은 파라미터입니다."),
  INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다."),
  NOT_FOUND("요청한 리소스를 찾을 수 없습니다."),
  UNAUTHORIZED("인증이 필요합니다."),
  FORBIDDEN("접근 권한이 없습니다."),

  // 보안 설정
  INVALID_SECURITY_CONFIG("보안 설정이 올바르지 않습니다."),
  INVALID_USER_ROLE("알 수 없는 사용자 역할입니다."),
  ADMIN_ONLY("관리자만 접근할 수 있습니다."),

  // 사용자
  USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
  DUPLICATE_EMAIL("이미 등록된 이메일이에요."),
  DUPLICATE_NICKNAME("이미 사용 중인 닉네임이에요."),
  ALREADY_ONBOARDED("이미 온보딩이 완료된 사용자입니다."),

  // OAuth
  OAUTH_TOKEN_REQUEST_FAILED("OAuth 토큰 요청에 실패했습니다."),
  OAUTH_INVALID_ID_TOKEN("유효하지 않은 ID 토큰입니다."),
  OAUTH_ID_TOKEN_EXPIRED("만료된 ID 토큰입니다."),
  OAUTH_INVALID_ISSUER("ID 토큰 발급자가 일치하지 않습니다."),
  OAUTH_INVALID_AUDIENCE("ID 토큰 대상이 일치하지 않습니다."),
  OAUTH_JWKS_FETCH_FAILED("JWKS 공개키 조회에 실패했습니다."),

  // 키워드
  KEYWORD_NOT_FOUND("키워드를 찾을 수 없습니다."),
  KEYWORD_IS_EMPTY("키워드 입력이 필요합니다."),
  KEYWORD_LIMIT_EXCEEDED("키워드는 최대 3개까지 입력 가능합니다."),
  DUPLICATE_KEYWORD("이미 등록된 키워드입니다."),

  // 블렌딩
  BLENDING_NOT_FOUND("블렌딩을 찾을 수 없습니다."),
  BLENDING_PERMISSION_DENIED("해당 블렌딩에 대한 접근 권한이 없습니다"),
  BLENDING_INVALID_CAPACITY("현재 참여 인원보다 적은 수로 정원을 변경할 수 없습니다."),
  BLENDING_CAPACITY_BELOW_MIN("블렌딩 정원은 최소 2명 이상이어야 합니다."),
  BLENDING_INVALID_SCHEDULE_TIME("유효하지 않은 블렌딩 일정입니다."),
  BLENDING_CANNOT_RECRUIT_FULL("모집 인원이 가득 차서 모집 중 상태로 변경할 수 없습니다."),
  BLENDING_FULL("블렌딩 정원이 가득 찼습니다."),
  BLENDING_ALREADY_APPLIED("이미 해당 블렌딩에 신청하거나 참여한 이력이 존재합니다."),
  BLENDING_NOT_APPLIED("신청하지 않은 블렌딩입니다."),
  BLENDING_NOT_RECRUITING("모집 중인 블렌딩이 아닙니다."),
  BLENDING_ALREADY_PROCESSED("이미 처리된 신청 내역입니다."),
  BLENDING_HOST_CANNOT_LEAVE("호스트는 블렌딩을 탈퇴할 없습니다."),
  ALREADY_BOOKMARKED("이미 북마크된 블렌딩입니다."),
  BOOKMARK_NOT_FOUND("북마크 내역이 존재하지 않습니다.");

  private final String message;
}
