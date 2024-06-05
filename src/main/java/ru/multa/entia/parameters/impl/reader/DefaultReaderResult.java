package ru.multa.entia.parameters.impl.reader;

import lombok.RequiredArgsConstructor;
import ru.multa.entia.parameters.api.reader.ReaderResult;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public class DefaultReaderResult implements ReaderResult {
    private final Map<String, Object> data;

    public static Builder builder () {
        return new Builder();
    }

    @Override
    public String get(final String property) {
        return data.containsKey(property) && data.get(property) != null ? String.valueOf(data.get(property)) : null;
    }

    @Override
    public <T> T getAs(final String property,
                       final Function<Object, T> adapter) {
        return data.containsKey(property) ? adapter.apply(data.get(property)) : null;
    }

    public static class Builder {
        private final Map<String, Object> data = new HashMap<>();

        public Builder put(final String property, final Object value) {
            data.put(property, value);
            return this;
        }

        public ReaderResult build() {
            return new DefaultReaderResult(data);
        }
    }
}
