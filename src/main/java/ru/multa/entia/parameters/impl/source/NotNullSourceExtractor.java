package ru.multa.entia.parameters.impl.source;

import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

public class NotNullSourceExtractor extends AbstractSourceExtractor<Object> {
    public enum Code {
        NOT_SET,
        IS_NULL
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.NOT_SET, "parameters:source-extractor.not-null:not-set");
        CR.update(Code.IS_NULL, "parameters:source-extractor.not-null:is-null");
    }

    @Override
    public Result<Object> get() {
        return DefaultResultBuilder.<Object>computeFromCodes(
                () -> {return raw;},
                () -> {return !isSet() ? CR.get(Code.NOT_SET) : null;},
                () -> {return raw == null ? CR.get(Code.IS_NULL) : null;}
        );
    }
}
