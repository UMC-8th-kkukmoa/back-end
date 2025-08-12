package kkukmoa.kkukmoa.owner.service;

import kkukmoa.kkukmoa.owner.dto.OwnerRegisterCheckResponse;
import kkukmoa.kkukmoa.owner.dto.OwnerLoginRequest;
import kkukmoa.kkukmoa.store.repository.StoreRepository;
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

    // 입점신청 확인 서비스

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;

    public OwnerRegisterCheckResponse checkPending(OwnerLoginRequest req) {
        // 1) 사용자 조회 (이메일이 로그인 ID)
        User user = userRepository.findByEmail(req.getEmail()).orElse(null);

        // 2) 비밀번호 검증 (보안상 상세 사유 노출 금지)
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return OwnerRegisterCheckResponse.builder()
                    .pending(false)
                    .message("현재 진행 중인 신청이 없거나, 연락처/비밀번호가 올바르지 않습니다.")
                    .build();
        }

        // 3) PENDING 존재 여부 즉시 확인 (레포지토리 래퍼 메서드 사용)
        boolean hasPending = storeRepository.existsPending(user.getId());

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
        boolean hasPending = storeRepository.existsPending(user.getId());

        // 3) 응답 메시지 구성
        String message = hasPending ? "현재 신청이 검토 중입니다." : "진행 중인 신청이 없습니다.";
        return OwnerRegisterCheckResponse.builder().pending(hasPending).message(message).build();
    }
}
