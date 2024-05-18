package ru.multa.entia.parameters.impl.source;

import ru.multa.entia.parameters.api.extractor.ExtractorOld;
import ru.multa.entia.parameters.api.source.SourceOld;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.Objects;
import java.util.function.Function;

public class DefaultEnvVarSourceOld implements SourceOld {
    public enum Code {
        PROPERTY_IS_ABSENCE
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.PROPERTY_IS_ABSENCE, "parameters:source.env-var.default:property-is-absence");
    }

    private final Function<String, String> getter;

    public DefaultEnvVarSourceOld() {
        this(null);
    }

    public DefaultEnvVarSourceOld(final Function<String, String> getter) {
        this.getter = Objects.requireNonNullElse(getter, System::getenv);
    }

    @Override
    public Result<Object> get(final ExtractorOld<?> extractorOld) {
        String raw = getter.apply(extractorOld.getProperty());
        if (raw == null) {
            return DefaultResultBuilder.<Object>fail(CR.get(Code.PROPERTY_IS_ABSENCE));
        }

        extractorOld.set(raw);
        return DefaultResultBuilder.<Object>ok();
    }
}
