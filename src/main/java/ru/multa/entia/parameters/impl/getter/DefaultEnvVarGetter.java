package ru.multa.entia.parameters.impl.getter;

import ru.multa.entia.parameters.api.getter.Getter;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class DefaultEnvVarGetter implements Getter<String, Result<String>> {
    public enum Code {
        NAME_IS_NULL,
        NAME_IS_BLANK,
        VAR_IS_ABSENCE
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.NAME_IS_NULL, "parameters:getter.env-var.default:name-is-null");
        CR.update(Code.NAME_IS_BLANK, "parameters:getter.env-var.default:name-is-blank");
        CR.update(Code.VAR_IS_ABSENCE, "parameters:getter.env-var.default:var-is-absence");
    }

    private final Function<String, String> getter;

    public DefaultEnvVarGetter() {
        this(null);
    }

    public DefaultEnvVarGetter(final Function<String, String> getter) {
        this.getter = Objects.requireNonNullElse(getter, System::getenv);
    }

    @Override
    public Result<String> get(final String name) {
        AtomicReference<String> var = new AtomicReference<>();
        return DefaultResultBuilder.<String>computeFromCodes(
                var::get,
                () -> {return name == null ? CR.get(Code.NAME_IS_NULL) : null;},
                () -> {return name.isBlank() ? CR.get(Code.NAME_IS_BLANK) : null;},
                () -> {
                    String value = getter.apply(name);
                    if (value != null) {
                        var.set(value);
                        return null;
                    }
                    return CR.get(Code.VAR_IS_ABSENCE);
                }
        );
    }
}
