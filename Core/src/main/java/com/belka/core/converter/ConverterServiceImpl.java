package com.belka.core.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class ConverterServiceImpl implements ConverterService {

    private final Map<Class<?>, Map<Class<?>, BelConverter<?, ?>>> converters = new HashMap<>();

    @Autowired(required = false)
    private void setConverters(Collection<BelConverter<?, ?>> converters) {
        if (converters == null) {
            return;
        }
        for (BelConverter<?, ?> converter : converters) {
            this.converters.computeIfAbsent(converter.getInputType(), v -> new HashMap<>())
                    .put(converter.getOutputType(), converter);
        }
    }

    @Override
    public <T> T ConvertTo(Class<T> to, Object value) {
        if (value == null) {
            return null;
        }
        if (to == null) {
            throw new RuntimeException("didn't set output type");
        }
        if (to.isInstance(value)) {
            return (T) value;
        }
        Map<Class<?>, BelConverter<?, ?>> inputTypeConverters = converters.get(value.getClass());
        if (inputTypeConverters == null) {
            throw new RuntimeException(String.format("there are not converters from %s to %s", value.getClass(), to));
        }
        BelConverter<?, ?> converter = inputTypeConverters.get(to);
        if (converter == null) {
            throw new RuntimeException(String.format("there are not converters from %s to %s", value.getClass(), to));
        }
        try {
            return (T) converter.convertObject(value);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }
}
