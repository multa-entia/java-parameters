package ru.multa.entia.parameters.impl.ids;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultIdTest {

    @Test
    void shouldCheckCreationAndGetting() {
        Ids type = Ids.FILE;
        String key = Faker.str_().random();
        UUID expected = new UUID(type.getValue(), key.hashCode());

        UUID gotten = new DefaultId(type, key).get();

        assertThat(gotten).isEqualTo(expected);
    }
}