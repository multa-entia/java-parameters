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
//import java.io.IOException;
//import java.lang.reflect.Field;
//import java.nio.file.*;
//import java.nio.file.attribute.BasicFileAttributes;
//import java.util.concurrent.atomic.AtomicReference;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class DefaultFileReaderOldTest {
//    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
//    private static final String EXIST_PATH_NAME = "test_reader_file.yml";
//    private static final String NON_EXIST_PATH_NAME = "non_exist_file.yml";
//
//    // TODO: temp + del
////    @Test
////    void test() {
////
////        Path path = Path.of(calculatePath(EXIST_PATH_NAME));
////        try {
////            Object creationTime = Files.getAttribute(path, "creationTime");
////            System.out.println(creationTime);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////
////        try {
////            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
////            System.out.println(attributes);
////            System.out.println("creationTime: " + attributes.creationTime());
////            System.out.println("fileKey: " + attributes.fileKey());
////            System.out.println("isDirectory: " + attributes.isDirectory());
////            System.out.println("isOther: " + attributes.isOther());
////            System.out.println("isRegularFile: " + attributes.isRegularFile());
////            System.out.println("isSymbolicLink: " + attributes.isSymbolicLink());
////            System.out.println("lastAccessTime: " + attributes.lastAccessTime());
////            System.out.println("lastModifiedTime: " + attributes.lastModifiedTime());
////            System.out.println("size: " + attributes.size());
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////    }
//
//    @SneakyThrows
//    @Test
//    void shouldCheckCreationByPath() {
//        Path expectedPath = Path.of(EXIST_PATH_NAME);
//        DefaultLiveFileReaderOld reader = new DefaultLiveFileReaderOld(expectedPath);
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
//        DefaultLiveFileReaderOld reader = new DefaultLiveFileReaderOld(EXIST_PATH_NAME);
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
//        Result<String> result = new DefaultLiveFileReaderOld(calculatePath(NON_EXIST_PATH_NAME)).read();
//
//        assertThat(
//                Results.comparator(result)
//                        .isFail()
//                        .seedsComparator()
//                        .code(CR.get(DefaultLiveFileReaderOld.Code.CANNOT_READ))
//                        .back()
//                        .compare()
//        ).isTrue();
//    }
//
//    @SneakyThrows
//    @Test
//    void shouldCheckReading() {
//        Path path = Path.of(calculatePath(EXIST_PATH_NAME));
//        String expectedContent = Files.readString(path);
//
//        Result<String> result = new DefaultLiveFileReaderOld(path).read();
//
//        assertThat(
//                Results.comparator(result)
//                        .isSuccess()
//                        .value(expectedContent)
//                        .seedsComparator()
//                        .isNull()
//                        .compare()
//        ).isTrue();
//    }
//
//    private String calculatePath(String name) {
//        return System.getProperty("user.dir") +
//                "/src/test/resources/" +
//                getClass().getPackageName().replace(".", "/") + "/" +
//                name;
//    }
//}