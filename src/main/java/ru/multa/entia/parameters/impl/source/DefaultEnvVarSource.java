package ru.multa.entia.parameters.impl.source;

import ru.multa.entia.parameters.api.extractor.Extractor;
import ru.multa.entia.parameters.api.source.Source;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.Objects;
import java.util.function.Function;

public class DefaultEnvVarSource implements Source {
    public enum Code {
        PROPERTY_IS_ABSENCE
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.PROPERTY_IS_ABSENCE, "parameters:source.env-var.default:property-is-absence");
    }

    private final Function<String, String> getter;

    public DefaultEnvVarSource() {
        this(null);
    }

    public DefaultEnvVarSource(final Function<String, String> getter) {
        this.getter = Objects.requireNonNullElse(getter, System::getenv);
    }

    @Override
    public Result<Object> get(final Extractor<?> extractor) {
        String raw = getter.apply(extractor.getProperty());
        if (raw == null) {
            return DefaultResultBuilder.<Object>fail(CR.get(Code.PROPERTY_IS_ABSENCE));
        }

        extractor.set(raw);
        return DefaultResultBuilder.<Object>ok();
    }
}
