package ru.multa.entia.parameters.api.source;

import ru.multa.entia.results.api.result.Result;

public interface PropertySource {
    Result<Object> update();
    Result<Object> get(String propertyName);
}
