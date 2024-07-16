package ru.multa.entia.parameters.impl.readers;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.parameters.api.ids.Id;
import ru.multa.entia.parameters.api.readers.Reader;
import ru.multa.entia.parameters.impl.ids.DefaultIdOld;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.utils.Results;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultFileReaderTest {
    private static final Path DUMMY_PATH = Path.of("/opt");
    private static final String EXIST_PATH_NAME = "test_reader_file.yml";
    private static final String NON_EXIST_PATH_NAME = "non_exist_file.yml";

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @SneakyThrows
    @Test
    void shouldCheckBuilderPathSetting() {
        DefaultFileReader.Builder builder = DefaultFileReader.builder().path(DUMMY_PATH);

        Field field = builder.getClass().getDeclaredField("path");
        field.setAccessible(true);
        Object gottenPath = field.get(builder);

        assertThat(gottenPath).isEqualTo(DUMMY_PATH);
    }

    @SneakyThrows
    @Test
    void shouldCheckBuilderIdSetting() {
        Id expectedId = Mockito.mock(Id.class);
        DefaultFileReader.Builder builder = DefaultFileReader.builder().id(expectedId);

        Field field = builder.getClass().getDeclaredField("id");
        field.setAccessible(true);
        Object gottenId = field.get(builder);

        assertThat(gottenId).isEqualTo(expectedId);
    }

    @Test
    void shouldCheckBuilding_ifPathNull() {
        Result<Reader<String>> result = DefaultFileReader.builder().build();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultFileReader.Code.PATH_IS_NULL))
                        .back()
                        .compare()
        ).isTrue();
    }

    @SneakyThrows
    @Test
    void shouldCheckBuilding_ifIdNotSet() {
        Path expectedPath = DUMMY_PATH;
        Result<Reader<String>> result = DefaultFileReader.builder().path(expectedPath).build();

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();

        Reader<String> reader = result.value();
        assertThat(reader.getId()).isEqualTo(DefaultIdOld.createIdForFile(expectedPath));

        Field field = reader.getClass().getDeclaredField("path");
        field.setAccessible(true);
        Object gottenPath = field.get(reader);
        assertThat(gottenPath).isEqualTo(expectedPath);
    }

    @SneakyThrows
    @Test
    void shouldCheckBuilding() {
        Path expectedPath = DUMMY_PATH;
        Id expectedId = Mockito.mock(Id.class);
        Result<Reader<String>> result = DefaultFileReader.builder()
                .path(expectedPath)
                .id(expectedId)
                .build();

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();

        Reader<String> reader = result.value();
        assertThat(reader.getId()).isEqualTo(expectedId);

        Field field = reader.getClass().getDeclaredField("path");
        field.setAccessible(true);
        Object gottenPath = field.get(reader);
        assertThat(gottenPath).isEqualTo(expectedPath);
    }

    @Test
    void shouldCheckReading_ifFail() {
        Reader<String> reader = DefaultFileReader.builder().path(Path.of(calculatePath(NON_EXIST_PATH_NAME))).build().value();

        Result<String> result = reader.read();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultFileReader.Code.CANNOT_READ))
                        .back()
                        .compare()
        ).isTrue();
    }

    @SneakyThrows
    @Test
    void shouldCheckReading() {
        Path path = Path.of(calculatePath(EXIST_PATH_NAME));
        Reader<String> reader = DefaultFileReader.builder().path(path).build().value();
        String expectedContent = Files.readString(path);

        Result<String> result = reader.read();

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();

        assertThat(result.value()).isEqualTo(expectedContent);
    }

    private String calculatePath(String name) {
        return System.getProperty("user.dir") +
                "/src/test/resources/" +
                getClass().getPackageName().replace(".", "/") + "/" +
                name;
    }
}