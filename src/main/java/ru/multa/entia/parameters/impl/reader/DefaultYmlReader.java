package ru.multa.entia.parameters.impl.reader;

import lombok.EqualsAndHashCode;
import ru.multa.entia.parameters.api.ids.Id;
import ru.multa.entia.parameters.api.reader.Reader;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.nio.file.Path;
import java.util.Map;

@EqualsAndHashCode
public class DefaultYmlReader implements Reader<Map<String, Object>> {
    public enum Code {
        READER_OR_PATH_NOT_SET,
        SYNTAX_ERROR
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.READER_OR_PATH_NOT_SET, "parameters:yml-reader.default:reader-or-path-not-set");
        CR.update(Code.SYNTAX_ERROR, "parameters:yml-reader.default:syntax-error");
    }

    public static Builder builder () {
        return new Builder();
    }

    @Override
    public Result<Map<String, Object>> read() {
        return null;
    }

    @Override
    public Id getId() {
        return null;
    }

    public static class Builder {
        private Reader<String> textReader;
        private Path path;

        public Builder textReader(final Reader<String> textReader) {
            this.textReader = textReader;
            return this;
        }

        public Builder path(final Path path) {
            this.path = path;
            return this;
        }

        public Result<Reader<Map<String, Object>>> build() {
            if (textReader != null) {
                return null;
            } else if (path != null) {
                return null;
            }

            return DefaultResultBuilder.<Reader<Map<String, Object>>>fail(CR.get(Code.READER_OR_PATH_NOT_SET));
        }
    }
}
