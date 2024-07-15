package ru.multa.entia.parameters.impl.ids;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.multa.entia.parameters.api.ids.Id;

import java.nio.file.Path;
import java.util.UUID;

@EqualsAndHashCode
public class DefaultId implements Id {
    @Getter
    @RequiredArgsConstructor
    public enum Ids {
        FILE(0),
        ENV_VARS(1);

        private final int value;
    }

    public static Id createIdForFile(Path path) {
        return new DefaultId(Ids.FILE, path);
    }

    public static Id createIdForEnvVar() {
        return new DefaultId(Ids.ENV_VARS, "");
    }

    public static Id createIdForEnvVar(final Object key) {
        return new DefaultId(Ids.ENV_VARS, key);
    }

    private final UUID value;

    private DefaultId(final Ids type, final Object key) {
        this.value = new UUID(type.getValue(), key.hashCode());
    }

    @Override
    public UUID get() {
        return value;
    }
}
