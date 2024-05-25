package ru.multa.entia.parameters.impl.source;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import ru.multa.entia.parameters.api.source.SourceAdapter;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.Map;

public class DefaultYamlSourceAdapter implements SourceAdapter<String, Map<String, Object>> {
    public enum Code {
        INPUT_IS_NULL,
        SYNTAX_ERROR
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.INPUT_IS_NULL, "parameters:source-adapter.yaml.default:input-is-null");
        CR.update(Code.SYNTAX_ERROR, "parameters:source-adapter.yaml.default:syntax-error");
    }

    @Override
    public Result<Map<String, Object>> adapt(final String input) {
        if (input == null) {
            return DefaultResultBuilder.<Map<String, Object>>fail(CR.get(Code.INPUT_IS_NULL));
        }

        try {
            return DefaultResultBuilder.<Map<String, Object>>ok(new Yaml().load(input));
        } catch (YAMLException ex) {
            return DefaultResultBuilder.<Map<String, Object>>fail(CR.get(Code.SYNTAX_ERROR));
        }
    }
}
