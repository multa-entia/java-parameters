// TODO: restore
//package ru.multa.entia.parameters.impl.property;
//
//import org.junit.jupiter.api.Test;
//import ru.multa.entia.fakers.impl.Faker;
//import ru.multa.entia.results.api.repository.CodeRepository;
//import ru.multa.entia.results.api.result.Result;
//import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
//import ru.multa.entia.results.utils.Results;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class DefaultNotNullPropertyTest {
//    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
//
//    @Test
//    void shouldCheckGetting_ifSetNull() {
//        DefaultNotNullProperty property = new DefaultNotNullProperty(Faker.str_().random());
//        property.set(null);
//
//        Result<Object> result = property.get();
//
//        assertThat(
//                Results.comparator(result)
//                        .isFail()
//                        .value(null)
//                        .seedsComparator()
//                        .code(CR.get(DefaultNotNullProperty.Code.IS_NULL))
//                        .back()
//                        .compare()
//        ).isTrue();
//    }
//
//    @Test
//    void shouldCheckGetting() {
//        String expected = Faker.str_().random();
//        DefaultNotNullProperty property = new DefaultNotNullProperty(Faker.str_().random());
//        property.set(expected);
//
//        Result<Object> result = property.get();
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