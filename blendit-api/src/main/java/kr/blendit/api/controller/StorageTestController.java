package kr.blendit.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.blendit.common.storage.dto.PresignedUrl;
import kr.blendit.common.storage.dto.UploadedObject;
import kr.blendit.common.storage.service.ObjectStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@Tag(name = "Storage Test API", description = "오브젝트 스토리지 테스트용 API")
@RestController
@RequestMapping("/api/blendit/storage")
@RequiredArgsConstructor
public class StorageTestController {

    private final ObjectStorageService objectStorageService;

    @Operation(
        summary = "Presigned PUT URL 발급",
        description = "클라이언트가 직접 업로드할 수 있는 Presigned PUT URL을 발급합니다. (key는 내부에서 생성)"
    )
    @PostMapping("/presign/put")
    public PresignedUrl presignPut(@RequestBody PresignPutRequest request) {
        Duration ttl = Duration.ofSeconds(request.ttlSeconds() == null ? 300 : request.ttlSeconds());

        return objectStorageService.presignPut(
            request.prefix(),
            request.originalFilename(),
            request.contentType(),
            ttl
        );
    }

    @Operation(
        summary = "Presigned PUT URL 발급 (key 지정)",
        description = "클라이언트가 직접 업로드할 수 있는 Presigned PUT URL을 발급합니다. (key는 호출자가 지정)"
    )
    @PostMapping("/presign/put-with-key")
    public PresignedUrl presignPutWithKey(@RequestBody PresignPutWithKeyRequest request) {
        Duration ttl = Duration.ofSeconds(request.ttlSeconds() == null ? 300 : request.ttlSeconds());

        return objectStorageService.presignPutWithKey(
            request.key(),
            request.contentType(),
            ttl
        );
    }

    @Operation(
        summary = "Presigned GET URL 발급",
        description = "private 객체 다운로드용 Presigned GET URL을 발급합니다."
    )
    @PostMapping("/presign/get")
    public PresignedUrl presignGet(@RequestBody PresignGetRequest request) {
        Duration ttl = Duration.ofSeconds(request.ttlSeconds() == null ? 300 : request.ttlSeconds());

        return objectStorageService.presignGet(
            request.key(),
            ttl
        );
    }

    @Operation(
        summary = "서버 업로드 테스트 (key 지정)",
        description = "서버가 직접 Object Storage로 파일을 업로드합니다. (key를 호출자가 지정)"
    )
    @PostMapping(value = "/upload-with-key", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadedObject uploadWithKey(
        @RequestPart("file") MultipartFile file,
        @RequestParam String key
    ) throws IOException {
        return objectStorageService.uploadWithKey(
            file.getInputStream(),
            file.getSize(),
            file.getContentType(),
            file.getOriginalFilename(),
            key
        );
    }

    @Operation(
        summary = "업로드 삭제",
        description = "Object Storage에서 해당 key를 삭제합니다."
    )
    @DeleteMapping("/delete")
    public Map<String, Object> delete(@RequestParam String key) {
        objectStorageService.delete(key);

        return Map.of(
            "key", key,
            "message", "삭제 완료"
        );
    }

    @Operation(summary = "스토리지 모듈 상태 확인", description = "스토리지 모듈이 빈으로 정상 로딩되었는지 간단히 확인합니다.")
    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of(
            "message", "pong - storage module is alive",
            "path", "/api/blendit/storage/ping"
        );
    }

    public record PresignPutRequest(
        String prefix,
        String originalFilename,
        String contentType,
        Integer ttlSeconds
    ) {

    }

    public record PresignPutWithKeyRequest(
        String key,
        String contentType,
        Integer ttlSeconds
    ) {

    }

    public record PresignGetRequest(
        String key,
        Integer ttlSeconds
    ) {

    }
}
