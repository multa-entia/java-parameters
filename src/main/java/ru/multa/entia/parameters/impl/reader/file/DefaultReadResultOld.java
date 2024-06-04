package ru.multa.entia.parameters.impl.reader.file;

import ru.multa.entia.parameters.api.reader.file.ReadResultOld;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

// TODO: del
public record DefaultReadResultOld(String content, Path path, BasicFileAttributes attributes) implements ReadResultOld {
}
