package ru.multa.entia.parameters.api.reader.file;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

// TODO: del
public interface ReadResultOld {
    String content();
    Path path();
    BasicFileAttributes attributes();
}
