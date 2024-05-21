//package ru.multa.entia.parameters.impl.property;
//
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.mockito.invocation.InvocationOnMock;
//import org.mockito.stubbing.Answer;
//import ru.multa.entia.fakers.impl.Faker;
//import ru.multa.entia.parameters.api.property.Property;
//import ru.multa.entia.results.api.result.Result;
//
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.function.Supplier;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class AbstractPropertyOldTest {
//
//    @Test
//    void shouldCheckNameGetting() {
//        String expectedName = Faker.str_().random();
//        Supplier<Property<Object>> propertySupplier = () -> {
//            DefaultPropertyOld property = Mockito.mock(DefaultPropertyOld.class);
//            Mockito.when(property.getName()).thenReturn(expectedName);
//
//            return property;
//        };
//
//        AbstractPropertyOld<Object> property = new AbstractPropertyOld<>(propertySupplier.get()) {
//            @Override
//            public Result<Object> get() {
//                return null;
//            }
//        };
//
//        assertThat(property.getName()).isEqualTo(expectedName);
//    }
//
//    @Test
//    void shouldCheckRawSetting() {
//        AtomicReference<String> holder = new AtomicReference<>();
//        Supplier<Property<Object>> propertySupplier = () -> {
//            DefaultPropertyOld property = Mockito.mock(DefaultPropertyOld.class);
//            Mockito
//                    .doAnswer(new Answer<Void>() {
//                        @Override
//                        public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
//                            holder.set(invocationOnMock.getArgument(0));
//                            return null;
//                        }
//                    })
//                    .when(property)
//                    .set(Mockito.any());
//
//            return property;
//        };
//        AbstractPropertyOld<Object> property = new AbstractPropertyOld<>(propertySupplier.get()) {
//            @Override
//            public Result<Object> get() {
//                return null;
//            }
//        };
//
//        String expectedRaw = Faker.str_().random();
//        property.set(expectedRaw);
//
//        assertThat(holder.get()).isEqualTo(expectedRaw);
//    }
//}