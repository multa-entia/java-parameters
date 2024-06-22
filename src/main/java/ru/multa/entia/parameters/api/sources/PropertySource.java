package ru.multa.entia.parameters.api.sources;

import ru.multa.entia.parameters.api.properties.Property;
import ru.multa.entia.results.api.result.Result;

public interface PropertySource {
    Result<Property<?>> register(Property<?> property);
    Result<Property<?>> unregister(Property<?> property);
    Result<Object> update();
}
