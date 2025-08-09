package kkukmoa.kkukmoa.owner.service;

import kkukmoa.kkukmoa.owner.dto.OwnerRegisterCheckResponse;
import kkukmoa.kkukmoa.owner.dto.OwnerSignupRequest;
import kkukmoa.kkukmoa.store.enums.StoreRegistrationStatus;
import kkukmoa.kkukmoa.store.repository.StoreRegistrationRepository;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OwnerRegisterCheckService {

    private final UserRepository userRepository;
    private final StoreRegistrationRepository storeRegistrationRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * [비로그인용] 연락처/비밀번호로 본인 확인 후 PENDING 여부 반환. - 사용자 미존재/비번 불일치는 동일 메시지로 응답하여 계정 유추 방지 - 존재 여부는
     * JPQL existsPending(...)으로 즉시 확인 (인덱스 있으면 매우 빠름)
     */
    public OwnerRegisterCheckResponse checkPending(OwnerSignupRequest req) {
        // 1) 사용자 조회 (연락처가 로그인 ID)
        User user = userRepository.findByPhoneNumber(req.getPhoneNumber()).orElse(null);

        // 2) 비밀번호 검증 (보안상 상세 사유 노출 금지)
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return OwnerRegisterCheckResponse.builder()
                    .pending(false)
                    .message("현재 진행 중인 신청이 없거나, 연락처/비밀번호가 올바르지 않습니다.")
                    .build();
        }

        // 3) PENDING 존재 여부 즉시 확인
        boolean hasPending =
                storeRegistrationRepository.existsPending(
                        user.getId(), StoreRegistrationStatus.PENDING);

        // 4) 응답 메시지 구성
        String message = hasPending ? "현재 신청이 검토 중입니다." : "진행 중인 신청이 없습니다.";
        return OwnerRegisterCheckResponse.builder().pending(hasPending).message(message).build();
    }

    /** [로그인 사용자용] 이미 인증된 사용자(userId) 기준으로 PENDING 여부만 확인. - 비밀번호 검증 없음 - 소셜/일반 로그인 공통 사용 */
    public OwnerRegisterCheckResponse checkPendingForUser(Long userId) {
        // 1) 사용자 존재 검증
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        // 2) PENDING 존재 여부 즉시 확인
        boolean hasPending =
                storeRegistrationRepository.existsPending(
                        user.getId(), StoreRegistrationStatus.PENDING);

        // 3) 응답 메시지 구성
        String message = hasPending ? "현재 신청이 검토 중입니다." : "진행 중인 신청이 없습니다.";
        return OwnerRegisterCheckResponse.builder().pending(hasPending).message(message).build();
    }
}
