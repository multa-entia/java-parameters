package ru.multa.entia.parameters.impl.source;

import lombok.RequiredArgsConstructor;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import ru.multa.entia.parameters.api.reader.ReaderOld;
import ru.multa.entia.parameters.api.source.SourceOld;
import ru.multa.entia.parameters.api.extractor.ExtractorOld;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.Map;

@RequiredArgsConstructor
public class DefaultYamlSourceOld implements SourceOld {
    public enum Code {
        READER_NOT_SET,
        READER_RETURN_FAIL,
        SYNTAX_ERROR,
        PROPERTY_NOT_EXIST
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.READER_NOT_SET, "parameters:source.yaml.default:reader-not-isSet");
        CR.update(Code.READER_RETURN_FAIL, "parameters:source.yaml.default:reader-return-fail");
        CR.update(Code.SYNTAX_ERROR, "parameters:source.yaml.default:syntax-error");
        CR.update(Code.PROPERTY_NOT_EXIST, "parameters:source.yaml.default:property-not-exist");
    }

    private final ReaderOld readerOld;

    private Integer prevContentHash;
    private Map<String, Object> data;

    @Override
    public Result<Object> get(final ExtractorOld<?> extractorOld) {

        if (readerOld == null) {
            return DefaultResultBuilder.<Object>fail(CR.get(Code.READER_NOT_SET));
        }

        Result<String> result = readerOld.read();
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
            try {
                data = new Yaml().load(result.value());
                prevContentHash = result.value().hashCode();
            } catch (YAMLException ex) {
                return DefaultResultBuilder.<Object>fail(CR.get(Code.SYNTAX_ERROR));
            }
        }

        if (data.containsKey(extractorOld.getProperty())) {
            Object value = data.get(extractorOld.getProperty());
            extractorOld.set(value);
            return DefaultResultBuilder.<Object>ok(value);
        } else {
            return DefaultResultBuilder.<Object>fail(CR.get(Code.PROPERTY_NOT_EXIST));
        }
    }
}
