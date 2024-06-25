package ru.multa.entia.parameters.api.watchers;

import ru.multa.entia.results.api.result.Result;

public interface WatcherListener {
    Result<Object> notifyListener(WatcherEvent watcherEvent);
}
