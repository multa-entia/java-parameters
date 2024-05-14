package ru.multa.entia.parameters.impl.extractor;

import lombok.Getter;
import ru.multa.entia.parameters.api.extractor.Extractor;

public abstract class AbstractExtractor<T> implements Extractor<T> {
    protected Object raw;
    @Getter
    protected boolean set;
    @Getter
    protected String property;

    public AbstractExtractor(final String property) {
        this.property = property;
    }

    @Override
    public void set(final Object object) {
        this.set = true;
        this.raw = object;
    }
}
