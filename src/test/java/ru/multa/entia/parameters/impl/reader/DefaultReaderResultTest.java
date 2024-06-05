package ru.multa.entia.parameters.impl.reader;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.parameters.api.reader.ReaderResult;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultReaderResultTest {

    @Test
    void shouldCheckGetting_ifAbsence() {
        String property = Faker.str_().random();
        ReaderResult result = DefaultReaderResult.builder().build();

        assertThat(result.get(property)).isNull();
    }

    @Test
    void shouldCheckGetting_ifNull() {
        String property = Faker.str_().random();
        ReaderResult result = DefaultReaderResult.builder().put(property, null).build();

        assertThat(result.get(property)).isNull();
    }

    @Test
    void shouldCheckGetting_ifNotString() {
        String property = Faker.str_().random();
        Integer initValue = Faker.int_().random();
        ReaderResult result = DefaultReaderResult.builder().put(property, initValue).build();

        assertThat(result.get(property)).isEqualTo(String.valueOf(initValue));
    }

    @Test
    void shouldCheckGetting_ifString() {
        String property = Faker.str_().random();
        String initValue = Faker.str_().random();
        ReaderResult result = DefaultReaderResult.builder().put(property, initValue).build();

        assertThat(result.get(property)).isEqualTo(initValue);
    }

    @Test
    void shouldCheckGettingAs_ifAbsence() {
        Function<Object, String> adapter = Object::toString;

        ReaderResult result = DefaultReaderResult.builder().build();
        String gotten = result.getAs(Faker.str_().random(), adapter);

        assertThat(gotten).isNull();
    }

    @Test
    void shouldCheckGettingAs() {
        Function<Object, Integer> adapter = input -> {return Integer.parseInt(String.valueOf(input));};

        Integer expected = Faker.int_().between(10, 20);
        String property = Faker.str_().random();
        ReaderResult result = DefaultReaderResult.builder().put(property, expected).build();
        Integer gotten = result.getAs(property, adapter);

        assertThat(gotten).isEqualTo(expected);
    }
}