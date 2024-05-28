package ru.multa.entia.parameters.impl.source;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.parameters.api.reader.file.ReadResult;
import ru.multa.entia.parameters.api.reader.file.Reader;
import ru.multa.entia.parameters.api.source.SourceAdapter;
import ru.multa.entia.parameters.impl.reader.file.DefaultReadResult;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.utils.Results;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultTextualPropertySourceTest {
    private static final Function<String, Reader> READER_FUNCTION = content -> {
        Result<ReadResult> result = DefaultResultBuilder.<ReadResult>ok(new DefaultReadResult(content, null, null));
        Reader reader = Mockito.mock(Reader.class);
        Mockito.when(reader.read()).thenReturn(result);

        return reader;
    };

    private static final Function<Map<String, Object>, TestAdapter> ADAPTER_FUNCTION = map -> {
        Result<Map<String, Object>> result = DefaultResultBuilder.<Map<String, Object>>ok(map);
        TestAdapter adapter = Mockito.mock(TestAdapter.class);
        Mockito.when(adapter.adapt(Mockito.any())).thenReturn(result);

        return adapter;
    };

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @Test
    void shouldCheckUpdating_ifReaderIsNull() {
        Result<Object> result = new DefaultTextualPropertySource(null).update();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultTextualPropertySource.Code.READER_IS_NULL))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckUpdating_ifReaderReturnedFail() {
        String expectedCode = Faker.str_().random();
        Supplier<Reader> readerSupplier = () -> {
            Result<ReadResult> result = DefaultResultBuilder.<ReadResult>fail(expectedCode);
            Reader reader = Mockito.mock(Reader.class);
            Mockito.when(reader.read()).thenReturn(result);

            return reader;
        };

        Result<Object> result = new DefaultTextualPropertySource(readerSupplier.get()).update();

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
    void shouldCheckUpdating_ifAdapterReturnedFail() {
        String expectedCode = Faker.str_().random();
        Supplier<TestAdapter> adapterSupplier = () -> {
            Result<Map<String, Object>> result = DefaultResultBuilder.<Map<String, Object>>fail(expectedCode);
            TestAdapter adapter = Mockito.mock(TestAdapter.class);
            Mockito.when(adapter.adapt(Mockito.any())).thenReturn(result);

            return adapter;
        };

        Result<Object> result = new DefaultTextualPropertySource(
                READER_FUNCTION.apply(Faker.str_().random()),
                adapterSupplier.get()
        ).update();

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

    @SneakyThrows
    @Test
    void shouldCheckUpdating() {
        int quantity = Faker.int_().between(10, 20);
        HashMap<String, Object> expected = new HashMap<>();
        for (int i = 0; i < quantity; i++) {
            expected.put(Faker.str_().random(), Faker.str_().random());
        }

        Supplier<TestAdapter> adapterSupplier = () -> {
            Result<Map<String, Object>> result = DefaultResultBuilder.<Map<String, Object>>ok(expected);
            TestAdapter adapter = Mockito.mock(TestAdapter.class);
            Mockito.when(adapter.adapt(Mockito.any())).thenReturn(result);

            return adapter;
        };

        DefaultTextualPropertySource source = new DefaultTextualPropertySource(
                READER_FUNCTION.apply(Faker.str_().random()),
                adapterSupplier.get()
        );
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

        Field field = source.getClass().getDeclaredField("rawProperties");
        field.setAccessible(true);
        Object gotten = field.get(source);

        assertThat(gotten).isEqualTo(expected);
    }

    @Test
    void shouldCheckGetting_ifItIsNotInit() {
        DefaultTextualPropertySource source = new DefaultTextualPropertySource(
                READER_FUNCTION.apply(Faker.str_().random()),
                ADAPTER_FUNCTION.apply(Map.of())
        );

        Result<Object> result = source.get(Faker.str_().random());

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultTextualPropertySource.Code.NOT_INIT))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckGetting_ifItIsAbsence() {
        String nonExistPropertyName = Faker.str_().random();
        DefaultTextualPropertySource source = new DefaultTextualPropertySource(
                READER_FUNCTION.apply(Faker.str_().random()),
                ADAPTER_FUNCTION.apply(Map.of())
        );
        source.update();

        Result<Object> result = source.get(nonExistPropertyName);

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultTextualPropertySource.Code.NOT_PRESENT))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckGetting() {
        String propertyName = Faker.str_().random();
        Object property = Faker.str_().random();
        DefaultTextualPropertySource source = new DefaultTextualPropertySource(
                READER_FUNCTION.apply(Faker.str_().random()),
                ADAPTER_FUNCTION.apply(Map.of(propertyName, property))
        );
        source.update();

        Result<Object> result = source.get(propertyName);

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

    private interface TestAdapter extends SourceAdapter<String, Map<String, Object>> {}
}