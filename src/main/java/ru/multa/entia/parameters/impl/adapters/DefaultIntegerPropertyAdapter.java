package ru.multa.entia.parameters.impl.adapters;

import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.function.Function;

public class DefaultIntegerPropertyAdapter implements Function<Object, Result<Integer>> {
    public enum Code {
        INVALID
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.INVALID, "parameters:int-property.default:invalid");
    }

    @Override
    public Result<Integer> apply(final Object object) {
        if (object == null) {
            return DefaultResultBuilder.<Integer>ok();
        }
        try {
            return DefaultResultBuilder.<Integer>ok(Integer.parseInt(String.valueOf(object)));
        } catch (NumberFormatException ex) {
            return new DefaultResultBuilder<Integer>()
                    .success(false)
                    .seedBuilder()
                    .code(CR.get(Code.INVALID))
                    .addLastArgs(object, ex.getMessage())
                    .apply().build();
        }
    }
}
