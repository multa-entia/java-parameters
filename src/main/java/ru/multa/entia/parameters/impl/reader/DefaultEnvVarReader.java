package ru.multa.entia.parameters.impl.reader;

import lombok.Getter;
import ru.multa.entia.parameters.api.ids.Id;
import ru.multa.entia.parameters.api.reader.Reader;
import ru.multa.entia.parameters.impl.ids.DefaultId;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.*;

public class DefaultEnvVarReader implements Reader<Map<String, Object>> {
    public enum Code {
        VAR_NAMES_IS_EMPTY
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.VAR_NAMES_IS_EMPTY, "parameters:env-var-reader.default:var-names-is-empty");
    }

    public static Builder builder() {
        return new Builder();
    }

    @Getter
    private final Id id;
    private final Set<String> varNames;

    private DefaultEnvVarReader(final Id id,
                                final Set<String> varNames) {
        this.id = id;
        this.varNames = varNames;
    }

    @Override
    public Result<Map<String, Object>> read() {
        HashMap<String, Object> properties = new HashMap<>();
        for (String varName : varNames) {
            String value = System.getenv(varName);
            if (value != null) {
                properties.put(varName, value);
            }
        }

        return DefaultResultBuilder.<Map<String, Object>>ok(properties);
    }

    public static class Builder {
        private final Set<String> varNames = new HashSet<>();
        private Id id;

        public Builder id(final Id id) {
            this.id = id;
            return this;
        }

        public Builder addVarName(final String name) {
            this.varNames.add(name);
            return this;
        }

        public Result<Reader<Map<String, Object>>> build() {
            return varNames.isEmpty()
                    ? DefaultResultBuilder.<Reader<Map<String, Object>>>fail(CR.get(Code.VAR_NAMES_IS_EMPTY))
                    : DefaultResultBuilder.<Reader<Map<String, Object>>>ok(
                            new DefaultEnvVarReader(
                                    Objects.requireNonNullElse(id, DefaultId.createIdForEnvVar()),
                                    varNames));
        }
    }
}
