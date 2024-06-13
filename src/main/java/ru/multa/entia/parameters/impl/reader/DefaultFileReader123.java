
// TODO: del
//package ru.multa.entia.parameters.impl.reader;
//
//import lombok.Getter;
//import ru.multa.entia.parameters.api.ids.Id;
//import ru.multa.entia.parameters.api.reader.Reader;
//import ru.multa.entia.parameters.api.reader.ReaderResult;
//import ru.multa.entia.parameters.impl.ids.DefaultId;
//import ru.multa.entia.results.api.repository.CodeRepository;
//import ru.multa.entia.results.api.result.Result;
//import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
//import ru.multa.entia.results.impl.result.DefaultResultBuilder;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.Objects;
//
//// TODO: del
//public class DefaultFileReader123 implements Reader {
//    public enum Properties {
//        CONTENT
//    }
//
//    public enum Code {
//        PATH_IS_NULL,
//        CANNOT_READ
//    }
//
//    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
//    static {
//        CR.update(Code.PATH_IS_NULL, "parameters:file-reader.default:path-is-null");
//        CR.update(Code.CANNOT_READ, "parameters:file-reader.default:cannot-read");
//    }
//
//    public static Builder builder() {
//        return new Builder();
//    }
//
//    private final Path path;
//    @Getter
//    private final Id id;
//
//    private DefaultFileReader123(final Path path, final Id id) {
//        this.path = path;
//        this.id = id;
//    }
//
//    @Override
//    public Result<ReaderResult> read() {
//        try {
//            return DefaultResultBuilder.<ReaderResult>ok(
//                    DefaultReaderResult.builder()
//                            .put(Properties.CONTENT.name(), Files.readString(path))
//                            .build()
//            );
//        } catch (IOException ex) {
//            return DefaultResultBuilder.<ReaderResult>fail(CR.get(Code.CANNOT_READ));
//        }
//    }
//
//    public static class Builder {
//        private Path path;
//        private Id id;
//
//        public Builder path(final Path path) {
//            this.path = path;
//            return this;
//        }
//
//        public Builder id(final Id id) {
//            this.id = id;
//            return this;
//        }
//
//        public Result<Reader> build() {
//            if (path == null) {
//                return DefaultResultBuilder.<Reader>fail(CR.get(Code.PATH_IS_NULL));
//            }
//
//            return DefaultResultBuilder.<Reader>ok(
//                    new DefaultFileReader123(path, Objects.requireNonNullElse(id, DefaultId.createIdForFile(path)))
//            );
//        }
//    }
//}