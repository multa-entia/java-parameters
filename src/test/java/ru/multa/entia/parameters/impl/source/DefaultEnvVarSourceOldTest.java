package ru.multa.entia.parameters.impl.source;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.parameters.api.extractor.ExtractorOld;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.utils.Results;

import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultEnvVarSourceOldTest {

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    @Test
    void shouldCheckGetting_ifAbsence() {
        Supplier<TestFunction> testFunctionSupplier = () -> {
            TestFunction function = Mockito.mock(TestFunction.class);
            Mockito.when(function.apply(Mockito.any())).thenReturn(null);

            return function;
        };

        DefaultEnvVarSourceOld source = new DefaultEnvVarSourceOld(testFunctionSupplier.get());
        Result<Object> result = source.get(new TestExtractorOld(Faker.str_().random()));

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator()
                        .code(CR.get(DefaultEnvVarSourceOld.Code.PROPERTY_IS_ABSENCE))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckGetting() {
        String rawValue = Faker.str_().random();
        Supplier<TestFunction> testFunctionSupplier = () -> {
            TestFunction function = Mockito.mock(TestFunction.class);
            Mockito.when(function.apply(Mockito.any())).thenReturn(rawValue);

            return function;
        };

        TestExtractorOld extractor = new TestExtractorOld(Faker.str_().random());
        DefaultEnvVarSourceOld source = new DefaultEnvVarSourceOld(testFunctionSupplier.get());
        Result<Object> result = source.get(extractor);

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();

        assertThat(
                Results.comparator(extractor.get())
                        .isSuccess()
                        .value(rawValue)
                        .seedsComparator()
                        .isNull()
                        .back()
                        .compare()
        ).isTrue();
    }

    public interface TestFunction extends Function<String, String> {}

    @RequiredArgsConstructor
    public static class TestExtractorOld implements ExtractorOld<String> {
        private Object object;
        @Getter
        private final String property;

        @Override
        public void set(final Object object) {
            this.object = object;
        }

        @Override
        public Result<String> get() {
            return DefaultResultBuilder.<String>ok(String.valueOf(object));
        }
    }
}