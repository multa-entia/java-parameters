package ru.multa.entia.parameters.impl.encryptor;

import lombok.SneakyThrows;
import org.jasypt.encryption.pbe.StandardPBEByteEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.utils.Results;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultStringEncryptorTest {

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @Test
    void shouldCheckCreation_ifPasswordNull() {
        Result<DefaultStringEncryptor> result = DefaultStringEncryptor.create(null);

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultStringEncryptor.Code.PASSWORD_IS_NULL))
                        .argsAreEmpty()
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckCreation_ifPasswordEmpty() {
        Result<DefaultStringEncryptor> result = DefaultStringEncryptor.create("");

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultStringEncryptor.Code.PASSWORD_IS_EMPTY))
                        .argsAreEmpty()
                        .back()
                        .compare()
        ).isTrue();
    }

    @SneakyThrows
    @Test
    void shouldCheckCreation() {
        String expectedPassword = Faker.str_().random(10, 20);
        Result<DefaultStringEncryptor> result = DefaultStringEncryptor.create(expectedPassword);

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();

        DefaultStringEncryptor value = result.value();
        Field field = value.getClass().getDeclaredField("encryptor");
        field.setAccessible(true);

        StandardPBEStringEncryptor encryptor = (StandardPBEStringEncryptor) field.get(value);
        field = encryptor.getClass().getDeclaredField("byteEncryptor");
        field.setAccessible(true);

        StandardPBEByteEncryptor byteEncryptor = (StandardPBEByteEncryptor) field.get(encryptor);

        field = byteEncryptor.getClass().getDeclaredField("password");
        field.setAccessible(true);

        char[] gottenPassword = (char[]) field.get(byteEncryptor);
        assertThat(new String(gottenPassword)).isEqualTo(expectedPassword);
    }

    @RepeatedTest(1)
    void shouldCheckDecryption() {
        String original = Faker.str_().random(30, 50);
        String password = Faker.str_().random(5, 10);

        Result<String> result = DefaultStringEncryptor.create(password).value().encrypt(original);

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();

        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(password);
        String decrypted = encryptor.decrypt(result.value());
        assertThat(decrypted).isEqualTo(original);
    }
}