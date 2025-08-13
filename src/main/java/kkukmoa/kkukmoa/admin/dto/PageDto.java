package kkukmoa.kkukmoa.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

@Getter
@AllArgsConstructor
public class PageDto<T> implements Serializable {

    @JsonProperty("items")
    private final List<T> items;

    @JsonProperty("page")
    private final PageMeta page;

    @Getter
    @AllArgsConstructor
    public static class PageMeta implements Serializable {
        private final int number;
        private final int size;
        private final long totalElements;
        private final int totalPages;
        private final boolean hasNext;
        private final boolean hasPrevious;
    }

    /** Spring Data Page -> PageDto - 번호형 페이지네이션 UI에 적합 (totalElements/totalPages 포함) */
    public static <T> PageDto<T> from(Page<T> page) {
        return new PageDto<>(
                page.getContent(),
                new PageMeta(
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalElements(),
                        page.getTotalPages(),
                        page.hasNext(),
                        page.hasPrevious()));
    }
}
