package com.belka.core.converter;

/**
 * Service that provides a converter
 */
public interface ConverterService {
    <T> T ConvertTo(Class<T> to, Object value);
}
