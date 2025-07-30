package kkukmoa.kkukmoa.category.domain;

public enum CategoryType {
    RESTAURANT("음식점"),
    CAFE("카페"),
    BEAUTY("미용"),
    EDUCATION("교육"),
    HEALTH("운동/건강");

    private final String displayName;

    CategoryType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static CategoryType fromDisplayName(String displayName) {
        for (CategoryType type : CategoryType.values()) {
            if (type.getDisplayName().equals(displayName)) {
                return type;
            }
        }
        throw new RuntimeException("유효하지 않은 카테고리입니다: " + displayName);
    }
}
