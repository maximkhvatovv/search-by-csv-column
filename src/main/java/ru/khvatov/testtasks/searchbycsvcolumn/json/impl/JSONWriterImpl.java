package ru.khvatov.testtasks.searchbycsvcolumn.json.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.khvatov.testtasks.searchbycsvcolumn.json.JSONWriter;

import java.io.IOException;
import java.nio.file.Path;

import static java.lang.String.format;

public class JSONWriterImpl implements JSONWriter {
    private final Path filePath;
    private final ObjectMapper objectMapper;

    public JSONWriterImpl(final Path filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void write(final Object object) {
        try {
            this.objectMapper.writeValue(
                    this.filePath.toFile(), object
            );
        } catch (final IOException e) {
            final String message = format("Failed to write object %s to %s", object, this.filePath);
            throw new RuntimeException(message, e);
        }
    }
}
