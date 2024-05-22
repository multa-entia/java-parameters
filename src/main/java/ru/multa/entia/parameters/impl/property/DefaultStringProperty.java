package ru.multa.entia.parameters.impl.property;

import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

public class DefaultStringProperty extends AbstractProperty<String>{
    public enum Code {
        IS_NULL
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(DefaultIntegerProperty.Code.IS_NULL, "parameters:str-property.default:is-null");
    }

    public DefaultStringProperty(final String name) {
        super(name);
    }

    @Override
    protected Result<String> checkRaw(final Object raw) {
        return DefaultResultBuilder.<String>computeFromCodes(
                () -> {return String.valueOf(raw);},
                () -> {return raw == null ? CR.get(Code.IS_NULL) : null;}
        );
    }
}
