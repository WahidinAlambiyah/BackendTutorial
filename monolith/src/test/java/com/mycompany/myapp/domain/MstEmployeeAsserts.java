package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class MstEmployeeAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMstEmployeeAllPropertiesEquals(MstEmployee expected, MstEmployee actual) {
        assertMstEmployeeAutoGeneratedPropertiesEquals(expected, actual);
        assertMstEmployeeAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMstEmployeeAllUpdatablePropertiesEquals(MstEmployee expected, MstEmployee actual) {
        assertMstEmployeeUpdatableFieldsEquals(expected, actual);
        assertMstEmployeeUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMstEmployeeAutoGeneratedPropertiesEquals(MstEmployee expected, MstEmployee actual) {
        assertThat(expected)
            .as("Verify MstEmployee auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMstEmployeeUpdatableFieldsEquals(MstEmployee expected, MstEmployee actual) {
        assertThat(expected)
            .as("Verify MstEmployee relevant properties")
            .satisfies(e -> assertThat(e.getFirstName()).as("check firstName").isEqualTo(actual.getFirstName()))
            .satisfies(e -> assertThat(e.getLastName()).as("check lastName").isEqualTo(actual.getLastName()))
            .satisfies(e -> assertThat(e.getEmail()).as("check email").isEqualTo(actual.getEmail()))
            .satisfies(e -> assertThat(e.getPhoneNumber()).as("check phoneNumber").isEqualTo(actual.getPhoneNumber()))
            .satisfies(e -> assertThat(e.getHireDate()).as("check hireDate").isEqualTo(actual.getHireDate()))
            .satisfies(e -> assertThat(e.getSalary()).as("check salary").isEqualTo(actual.getSalary()))
            .satisfies(e -> assertThat(e.getCommissionPct()).as("check commissionPct").isEqualTo(actual.getCommissionPct()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMstEmployeeUpdatableRelationshipsEquals(MstEmployee expected, MstEmployee actual) {
        assertThat(expected)
            .as("Verify MstEmployee relationships")
            .satisfies(e -> assertThat(e.getManager()).as("check manager").isEqualTo(actual.getManager()))
            .satisfies(e -> assertThat(e.getDepartment()).as("check department").isEqualTo(actual.getDepartment()))
            .satisfies(e -> assertThat(e.getMstDepartment()).as("check mstDepartment").isEqualTo(actual.getMstDepartment()));
    }
}
