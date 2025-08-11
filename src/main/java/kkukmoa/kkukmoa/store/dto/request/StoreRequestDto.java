package kkukmoa.kkukmoa.store.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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

//    @Schema(example = "09002000", description = "오픈HHmm+클로즈HHmm 붙인 값")
//    private String timeRange;
//
//    public void parseTimes() {
//        if (timeRange != null && timeRange.length() == 8) {
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");
//            this.openingHours = LocalTime.parse(timeRange.substring(0, 4), formatter);
//            this.closingHours = LocalTime.parse(timeRange.substring(4), formatter);
//        } else {
//            throw new IllegalArgumentException("시간 형식이 올바르지 않습니다. (예: 09002000)");
//        }
//    }
}
