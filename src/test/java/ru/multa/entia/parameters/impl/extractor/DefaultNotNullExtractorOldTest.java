package ru.multa.entia.parameters.impl.extractor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.utils.Results;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultNotNullExtractorOldTest {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @Test
    void shouldCheckGetting_ifNotSet() {
        DefaultNotNullExtractorOld extractor = new DefaultNotNullExtractorOld("");

        Result<Object> result = extractor.get();
        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(CR.get(DefaultNotNullExtractorOld.Code.NOT_SET))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckGetting_ifNull() {
        DefaultNotNullExtractorOld extractor = new DefaultNotNullExtractorOld("");
        extractor.set(null);

        Result<Object> result = extractor.get();
        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(CR.get(DefaultNotNullExtractorOld.Code.IS_NULL))
                        .back()
                        .compare()
        ).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {
            "null",
            "str",
            "123"
    })
    void shouldCheckGetting(Object raw) {
        DefaultNotNullExtractorOld extractor = new DefaultNotNullExtractorOld("");
        extractor.set(raw);

        Result<Object> result = extractor.get();
        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .value(raw)
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();
    }
}