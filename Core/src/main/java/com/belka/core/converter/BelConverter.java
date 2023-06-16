package com.belka.core.converter;

public interface BelConverter<I, O> {
    default O convertObject(Object input) {
        if (input == null) {
            return null;
        }
        if (getInputType().isInstance(input)) {
            return convert((I) input);
        } else {
            throw new RuntimeException("converter fail.Expected input value - " + getInputType().getTypeName() +
                    ", but passed - " + input.getClass().getTypeName());
        }
    }

    O convert(I value);

    Class<O> getOutputType();

    Class<I> getInputType();
}
