package com.xshxy.seeklightbackend.constant;

public final class SecurityRoles {

    public static final String ROLE_PREFIX = "ROLE_";
    public static final String ADMIN = ROLE_PREFIX + "ADMIN";
    public static final String USER = ROLE_PREFIX + "USER";

    private SecurityRoles() {
    }

    public static String normalize(String role) {
        if (role == null || role.isBlank()) {
            return USER;
        }

        String normalizedRole = role.trim().toUpperCase();
        if (normalizedRole.startsWith(ROLE_PREFIX)) {
            return normalizedRole;
        }
        return ROLE_PREFIX + normalizedRole;
    }
}
