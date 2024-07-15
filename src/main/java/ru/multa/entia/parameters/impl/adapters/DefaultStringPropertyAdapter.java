package ru.multa.entia.parameters.impl.adapters;

import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.function.Function;

public class DefaultStringPropertyAdapter implements Function<Object, Result<String>> {
    @Override
    public Result<String> apply(final Object object) {
        return object == null
                ? DefaultResultBuilder.<String>ok()
                : DefaultResultBuilder.<String>ok(String.valueOf(object));
    }
}
