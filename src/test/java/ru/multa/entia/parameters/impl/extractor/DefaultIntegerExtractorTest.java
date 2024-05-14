package ru.multa.entia.parameters.impl.extractor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.utils.Results;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultIntegerExtractorTest {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @Test
    void shouldCheckGetting_ifNotSet() {
        DefaultIntegerExtractor extractor = new DefaultIntegerExtractor("");

        Result<Integer> result = extractor.get();
        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(CR.get(DefaultIntegerExtractor.Code.NOT_SET))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckGetting_ifNull() {
        DefaultIntegerExtractor extractor = new DefaultIntegerExtractor("");
        extractor.set(null);

        Result<Integer> result = extractor.get();
        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(CR.get(DefaultIntegerExtractor.Code.IS_NULL))
                        .back()
                        .compare()
        ).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {
            "str",
            "12.4",
            "0xff",
            "0b10101"
    })
    void shouldCheckGetting_ifNotInteger(Object raw) {
        DefaultIntegerExtractor extractor = new DefaultIntegerExtractor("");
        extractor.set(raw);

        Result<Integer> result = extractor.get();
        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(CR.get(DefaultIntegerExtractor.Code.IS_NOT_INTEGER))
                        .back()
                        .compare()
        ).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {
            "-100,-100",
            "-1,-1",
            "0,0",
            "1,1",
            "42,42"
    })
    void shouldCheckGetting(Object raw, int expected) {
        DefaultIntegerExtractor extractor = new DefaultIntegerExtractor("");
        extractor.set(raw);

        Result<Integer> result = extractor.get();
        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .value(expected)
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();
    }
}