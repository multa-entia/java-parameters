package ru.multa.entia.parameters.api.readers;

import ru.multa.entia.parameters.api.ids.IdGetter;
import ru.multa.entia.results.api.result.Result;

public interface Reader<R> extends IdGetter {
    Result<R> read();
}
