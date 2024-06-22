package ru.multa.entia.parameters.impl.adapters;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.parameters.api.decryptors.Decryptor;
import ru.multa.entia.parameters.impl.decryptors.DefaultStringDecryptor;
import ru.multa.entia.parameters.impl.encryptors.DefaultStringEncryptor;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.utils.Results;

import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultDecryptStringAdapterTest {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @Test
    void shouldCheckAdaptation_ifObjectNull() {
        Result<String> result = new DefaultDecryptStringPropertyAdapter(null).apply(null);

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .value(null)
                        .seedsComparator().isNull()
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckAdaptation_ifPureTextAdapterReturnedFail() {
        String expectedCode = Faker.str_().random();
        Supplier<TestStringAdapter> adapterSupplier = () -> {
            Result<String> result = DefaultResultBuilder.<String>fail(expectedCode);
            TestStringAdapter adapter = Mockito.mock(TestStringAdapter.class);
            Mockito.when(adapter.apply(Mockito.any())).thenReturn(result);

            return adapter;
        };

        Result<String> result = new DefaultDecryptStringPropertyAdapter(null, adapterSupplier.get())
                .apply(Faker.str_().random());

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator().code(expectedCode)
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckAdaptation_ifDecryptorIsNull() {
        Result<String> result = new DefaultDecryptStringPropertyAdapter(null).apply(Faker.str_().random());

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator().code(CR.get(DefaultDecryptStringPropertyAdapter.Code.DECRYPTOR_IS_NULL))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckAdaptation_ifDecryptorReturnedFail() {
        String expectedCode = Faker.str_().random();
        Supplier<TestDecryptor> decryptorSupplier = () -> {
            Result<String> result = DefaultResultBuilder.<String>fail(expectedCode);
            TestDecryptor decryptor = Mockito.mock(TestDecryptor.class);
            Mockito.when(decryptor.decrypt(Mockito.any())).thenReturn(result);

            return decryptor;
        };

        Result<String> result = new DefaultDecryptStringPropertyAdapter(decryptorSupplier.get()).apply(Faker.str_().random());

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator().code(expectedCode)
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckAdaptation() {
        String password = Faker.str_().random();
        String original = Faker.str_().random();
        String encrypted = DefaultStringEncryptor.create(password).value().encrypt(original).value();

        DefaultStringDecryptor decryptor = DefaultStringDecryptor.create(password).value();
        Result<String> result = new DefaultDecryptStringPropertyAdapter(decryptor).apply(encrypted);

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .value(original)
                        .seedsComparator().isNull()
                        .back()
                        .compare()
        ).isTrue();
    }

    private interface TestStringAdapter extends Function<Object, Result<String>> {}
    private interface TestDecryptor extends Decryptor<String, Result<String>> {}
}