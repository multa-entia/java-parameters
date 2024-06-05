package ru.multa.entia.parameters.api.reader;

import java.util.function.Function;

public interface ReaderResult {
    String get(String property);
    <T> T getAs(String property, Function<Object, T> adapter);
}
