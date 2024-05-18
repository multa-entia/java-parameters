package ru.multa.entia.parameters.api.reader.file;

import ru.multa.entia.results.api.result.Result;

public interface Reader {
    Result<ReadResult> read();
}
