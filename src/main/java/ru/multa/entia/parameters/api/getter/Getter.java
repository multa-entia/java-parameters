package ru.multa.entia.parameters.api.getter;

import ru.multa.entia.results.api.result.Result;

public interface Getter<I, O> {
    O get(I input);
}
