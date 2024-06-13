
// TODO: restore
//package ru.multa.entia.parameters.impl.watcher;
//
//import lombok.SneakyThrows;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvSource;
//import org.mockito.Mockito;
//import org.mockito.invocation.InvocationOnMock;
//import org.mockito.stubbing.Answer;
//import ru.multa.entia.fakers.impl.Faker;
//import ru.multa.entia.parameters.api.ids.Id;
//import ru.multa.entia.parameters.api.watcher.Watcher;
//import ru.multa.entia.parameters.api.watcher.WatcherListener;
//import ru.multa.entia.results.api.repository.CodeRepository;
//import ru.multa.entia.results.api.result.Result;
//import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
//import ru.multa.entia.results.utils.Results;
//
//import java.io.IOException;
//import java.lang.reflect.Field;
//import java.nio.file.*;
//import java.util.Set;
//import java.util.UUID;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.function.Supplier;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class DefaultFileModificationWatcherTest {
//    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
//
//    private static final Supplier<WatcherListener> WATCHER_LISTENER_SUPPLIER = () -> {
//        return Mockito.mock(WatcherListener.class);
//    };
//
//    @Test
//    void shouldCheckIdGetting() {
//        Ids type = Ids.FILE;
//        Path path = Paths.get("projects\\multa-entia\\java-parameters");
//        UUID expected = new UUID(type.getValue(), path.hashCode());
//
//        Id gotten = DefaultFileModificationWatcher.create(path).value().getId();
//
//        assertThat(gotten.get()).isEqualTo(expected);
//    }
//
//    @ParameterizedTest
//    @CsvSource(value = {
//            ",",
//            " ,",
//            "C:,",
//    },
//    ignoreLeadingAndTrailingWhitespace = false)
//    void shouldCheckCreation_ifBothNull(Path initPath) {
//        Result<Watcher> result = DefaultFileModificationWatcher.create(initPath);
//
//        assertThat(
//                Results.comparator(result)
//                        .isFail()
//                        .value(null)
//                        .seedsComparator()
//                        .code(CR.get(DefaultFileModificationWatcher.Code.INVALID_PATH))
//                        .back()
//                        .compare()
//        ).isTrue();
//    }
//
//    @SneakyThrows
//    @Test
//    void shouldCheckCreation() {
//        Path expectedDirectoryPath = Paths.get("projects\\multa-entia\\java-parameters");
//        String expectedFileName = "text.txt";
//
//        Path path = Paths.get(String.format("%s\\%s", expectedDirectoryPath, expectedFileName));
//        Result<Watcher> result = DefaultFileModificationWatcher.create(path);
//
//        assertThat(
//                Results.comparator(result)
//                        .isSuccess()
//                        .seedsComparator()
//                        .isNull()
//                        .back()
//                        .compare()
//        ).isTrue();
//        assertThat(result).isNotNull();
//
//        Watcher watcher = result.value();
//
//        Field field = watcher.getClass().getDeclaredField("directoryPath");
//        field.setAccessible(true);
//        Object gottenDirectoryPath = field.get(watcher);
//
//        field = watcher.getClass().getDeclaredField("fileName");
//        field.setAccessible(true);
//        Object gottenFileName = field.get(watcher);
//
//        assertThat(gottenDirectoryPath).isEqualTo(expectedDirectoryPath);
//        assertThat(gottenFileName).isEqualTo(expectedFileName);
//    }
//
//    @SneakyThrows
//    @Test
//    void shouldCheckStarting() {
//        Path path = getPathToTestFile();
//
//        Watcher watcher = DefaultFileModificationWatcher.create(path).value();
//        Result<Object> result = watcher.start();
//        assertThat(
//                Results.comparator(result)
//                        .isSuccess()
//                        .seedsComparator()
//                        .isNull()
//                        .back()
//                        .compare()
//        ).isTrue();
//
//        Class<? extends Watcher> type = watcher.getClass();
//        Field field = type.getDeclaredField("executed");
//        field.setAccessible(true);
//        AtomicBoolean gottenExecuted = (AtomicBoolean) field.get(watcher);
//
//        field = type.getDeclaredField("service");
//        field.setAccessible(true);
//        Object gottenService = field.get(watcher);
//
//        assertThat(gottenExecuted.get()).isTrue();
//        assertThat(gottenService).isNotNull();
//    }
//
//    @Test
//    void shouldCheckStarting_ifAlreadyStarted() {
//        Path path = getPathToTestFile();
//
//        Watcher watcher = DefaultFileModificationWatcher.create(path).value();
//        watcher.start();
//        Result<Object> result = watcher.start();
//
//        assertThat(
//                Results.comparator(result)
//                        .isFail()
//                        .value(null)
//                        .seedsComparator()
//                        .code(CR.get(DefaultFileModificationWatcher.Code.ALREADY_STARTED))
//                        .back()
//                        .compare()
//        ).isTrue();
//    }
//
//    @Test
//    void shouldCheckStopping_ifAlreadyStopped() {
//        Path path = getPathToTestFile();
//
//        Watcher watcher = DefaultFileModificationWatcher.create(path).value();
//        Result<Object> result = watcher.stop();
//
//        assertThat(
//                Results.comparator(result)
//                        .isFail()
//                        .value(null)
//                        .seedsComparator()
//                        .code(CR.get(DefaultFileModificationWatcher.Code.ALREADY_STOPPED))
//                        .back()
//                        .compare()
//        ).isTrue();
//    }
//
//    @SneakyThrows
//    @Test
//    void shouldCheckStopping() {
//        Path path = getPathToTestFile();
//
//        Watcher watcher = DefaultFileModificationWatcher.create(path).value();
//        watcher.start();
//        Result<Object> result = watcher.stop();
//
//        assertThat(
//                Results.comparator(result)
//                        .isSuccess()
//                        .value(null)
//                        .seedsComparator()
//                        .isNull()
//                        .back()
//                        .compare()
//        ).isTrue();
//
//        Class<? extends Watcher> type = watcher.getClass();
//        Field field = type.getDeclaredField("executed");
//        field.setAccessible(true);
//        AtomicBoolean gottenExecuted = (AtomicBoolean) field.get(watcher);
//
//        field = type.getDeclaredField("service");
//        field.setAccessible(true);
//        Object gottenService = field.get(watcher);
//
//        assertThat(gottenExecuted.get()).isFalse();
//        assertThat(gottenService).isNull();
//    }
//
//    @SuppressWarnings("unchecked")
//    @SneakyThrows
//    @Test
//    void shouldCheckListenerAddition() {
//        int quantity = Faker.int_().between(10, 20);
//        Path path = getPathToTestFile();
//        Watcher watcher = DefaultFileModificationWatcher.create(path).value();
//
//        for (int i = 0; i < quantity; i++) {
//            Result<Object> result = watcher.addListener(WATCHER_LISTENER_SUPPLIER.get());
//
//            assertThat(
//                    Results.comparator(result)
//                            .isSuccess()
//                            .value(null)
//                            .seedsComparator()
//                            .isNull()
//                            .back()
//                            .compare()
//            ).isTrue();
//        }
//
//        Field field = watcher.getClass().getDeclaredField("listeners");
//        field.setAccessible(true);
//        Set<WatcherListener> gottenListeners = (Set<WatcherListener>) field.get(watcher);
//
//        assertThat(gottenListeners).hasSize(quantity);
//    }
//
//    @SuppressWarnings("unchecked")
//    @SneakyThrows
//    @Test
//    void shouldCheckListenerAddition_ifItIsAdded() {
//        Path path = getPathToTestFile();
//        Watcher watcher = DefaultFileModificationWatcher.create(path).value();
//
//        WatcherListener listener = WATCHER_LISTENER_SUPPLIER.get();
//        watcher.addListener(listener);
//        Result<Object> result = watcher.addListener(listener);
//
//        assertThat(
//                Results.comparator(result)
//                        .isFail()
//                        .value(null)
//                        .seedsComparator()
//                        .code(CR.get(DefaultFileModificationWatcher.Code.LISTENER_IS_ALREADY_ADDED))
//                        .back()
//                        .compare()
//        ).isTrue();
//
//        Field field = watcher.getClass().getDeclaredField("listeners");
//        field.setAccessible(true);
//        Set<WatcherListener> gottenListeners = (Set<WatcherListener>) field.get(watcher);
//
//        assertThat(gottenListeners).hasSize(1);
//    }
//
//    @SuppressWarnings("unchecked")
//    @SneakyThrows
//    @Test
//    void shouldCheckListenerRemoving() {
//        Path path = getPathToTestFile();
//        Watcher watcher = DefaultFileModificationWatcher.create(path).value();
//
//        WatcherListener listener = WATCHER_LISTENER_SUPPLIER.get();
//        watcher.addListener(listener);
//        Result<Object> result = watcher.removeListener(listener);
//
//        assertThat(
//                Results.comparator(result)
//                        .isSuccess()
//                        .value(null)
//                        .seedsComparator()
//                        .isNull()
//                        .back()
//                        .compare()
//        ).isTrue();
//
//        Field field = watcher.getClass().getDeclaredField("listeners");
//        field.setAccessible(true);
//        Set<WatcherListener> gottenListeners = (Set<WatcherListener>) field.get(watcher);
//
//        assertThat(gottenListeners).hasSize(0);
//    }
//
//    @Test
//    void shouldCheckListenerRemoving_ifItIsAbsence() {
//        Path path = getPathToTestFile();
//        Watcher watcher = DefaultFileModificationWatcher.create(path).value();
//
//        WatcherListener listener = WATCHER_LISTENER_SUPPLIER.get();
//        Result<Object> result = watcher.removeListener(listener);
//
//        assertThat(
//                Results.comparator(result)
//                        .isFail()
//                        .value(null)
//                        .seedsComparator()
//                        .code(CR.get(DefaultFileModificationWatcher.Code.LISTENER_IS_ALREADY_REMOVED))
//                        .back()
//                        .compare()
//        ).isTrue();
//    }
//
//    @SneakyThrows
//    @Test
//    void shouldCheckExecution() {
//        Path path = getPathToTestFile();
//        AtomicReference<Object> holder = new AtomicReference<>();
//
//        Supplier<WatcherListener> watcherListenerSupplier = () -> {
//            WatcherListener listener = Mockito.mock(WatcherListener.class);
//            Mockito
//                    .doAnswer(new Answer<Void>() {
//                        @Override
//                        public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
//                            holder.set(invocationOnMock.getArgument(0));
//                            return null;
//                        }
//                    })
//                    .when(listener)
//                    .notifyListener(Mockito.any());
//
//            return listener;
//        };
//
//        Runnable fileModificationRunnable = () -> {
//            try {
//                Files.writeString(path, String.format("int_value: %s", Faker.int_().between(0, 1000)));
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        };
//
//        Watcher watcher = DefaultFileModificationWatcher.create(path).value();
//        watcher.addListener(watcherListenerSupplier.get());
//
//        watcher.start();
//        new Thread(fileModificationRunnable).start();
//
//        Thread.sleep(100);
//        watcher.stop();
//
//        assertThat(holder.get()).isNotNull();
//        assertThat(holder.get().getClass()).isEqualTo(DefaultFileWatcherResult.class);
//        DefaultFileWatcherResult result = (DefaultFileWatcherResult) holder.get();
//        assertThat(result.kind()).isEqualTo(DefaultFileWatcherResult.Kind.MODIFIED);
//        assertThat(result.path()).isEqualTo(path);
//    }
//
//    private Path getPathToTestFile() {
//        String path = System.getProperty("user.dir")
//                + "\\src\\test\\resources\\"
//                + getClass().getPackageName().replace('.', '\\')
//                + "\\" + getClass().getSimpleName() + ".txt";
//        return Path.of(path);
//    }
//}