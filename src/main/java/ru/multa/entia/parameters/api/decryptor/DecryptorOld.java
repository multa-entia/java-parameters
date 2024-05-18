package ru.multa.entia.parameters.api.decryptor;

public interface DecryptorOld<I, O> {
    O decrypt(I input);
}
