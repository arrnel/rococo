package org.rococo.tests.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.*;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PageableMap {

    @Builder.Default
    private Integer page = 0;

    @Builder.Default
    private Integer size = 10;

    @Builder.Default
    private Sort sort = new Sort();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class Sort {

        @Builder.Default
        private List<String> columns = new ArrayList<>();

        private Direction direction;

        public String toString() {

            var newList = new ArrayList<>(this.columns);
            Optional.ofNullable(direction).
                    ifPresent(d -> newList.add(d.toString()));

            String text = newList.stream()
                    .filter(Objects::nonNull)
                    .filter(s -> !s.isBlank())
                    .collect(Collectors.joining(","));

            return text.isBlank()
                    ? null
                    : text;

        }

    }

    public enum Direction {
        ASC, DESC
    }

    public static Map<String, String> defaultPageable() {
        HashMap<String, String> map = new HashMap<>();
        map.put("page", "0");
        map.put("size", "10");
        return map;
    }

    @Override
    public String toString() {
        return """
                {
                  "page": %d,
                  "size": %d,
                  "sort": %s
                }""".formatted(
                page,
                size,
                sort == null
                        ? null
                        : "\"" + sort + "\"");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PageableMap that = (PageableMap) o;
        return Objects.equals(page, that.page) && Objects.equals(size, that.size) && Objects.equals(sort, that.sort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), page, size, sort);
    }

}
