package ru.multa.entia.parameters.impl.property;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.parameters.api.property.Property;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.utils.Results;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultNotNullPropertyTest {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @Test
    void shouldCheckPropertyGetting() {
        String expectedName = Faker.str_().random();
        Supplier<Property<Object>> propertySupplier = () -> {
            DefaultProperty property = Mockito.mock(DefaultProperty.class);
            Mockito.when(property.getName()).thenReturn(expectedName);

            return property;
        };

        DefaultNotNullProperty property = new DefaultNotNullProperty(expectedName);

        assertThat(property.getName()).isEqualTo(expectedName);
    }

    @Test
    void shouldCheckGetting_ifInnerPropertyReturnsFail() {
        String expectedCode = Faker.str_().random();
        Supplier<Property<Object>> propertySupplier = () -> {
            Result<Object> result = DefaultResultBuilder.<Object>fail(expectedCode);
            DefaultProperty property = Mockito.mock(DefaultProperty.class);
            Mockito.when(property.get()).thenReturn(result);

            return property;
        };

        Result<Object> result = new DefaultNotNullProperty(Faker.str_().random(), propertySupplier.get()).get();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(expectedCode)
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckGetting_ifRawNull() {
        Supplier<Property<Object>> propertySupplier = () -> {
            Result<Object> result = DefaultResultBuilder.<Object>ok();
            DefaultProperty property = Mockito.mock(DefaultProperty.class);
            Mockito.when(property.get()).thenReturn(result);

            return property;
        };

        Result<Object> result = new DefaultNotNullProperty(Faker.str_().random(), propertySupplier.get()).get();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultNotNullProperty.Code.IS_NULL))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckGetting() {
        String expectedValue = Faker.str_().random();
        DefaultNotNullProperty property = new DefaultNotNullProperty(Faker.str_().random());
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