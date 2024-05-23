package ru.multa.entia.parameters.impl.property;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.parameters.api.property.Property;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

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

    }

    @Test
    void shouldCheckGetting_ifDecryptorReturnsFail() {

    }

    @Test
    void shouldCheckGetting() {

    }

    private interface TestStringProperty extends Property<String> {}
}