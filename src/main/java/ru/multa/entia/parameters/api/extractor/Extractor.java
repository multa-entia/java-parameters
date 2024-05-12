package ru.multa.entia.parameters.api.extractor;

import ru.multa.entia.results.api.result.Result;

public interface Extractor<T> {
    void set(Object object);
    Result<T> get();
}
