package ru.multa.entia.parameters.impl.property;

import ru.multa.entia.parameters.api.property.Property;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

public class DefaultIntegerProperty extends AbstractProperty<Integer> {
    public enum Code {
        PARSE_ERROR
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.PARSE_ERROR, "parameters:int-property.default:parse-error");
    }

    public DefaultIntegerProperty(final Property<Object> innerProperty) {
        super(innerProperty);
    }

    @Override
    public Result<Integer> get() {
        Result<Object> result = innerProperty.get();
        if (!result.ok()) {
            return DefaultResultBuilder.<Integer>fail(result.seed());
        }

        try {
            return DefaultResultBuilder.<Integer>ok(Integer.parseInt(String.valueOf(result.value())));
        } catch (NumberFormatException ex) {
            return DefaultResultBuilder.<Integer>fail(CR.get(Code.PARSE_ERROR));
        }
    }
}
