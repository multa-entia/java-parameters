package ru.multa.entia.parameters.impl.sources;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.parameters.api.ids.Id;
import ru.multa.entia.parameters.api.properties.Property;
import ru.multa.entia.parameters.api.readers.Reader;
import ru.multa.entia.parameters.api.sources.PropertySource;
import ru.multa.entia.parameters.impl.ids.DefaultId;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.utils.Results;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
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

    @SneakyThrows
    @Test
    void shouldCheckRegister_ifDataNotEmpty() {
        AtomicReference<Object> holder = new AtomicReference<>();
        Function<String, Property<String>> propertyFunction = name -> {
            TestStringProperty property = Mockito.mock(TestStringProperty.class);
            Mockito
                    .when(property.set(Mockito.any()))
                    .thenAnswer(new Answer<Result<String>>() {
                        @Override
                        public Result<String> answer(InvocationOnMock invocationOnMock) throws Throwable {
                            holder.set(invocationOnMock.getArgument(0));
                            return null;
                        }
                    });
            Mockito.when(property.getName()).thenReturn(name);

            return property;
        };


        String key = Faker.str_().random();
        String value = Faker.str_().random();
        HashMap<String, Object> data = new HashMap<>() {{
            put(key, value);
        }};

        PropertySource source = DefaultPropertySource.builder().reader(READER).build().value();
        Field field = source.getClass().getDeclaredField("data");
        field.setAccessible(true);
        field.set(source, data);

        Property<String> property = propertyFunction.apply(key);
        source.register(property);

        assertThat(holder.get()).isEqualTo(value);
    }

    @Test
    void shouldCheckUnregister_ifAbsence() {
        Property<String> property = PROPERTY_FUNCTION.apply(Faker.str_().random());
        PropertySource source = DefaultPropertySource.builder().reader(READER).build().value();

        Result<Property<?>> result = source.unregister(property);

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultPropertySource.Code.PROPERTY_ALREADY_UNREGISTERED))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckUnregister() {
        Property<String> property = PROPERTY_FUNCTION.apply(Faker.str_().random());
        PropertySource source = DefaultPropertySource.builder().reader(READER).build().value();
        source.register(property);
        Result<Property<?>> result = source.unregister(property);

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .value(property)
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckUpdating_ifReaderRetFail() {
        Supplier<TestReader> readerSupplier = () -> {
            Result<Map<String, Object>> result = DefaultResultBuilder.<Map<String, Object>>fail(Faker.str_().random());
            TestReader reader = Mockito.mock(TestReader.class);
            Mockito.when(reader.read()).thenReturn(result);

            return reader;
        };

        PropertySource source = DefaultPropertySource.builder().reader(readerSupplier.get()).build().value();
        Result<Object> result = source.update();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultPropertySource.Code.READER_RETURNED_FAIL))
                        .back()
                        .compare()
        ).isTrue();
    }

    @SneakyThrows
    @Test
    void shouldCheckUpdating_ifReaderRetFailOnSecondTime() {
        HashMap<String, Object> expectedData = new HashMap<>() {{
            put(Faker.str_().random(), Faker.str_().random());
        }};
        AtomicBoolean first = new AtomicBoolean(true);

        Supplier<TestReader> readerSupplier = () -> {
            TestReader reader = Mockito.mock(TestReader.class);
            Mockito
                    .when(reader.read())
                    .thenAnswer(new Answer<Result<Map<String, Object>>>() {
                        @Override
                        public Result<Map<String, Object>> answer(InvocationOnMock invocationOnMock) throws Throwable {
                            if (first.get()) {
                                first.set(false);
                                return DefaultResultBuilder.<Map<String, Object>>ok(expectedData);
                            }
                            return DefaultResultBuilder.<Map<String, Object>>fail(Faker.str_().random());
                        }
                    });
            return reader;
        };

        PropertySource source = DefaultPropertySource.builder().reader(readerSupplier.get()).build().value();
        source.update();
        Result<Object> result = source.update();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultPropertySource.Code.READER_RETURNED_FAIL))
                        .back()
                        .compare()
        ).isTrue();

        Field field = source.getClass().getDeclaredField("data");
        field.setAccessible(true);
        Object gotten = field.get(source);

        assertThat(gotten).isEqualTo(expectedData);
    }

    @Test
    void shouldCheckUpdating() {
        String key = Faker.str_().random();
        String value = Faker.str_().random();
        HashMap<String, Object> expectedData = new HashMap<>() {{
            put(key, value);
        }};

        Supplier<TestReader> readerSupplier = () -> {
            Result<Map<String, Object>> result = DefaultResultBuilder.<Map<String, Object>>ok(expectedData);
            TestReader reader = Mockito.mock(TestReader.class);
            Mockito
                    .when(reader.read())
                    .thenReturn(result);
            return reader;
        };

        AtomicReference<Object> holder = new AtomicReference<>();
        Supplier<TestStringProperty> propertySupplier = () -> {
            TestStringProperty property = Mockito.mock(TestStringProperty.class);
            Mockito
                    .when(property.set(Mockito.any()))
                    .thenAnswer(new Answer<Result<String>>() {
                        @Override
                        public Result<String> answer(InvocationOnMock invocationOnMock) throws Throwable {
                            holder.set(invocationOnMock.getArgument(0));
                            return null;
                        }
                    });
            Mockito.when(property.getName()).thenReturn(key);

            return property;
        };

        TestStringProperty property = propertySupplier.get();
        PropertySource source = DefaultPropertySource.builder().reader(readerSupplier.get()).build().value();
        source.register(property);
        source.update();
        Result<Object> result = source.update();

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .value(null)
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();

        assertThat(holder.get()).isEqualTo(value);
    }

    @Test
    void shouldCheckIdGetting() {
        Id expectedId = DefaultId.createIdForFile(Path.of("/opt"));
        Supplier<TestReader> readerSupplier = () -> {
            TestReader reader = Mockito.mock(TestReader.class);
            Mockito.when(reader.getId()).thenReturn(expectedId);

            return reader;
        };

        PropertySource source = DefaultPropertySource.builder().reader(readerSupplier.get()).build().value();

        assertThat(source.getId()).isEqualTo(expectedId);
    }

    private interface TestStringProperty extends Property<String> {}
    private interface TestReader extends Reader<Map<String, Object>> {}
}