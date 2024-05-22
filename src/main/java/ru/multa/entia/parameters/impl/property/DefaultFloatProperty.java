package ru.multa.entia.parameters.impl.property;

import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

public class DefaultFloatProperty extends AbstractProperty<Float>{
    public enum Code {
        IS_NULL,
        INVALID_RAW
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.IS_NULL, "parameters:float-property.default:is-null");
        CR.update(Code.INVALID_RAW, "parameters:float-property.default:invalid-raw");
    }

    public DefaultFloatProperty(final String name) {
        super(name);
    }

    @Override
    protected Result<Float> checkRaw(final Object raw) {
        if (raw == null) {
            return DefaultResultBuilder.<Float>fail(CR.get(Code.IS_NULL));
        }

        try {
            return DefaultResultBuilder.<Float>ok(Float.parseFloat(String.valueOf(raw)));
        } catch (NumberFormatException ex) {
            return DefaultResultBuilder.<Float>fail(CR.get(Code.INVALID_RAW));
        }
    }
}
