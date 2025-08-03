package kkukmoa.kkukmoa.region.domain;

import jakarta.persistence.*;

import kkukmoa.kkukmoa.common.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Region extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String address;

    private String detailAddress; // 상세 주소

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;
}
