package ru.multa.entia.parameters.api.source;

import ru.multa.entia.results.api.result.Result;

// TODO: move into package extractor
public interface SourceExtractor<T> {
    void set(Object object);
    Result<T> get();
}
