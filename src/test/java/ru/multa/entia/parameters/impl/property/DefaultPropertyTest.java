// TODO: restore
//package ru.multa.entia.parameters.impl.property;
//
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvSource;
//import ru.multa.entia.fakers.impl.Faker;
//import ru.multa.entia.results.api.result.Result;
//import ru.multa.entia.results.utils.Results;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class DefaultPropertyTest {
//
//    @ParameterizedTest
//    @CsvSource({
//            ",",
//            "123,",
//            "hello,",
//            "world,"
//    })
//    void shouldCheckGetting(Object raw) {
//        DefaultProperty property = new DefaultProperty(Faker.str_().random());
//        property.set(raw);
//
//        Result<Object> result = property.get();
//
//        assertThat(
//                Results.comparator(result)
//                        .isSuccess()
//                        .value(raw)
//                        .seedsComparator()
//                        .isNull()
//                        .back()
//                        .compare()
//        ).isTrue();
//    }
//}