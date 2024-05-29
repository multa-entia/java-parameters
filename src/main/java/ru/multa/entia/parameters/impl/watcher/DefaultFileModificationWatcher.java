package ru.multa.entia.parameters.impl.watcher;

import ru.multa.entia.parameters.api.watcher.Watcher;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class DefaultFileModificationWatcher implements Watcher {
    public enum Code {
        INVALID_PATH,
        ALREADY_STARTED,
        ALREADY_STOPPED
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.INVALID_PATH, "parameters:watcher.file-modification.default:invalid-path");
        CR.update(Code.ALREADY_STARTED, "parameters:watcher.file-modification.default:already-started");
        CR.update(Code.ALREADY_STOPPED, "parameters:watcher.file-modification.default:already-stopped");
    }

    private final AtomicBoolean executed = new AtomicBoolean(false);
    private final Path directoryPath;
    private final String fileName;
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
                        () -> {return Executors.newSingleThreadExecutor(new WatcherThreadFactory(path));}));
    }

    private DefaultFileModificationWatcher(final Path directoryPath,
                                           final String fileName,
                                           final Supplier<ExecutorService> serviceSupplier) {
        this.directoryPath = directoryPath;
        this.fileName = fileName;
        this.serviceSupplier = serviceSupplier;
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

    private void execute() {

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
