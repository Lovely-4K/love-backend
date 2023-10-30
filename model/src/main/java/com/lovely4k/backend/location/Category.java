package com.lovely4k.backend.location;

public enum Category {
    CAFE, FOOD, ACCOMODATION, CULTURE, ETC;

    public static void validateRequest(String category) {
        try {
            Category.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid input : " + category);
        }
    }
}
