package kr.blendit.common.storage.service;

import java.io.InputStream;
import kr.blendit.common.storage.dto.UploadedObject;

public interface ObjectStorageService {

    UploadedObject upload(
        InputStream inputStream,
        long size,
        String contentType,
        String originalFilename,
        String prefix
    );

    void delete(String key);

    String publicUrl(String key);
}
