package ru.multa.entia.parameters.impl.properties;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.utils.Results;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultCastNullablePropertyTest {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @Test
    void shouldCheckSettingGetting_ifNull() {
        String expected = null;
        DefaultCastNullableProperty<String> property = new DefaultCastNullableProperty<>(Faker.str_().random(), String.class);
        Result<String> result = property.set(expected);

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

    @Test
    void shouldCheckSettingGetting_ifBadCast() {
        DefaultCastNullableProperty<Integer> property = new DefaultCastNullableProperty<>(Faker.str_().random(), Integer.class);
        Result<Integer> result = property.set(Faker.str_().random());

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultCastNullableProperty.Code.BAD_CAST))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckSettingGetting() {
        String expected = Faker.str_().random();
        DefaultCastNullableProperty<String> property = new DefaultCastNullableProperty<>(Faker.str_().random(), String.class);
        Result<String> result = property.set(expected);

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