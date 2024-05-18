package ru.multa.entia.parameters.impl.extractor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.utils.Results;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultExtractorOldTest {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @Test
    void shouldCheckGetting_ifNotSet() {
        DefaultExtractorOld extractor = new DefaultExtractorOld("");

        Result<Object> result = extractor.get();
        assertThat(
                Results.comparator(result)
                        .isFail()
                        .seedsComparator()
                        .code(CR.get(DefaultExtractorOld.Code.NOT_SET))
                        .back()
                        .compare()
        ).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {
            "null",
            "str",
            "123"
    },
    nullValues = "null")
    void shouldCheckGetting(Object raw) {
        DefaultExtractorOld extractor = new DefaultExtractorOld("");
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