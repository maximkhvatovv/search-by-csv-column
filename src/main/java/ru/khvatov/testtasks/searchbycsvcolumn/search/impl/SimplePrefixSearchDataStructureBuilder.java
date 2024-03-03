package ru.khvatov.testtasks.searchbycsvcolumn.search.impl;

import ru.khvatov.testtasks.searchbycsvcolumn.csv.CSVReader;
import ru.khvatov.testtasks.searchbycsvcolumn.search.PrefixSearchDataStructure;
import ru.khvatov.testtasks.searchbycsvcolumn.search.PrefixSearchDataStructureBuilder;

import java.util.Optional;

public class SimplePrefixSearchDataStructureBuilder
        implements PrefixSearchDataStructureBuilder {

    private final CSVReader reader;
    private final PrefixSearchDataStructure prefixSearchDataStructure;

    public SimplePrefixSearchDataStructureBuilder(
            final CSVReader reader,
            final PrefixSearchDataStructure prefixSearchDataStructure
    ) {
        this.reader = reader;
        this.prefixSearchDataStructure = prefixSearchDataStructure;
    }

    @Override
    public PrefixSearchDataStructure build(final Integer indexedColumnNo) {

        try (reader;) {
            Optional<String[]> parsedRecord;
            while ((parsedRecord = reader.readRecord()).isPresent()) {
                final String[] values = parsedRecord.get();
                final Integer id = Integer.parseInt(values[0]);
                final String word = values[indexedColumnNo - 1];
                this.prefixSearchDataStructure.add(word, id);
            }
        } catch (final Exception ioException) {
            final String message = "Failed to read record from file";
            throw new RuntimeException(message, ioException);
        }
        return this.prefixSearchDataStructure;
    }
}
