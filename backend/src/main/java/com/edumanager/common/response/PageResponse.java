package com.edumanager.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.List;

/**
 * 페이징 처리된 API 응답 클래스
 * Spring Data의 Page 객체를 클라이언트 친화적인 형태로 변환
 * @param <T> 응답 데이터 타입
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"success", "code", "message", "data", "page", "timestamp"})
public class PageResponse<T> {

    private boolean success;
    private String code;
    private String message;
    private List<T> data;
    private PageInfo page;
    private Instant timestamp;

    @Builder
    private PageResponse(boolean success, String code, String message, List<T> data, PageInfo page) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.page = page;
        this.timestamp = Instant.now();
    }

    /**
     * Spring Data Page 객체로부터 PageResponse 생성
     */
    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .success(true)
                .code("SUCCESS")
                .message("요청이 성공적으로 처리되었습니다.")
                .data(page.getContent())
                .page(PageInfo.of(page))
                .build();
    }

    /**
     * Spring Data Page 객체로부터 PageResponse 생성 (커스텀 메시지)
     */
    public static <T> PageResponse<T> of(Page<T> page, String message) {
        return PageResponse.<T>builder()
                .success(true)
                .code("SUCCESS")
                .message(message)
                .data(page.getContent())
                .page(PageInfo.of(page))
                .build();
    }

    /**
     * 페이징 정보를 담는 내부 클래스
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({"number", "size", "totalElements", "totalPages", "first", "last", "empty", "sorted", "sort"})
    public static class PageInfo {
        private int number;         // 현재 페이지 번호 (0부터 시작)
        private int size;           // 페이지 크기
        private long totalElements; // 전체 요소 수
        private int totalPages;     // 전체 페이지 수
        private boolean first;      // 첫 페이지 여부
        private boolean last;       // 마지막 페이지 여부
        private boolean empty;      // 빈 페이지 여부
        private boolean sorted;     // 정렬 여부
        private SortInfo sort;      // 정렬 정보

        @Builder
        private PageInfo(int number, int size, long totalElements, int totalPages,
                         boolean first, boolean last, boolean empty, boolean sorted, SortInfo sort) {
            this.number = number;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.first = first;
            this.last = last;
            this.empty = empty;
            this.sorted = sorted;
            this.sort = sort;
        }

        public static PageInfo of(Page<?> page) {
            Sort sort = page.getSort();
            SortInfo sortInfo = sort.isSorted() ? SortInfo.of(sort) : null;

            return PageInfo.builder()
                    .number(page.getNumber())
                    .size(page.getSize())
                    .totalElements(page.getTotalElements())
                    .totalPages(page.getTotalPages())
                    .first(page.isFirst())
                    .last(page.isLast())
                    .empty(page.isEmpty())
                    .sorted(sort.isSorted())
                    .sort(sortInfo)
                    .build();
        }
    }

    /**
     * 정렬 정보를 담는 내부 클래스
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @JsonPropertyOrder({"property", "direction", "ignoreCase", "nullHandling"})
    public static class SortInfo {
        private String property;
        private String direction;
        private boolean ignoreCase;
        private String nullHandling;

        @Builder
        private SortInfo(String property, String direction, boolean ignoreCase, String nullHandling) {
            this.property = property;
            this.direction = direction;
            this.ignoreCase = ignoreCase;
            this.nullHandling = nullHandling;
        }

        public static SortInfo of(Sort sort) {
            if (sort.isEmpty()) {
                return null;
            }

            // 첫 번째 정렬 조건만 반환 (간단한 구현)
            Sort.Order order = sort.iterator().next();
            return SortInfo.builder()
                    .property(order.getProperty())
                    .direction(order.getDirection().name())
                    .ignoreCase(order.isIgnoreCase())
                    .nullHandling(order.getNullHandling().name())
                    .build();
        }
    }
}
