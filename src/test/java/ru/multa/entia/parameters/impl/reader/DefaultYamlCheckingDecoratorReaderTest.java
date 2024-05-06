package ru.multa.entia.parameters.impl.reader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.parameters.api.reader.Reader;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.utils.Results;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultYamlCheckingDecoratorReaderTest {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @ParameterizedTest
    @CsvSource({
            "intValue: 123 floatValue: 1.2",
            "strValue: hello::world"
    })
    void shouldCheckMoreOneColonCheck_ifFail(String line) {
        DefaultYamlCheckingDecoratorReader.MoreOneColonChecker checker
                = new DefaultYamlCheckingDecoratorReader.MoreOneColonChecker();
        Result<String> result = checker.apply(line);

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultYamlCheckingDecoratorReader.MoreOneColonChecker.Code.MORE_ONE_COLON))
                        .back()
                        .compare()
        ).isTrue();
    }

    @ParameterizedTest
    @CsvSource({
            "intValue: 123",
            "strValue: \"hello::world\""
    })
    void shouldCheckMoreOneColonCheck_ifSuccess(String line) {
        DefaultYamlCheckingDecoratorReader.MoreOneColonChecker checker
                = new DefaultYamlCheckingDecoratorReader.MoreOneColonChecker();
        Result<String> result = checker.apply(line);

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .value(line)
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckReading_ifInnerReaderNotSet() {
        DefaultYamlCheckingDecoratorReader reader = new DefaultYamlCheckingDecoratorReader(null, false);

        Result<String> result = reader.read();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultYamlCheckingDecoratorReader.Code.INNER_READER_IS_NOT_SET))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckReading_ifInnerReaderFail() {
        String expectedCode = Faker.str_().random();
        Supplier<Reader> readerSupplier = () -> {
            Result<String> result = DefaultResultBuilder.<String>fail(expectedCode);
            Reader reader = Mockito.mock(Reader.class);
            Mockito
                    .when(reader.read())
                    .thenReturn(result);

            return reader;
        };
        DefaultYamlCheckingDecoratorReader reader = new DefaultYamlCheckingDecoratorReader(readerSupplier.get(), false);

        Result<String> result = reader.read();

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



//    @Test
//    void shouldCheckReading_ifSyntaxError() {
////        String source =
////"""
////value0: 123
////# value1: 123value2: 123
////value3: 123value4: 123
////value5: 123
////""";
////
////        Supplier<Reader> readerSupplier = () -> {
////            Result<String> result = DefaultResultBuilder.<String>ok(source);
////            Reader reader = Mockito.mock(Reader.class);
////            Mockito
////                    .when(reader.read())
////                    .thenReturn(result);
////
////            return reader;
////        };
////        DefaultYamlCheckingDecoratorReader reader = new DefaultYamlCheckingDecoratorReader(readerSupplier.get(), false);
////
////        Result<String> result = reader.read();
////
////        assertThat(
////                Results.comparator(result)
////                        .isFail()
////                        .value(null)
////                        .seedsComparator()
////                        .code(expectedCode)
////                        .back()
////                        .compare()
////        ).isTrue();
//    }

//    @Test
//    void shouldCheckReading_ifSyntaxErrorAndIgnorance() {
//
//    }
//
//    @Test
//    void shouldCheckReading() {
//
//    }
}