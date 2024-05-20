package ru.multa.entia.parameters.impl.reader.file;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.parameters.api.property.Property;
import ru.multa.entia.parameters.impl.property.DefaultProperty;
import ru.multa.entia.results.api.result.Result;

import java.lang.reflect.Field;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractPropertyTest {

    @Test
    void shouldCheckNameGetting() {
        String expectedName = Faker.str_().random();
        Supplier<Property<Object>> propertySupplier = () -> {
            DefaultProperty property = Mockito.mock(DefaultProperty.class);
            Mockito.when(property.getName()).thenReturn(expectedName);

            return property;
        };

        AbstractProperty<Object> property = new AbstractProperty<>(propertySupplier.get()) {
            @Override
            public Result<Object> get() {
                return null;
            }
        };

        assertThat(property.getName()).isEqualTo(expectedName);
    }

    @SneakyThrows
    @Test
    void shouldCheckRawSetting() {
        String expectedRaw = Faker.str_().random();
        AbstractProperty<Object> property = new AbstractProperty<>(null) {
            @Override
            public Result<Object> get() {
                return null;
            }
        };

        property.set(expectedRaw);
        Field field = property.getClass().getSuperclass().getDeclaredField("raw");
        field.setAccessible(true);
        Object gottenRaw = field.get(property);

        assertThat(gottenRaw).isEqualTo(expectedRaw);
    }
}