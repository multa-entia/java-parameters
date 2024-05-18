package ru.multa.entia.parameters.impl.reader.file;

import ru.multa.entia.parameters.api.reader.file.ReadResult;
import ru.multa.entia.parameters.api.reader.file.Reader;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class DefaultReader implements Reader {
    public enum Code {
        CANNOT_READ
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.CANNOT_READ, "parameters:file-reader.default:cannot-read");
    }

    private final Path path;

    public DefaultReader(final String path) {
        this(Path.of(path));
    }

    public DefaultReader(final Path path) {
        this.path = path;
    }

    @Override
    public Result<ReadResult> read() {
        try {
            return DefaultResultBuilder.<ReadResult>ok(new DefaultReadResult(
                    Files.readString(path),
                    path,
                    Files.readAttributes(path, BasicFileAttributes.class))
            );
        } catch (IOException e) {
            return DefaultResultBuilder.<ReadResult>fail(CR.get(Code.CANNOT_READ), e.getMessage());
        }
    }
}
