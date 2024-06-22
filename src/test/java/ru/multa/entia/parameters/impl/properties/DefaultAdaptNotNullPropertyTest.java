package ru.multa.entia.parameters.impl.properties;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.utils.Results;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultAdaptNotNullPropertyTest {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @Test
    void shouldCheckSettingGetting_ifBadAdaptResult() {
        String expectedCode = Faker.str_().random();
        DefaultAdaptNotNullProperty<String> property = new DefaultAdaptNotNullProperty<>(Faker.str_().random(), input -> {
            return DefaultResultBuilder.<String>fail(expectedCode);
        });
        Result<String> result = property.set(Faker.str_().random());

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultAdaptNullableProperty.Code.ADAPT_ERROR))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckSettingGetting_ifNullAdaptValue() {
        DefaultAdaptNotNullProperty<String> property = new DefaultAdaptNotNullProperty<>(Faker.str_().random(), input -> {
            return DefaultResultBuilder.<String>ok();
        });
        Result<String> result = property.set(null);

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultAdaptNotNullProperty.Code.IS_NULL))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckSettingGetting() {
        String expectedValue = Faker.str_().random();
        DefaultAdaptNotNullProperty<String> property = new DefaultAdaptNotNullProperty<>(Faker.str_().random(), input -> {
            return DefaultResultBuilder.<String>ok(expectedValue);
        });
        Result<String> result = property.set(Faker.str_().random());

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