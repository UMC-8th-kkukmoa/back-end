package kkukmoa.kkukmoa.store.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreRequestDto {
    private String name;

    private String number;

    private LocalTime openingHours;

    private LocalTime closingHours;

    private String address;
    private String detailAddress;

    private double latitude;
    private double longitude;

    private String category;

    private String storeImage;
}
