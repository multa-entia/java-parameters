package ru.multa.entia.parameters.api.source;

import ru.multa.entia.parameters.api.extractor.Extractor;
import ru.multa.entia.results.api.result.Result;

public interface Source {
    Result<Object> get(Extractor<?> extractor);
}
