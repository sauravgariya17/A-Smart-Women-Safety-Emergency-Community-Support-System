package com.gehu.safety.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor

public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public static <T> PageResponse<T> from(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    public PageResponse(List<T> content, int page, int size, long totalElements, int totalPages, boolean last) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
    }
    public static <T> Builder<T> builder() { return new Builder<>(); }
    public static class Builder<T> {
        private final PageResponse<T> value = new PageResponse<>();
        public Builder<T> content(List<T> value) { this.value.content = value; return this; }
        public Builder<T> page(int value) { this.value.page = value; return this; }
        public Builder<T> size(int value) { this.value.size = value; return this; }
        public Builder<T> totalElements(long value) { this.value.totalElements = value; return this; }
        public Builder<T> totalPages(int value) { this.value.totalPages = value; return this; }
        public Builder<T> last(boolean value) { this.value.last = value; return this; }
        public PageResponse<T> build() { return value; }
    }
}
