package kr.blendit.common.storage.generator;

import java.time.LocalDate;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class DefaultObjectKeyGenerator implements ObjectKeyGenereator {

    @Override
    public String generate(String prefix, String originalFilename) {
        String safeName = sanitize(originalFilename == null ? "file" : originalFilename);

        LocalDate today = LocalDate.now();
        String normalizedPrefix = trimSlashes(prefix == null ? "" : prefix);

        if (normalizedPrefix.isBlank()) {
            return String.format("%04d/%02d/%02d/%s_%s",
                today.getYear(), today.getMonthValue(), today.getDayOfMonth(),
                UUID.randomUUID(), safeName
            );
        }

        return String.format("%s/%04d/%02d/%02d/%s_%s",
            normalizedPrefix,
            today.getYear(), today.getMonthValue(), today.getDayOfMonth(),
            UUID.randomUUID(), safeName
        );
    }

    private String sanitize(String name) {
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private String trimSlashes(String s) {
        String t = s;
        while (t.startsWith("/")) {
            t = t.substring(1);
        }
        while (t.endsWith("/")) {
            t = t.substring(0, t.length() - 1);
        }
        return t;
    }
}