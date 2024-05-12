package ru.multa.entia.parameters.impl.reader;

import ru.multa.entia.parameters.api.reader.Reader;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DefaultOneReadFileReader implements Reader {
    public enum Code {
        CANNOT_READ
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.CANNOT_READ, "parameters:reader.file.one-read.default:cannot-read");
    }

    private final Path path;

    private Result<String> result;

    public DefaultOneReadFileReader(final String path) {
        this(Path.of(path));
    }

    public DefaultOneReadFileReader(final Path path) {
        this.path = path;
    }

    @Override
    public Result<String> read() {
        if (result == null) {
            try {
                result = DefaultResultBuilder.<String>ok(Files.readString(path));
            } catch (IOException e) {
                result = DefaultResultBuilder.<String>fail(CR.get(Code.CANNOT_READ), e.getMessage());
            }
        }
        return result;
    }
}
