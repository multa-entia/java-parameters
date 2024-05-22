package ru.multa.entia.parameters.impl.property;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.utils.Results;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultIntegerPropertyTest {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @Test
    void shouldCheckGetting_ifWasSetNull() {
        DefaultIntegerProperty property = new DefaultIntegerProperty(Faker.str_().random());
        property.set(null);
        Result<Integer> result = property.get();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultIntegerProperty.Code.IS_NULL))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckGetting_ifWasSetInvalidObject() {
        DefaultIntegerProperty property = new DefaultIntegerProperty(Faker.str_().random());
        property.set(Faker.str_().fromTemplate("[a-b]{10:20}"));
        Result<Integer> result = property.get();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultIntegerProperty.Code.INVALID_RAW))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckGetting() {
        Integer expected = Faker.int_().between(-100, 100);
        DefaultIntegerProperty property = new DefaultIntegerProperty(Faker.str_().random());
        property.set(String.valueOf(expected));
        Result<Integer> result = property.get();

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
}