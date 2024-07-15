
# v0.3

## Common
- There was fundamental refactoring

## Reader

There is added interface __Reader__ & its implementations. Interface __Reader__ makes possible to read sth.

### __DefaultFileReader__
__DefaultFileReader__ is an implementation of __Reader__ that makes it possible to read file by path.

### __DefaultYmlReader__
__DefaultYmlReader__ is a wrapper for __DefaultFileReader__. It is serving for *.yml file reading.

### __DefaultEnvVarReader__
__DefaultEnvVarReader__ is an environment variables reader.

## Source

__DefaultPropertySource__ is thread safe source of properties. 
Properties are being registered in source.
Source is being updated from controller of parameters.
__DefaultPropertySource__ uses implementations of __Reader__ for getting of vales.

## Property

__Property__ is a wrapper for holding of value. 
Also, it has ability of value updating with checking.

### __DefaultAdaptNullableProperty__
__DefaultAdaptNullableProperty__ is a generic implementation of property,
where set value is being adapted with Function<> except null.
Null is being passed directly.

### __DefaultAdaptNotNullProperty__
__DefaultAdaptNotNullProperty__ is a generic implementation of property,
where set value is being adapted with Function<>.
In case of null, instance returns error.

### __DefaultCastNullableProperty__
__DefaultCastNullableProperty__ is a generic implementation of property,
where set value is being cast except null.
Null is being passed directly.

### __DefaultCastNotNullProperty__
__DefaultCastNullableProperty__ is a generic implementation of property,
where set value is being cast except null.
In case of null, instance returns error.

## Encryptor

### __DefaultStringEncryptor__
__DefaultStringEncryptor__ is a class-wrapper above StandardPBEStringEncryptor,
which makes an encryption.

## Decryptor

### __DefaultStringDecryptor__
__DefaultStringDecryptor__ is class-wrapper above StandardPBEStringEncryptor,
which makes a decryption.

## Adapter
__Adapters__ are being used in __DefaultAdaptNotNullProperty__ & __DefaultAdaptNullableProperty__
for adaptation of set value.

There are the following implementations:
- __DefaultIntegerPropertyAdapter__
- __DefaultFloatPropertyAdapter__
- __DefaultStringPropertyAdapter__
- __DefaultDecryptStringPropertyAdapter__

## Id
__DefaultId__ is an identifier, which connects __Readers__, __Property sources__ and __Watchers__.

## Watcher
__Watcher__ is an entity which makes observation to sth.

There are the following implementations:
- __DefaultFileModificationWatcher__

## Watcher event
It contains ID of watcher and kind of event.

## Controller of parameters
It is a bridge between the source of the property and the watcher.
It receives notification from the observer and causes the corresponding
property source to be updated.

# v0.2

- DefaultStringEncryptor is added

# v0.1

- DefaultStringDecryptor is added
- DefaultEnvVarGetter is added