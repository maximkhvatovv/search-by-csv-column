package ru.khvatov.testtasks.searchbycsvcolumn.csv;

import java.util.Optional;

public interface CSVReader extends AutoCloseable {
    Optional<String[]> readRecord();
}
