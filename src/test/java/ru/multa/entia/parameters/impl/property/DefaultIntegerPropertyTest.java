//package ru.multa.entia.parameters.impl.property;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvSource;
//import org.mockito.Mockito;
//import ru.multa.entia.fakers.impl.Faker;
//import ru.multa.entia.parameters.api.property.Property;
//import ru.multa.entia.results.api.repository.CodeRepository;
//import ru.multa.entia.results.api.result.Result;
//import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
//import ru.multa.entia.results.impl.result.DefaultResultBuilder;
//import ru.multa.entia.results.utils.Results;
//
//import java.util.function.Supplier;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class DefaultIntegerPropertyTest {
//    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
//
//    @Test
//    void shouldCheckGetting_ifInnerPropertyFail() {
//        String expectedCode = Faker.str_().random();
//        Supplier<Property<Object>> propertySupplier = () -> {
//            Result<Object> result = DefaultResultBuilder.<Object>fail(expectedCode);
//            DefaultPropertyOld property = Mockito.mock(DefaultPropertyOld.class);
//            Mockito.when(property.get()).thenReturn(result);
//
//            return property;
//        };
//
//        Result<Integer> result = new DefaultIntegerPropertyOld(propertySupplier.get()).get();
//
//        assertThat(
//                Results.comparator(result)
//                        .isFail()
//                        .value(null)
//                        .seedsComparator()
//                        .code(expectedCode)
//                        .back()
//                        .compare()
//        ).isTrue();
//    }
//
//    @ParameterizedTest
//    @CsvSource(value = {
//            ",",
//            "abc,",
//            "12.3.4.5,"
//    }, nullValues = "")
//    void shouldCheckGetting_ifParsingFail(String raw) {
//        Supplier<Property<Object>> propertySupplier = () -> {
//            Result<Object> result = DefaultResultBuilder.<Object>ok(raw);
//            DefaultPropertyOld property = Mockito.mock(DefaultPropertyOld.class);
//            Mockito.when(property.get()).thenReturn(result);
//
//            return property;
//        };
//
//        Result<Integer> result = new DefaultIntegerPropertyOld(propertySupplier.get()).get();
//
//        assertThat(
//                Results.comparator(result)
//                        .isFail()
//                        .value(null)
//                        .seedsComparator()
//                        .code(CR.get(DefaultIntegerPropertyOld.Code.PARSE_ERROR))
//                        .back()
//                        .compare()
//        ).isTrue();
//    }
//
//    @ParameterizedTest
//    @CsvSource(value = {
//            "0,0",
//            "11,11",
//            "-123,-123"
//    }, nullValues = "")
//    void shouldCheckGetting(String raw, int expected) {
//        Supplier<Property<Object>> propertySupplier = () -> {
//            Result<Object> result = DefaultResultBuilder.<Object>ok(raw);
//            DefaultPropertyOld property = Mockito.mock(DefaultPropertyOld.class);
//            Mockito.when(property.get()).thenReturn(result);
//
//            return property;
//        };
//
//        Result<Integer> result = new DefaultIntegerPropertyOld(propertySupplier.get()).get();
//
//        assertThat(
//                Results.comparator(result)
//                        .isSuccess()
//                        .value(expected)
//                        .seedsComparator()
//                        .isNull()
//                        .back()
//                        .compare()
//        ).isTrue();
//    }
//}