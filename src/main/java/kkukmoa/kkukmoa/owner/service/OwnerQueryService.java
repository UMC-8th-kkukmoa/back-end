package kkukmoa.kkukmoa.owner.service;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.GeneralException;
import kkukmoa.kkukmoa.apiPayload.exception.handler.QrHandler;
import kkukmoa.kkukmoa.common.enums.QrCodeType;
import kkukmoa.kkukmoa.common.util.AuthService;
import kkukmoa.kkukmoa.common.util.QrCodeUtil;
import kkukmoa.kkukmoa.owner.dto.OwnerQrResponseDto;
import kkukmoa.kkukmoa.owner.dto.request.OwnerLoginRequest;
import kkukmoa.kkukmoa.owner.dto.response.OwnerRegisterCheckResponse;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.store.repository.StoreRepository;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.user.repository.UserRepository;
import kkukmoa.kkukmoa.voucher.domain.Voucher;
import kkukmoa.kkukmoa.voucher.repository.VoucherRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final VoucherRepository voucherRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
        redisTemplate.opsForValue().set(qrCodeKey, qrSource, 60, TimeUnit.SECONDS);

        // key: QR, value: storeId
        redisTemplate.opsForValue().set(qrStoreKey, storeId, 60, TimeUnit.SECONDS);

        log.info("새로 생성한 qrSource = {}", qrSource);
    }

    @Transactional(readOnly = true)
    public OwnerQrResponseDto.QrTypeDto getQrType(String qrCode) {

        // 요청받은 qr 코드 정보로 유형 구분
        QrCodeType qrType = QrCodeType.getQrCodeTypeByQrPrefix(qrCode);

        // 스탬프일 경우 예외 발생
        if (qrType == QrCodeType.STAMP) {
            throw new GeneralException(ErrorStatus.QR_INVALID_TYPE);
        }

        // 쿠폰 or 금액권 뭐지 될지 모르니 Object 객체로 우선 생성
        Voucher voucher;

        // 바우처일 경우 voucher 정보 조회 ( 남은 금액 조회 목적 )
        if (qrType == QrCodeType.VOUCHER) {
            voucher =
                    voucherRepository
                            .findByQrCodeUuid(qrCode)
                            .orElseThrow(() -> new GeneralException(ErrorStatus.VOUCHER_NOT_FOUND));
        } else {
            voucher = null;
        }

        // 남은 금액 조회
        Integer remainValue = qrType == QrCodeType.VOUCHER ? voucher.getRemainingValue() : null;

        return OwnerQrResponseDto.QrTypeDto.builder().type(qrType).balance(remainValue).build();
    }

    /**
     * [비로그인/로그인 공통 진입점] 연락처/비밀번호를 받아 PENDING 여부를 확인합니다. - 이메일을 로그인 ID로 사용한다고 가정합니다. - 사용자 조회 및 비밀번호
     * 검증 후, 해당 사용자의 PENDING 존재 여부만 반환합니다. - 보안상으로 "존재/불일치"를 구분하지 않고 동일 메시지를 제공합니다.
     */
    @Transactional(readOnly = true)
    public OwnerRegisterCheckResponse checkPending(OwnerLoginRequest req) {
        // 1) 사용자 조회
        User user = userRepository.findByEmail(req.getEmail()).orElse(null);

        // 2) 비밀번호 검증 (상세 사유 노출 금지)
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return OwnerRegisterCheckResponse.builder()
                    .pending(false)
                    .message("현재 진행 중인 신청이 없거나, 연락처/비밀번호가 올바르지 않습니다.")
                    .build();
        }

        // 3) PENDING 존재 여부 확인
        boolean hasPending = storeRepository.existsPending(user.getId());

        // 4) 메시지 구성
        String message = hasPending ? "현재 신청이 검토 중입니다." : "진행 중인 신청이 없습니다.";
        return OwnerRegisterCheckResponse.builder().pending(hasPending).message(message).build();
    }

    /**
     * [로그인 사용자 전용] 이미 인증된 userId 기준으로 PENDING 여부만 확인합니다. - 소셜/일반 로그인 공통 사용. - 비밀번호 검증 없음(이미 인증된
     * 컨텍스트).
     */
    @Transactional(readOnly = true)
    public OwnerRegisterCheckResponse checkPendingForUser(Long userId) {
        // 1) 사용자 존재 검증
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        // 2) PENDING 존재 여부 확인
        boolean hasPending = storeRepository.existsPending(user.getId());

        // 3) 메시지 구성
        String message = hasPending ? "현재 신청이 검토 중입니다." : "진행 중인 신청이 없습니다.";
        return OwnerRegisterCheckResponse.builder().pending(hasPending).message(message).build();
    }
}
