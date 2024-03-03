package ru.khvatov.testtasks.searchbycsvcolumn.sort;

import java.util.List;

public interface Sorter {
    List<String> sort(final List<String> words);
}
