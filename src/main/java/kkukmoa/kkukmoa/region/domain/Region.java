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

    @Column(nullable = false)
    private String address;
    private String detailAddress; // 상세 주소

    @Column(nullable = false)
    private double latitude;
    @Column(nullable = false)
    private double longitude;

//    TODO: Point 필드와 좌표 필드의 중복성 검토 필요
//    @Column(name = "location", columnDefinition = "POINT")
//    private Point location;

}
