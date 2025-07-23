package kkukmoa.kkukmoa.ticket.dto;

import lombok.Builder;
import lombok.Getter;

public class TicketResponseDto {
    @Getter
    @Builder
    public static class TicketListResponseDto {

        private String name;
        private String validDays;
        private boolean used;
    }
    @Getter
    @Builder
    public static class TicketDetailResponseDto {

        private String name;
        private String value;
        private String validDays;
        private boolean used;
        private String qrCodeUuid;
        private String qrCodeImageBase64;
    }
}
