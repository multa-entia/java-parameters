package ru.multa.entia.parameters.impl.reader;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.parameters.api.ids.Id;
import ru.multa.entia.parameters.api.reader.Reader;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.utils.Results;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultYmlReaderTest {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @SneakyThrows
    @Test
    void shouldCheckBuilder_readerSettingAsReader() {
        TestTextReader expected = Mockito.mock(TestTextReader.class);

        DefaultYmlReader.Builder builder = DefaultYmlReader.builder().textReader(expected);
        Field field = builder.getClass().getDeclaredField("textReader");
        field.setAccessible(true);
        Object gotten = field.get(builder);

        assertThat(gotten).isEqualTo(expected);
    }

    @SneakyThrows
    @Test
    void shouldCheckBuilder_readerSettingAsPath() {
        Path expected = Path.of("/opt");

        DefaultYmlReader.Builder builder = DefaultYmlReader.builder().path(expected);
        Field field = builder.getClass().getDeclaredField("path");
        field.setAccessible(true);
        Object gotten = field.get(builder);

        assertThat(gotten).isEqualTo(expected);
    }

    @SneakyThrows
    @Test
    void shouldCheckBuilder_idSetting() {
        Id expectedId = Mockito.mock(Id.class);
        DefaultYmlReader.Builder builder = DefaultYmlReader.builder().id(expectedId);

        Field field = builder.getClass().getDeclaredField("id");
        field.setAccessible(true);
        Object gotten = field.get(builder);

        assertThat(gotten).isEqualTo(expectedId);
    }

    @Test
    void shouldCheckBuilding_ifReaderAndPathNotSet() {
        Result<Reader<Map<String, Object>>> result = DefaultYmlReader.builder().build();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultYmlReader.Code.READER_OR_PATH_NOT_SET))
                        .back()
                        .compare()
        ).isTrue();
    }

    @SneakyThrows
    @Test
    void shouldCheckBuilding_ifOnlyPathSet() {
        Path expected = Path.of("/opt");
        Reader<Map<String, Object>> reader = DefaultYmlReader.builder().path(expected).build().value();

        Field field = reader.getClass().getDeclaredField("textReader");
        field.setAccessible(true);
        Object gottenTextReader = field.get(reader);
        field = gottenTextReader.getClass().getDeclaredField("path");
        field.setAccessible(true);
        Object gotten = field.get(gottenTextReader);

        assertThat(gotten).isEqualTo(expected);
    }

    @SneakyThrows
    @Test
    void shouldCheckBuilding() {
        TestTextReader expectedTestReader = Mockito.mock(TestTextReader.class);
        Reader<Map<String, Object>> reader = DefaultYmlReader.builder().textReader(expectedTestReader).build().value();

        Field field = reader.getClass().getDeclaredField("textReader");
        field.setAccessible(true);
        Object gotten = field.get(reader);

        assertThat(gotten).isEqualTo(expectedTestReader);
    }

    @Test
    void shouldCheckIdGetting() {
        Id expectedId = Mockito.mock(Id.class);
        Reader<Map<String, Object>> reader = DefaultYmlReader.builder().path(Path.of("/opt")).id(expectedId).build().value();

        assertThat(reader.getId()).isEqualTo(expectedId);
    }

    @Test
    void shouldCheckReading_ifTextReaderRetFail() {
        Supplier<Reader<String>> readerSupplier = () -> {
            Result<String> result = DefaultResultBuilder.<String>fail(Faker.str_().random());
            TestTextReader reader = Mockito.mock(TestTextReader.class);
            Mockito.when(reader.read()).thenReturn(result);

            return reader;
        };

        Reader<Map<String, Object>> reader = DefaultYmlReader.builder().textReader(readerSupplier.get()).build().value();
        Result<Map<String, Object>> result = reader.read();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultYmlReader.Code.TEXT_READER_RET_FAIL))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckReading_whenSyntaxError() {
        String text =
                """
                intValue: 123doubleValue: 123.45
                """;
        Supplier<Reader<String>> textReaderSupplier = () -> {
            Result<String> result = DefaultResultBuilder.<String>ok(text);
            TestTextReader reader = Mockito.mock(TestTextReader.class);
            Mockito.when(reader.read()).thenReturn(result);

            return reader;
        };

        Reader<Map<String, Object>> reader = DefaultYmlReader.builder().textReader(textReaderSupplier.get()).build().value();
        Result<Map<String, Object>> result = reader.read();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultYmlReader.Code.SYNTAX_ERROR))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckReading() {
        String key0 = "intValue";
        String key1 = "doubleValue";
        int value0 = 123;
        double value1 = 123.45;
        String template =
                """
                %s: %s
                %s: %s
                """;
        String text = String.format(template, key0, value0, key1, value1);

        Supplier<Reader<String>> textReaderSupplier = () -> {
            Result<String> result = DefaultResultBuilder.<String>ok(text);
            TestTextReader reader = Mockito.mock(TestTextReader.class);
            Mockito.when(reader.read()).thenReturn(result);

            return reader;
        };

        Reader<Map<String, Object>> reader = DefaultYmlReader.builder().textReader(textReaderSupplier.get()).build().value();
        Result<Map<String, Object>> result = reader.read();

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .value(new HashMap<>(){{
                            put(key0, value0);
                            put(key1, value1);
                        }})
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();
    }

    private interface TestTextReader extends Reader<String> {}
}