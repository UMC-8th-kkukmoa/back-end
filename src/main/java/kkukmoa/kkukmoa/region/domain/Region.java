package kkukmoa.kkukmoa.region.domain;

import jakarta.persistence.*;
import kkukmoa.kkukmoa.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;


@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Region extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;
    private String detailAddress; // 상세 주소

    private double latitude;
    private double longitude;

    @Column(name = "location", columnDefinition = "POINT")
    private Point location;

}
