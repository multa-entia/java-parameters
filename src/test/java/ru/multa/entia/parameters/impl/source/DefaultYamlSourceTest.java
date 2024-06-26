package ru.multa.entia.parameters.impl.source;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.parameters.api.extractor.Extractor;
import ru.multa.entia.parameters.api.reader.Reader;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.utils.Results;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultYamlSourceTest {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    private interface MockTestExtractor extends Extractor<String> {}
    private static final Supplier<Extractor<String>> EXTRACTOR_SUPPLIER = () -> {
        return Mockito.mock(MockTestExtractor.class);
    };

    @Test
    void shouldCheckGetting_ifReaderNull() {
        DefaultYamlSource source = new DefaultYamlSource(null);
        Result<Object> result = source.get(EXTRACTOR_SUPPLIER.get());

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultYamlSource.Code.READER_NOT_SET))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckGetting_ifReaderReturnFail() {
        Result<String> cause = DefaultResultBuilder.<String>fail(Faker.str_().random());
        Supplier<Reader> readerSupplier = () -> {
            Reader reader = Mockito.mock(Reader.class);
            Mockito.when(reader.read()).thenReturn(cause);

            return reader;
        };

        DefaultYamlSource source = new DefaultYamlSource(readerSupplier.get());
        Result<Object> result = source.get(EXTRACTOR_SUPPLIER.get());

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .causes(List.of(cause))
                        .seedsComparator()
                        .code(CR.get(DefaultYamlSource.Code.READER_RETURN_FAIL))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckGetting_ifSyntaxError() {
        String raw =
"""
intValue: 123floatValue: 123.45
""";

        Result<String> readerResult = DefaultResultBuilder.<String>ok(raw);
        Supplier<Reader> readerSupplier = () -> {
            Reader reader = Mockito.mock(Reader.class);
            Mockito.when(reader.read()).thenReturn(readerResult);

            return reader;
        };

        DefaultYamlSource source = new DefaultYamlSource(readerSupplier.get());
        Result<Object> result = source.get(EXTRACTOR_SUPPLIER.get());

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultYamlSource.Code.SYNTAX_ERROR))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckGetting() {
        String nonExistKey = "nonExist";
        String intKey = "int_value";
        String doubleKey = "float_value";
        String booleanKey = "boolean_key";
        String listKey = "list_value";
        Integer intValue = Faker.int_().random();
        double doubleValue = ((double) Faker.int_().between(0, 1000)) / 100.0;
        boolean booleanValue = Faker.int_().random(0, 1000) % 2 == 0;
        String item0 = "_" + Faker.int_().random();
        String item1 = "_" + Faker.int_().random();
        String item2 = "_" + Faker.int_().random();
        String template =
                """
                %s: %s
                %s: %s
                %s: %s
                %s: [%s, %s, %s]
                """;
        String raw = String.format(
                template,
                intKey, intValue,
                doubleKey, doubleValue,
                booleanKey, booleanValue,
                listKey, item0, item1, item2);

        Result<String> readerResult = DefaultResultBuilder.<String>ok(raw);
        Supplier<Reader> readerSupplier = () -> {
            Reader reader = Mockito.mock(Reader.class);
            Mockito.when(reader.read()).thenReturn(readerResult);

            return reader;
        };

        DefaultYamlSource source = new DefaultYamlSource(readerSupplier.get());

        TestExtractor nonExistExtractor = new TestExtractor(nonExistKey);
        TestExtractor intKeyExtractor = new TestExtractor(intKey);
        TestExtractor doubleKeyExtractor = new TestExtractor(doubleKey);
        TestExtractor booleanKeyExtractor = new TestExtractor(booleanKey);
        TestExtractor listKeyExtractor = new TestExtractor(listKey);

        Result<Object> nonExistResult = source.get(nonExistExtractor);
        Result<Object> intKeyResult = source.get(intKeyExtractor);
        Result<Object> doubleKeyResult = source.get(doubleKeyExtractor);
        Result<Object> booleanKeyResult = source.get(booleanKeyExtractor);
        Result<Object> listKeyResult = source.get(listKeyExtractor);

        assertThat(Results.comparator(intKeyResult).isSuccess().seedsComparator().isNull().back().compare()).isTrue();
        assertThat(Results.comparator(doubleKeyResult).isSuccess().seedsComparator().isNull().back().compare()).isTrue();
        assertThat(Results.comparator(booleanKeyResult).isSuccess().seedsComparator().isNull().back().compare()).isTrue();
        assertThat(Results.comparator(listKeyResult).isSuccess().seedsComparator().isNull().back().compare()).isTrue();

        assertThat(
                Results.comparator(nonExistResult)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultYamlSource.Code.PROPERTY_NOT_EXIST))
                        .back()
                        .compare()
        ).isTrue();
        assertThat(
                Results.comparator(intKeyExtractor.get())
                        .isSuccess()
                        .value(intValue)
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();
        assertThat(
                Results.comparator(doubleKeyExtractor.get())
                        .isSuccess()
                        .value(doubleValue)
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();
        assertThat(
                Results.comparator(booleanKeyExtractor.get())
                        .isSuccess()
                        .value(booleanValue)
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();
        assertThat(
                Results.comparator(listKeyExtractor.get())
                        .isSuccess().
                        value(List.of(item0, item1, item2))
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    void shouldCheckGetting_secondTime() {
        String intKey = "int_value";
        Integer intValue = Faker.int_().random();
        String template =
                """
                %s: %s
                """;
        String raw = String.format(
                template,
                intKey, intValue);

        Result<String> readerResult = DefaultResultBuilder.<String>ok(raw);
        Supplier<Reader> readerSupplier = () -> {
            Reader reader = Mockito.mock(Reader.class);
            Mockito.when(reader.read()).thenReturn(readerResult);

            return reader;
        };

        DefaultYamlSource source = new DefaultYamlSource(readerSupplier.get());
        source.get(new TestExtractor(intKey));

        Field field = source.getClass().getDeclaredField("data");
        field.setAccessible(true);

        Map<String, Object> gottenData = (Map<String, Object>) field.get(source);

        assertThat(gottenData).containsKey(intKey);
        assertThat(gottenData.get(intKey)).isEqualTo(intValue);
    }

    @RequiredArgsConstructor
    private static class TestExtractor implements Extractor<Object> {
        @Getter
        private final String property;

        private Object object;

        @Override
        public void set(Object object) {
            this.object = object;
        }

        @Override
        public Result<Object> get() {
            return DefaultResultBuilder.<Object>ok(object);
        }
    }
}