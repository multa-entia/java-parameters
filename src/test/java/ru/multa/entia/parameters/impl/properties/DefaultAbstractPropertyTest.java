package ru.multa.entia.parameters.impl.properties;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.utils.Results;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultAbstractPropertyTest {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    private static final String CODE = Faker.str_().random();

    @Test
    void shouldCheckNameGetting() {
        String expectedName = Faker.str_().random();
        String gottenName = new TestProperty(expectedName).getName();

        assertThat(gottenName).isEqualTo(expectedName);
    }

    @Test
    void shouldCheckInitialGetting() {
        Result<String> result = new TestProperty(Faker.str_().random()).get();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultAbstractProperty.Code.IS_NOT_SET))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckSettingGetting() {
        String expectedValue = Faker.str_().random();
        TestProperty property = new TestProperty(Faker.str_().random());
        Result<String> setResult = property.set(expectedValue);

        assertThat(
                Results.comparator(setResult)
                        .isSuccess()
                        .value(expectedValue)
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();

        Result<String> getResult = property.get();
        assertThat(Results.equal(getResult, setResult)).isTrue();
    }

    @Test
    void shouldCheckSettingGetting_badSecondAttempt() {
        String firstExpectedValue = Faker.str_().random();
        String secondExpectedValue = Faker.str_().random();
        TestProperty property = new TestProperty(Faker.str_().random());
        Result<String> firstSetResult = property.set(firstExpectedValue);
        Result<String> secondSetResult = property.set(secondExpectedValue);

        assertThat(
                Results.comparator(secondSetResult)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CODE)
                        .back()
                        .compare()
        ).isTrue();

        Result<String> getResult = property.get();
        assertThat(Results.equal(getResult, firstSetResult)).isTrue();
    }

    private static class TestProperty extends DefaultAbstractProperty<String>  {
        private boolean first = true;
        public TestProperty(final String name) {
            super(name);
        }

        @Override
        protected Result<String> checkAndGet(Object object) {
            if (first) {
                first = false;
                return DefaultResultBuilder.<String>ok(String.valueOf(object));
            }
            return DefaultResultBuilder.<String>fail(CODE);
        }
    }
}