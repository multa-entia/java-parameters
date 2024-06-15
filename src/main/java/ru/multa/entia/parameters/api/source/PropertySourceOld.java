package ru.multa.entia.parameters.api.source;

import ru.multa.entia.results.api.result.Result;

public interface PropertySourceOld {
    Result<Object> update();
    Result<Object> get(String propertyName);
}
