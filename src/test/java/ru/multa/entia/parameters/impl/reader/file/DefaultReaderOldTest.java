// TODO: del
//package ru.multa.entia.parameters.impl.reader.file;
//
//import lombok.SneakyThrows;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import ru.multa.entia.parameters.api.ids.Id;
//import ru.multa.entia.parameters.api.reader.file.ReadResultOld;
//import ru.multa.entia.parameters.impl.ids.Ids;
//import ru.multa.entia.results.api.repository.CodeRepository;
//import ru.multa.entia.results.api.result.Result;
//import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
//import ru.multa.entia.results.utils.Results;
//
//import java.lang.reflect.Field;
//import java.nio.file.*;
//import java.nio.file.attribute.BasicFileAttributes;
//import java.util.UUID;
//import java.util.function.Supplier;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class DefaultReaderOldTest {
//    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
//    private static final String EXIST_PATH_NAME = "test_reader_file.yml";
//    private static final String NON_EXIST_PATH_NAME = "non_exist_file.yml";
//
//    @SneakyThrows
//    @Test
//    void shouldCheckCreationByPath() {
//        Path expectedPath = Path.of(EXIST_PATH_NAME);
//        DefaultReaderOld reader = new DefaultReaderOld(expectedPath);
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
//        DefaultReaderOld reader = new DefaultReaderOld(EXIST_PATH_NAME);
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
//        Result<ReadResultOld> result = new DefaultReaderOld(calculatePath(NON_EXIST_PATH_NAME)).read();
//
//        assertThat(
//                Results.comparator(result)
//                        .isFail()
//                        .seedsComparator()
//                        .code(CR.get(DefaultReaderOld.Code.CANNOT_READ))
//                        .back()
//                        .compare()
//        ).isTrue();
//    }
//
//    @SneakyThrows
//    @Test
//    void shouldCheckReading() {
//        Path path = Path.of(calculatePath(EXIST_PATH_NAME));
//        String content = Files.readString(path);
//        BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
//
//        Supplier<ReadResultOld> readResultSupplier = () -> {
//            ReadResultOld readResultOld = Mockito.mock(ReadResultOld.class);
//            Mockito.when(readResultOld.content()).thenReturn(content);
//            Mockito.when(readResultOld.path()).thenReturn(path);
//            Mockito.when(readResultOld.attributes()).thenReturn(attributes);
//
//            return readResultOld;
//        };
//
//        Result<ReadResultOld> result = new DefaultReaderOld(path).read();
//
//        assertThat(
//                Results.comparator(result)
//                        .isSuccess()
//                        .value(readResultSupplier.get())
//                        .seedsComparator()
//                        .isNull()
//                        .compare()
//        ).isTrue();
//    }
//
//    @Test
//    void shouldCheckIdGetting() {
//        Ids type = Ids.FILE;
//        Path path = Path.of(calculatePath(EXIST_PATH_NAME));
//        UUID expected = new UUID(type.getValue(), path.hashCode());
//
//        Id gotten = new DefaultReaderOld(path).getId();
//
//        assertThat(gotten.get()).isEqualTo(expected);
//    }
//
//    private String calculatePath(final String name) {
//        return System.getProperty("user.dir") +
//                "/src/test/resources/" +
//                getClass().getPackageName().replace(".", "/") + "/" +
//                name;
//    }
//}