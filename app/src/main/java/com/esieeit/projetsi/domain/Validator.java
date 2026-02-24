package com.esiee.project.domain.validation;

import java.util.regex.Pattern;
import com.esiee.project.domain.exception.ValidationException;

public final class Validators {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private Validators() {
        // classe utilitaire, non instanciable
        throw new IllegalStateException("Utility class");
    }

    public static void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName + " ne doit pas être null");
        }
    }

    public static String requireNonBlank(String value, String fieldName, int min, int max) {
        if (value == null) {
            throw new ValidationException(fieldName + " ne doit pas être null");
        }

        String v = value.trim();

        if (v.isEmpty()) {
            throw new ValidationException(fieldName + " ne doit pas être vide");
        }

        return requireSize(v, fieldName, min, max);
    }

    public static String requireSize(String value, String fieldName, int min, int max) {
        int len = value.length();

        if (len < min || len > max) {
            throw new ValidationException(
                    fieldName + " doit être entre " + min + " et " + max + " caractères"
            );
        }

        return value;
    }

    public static String requireEmail(String value, String fieldName) {
        String v = requireNonBlank(value, fieldName, 5, 254);

        if (!EMAIL_PATTERN.matcher(v).matches()) {
            throw new ValidationException(fieldName + " doit être un email valide");
        }

        return v;
    }

    public static Long requirePositive(Long value, String fieldName) {
        requireNonNull(value, fieldName);

        if (value <= 0) {
            throw new ValidationException(fieldName + " doit être > 0");
        }

        return value;
    }
}
