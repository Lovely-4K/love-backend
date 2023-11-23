package com.lovely4k.backend.member;

public enum Sex {
    MALE, FEMALE;

    public static Sex of(String gender) {
        validateString(gender);

        if (gender.equalsIgnoreCase("male") || gender.equalsIgnoreCase("M")) {
            return Sex.MALE;
        } else {
            return Sex.FEMALE;
        }
    }

    private static void validateString(String gender) {
        if (gender == null) {
            throw new IllegalArgumentException("gender cannot be null");
        }
    }
}
