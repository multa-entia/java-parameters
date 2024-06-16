package ru.multa.entia.parameters.impl.watcher;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultFileWatcherResultTest {
    private static final Path PATH = Path.of("/opt");

    @Test
    void shouldCheckKindGetting() {
        DefaultFileWatcherResult.Kind expectedKind = DefaultFileWatcherResult.Kind.CREATED;
        DefaultFileWatcherResult.Kind gottenKind
                = new DefaultFileWatcherResult(expectedKind, PATH).kind();

        assertThat(gottenKind).isEqualTo(expectedKind);
    }

    @Test
    void shouldCheckPathGetting() {
        Path gottenPath
                = new DefaultFileWatcherResult(DefaultFileWatcherResult.Kind.CREATED, PATH).path();

        assertThat(gottenPath).isEqualTo(PATH);
    }

    @Test
    void shouldCheckAsCreatedCreation() {
        DefaultFileWatcherResult.Kind gottenKind = DefaultFileWatcherResult.created(PATH).kind();

        assertThat(gottenKind).isEqualTo(DefaultFileWatcherResult.Kind.CREATED);
    }

    @Test
    void shouldCheckAsModifiedCreation() {
        DefaultFileWatcherResult.Kind gottenKind = DefaultFileWatcherResult.modified(PATH).kind();

        assertThat(gottenKind).isEqualTo(DefaultFileWatcherResult.Kind.MODIFIED);
    }

    @Test
    void shouldCheckAsDeletedCreation() {
        DefaultFileWatcherResult.Kind gottenKind = DefaultFileWatcherResult.deleted(PATH).kind();

        assertThat(gottenKind).isEqualTo(DefaultFileWatcherResult.Kind.DELETED);
    }
}