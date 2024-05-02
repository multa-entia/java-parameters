package ru.multa.entia.parameters.impl.source;

import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

public class DefaultIntegerSourceExtractor extends AbstractSourceExtractor<Integer> {
    public enum Code {
        NOT_SET,
        IS_NULL,
        IS_NOT_INTEGER
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.NOT_SET, "parameters:source-extractor.integer.default:not-set");
        CR.update(Code.IS_NULL, "parameters:source-extractor.integer.default:is-null");
        CR.update(Code.IS_NOT_INTEGER, "parameters:source-extractor.integer.default:is-not-integer");
    }

    @Override
    public Result<Integer> get() {
        if (!isSet()) {
            return DefaultResultBuilder.<Integer>fail(CR.get(Code.NOT_SET));
        } else if (raw == null) {
            return DefaultResultBuilder.<Integer>fail(CR.get(Code.IS_NULL));
        }

        try {
            return DefaultResultBuilder.<Integer>ok(Integer.parseInt(String.valueOf(raw)));
        } catch (NumberFormatException ex) {
            return DefaultResultBuilder.<Integer>fail(CR.get(Code.IS_NOT_INTEGER));
        }
    }
}