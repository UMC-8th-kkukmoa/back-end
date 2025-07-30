package kkukmoa.kkukmoa.common.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateUtil {


    /**
     * 남은 일수를 계산하여 "D-xx" 형식으로 반환
     * 오늘 + 유효일수로 만료일 계산
     *
     * @param validDaysStr 금액권 유효기간 ("30" 등)
     * @return D-30, D-1, D-0 등 형식 문자열
     */
    public static String getDdayFromToday(String validDaysStr) {
        try {
            int validDays = Integer.parseInt(validDaysStr);
            LocalDate today = LocalDate.now();
            LocalDate expiryDate = today.plusDays(validDays);

            long daysRemaining = ChronoUnit.DAYS.between(today, expiryDate);
            return "D-" + Math.max(daysRemaining, 0); // 음수 방지
        } catch (NumberFormatException e) {
            return "D-0"; // 유효하지 않은 숫자일 경우
        }
    }
}
