package ru.multa.entia.parameters.impl.controllers;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ru.multa.entia.fakers.impl.Faker;
import ru.multa.entia.parameters.api.controllers.ParametersController;
import ru.multa.entia.parameters.api.ids.Id;
import ru.multa.entia.parameters.api.properties.Property;
import ru.multa.entia.parameters.api.sources.PropertySource;
import ru.multa.entia.parameters.impl.ids.DefaultId;
import ru.multa.entia.parameters.impl.watchers.DefaultWatcherEvent;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;
import ru.multa.entia.results.utils.Results;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultParametersControllerTest {
    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();

    private static final Supplier<PropertySource> PROPERTY_SOURCE_SUPPLIER = () -> {
        return Mockito.mock(PropertySource.class);
    };
    private static final Supplier<Property<?>> PROPERTY_SUPPLIER = () -> {
        return Mockito.mock(TestProperty.class);
    };

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    void shouldCheckBuildingBinding_ifBothSourceAndPropertyNull() {
        DefaultParametersController.Builder builder = DefaultParametersController.builder().binding(null, null);

        Field field = builder.getClass().getDeclaredField("propertySources");
        field.setAccessible(true);
        Map<Id, PropertySource> gotten = (Map<Id, PropertySource>) field.get(builder);

        assertThat(gotten).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    void shouldCheckBuildingBinding_ifSourceNull() {
        DefaultParametersController.Builder builder = DefaultParametersController.builder().binding(null, PROPERTY_SUPPLIER.get());

        Field field = builder.getClass().getDeclaredField("propertySources");
        field.setAccessible(true);
        Map<Id, PropertySource> gotten = (Map<Id, PropertySource>) field.get(builder);

        assertThat(gotten).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    void shouldCheckBuildingBinding_ifPropertyNull() {
        DefaultParametersController.Builder builder = DefaultParametersController.builder().binding(PROPERTY_SOURCE_SUPPLIER.get(), null);

        Field field = builder.getClass().getDeclaredField("propertySources");
        field.setAccessible(true);
        Map<Id, PropertySource> gotten = (Map<Id, PropertySource>) field.get(builder);

        assertThat(gotten).isEmpty();
    }

    @Test
    void shouldCheckBuildingBinding_ifPropertySourcesIsEmpty() {
        Result<ParametersController> result = DefaultParametersController.builder().build();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator().code(CR.get(DefaultParametersController.Code.PROPERTY_SOURCES_IS_EMPTY))
                        .back()
                        .compare()
        ).isTrue();
    }

    @SneakyThrows
    @Test
    void shouldCheckBuildingBinding() {
        ConcurrentHashMap<PropertySource, Set<Property<?>>> holder = new ConcurrentHashMap<>();

        Property<?> property00 = PROPERTY_SUPPLIER.get();
        Property<?> property01 = PROPERTY_SUPPLIER.get();

        Property<?> property10 = PROPERTY_SUPPLIER.get();
        Property<?> property11 = PROPERTY_SUPPLIER.get();
        Property<?> property12 = PROPERTY_SUPPLIER.get();

        Function<Id, PropertySource> propertySourceSupplier = id -> {
            PropertySource source = Mockito.mock(PropertySource.class);
            Mockito
                    .when(source.register(Mockito.any()))
                    .thenAnswer(new Answer<Result<Property<?>>>() {
                        @Override
                        public Result<Property<?>> answer(InvocationOnMock invocationOnMock) throws Throwable {
                            Property<?> argument = invocationOnMock.getArgument(0);
                            if (holder.containsKey(source)) {
                                holder.get(source).add(argument);
                            } else {
                                holder.put(source, new HashSet<Property<?>>(){{add(argument);}});
                            }
                            return null;
                        }
                    });
            Mockito.when(source.getId()).thenReturn(id);

            return source;
        };
        PropertySource source0 = propertySourceSupplier.apply(DefaultId.createIdForFile(Path.of("/opt")));
        PropertySource source1 = propertySourceSupplier.apply(DefaultId.createIdForFile(Path.of("/home")));

        HashMap<PropertySource, Set<Property<?>>> expectedHolder = new HashMap<>() {{
            put(source0, Set.of(property00, property01));
            put(source1, Set.of(property10, property11, property12));
        }};
        Map<Id, PropertySource> expectedPropertySources = Map.of(source0.getId(), source0, source1.getId(), source1);

        Result<ParametersController> result = DefaultParametersController.builder()
                .binding(source0, property00)
                .binding(source0, property01)
                .binding(source1, property10)
                .binding(source1, property11)
                .binding(source1, property12)
                .build();

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .seedsComparator().isNull()
                        .back()
                        .compare()
        ).isTrue();

        ParametersController controller = result.value();
        assertThat(controller).isNotNull();

        Field field = controller.getClass().getDeclaredField("propertySources");
        field.setAccessible(true);
        Object gotten = field.get(controller);
        assertThat(gotten).isEqualTo(expectedPropertySources);

        assertThat(holder).isEqualTo(expectedHolder);
    }

    @Test
    void shouldCheckStarting() {
        ParametersController controller = DefaultParametersController.builder()
                .binding(PROPERTY_SOURCE_SUPPLIER.get(), PROPERTY_SUPPLIER.get())
                .build()
                .value();

        Result<Object> result = controller.start();

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .value(null)
                        .seedsComparator().isNull()
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckStarting_ifAlreadyStarted() {
        ParametersController controller = DefaultParametersController.builder()
                .binding(PROPERTY_SOURCE_SUPPLIER.get(), PROPERTY_SUPPLIER.get())
                .build()
                .value();

        controller.start();
        Result<Object> result = controller.start();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator().code(CR.get(DefaultParametersController.Code.ALREADY_STARTED))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckStopping_ifAlreadyStopped() {
        ParametersController controller = DefaultParametersController.builder()
                .binding(PROPERTY_SOURCE_SUPPLIER.get(), PROPERTY_SUPPLIER.get())
                .build()
                .value();

        Result<Object> result = controller.stop();

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator().code(CR.get(DefaultParametersController.Code.ALREADY_STOPPED))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckStopping() {
        ParametersController controller = DefaultParametersController.builder()
                .binding(PROPERTY_SOURCE_SUPPLIER.get(), PROPERTY_SUPPLIER.get())
                .build()
                .value();

        controller.start();
        Result<Object> result = controller.stop();

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .value(null)
                        .seedsComparator().isNull()
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckListenerNotification_ifPropertySourceAbsence() {
        Id nonExistId = DefaultId.createIdForFile(Path.of("/home"));
        Id existId = DefaultId.createIdForFile(Path.of("/opt"));
        Supplier<PropertySource> propertySourceSupplier = () -> {
            PropertySource source = Mockito.mock(PropertySource.class);
            Mockito.when(source.getId()).thenReturn(existId);

            return source;
        };

        ParametersController controller = DefaultParametersController.builder()
                .binding(propertySourceSupplier.get(), PROPERTY_SUPPLIER.get())
                .build()
                .value();

        controller.start();
        Result<Object> result = controller.notifyListener(DefaultWatcherEvent.modified(nonExistId));

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator().code(CR.get(DefaultParametersController.Code.SOURCE_PROPERTY_ABSENCE))
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckListenerNotification_ifFailOnPropertySourceUpdating() {
        String expectedCode = Faker.str_().random();
        Id existId = DefaultId.createIdForFile(Path.of("/opt"));
        Supplier<PropertySource> propertySourceSupplier = () -> {
            Result<Object> result = DefaultResultBuilder.<Object>fail(expectedCode);
            PropertySource source = Mockito.mock(PropertySource.class);
            Mockito.when(source.getId()).thenReturn(existId);
            Mockito.when(source.update(Mockito.any())).thenReturn(result);

            return source;
        };

        ParametersController controller = DefaultParametersController.builder()
                .binding(propertySourceSupplier.get(), PROPERTY_SUPPLIER.get())
                .build()
                .value();

        controller.start();
        Result<Object> result = controller.notifyListener(DefaultWatcherEvent.modified(existId));

        assertThat(
                Results.comparator(result)
                        .isFail()
                        .value(null)
                        .seedsComparator().code(expectedCode)
                        .back()
                        .compare()
        ).isTrue();
    }

    @Test
    void shouldCheckListenerNotification() {
        AtomicBoolean holder = new AtomicBoolean(false);
        Id existId = DefaultId.createIdForFile(Path.of("/opt"));
        Supplier<PropertySource> propertySourceSupplier = () -> {
            PropertySource source = Mockito.mock(PropertySource.class);
            Mockito.when(source.getId()).thenReturn(existId);
            Mockito
                    .when(source.update(Mockito.any()))
                    .thenAnswer(new Answer<Result<Object>>() {
                        @Override
                        public Result<Object> answer(InvocationOnMock invocationOnMock) throws Throwable {
                            holder.set(true);
                            return DefaultResultBuilder.<Object>ok();
                        }
                    });

            return source;
        };

        ParametersController controller = DefaultParametersController.builder()
                .binding(propertySourceSupplier.get(), PROPERTY_SUPPLIER.get())
                .build()
                .value();

        controller.start();
        Result<Object> result = controller.notifyListener(DefaultWatcherEvent.modified(existId));

        assertThat(
                Results.comparator(result)
                        .isSuccess()
                        .value(null)
                        .seedsComparator().isNull()
                        .back()
                        .compare()
        ).isTrue();
        assertThat(holder.get()).isTrue();
    }

    private interface TestProperty extends Property<String> {}
}