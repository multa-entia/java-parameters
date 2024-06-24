package ru.multa.entia.parameters.impl.watchers;

import ru.multa.entia.parameters.api.ids.Id;
import ru.multa.entia.parameters.api.watchers.WatcherEvent;

public record DefaultWatcherEvent(Id watcherId,
                                  WatcherEventKind kind) implements WatcherEvent {

    public static DefaultWatcherEvent created(final Id watcherId) {
        return new DefaultWatcherEvent(watcherId, WatcherEventKind.CREATED);
    }

    public static DefaultWatcherEvent modified(final Id watcherId) {
        return new DefaultWatcherEvent(watcherId, WatcherEventKind.MODIFIED);
    }

    public static DefaultWatcherEvent deleted(final Id watcherId) {
        return new DefaultWatcherEvent(watcherId, WatcherEventKind.DELETED);
    }
}
