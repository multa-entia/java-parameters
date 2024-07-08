package ru.multa.entia.parameters.impl;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.parameters.api.controllers.ParametersController;
import ru.multa.entia.parameters.api.readers.Reader;
import ru.multa.entia.parameters.api.sources.PropertySource;
import ru.multa.entia.parameters.api.watchers.Watcher;
import ru.multa.entia.parameters.impl.adapters.DefaultDecryptStringPropertyAdapter;
import ru.multa.entia.parameters.impl.adapters.DefaultFloatPropertyAdapter;
import ru.multa.entia.parameters.impl.adapters.DefaultIntegerPropertyAdapter;
import ru.multa.entia.parameters.impl.adapters.DefaultStringPropertyAdapter;
import ru.multa.entia.parameters.impl.controllers.DefaultParametersController;
import ru.multa.entia.parameters.impl.decryptors.DefaultStringDecryptor;
import ru.multa.entia.parameters.impl.ids.DefaultId;
import ru.multa.entia.parameters.impl.properties.DefaultAdaptNotNullProperty;
import ru.multa.entia.parameters.impl.readers.DefaultYmlReader;
import ru.multa.entia.parameters.impl.sources.DefaultPropertySource;
import ru.multa.entia.parameters.impl.watchers.DefaultFileModificationWatcher;
import ru.multa.entia.parameters.impl.watchers.DefaultWatcherEvent;

import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: fill readme too
public class DemoTest {
    private static final String PASSWORD = "secret";

    private static final String DEFAULT_ORIGINAL_TEXT = "original text";
    private static final String DEFAULT_ENCRYPTED_TEXT = "/nWE551ZC4C9Fs6dfA7lV9Y/OGmqorWT";
    private static final String DEFAULT_TEXT_VALUE = "some-text";
    private static final int DEFAULT_INT_VALUE = 123;
    private static final float DEFAULT_FLOAT_VALUE = 123.456f;

    private static final String TEMPLATE =
"""
original_text: %s
encrypted_text: '%s'
text_value: %s
int_value: %s
float_value: %s
""";

    @SneakyThrows
    @Test
    void test() {
        DefaultAdaptNotNullProperty<String> originalTextProperty
                = new DefaultAdaptNotNullProperty<>("original_text", new DefaultStringPropertyAdapter());
        DefaultAdaptNotNullProperty<String> encryptedTextProperty = new DefaultAdaptNotNullProperty<>(
                "encrypted_text",
                new DefaultDecryptStringPropertyAdapter(DefaultStringDecryptor.create(PASSWORD).value())
        );
        DefaultAdaptNotNullProperty<String> textValueProperty
                = new DefaultAdaptNotNullProperty<>("text_value", new DefaultStringPropertyAdapter());
        DefaultAdaptNotNullProperty<Integer> intValueProperty
                = new DefaultAdaptNotNullProperty<>("int_value", new DefaultIntegerPropertyAdapter());
        DefaultAdaptNotNullProperty<Float> floatValueProperty
                = new DefaultAdaptNotNullProperty<>("float_value", new DefaultFloatPropertyAdapter());
        Path path = computeTestFilePath();

        Reader<Map<String, Object>> reader = DefaultYmlReader.builder().path(path).build().value();
        PropertySource source = DefaultPropertySource.builder().reader(reader).build().value();

        ParametersController controller = DefaultParametersController.builder()
                .binding(source, originalTextProperty)
                .binding(source, encryptedTextProperty)
                .binding(source, textValueProperty)
                .binding(source, intValueProperty)
                .binding(source, floatValueProperty)
                .build()
                .value();
        controller.start();

        Watcher watcher = DefaultFileModificationWatcher.create(path).value();
        watcher.addListener(controller);

        source.update(
                DefaultWatcherEvent.modified(
                        DefaultId.createIdForFile(path)
                )
        );

        System.out.println("\n### STEP 0 ###");
        System.out.printf("original_text: %s\n", originalTextProperty.get());
        System.out.printf("encrypted_text: %s\n", encryptedTextProperty.get());
        System.out.printf("text_value: %s\n", textValueProperty.get());
        System.out.printf("int_value: %s\n", intValueProperty.get());
        System.out.printf("float_value: %s\n", floatValueProperty.get());
        assertThat(originalTextProperty.get().value()).isEqualTo(DEFAULT_ORIGINAL_TEXT);
        assertThat(encryptedTextProperty.get().value()).isEqualTo(DEFAULT_ORIGINAL_TEXT);
        assertThat(textValueProperty.get().value()).isEqualTo(DEFAULT_TEXT_VALUE);
        assertThat(intValueProperty.get().value()).isEqualTo(DEFAULT_INT_VALUE);
        assertThat(floatValueProperty.get().value()).isEqualTo(DEFAULT_FLOAT_VALUE);

        String newTextValue = Faker.str_().fromTemplate("[a-z]{5:10}");
        Integer newIntValue = Faker.int_().between(10, 100);
        float newFloatValue = (float) Faker.int_().between(1000, 2000) / 10.0f;
        new Thread(() -> {
            // TODO: rewrite file
        }).start();

        Thread.sleep(100);

        // TODO: check

        // TODO: write original content
    }

    private Path computeTestFilePath() {
        String path = System.getProperty("user.dir")
                + "\\src\\test\\resources\\"
                + getClass().getPackageName().replace('.', '\\')
                + "\\" + getClass().getSimpleName() + ".yml";
        return Path.of(path);
    }
}
