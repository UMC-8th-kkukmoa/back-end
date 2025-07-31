package kkukmoa.kkukmoa.stamp.service.stamp;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.QrHandler;
import kkukmoa.kkukmoa.common.enums.QrCodeType;
import kkukmoa.kkukmoa.common.util.AuthService;
import kkukmoa.kkukmoa.stamp.domain.Coupon;
import kkukmoa.kkukmoa.stamp.domain.Stamp;
import kkukmoa.kkukmoa.stamp.dto.stampDto.StampResponseDto;
import kkukmoa.kkukmoa.stamp.enums.CouponName;
import kkukmoa.kkukmoa.stamp.enums.CouponStatus;
import kkukmoa.kkukmoa.stamp.repository.CouponRepository;
import kkukmoa.kkukmoa.stamp.repository.StampRepository;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.store.repository.StoreRepository;
import kkukmoa.kkukmoa.user.domain.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StampCommandService {

  private final StoreRepository storeRepository;
  private final StampRepository stampRepository;
  private final StringRedisTemplate stringRedisTemplate;
  private final AuthService authService;
  private final CouponRepository couponRepository;

  // storeId를 찾기 위한 레디스 키 접두사
  private String REDIS_QR_PREFIX = "qrStore:";

  @Transactional(readOnly = false)
  public StampResponseDto.StampSaveDto save(String qrCode) {

    User user = authService.getCurrentUser();

    // key = REDIS_QR_PREFIX(접두사) + qrCode
    String qrStoreKey = REDIS_QR_PREFIX + qrCode;

    // QR 정보로 Redis 이용하여 가게 ID 조회하기
    Long storeId =
        Optional.ofNullable(stringRedisTemplate.opsForValue().get(qrStoreKey))
            .filter(value -> value.matches("\\d+"))
            .map(Long::parseLong)
            .orElseThrow(() -> new QrHandler(ErrorStatus.QR_INVALID));
    log.info("storeId = {}", storeId);

    // 스탬프 조회
    Optional<Stamp> optionalStamp = stampRepository.findByUserAndStore(user, storeId);

    Stamp stamp;
    Store store;

    // 스탬프 유무에 따른 처리
    if (optionalStamp.isPresent()) { // 스탬프가 존재하면
      stamp = optionalStamp.get();
      store = stamp.getStore();
    } else { // 스탬프가 존재하지 않으면
      store =
          storeRepository
              .findById(storeId)
              .orElseThrow(() -> new QrHandler(ErrorStatus.STORE_NOT_FOUND));
      stamp = makeStamp(user, store);
    }

    // 스탬프 적립
    stamp.saveStamp();
    stampRepository.save(stamp);
    log.info("스탬프를 적립했습니다. 현재 스탬프 점수 = {}개", stamp.getCount());

    // 스탬프 다 채우면
    boolean isComplete = stamp.getCount() == Stamp.maxCount;
    if (isComplete) { // Stamp 클래스에 max 지정되어 있음
      log.info("스탬프 완성! 쿠폰을 1개 발급했습니다.");
      stampRepository.delete(stamp); // 스탬프 제거
      couponRepository.save(makeCoupon(user, store)); // 쿠폰 발급
    }

    return StampResponseDto.StampSaveDto.builder().hasEarnedCoupon(isComplete).build();
  }

  private Stamp makeStamp(User user, Store store) {
    return Stamp.builder().user(user).store(store).count(0).build();
  }

  private Coupon makeCoupon(User user, Store store) {
    return Coupon.builder()
        .name(CouponName.SERVICE.getName())
        .description(store.getName())
        .discountAmount(0) // v1 까지는 0으로 두고 v2 이후에 수정
        .status(CouponStatus.UNUSED)
        .qrCode(QrCodeType.COUPON.getQrPrefix() + UUID.randomUUID())
        .user(user)
        .store(store)
        .build();
  }
}