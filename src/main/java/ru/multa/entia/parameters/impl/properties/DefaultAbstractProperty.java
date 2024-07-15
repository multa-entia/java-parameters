package ru.multa.entia.parameters.impl.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.multa.entia.parameters.api.properties.Property;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

@RequiredArgsConstructor
public abstract class DefaultAbstractProperty<T> implements Property<T> {
    public enum Code {
        IS_NOT_SET
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.IS_NOT_SET, "parameters:abstract-property.default:is-not-set");
    }

    @Getter
    private final String name;

    protected Result<T> data = DefaultResultBuilder.<T>fail(CR.get(Code.IS_NOT_SET));

    @Override
    public Result<T> set(final Object object) {
        Result<T> result = checkAndGet(object);
        if (result.ok()) {
            data = result;
        }

        return result;
    }

    @Override
    public Result<T> get() {
        return data;
    }

    protected abstract Result<T> checkAndGet(Object object);
}
