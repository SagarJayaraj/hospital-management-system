package com.sagar.hms.patient;

import org.springframework.data.jpa.domain.Specification;

public final class PatientSpecifications {
    private PatientSpecifications() {
    }

    public static Specification<Patient> withFilters(String name, String phoneNumber, String email) {
        return Specification.where(likeIfPresent("name", name))
                .and(likeIfPresent("phoneNumber", phoneNumber))
                .and(likeIfPresent("email", email));
    }

    private static Specification<Patient> likeIfPresent(String field, String value) {
        return (root, query, cb) -> {
            if (value == null || value.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get(field)), "%" + escapeForLike(value.toLowerCase()) + "%", '\\');
        };
    }

    private static String escapeForLike(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
