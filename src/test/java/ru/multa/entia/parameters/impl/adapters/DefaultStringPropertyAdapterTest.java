package ru.multa.entia.parameters.impl.adapters;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.utils.Results;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultStringPropertyAdapterTest {

    @Test
    void shouldCheckAdaptation_ifInputNull() {
        Result<String> result = new DefaultStringPropertyAdapter().apply(null);

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .value(null)
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();
    }

    @ParameterizedTest
    @MethodSource
    void shouldCheckAdaptation(Object input) {
        String expected = String.valueOf(input);
        Result<String> result = new DefaultStringPropertyAdapter().apply(input);

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .value(expected)
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();
    }

    private Stream<Object> shouldCheckAdaptation() {
        return Stream.<Object>of(
                Faker.str_().random(),
                Faker.bool_().random(),
                Faker.int_().random(),
                Faker.long_().random(),
                Faker.uuid_().random()
        );
    }
}