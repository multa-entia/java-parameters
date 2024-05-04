package ru.multa.entia.parameters.impl.source;

import lombok.AllArgsConstructor;
import ru.multa.entia.parameters.api.reader.Reader;
import ru.multa.entia.parameters.api.source.Source;
import ru.multa.entia.parameters.api.source.SourceExtractor;
import ru.multa.entia.results.api.result.Result;

@AllArgsConstructor
public class DefaultLiveYamlSource implements Source {
    private final Reader reader;

    @Override
    public Result<Object> get(SourceExtractor<?> extractor) {
        // TODO: impl
        return null;
    }
}
