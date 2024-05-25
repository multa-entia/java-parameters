package ru.multa.entia.parameters.impl.extractor;

import lombok.Getter;
import ru.multa.entia.parameters.api.extractor.ExtractorOld;

// TODO: del
public abstract class AbstractExtractorOld<T> implements ExtractorOld<T> {
    protected Object raw;
    @Getter
    protected boolean set;
    @Getter
    protected String property;

    public AbstractExtractorOld(final String property) {
        this.property = property;
    }

    @Override
    public void set(final Object object) {
        this.set = true;
        this.raw = object;
    }
}
