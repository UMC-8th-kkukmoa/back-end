package kkukmoa.kkukmoa.user.repository;

import kkukmoa.kkukmoa.user.dto.TokenResponseDto;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;
@Repository
public interface AuthExchangeRepository {
    void save(String code, TokenResponseDto tokens, Duration ttl);
    TokenResponseDto find(String code);
    void delete(String code);
}
