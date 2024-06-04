package ru.multa.entia.parameters.api.reader.file;

import ru.multa.entia.parameters.api.ids.IdGetter;
import ru.multa.entia.results.api.result.Result;

public interface ReaderOld extends IdGetter {
    Result<ReadResultOld> read();
}
