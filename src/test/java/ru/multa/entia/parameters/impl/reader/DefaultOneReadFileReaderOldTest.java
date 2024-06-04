// TODO: del
//package ru.multa.entia.parameters.impl.reader;
//
//import lombok.SneakyThrows;
//import org.junit.jupiter.api.Test;
//import ru.multa.entia.results.api.repository.CodeRepository;
//import ru.multa.entia.results.api.result.Result;
//import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
//import ru.multa.entia.results.utils.Results;
//
//import java.lang.reflect.Field;
//import java.nio.file.Files;
//import java.nio.file.Path;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class DefaultOneReadFileReaderOldTest {
//
//    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
//    private static final String EXIST_PATH_NAME = "test_reader_file.yml";
//    private static final String NON_EXIST_PATH_NAME = "non_exist_file.yml";
//
//    @SneakyThrows
//    @Test
//    void shouldCheckCreationByPath() {
//        Path expectedPath = Path.of(EXIST_PATH_NAME);
//        DefaultOneReadFileReaderOld reader = new DefaultOneReadFileReaderOld(expectedPath);
//
//        Field field = reader.getClass().getDeclaredField("path");
//        field.setAccessible(true);
//        Path gottenPath = (Path) field.get(reader);
//
//        assertThat(gottenPath).isEqualTo(expectedPath);
//    }
//
//    @SneakyThrows
//    @Test
//    void shouldCheckCreationByString() {
//        DefaultOneReadFileReaderOld reader = new DefaultOneReadFileReaderOld(EXIST_PATH_NAME);
//
//        Field field = reader.getClass().getDeclaredField("path");
//        field.setAccessible(true);
//        Path gottenPath = (Path) field.get(reader);
//
//        assertThat(gottenPath).isEqualTo(Path.of(EXIST_PATH_NAME));
//    }
//
//    @Test
//    void shouldCheckReading_ifFail() {
//        Result<String> result = new DefaultOneReadFileReaderOld(calculatePath(NON_EXIST_PATH_NAME)).read();
//
//        assertThat(
//                Results.comparator(result)
//                        .isFail()
//                        .seedsComparator()
//                        .code(CR.get(DefaultOneReadFileReaderOld.Code.CANNOT_READ))
//                        .back()
//                        .compare()
//        ).isTrue();
//    }
//
//    @SuppressWarnings("unchecked")
//    @SneakyThrows
//    @Test
//    void shouldCheckReading() {
//        Path path = Path.of(calculatePath(EXIST_PATH_NAME));
//        String expectedContent = Files.readString(path);
//
//        DefaultOneReadFileReaderOld reader = new DefaultOneReadFileReaderOld(path);
//        Result<String> result = reader.read();
//        Field field = reader.getClass().getDeclaredField("result");
//        field.setAccessible(true);
//        Result<String> gottenResult = (Result<String>) field.get(reader);
//
//        assertThat(
//                Results.comparator(result)
//                        .isSuccess()
//                        .value(expectedContent)
//                        .seedsComparator()
//                        .isNull()
//                        .compare()
//        ).isTrue();
//        assertThat(Results.equal(result, gottenResult)).isTrue();
//    }
//
//    private String calculatePath(String name) {
//        return System.getProperty("user.dir") +
//                "/src/test/resources/" +
//                getClass().getPackageName().replace(".", "/") + "/" +
//                name;
//    }
//}