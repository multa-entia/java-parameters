package ru.multa.entia.parameters.impl.reader.file;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.multa.entia.fakers.impl.Faker;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultReadResultOldTest {

    @Test
    void shouldCheckContentGetting() {
        String expectedContent = Faker.str_().random();
        DefaultReadResultOld readResult = new DefaultReadResultOld(expectedContent, null, null);

        assertThat(readResult.content()).isEqualTo(expectedContent);
    }

    @Test
    void shouldCheckPathGetting() {
        Path expectedPath = Path.of("/opt");
        DefaultReadResultOld readResult = new DefaultReadResultOld(null, expectedPath, null);

        assertThat(readResult.path()).isEqualTo(expectedPath);
    }

    @Test
    void shouldCheckAttributesGetting() {
        Supplier<BasicFileAttributes> basicFileAttributesSupplier = () -> {
            return Mockito.mock(BasicFileAttributes.class);
        };
        BasicFileAttributes expectedAttributes = basicFileAttributesSupplier.get();
        DefaultReadResultOld readResult = new DefaultReadResultOld(null, null, expectedAttributes);

        assertThat(readResult.attributes()).isEqualTo(expectedAttributes);
    }
}