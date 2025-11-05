package kkukmoa.kkukmoa.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import kkukmoa.kkukmoa.common.enums.QrCodeType;
import kkukmoa.kkukmoa.common.util.QrCodeUtil;
import kkukmoa.kkukmoa.owner.dto.request.OwnerRegisterRequest;
import kkukmoa.kkukmoa.owner.dto.request.OwnerSignupRequest;
import kkukmoa.kkukmoa.owner.service.OwnerCommandService;
import kkukmoa.kkukmoa.stamp.domain.Coupon;
import kkukmoa.kkukmoa.stamp.domain.Stamp;
import kkukmoa.kkukmoa.stamp.enums.CouponStatus;
import kkukmoa.kkukmoa.stamp.repository.CouponRepository;
import kkukmoa.kkukmoa.stamp.repository.StampRepository;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.store.repository.StoreRepository;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.user.repository.UserRepository;
import kkukmoa.kkukmoa.voucher.domain.Voucher;
import kkukmoa.kkukmoa.voucher.repository.VoucherRepository;
import lombok.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/dummy")
@RequiredArgsConstructor
public class DummyController {

    private final OwnerCommandService ownerCommandService;

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final CouponRepository couponRepository;
    private final StampRepository stampRepository;
    private final VoucherRepository voucherRepository;

    // 유저들은 그냥 직접 생성 ( 사장님으로 )
    // 가게 만들고

    @PostMapping("/stores")
    @Operation(summary = "가게 & 사장 계정 더미데이터")
    public void putStoreDummy(@RequestBody DummyList dto){

        // 사장님 생성
        dto.getRequest()
                .forEach( request ->  ownerCommandService.registerLocalOwner(request.getOwnerSignupRequest()));

        // 사장님 조회
        List<String> idList = dto.getRequest().stream().map(request -> request.getOwnerSignupRequest().getEmail()).toList();
        List<User> users = userRepository.findByEmailIn(idList);

        // 가게 생성
        List<OwnerRegisterRequest> storeRequestList = dto.getRequest().stream().map(DummyDTO::getOwnerRegisterRequest).toList();

        if(users.size() != storeRequestList.size()){
            System.out.println("user.size() != storeRequestList.size()");
        }

        for(int i = 0; i < users.size(); i++){

            User user = users.get(i);
            OwnerRegisterRequest storeRequest = storeRequestList.get(i);
            ownerCommandService.applyStoreRegistration(user, storeRequest);
            System.out.println("이메일 : " + user.getEmail() + "가게명 : " + storeRequest.getStoreName() + " 저장 성공! ");

        }

    }

    @PostMapping("/show")
    @Operation(summary = "시연 편의 위해 쿠폰, 스탬프, 금액권 개수 조정")
    public void editDummy(@RequestBody EditRequest request){

        // 유저 조회
        Long userId = request.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저"));

        // 가게 조회
        Long storeId = request.getStoreId();
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 가게"));

        // 쿠폰 생성
        Coupon coupon = Coupon.builder()
                .name("서비스 쿠폰")
                .description("아주 멋진 쿠폰입니다.")
                .discountAmount(request.getCouponAmount())
                .status(CouponStatus.UNUSED)
                .qrCode(QrCodeType.COUPON.getQrPrefix() + UUID.randomUUID())
                .user(user)
                .store(store)
                .build();
        couponRepository.save(coupon);

        // 스탬프 점수 갱신
        Stamp stamp = stampRepository.findByUserAndStore(userId, storeId)
                .orElseGet( () -> Stamp.builder()
                        .count(request.getStampScore())
                        .user(user)
                        .store(store)
                        .build()
                );

        stamp.changeCount(request.getStampScore());
        stampRepository.save(stamp);

        // 금액권 생성

        int value = request.getVoucherValue();
        String voucherName;

        if (value <= 10000) {
            voucherName = "1만원권";
        } else if (value <= 30000) {
            voucherName = "3만원권";
        } else if (value <= 50000) {
            voucherName = "5만원권";
        } else {
            voucherName = "10만원권";
        }

        String qrUuid = QrCodeUtil.generatePrefixedUuid(QrCodeType.VOUCHER);
        Voucher voucher =
                Voucher.builder()
                        .qrCodeUuid(qrUuid)
                        .voucherName(voucherName)
                        .value(request.voucherValue)
                        .remainingValue(20000)
                        .validDays(LocalDate.now().plusYears(1).toString())
                        .payment(null)
                        .status(CouponStatus.UNUSED)
                        .user(user)
                        .build();

        voucherRepository.save(voucher);
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DummyList{

        List<DummyDTO> request;

    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DummyDTO{

        OwnerSignupRequest ownerSignupRequest;
        OwnerRegisterRequest ownerRegisterRequest;

    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditRequest{
        Long userId;
        Long storeId;
        Integer couponAmount;
        Integer stampScore;
        boolean makeVoucher;
        Integer voucherValue;
    }


}
