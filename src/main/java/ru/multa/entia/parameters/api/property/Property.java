package ru.multa.entia.parameters.api.property;

import ru.multa.entia.results.api.result.Result;

public interface Property<T> {
    void set(Object raw);
    Result<T> get();
    String getName();
}
