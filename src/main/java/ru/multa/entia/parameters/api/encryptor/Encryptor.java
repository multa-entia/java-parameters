package ru.multa.entia.parameters.api.encryptor;

public interface Encryptor<I, O> {
    O encrypt(I input);
}
