package ru.multa.entia.parameters.impl.reader;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.parameters.api.reader.Reader;
import ru.multa.entia.parameters.api.reader.ReaderResult;
import ru.multa.entia.parameters.impl.ids.DefaultId;
import ru.multa.entia.parameters.impl.ids.Ids;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.utils.Results;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultEnvVarReader123Test {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @SneakyThrows
    @Test
    void shouldCheckBuilderVarNameAddition() {
        HashSet<String> expected = new HashSet<>();
        int quantity = Faker.int_().between(10, 20);

        DefaultEnvVarReader123.Builder builder = new DefaultEnvVarReader123.Builder();
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
        Result<Reader> result = DefaultEnvVarReader123.builder().build();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultEnvVarReader123.Code.VAR_NAMES_IS_EMPTY))
                        .back()
                        .compare()
        ).isTrue();
    }

    @SneakyThrows
    @Test
    void shouldCheckBuilding() {
        HashSet<String> expected = new HashSet<>();
        int quantity = Faker.int_().between(10, 20);

        DefaultEnvVarReader123.Builder builder = new DefaultEnvVarReader123.Builder();
        for (int i = 0; i < quantity; i++) {
            String varName = Faker.str_().random();
            builder.addVarName(varName);
            expected.add(varName);
        }
        Result<Reader> result = builder.build();

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();

        Reader reader = result.value();
        Field field = reader.getClass().getDeclaredField("varNames");
        field.setAccessible(true);
        Object gotten = field.get(reader);

        assertThat(gotten).isEqualTo(expected);
    }

    @Test
    void shouldCheckIdGetting() {
        Reader reader = DefaultEnvVarReader123.builder().addVarName(Faker.str_().random()).build().value();

        assertThat(reader.getId()).isEqualTo(new DefaultId(Ids.ENV_VARS, ""));
    }

    @SuppressWarnings("unchecked")
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

        DefaultEnvVarReader123.Builder builder = DefaultEnvVarReader123.builder();
        absenceProperties.forEach(builder::addVarName);
        Reader reader = builder.build().value();

        Result<ReaderResult> result = reader.read();

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();

        ReaderResult readerResult = result.value();
        Function<Object, Set<String>> adapter = o -> {return (Set<String>) o;};

        for (String absenceProperty : absenceProperties) {
            assertThat(readerResult.get(absenceProperty)).isNull();
        }
    }

    @SuppressWarnings("unchecked")
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

        DefaultEnvVarReader123.Builder builder = DefaultEnvVarReader123.builder();
        variables.keySet().forEach(builder::addVarName);
        Reader reader = builder.build().value();

        Result<ReaderResult> result = reader.read();

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();

        ReaderResult readerResult = result.value();
        Function<Object, Set<String>> adapter = o -> {return (Set<String>) o;};

        for (String key : variables.keySet()) {
            assertThat(readerResult.get(key)).isEqualTo(variables.get(key));
        }
    }
}