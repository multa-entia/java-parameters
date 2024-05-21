package ru.multa.entia.parameters.impl.property;

import lombok.Getter;
import ru.multa.entia.parameters.api.property.Property;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractProperty<T> implements Property<T> {
    public enum Code {
        IS_NOT_SET
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.IS_NOT_SET, "parameters:property.default:is-not-set");
    }

    private final AtomicReference<Datum<T>> holder
            = new AtomicReference<>(new Datum<>(false, DefaultResultBuilder.<T>fail(CR.get(Code.IS_NOT_SET))));

    @Getter
    private final String name;

    public AbstractProperty(final String name) {
        this.name = name;
    }

    @Override
    public void set(final Object raw) {
        Result<T> result = checkRaw(raw);
        if (result.ok() || !holder.get().isSet()) {
            holder.set(new Datum<>(true, result));
        }
    }

    @Override
    public Result<T> get() {
        return holder.get().result();
    }

    protected abstract Result<T> checkRaw(Object raw);

    private record Datum<T>(boolean isSet, Result<T> result) {}
}
