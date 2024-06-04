package ru.multa.entia.parameters.impl.watcher;

import lombok.Getter;
import ru.multa.entia.parameters.api.ids.Id;
import ru.multa.entia.parameters.api.watcher.Watcher;
import ru.multa.entia.parameters.api.watcher.WatcherListener;
import ru.multa.entia.parameters.impl.ids.DefaultId;
import ru.multa.entia.parameters.impl.ids.Ids;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class DefaultFileModificationWatcher implements Watcher {
    public enum Code {
        INVALID_PATH,
        ALREADY_STARTED,
        ALREADY_STOPPED,
        LISTENER_IS_ALREADY_ADDED,
        LISTENER_IS_ALREADY_REMOVED
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.INVALID_PATH, "parameters:watcher.file-modification.default:invalid-path");
        CR.update(Code.ALREADY_STARTED, "parameters:watcher.file-modification.default:already-started");
        CR.update(Code.ALREADY_STOPPED, "parameters:watcher.file-modification.default:already-stopped");
        CR.update(Code.LISTENER_IS_ALREADY_ADDED, "parameters:watcher.file-modification.default:listener-is-already-added");
        CR.update(Code.LISTENER_IS_ALREADY_REMOVED, "parameters:watcher.file-modification.default:listener-is-already-removed");
    }

    private final AtomicBoolean executed = new AtomicBoolean(false);
    private final Set<WatcherListener> listeners = ConcurrentHashMap.newKeySet();
    private final Path directoryPath;
    private final String fileName;
    @Getter
    private final Id id;
    private final Supplier<ExecutorService> serviceSupplier;

    private ExecutorService service;

    public static Result<Watcher> create(final Path path) {
        if (path == null) {
            return DefaultResultBuilder.<Watcher>fail(CR.get(Code.INVALID_PATH));
        }

        Path parent = path.getParent();
        Path fileName = path.getFileName();
        if (fileName == null || fileName.toString().isBlank() || parent == null || parent.toString().isBlank()) {
            return DefaultResultBuilder.<Watcher>fail(CR.get(Code.INVALID_PATH));
        }

        return DefaultResultBuilder.<Watcher>ok(
                new DefaultFileModificationWatcher(
                        parent,
                        fileName.toString(),
                        path,
                        () -> {return Executors.newSingleThreadExecutor(new WatcherThreadFactory(path));}));
    }

    private DefaultFileModificationWatcher(final Path directoryPath,
                                           final String fileName,
                                           final Path path,
                                           final Supplier<ExecutorService> serviceSupplier) {
        this.directoryPath = directoryPath;
        this.fileName = fileName;
        this.serviceSupplier = serviceSupplier;
        this.id = new DefaultId(Ids.FILE, path.hashCode());
    }

    @Override
    public Result<Object> start() {
        if (executed.compareAndSet(false, true)) {
            service = serviceSupplier.get();
            service.submit(this::execute);
            return DefaultResultBuilder.<Object>ok();
        }

        return DefaultResultBuilder.<Object>fail(CR.get(Code.ALREADY_STARTED));
    }

    @Override
    public Result<Object> stop() {
        if (executed.compareAndSet(true, false)) {
            service.shutdown();
            service = null;
            return DefaultResultBuilder.<Object>ok();
        }

        return DefaultResultBuilder.<Object>fail(CR.get(Code.ALREADY_STOPPED));
    }

    @Override
    public Result<Object> addListener(final WatcherListener listener) {
        return listeners.add(listener)
                ? DefaultResultBuilder.<Object>ok()
                : DefaultResultBuilder.<Object>fail(CR.get(Code.LISTENER_IS_ALREADY_ADDED));
    }

    @Override
    public Result<Object> removeListener(final WatcherListener listener) {
        return listeners.remove(listener)
                ? DefaultResultBuilder.<Object>ok()
                : DefaultResultBuilder.<Object>fail(CR.get(Code.LISTENER_IS_ALREADY_REMOVED));
    }

    private void execute() {
        try(WatchService watchService = FileSystems.getDefault().newWatchService()) {
            Path path = Path.of(directoryPath.toUri());
            path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            WatchKey key;
            while (executed.get() && (key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                        for (WatcherListener listener : listeners) {
                            listener.notifyListener(
                                    DefaultFileWatcherResult.modified(Path.of(directoryPath.toString(), fileName))
                            );
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            executed.set(false);
        }
    }

    private static class WatcherThreadFactory implements ThreadFactory {
        private static int counter = 0;

        private final String name;

        public WatcherThreadFactory(final Path path) {
            this.name = String.format("%s_%s", path, counter++);
        }

        @Override
        public Thread newThread(final Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setName(name);

            return thread;
        }
    }
}
