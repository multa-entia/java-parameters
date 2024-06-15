// TODO: restore
//package ru.multa.entia.parameters.impl.property;
//
//import ru.multa.entia.results.api.repository.CodeRepository;
//import ru.multa.entia.results.api.result.Result;
//import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
//import ru.multa.entia.results.impl.result.DefaultResultBuilder;
//
//public class DefaultIntegerProperty extends AbstractProperty<Integer>{
//    public enum Code {
//        IS_NULL,
//        INVALID_RAW
//    }
//
//    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
//    static {
//        CR.update(Code.IS_NULL, "parameters:int-property.default:is-null");
//        CR.update(Code.INVALID_RAW, "parameters:int-property.default:invalid-raw");
//    }
//
//    public DefaultIntegerProperty(final String name) {
//        super(name);
//    }
//
//    @Override
//    protected Result<Integer> checkRaw(final Object raw) {
//        if (raw == null) {
//            return DefaultResultBuilder.<Integer>fail(CR.get(Code.IS_NULL));
//        }
//
//        try {
//            return DefaultResultBuilder.<Integer>ok(Integer.parseInt(String.valueOf(raw)));
//        } catch (NumberFormatException ex) {
//            return DefaultResultBuilder.<Integer>fail(CR.get(Code.INVALID_RAW));
//        }
//    }
//}
