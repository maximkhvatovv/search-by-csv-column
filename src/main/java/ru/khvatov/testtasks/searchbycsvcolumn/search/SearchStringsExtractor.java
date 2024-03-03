package ru.khvatov.testtasks.searchbycsvcolumn.search;

import java.util.stream.Stream;

public interface SearchStringsExtractor {
    Stream<String> retrieveSearchStrings();
}
