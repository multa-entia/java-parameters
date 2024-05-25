package ru.multa.entia.parameters.impl.property;

import ru.multa.entia.parameters.api.decryptor.Decryptor;
import ru.multa.entia.parameters.api.property.Property;
import ru.multa.entia.results.api.result.Result;

public class DefaultDecryptStringProperty implements Property<String> {
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
        Result<String> innerPropertyResult = innerProperty.get();
        if (!innerPropertyResult.ok()) {
            return innerPropertyResult;
        }
        return decryptor.decrypt(innerPropertyResult.value());
    }

    @Override
    public String getName() {
        return innerProperty.getName();
    }
}
