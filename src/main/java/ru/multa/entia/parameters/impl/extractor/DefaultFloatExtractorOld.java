package ru.multa.entia.parameters.impl.extractor;

import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

public class DefaultFloatExtractorOld extends AbstractExtractorOld<Float> {
    public enum Code {
        NOT_SET,
        IS_NULL,
        IS_NOT_FLOAT
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.NOT_SET, "parameters:source-extractor.float.default:not-isSet");
        CR.update(Code.IS_NULL, "parameters:source-extractor.float.default:is-null");
        CR.update(Code.IS_NOT_FLOAT, "parameters:source-extractor.float.default:is-not-float");
    }

    public DefaultFloatExtractorOld(final String property) {
        super(property);
    }

    @Override
    public Result<Float> get() {
        if (!isSet()) {
            return DefaultResultBuilder.<Float>fail(CR.get(Code.NOT_SET));
        } else if (raw == null) {
            return DefaultResultBuilder.<Float>fail(CR.get(Code.IS_NULL));
        }

        try {
            return DefaultResultBuilder.<Float>ok(Float.parseFloat(String.valueOf(raw)));
        } catch (NumberFormatException ex) {
            return DefaultResultBuilder.<Float>fail(CR.get(Code.IS_NOT_FLOAT));
        }
    }
}
