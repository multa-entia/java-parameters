package ru.multa.entia.parameters.impl.getter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.utils.Results;

import java.util.function.Function;

class DefaultEnvVarGetterTest {

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @Test
    void shouldCheckGetting_ifNameNull() {
        DefaultEnvVarGetter getter = new DefaultEnvVarGetter();
        Result<String> result = getter.get(null);

        Assertions.assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultEnvVarGetter.Code.NAME_IS_NULL))
                        .argsAreEmpty()
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckGetting_ifNameBlank() {
        DefaultEnvVarGetter getter = new DefaultEnvVarGetter();
        Result<String> result = getter.get("   ");

        Assertions.assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultEnvVarGetter.Code.NAME_IS_BLANK))
                        .argsAreEmpty()
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckGetting_ifVarAbsence() {
        Function<String, String> innerGetter = name -> {return null;};
        DefaultEnvVarGetter getter = new DefaultEnvVarGetter(innerGetter);

        Result<String> result = getter.get(Faker.str_().random());

        Assertions.assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultEnvVarGetter.Code.VAR_IS_ABSENCE))
                        .argsAreEmpty()
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckGetting() {
        String expectedValue = Faker.str_().random();
        Function<String, String> innerGetter = name -> {return expectedValue;};
        DefaultEnvVarGetter getter = new DefaultEnvVarGetter(innerGetter);

        Result<String> result = getter.get(Faker.str_().random());

        Assertions.assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .value(expectedValue)
                        .seedsComparator()
                        .isNull()
                        .compare()
        ).isTrue();
    }
}