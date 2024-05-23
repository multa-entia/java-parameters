package ru.multa.entia.parameters.impl.property;

import ru.multa.entia.parameters.api.decryptor.Decryptor;
import ru.multa.entia.parameters.api.property.Property;
import ru.multa.entia.parameters.impl.decryptor.DefaultStringDecryptor;
import ru.multa.entia.results.api.result.Result;

import java.util.Objects;

public class DefaultDecryptStringProperty implements Property<String> {
//    public enum Code {
//        IS_NULL
//    }
//
//    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
//    static {
//        CR.update(Code.IS_NULL, "parameters:str-property.default:is-null");
//    }

    private final Property<String> innerProperty;
    private final Decryptor<String, Result<String>> decryptor;

    public DefaultDecryptStringProperty(final String name,
                                        final Decryptor<String, Result<String>> decryptor) {
        this(new DefaultStringProperty(name), decryptor);
    }

    public DefaultDecryptStringProperty(final Property<String> innerProperty,
                                        final Decryptor<String, Result<String>> decryptor) {
        this.innerProperty = innerProperty;
        this.decryptor = decryptor;
    }

    @Override
    public void set(final Object raw) {
        this.innerProperty.set(raw);
    }

    @Override
    public Result<String> get() {
        // TODO: impl
        return null;
    }

    @Override
    public String getName() {
        return innerProperty.getName();
    }
}
