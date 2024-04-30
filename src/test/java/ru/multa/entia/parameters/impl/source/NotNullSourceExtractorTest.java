package ru.multa.entia.parameters.impl.source;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.utils.Results;

import static org.assertj.core.api.Assertions.assertThat;

class NotNullSourceExtractorTest {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @Test
    void shouldCheckGetting_ifNotSet() {
        NotNullSourceExtractor extractor = new NotNullSourceExtractor();

        Result<Object> result = extractor.get();
        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(CR.get(NotNullSourceExtractor.Code.NOT_SET))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckGetting_ifNull() {
        NotNullSourceExtractor extractor = new NotNullSourceExtractor();
        extractor.set(null);

        Result<Object> result = extractor.get();
        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(CR.get(NotNullSourceExtractor.Code.IS_NULL))
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
        NotNullSourceExtractor extractor = new NotNullSourceExtractor();
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