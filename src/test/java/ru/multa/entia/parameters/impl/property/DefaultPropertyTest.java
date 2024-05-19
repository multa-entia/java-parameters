package ru.multa.entia.parameters.impl.property;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.utils.Results;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPropertyTest {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @Test
    void shouldCheckPropertyGetting() {
        String expectedName = Faker.str_().random();
        DefaultProperty property = new DefaultProperty(expectedName);

        assertThat(property.getName()).isEqualTo(expectedName);
    }

    @Test
    void shouldCheckGetting_ifNotSet() {
        Result<Object> result = new DefaultProperty(Faker.str_().random()).get();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultProperty.Code.RAW_NOT_SET))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckGetting_ifRawNull() {
        DefaultProperty property = new DefaultProperty(Faker.str_().random());
        property.set(null);
        Result<Object> result = property.get();

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

    @Test
    void shouldCheckGetting() {
        String expectedValue = Faker.str_().random();
        DefaultProperty property = new DefaultProperty(Faker.str_().random());
        property.set(expectedValue);
        Result<Object> result = property.get();

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .value(expectedValue)
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();
    }
}