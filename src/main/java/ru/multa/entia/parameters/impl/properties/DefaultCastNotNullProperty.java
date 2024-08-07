package ru.multa.entia.parameters.impl.properties;

import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

public class DefaultCastNotNullProperty<T> extends DefaultCastNullableProperty<T> {
    public enum Code {
        IS_NULL
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.IS_NULL, "parameters:not-null-cast-property.default:is-null");
    }

    public DefaultCastNotNullProperty(final String name,
                                      final Class<T> type) {
        super(name, type);
    }

    @Override
    protected Result<T> checkAndGet(final Object object) {
        Result<T> result = super.checkAndGet(object);
        return result.ok() && result.value() == null
                ? DefaultResultBuilder.<T>fail(CR.get(Code.IS_NULL))
                : result;
    }
}
