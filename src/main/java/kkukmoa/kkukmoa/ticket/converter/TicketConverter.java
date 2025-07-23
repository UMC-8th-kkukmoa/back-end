package kkukmoa.kkukmoa.ticket.converter;

import jakarta.persistence.Column;
import kkukmoa.kkukmoa.ticket.domain.Ticket;
import kkukmoa.kkukmoa.ticket.dto.TicketResponseDto;
import org.springframework.stereotype.Component;

@Component
public class TicketConverter {
    public TicketResponseDto.TicketListResponseDto toListResponseDto(Ticket ticket) {
        return TicketResponseDto.TicketListResponseDto.builder()
                .name(ticket.getName())
                .validDays(ticket.getValidDays())
                .used(ticket.isUsed())
                .build();
    }
    public TicketResponseDto.TicketDetailResponseDto toDetailResponseDto(Ticket ticket) {

        String rawUuid = ticket.getQrCodeUuid();
        String pureUuid = rawUuid != null && rawUuid.startsWith("Ticket_")
                ? rawUuid.substring("Ticket_".length())
                : rawUuid;

        return TicketResponseDto.TicketDetailResponseDto.builder()
                .name(ticket.getName())
                .value(ticket.getValue())
                .validDays(ticket.getValidDays())
                .used(ticket.isUsed())
                .qrCodeUuid(pureUuid)
                .qrCodeImageBase64(ticket.getQrCodeImageBase64())
                .build();
    }


}
