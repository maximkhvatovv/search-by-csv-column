package ru.khvatov.testtasks.searchbycsvcolumn;

import ru.khvatov.testtasks.searchbycsvcolumn.banchmarktools.Stopwatch;
import ru.khvatov.testtasks.searchbycsvcolumn.csv.CSVReader;
import ru.khvatov.testtasks.searchbycsvcolumn.csv.impl.MkyongAdoptedCSVReader;
import ru.khvatov.testtasks.searchbycsvcolumn.json.JSONWriter;
import ru.khvatov.testtasks.searchbycsvcolumn.json.impl.JSONWriterImpl;
import ru.khvatov.testtasks.searchbycsvcolumn.params.ProgramParams;
import ru.khvatov.testtasks.searchbycsvcolumn.params.ProgramParamsExtractor;
import ru.khvatov.testtasks.searchbycsvcolumn.params.impl.DefaultParamsExtractor;
import ru.khvatov.testtasks.searchbycsvcolumn.search.PrefixSearchDataStructure;
import ru.khvatov.testtasks.searchbycsvcolumn.search.PrefixSearchDataStructureBuilder;
import ru.khvatov.testtasks.searchbycsvcolumn.search.SearchExecutor;
import ru.khvatov.testtasks.searchbycsvcolumn.search.SearchStringsExtractor;
import ru.khvatov.testtasks.searchbycsvcolumn.search.SearchingResult;
import ru.khvatov.testtasks.searchbycsvcolumn.search.impl.DefaultSearchStringsExtractor;
import ru.khvatov.testtasks.searchbycsvcolumn.search.impl.SearchExecutorImpl;
import ru.khvatov.testtasks.searchbycsvcolumn.search.impl.SimplePrefixSearchDataStructureBuilder;
import ru.khvatov.testtasks.searchbycsvcolumn.search.impl.SortedMapPrefixSearchDataStructure;
import ru.khvatov.testtasks.searchbycsvcolumn.sort.DecimalDetector;
import ru.khvatov.testtasks.searchbycsvcolumn.sort.impl.OnlyIfDecimalSorter;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class App {

    public static void main(String[] args) {
        final Stopwatch initTimeStopwatch = new Stopwatch.NanoTimeBasedStopwatch();
        initTimeStopwatch.start();

        final ProgramParamsExtractor programParamsExtractor = new DefaultParamsExtractor(args);
        final ProgramParams params = programParamsExtractor.extract();

        final CSVReader csvReader = new MkyongAdoptedCSVReader(
                params.getPathToDataset(),
                MkyongAdoptedCSVReader.EmbeddedQuoteTemplate.ESCAPED
        );
        final PrefixSearchDataStructureBuilder prefixSearchDataStructureBuilder =
                new SimplePrefixSearchDataStructureBuilder(
                        csvReader,
                        new SortedMapPrefixSearchDataStructure()
                );
        final PrefixSearchDataStructure prefixSearchDataStructure
                = prefixSearchDataStructureBuilder.build(params.getColumnNoToSearchFor());


        final SearchStringsExtractor searchStringsExtractor = new DefaultSearchStringsExtractor(
                params.getPathToFileWithStringsToSearchFor()
        );

        final long initTime = initTimeStopwatch.getElapsedTime(TimeUnit.MILLISECONDS);

        final SearchExecutor searchExecutor = new SearchExecutorImpl(
                searchStringsExtractor.retrieveSearchStrings(),
                prefixSearchDataStructure,
                new OnlyIfDecimalSorter(new DecimalDetector.Default()),
                new Stopwatch.NanoTimeBasedStopwatch()
        );

        final List<SearchingResult.SubResult> searchResults = searchExecutor.performSearch();

        final SearchingResult searchingResult = SearchingResult.builder()
                .initTime(initTime)
                .result(searchResults)
                .build();

        final JSONWriter jsonWriter = new JSONWriterImpl(params.getPathToFileWithResultOfSearching());
        jsonWriter.write(searchingResult);

    }
}
