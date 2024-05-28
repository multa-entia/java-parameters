package ru.multa.entia.parameters.impl.source;

import lombok.EqualsAndHashCode;
import ru.multa.entia.parameters.api.reader.file.ReadResult;
import ru.multa.entia.parameters.api.reader.file.Reader;
import ru.multa.entia.parameters.api.source.PropertySource;
import ru.multa.entia.parameters.api.source.SourceAdapter;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@EqualsAndHashCode
public class DefaultTextualPropertySource implements PropertySource {
    public enum Code {
        READER_IS_NULL,
        NOT_INIT,
        NOT_PRESENT
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.READER_IS_NULL, "parameters:property-source.textual.default:reader-is-null");
        CR.update(Code.NOT_INIT, "parameters:property-source.textual.default:not-init");
        CR.update(Code.NOT_PRESENT, "parameters:property-source.textual.default:not-present");
    }

    private final Reader reader;
    private final SourceAdapter<String, Map<String, Object>> adapter;

    private Map<String, Object> rawProperties;

    public DefaultTextualPropertySource() {
        this(null, null);
    }

    public DefaultTextualPropertySource(final Reader reader) {
        this(reader, null);
    }

    public DefaultTextualPropertySource(final Reader reader,
                                        final SourceAdapter<String, Map<String, Object>> adapter) {
        this.reader = reader;
        this.adapter = Objects.requireNonNullElse(adapter, new DefaultYamlSourceAdapter());
    }

    @Override
    public Result<Object> update() {
        if (reader == null) {
            return DefaultResultBuilder.<Object>fail(CR.get(Code.READER_IS_NULL));
        }

        Result<ReadResult> readerResult = reader.read();
        if (!readerResult.ok()) {
            return DefaultResultBuilder.<Object>fail(readerResult.seed());
        }

        Result<Map<String, Object>> adapterResult = adapter.adapt(readerResult.value().content());
        if (!adapterResult.ok()) {
            return DefaultResultBuilder.<Object>fail(adapterResult.seed());
        }

        synchronized (this) {
            rawProperties = adapterResult.value();
        }
        return DefaultResultBuilder.<Object>ok();
    }

    @Override
    public synchronized Result<Object> get(final String propertyName) {
        if (rawProperties == null) {
            return DefaultResultBuilder.<Object>fail(CR.get(Code.NOT_INIT));
        }
        return rawProperties.containsKey(propertyName)
                ? DefaultResultBuilder.<Object>ok(rawProperties.get(propertyName))
                : DefaultResultBuilder.<Object>fail(CR.get(Code.NOT_PRESENT));
    }
}
