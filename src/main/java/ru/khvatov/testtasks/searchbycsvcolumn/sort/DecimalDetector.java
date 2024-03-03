package ru.khvatov.testtasks.searchbycsvcolumn.sort;

import java.math.BigDecimal;

public interface DecimalDetector {
    boolean isDecimal(final String value);

    final class Default implements DecimalDetector {
        @Override
        public boolean isDecimal(final String value) {
            try {
                final BigDecimal decimal = new BigDecimal(value);
                return true;
            } catch (final NumberFormatException exception) {
                return false;
            }
        }
    }
}
