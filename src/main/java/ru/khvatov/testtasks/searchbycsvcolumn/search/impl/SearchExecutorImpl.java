package ru.khvatov.testtasks.searchbycsvcolumn.search.impl;

import ru.khvatov.testtasks.searchbycsvcolumn.banchmarktools.Stopwatch;
import ru.khvatov.testtasks.searchbycsvcolumn.search.PrefixSearchDataStructure;
import ru.khvatov.testtasks.searchbycsvcolumn.search.SearchExecutor;
import ru.khvatov.testtasks.searchbycsvcolumn.search.SearchingResult;
import ru.khvatov.testtasks.searchbycsvcolumn.sort.Sorter;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class SearchExecutorImpl implements SearchExecutor {
    private final Stream<String> searchStrings;
    private final PrefixSearchDataStructure prefixSearchDataStructure;
    private final Sorter sorter;
    private final Stopwatch stopwatch;

    public SearchExecutorImpl(final Stream<String> searchStrings,
                              final PrefixSearchDataStructure prefixSearchDataStructure,
                              final Sorter sorter,
                              final Stopwatch stopwatch) {
        this.searchStrings = searchStrings;
        this.prefixSearchDataStructure = prefixSearchDataStructure;
        this.sorter = sorter;
        this.stopwatch = stopwatch;
    }

    @Override
    public List<SearchingResult.SubResult> performSearch() {
        return searchStrings.map(searchString -> {
                    stopwatch.start();

                    final List<Integer> searchedIds = prefixSearchDataStructure.findRowIdsByPrefix(
                            searchString, sorter
                    );

                    final long elapsedTime = stopwatch.getElapsedTime(TimeUnit.MILLISECONDS);

                    return SearchingResult.SubResult.builder()
                            .search(searchString)
                            .result(searchedIds)
                            .time(elapsedTime).build();
                }
        ).collect(toList());
    }
}
