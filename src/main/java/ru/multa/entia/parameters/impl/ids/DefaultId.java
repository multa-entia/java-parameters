package ru.multa.entia.parameters.impl.ids;

import lombok.EqualsAndHashCode;
import ru.multa.entia.parameters.api.ids.Id;

import java.util.UUID;

// TODO: add static create-methods
@EqualsAndHashCode
public class DefaultId implements Id {
    private final UUID value;

    public DefaultId(final Ids type, final Object key) {
        this.value = new UUID(type.getValue(), key.hashCode());
    }

    @Override
    public UUID get() {
        return value;
    }
}
