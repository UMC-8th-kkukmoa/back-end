package kkukmoa.kkukmoa.admin.dto;

public interface PendingStoreSummary {
    Long getStoreId();

    String getOwnerEmail();

    java.time.LocalDateTime getAppliedAt();
}
