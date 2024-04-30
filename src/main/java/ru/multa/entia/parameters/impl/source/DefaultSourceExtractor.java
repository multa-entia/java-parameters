package ru.multa.entia.parameters.impl.source;

import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

public class DefaultSourceExtractor extends AbstractSourceExtractor<Object> {
    @Override
    public Result<Object> get() {
        return DefaultResultBuilder.<Object>ok(raw);
    }
}
