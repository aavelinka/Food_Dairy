package com.uni.project.model.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Base64;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
    private static final String COUNT_SEPARATOR = "|";
    private static final String VALUE_SEPARATOR = ",";
    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final Base64.Decoder DECODER = Base64.getDecoder();

    @Override
    public String convertToDatabaseColumn(final List<String> attribute) {
        final List<String> values = attribute == null ? Collections.emptyList() : attribute;
        final StringBuilder result = new StringBuilder()
                .append(values.size())
                .append(COUNT_SEPARATOR);

        for (int index = 0; index < values.size(); index++) {
            if (index > 0) {
                result.append(VALUE_SEPARATOR);
            }

            final String value = values.get(index) == null ? "" : values.get(index);
            result.append(ENCODER.encodeToString(value.getBytes(StandardCharsets.UTF_8)));
        }

        return result.toString();
    }

    @Override
    public List<String> convertToEntityAttribute(final String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Collections.emptyList();
        }

        try {
            final int separatorIndex = dbData.indexOf(COUNT_SEPARATOR);
            if (separatorIndex < 0) {
                throw new IllegalArgumentException("Incorrect notes format");
            }

            final int expectedSize = Integer.parseInt(dbData.substring(0, separatorIndex));
            if (expectedSize == 0) {
                return Collections.emptyList();
            }

            final String encodedValues = dbData.substring(separatorIndex + 1);
            final String[] parts = encodedValues.split(VALUE_SEPARATOR, -1);

            if (parts.length != expectedSize) {
                throw new IllegalArgumentException("Incorrect notes format");
            }

            final List<String> values = new ArrayList<>(expectedSize);
            for (final String part : parts) {
                values.add(new String(DECODER.decode(part), StandardCharsets.UTF_8));
            }

            return values;
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("Could not convert database value to notes list", exception);
        }
    }
}
