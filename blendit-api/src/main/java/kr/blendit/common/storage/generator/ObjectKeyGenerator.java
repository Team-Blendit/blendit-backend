package kr.blendit.common.storage.generator;

public interface ObjectKeyGenerator {

    String generate(String prefix, String originalFileName);
}
