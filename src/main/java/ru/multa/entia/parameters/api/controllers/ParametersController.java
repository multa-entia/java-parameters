package ru.multa.entia.parameters.api.controllers;

import ru.multa.entia.parameters.api.properties.Property;
import ru.multa.entia.parameters.api.sources.PropertySource;
import ru.multa.entia.parameters.api.watchers.Watcher;
import ru.multa.entia.parameters.api.watchers.WatcherListener;
import ru.multa.entia.results.api.result.Result;

public interface ParametersController extends WatcherListener {
    Result<Object> bind(PropertySource propertySource, Watcher watcher);
    Result<Object> bind(PropertySource propertySource, Property<?> property);
    Result<Object> start();
    Result<Object> stop();
}
