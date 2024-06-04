package ru.multa.entia.parameters.api.watcher;

import ru.multa.entia.parameters.api.ids.IdGetter;
import ru.multa.entia.results.api.result.Result;

public interface Watcher extends IdGetter {
    Result<Object> start();
    Result<Object> stop();
    Result<Object> addListener(WatcherListener listener);
    Result<Object> removeListener(WatcherListener listener);
}
