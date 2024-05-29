package ru.multa.entia.parameters.api.watcher;

import ru.multa.entia.results.api.result.Result;

public interface Watcher {
    Result<Object> start();
    Result<Object> stop();

    // TODO: add addListener
    // TODO: add removeListener
}
