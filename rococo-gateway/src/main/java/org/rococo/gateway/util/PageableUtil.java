package org.rococo.gateway.util;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class PageableUtil {

    private PageableUtil() {
    }

    @Nonnull
    public static String getPageableLogText(Pageable pageable) {
        return "page = %s, size = %s, direction = %s, columns = %s".formatted(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().iterator().hasNext()
                        ? pageable.getSort().iterator().next().getDirection().name()
                        : Sort.Direction.ASC.name(),
                pageable.getSort().stream().map(Sort.Order::getProperty).toList()
        );
    }

    @Nonnull
    public static String getLogText(Pageable pageable,
                                    Map<String, String> params
    ) {
        final var pageableText = getPageableLogText(pageable);
        final var paramsText = params.isEmpty()
                ? ""
                : params.keySet().stream()
                .filter(key -> params.get(key) != null)
                .map(key -> "%s = [%s]".formatted(key, params.get(key)))
                .collect(Collectors.joining(","));
        return paramsText.isEmpty() ? pageableText : paramsText + ", " + pageableText;
    }

}
