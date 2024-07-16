package ru.multa.entia.parameters.impl.readers;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.multa.entia.parameters.api.ids.Id;
import ru.multa.entia.parameters.api.readers.Reader;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@EqualsAndHashCode
public class DefaultFileReader implements Reader<String> {
    public enum Code {
        PATH_IS_NULL,
        CANNOT_READ
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.PATH_IS_NULL, "parameters:file-reader.default:path-is-null");
        CR.update(Code.CANNOT_READ, "parameters:file-reader.default:cannot-read");
    }

    public static Builder builder() {
        return new Builder();
    }

    private final Path path;
    @Getter
    private final Id id;

    private DefaultFileReader(final Path path, final Id id) {
        this.path = path;
        this.id = id;
    }

    @Override
    public Result<String> read() {
        try {
            return DefaultResultBuilder.<String>ok(Files.readString(path));
        } catch (IOException ex) {
            ex.printStackTrace();
            return DefaultResultBuilder.<String>fail(CR.get(Code.CANNOT_READ));
        }
    }

    public static class Builder {
        private Path path;
        private Id id;

        public Builder path(final Path path) {
            this.path = path;
            return this;
        }

        public Builder id(final Id id) {
            this.id = id;
            return this;
        }

        public Result<Reader<String>> build() {
            return path != null
                    ? DefaultResultBuilder.<Reader<String>>ok(new DefaultFileReader(path, id))
                    : DefaultResultBuilder.<Reader<String>>fail(CR.get(Code.PATH_IS_NULL));
        }
    }
}

