package ru.multa.entia.parameters.impl.extractor;

import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

public class DefaultStringExtractor extends AbstractExtractor<String> {
    public enum Code {
        NOT_SET,
        IS_NULL,
        IS_NOT_STRING
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.NOT_SET, "parameters:source-extractor.string.default:not-set");
        CR.update(Code.IS_NULL, "parameters:source-extractor.string.default:is-null");
        CR.update(Code.IS_NOT_STRING, "parameters:source-extractor.string.default:is-not-string");
    }

    @Override
    public Result<String> get() {
        return DefaultResultBuilder.<String>computeFromCodes(
                () -> {return String.valueOf(raw);},
                () -> {return !isSet() ? CR.get(Code.NOT_SET) : null;},
                () -> {return raw == null ? CR.get(Code.IS_NULL) : null;},
                () -> {return !raw.getClass().equals(String.class) ? CR.get(Code.IS_NOT_STRING) : null;}
        );
    }
}
