package ru.multa.entia.parameters.impl.watchers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.parameters.api.ids.Id;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultWatcherEventTest {

    private static final Id EXPECTED_ID = Mockito.mock(Id.class);

    @Test
    void shouldCheckIdGetting() {
        Id gottenId = new DefaultWatcherEvent(EXPECTED_ID, null).watcherId();

        assertThat(gottenId).isEqualTo(EXPECTED_ID);
    }

    @Test
    void shouldCheckKindGetting() {
        WatcherEventKind expectedKind = WatcherEventKind.CREATED;
        WatcherEventKind gottenKind = new DefaultWatcherEvent(null, expectedKind).kind();

        assertThat(gottenKind).isEqualTo(expectedKind);
    }

    @Test
    void shouldCheckCreation_ofCreated() {
        DefaultWatcherEvent event = DefaultWatcherEvent.created(EXPECTED_ID);

        assertThat(event.watcherId()).isEqualTo(EXPECTED_ID);
        assertThat(event.kind()).isEqualTo(WatcherEventKind.CREATED);
    }

    @Test
    void shouldCheckCreation_ofModified() {
        DefaultWatcherEvent event = DefaultWatcherEvent.modified(EXPECTED_ID);

        assertThat(event.watcherId()).isEqualTo(EXPECTED_ID);
        assertThat(event.kind()).isEqualTo(WatcherEventKind.MODIFIED);
    }

    @Test
    void shouldCheckCreation_ofDeleted() {
        DefaultWatcherEvent event = DefaultWatcherEvent.deleted(EXPECTED_ID);

        assertThat(event.watcherId()).isEqualTo(EXPECTED_ID);
        assertThat(event.kind()).isEqualTo(WatcherEventKind.DELETED);
    }
}