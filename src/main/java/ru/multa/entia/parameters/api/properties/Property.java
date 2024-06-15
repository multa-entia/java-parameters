package ru.multa.entia.parameters.api.properties;

import ru.multa.entia.results.api.result.Result;

public interface Property<T> {
    Result<T> set(Object object);
    Result<T> get();
    String getName();
}
