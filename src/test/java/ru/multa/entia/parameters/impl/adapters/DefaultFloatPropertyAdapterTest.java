package ru.multa.entia.parameters.impl.adapters;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.utils.Results;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultFloatPropertyAdapterTest {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @Test
    void shouldCheckAdaptation_ifRawObjectIsNull() {
        Result<Float> result = new DefaultFloatPropertyAdapter().apply(null);

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
    void shouldCheckAdaptation_onFailAdaptation() {
        String object = Faker.str_().fromTemplate("[x-z]{3:7}");
        Result<Float> result = new DefaultFloatPropertyAdapter().apply(object);

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultFloatPropertyAdapter.Code.INVALID))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckAdaptation() {
        String object = Faker.str_().fromTemplate("[1-9]{3:4}.[1-9]{3:4}");
        float expected = Float.parseFloat(object);
        Result<Float> result = new DefaultFloatPropertyAdapter().apply(object);

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