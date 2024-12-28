package com.medix.medix.utils;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ThymeleafBinder {
    public String generateSortUrl(Pageable pageable, String route, String fieldName, String extraParams) {
        return generateSortUrl(pageable, route, List.of(fieldName), extraParams);
    }

    public String generateSortUrl(Pageable pageable, String route, List<String> fieldNames, String extraParams) {
        boolean isAscending = pageable.getSort()
                .getOrderFor(fieldNames.get(0)) == null ||
                pageable.getSort().getOrderFor(fieldNames.get(0)).isAscending();
        String sortOrder = isAscending ? "DESC" : "ASC";

        String sortParams = fieldNames.stream()
                .map(field -> field + "," + sortOrder)
                .collect(Collectors.joining("&sort="));

        return String.format("%s?page=%d&size=%d&sort=%s&%s",
                route,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sortParams,
                (extraParams != null ? extraParams : ""));
    }

    public String toQueryString(Object form) {
        return Arrays.stream(form.getClass().getDeclaredFields())
                .peek(f -> f.setAccessible(true))
                .map(f -> {
                    try {
                        Object value = f.get(form);
                        return value != null ? f.getName() + "=" + value : null;
                    } catch (IllegalAccessException e) {
                        return null;
                    }
                })
                .filter(s -> s != null)
                .collect(Collectors.joining("&"));
    }

    public String generatePaginationUrl(String route, Pageable pageable, Long page, String extraParams) {
        String sortParams = pageable.getSort().stream()
                .map(order -> order.getProperty() + "," + order.getDirection())
                .collect(Collectors.joining("&sort="));

        return String.format("%s?page=%d&size=%d&sort=%s&%s",
                route,
                page,
                pageable.getPageSize(),
                sortParams,
                (extraParams != null ? extraParams : ""));
    }
}