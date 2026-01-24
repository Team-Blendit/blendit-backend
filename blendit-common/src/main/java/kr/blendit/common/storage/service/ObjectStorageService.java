package kr.blendit.common.storage.service;

import java.io.InputStream;
import java.time.Duration;
import kr.blendit.common.storage.dto.PresignedUrl;
import kr.blendit.common.storage.dto.UploadedObject;

public interface ObjectStorageService {

    /**
     * key를 지정해서 업로드하고 싶을 때 (재시도/동일키 정책 등)
     */
    UploadedObject uploadWithKey(InputStream inputStream,
        long size,
        String contentType,
        String originalFilename,
        String key);

    void delete(String key);

    /**
     * 클라이언트 직접 업로드용: presigned PUT URL 발급 (key는 내부에서 생성)
     */
    PresignedUrl presignPut(String prefix,
        String originalFilename,
        String contentType,
        Duration ttl);

    /**
     * 클라이언트 직접 업로드용: presigned PUT URL 발급 (key는 호출자가 지정)
     */
    PresignedUrl presignPutWithKey(String key,
        String contentType,
        Duration ttl);

    /**
     * private 객체 다운로드용: presigned GET URL 발급
     */
    PresignedUrl presignGet(String key, Duration ttl);
}