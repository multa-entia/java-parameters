package ru.multa.entia.parameters.impl.source;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.parameters.api.properties.Property;
import ru.multa.entia.parameters.api.reader.Reader;
import ru.multa.entia.parameters.api.source.PropertySource;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.utils.Results;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPropertySourceTest {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    private static final TestReader READER = Mockito.mock(TestReader.class);
    private static final Function<String, Property<String>> PROPERTY_FUNCTION = name -> {
        TestStringProperty property = Mockito.mock(TestStringProperty.class);
        Mockito.when(property.getName()).thenReturn(name);

        return property;
    };

    @SneakyThrows
    @Test
    void shouldCheckBuilding_readerSetting() {
        DefaultPropertySource.Builder builder = DefaultPropertySource.builder().reader(READER);
        Field field = builder.getClass().getDeclaredField("reader");
        field.setAccessible(true);
        Object gotten = field.get(builder);

        assertThat(gotten).isEqualTo(READER);
    }

    @Test
    void shouldCheckBuilding_readerIsNull() {
        Result<PropertySource> result = DefaultPropertySource.builder().build();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(CR.get(DefaultPropertySource.Code.READER_IS_NULL))
                        .back()
                        .compare()
        ).isTrue();
    }

    @SneakyThrows
    @Test
    void shouldCheckBuilding() {
        PropertySource source = DefaultPropertySource.builder().reader(READER).build().value();
        Field field = source.getClass().getDeclaredField("reader");
        field.setAccessible(true);
        Object gotten = field.get(source);

        assertThat(gotten).isEqualTo(READER);
    }

    @Test
    void shouldCheckRegister() {
        Property<String> expected = PROPERTY_FUNCTION.apply(Faker.str_().random());
        PropertySource source = DefaultPropertySource.builder().reader(READER).build().value();
        Result<Property<?>> result = source.register(expected);

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
    void shouldCheckRegister_ifAlreadyRegistered() {
        Property<String> property = PROPERTY_FUNCTION.apply(Faker.str_().random());

        PropertySource source = DefaultPropertySource.builder().reader(READER).build().value();
        source.register(property);
        Result<Property<?>> result = source.register(property);

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultPropertySource.Code.PROPERTY_ALREADY_REGISTERED))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckRegister_ifDataNotEmpty() {

    }

    @Test
    void shouldCheckUnregister_ifAbsence() {

    }

    @Test
    void shouldCheckUnregister() {

    }

    @Test
    void shouldCheckUpdating_ifReaderRetFail() {

    }

    @Test
    void shouldCheckUpdating_ifReaderRetFailOnSecondTime() {

    }

    @Test
    void shouldCheckUpdating() {

    }

    private interface TestStringProperty extends Property<String> {}
    private interface TestReader extends Reader<Map<String, Object>> {}
}