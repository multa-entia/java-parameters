package ru.multa.entia.parameters.impl.getter;

import ru.multa.entia.parameters.api.getter.Getter;
import ru.multa.entia.results.api.result.Result;

// TODO: ??? remake
public class DefaultGetter<T> implements Getter<String, Result<T>> {
    @Override
    public Result<T> get(String input) {
        return null;
    }
}
