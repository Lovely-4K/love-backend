package com.lovely4k.backend.common;

public final class ExceptionMessage {

    private static final String NOT_FOUND_ENTITY_MESSAGE = "존재하지 않는 %s 아이디: %d";

    public static String notFoundEntityMessage(String domain, long id) {
        return String.format(NOT_FOUND_ENTITY_MESSAGE, domain, id);
    }
}