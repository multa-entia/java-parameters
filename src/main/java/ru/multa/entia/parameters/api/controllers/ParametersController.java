package ru.multa.entia.parameters.api.controllers;

import ru.multa.entia.parameters.api.watchers.WatcherListener;
import ru.multa.entia.results.api.result.Result;

public interface ParametersController extends WatcherListener {
    Result<Object> start();
    Result<Object> stop();
}
