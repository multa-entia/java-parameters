package ru.multa.entia.parameters.impl.properties;

import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.api.seed.SeedBuilder;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.Arrays;
import java.util.function.Function;

public class DefaultAdaptNullableProperty<T> extends DefaultAbstractProperty<T>{
    public enum Code {
        ADAPTER_IS_NULL,
        ADAPT_ERROR
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.ADAPTER_IS_NULL, "parameters:not-null-adapt-property.default:adapter-is-null");
        CR.update(Code.ADAPT_ERROR, "parameters:not-null-adapt-property.default:adapt-error");
    }

    private final Function<Object, Result<T>> adapter;

    public DefaultAdaptNullableProperty(final String name,
                                        final Function<Object, Result<T>> adapter) {
        super(name);
        this.adapter = adapter;
    }

    @Override
    protected Result<T> checkAndGet(final Object object) {
        if (object == null) {
            return DefaultResultBuilder.<T>ok();
        } else if (adapter == null) {
            return DefaultResultBuilder.<T>fail(CR.get(Code.ADAPTER_IS_NULL));
        }

        Result<T> result = adapter.apply(object);
        if (result.ok()) {
            return result;
        }

        SeedBuilder<T> builder = new DefaultResultBuilder<T>()
                .success(false)
                .seedBuilder()
                .code(CR.get(Code.ADAPT_ERROR));
        Arrays.stream(result.seed().args()).forEach(builder::addLastArgs);

        return builder.apply().build();
    }
}
