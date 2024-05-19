package ru.multa.entia.parameters.impl.property;

import ru.multa.entia.parameters.api.property.Property;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.Objects;

public class DefaultNotNullProperty implements Property<Object> {
    public enum Code {
        IS_NULL
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.IS_NULL, "parameters:not-null-property.default:is-null");
    }

    private final Property<Object> innerProperty;

    public DefaultNotNullProperty(final String name) {
        this(name, null);
    }

    public DefaultNotNullProperty(final String name, final Property<Object> property) {
        this.innerProperty = Objects.requireNonNullElse(property, new DefaultProperty(name));
    }

    @Override
    public void set(final Object raw) {
        this.innerProperty.set(raw);
    }

    @Override
    public Result<Object> get() {
        Result<Object> result = innerProperty.get();
        return result.ok() && result.value() == null
                ? DefaultResultBuilder.<Object>fail(CR.get(Code.IS_NULL))
                : result;
    }

    @Override
    public String getName() {
        return innerProperty.getName();
    }
}
