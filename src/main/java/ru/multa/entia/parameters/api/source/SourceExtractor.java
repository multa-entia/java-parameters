package ru.multa.entia.parameters.api.source;

import ru.multa.entia.results.api.result.Result;

public interface SourceExtractor<T> {
    void set(Object object);
    Result<T> get();
}
