package ru.multa.entia.parameters.api.reader;

import ru.multa.entia.results.api.result.Result;

public interface Reader {
    Result<String> read();
}
