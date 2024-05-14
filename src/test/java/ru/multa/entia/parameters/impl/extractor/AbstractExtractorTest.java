package ru.multa.entia.parameters.impl.extractor;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.result.Result;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractExtractorTest {

    @SneakyThrows
    @Test
    void shouldCheckSetting() {
        String expected = Faker.str_().random();
        AbstractExtractor<Object> extractor = new AbstractExtractor<>("") {
            @Override
            public Result<Object> get() {
                return null;
            }
        };
        extractor.set(expected);

        assertThat(extractor.raw).isEqualTo(expected);
        assertThat(extractor.isSet()).isTrue();
    }
}