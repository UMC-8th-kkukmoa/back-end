package kkukmoa.kkukmoa.ticket.domain;

import jakarta.persistence.*;

import kkukmoa.kkukmoa.user.domain.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String value; // 가격
    private String validDays; // 유효기간
    private boolean used; // 사용 여부

    private String qrCodeUuid;
    private String qrCodeImageBase64;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
