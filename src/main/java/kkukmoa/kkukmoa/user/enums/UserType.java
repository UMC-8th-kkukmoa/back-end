package kkukmoa.kkukmoa.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserType {
    USER("ROLE_USER"),
    PENDING_OWNER("ROLE_PENDING_OWNER"), // 입점 신청 중
    OWNER("ROLE_OWNER"), // 입점 승인
    ADMIN("ROLE_ADMIN");
    ;

    private final String roleName;
}
