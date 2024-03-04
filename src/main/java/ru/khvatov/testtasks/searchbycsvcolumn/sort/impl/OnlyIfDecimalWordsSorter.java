package ru.khvatov.testtasks.searchbycsvcolumn.sort.impl;

import ru.khvatov.testtasks.searchbycsvcolumn.sort.DecimalDetector;
import ru.khvatov.testtasks.searchbycsvcolumn.sort.WordsSorter;

import java.math.BigDecimal;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class OnlyIfDecimalWordsSorter implements WordsSorter {
    private final DecimalDetector decimalDetector;

    public OnlyIfDecimalWordsSorter(final DecimalDetector decimalDetector) {
        this.decimalDetector = decimalDetector;
    }

    @Override
    public List<String> sort(final List<String> words) {
        final boolean isWordDecimal = words.stream()
                .findFirst()
                .map(decimalDetector::isDecimal)
                .orElse(false);

        if (isWordDecimal) {
            return words.stream().sorted((w1, w2) -> {
                final BigDecimal first = new BigDecimal(w1);
                final BigDecimal second = new BigDecimal(w2);
                return first.compareTo(second);
            }).collect(toList());
        } else {
            return words;
        }
    }
}
