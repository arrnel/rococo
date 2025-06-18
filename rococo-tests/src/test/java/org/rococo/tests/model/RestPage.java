package org.rococo.tests.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class RestPage<T> extends PageImpl<T> {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RestPage(@JsonProperty("content") List<T> content,
                    @JsonProperty("page") PageMetadata page) {
        super(content, PageRequest.of(page.number, page.size), page.totalElements);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PageMetadata {

        private final int size;
        private final int number;
        private final long totalElements;

        @JsonCreator
        public PageMetadata(
                @JsonProperty("size") Integer size,
                @JsonProperty("number") Integer number,
                @JsonProperty("totalElements") Long totalElements) {
            this.size = size != null ? size : 1;
            this.number = number != null ? number : 0;
            this.totalElements = totalElements != null ? totalElements : 0;
        }

    }

    public RestPage(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public RestPage(List<T> content) {
        super(content);
    }

    public RestPage() {
        super(new ArrayList<T>());
    }

}
