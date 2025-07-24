package kkukmoa.kkukmoa.store.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreRequestDto {
    private String name;

    private String number;

    private LocalDateTime openingHours;
    private LocalDateTime closingHours;

    private String address;
    private String detailAddress;

    private double latitude;
    private double longitude;

    private String category;
}
