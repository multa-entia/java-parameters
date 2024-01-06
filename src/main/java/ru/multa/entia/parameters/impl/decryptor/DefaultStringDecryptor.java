package ru.multa.entia.parameters.impl.decryptor;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import ru.multa.entia.parameters.api.decryptor.Decryptor;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

public class DefaultStringDecryptor implements Decryptor<String, Result<String>> {
    public enum Code {
        PASSWORD_IS_NULL,
        PASSWORD_IS_EMPTY,
        DECRYPTION_ERROR
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.PASSWORD_IS_NULL, "parameters:decryptor.jasypt-string.password-is-null");
        CR.update(Code.PASSWORD_IS_EMPTY, "parameters:decryptor.jasypt-string.password-is-empty");
        CR.update(Code.DECRYPTION_ERROR, "parameters:decryptor.jasypt-string.decryption-error");
    }

    private final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();

    public static Result<DefaultStringDecryptor> create(final String password) {
        if (password == null) {
            return DefaultResultBuilder.<DefaultStringDecryptor>fail(CR.get(Code.PASSWORD_IS_NULL));
        }

        if (password.isEmpty()) {
            return DefaultResultBuilder.<DefaultStringDecryptor>fail(CR.get(Code.PASSWORD_IS_EMPTY));
        }

        return DefaultResultBuilder.<DefaultStringDecryptor>ok(new DefaultStringDecryptor(password));
    }

    private DefaultStringDecryptor(final String password) {
        this.encryptor.setPassword(password);
    }

    @Override
    public Result<String> decrypt(final String input) {
        try {
            return DefaultResultBuilder.<String>ok(encryptor.decrypt(input));
        } catch (RuntimeException ex) {
            String arg = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
            return DefaultResultBuilder.<String>fail(CR.get(Code.DECRYPTION_ERROR), arg);
        }
    }
}
