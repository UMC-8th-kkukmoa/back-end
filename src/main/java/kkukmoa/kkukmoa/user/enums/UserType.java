package kkukmoa.kkukmoa.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public enum UserType {
    USER("ROLE_USER"),
    PENDING_OWNER("ROLE_PENDING_OWNER"), // 입점 신청 중
    OWNER("ROLE_OWNER"), // 입점 승인
    ADMIN("ROLE_ADMIN");

    private final String roleName;

    public static String resolveRole(Set<UserType> roles) {
        if (roles == null || roles.isEmpty()) return UserType.USER.getRoleName();

        if (roles.contains(UserType.OWNER)) return UserType.OWNER.getRoleName();
        if (roles.contains(UserType.PENDING_OWNER)) return UserType.PENDING_OWNER.getRoleName();
        if (roles.contains(UserType.ADMIN)) return UserType.ADMIN.getRoleName();

        return UserType.USER.getRoleName();
    }
}
