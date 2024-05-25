package ru.multa.entia.parameters.api.source;

import ru.multa.entia.results.api.result.Result;

public interface SourceAdapter<I, O> {
    Result<O> adapt(I input);
}
