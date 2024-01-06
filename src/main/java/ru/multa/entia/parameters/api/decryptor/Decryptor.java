package ru.multa.entia.parameters.api.decryptor;

public interface Decryptor<I, O> {
    O decrypt(I input);
}
