package ru.multa.entia.parameters.impl.reader;

import ru.multa.entia.parameters.api.reader.ReaderOld;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// TODO: del
public class DefaultLiveFileReaderOld implements ReaderOld {
    public enum Code {
        CANNOT_READ
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.CANNOT_READ, "parameters:reader.file.live.default:cannot-read");
    }

    private final Path path;

    public DefaultLiveFileReaderOld(final String path) {
        this(Path.of(path));
    }

    public DefaultLiveFileReaderOld(final Path path) {
        this.path = path;
    }

    @Override
    public Result<String> read() {
        try {
            return DefaultResultBuilder.<String>ok(Files.readString(path));
        } catch (IOException e) {
            return DefaultResultBuilder.<String>fail(CR.get(Code.CANNOT_READ), e.getMessage());
        }
    }
}
