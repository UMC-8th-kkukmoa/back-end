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

        // 요청 받은 카테고리 예외 처리
        Category category =
            categoryRepository
                .findByType(storeType)
                .orElseThrow(() -> new GeneralException(ErrorStatus.STORE_CATEGORY_NOT_FOUND));

        // 스탬프 조회 ( 가게, 카테고리 fetch join )
        List<Stamp> stampList = stampRepository.findByCategoryAndUser(category, user);

        // dto -> List<dto>로 변환
        List<StampDto> stampListDto = StampConverter.toStampDtoList(stampList);

        // List<dto> -> 응답 형태로 변환 후 반환
        return StampListDto.builder().stamps(stampListDto).total(stampList.size()).build();
    }
}