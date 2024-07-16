package ru.multa.entia.parameters.impl.ids;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.parameters.api.ids.Id;

import java.nio.file.Path;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultIdTest {

    @Test
    void shouldCheckCreation() {
        DefaultId.Ids type = DefaultId.Ids.FILE;
        String key = Faker.str_().random();
        Integer size = Faker.int_().between(5, 10);
        Object[] optionalKeys = new Object[size];
        for (int i = 0; i < size; i++) {
            optionalKeys[i] = Faker.str_().random();
        }

        DefaultId id = new DefaultId(type, key, optionalKeys);
        assertThat(id.get()).isEqualTo(computeTestHash(type, key, optionalKeys));
    }

    @Test
    void shouldCheckCreation_forEnvVars() {
        DefaultId.Ids type = DefaultId.Ids.ENV_VARS;
        Integer size = Faker.int_().between(5, 10);
        Object[] optionalKeys = new Object[size];
        for (int i = 0; i < size; i++) {
            optionalKeys[i] = Faker.str_().random();
        }

        Id id = DefaultId.createIdForEnvVar(optionalKeys);
        assertThat(id.get()).isEqualTo(computeTestHash(type, "", optionalKeys));
    }

    @Test
    void shouldCheckCreation_forFile() {
        DefaultId.Ids type = DefaultId.Ids.FILE;
        Path path = Path.of("/opt");
        Integer size = Faker.int_().between(5, 10);
        Object[] optionalKeys = new Object[size];
        for (int i = 0; i < size; i++) {
            optionalKeys[i] = Faker.str_().random();
        }

        Id id = DefaultId.createIdForFile(path, optionalKeys);
        assertThat(id.get()).isEqualTo(computeTestHash(type, path, optionalKeys));
    }

    private static int computeTestHash(final DefaultId.Ids type,
                                       final Object key,
                                       final Object... optionalKeys){
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + (type == null ? 43 : type.hashCode());
        result = result * PRIME + (key == null ? 43 : key.hashCode());
        result = result * PRIME + Arrays.deepHashCode(optionalKeys);
        return result;
    }
}