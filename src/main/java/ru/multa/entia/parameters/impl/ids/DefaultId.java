package ru.multa.entia.parameters.impl.ids;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.multa.entia.parameters.api.ids.Id;

import java.nio.file.Path;

@RequiredArgsConstructor
@EqualsAndHashCode
public class DefaultId implements Id {
    @Getter
    @RequiredArgsConstructor
    public enum Ids {
        FILE(0),
        ENV_VARS(1);

        private final int value;
    }

    public static Id createIdForEnvVar(final Object... optionalKeys) {
        return new DefaultId(Ids.ENV_VARS, "", optionalKeys);
    }

    public static Id createIdForFile(final Path path, final Object... optionalKeys) {
        return new DefaultId(Ids.FILE, path, optionalKeys);
    }

    private final Ids type;
    private final Object key;
    private final Object[] optionalKeys;

    @Override
    public int get() {
        return hashCode();
    }
}
