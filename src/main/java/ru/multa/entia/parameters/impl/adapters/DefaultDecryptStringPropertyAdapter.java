package ru.multa.entia.parameters.impl.adapters;

import ru.multa.entia.parameters.api.decryptors.Decryptor;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.Objects;
import java.util.function.Function;

public class DefaultDecryptStringPropertyAdapter implements Function<Object, Result<String>> {
    public enum Code {
        DECRYPTOR_IS_NULL
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.DECRYPTOR_IS_NULL, "parameters:decrypt-string-property.default:decryptor-is-null");
    }

    private final Decryptor<String, Result<String>> decryptor;
    private final Function<Object, Result<String>> pureTextAdapter;

    public DefaultDecryptStringPropertyAdapter(final Decryptor<String, Result<String>> decryptor) {
        this(decryptor,  null);
    }

    public DefaultDecryptStringPropertyAdapter(final Decryptor<String, Result<String>> decryptor,
                                               final Function<Object, Result<String>> pureTextAdapter) {
        this.decryptor = decryptor;
        this.pureTextAdapter = Objects.requireNonNullElse(pureTextAdapter, new DefaultStringPropertyAdapter());
    }

    @Override
    public Result<String> apply(final Object object) {
        if (object == null) {
            return DefaultResultBuilder.<String>ok();
        }

        Result<String> pureResult = pureTextAdapter.apply(object);
        if (!pureResult.ok()) {
            return pureResult;
        }

        return decryptor == null
                ? DefaultResultBuilder.<String>fail(CR.get(Code.DECRYPTOR_IS_NULL))
                : decryptor.decrypt(pureResult.value());
    }
}
