package ru.multa.entia.parameters.impl.readers;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.parameters.api.ids.Id;
import ru.multa.entia.parameters.api.readers.Reader;
import ru.multa.entia.parameters.impl.ids.DefaultId;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.utils.Results;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultEnvVarReaderTest {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @SneakyThrows
    @Test
    void shouldCheckBuilderVarNameAddition() {
        HashSet<String> expected = new HashSet<>();
        int quantity = Faker.int_().between(10, 20);

        DefaultEnvVarReader.Builder builder = DefaultEnvVarReader.builder();
        for (int i = 0; i < quantity; i++) {
            String varName = Faker.str_().random();
            builder.addVarName(varName);
            expected.add(varName);
        }
        Field field = builder.getClass().getDeclaredField("varNames");
        field.setAccessible(true);
        Object gotten = field.get(builder);

        assertThat(gotten).isEqualTo(expected);
    }

    @Test
    void shouldCheckBuilding_ifVarNamesIsEmpty() {
        Result<Reader<Map<String, Object>>> result = DefaultEnvVarReader.builder().build();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultEnvVarReader.Code.VAR_NAMES_IS_EMPTY))
                        .back()
                        .compare()
        ).isTrue();
    }

    @SneakyThrows
    @Test
    void shouldCheckBuilding_ifIdNotSet() {
        int quantity = Faker.int_().between(10, 20);

        DefaultEnvVarReader.Builder builder = DefaultEnvVarReader.builder();
        for (int i = 0; i < quantity; i++) {
            String varName = Faker.str_().random();
            builder.addVarName(varName);
        }
        Result<Reader<Map<String, Object>>> result = builder.build();

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();

        assertThat(result.value().getId()).isEqualTo(DefaultId.createIdForEnvVar());
    }

    @SneakyThrows
    @Test
    void shouldCheckBuilding() {
        HashSet<String> expected = new HashSet<>();
        int quantity = Faker.int_().between(10, 20);

        DefaultEnvVarReader.Builder builder = DefaultEnvVarReader.builder();
        for (int i = 0; i < quantity; i++) {
            String varName = Faker.str_().random();
            builder.addVarName(varName);
            expected.add(varName);
        }
        Result<Reader<Map<String, Object>>> result = builder.build();

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();

        Reader<Map<String, Object>> reader = result.value();
        Field field = reader.getClass().getDeclaredField("varNames");
        field.setAccessible(true);
        Object gotten = field.get(reader);

        assertThat(gotten).isEqualTo(expected);
    }

    @Test
    void shouldCheckIdGetting() {
        Id expectedId = DefaultId.createIdForEnvVar(Faker.str_().random());
        Reader<Map<String, Object>> reader = DefaultEnvVarReader.builder()
                .addVarName(Faker.str_().random())
                .id(expectedId).build()
                .value();

        assertThat(reader.getId()).isEqualTo(expectedId);
    }

    @Test
    void shouldCheckReading_ifPropertyAbsence() {
        int quantity = Faker.int_().between(10, 20);
        HashSet<String> absenceProperties = new HashSet<>();
        while (true) {
            String property = Faker.str_().random();
            if (System.getProperty(property) == null) {
                absenceProperties.add(property);
                if (absenceProperties.size() == quantity) {
                    break;
                }
            }
        }

        DefaultEnvVarReader.Builder builder = DefaultEnvVarReader.builder();
        absenceProperties.forEach(builder::addVarName);
        Reader<Map<String, Object>> reader = builder.build().value();

        Result<Map<String, Object>> result = reader.read();

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();

        assertThat(result.value()).isEmpty();
    }

    @Test
    void shouldCheckReading() {
        int quantity = Faker.int_().between(2, 5);
        HashMap<String, String> variables = new HashMap<>();
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            variables.put(entry.getKey(), entry.getValue());
            if (variables.size() == quantity) {
                break;
            }
        }

        DefaultEnvVarReader.Builder builder = DefaultEnvVarReader.builder();
        variables.keySet().forEach(builder::addVarName);
        Reader<Map<String, Object>> reader = builder.build().value();

        Result<Map<String, Object>> result = reader.read();

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();

        Map<String, Object> properties = result.value();
        assertThat(properties).isEqualTo(variables);
    }
}