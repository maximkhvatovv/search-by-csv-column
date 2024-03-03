package ru.khvatov.testtasks.searchbycsvcolumn.params.impl;

import ru.khvatov.testtasks.searchbycsvcolumn.params.ProgramParams;
import ru.khvatov.testtasks.searchbycsvcolumn.params.ProgramParamsExtractor;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static ru.khvatov.testtasks.searchbycsvcolumn.params.ProgramParams.ParamKey;
import static ru.khvatov.testtasks.searchbycsvcolumn.params.ProgramParams.ParamKey.COLUMN_NO_TO_SEARCH_FOR;
import static ru.khvatov.testtasks.searchbycsvcolumn.params.ProgramParams.ParamKey.PATH_TO_DATASET;
import static ru.khvatov.testtasks.searchbycsvcolumn.params.ProgramParams.ParamKey.PATH_TO_FILE_WITH_RESULT_OF_SEARCHING;
import static ru.khvatov.testtasks.searchbycsvcolumn.params.ProgramParams.ParamKey.PATH_TO_FILE_WITH_STRINGS_TO_SEARCH_FOR;

public class DefaultParamsExtractor implements ProgramParamsExtractor {
    private final List<ParamKey> extractingParams = List.of(
            PATH_TO_DATASET,
            COLUMN_NO_TO_SEARCH_FOR,
            PATH_TO_FILE_WITH_STRINGS_TO_SEARCH_FOR,
            PATH_TO_FILE_WITH_RESULT_OF_SEARCHING
    );
    private final Map<String, String> paramNameToParamValue;

    public DefaultParamsExtractor(final String[] args) {
        validateSize(args);
        this.paramNameToParamValue = this.toMap(args);
        checkAllParamsIsPresent(this.paramNameToParamValue);
        checkAllParamsIsValid(this.paramNameToParamValue);
    }

    private void validateSize(final String[] args) {
        if (args.length != extractingParams.size() * 2) {
            final String message = format(
                    "program must take %d parameters: %s.",
                    extractingParams.size(), extractingParams.stream().map(ParamKey::getValue).collect(joining(", "))
            );
            throw new IllegalArgumentException(message);
        }
    }

    private Map<String, String> toMap(final String[] args) {
        final Map<String, String> paramNameToParamValue = new HashMap<>();
        for (int i = 0; i < args.length - 1; i += 2) {
            final String paramName = args[i].trim();
            final String paramValue = args[i + 1].trim();
            paramNameToParamValue.put(paramName, paramValue);
        }
        return paramNameToParamValue;
    }

    private void checkAllParamsIsPresent(final Map<String, String> paramNameToParamValue) {
        this.extractingParams.stream().map(ParamKey::getValue).forEach(l -> {
            if (!paramNameToParamValue.containsKey(l)) {
                throw new IllegalArgumentException(format("The parameter `%s` must be present", l));
            }
        });
    }

    private void checkAllParamsIsValid(final Map<String, String> paramNameToParamValue) {
        //todo: check that path is path, column num is num
    }

    @Override
    public ProgramParams extract() {
        final String pathToDataset = paramNameToParamValue.get(
                PATH_TO_DATASET.getValue()
        );
        final int columnNo = Integer.parseInt(
                paramNameToParamValue.get(COLUMN_NO_TO_SEARCH_FOR.getValue())
        );
        final String pathToFileWithStringsToSearchFor = paramNameToParamValue.get(
                PATH_TO_FILE_WITH_STRINGS_TO_SEARCH_FOR.getValue()
        );
        final String pathToFileWithResultOfSearching = paramNameToParamValue.get(
                PATH_TO_FILE_WITH_RESULT_OF_SEARCHING.getValue()
        );
        return new ProgramParams(
                Paths.get(pathToDataset),
                columnNo,
                Paths.get(pathToFileWithStringsToSearchFor),
                Paths.get(pathToFileWithResultOfSearching)
        );
    }

}
