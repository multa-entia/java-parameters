package ru.multa.entia.parameters.impl.extractor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.utils.Results;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultFloatExtractorOldTest {

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @Test
    void shouldCheckGetting_ifNotSet() {
        DefaultFloatExtractorOld extractor = new DefaultFloatExtractorOld("");

        Result<Float> result = extractor.get();
        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(CR.get(DefaultFloatExtractorOld.Code.NOT_SET))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckGetting_ifNull() {
        DefaultFloatExtractorOld extractor = new DefaultFloatExtractorOld("");
        extractor.set(null);

        Result<Float> result = extractor.get();
        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(CR.get(DefaultFloatExtractorOld.Code.IS_NULL))
                        .back()
                        .compare()
        ).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {
            "str",
            "12.4.3",
            "0xff",
            "0b10101"
    })
    void shouldCheckGetting_ifNotInteger(Object raw) {
        DefaultFloatExtractorOld extractor = new DefaultFloatExtractorOld("");
        extractor.set(raw);

        Result<Float> result = extractor.get();
        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(CR.get(DefaultFloatExtractorOld.Code.IS_NOT_FLOAT))
                        .back()
                        .compare()
        ).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {
            "-100.1,-100.1",
            "-1.2,-1.2",
            "0.0,0.0",
            "1.3,1.3",
            "42.42,42.42"
    })
    void shouldCheckGetting(Object raw, float expected) {
        DefaultFloatExtractorOld extractor = new DefaultFloatExtractorOld("");
        extractor.set(raw);

        Result<Float> result = extractor.get();
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