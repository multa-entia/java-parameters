package ru.multa.entia.parameters.impl.reader;

import ru.multa.entia.parameters.api.ids.Id;
import ru.multa.entia.parameters.api.reader.Reader;
import ru.multa.entia.parameters.api.reader.ReaderResult;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.HashSet;
import java.util.Set;

public class DefaultEnvVarReader implements Reader {
    public enum Code {
        VAR_NAMES_IS_EMPTY
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.VAR_NAMES_IS_EMPTY, "parameters:env-var-reader.default:var-names-is-empty");
    }

    public static final String VAR_NAME_VAR_NAMES = "varNames";
    public static final String PREFIX = "__";

    public static Builder builder() {
        return new Builder();
    }

    private final Set<String> varNames;

    private DefaultEnvVarReader(final Set<String> varNames) {
        this.varNames = varNames;
    }

    @Override
    public Result<ReaderResult> read() {
        // TODO: impl
        return null;
    }

    @Override
    public Id getId() {
        // TODO: impl
        return null;
    }

    public static class Builder {
        private final Set<String> varNames = new HashSet<>();

        public Builder addVarName(final String varName) {
            this.varNames.add(varName);
            return this;
        }

        public Result<Reader> build() {
            return varNames.isEmpty()
                    ? DefaultResultBuilder.<Reader>fail(CR.get(Code.VAR_NAMES_IS_EMPTY))
                    : DefaultResultBuilder.<Reader>ok(new DefaultEnvVarReader(varNames));
        }
    }
}
