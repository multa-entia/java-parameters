package ru.multa.entia.parameters.impl.extractor;

import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

public class DefaultExtractor extends AbstractExtractor<Object> {
    public enum Code {
        NOT_SET
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.NOT_SET, "parameters:source-extractor.default:not-set");
    }

    public DefaultExtractor(final String property) {
        super(property);
    }

    @Override
    public Result<Object> get() {
        return isSet() ? DefaultResultBuilder.<Object>ok(raw) : DefaultResultBuilder.<Object>fail(CR.get(Code.NOT_SET));
    }
}
