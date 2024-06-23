package ru.multa.entia.parameters.impl.watchers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.parameters.api.ids.Id;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultFileWatcherEventTest {

    private static final Id EXPECTED_ID = Mockito.mock(Id.class);

    @Test
    void shouldCheckIdGetting() {
        Id gottenId = new DefaultFileWatcherEvent(EXPECTED_ID, null).watcherId();

        assertThat(gottenId).isEqualTo(EXPECTED_ID);
    }

    @Test
    void shouldCheckKindGetting() {
        DefaultFileWatcherEvent.Kind expectedKind = DefaultFileWatcherEvent.Kind.CREATED;
        DefaultFileWatcherEvent.Kind gottenKind = new DefaultFileWatcherEvent(null, expectedKind).kind();

        assertThat(gottenKind).isEqualTo(expectedKind);
    }

    @Test
    void shouldCheckCreation_ofCreated() {
        DefaultFileWatcherEvent event = DefaultFileWatcherEvent.created(EXPECTED_ID);

        assertThat(event.watcherId()).isEqualTo(EXPECTED_ID);
        assertThat(event.kind()).isEqualTo(DefaultFileWatcherEvent.Kind.CREATED);
    }

    @Test
    void shouldCheckCreation_ofModified() {
        DefaultFileWatcherEvent event = DefaultFileWatcherEvent.modified(EXPECTED_ID);

        assertThat(event.watcherId()).isEqualTo(EXPECTED_ID);
        assertThat(event.kind()).isEqualTo(DefaultFileWatcherEvent.Kind.MODIFIED);
    }

    @Test
    void shouldCheckCreation_ofDeleted() {
        DefaultFileWatcherEvent event = DefaultFileWatcherEvent.deleted(EXPECTED_ID);

        assertThat(event.watcherId()).isEqualTo(EXPECTED_ID);
        assertThat(event.kind()).isEqualTo(DefaultFileWatcherEvent.Kind.DELETED);
    }
}