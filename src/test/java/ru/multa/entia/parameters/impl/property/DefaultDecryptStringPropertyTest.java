package ru.multa.entia.parameters.impl.property;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.parameters.api.decryptor.Decryptor;
import ru.multa.entia.parameters.api.property.Property;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.utils.Results;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;

class DefaultDecryptStringPropertyTest {

    @Test
    void shouldCheckPropertyNameGetting() {
        String expectedName = Faker.str_().random();
        String gottenName = new DefaultDecryptStringProperty(expectedName, null).getName();

        assertThat(gottenName).isEqualTo(expectedName);
    }

    @Test
    void shouldCheckSetting() {
        AtomicReference<Object> holder = new AtomicReference<>();
        Supplier<TestStringProperty> innerPropertySupplier = () -> {
            TestStringProperty property = Mockito.mock(TestStringProperty.class);
            Mockito
                    .doAnswer(new Answer<Void>() {
                        @Override
                        public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                            holder.set(invocationOnMock.getArgument(0));
                            return null;
                        }
                    })
                    .when(property)
                    .set(Mockito.any());

            return property;
        };

        String expected = Faker.str_().random();
        new DefaultDecryptStringProperty(innerPropertySupplier.get(), null).set(expected);

        assertThat(holder.get()).isEqualTo(expected);
    }

    @Test
    void shouldCheckGetting_ifInnerPropertyIsFail() {
        String expectedCode = Faker.str_().random();
        Supplier<TestStringProperty> innerPropertySupplier = () -> {
            Result<String> result = DefaultResultBuilder.<String>fail(expectedCode);
            TestStringProperty property = Mockito.mock(TestStringProperty.class);
            Mockito.when(property.get()).thenReturn(result);

            return property;
        };

        Result<String> result = new DefaultDecryptStringProperty(innerPropertySupplier.get(), null).get();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(expectedCode)
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckGetting_ifDecryptorReturnsFail() {
        String expectedCode = Faker.str_().random();
        Supplier<TestStringProperty> innerPropertySupplier = () -> {
            Result<String> result = DefaultResultBuilder.<String>ok(Faker.str_().random());
            TestStringProperty property = Mockito.mock(TestStringProperty.class);
            Mockito.when(property.get()).thenReturn(result);

            return property;
        };
        Supplier<TestDecryptor> decryptorSupplier = () -> {
            Result<String> result = DefaultResultBuilder.<String>fail(expectedCode);
            TestDecryptor decryptor = Mockito.mock(TestDecryptor.class);
            Mockito.when(decryptor.decrypt(Mockito.any())).thenReturn(result);

            return decryptor;
        };

        Result<String> result = new DefaultDecryptStringProperty(innerPropertySupplier.get(), decryptorSupplier.get()).get();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(expectedCode)
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckGetting() {
        Function<String, String> decryptorFunction = input -> {
            return "___" + input;
        };
        String initValue = Faker.str_().random();
        String expectedValue = decryptorFunction.apply(initValue);

        Supplier<TestStringProperty> innerPropertySupplier = () -> {
            Result<String> result = DefaultResultBuilder.<String>ok(initValue);
            TestStringProperty property = Mockito.mock(TestStringProperty.class);
            Mockito.when(property.get()).thenReturn(result);

            return property;
        };

        Supplier<TestDecryptor> decryptorSupplier = () -> {
            TestDecryptor decryptor = Mockito.mock(TestDecryptor.class);
            Mockito
                    .when(decryptor.decrypt(Mockito.anyString()))
                    .thenAnswer(new Answer<Result<String>>() {
                        @Override
                        public Result<String> answer(InvocationOnMock invocationOnMock) throws Throwable {
                            return DefaultResultBuilder.<String>ok(decryptorFunction.apply(invocationOnMock.getArgument(0)));
                        }
                    });

            return decryptor;
        };

        Result<String> result = new DefaultDecryptStringProperty(innerPropertySupplier.get(), decryptorSupplier.get()).get();

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .value(expectedValue)
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();
    }

    private interface TestStringProperty extends Property<String> {}
    private interface TestDecryptor extends Decryptor<String, Result<String>> {}
}