package ru.multa.entia.parameters.api.extractor;

import ru.multa.entia.results.api.result.Result;

public interface ExtractorOld<T> {
    void set(Object object);
    Result<T> get();
    String getProperty();
}
