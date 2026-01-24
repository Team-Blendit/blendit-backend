package kr.blendit.common.storage.generator;

public interface ObjectKeyGenereator {

    String generate(
        String prefix,
        String originalFileName
    );
}
