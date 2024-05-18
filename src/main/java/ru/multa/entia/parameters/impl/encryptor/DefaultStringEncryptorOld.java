package ru.multa.entia.parameters.impl.encryptor;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import ru.multa.entia.parameters.api.encryptor.EncryptorOld;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.nio.file.Files;

public class DefaultStringEncryptorOld implements EncryptorOld<String, Result<String>> {
    public enum Code {
        PASSWORD_IS_NULL,
        PASSWORD_IS_EMPTY,
        ENCRYPTION_ERROR
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.PASSWORD_IS_NULL, "parameters:encryptor.string.default:password-is-null");
        CR.update(Code.PASSWORD_IS_EMPTY, "parameters:encryptor.string.default:password-is-empty");
        CR.update(Code.ENCRYPTION_ERROR, "parameters:encryptor.string.default:decryption-error");
    }

    private final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();

    public static Result<DefaultStringEncryptorOld> create(final String password){
        return DefaultResultBuilder.<DefaultStringEncryptorOld>computeFromCodes(
                () -> {return new DefaultStringEncryptorOld(password);},
                () -> {return password == null ? CR.get(Code.PASSWORD_IS_NULL) : null;},
                () -> {return password.isEmpty() ? CR.get(Code.PASSWORD_IS_EMPTY) : null;}
        );
    }

    private DefaultStringEncryptorOld(final String password) {
        this.encryptor.setPassword(password);
    }

    @Override
    public Result<String> encrypt(final String decrypted) {
        try {
            String encrypted = this.encryptor.encrypt(decrypted);
            return DefaultResultBuilder.<String>ok(encrypted);
        } catch (Exception ex) {
            String arg = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
            return DefaultResultBuilder.<String>fail(CR.get(Code.ENCRYPTION_ERROR), arg);
        }
    }
}
