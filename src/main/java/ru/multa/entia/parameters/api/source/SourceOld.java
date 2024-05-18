package ru.multa.entia.parameters.api.source;

import ru.multa.entia.parameters.api.extractor.ExtractorOld;
import ru.multa.entia.results.api.result.Result;

public interface SourceOld {
    Result<Object> get(ExtractorOld<?> extractorOld);
}
