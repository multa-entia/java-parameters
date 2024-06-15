// TODO: restore
//package ru.multa.entia.parameters.impl.property;
//
//import ru.multa.entia.results.api.repository.CodeRepository;
//import ru.multa.entia.results.api.result.Result;
//import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
//import ru.multa.entia.results.impl.result.DefaultResultBuilder;
//
//public class DefaultNotNullProperty extends AbstractProperty<Object>{
//    public enum Code {
//        IS_NULL
//    }
//
//    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
//    static {
//        CR.update(Code.IS_NULL, "parameters:not-null-property.default:is-null");
//    }
//
//    public DefaultNotNullProperty(final String name) {
//        super(name);
//    }
//
//    @Override
//    protected Result<Object> checkRaw(final Object raw) {
//        return DefaultResultBuilder.<Object>computeFromCodes(
//                () -> {return raw;},
//                () -> {return raw == null ? CR.get(Code.IS_NULL) : null;}
//        );
//    }
//}
