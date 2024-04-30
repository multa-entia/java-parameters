package ru.multa.entia.parameters.impl.source;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.utils.Results;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultSourceExtractorTest {

    @ParameterizedTest
    @CsvSource(value = {
            "null",
            "str",
            "123"
    },
    nullValues = "null")
    void shouldCheckGetting(Object raw) {
        DefaultSourceExtractor extractor = new DefaultSourceExtractor();
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