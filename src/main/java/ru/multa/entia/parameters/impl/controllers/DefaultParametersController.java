package ru.multa.entia.parameters.impl.controllers;

import ru.multa.entia.parameters.api.controllers.ParametersController;
import ru.multa.entia.parameters.api.ids.Id;
import ru.multa.entia.parameters.api.properties.Property;
import ru.multa.entia.parameters.api.sources.PropertySource;
import ru.multa.entia.parameters.api.watchers.WatcherEvent;
import ru.multa.entia.results.api.repository.CodeRepository;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.impl.repository.DefaultCodeRepository;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultParametersController implements ParametersController {
    public enum Code {
        PROPERTY_SOURCES_IS_EMPTY,
        ALREADY_STARTED,
        ALREADY_STOPPED,
        SOURCE_PROPERTY_ABSENCE
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.PROPERTY_SOURCES_IS_EMPTY, "parameters:parameters-controller.default:property-sources-is-empty");
        CR.update(Code.ALREADY_STARTED, "parameters:parameters-controller.default:already-started");
        CR.update(Code.ALREADY_STOPPED, "parameters:parameters-controller.default:already-stopped");
        CR.update(Code.SOURCE_PROPERTY_ABSENCE, "parameters:parameters-controller.default:source-property-absence");
    }

    private final Map<Id, PropertySource> propertySources;
    private final AtomicBoolean doing = new AtomicBoolean(false);

    public static Builder builder() {
        return new Builder();
    }

    private DefaultParametersController(final Map<Id, PropertySource> propertySources) {
        this.propertySources = propertySources;
    }

    @Override
    public Result<Object> start() {
        return doing.compareAndSet(false, true)
                ? DefaultResultBuilder.<Object>ok()
                : DefaultResultBuilder.<Object>fail(CR.get(Code.ALREADY_STARTED));
    }

    @Override
    public Result<Object> stop() {
        return doing.compareAndSet(true, false)
                ? DefaultResultBuilder.<Object>ok()
                : DefaultResultBuilder.<Object>fail(CR.get(Code.ALREADY_STOPPED));
    }

    @Override
    public Result<Object> notifyListener(final WatcherEvent watcherEvent) {
        Id id = watcherEvent.watcherId();
        if (propertySources.containsKey(id)) {
            Result<Object> result = propertySources.get(id).update(watcherEvent);
            return result.ok() ? DefaultResultBuilder.<Object>ok() : result;
        }

        return DefaultResultBuilder.<Object>fail(CR.get(Code.SOURCE_PROPERTY_ABSENCE));
    }

    public static class Builder {
        private final Map<Id, PropertySource> propertySources = new HashMap<>();

        public Builder binding(final PropertySource propertySource,
                               final Property<?> property) {
            if (propertySource != null && property != null){
                propertySource.register(property);
                propertySources.put(propertySource.getId(), propertySource);
            }

            return this;
        }

        public Result<ParametersController> build() {
            return propertySources.isEmpty()
                    ? DefaultResultBuilder.<ParametersController>fail(CR.get(Code.PROPERTY_SOURCES_IS_EMPTY))
                    : DefaultResultBuilder.<ParametersController>ok(new DefaultParametersController(propertySources));
        }
    }
}
