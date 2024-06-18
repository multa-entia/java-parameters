package ru.multa.entia.parameters.impl.properties;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.utils.Results;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultAdaptNullablePropertyTest {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @Test
    void shouldCheckSettingGetting_ifArgumentNull() {
        DefaultAdaptNullableProperty<String> property = new DefaultAdaptNullableProperty<>(Faker.str_().random(), null);
        Result<String> result = property.set(null);

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
    void shouldCheckSettingGetting_ifAdapterNull() {
        DefaultAdaptNullableProperty<String> property = new DefaultAdaptNullableProperty<>(Faker.str_().random(), null);
        Result<String> result = property.set(Faker.int_().random());

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultAdaptNullableProperty.Code.ADAPTER_IS_NULL))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckSettingGetting_ifAdapterReturnedFail() {
        Function<Object, Result<Float>> adapter = o -> {
            return DefaultResultBuilder.<Float>fail(Faker.str_().random());
        };

        DefaultAdaptNullableProperty<Float> property = new DefaultAdaptNullableProperty<>(Faker.str_().random(), adapter);
        Result<Float> result = property.set(Faker.int_().random());

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
    void shouldCheckSettingGetting() {
        Integer expected = Faker.int_().between(10, 100);
        Function<Object, Result<Integer>> adapter = o -> {
            return DefaultResultBuilder.<Integer>ok(expected);
        };

        DefaultAdaptNullableProperty<Integer> property = new DefaultAdaptNullableProperty<>(Faker.str_().random(), adapter);
        Result<Integer> result = property.set(Faker.int_().random());

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