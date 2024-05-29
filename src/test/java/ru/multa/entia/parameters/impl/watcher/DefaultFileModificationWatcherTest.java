package ru.multa.entia.parameters.impl.watcher;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.parameters.api.watcher.Watcher;
import ru.multa.entia.parameters.api.watcher.WatcherListener;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.utils.Results;

import java.lang.reflect.Field;
import java.nio.file.*;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultFileModificationWatcherTest {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    private static final Supplier<WatcherListener> WATCHER_LISTENER_SUPPLIER = () -> {
        return Mockito.mock(WatcherListener.class);
    };

    @ParameterizedTest
    @CsvSource(value = {
            ",",
            " ,",
            "C:,",
    },
    ignoreLeadingAndTrailingWhitespace = false)
    void shouldCheckCreation_ifBothNull(Path initPath) {
        Result<Watcher> result = DefaultFileModificationWatcher.create(initPath);

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultFileModificationWatcher.Code.INVALID_PATH))
                        .back()
                        .compare()
        ).isTrue();
    }

    @SneakyThrows
    @Test
    void shouldCheckCreation() {
        Path expectedDirectoryPath = Paths.get("projects\\multa-entia\\java-parameters");
        String expectedFileName = "text.txt";

        Path path = Paths.get(String.format("%s\\%s", expectedDirectoryPath, expectedFileName));
        Result<Watcher> result = DefaultFileModificationWatcher.create(path);

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();
        assertThat(result).isNotNull();

        Watcher watcher = result.value();

        Field field = watcher.getClass().getDeclaredField("directoryPath");
        field.setAccessible(true);
        Object gottenDirectoryPath = field.get(watcher);

        field = watcher.getClass().getDeclaredField("fileName");
        field.setAccessible(true);
        Object gottenFileName = field.get(watcher);

        assertThat(gottenDirectoryPath).isEqualTo(expectedDirectoryPath);
        assertThat(gottenFileName).isEqualTo(expectedFileName);
    }

    @SneakyThrows
    @Test
    void shouldCheckStarting() {
        Path path = getPathToTestFile();

        Watcher watcher = DefaultFileModificationWatcher.create(path).value();
        Result<Object> result = watcher.start();
        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();

        Class<? extends Watcher> type = watcher.getClass();
        Field field = type.getDeclaredField("executed");
        field.setAccessible(true);
        AtomicBoolean gottenExecuted = (AtomicBoolean) field.get(watcher);

        field = type.getDeclaredField("service");
        field.setAccessible(true);
        Object gottenService = field.get(watcher);

        assertThat(gottenExecuted.get()).isTrue();
        assertThat(gottenService).isNotNull();
    }

    @Test
    void shouldCheckStarting_ifAlreadyStarted() {
        Path path = getPathToTestFile();

        Watcher watcher = DefaultFileModificationWatcher.create(path).value();
        watcher.start();
        Result<Object> result = watcher.start();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultFileModificationWatcher.Code.ALREADY_STARTED))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckStopping_ifAlreadyStopped() {
        Path path = getPathToTestFile();

        Watcher watcher = DefaultFileModificationWatcher.create(path).value();
        Result<Object> result = watcher.stop();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultFileModificationWatcher.Code.ALREADY_STOPPED))
                        .back()
                        .compare()
        ).isTrue();
    }

    @SneakyThrows
    @Test
    void shouldCheckStopping() {
        Path path = getPathToTestFile();

        Watcher watcher = DefaultFileModificationWatcher.create(path).value();
        watcher.start();
        Result<Object> result = watcher.stop();

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .value(null)
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();

        Class<? extends Watcher> type = watcher.getClass();
        Field field = type.getDeclaredField("executed");
        field.setAccessible(true);
        AtomicBoolean gottenExecuted = (AtomicBoolean) field.get(watcher);

        field = type.getDeclaredField("service");
        field.setAccessible(true);
        Object gottenService = field.get(watcher);

        assertThat(gottenExecuted.get()).isFalse();
        assertThat(gottenService).isNull();
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    void shouldCheckListenerAddition() {
        int quantity = Faker.int_().between(10, 20);
        Path path = getPathToTestFile();
        Watcher watcher = DefaultFileModificationWatcher.create(path).value();

        for (int i = 0; i < quantity; i++) {
            Result<Object> result = watcher.addListener(WATCHER_LISTENER_SUPPLIER.get());

            assertThat(
                    Results.comparator(result)
                            .isSuccess()
                            .value(null)
                            .seedsComparator()
                            .isNull()
                            .back()
                            .compare()
            ).isTrue();
        }

        Field field = watcher.getClass().getDeclaredField("listeners");
        field.setAccessible(true);
        Set<WatcherListener> gottenListeners = (Set<WatcherListener>) field.get(watcher);

        assertThat(gottenListeners).hasSize(quantity);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    void shouldCheckListenerAddition_ifItIsAdded() {
        Path path = getPathToTestFile();
        Watcher watcher = DefaultFileModificationWatcher.create(path).value();

        WatcherListener listener = WATCHER_LISTENER_SUPPLIER.get();
        watcher.addListener(listener);
        Result<Object> result = watcher.addListener(listener);

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultFileModificationWatcher.Code.LISTENER_IS_ALREADY_ADDED))
                        .back()
                        .compare()
        ).isTrue();

        Field field = watcher.getClass().getDeclaredField("listeners");
        field.setAccessible(true);
        Set<WatcherListener> gottenListeners = (Set<WatcherListener>) field.get(watcher);

        assertThat(gottenListeners).hasSize(1);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    void shouldCheckListenerRemoving() {
        Path path = getPathToTestFile();
        Watcher watcher = DefaultFileModificationWatcher.create(path).value();

        WatcherListener listener = WATCHER_LISTENER_SUPPLIER.get();
        watcher.addListener(listener);
        Result<Object> result = watcher.removeListener(listener);

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .value(null)
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();

        Field field = watcher.getClass().getDeclaredField("listeners");
        field.setAccessible(true);
        Set<WatcherListener> gottenListeners = (Set<WatcherListener>) field.get(watcher);

        assertThat(gottenListeners).hasSize(0);
    }

    @Test
    void shouldCheckListenerRemoving_ifItIsAbsence() {
        Path path = getPathToTestFile();
        Watcher watcher = DefaultFileModificationWatcher.create(path).value();

        WatcherListener listener = WATCHER_LISTENER_SUPPLIER.get();
        Result<Object> result = watcher.removeListener(listener);

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultFileModificationWatcher.Code.LISTENER_IS_ALREADY_REMOVED))
                        .back()
                        .compare()
        ).isTrue();
    }

    private Path getPathToTestFile() {
        String path = System.getProperty("user.dir")
                + "\\src\\test\\resource\\"
                + getClass().getPackageName().replace('.', '\\')
                + "\\" + getClass().getSimpleName() + ".txt";
        return Path.of(path);
    }

    // TODO: del
//        String absPath = System.getProperty("user.dir");
////        System.out.println(absPath);
//
////        ""
//
//        Path path = Paths.get("");
//        Path path1 = Paths.get("");
//        Path path2 = Paths.get("");
//        Path path3 = Paths.get("C:\\Users\\KasymbekovPN\\projects\\multa-entia\\java-parameters\\text.txt");
//
////        System.out.println(path);
////        System.out.println(path1);
////        System.out.println(path2);
//
//        System.out.println("path: " + path);
//        System.out.println("parent: " + path.getParent());
//        System.out.println("name: " + path.getFileName());
//        System.out.println("path: " + path3);
//        System.out.println("parent: " + path3.getParent());
//        System.out.println("name: " + path3.getFileName());

//        String absPath = System.getProperty("user.dir");
//        absPath += "\\src\\test\\resources\\";
//        absPath += getClass().getPackageName().replace('.', '\\');
//        absPath += "\\" + getClass().getSimpleName() + ".txt";
//        System.out.println(absPath);
//
//        String string = Files.readString(Path.of(absPath));
//        System.out.println(string);

//    /*
//
//        // TODO: del
//    @Test
//    void test1() throws IOException, InterruptedException {
////        WatchService watchService = FileSystems.getDefault().newWatchService();
//
//        /*
//
//        Path path = Paths.get("pathToDir");
//
//        WatchKey watchKey = path.register(
//  watchService, StandardWatchEventKinds...);
//
//  WatchKey watchKey = watchService.poll();
//
//  WatchKey watchKey = watchService.poll(long timeout, TimeUnit units);
//
//  WatchKey watchKey = watchService.take();
//
//  watchKey.reset();
//
//        WatchKey key;
//        while ((key = watchService.take()) != null) {
//            for (WatchEvent<?> event : key.pollEvents()) {
//                System.out.println(
//                  "Event kind:" + event.kind()
//                    + ". File affected: " + event.context() + ".");
//            }
//            key.reset();
//        }
//
//         */
//
//    AtomicReference<WatchService> serviceHolder = new AtomicReference<>(FileSystems.getDefault().newWatchService());
//    Path path = Path.of("C:\\Users\\KasymbekovPN\\projects\\multa-entia\\java-parameters\\src\\test\\resources\\ru\\multa\\entia\\parameters\\impl\\reader\\");
//        path.register(serviceHolder.get(), StandardWatchEventKinds.ENTRY_MODIFY);
//
//    Runnable r = () -> {
//        WatchKey key;
//        try {
//            while ((key = serviceHolder.get().take()) != null) {
//                for (WatchEvent<?> pollEvent : key.pollEvents()) {
//                    System.out.println("EKIND: " + pollEvent.kind() + ". File affected: " + pollEvent.context());
//                }
//                key.reset();
//            }
//        } catch (InterruptedException ignored) {}
//    };
//    Thread t = new Thread(r);
//        t.start();
//
//        Thread.sleep(3_000);
//}
}