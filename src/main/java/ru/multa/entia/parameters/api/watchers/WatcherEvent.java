package ru.multa.entia.parameters.api.watchers;

import ru.multa.entia.parameters.api.ids.Id;
import ru.multa.entia.parameters.impl.watchers.WatcherEventKind;

public interface WatcherEvent {
    Id watcherId();
    WatcherEventKind kind();
}
