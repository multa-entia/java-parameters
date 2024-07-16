# java-parameters

Library for working with external parameter sources

# Example
Working example in DemoTest

We have a configuration file with some parameters that can be changed over time.
```yaml
original_text: original text
encrypted_text: '/nWE551ZC4C9Fs6dfA7lV9Y/OGmqorWT'
text_value: some-text
int_value: 123
float_value: 123.456
```

For access to file we need path and id
```java
Path path = computeTestFilePath();
Id id = DefaultId.createIdForFile(path, "config");
```

We may create several property-object
```java
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
```

Next step, we are creating source of properties
```java
// path - path to config file
Reader<Map<String, Object>> reader = DefaultYmlReader.builder().path(path).id(id).build().value();
PropertySource source = DefaultPropertySource.builder().reader(reader).build().value();
```

Then, we are creating watcher and source controller
```java
// path - path to config file
ParametersController controller = DefaultParametersController.builder()
    .binding(source, originalTextProperty)
    .binding(source, encryptedTextProperty)
    .binding(source, textValueProperty)
    .binding(source, intValueProperty)
    .binding(source, floatValueProperty)
    .build()
    .value();
controller.start();

Watcher watcher = DefaultFileModificationWatcher.create(path, id).value();
watcher.addListener(controller);
watcher.start();
```

For first config-file reading we may call next:
```java
source.update(DefaultWatcherEvent.modified(id));
```
