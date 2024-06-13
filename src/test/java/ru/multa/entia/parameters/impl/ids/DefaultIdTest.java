package ru.multa.entia.parameters.impl.ids;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.parameters.api.ids.Id;

import java.nio.file.Path;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultIdTest {

    @Test
    void shouldCheckIdForFileCreation() {
        Path path = Path.of("/opt");
        UUID expected = new UUID(DefaultId.Ids.FILE.getValue(), path.hashCode());

        Id id = DefaultId.createIdForFile(path);
        assertThat(id.get()).isEqualTo(expected);
    }

    @Test
    void shouldCheckIdForEnvVar() {
        UUID expected = new UUID(DefaultId.Ids.ENV_VARS.getValue(), "".hashCode());

        Id id = DefaultId.createIdForEnvVar();
        assertThat(id.get()).isEqualTo(expected);
    }

    @Test
    void shouldCheckIdForEnvVar_withKey() {
        String key = Faker.str_().random();
        UUID expected = new UUID(DefaultId.Ids.ENV_VARS.getValue(), key.hashCode());

        Id id = DefaultId.createIdForEnvVar(key);
        assertThat(id.get()).isEqualTo(expected);
    }
}