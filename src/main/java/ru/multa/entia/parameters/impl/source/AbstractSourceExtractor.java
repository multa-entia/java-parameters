package ru.multa.entia.parameters.impl.source;

import lombok.Getter;
import ru.multa.entia.parameters.api.source.SourceExtractor;

// TODO: !!!
// StringExtractor
// IntExtractor
// FloatExtractor
public abstract class AbstractSourceExtractor<T> implements SourceExtractor<T> {
    protected Object raw;
    @Getter
    protected boolean set;

    @Override
    public void set(final Object object) {
        this.set = true;
        this.raw = object;
    }
}
