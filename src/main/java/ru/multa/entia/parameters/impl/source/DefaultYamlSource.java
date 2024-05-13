package ru.multa.entia.parameters.impl.source;

import lombok.RequiredArgsConstructor;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import ru.multa.entia.parameters.api.reader.Reader;
import ru.multa.entia.parameters.api.source.Source;
import ru.multa.entia.parameters.api.extractor.Extractor;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.Map;

@RequiredArgsConstructor
public class DefaultYamlSource implements Source {
    public enum Code {
        READER_NOT_SET,
        READER_RETURN_FAIL,
        SYNTAX_ERROR
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.READER_NOT_SET, "parameters:source.yaml.default:reader-not-set");
        CR.update(Code.READER_RETURN_FAIL, "parameters:source.yaml.default:reader-return-fail");
        CR.update(Code.SYNTAX_ERROR, "parameters:source.yaml.default:syntax-error");
    }

    private final Reader reader;

    private Integer prevContentHash;
    private Map<String, Object> data;

    @Override
    public Result<Object> get(final Extractor<?> extractor) {

        if (reader == null) {
            return DefaultResultBuilder.<Object>fail(CR.get(Code.READER_NOT_SET));
        }

        Result<String> result = reader.read();
        if (!result.ok()) {
            return new DefaultResultBuilder<Object>()
                    .success(false)
                    .causes(result)
                    .seedBuilder()
                    .code(CR.get(Code.READER_RETURN_FAIL))
                    .apply()
                    .build();
        }

        if (prevContentHash == null || prevContentHash != result.value().hashCode()) {
            prevContentHash = result.value().hashCode();
            try {
                data = new Yaml().load(result.value());
            } catch (YAMLException ex) {
                return DefaultResultBuilder.<Object>fail(CR.get(Code.SYNTAX_ERROR));
            }
        }


//        if (data.containsKey())



        // TODO: impl
        return null;
    }
}
