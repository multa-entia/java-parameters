package ru.multa.entia.parameters.api.source;

import ru.multa.entia.results.api.result.Result;

public interface Source {
    Result<Object> getRaw();
    Result<Object> get(SourceExtractor<?> extractor);
}
