package ru.multa.entia.parameters.api.encryptors;

public interface Encryptor<I, O> {
    O encrypt(I input);
}
