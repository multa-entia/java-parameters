package ru.multa.entia.parameters.impl.extractor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.utils.Results;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultStringExtractorOldTest {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @Test
    void shouldCheckGetting_ifNotSet() {
        DefaultStringExtractorOld extractor = new DefaultStringExtractorOld("");

        Result<String> result = extractor.get();
        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(CR.get(DefaultStringExtractorOld.Code.NOT_SET))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckGetting_ifNull() {
        DefaultStringExtractorOld extractor = new DefaultStringExtractorOld("");
        extractor.set(null);

        Result<String> result = extractor.get();
        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(CR.get(DefaultStringExtractorOld.Code.IS_NULL))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckGetting_ifNotString() {
        DefaultStringExtractorOld extractor = new DefaultStringExtractorOld("");
        extractor.set(123);

        Result<String> result = extractor.get();
        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(CR.get(DefaultStringExtractorOld.Code.IS_NOT_STRING))
                        .back()
                        .compare()
        ).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {
            "str",
            "line"
    })
    void shouldCheckGetting(Object raw) {
        DefaultStringExtractorOld extractor = new DefaultStringExtractorOld("");
        extractor.set(raw);

        Result<String> result = extractor.get();
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