package kkukmoa.kkukmoa.owner.service;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.QrHandler;
import kkukmoa.kkukmoa.common.enums.QrCodeType;
import kkukmoa.kkukmoa.common.util.AuthService;
import kkukmoa.kkukmoa.common.util.QrCodeUtil;
import kkukmoa.kkukmoa.owner.dto.OwnerQrResponseDto;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.store.repository.StoreRepository;
import kkukmoa.kkukmoa.user.domain.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OwnerQueryService {

    private final AuthService authService;
    private final StoreRepository storeRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private int qrCodeExpirationTime = 300;

    /**
     * 스탬프의 QR 코드를 생성하고 반환합니다. QR 코드 생성 시, QR 코드 정보를
     *
     * @return 클라이언트에 보여질 Dto 반환
     */
    @Transactional(readOnly = true)
    public OwnerQrResponseDto.QrDto getStamp() {

        // 정보 추출
        User currentOwner = authService.getCurrentUser();
        Store store =
                storeRepository
                        .findByOwner(currentOwner)
                        .orElseThrow(() -> new QrHandler(ErrorStatus.OWNER_STORE_NOT_FOUND));

        // redis 캐시에서 QR 정보 가져옴
        String qrSource = QrCodeType.STAMP.getQrPrefix() + UUID.randomUUID();
        makeOrUpdateQrCode(qrSource, String.valueOf(store.getId()));

        // QR 코드 생성하여 Dto 반환
        return OwnerQrResponseDto.QrDto.builder()
                .qrCode(QrCodeUtil.qrCodeToBase64(qrSource))
                .build();
    }

    /**
     * Redis에 QR 코드와 관련된 정보 저장
     *
     * <p>하나의 Store에 대해 항상 하나의 스탬프 QR 코드만 유지되록 관리합니다. 이미 저장된 QR 코드가 있으면 해당 QR 코드에 대한 Redis 키들은
     * 삭제됩니다. 새로 생성되는 QR 코드에 대한 Redis 소스는 다음과 같습니다 - key: 'qrCode:{storeId}' -> value: {qrSource} -
     * key: 'qrStore:{qrSource} -> value: {storeId}
     *
     * @param qrSource 새로 생성될 QR 코드의 문자열
     * @param storeId 해당 QR 코드를 사용하는 Store 의 식별자
     */
    private void makeOrUpdateQrCode(String qrSource, String storeId) {

        String qrCodeKey = "qrCode:" + storeId; // QR 정보 가져오는 키
        String qrStoreKey = "qrStore:" + qrSource; // StoreId 가져오는 키

        String oldQrSource = redisTemplate.opsForValue().get(qrCodeKey); // 기존 QR 코드 정보

        // 가게에 해당하는 QR 코드가 존재한다면 기존 QR 소스 삭제
        if (oldQrSource != null) {
            log.info("기존 QR 정보 삭제. storeId = {}", storeId);
            String oldQrStoreKey = "qrStore:" + oldQrSource;
            redisTemplate.delete(oldQrStoreKey);
            redisTemplate.delete(qrCodeKey);
        }

        // 새로운 QR 소스 저장
        // key: storeId, value: QR
        redisTemplate.opsForValue().set(qrCodeKey, qrSource, qrCodeExpirationTime, TimeUnit.SECONDS);

        // key: QR, value: storeId
        redisTemplate.opsForValue().set(qrStoreKey, storeId, qrCodeExpirationTime, TimeUnit.SECONDS);

        log.info("새로 생성한 qrSource = {}", qrSource);
    }
}
