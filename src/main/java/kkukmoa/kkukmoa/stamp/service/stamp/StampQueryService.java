package kkukmoa.kkukmoa.stamp.service.stamp;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.GeneralException;
import kkukmoa.kkukmoa.category.domain.Category;
import kkukmoa.kkukmoa.category.domain.CategoryType;
import kkukmoa.kkukmoa.category.repository.CategoryRepository;
import kkukmoa.kkukmoa.common.util.AuthService;
import kkukmoa.kkukmoa.stamp.converter.StampConverter;
import kkukmoa.kkukmoa.stamp.domain.Stamp;
import kkukmoa.kkukmoa.stamp.dto.stampDto.StampResponseDto;
import kkukmoa.kkukmoa.stamp.dto.stampDto.StampResponseDto.StampDto;
import kkukmoa.kkukmoa.stamp.dto.stampDto.StampResponseDto.StampListDto;
import kkukmoa.kkukmoa.stamp.repository.StampRepository;
import kkukmoa.kkukmoa.user.domain.User;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StampQueryService {

    private final StampRepository stampRepository;
    private final CategoryRepository categoryRepository;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public StampResponseDto.StampListDto stampList(CategoryType storeType) {

        // 로그인한 유저
        User user = authService.getCurrentUser();

        // List<Stamp> 생성
        List<Stamp> stampList;

        // 스탬프 조회
        if(storeType != null) { // store 카테고리 조건 있음

            Category category = categoryRepository
                .findByType(storeType)
                .orElseThrow(
                    () -> new GeneralException(ErrorStatus.STORE_CATEGORY_NOT_FOUND));

            // category, user로 조회
            stampList = stampRepository.findByCategoryAndUser(category, user);

        } else{ // store 카테고리 없음 ( ALL )
            // user로만 조회
            stampList = stampRepository.findByUser(user);
        }

        // dto -> List<dto>로 변환
        List<StampDto> stampListDto = StampConverter.toStampDtoList(stampList);

        // List<dto> -> 응답 형태로 변환 후 반환
        return StampListDto.builder().stamps(stampListDto).total(stampList.size()).build();
    }
}
