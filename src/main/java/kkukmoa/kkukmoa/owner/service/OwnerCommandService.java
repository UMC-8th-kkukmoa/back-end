package kkukmoa.kkukmoa.owner.service;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.UserHandler;
import kkukmoa.kkukmoa.category.domain.Category;
import kkukmoa.kkukmoa.category.repository.CategoryRepository;
import kkukmoa.kkukmoa.config.security.JwtTokenProvider;
import kkukmoa.kkukmoa.owner.dto.request.OwnerLoginRequest;
import kkukmoa.kkukmoa.owner.dto.request.OwnerRegisterRequest;
import kkukmoa.kkukmoa.owner.dto.request.OwnerSignupRequest;
import kkukmoa.kkukmoa.region.domain.Region;
import kkukmoa.kkukmoa.region.service.RegionService;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.store.enums.StoreStatus;
import kkukmoa.kkukmoa.store.repository.StoreRepository;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.user.dto.TokenWithRolesResponseDto;
import kkukmoa.kkukmoa.user.enums.SocialType;
import kkukmoa.kkukmoa.user.enums.UserType;
import kkukmoa.kkukmoa.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class OwnerCommandService {

    private final RegionService regionService;

    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * [로컬 사장님 회원가입] - 약관 동의 필수 체크 - 이메일 중복 검사 - 비밀번호 암호화 후 USER 엔티티 저장 - 초기 권한: PENDING_OWNER (승인
     * 대기)
     */
    @Transactional
    public void registerLocalOwner(OwnerSignupRequest request) {
        // 0. 약관 동의 여부 확인
        if (!request.isAgreeTerms()) {
            throw new UserHandler(ErrorStatus.TERMS_NOT_AGREED);
        }
        if (!request.isAgreePrivacy()) {
            throw new UserHandler(ErrorStatus.PRIVACY_NOT_AGREED);
        }

        // 1. 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserHandler(ErrorStatus.DUPLICATION_DUPLICATION_EMAIL);
        }

        // 2. 유저 생성
        User user =
                User.builder()
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .socialType(SocialType.LOCAL)
                        .agreeTerms(request.isAgreeTerms())
                        .agreePrivacy(request.isAgreePrivacy())
                        .roles(Set.of(UserType.PENDING_OWNER)) // owner 승인 대기 role 부여
                        .build();

        userRepository.save(user);
    }

    /** [사장님 로그인] - 이메일 기반 사용자 조회 - 비밀번호 검증 - Access Token + Refresh Token 발급 및 저장 */
    @Transactional
    public TokenWithRolesResponseDto loginOwner(OwnerLoginRequest request) {
        User user =
                userRepository
                        .findByEmail(request.getEmail())
                        .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UserHandler(ErrorStatus.PASSWORD_NOT_MATCH);
        }

        return jwtTokenProvider.createTokenWithRoles(user); // access + refresh token 발급 및 저장
    }

    /**
     * [입점 신청 처리] - 신청자(User)와 매장 정보(OwnerRegisterRequest)로 Store 생성 - 중복 신청 방지 - 카테고리/지역 정보 연동 -
     * Store 상태: PENDING - 신청자 권한에 PENDING_OWNER 추가
     */
    @Transactional
    public void applyStoreRegistration(User user, OwnerRegisterRequest request) {

        user =
                userRepository
                        .findById(user.getId())
                        .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        /* 1) 중복 신청 방지 정책
         */
        if (storeRepository.existsByOwner(user)) {
            throw new UserHandler(ErrorStatus.OWNER_REQUEST_ALREADY_SUBMITTED);
        }

        /* 2) 카테고리 조회 (요청 displayName → enum → 엔티티) */
        Category category =
                categoryRepository
                        .findByType(request.getCategory())
                        .orElseThrow(() -> new UserHandler(ErrorStatus.STORE_CATEGORY_NOT_FOUND));

        /* 3) Region 연동 */
        Region region =
                regionService.createRegion(
                        request.getStoreAddress(),
                        request.getStoreAddressDetail(),
                        request.getLatitude(),
                        request.getLongitude());

        Store store =
                Store.builder()
                        .owner(user) // 신청자
                        .merchantNumber(request.getMerchantNumber())
                        .name(request.getStoreName()) // 매장명
                        .number(request.getStorePhoneNumber()) // 대표 전화
                        .storeImage(request.getStoreImageUrl()) // 이미지 URL
                        .openingHours(request.getOpeningHours())
                        .closingHours(request.getClosingHours())
                        .qrUrl(null) //
                        .region(region) //
                        .category(category)
                        .status(StoreStatus.PENDING) // 신청 = PENDING
                        .build();

        storeRepository.save(store);

        /* 5) 신청자 롤 갱신 (대기 상태를 표현)
         */
        user.getRoles().add(UserType.PENDING_OWNER);
    }
}
