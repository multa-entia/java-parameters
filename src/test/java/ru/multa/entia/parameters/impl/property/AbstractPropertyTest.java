package ru.multa.entia.parameters.impl.property;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.utils.Results;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractPropertyTest {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @Test
    void shouldCheckNameGetting() {
        String expectedName = Faker.str_().random();
        String gottenName = new TestDefaultProperty(expectedName).getName();

        assertThat(gottenName).isEqualTo(expectedName);
    }

    @Test
    void shouldCheckGetting_ifNotSet() {
        Result<Object> result = new TestDefaultProperty(Faker.str_().random()).get();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(AbstractProperty.Code.IS_NOT_SET))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckGetting_ifSetNul() {
        TestDefaultProperty property = new TestDefaultProperty(Faker.str_().random());
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
        String expected = Faker.str_().random();
        TestDefaultProperty property = new TestDefaultProperty(Faker.str_().random());
        property.set(expected);

        Result<Object> result = property.get();

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

    private static class TestDefaultProperty extends AbstractProperty<Object> {
        public TestDefaultProperty(final String name) {
            super(name);
        }

        @Override
        protected Result<Object> checkRaw(final Object raw) {
            return DefaultResultBuilder.<Object>ok(raw);
        }
    }
}