package kr.blendit.common.storage.service;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.List;
import kr.blendit.common.exception.BaseErrorCode;
import kr.blendit.common.exception.BaseException;
import kr.blendit.common.storage.config.LocalStorageProperties;
import kr.blendit.common.storage.dto.UploadedObject;
import kr.blendit.common.storage.generator.ObjectKeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalObjectStorageService implements ObjectStorageService {

  private final LocalStorageProperties props;
  private final ObjectKeyGenerator keyGenerator;

  @Override
  public UploadedObject upload(
      MultipartFile file,
      String prefix
  ) {
    if (file == null || file.isEmpty()) {
      return null;
    }
    Path tmp = null;

    try (InputStream inputStream = file.getInputStream()) {
      String originalFilename = file.getOriginalFilename();
      long size = file.getSize();
      String contentType = file.getContentType();

      validate(size, contentType);

      String finalPrefix = (prefix == null || prefix.isBlank()) ? "uploads" : prefix;
      String key = keyGenerator.generate(finalPrefix, originalFilename);

      Path root = Paths.get(required(props.rootDir(), "storage.local.root-dir"))
          .toAbsolutePath().normalize();
      Path target = resolveSafe(root, key);

      Files.createDirectories(target.getParent());

      // 1) temp 파일에 먼저 write
      tmp = Files.createTempFile(target.getParent(), ".upload_", ".tmp");

      MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
      try (DigestInputStream dis = new DigestInputStream(new BufferedInputStream(inputStream), sha256)) {
        Files.copy(dis, tmp, StandardCopyOption.REPLACE_EXISTING);
      }

      long realSize = Files.size(tmp);

      // 2) atomic move (가능하면)
      try {
        Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      } catch (AtomicMoveNotSupportedException e) {
        Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
      }
      tmp = null; // move 성공했으면 tmp는 더 이상 존재하지 않음(로직상)

      String eTag = toHex(sha256.digest());
      String url = publicUrl(key);

      return new UploadedObject(
          "local",
          key,
          url,
          eTag,
          realSize,
          contentType,
          originalFilename
      );
    } catch (Exception e) {
      log.error("파일 업로드 실패", e);
      if (tmp != null) {
        try {
          Files.deleteIfExists(tmp);
        } catch (Exception ignore) {
        }
      }
      throw new BaseException(BaseErrorCode.FILE_UPLOAD_FAILED);
    }
  }

  @Override
  public void delete(String key) {
    Path root = Paths.get(required(props.rootDir(), "storage.local.root-dir"))
        .toAbsolutePath().normalize();
    Path target = resolveSafe(root, key);

    try {
      Files.deleteIfExists(target);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to delete local file: " + key, e);
    }
  }

  @Override
  public String publicUrl(String key) {
    String base = required(props.publicBaseUrl(), "storage.local.public-base-url");
    if (base.endsWith("/")) {
      base = base.substring(0, base.length() - 1);
    }
    return base + "/" + encodePath(key);
  }

  private void validate(long size, String contentType) {
    long max = props.maxUploadBytes();
    if (max > 0 && size > max) {
      throw new IllegalArgumentException("File too large. max=" + max + ", size=" + size);
    }

    List<String> allowed = props.allowedMimeTypes();
    if (allowed != null && !allowed.isEmpty()) {
      if (contentType == null || contentType.isBlank() || !allowed.contains(contentType)) {
        throw new IllegalArgumentException("Unsupported contentType: " + contentType);
      }
    }
  }

  private Path resolveSafe(Path root, String key) {
    if (key == null || key.isBlank()) {
      throw new IllegalArgumentException("key is blank");
    }
    Path resolved = root.resolve(key).normalize();
    if (!resolved.startsWith(root)) {
      throw new IllegalArgumentException("path traversal detected: " + key);
    }
    return resolved;
  }

  private String encodePath(String key) {
    String[] parts = key.split("/");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < parts.length; i++) {
      if (i > 0) {
        sb.append("/");
      }
      sb.append(URLEncoder.encode(parts[i], StandardCharsets.UTF_8));
    }
    return sb.toString();
  }

  private String required(String v, String name) {
    if (v == null || v.isBlank()) {
      throw new IllegalArgumentException(name + " is required");
    }
    return v;
  }

  private String toHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder(bytes.length * 2);
    for (byte b : bytes) {
      sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }
}
