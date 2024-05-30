package ru.multa.entia.parameters.impl.watcher;

import ru.multa.entia.parameters.api.watcher.WatcherResult;

import java.nio.file.Path;

record DefaultFileWatcherResult(ru.multa.entia.parameters.impl.watcher.DefaultFileWatcherResult.Kind kind,
                                       Path path) implements WatcherResult {
    public enum Kind {
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
