package kr.blendit.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.Map;
import kr.blendit.common.storage.dto.UploadedObject;
import kr.blendit.common.storage.service.ObjectStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Storage Test API", description = "로컬 볼륨 스토리지 테스트용 API")
@RestController
@RequestMapping("/api/blendit/storage")
@RequiredArgsConstructor
public class StorageTestController {

  private final ObjectStorageService objectStorageService;

  @Operation(
      summary = "서버 업로드 테스트",
      description = "multipart 파일을 서버로 업로드하면, 서버 볼륨(rootDir)에 저장하고 UploadedObject를 반환합니다."
  )
  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public UploadedObject upload(
      @RequestPart("file") MultipartFile file,
      @RequestParam(required = false) String prefix
  ) {
    return objectStorageService.upload(
        file,
        prefix
    );
  }

  @Operation(
      summary = "서버 업로드 테스트 (key 지정)",
      description = "※ 테스트용. 실제 운영에서는 권장하지 않음. 키를 직접 지정해 서버 볼륨에 저장합니다."
  )
  @PostMapping(value = "/upload-with-key", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public UploadedObject uploadWithKey(
      @RequestPart("file") MultipartFile file,
      @RequestParam String key
  ) throws IOException {
    // ObjectStorageService에 uploadWithKey가 없다면, 이 API는 제거하는 게 맞습니다.
    // 만약 필요하면 인터페이스에 uploadWithKey를 다시 추가하거나,
    // key 지정 업로드를 LocalObjectStorageService에 별도 메서드로 노출하세요.
    throw new UnsupportedOperationException("upload-with-key is not supported by current ObjectStorageService.");
  }

  @Operation(
      summary = "업로드 URL 확인",
      description = "해당 key의 공개 URL을 계산해서 반환합니다. (Nginx /uploads alias or static serving 필요)"
  )
  @GetMapping("/url")
  public Map<String, String> publicUrl(@RequestParam String key) {
    return Map.of(
        "key", key,
        "url", objectStorageService.publicUrl(key)
    );
  }

  @Operation(
      summary = "업로드 삭제",
      description = "서버 볼륨(rootDir)에서 해당 key 파일을 삭제합니다."
  )
  @DeleteMapping("/delete")
  public Map<String, Object> delete(@RequestParam String key) {
    objectStorageService.delete(key);

    return Map.of(
        "key", key,
        "message", "삭제 완료"
    );
  }

  @Operation(summary = "스토리지 모듈 상태 확인", description = "스토리지 모듈이 빈으로 정상 로딩되었는지 확인합니다.")
  @GetMapping("/ping")
  public Map<String, String> ping() {
    return Map.of(
        "message", "pong - local storage module is alive",
        "path", "/api/blendit/storage/ping"
    );
  }
}
