package ru.multa.entia.parameters.impl.reader;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.parameters.api.reader.Reader;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.utils.Results;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Map;

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

    @Test
    void shouldCheckBuilding_ifOnlyPathSet() {

    }

    @Test
    void shouldCheckBuilding() {

    }

    @Test
    void shouldCheckIdGetting() {

    }

    @Test
    void shouldCheckReading_ifTextReaderRetFail() {

    }

    @Test
    void shouldCheckReading_whenSyntaxError() {

    }

    @Test
    void shouldCheckReading() {

    }

    private interface TestTextReader extends Reader<String> {}
}