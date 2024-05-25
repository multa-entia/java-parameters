package ru.multa.entia.parameters.impl.source;

import lombok.RequiredArgsConstructor;
import ru.multa.entia.parameters.api.reader.file.Reader;
import ru.multa.entia.parameters.api.source.PropertySource;
import ru.multa.entia.results.api.result.Result;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class DefaultTextualPropertySource implements PropertySource {
    private final Reader reader;

    private Map<String, Object> rawProperties = new HashMap<>();

    @Override
    public Result<Object> update() {
        // TODO: impl
        return null;
    }

    @Override
    public Result<Object> get(final String propertyName) {
        // TODO: impl
        return null;
    }
}
