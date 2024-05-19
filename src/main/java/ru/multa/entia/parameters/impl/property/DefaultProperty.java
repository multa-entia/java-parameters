package ru.multa.entia.parameters.impl.property;

import lombok.Getter;
import ru.multa.entia.parameters.api.property.Property;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.concurrent.atomic.AtomicReference;

public class DefaultProperty implements Property<Object> {
    public enum Code {
        RAW_NOT_SET
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.RAW_NOT_SET, "parameters:property.default:raw-not-isSet");
    }

    protected AtomicReference<Datum> raw = new AtomicReference<>(new Datum(false, null));
    @Getter
    protected String name;

    public DefaultProperty(final String name) {
        this.name = name;
    }

    @Override
    public void set(final Object raw) {
        this.raw.set(new Datum(true, raw));
    }

    @Override
    public Result<Object> get() {
        Datum datum = raw.get();
        return datum.isSet()
                ? DefaultResultBuilder.<Object>ok(datum.value())
                : DefaultResultBuilder.<Object>fail(CR.get(Code.RAW_NOT_SET));
    }

    protected record Datum(boolean isSet, Object value) {}
}
