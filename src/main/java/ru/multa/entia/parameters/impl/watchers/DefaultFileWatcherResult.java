package ru.multa.entia.parameters.impl.watchers;

import ru.multa.entia.parameters.api.watchers.WatcherResult;

import java.nio.file.Path;

record DefaultFileWatcherResult(ru.multa.entia.parameters.impl.watchers.DefaultFileWatcherResult.Kind kind,
                                       Path path) implements WatcherResult {
    public enum Kind {
        UNKNOWN,
        CREATED,
        MODIFIED,
        DELETED
    }

    public static DefaultFileWatcherResult created(final Path path) {
        return new DefaultFileWatcherResult(Kind.CREATED, path);
    }

    public static DefaultFileWatcherResult modified(final Path path) {
        return new DefaultFileWatcherResult(Kind.MODIFIED, path);
    }

    public static DefaultFileWatcherResult deleted(final Path path) {
        return new DefaultFileWatcherResult(Kind.DELETED, path);
    }
}
