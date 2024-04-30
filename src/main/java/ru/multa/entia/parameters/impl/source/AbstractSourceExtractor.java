package ru.multa.entia.parameters.impl.source;

import ru.multa.entia.parameters.api.source.SourceExtractor;

public abstract class AbstractSourceExtractor<T> implements SourceExtractor<T> {
    protected Object raw;

    @Override
    public void set(final Object object) {
        this.raw = object;
    }
}
