package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class MstDepartmentAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMstDepartmentAllPropertiesEquals(MstDepartment expected, MstDepartment actual) {
        assertMstDepartmentAutoGeneratedPropertiesEquals(expected, actual);
        assertMstDepartmentAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMstDepartmentAllUpdatablePropertiesEquals(MstDepartment expected, MstDepartment actual) {
        assertMstDepartmentUpdatableFieldsEquals(expected, actual);
        assertMstDepartmentUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMstDepartmentAutoGeneratedPropertiesEquals(MstDepartment expected, MstDepartment actual) {
        assertThat(expected)
            .as("Verify MstDepartment auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMstDepartmentUpdatableFieldsEquals(MstDepartment expected, MstDepartment actual) {
        assertThat(expected)
            .as("Verify MstDepartment relevant properties")
            .satisfies(e -> assertThat(e.getDepartmentName()).as("check departmentName").isEqualTo(actual.getDepartmentName()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMstDepartmentUpdatableRelationshipsEquals(MstDepartment expected, MstDepartment actual) {
        assertThat(expected)
            .as("Verify MstDepartment relationships")
            .satisfies(e -> assertThat(e.getLocation()).as("check location").isEqualTo(actual.getLocation()));
    }
}
