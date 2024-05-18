package ru.multa.entia.parameters.api.encryptor;

public interface EncryptorOld<I, O> {
    O encrypt(I input);
}
