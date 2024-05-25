package ru.multa.entia.parameters.impl.source;

import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.utils.Results;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultYamlSourceAdapterTest {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @Test
    void shouldCheckAdapt_ifInputIsNull() {
        Result<Map<String, Object>> result = new DefaultYamlSourceAdapter().adapt(null);

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultYamlSourceAdapter.Code.INPUT_IS_NULL))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckAdapt_ifInputIsInvalid() {
        String input =
                """
                intValue: 123floatValue: 123.45
                """;

        Result<Map<String, Object>> result = new DefaultYamlSourceAdapter().adapt(input);

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultYamlSourceAdapter.Code.SYNTAX_ERROR))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckAdapt() {
        String nonExistKey = "nonExist";
        String intKey = "int_value";
        String doubleKey = "float_value";
        String booleanKey = "boolean_key";
        String listKey = "list_value";
        Integer intValue = Faker.int_().random();
        double doubleValue = ((double) Faker.int_().between(0, 1000)) / 100.0;
        boolean booleanValue = Faker.int_().random(0, 1000) % 2 == 0;
        String item0 = "_" + Faker.int_().random();
        String item1 = "_" + Faker.int_().random();
        String item2 = "_" + Faker.int_().random();
        String template =
                """
                %s: %s
                %s: %s
                %s: %s
                %s: [%s, %s, %s]
                """;
        String input = String.format(
                template,
                intKey, intValue,
                doubleKey, doubleValue,
                booleanKey, booleanValue,
                listKey, item0, item1, item2);

        HashMap<String, Object> expectedValue = new HashMap<>() {{
            put(intKey, intValue);
            put(doubleKey, doubleValue);
            put(booleanKey, booleanValue);
            put(listKey, List.of(item0, item1, item2));
        }};

        Result<Map<String, Object>> result = new DefaultYamlSourceAdapter().adapt(input);

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .value(expectedValue)
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();

        assertThat(result.value().containsKey(nonExistKey)).isFalse();
    }
}