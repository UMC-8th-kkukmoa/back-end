package kkukmoa.kkukmoa.stamp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CouponStatus {
    USED("사용됨"),
    IN_USE("사용중"),
    UNUSED("미사용"),
    EXPIRED("만료됨");

    private final String description;
}
