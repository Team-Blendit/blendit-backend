package kr.blendit.common.storage.dto;

public record UploadedObject(
    String bucket,
    String key,
    String url,
    String eTag,
    long size,
    String contentType,
    String originalFileName
) {

}
