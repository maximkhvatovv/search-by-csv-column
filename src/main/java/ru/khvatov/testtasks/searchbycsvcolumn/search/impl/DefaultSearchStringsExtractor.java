package ru.khvatov.testtasks.searchbycsvcolumn.search.impl;

import ru.khvatov.testtasks.searchbycsvcolumn.Settings;
import ru.khvatov.testtasks.searchbycsvcolumn.search.SearchStringsExtractor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

import static java.lang.String.format;

public class DefaultSearchStringsExtractor implements SearchStringsExtractor {
    private final Path filePath;

    public DefaultSearchStringsExtractor(final Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public Stream<String> retrieveSearchStrings() {
        final List<String> searchStrings = new ArrayList<>();
        try (final var scanner = new Scanner(filePath.toFile(), Settings.CHARSET);) {
            while (scanner.hasNextLine()) {
                searchStrings.add(scanner.nextLine());
            }
        } catch (final IOException e) {
            final String message = format("Fail to read %s", filePath);
            throw new RuntimeException(message, e);
        }
        return searchStrings.stream();
    }
}
