package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class CityAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCityAllPropertiesEquals(City expected, City actual) {
        assertCityAutoGeneratedPropertiesEquals(expected, actual);
        assertCityAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCityAllUpdatablePropertiesEquals(City expected, City actual) {
        assertCityUpdatableFieldsEquals(expected, actual);
        assertCityUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCityAutoGeneratedPropertiesEquals(City expected, City actual) {
        assertThat(expected)
            .as("Verify City auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCityUpdatableFieldsEquals(City expected, City actual) {
        assertThat(expected)
            .as("Verify City relevant properties")
            .satisfies(e -> assertThat(e.getName()).as("check name").isEqualTo(actual.getName()))
            .satisfies(e -> assertThat(e.getUnm49Code()).as("check unm49Code").isEqualTo(actual.getUnm49Code()))
            .satisfies(e -> assertThat(e.getIsoAlpha2Code()).as("check isoAlpha2Code").isEqualTo(actual.getIsoAlpha2Code()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCityUpdatableRelationshipsEquals(City expected, City actual) {
        assertThat(expected)
            .as("Verify City relationships")
            .satisfies(e -> assertThat(e.getProvince()).as("check province").isEqualTo(actual.getProvince()));
    }
}
