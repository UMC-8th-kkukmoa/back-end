/*
package kkukmoa.kkukmoa.owner.service;


import kkukmoa.kkukmoa.common.util.AuthService;
import kkukmoa.kkukmoa.owner.dto.request.OwnerLoginRequest;
import kkukmoa.kkukmoa.owner.dto.response.OwnerRegisterCheckResponse;
import kkukmoa.kkukmoa.owner.dto.OwnerQrResponseDto;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.store.repository.StoreRepository;
import kkukmoa.kkukmoa.user.repository.UserRepository;
import kkukmoa.kkukmoa.voucher.domain.Voucher;
import kkukmoa.kkukmoa.voucher.repository.VoucherRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OwnerQueryServiceTest {

    @Mock private AuthService authService;
    @Mock private StoreRepository storeRepository;
    @Mock private VoucherRepository voucherRepository;
    @Mock private RedisTemplate<String, String> redisTemplate;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private OwnerQueryService ownerQueryService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getStamp_성공() {
        // given
        User mockUser = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("encoded")
                .build();

        Store mockStore = Store.builder().id(1L).name("스타벅스 강남점").owner(mockUser).build();

        when(authService.getCurrentUser()).thenReturn(mockUser);
        when(storeRepository.findByOwner(mockUser)).thenReturn(Optional.of(mockStore));

        // when
        OwnerQrResponseDto.QrDto result = ownerQueryService.getStamp();

        // then
        assertThat(result.getStoreName()).isEqualTo("스타벅스 강남점");
        assertThat(result.getQrCode()).isNotBlank();
    }

    @Test
    void getQrType_Voucher일_경우_잔액조회() {
        // given
        Voucher voucher = Voucher.builder().qrCodeUuid("voucher:123").remainingValue(5000).build();
        when(voucherRepository.findByQrCodeUuid("voucher:123")).thenReturn(Optional.of(voucher));

        // when
        var result = ownerQueryService.getQrType("voucher:123");

        // then
        assertThat(result.getType().name()).isEqualTo("VOUCHER");
        assertThat(result.getBalance()).isEqualTo(5000);
    }

    @Test
    void checkPending_성공적으로_Pending존재() {
        // given
        User mockUser = User.builder().id(1L).email("test@test.com").password("encoded").build();
        OwnerLoginRequest request = new OwnerLoginRequest("test@test.com", "1234");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("1234", "encoded")).thenReturn(true);
        when(storeRepository.existsPending(1L)).thenReturn(true);

        // when
        OwnerRegisterCheckResponse response = ownerQueryService.checkPending(request);

        // then
        assertThat(response.isPending()).isTrue();
        assertThat(response.getMessage()).contains("검토 중");
    }

    @Test
    void checkPendingForUser_신청없을때() {
        // given
        User mockUser = User.builder().id(2L).email("user@test.com").build();
        when(userRepository.findById(2L)).thenReturn(Optional.of(mockUser));
        when(storeRepository.existsPending(2L)).thenReturn(false);

        // when
        OwnerRegisterCheckResponse response = ownerQueryService.checkPendingForUser(2L);

        // then
        assertThat(response.isPending()).isFalse();
        assertThat(response.getMessage()).contains("없습니다");
    }
}
*/
