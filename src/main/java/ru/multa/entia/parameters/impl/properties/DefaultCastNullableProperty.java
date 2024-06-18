package ru.multa.entia.parameters.impl.properties;

import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

public class DefaultCastNullableProperty<T> extends DefaultAbstractProperty<T>{
    public enum Code {
        BAD_CAST
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.BAD_CAST, "parameters:nullable-cast-property.default:bad-cast");
    }

    private final Class<T> type;

    public DefaultCastNullableProperty(final String name,
                                       final Class<T> type) {
        super(name);
        this.type = type;
    }

    @Override
    protected Result<T> checkAndGet(final Object object) {
        if (object == null) {
            return DefaultResultBuilder.<T>ok();
        }
        try{
            return DefaultResultBuilder.<T>ok(type.cast(object));
        } catch (ClassCastException ex) {
            return DefaultResultBuilder.<T>fail(CR.get(Code.BAD_CAST));
        }
    }
}
