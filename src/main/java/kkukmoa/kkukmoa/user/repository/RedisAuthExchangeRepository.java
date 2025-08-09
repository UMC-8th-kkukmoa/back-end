package kkukmoa.kkukmoa.user.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.TokenHandler;
import kkukmoa.kkukmoa.user.dto.TokenResponseDto;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisAuthExchangeRepository implements AuthExchangeRepository {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String PREFIX = "oauth:ex:";

    private String key(String code) {
        return PREFIX + code;
    }

    private static final org.springframework.data.redis.core.script.DefaultRedisScript<String>
            GET_AND_DEL_SCRIPT =
                    new org.springframework.data.redis.core.script.DefaultRedisScript<>(
                            "local v = redis.call('GET', KEYS[1]); "
                                    + "if v then redis.call('DEL', KEYS[1]); end; "
                                    + "return v",
                            String.class);

    @Override
    public void save(String code, TokenResponseDto tokens, Duration ttl) {
        try {
            String k = key(code);
            String json = objectMapper.writeValueAsString(tokens);
            Boolean ok = redisTemplate.opsForValue().setIfAbsent(k, json, ttl);
            if (Boolean.FALSE.equals(ok)) {
                throw new TokenHandler(ErrorStatus.EXCHANGE_CODE_DUPLICATE);
            }
        } catch (JsonProcessingException e) {
            throw new TokenHandler(ErrorStatus.EXCHANGE_CODE_SERIALIZE_FAIL);
        }
    }

    @Override
    public TokenResponseDto find(String code) {
        String k = key(code);

        String json =
                redisTemplate.execute(GET_AND_DEL_SCRIPT, java.util.Collections.singletonList(k));

        try {
            return objectMapper.readValue(json, TokenResponseDto.class);
        } catch (IOException e) {
            throw new TokenHandler(ErrorStatus.EXCHANGE_CODE_DESERIALIZE_FAIL);
        }
    }

    @Override
    public void delete(String code) {
        redisTemplate.delete(key(code));
    }
}
