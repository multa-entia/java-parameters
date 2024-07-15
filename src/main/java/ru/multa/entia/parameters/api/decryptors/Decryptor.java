package ru.multa.entia.parameters.api.decryptors;

public interface Decryptor<I, O> {
    O decrypt(I input);
}
