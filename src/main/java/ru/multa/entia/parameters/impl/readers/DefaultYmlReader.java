package ru.multa.entia.parameters.impl.readers;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import ru.multa.entia.parameters.api.ids.Id;
import ru.multa.entia.parameters.api.readers.Reader;
import ru.multa.entia.parameters.impl.ids.DefaultId;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

@EqualsAndHashCode
public class DefaultYmlReader implements Reader<Map<String, Object>> {
    public enum Code {
        READER_OR_PATH_NOT_SET,
        TEXT_READER_RET_FAIL,
        SYNTAX_ERROR
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.READER_OR_PATH_NOT_SET, "parameters:yml-reader.default:reader-or-path-not-set");
        CR.update(Code.TEXT_READER_RET_FAIL, "parameters:yml-reader.default:text-reader-ret-fail");
        CR.update(Code.SYNTAX_ERROR, "parameters:yml-reader.default:syntax-error");
    }

    private static final String PATH_TEMPLATE = "yml%s";

    public static Builder builder () {
        return new Builder();
    }

    private final Reader<String> textReader;
    @Getter
    private final Id id;

    private DefaultYmlReader(final Reader<String> textReader,
                            final Id id) {
        this.textReader = textReader;
        this.id = id;
    }

    @Override
    public Result<Map<String, Object>> read() {
        Result<String> result = textReader.read();
        if (!result.ok()) {
            return new DefaultResultBuilder<Map<String, Object>>()
                    .success(false)
                    .seedBuilder()
                    .code(CR.get(Code.TEXT_READER_RET_FAIL))
                    .apply()
                    .causes(result)
                    .build();
        }

        try {
            return DefaultResultBuilder.<Map<String, Object>>ok(new Yaml().load(result.value()));
        } catch (YAMLException ex) {
            return DefaultResultBuilder.<Map<String, Object>>fail(CR.get(Code.SYNTAX_ERROR));
        }
    }

    public static class Builder {
        private Reader<String> textReader;
        private Path path;
        private Id id;

        public Builder textReader(final Reader<String> textReader) {
            this.textReader = textReader;
            return this;
        }

        public Builder path(final Path path) {
            this.path = path;
            return this;
        }

        public Builder id (final Id id) {
            this.id = id;
            return this;
        }

        public Result<Reader<Map<String, Object>>> build() {
            id = Objects.requireNonNullElse(id, DefaultId.createIdForFile(Path.of(String.format(PATH_TEMPLATE, path))));
            if (textReader == null && path != null) {
                textReader = DefaultFileReader.builder().path(path).id(id).build().value();
            }

            return textReader != null
                    ? DefaultResultBuilder.<Reader<Map<String, Object>>>ok(new DefaultYmlReader(textReader, id))
                    : DefaultResultBuilder.<Reader<Map<String, Object>>>fail(CR.get(Code.READER_OR_PATH_NOT_SET));
        }
    }
}
