package kr.blendit.common.storage.service;

import kr.blendit.common.storage.dto.UploadedObject;
import org.springframework.web.multipart.MultipartFile;

public interface ObjectStorageService {

  UploadedObject upload(
      MultipartFile file,
      String prefix
  );

  void delete(String key);

  String publicUrl(String key);
}
