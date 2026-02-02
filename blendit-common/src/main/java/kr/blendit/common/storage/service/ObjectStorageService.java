package kr.blendit.common.storage.service;

import java.io.InputStream;
import kr.blendit.common.storage.dto.UploadedObject;
import org.springframework.web.multipart.MultipartFile;

public interface ObjectStorageService {

    UploadedObject upload(
        MultipartFile file,
        String prefix
    );

    void deleteByUrl(String url);

    String publicUrl(String key);
}
