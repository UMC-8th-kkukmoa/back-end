package kkukmoa.kkukmoa.region.domain;

import jakarta.persistence.*;

import kkukmoa.kkukmoa.common.BaseEntity;
import kkukmoa.kkukmoa.common.util.GeometryUtils;

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

    @Column(columnDefinition = "POINT SRID 4326")
    private Point location;

    //    public void setLocationFromLatLon() {
    //        this.location = GeometryUtils.createPoint(longitude, latitude);
    //    }

    @PrePersist
    @PreUpdate
    private void syncPoint() {
        // lat/lon이 유효하면 location 갱신
        if (!Double.isNaN(latitude) && !Double.isNaN(longitude)) {
            this.location = GeometryUtils.createPoint(longitude, latitude);
        }
    }
}
