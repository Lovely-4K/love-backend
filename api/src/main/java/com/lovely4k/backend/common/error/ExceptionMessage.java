package com.lovely4k.backend.common.error;

public final class ExceptionMessage {

    private static final String NOT_FOUND_ENTITY_MESSAGE = "존재하지 않는 %s 아이디: %d";
    private static final String NO_AUTHORITY_MESSAGE = "%s %d은 %s %d에 대한 권한이 없음";

    private ExceptionMessage() {}

    public static String notFoundEntityMessage(String domain, long id) {
        return String.format(NOT_FOUND_ENTITY_MESSAGE, domain, id);
    }

    public static String noAuthorityMessage(String accessor, long accessorId, String resource, long resourceId) {
        return String.format(NO_AUTHORITY_MESSAGE, accessor, accessorId, resource, resourceId);
    }

}