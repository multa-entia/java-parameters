package ru.multa.entia.parameters.api.reader.file;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public interface ReadResult {
    String content();
    Path path();
    BasicFileAttributes attributes();
}
