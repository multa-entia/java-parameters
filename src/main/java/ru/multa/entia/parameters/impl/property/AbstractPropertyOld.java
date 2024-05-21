package ru.multa.entia.parameters.impl.property;

import lombok.RequiredArgsConstructor;
import ru.multa.entia.parameters.api.property.Property;

@RequiredArgsConstructor
public abstract class AbstractPropertyOld<T> implements Property<T> {
    protected final Property<Object> innerProperty;

    @Override
    public void set(final Object raw) {
        innerProperty.set(raw);
    }

    @Override
    public String getName() {
        return innerProperty.getName();
    }
}
