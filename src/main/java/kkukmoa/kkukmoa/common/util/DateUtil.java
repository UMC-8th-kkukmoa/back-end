package kkukmoa.kkukmoa.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class DateUtil {
    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter KOREAN_DATE_WITH_DAY_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E)", Locale.KOREAN);
    /**
     * 남은 일수를 계산하여 "D-xx" 형식으로 반환 오늘 + 유효일수로 만료일 계산
     *
     * @param validUntil 금액권 유효기간 ("30" 등)
     * @return 30, 1, 0 등 형식 숫자
     */
    public static int getDdayFromToday(String validUntil) {
        try {
            LocalDate today = LocalDate.now();
            LocalDate expiryDate = LocalDate.parse(validUntil); // "yyyy-MM-dd" 형식 파싱

            long daysRemaining = ChronoUnit.DAYS.between(today, expiryDate);

            if (daysRemaining < 0) {
                return -1; // 만료됨을 -1로 표시
            } else if (daysRemaining == 0) {
                return 0;
            } else {
                return (int) daysRemaining;
            }
        } catch (DateTimeParseException e) {
            return -1; // 형식이 잘못된 경우 또는 파싱 오류는 -1로 처리
        }
    }
    /**
     * LocalDateTime → "2025년 8월 8일 (금)" 형식으로 포맷
     */
    public static String formatKoreanFullDateWithDay(LocalDateTime dateTime) {
        return dateTime.atZone(KOREA_ZONE).format(KOREAN_DATE_WITH_DAY_FORMATTER);
    }
}
