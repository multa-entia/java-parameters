package ru.multa.entia.parameters.impl.reader.file;

import ru.multa.entia.parameters.api.reader.file.ReadResult;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public record DefaultReadResult(String content, Path path, BasicFileAttributes attributes) implements ReadResult {
}
