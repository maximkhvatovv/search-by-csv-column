package ru.khvatov.testtasks.searchbycsvcolumn.search;

import java.util.List;

public interface SearchExecutor {
    List<SearchingResult.SubResult> performSearch();
}
