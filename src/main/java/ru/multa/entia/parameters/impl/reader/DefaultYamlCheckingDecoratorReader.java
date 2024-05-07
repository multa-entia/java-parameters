package ru.multa.entia.parameters.impl.reader;

import ru.multa.entia.parameters.api.reader.Reader;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.function.Function;

public class DefaultYamlCheckingDecoratorReader implements Reader {
    public enum Code {
        INNER_READER_IS_NOT_SET,
        SYNTAX_ERROR,
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.INNER_READER_IS_NOT_SET, "parameters:reader.yaml-checking.default:reader-not-set");
        CR.update(Code.SYNTAX_ERROR, "parameters:reader.yaml-checking.default:syntax-error");
    }

    private final Reader reader;
    private final boolean ignore;
    private final Checker[] checkers;

    public DefaultYamlCheckingDecoratorReader(final Reader reader,
                                              final boolean ignore,
                                              final Checker... checkers) {
        this.reader = reader;
        this.ignore = ignore;
        this.checkers = checkers.length == 0 ? getDefaultCheckers() : checkers;
    }

    @Override
    public Result<String> read() {
        if (reader == null) {
            return DefaultResultBuilder.<String>fail(CR.get(Code.INNER_READER_IS_NOT_SET));
        }

        Result<String> innerReaderResult = reader.read();
        if (!innerReaderResult.ok()) {
            return new DefaultResultBuilder<String>()
                    .success(false)
                    .seed(innerReaderResult.seed())
                    .causes(innerReaderResult)
                    .build();
        }

        // TODO: !!!
        return null;
    }

    private Checker[] getDefaultCheckers() {
        return new Checker[]{
                new MoreOneColonChecker(),
                new NotClosedSquareBraceChecker()
        };
    }

    public interface Checker extends Function<String, Result<String>> {}

    public static class MoreOneColonChecker implements Checker {
        public enum Code {
            MORE_ONE_COLON
        }

        private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
        static {
            CR.update(Code.MORE_ONE_COLON, "parameters:reader.checker:more-one-colon");
        }

        @Override
        public Result<String> apply(final String line) {
            String filterLine = line.replaceAll("\".+\"", "");
            int colonQuantity = filterLine.length() - filterLine.replaceAll(":", "").length();

            return colonQuantity > 1
                    ? DefaultResultBuilder.<String>fail(CR.get(Code.MORE_ONE_COLON))
                    : DefaultResultBuilder.<String>ok(line);
        }
    }

    public static class NotClosedSquareBraceChecker implements Checker {
        public enum Code {
            NOT_CLOSED_SQUARE_BRACE
        }

        private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
        static {
            CR.update(Code.NOT_CLOSED_SQUARE_BRACE, "parameters:reader.checker:not-closed-square-brace");
        }

        @Override
        public Result<String> apply(final String line) {
            String[] split = line.split(":");
            if (split.length == 2) {
                String part = split[1].trim();
                if (part.length() > 1 && part.charAt(0) == '[' && part.charAt(part.length() - 1) != ']') {
                    return DefaultResultBuilder.<String>fail(CR.get(Code.NOT_CLOSED_SQUARE_BRACE));
                }
            }
            return DefaultResultBuilder.<String>ok(line);
        }
    }
}
