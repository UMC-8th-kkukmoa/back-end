package kkukmoa.kkukmoa.common.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class DateUtil {

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
}
