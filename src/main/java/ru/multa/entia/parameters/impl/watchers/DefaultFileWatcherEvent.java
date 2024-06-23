package ru.multa.entia.parameters.impl.watchers;

import ru.multa.entia.parameters.api.ids.Id;
import ru.multa.entia.parameters.api.watchers.WatcherEvent;

public record DefaultFileWatcherEvent(Id watcherId,
                                      Kind kind) implements WatcherEvent {
    public enum Kind {
        UNKNOWN,
        CREATED,
        MODIFIED,
        DELETED
    }

    public static DefaultFileWatcherEvent created(final Id watcherId) {
        return new DefaultFileWatcherEvent(watcherId, Kind.CREATED);
    }

    public static DefaultFileWatcherEvent modified(final Id watcherId) {
        return new DefaultFileWatcherEvent(watcherId, Kind.MODIFIED);
    }

    public static DefaultFileWatcherEvent deleted(final Id watcherId) {
        return new DefaultFileWatcherEvent(watcherId, Kind.DELETED);
    }
}
