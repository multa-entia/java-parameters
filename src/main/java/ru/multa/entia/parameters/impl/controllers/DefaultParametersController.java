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
        PROPERTY_SOURCES_IS_EMPTY
    }

    private static final CodeRepository CR = DefaultCodeRepository.getDefaultInstance();
    static {
        CR.update(Code.PROPERTY_SOURCES_IS_EMPTY, "parameters:parameters-controller.default:property-sources-is-empty");
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
        // TODO: impl
        return null;
    }

    @Override
    public Result<Object> stop() {
        // TODO: impl
        return null;
    }

    @Override
    public void notifyListener(final WatcherEvent watcherEvent) {
        // TODO: impl
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
