
// TODO: del
//package ru.multa.entia.parameters.impl.reader.file;
//
//import lombok.EqualsAndHashCode;
//import lombok.Getter;
//import ru.multa.entia.parameters.api.ids.Id;
//import ru.multa.entia.parameters.api.reader.file.ReadResultOld;
//import ru.multa.entia.parameters.api.reader.file.ReaderOld;
//import ru.multa.entia.parameters.impl.ids.DefaultId;
//import ru.multa.entia.parameters.impl.ids.Ids;
//import ru.multa.entia.results.api.repository.CodeRepository;
//import ru.multa.entia.results.api.result.Result;
//import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
//import ru.multa.entia.results.impl.result.DefaultResultBuilder;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.attribute.BasicFileAttributes;
//
//// TODO: del
//@EqualsAndHashCode
//public class DefaultReaderOld implements ReaderOld {
//    public enum Code {
//        CANNOT_READ
//    }
//
//    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
//    static {
//        CR.update(Code.CANNOT_READ, "parameters:file-reader.default:cannot-read");
//    }
//
//    private final Path path;
//    @Getter
//    private final Id id;
//
//    public DefaultReaderOld(final String path) {
//        this(Path.of(path));
//    }
//
//    public DefaultReaderOld(final Path path) {
//        this.path = path;
//        this.id = new DefaultId(Ids.FILE, path.hashCode());
//    }
//
//    @Override
//    public Result<ReadResultOld> read() {
//        try {
//            return DefaultResultBuilder.<ReadResultOld>ok(new DefaultReadResultOld(
//                    Files.readString(path),
//                    path,
//                    Files.readAttributes(path, BasicFileAttributes.class))
//            );
//        } catch (IOException e) {
//            return DefaultResultBuilder.<ReadResultOld>fail(CR.get(Code.CANNOT_READ), e.getMessage());
//        }
//    }
//}
