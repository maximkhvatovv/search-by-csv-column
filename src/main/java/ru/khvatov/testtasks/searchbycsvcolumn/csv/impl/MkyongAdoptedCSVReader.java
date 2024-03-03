package ru.khvatov.testtasks.searchbycsvcolumn.csv.impl;

import lombok.Getter;
import ru.khvatov.testtasks.searchbycsvcolumn.Settings;
import ru.khvatov.testtasks.searchbycsvcolumn.csv.CSVReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Objects.isNull;

/**
 * Адаптированное решение mkyong <a href="https://mkyong.com/java/how-to-read-and-parse-csv-file-in-java/#single-class-to-read-and-parse-a-csv-file">CsvParserSimple</a>,
 * файл airports.csv содержит embedded quotes, которые не соответствуют RFC 4180, т.е. вместо "" внутри филдов
 * используется \", для решения данной проблемы используются {@link EmbeddedQuoteTemplate}, {@link MkyongAdoptedCSVReader#processEmbeddedQuote}
 */
public class MkyongAdoptedCSVReader implements CSVReader {
    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DOUBLE_QUOTES = '"';
    private static final char DEFAULT_QUOTE_CHAR = DOUBLE_QUOTES;
    private static final String NEW_LINE = "\n";

    private boolean isMultiLine = false;
    private String pendingField = "";
    private String[] pendingFieldLine = new String[]{};

    @Getter
    public enum EmbeddedQuoteTemplate {
        ESCAPED("\\\""),
        DOUBLE("\"\"");

        private final String value;

        EmbeddedQuoteTemplate(String value) {
            this.value = value;
        }

    }

    private final Map<EmbeddedQuoteTemplate, Consumer<StringBuilder>> processEmbeddedQuote
            = Map.of(
            EmbeddedQuoteTemplate.DOUBLE, sB -> {
            },
            EmbeddedQuoteTemplate.ESCAPED, sb -> {
                if (sb.length() != 0) {
                    char lastChar = sb.charAt(sb.length() - 1);
                    if (lastChar == '\\') {
                        sb.setLength(sb.length() - 1);
                        sb.append(DOUBLE_QUOTES);
                    }
                }
            }
    );

    private final Path filePath;
    private final char separator;
    private final EmbeddedQuoteTemplate embeddedQuoteTemplate;
    private final int skipLine;
    private BufferedReader bufferedReader;
    private int indexLine;
    private boolean closed;


    public MkyongAdoptedCSVReader(
            final Path filePath,
            final EmbeddedQuoteTemplate embeddedQuoteTemplate
    ) {
        this(filePath, DEFAULT_SEPARATOR, embeddedQuoteTemplate, 0);
    }

    public MkyongAdoptedCSVReader(
            final Path filePath,
            final char separator,
            final EmbeddedQuoteTemplate embeddedQuoteTemplate
    ) {
        this(filePath, separator, embeddedQuoteTemplate, 0);
    }

    public MkyongAdoptedCSVReader(
            final Path filePath,
            final char separator,
            final EmbeddedQuoteTemplate embeddedQuoteTemplate,
            final int skipLine
    ) {
        this.filePath = filePath;
        this.separator = separator;
        this.embeddedQuoteTemplate = embeddedQuoteTemplate;
        this.skipLine = skipLine;
        this.indexLine = 1;
    }

    @Override
    public Optional<String[]> readRecord() {
        initBufferedReaderIfNotInitYet();
        try {
            if (closed) {
                return Optional.empty();
            }
            while (true) {
                final String line = bufferedReader.readLine();
                if (line == null) {
                    closeIfNotClosedYet();
                    return Optional.empty();
                }

                if (indexLine++ <= skipLine) {
                    continue;
                }

                final String[] csvLineInArray = parse(line);

                if (isMultiLine) {
                    pendingFieldLine = joinArrays(pendingFieldLine, csvLineInArray);
                } else {

                    if (pendingFieldLine != null && pendingFieldLine.length > 0) {
                        // joins all fields and add to list
                        final String[] copyPendingField = Arrays.copyOf(pendingFieldLine, pendingField.length());
                        pendingFieldLine = new String[]{};
                        return Optional.of(joinArrays(copyPendingField, csvLineInArray));
                    } else {
                        // if you don't want to support multiline, only this line is required.
                        return Optional.of(csvLineInArray);
                    }

                }

            }
        } catch (final Exception exception) {
            final String message = format("Failed to read record from %s, line index: %d", filePath, this.indexLine);
            throw new RuntimeException(message, exception);
        }
    }

    private void initBufferedReaderIfNotInitYet() {
        if (isNull(bufferedReader)) {
            try {
                this.bufferedReader = Files.newBufferedReader(filePath, Settings.CHARSET);
            } catch (final IOException e) {
                throw new RuntimeException("Failed to init bufferedReader", e);
            }
        }
    }

    private void closeIfNotClosedYet() {
        try {
            close();
        } catch (final Exception e) {
            throw new RuntimeException("Failed to close bufferedReader", e);
        }
    }

    @Override
    public void close() throws Exception {
        if (!closed) {
            bufferedReader.close();
            closed = true;
        }
    }

    private String[] parse(final String line)
            throws Exception {

        final List<String> result = new ArrayList<>();

        boolean inQuotes = false;
        boolean isFieldWithEmbeddedDoubleQuotes = false;

        StringBuilder field = new StringBuilder();

        for (char c : line.toCharArray()) {

            if (c == DOUBLE_QUOTES) {               // handle embedded double quotes ""
                if (isFieldWithEmbeddedDoubleQuotes) {

                    if (field.length() > 0) {       // handle for empty field like "",""
                        field.append(DOUBLE_QUOTES);
                        isFieldWithEmbeddedDoubleQuotes = false;
                    }

                } else {
                    isFieldWithEmbeddedDoubleQuotes = true;
                }
            } else {
                isFieldWithEmbeddedDoubleQuotes = false;
            }

            if (isMultiLine) {                      // multiline, add pending from the previous field
                field.append(pendingField).append(NEW_LINE);
                pendingField = "";
                inQuotes = true;
                isMultiLine = false;
            }

            if (c == DEFAULT_QUOTE_CHAR) {
                processEmbeddedQuote.get(this.embeddedQuoteTemplate).accept(field);
                inQuotes = !inQuotes;
            } else {
                if (c == this.separator && !inQuotes) {  // if find separator and not in quotes, add field to the list
                    result.add(field.toString());
                    field.setLength(0);             // empty the field and ready for the next
                } else {
                    field.append(c);                // else append the char into a field
                }
            }

        }

        //line done, what to do next?
        if (inQuotes) {
            pendingField = field.toString();        // multiline
            isMultiLine = true;
        } else {
            result.add(field.toString());           // this is the last field
        }

        return result.toArray(String[]::new);

    }

    private String[] joinArrays(final String[] array1, final String[] array2) {
        return Stream.concat(Arrays.stream(array1), Arrays.stream(array2))
                .toArray(String[]::new);
    }
}
