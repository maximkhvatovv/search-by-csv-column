package ru.khvatov.testtasks.searchbycsvcolumn.search;

import ru.khvatov.testtasks.searchbycsvcolumn.sort.Sorter;

import java.util.List;

public interface PrefixSearchDataStructure {
    void add(final String word, final Integer rowId);

    List<Integer> findRowIdsByPrefix(final String prefix, final Sorter sorter);
}
