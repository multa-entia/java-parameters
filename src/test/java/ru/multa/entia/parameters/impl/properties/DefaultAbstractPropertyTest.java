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
        property.set(expectedValue);

        Result<String> result = property.get();

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

    private static class TestProperty extends DefaultAbstractProperty<String>  {
        public TestProperty(final String name) {
            super(name);
        }

        @Override
        public Result<String> set(final Object object) {
            data = DefaultResultBuilder.<String>ok(String.valueOf(object));
            return data;
        }
    }
}