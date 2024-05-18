package ru.multa.entia.parameters.impl.reader.file;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.parameters.api.reader.file.ReadResult;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.utils.Results;

import java.lang.reflect.Field;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultReaderTest {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    private static final String EXIST_PATH_NAME = "test_reader_file.yml";
    private static final String NON_EXIST_PATH_NAME = "non_exist_file.yml";

    @SneakyThrows
    @Test
    void shouldCheckCreationByPath() {
        Path expectedPath = Path.of(EXIST_PATH_NAME);
        DefaultReader reader = new DefaultReader(expectedPath);

        Field field = reader.getClass().getDeclaredField("path");
        field.setAccessible(true);
        Path gottenPath = (Path) field.get(reader);

        assertThat(gottenPath).isEqualTo(expectedPath);
    }

    @SneakyThrows
    @Test
    void shouldCheckCreationByString() {
        DefaultReader reader = new DefaultReader(EXIST_PATH_NAME);

        Field field = reader.getClass().getDeclaredField("path");
        field.setAccessible(true);
        Path gottenPath = (Path) field.get(reader);

        assertThat(gottenPath).isEqualTo(Path.of(EXIST_PATH_NAME));
    }

    @Test
    void shouldCheckReading_ifFail() {
        Result<ReadResult> result = new DefaultReader(calculatePath(NON_EXIST_PATH_NAME)).read();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(CR.get(DefaultReader.Code.CANNOT_READ))
                        .back()
                        .compare()
        ).isTrue();
    }

    @SneakyThrows
    @Test
    void shouldCheckReading() {
        Path path = Path.of(calculatePath(EXIST_PATH_NAME));
        String content = Files.readString(path);
        BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);

        Supplier<ReadResult> readResultSupplier = () -> {
            ReadResult readResult = Mockito.mock(ReadResult.class);
            Mockito.when(readResult.content()).thenReturn(content);
            Mockito.when(readResult.path()).thenReturn(path);
            Mockito.when(readResult.attributes()).thenReturn(attributes);

            return readResult;
        };

        Result<ReadResult> result = new DefaultReader(path).read();

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .value(readResultSupplier.get())
                        .seedsComparator()
                        .isNull()
                        .compare()
        ).isTrue();
    }

    private String calculatePath(final String name) {
        return System.getProperty("user.dir") +
                "/src/test/resources/" +
                getClass().getPackageName().replace(".", "/") + "/" +
                name;
    }
}