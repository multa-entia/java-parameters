package ru.multa.entia.parameters.impl.source;

import lombok.RequiredArgsConstructor;
import ru.multa.entia.parameters.api.properties.Property;
import ru.multa.entia.parameters.api.reader.Reader;
import ru.multa.entia.parameters.api.source.PropertySource;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class DefaultPropertySource implements PropertySource {
    public enum Code {
        READER_IS_NULL,
        READER_RETURNED_FAIL,
        PROPERTY_ALREADY_REGISTERED,
        PROPERTY_ALREADY_UNREGISTERED
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.READER_IS_NULL, "parameters:property-source.default:reader-is-null");
        CR.update(Code.READER_RETURNED_FAIL, "parameters:property-source.default:reader-returned-fail");
        CR.update(Code.PROPERTY_ALREADY_REGISTERED, "parameters:property-source.default:property-already-registered");
        CR.update(Code.PROPERTY_ALREADY_UNREGISTERED, "parameters:property-source.default:property-already-unregistered");
    }

    private final Map<String, Object> data = new HashMap<>();
    private final Map<String, Property<?>> properties = new HashMap<>();
    private final Reader<Map<String, Object>> reader;

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public synchronized Result<Property<?>> register(final Property<?> property) {
        if (properties.containsKey(property.getName())) {
            return DefaultResultBuilder.<Property<?>>fail(CR.get(Code.PROPERTY_ALREADY_REGISTERED));
        }
        properties.put(property.getName(), property);

        return DefaultResultBuilder.<Property<?>>ok(property);
    }

    @Override
    public synchronized Result<Property<?>> unregister(final Property<?> property) {
        return null;
    }

    @Override
    public synchronized Result<Object> update() {
        return null;
    }

    public static class Builder {
        private Reader<Map<String, Object>> reader;

        public Builder reader(final Reader<Map<String, Object>> reader) {
            this.reader = reader;
            return this;
        }

        public Result<PropertySource> build() {
            return reader != null
                    ? DefaultResultBuilder.<PropertySource>ok(new DefaultPropertySource(reader))
                    : DefaultResultBuilder.<PropertySource>fail(CR.get(Code.READER_IS_NULL));
        }
    }
}
