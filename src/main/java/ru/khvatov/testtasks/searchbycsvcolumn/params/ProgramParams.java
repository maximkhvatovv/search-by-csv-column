package ru.khvatov.testtasks.searchbycsvcolumn.params;

import lombok.Data;
import lombok.Getter;

import java.nio.file.Path;

@Data
public class ProgramParams {
    @Getter
    public enum ParamKey {
        PATH_TO_DATASET("--data"),
        COLUMN_NO_TO_SEARCH_FOR("--indexed-column-id"),
        PATH_TO_FILE_WITH_STRINGS_TO_SEARCH_FOR("--input-file"),
        PATH_TO_FILE_WITH_RESULT_OF_SEARCHING("--output-file");
        private final String value;

        ParamKey(final String value) {
            this.value = value;
        }
    }

    private final Path pathToDataset;

    private final int columnNoToSearchFor;

    private final Path pathToFileWithStringsToSearchFor;

    private final Path pathToFileWithResultOfSearching;

}
