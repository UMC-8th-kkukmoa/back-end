package kkukmoa.kkukmoa.common.util;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Optional;

public final class CursorUtil {


    private CursorUtil() {}


    public record Cursor(LocalDateTime usedAt, Long id) {}

    // 커서 인코딩
    public static String encode(LocalDateTime usedAt, Long id, ZoneId zone) {
        long millis = usedAt.atZone(zone).toInstant().toEpochMilli();
        String raw = millis + ":" + id;
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    //커서 디코딩
    public static Optional<Cursor> decode(String cursor, ZoneId zone) {
        if (cursor == null || cursor.isBlank()) return Optional.empty();
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
            String[] p = decoded.split(":");
            long millis = Long.parseLong(p[0]);
            long id = Long.parseLong(p[1]);
            return Optional.of(new Cursor(
                    Instant.ofEpochMilli(millis).atZone(zone).toLocalDateTime(), id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}

