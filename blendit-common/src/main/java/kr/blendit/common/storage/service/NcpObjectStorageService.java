package kr.blendit.common.storage.service;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import kr.blendit.common.storage.config.ObjectStorageProperties;
import kr.blendit.common.storage.dto.PresignedUrl;
import kr.blendit.common.storage.dto.UploadedObject;
import kr.blendit.common.storage.generator.ObjectKeyGenereator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@Service
@RequiredArgsConstructor
public class NcpObjectStorageService implements ObjectStorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final ObjectStorageProperties props;
    private final ObjectKeyGenereator keyGenerator;

    @Override
    public UploadedObject uploadWithKey(InputStream inputStream,
        long size,
        String contentType,
        String originalFilename,
        String key) {

        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(props.bucket())
            .key(key)
            .contentType(contentType)
            .contentLength(size)
            .build();

        PutObjectResponse response = s3Client.putObject(
            request,
            RequestBody.fromInputStream(inputStream, size)
        );

        String url = buildPublicUrl(props.bucket(), key);

        return new UploadedObject(
            props.bucket(),
            key,
            url,
            response.eTag(),
            size,
            contentType,
            originalFilename
        );
    }

    @Override
    public void delete(String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
            .bucket(props.bucket())
            .key(key)
            .build());
    }

    @Override
    public PresignedUrl presignPut(String prefix,
        String originalFilename,
        String contentType,
        Duration ttl) {

        String finalPrefix = (prefix == null || prefix.isBlank())
            ? defaultPrefix()
            : prefix;

        String key = keyGenerator.generate(finalPrefix, originalFilename);
        return presignPutWithKey(key, contentType, ttl);
    }

    @Override
    public PresignedUrl presignPutWithKey(String key,
        String contentType,
        Duration ttl) {

        PutObjectRequest putReq = PutObjectRequest.builder()
            .bucket(props.bucket())
            .key(key)
            .contentType(contentType)
            .build();

        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(b -> b
            .signatureDuration(ttl)
            .putObjectRequest(putReq)
        );

        URL url = presigned.url();
        Instant expiresAt = Instant.now().plus(ttl);

        return new PresignedUrl(key, url.toString(), expiresAt);
    }

    @Override
    public PresignedUrl presignGet(String key, Duration ttl) {
        GetObjectRequest getReq = GetObjectRequest.builder()
            .bucket(props.bucket())
            .key(key)
            .build();

        PresignedGetObjectRequest presigned = s3Presigner.presignGetObject(b -> b
            .signatureDuration(ttl)
            .getObjectRequest(getReq)
        );

        URL url = presigned.url();
        Instant expiresAt = Instant.now().plus(ttl);

        return new PresignedUrl(key, url.toString(), expiresAt);
    }

    private String defaultPrefix() {
        String dp = props.defaultPrefix();
        return (dp == null || dp.isBlank()) ? "uploads" : dp;
    }

    private String buildPublicUrl(String bucket, String key) {
        String base = props.publicBaseUrl();

        if (base != null && !base.isBlank()) {
            if (base.endsWith("/"))
                base = base.substring(0, base.length() - 1);
            return base + "/" + key;
        }

        String endpoint = props.endpoint();
        if (endpoint.endsWith("/"))
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        return endpoint + "/" + bucket + "/" + key;
    }

}
