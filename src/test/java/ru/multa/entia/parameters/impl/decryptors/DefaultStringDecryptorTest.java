package ru.multa.entia.parameters.impl.decryptors;

import lombok.SneakyThrows;
import org.jasypt.encryption.pbe.StandardPBEByteEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.utils.Results;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultStringDecryptorTest {

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @Test
    void shouldCheckCreation_ifPasswordNull() {
        Result<DefaultStringDecryptor> result = DefaultStringDecryptor.create(null);

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultStringDecryptor.Code.PASSWORD_IS_NULL))
                        .argsAreEmpty()
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckCreation_ifPasswordEmpty() {
        Result<DefaultStringDecryptor> result = DefaultStringDecryptor.create("");

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultStringDecryptor.Code.PASSWORD_IS_EMPTY))
                        .argsAreEmpty()
                        .back()
                        .compare()
        ).isTrue();
    }

    @SneakyThrows
    @Test
    void shouldCheckCreation() {
        String expectedPassword = Faker.str_().random(10, 20);
        Result<DefaultStringDecryptor> result = DefaultStringDecryptor.create(expectedPassword);

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();

        DefaultStringDecryptor decryptor = result.value();
        Field field = decryptor.getClass().getDeclaredField("encryptor");
        field.setAccessible(true);

        StandardPBEStringEncryptor encryptor = (StandardPBEStringEncryptor) field.get(decryptor);
        field = encryptor.getClass().getDeclaredField("byteEncryptor");
        field.setAccessible(true);

        StandardPBEByteEncryptor byteEncryptor = (StandardPBEByteEncryptor) field.get(encryptor);

        field = byteEncryptor.getClass().getDeclaredField("password");
        field.setAccessible(true);

        char[] gottenPassword = (char[]) field.get(byteEncryptor);
        assertThat(new String(gottenPassword)).isEqualTo(expectedPassword);
    }

    @Test
    void shouldCheckFailDecryption() {
        String password = Faker.str_().random(10, 20);
        DefaultStringDecryptor decryptor = DefaultStringDecryptor.create(password).value();

        Result<String> result = decryptor.decrypt("");

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultStringDecryptor.Code.DECRYPTION_ERROR))
                        .back()
                        .compare()
        ).isTrue();
    }

    @RepeatedTest(100)
    void shouldCheckDecryption() {
        String expectedOriginal = Faker.str_().random(30, 50);
        String password = Faker.str_().random(5, 10);

        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(password);
        String encrypted = encryptor.encrypt(expectedOriginal);

        Result<String> result = DefaultStringDecryptor.create(password).value().decrypt(encrypted);

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .value(expectedOriginal)
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();
    }
}