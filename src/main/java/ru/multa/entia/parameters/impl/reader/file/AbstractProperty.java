package ru.multa.entia.parameters.impl.reader.file;

import lombok.RequiredArgsConstructor;
import ru.multa.entia.parameters.api.property.Property;

@RequiredArgsConstructor
public abstract class AbstractProperty<T> implements Property<T> {
    private final Property<T> innerProperty;

    protected Object raw;

    @Override
    public void set(final Object raw) {
        this.raw = raw;
    }


    @Override
    public String getName() {
        return innerProperty.getName();
    }
}
