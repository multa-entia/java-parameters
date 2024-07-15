package ru.multa.entia.parameters.impl.adapters;

import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.function.Function;

public class DefaultFloatPropertyAdapter implements Function<Object, Result<Float>> {
    public enum Code {
        INVALID
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.INVALID, "parameters:float-property.default:invalid");
    }

    @Override
    public Result<Float> apply(final Object object) {
        if (object == null) {
            return DefaultResultBuilder.<Float>ok();
        }
        try {
            return DefaultResultBuilder.<Float>ok(Float.parseFloat(String.valueOf(object)));
        } catch (NumberFormatException ex) {
            return new DefaultResultBuilder<Float>()
                    .success(false)
                    .seedBuilder()
                    .code(CR.get(Code.INVALID))
                    .addLastArgs(object, ex.getMessage())
                    .apply().build();
        }
    }
}
