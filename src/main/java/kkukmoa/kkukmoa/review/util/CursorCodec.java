package kkukmoa.kkukmoa.review.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class CursorCodec {
    private CursorCodec() {}

    public static String encode(String raw) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static String decode(String cursor) {
        return new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
    }
}