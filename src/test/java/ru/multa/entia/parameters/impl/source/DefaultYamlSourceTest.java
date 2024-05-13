package ru.multa.entia.parameters.impl.source;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.yaml.snakeyaml.Yaml;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.parameters.api.extractor.Extractor;
import ru.multa.entia.parameters.api.reader.Reader;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.utils.Results;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultYamlSourceTest {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    private interface TestExtractor extends Extractor<String> {}
    private static final Supplier<Extractor<String>> EXTRACTOR_SUPPLIER = () -> {
        return Mockito.mock(TestExtractor.class);
    };

    @Test
    void shouldCheckGetting_ifReaderNull() {
        DefaultYamlSource source = new DefaultYamlSource(null);
        Result<Object> result = source.get(EXTRACTOR_SUPPLIER.get());

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultYamlSource.Code.READER_NOT_SET))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckGetting_ifReaderReturnFail() {
        Result<String> cause = DefaultResultBuilder.<String>fail(Faker.str_().random());
        Supplier<Reader> readerSupplier = () -> {
            Reader reader = Mockito.mock(Reader.class);
            Mockito.when(reader.read()).thenReturn(cause);

            return reader;
        };

        DefaultYamlSource source = new DefaultYamlSource(readerSupplier.get());
        Result<Object> result = source.get(EXTRACTOR_SUPPLIER.get());

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .causes(List.of(cause))
                        .seedsComparator()
                        .code(CR.get(DefaultYamlSource.Code.READER_RETURN_FAIL))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckGetting_ifSyntaxError() {
        String raw =
"""
intValue: 123floatValue: 123.45
""";

        Result<String> readerResult = DefaultResultBuilder.<String>ok(raw);
        Supplier<Reader> readerSupplier = () -> {
            Reader reader = Mockito.mock(Reader.class);
            Mockito.when(reader.read()).thenReturn(readerResult);

            return reader;
        };

        DefaultYamlSource source = new DefaultYamlSource(readerSupplier.get());
        Result<Object> result = source.get(EXTRACTOR_SUPPLIER.get());

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultYamlSource.Code.SYNTAX_ERROR))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckGetting() {

    }

    @Test
    void shouldCheckGetting_secondTime() {
        String intKey = "int_value";
        String floatKey = "float_value";
        String booleanKey = "boolean_key";
        String listKey = "list_value";
        Integer intValue = Faker.int_().random();
        float floatValue = ((float) Faker.int_().between(0, 1000)) / 100.0f;
        boolean booleanValue = Faker.int_().random(0, 1000) % 2 == 0;
        String item0 = Faker.str_().random();
        String item1 = Faker.str_().random();
        String item2 = Faker.str_().random();
        String template =
"""
%s: %s
%s: %s
%s: %s
%s: [%s, %s, %s]
""";
        String raw = String.format(
                template,
                intKey, intValue,
                floatKey, floatValue,
                booleanKey, booleanValue,
                listKey, item0, item1, item2);

        Result<String> readerResult = DefaultResultBuilder.<String>ok(raw);
        Supplier<Reader> readerSupplier = () -> {
            Reader reader = Mockito.mock(Reader.class);
            Mockito.when(reader.read()).thenReturn(readerResult);

            return reader;
        };

        DefaultYamlSource source = new DefaultYamlSource(readerSupplier.get());

        Extractor<String> testExtractor = new Extractor<>() {
            private Object object;

            @Override
            public void set(Object object) {
                this.object = object;
            }

            @Override
            public Result<String> get() {
                return DefaultResultBuilder.<String>ok(String.valueOf(object));
            }
        };

        source.get(testExtractor);
        System.out.println(testExtractor);
        // TODO: !!!
//
//        assertThat(
//                Results.comparator(result)
//                        .isSuccess()
//                        .seedsComparator()
//                        .isNull()
//                        .back()
//                        .compare()
//        ).isTrue();
    }

    // TODO: del
    @Test
    void test() {

        String source =
"""
int_value: 123
float_value: 123.56
boolean_value: true
list_value_0: [val0, val1, val2]
list_value_1:
  - val3
  - val4
  - val5
str_value_0: hello
str_value_1: "world"
str_value_2: |
   Hello
   world
   !!!
object_values_0:
  dan:
    name: Dan
    age: 21
  dora:
    name: Dora
    age: 22
#bad_int: 123float_v: 123.78
    name: Dora
    age: 221
joe:
  name: J
  age: 100
list_value_3: val0, val1, val2
list_value_11:
  val3
  val4
  val5
""";

        Yaml yaml = new Yaml();
        Map<String, Object> object = yaml.load(source);
        for (Map.Entry<String, Object> entry : object.entrySet()) {
            System.out.printf("%s : %s (%s)\n", entry.getKey(), entry.getValue(), entry.getValue().getClass());
        }
    }
}