package ru.khvatov.testtasks.searchbycsvcolumn.search.impl;

import ru.khvatov.testtasks.searchbycsvcolumn.banchmarktools.Stopwatch;
import ru.khvatov.testtasks.searchbycsvcolumn.search.PrefixSearchDataStructure;
import ru.khvatov.testtasks.searchbycsvcolumn.search.SearchExecutor;
import ru.khvatov.testtasks.searchbycsvcolumn.search.SearchingResult;
import ru.khvatov.testtasks.searchbycsvcolumn.sort.WordsSorter;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

public class SearchExecutorImpl implements SearchExecutor {
    private final List<String> searchStrings;
    private final PrefixSearchDataStructure prefixSearchDataStructure;
    private final WordsSorter wordsSorter;
    private final Stopwatch stopwatch;

    public SearchExecutorImpl(final List<String> searchStrings,
                              final PrefixSearchDataStructure prefixSearchDataStructure,
                              final WordsSorter wordsSorter,
                              final Stopwatch stopwatch) {
        this.searchStrings = searchStrings;
        this.prefixSearchDataStructure = prefixSearchDataStructure;
        this.wordsSorter = wordsSorter;
        this.stopwatch = stopwatch;
    }

    @Override
    public List<SearchingResult.SubResult> performSearch() {
        return searchStrings.stream().map(searchString -> {
                    stopwatch.start();

                    final List<Integer> searchedIds = prefixSearchDataStructure.findRowIdsByPrefix(
                            searchString, wordsSorter
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
