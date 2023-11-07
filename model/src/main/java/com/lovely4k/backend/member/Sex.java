package com.lovely4k.backend.member;

public enum Sex {
    MALE, FEMALE;

    public static Sex of(String gender) {
        if (gender.equalsIgnoreCase("male") || gender.equalsIgnoreCase("M")) {
            return Sex.MALE;
        } else {
            return Sex.FEMALE;
        }
    }
}
