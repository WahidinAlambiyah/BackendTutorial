package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class TrxTestimonialAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTrxTestimonialAllPropertiesEquals(TrxTestimonial expected, TrxTestimonial actual) {
        assertTrxTestimonialAutoGeneratedPropertiesEquals(expected, actual);
        assertTrxTestimonialAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTrxTestimonialAllUpdatablePropertiesEquals(TrxTestimonial expected, TrxTestimonial actual) {
        assertTrxTestimonialUpdatableFieldsEquals(expected, actual);
        assertTrxTestimonialUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTrxTestimonialAutoGeneratedPropertiesEquals(TrxTestimonial expected, TrxTestimonial actual) {
        assertThat(expected)
            .as("Verify TrxTestimonial auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTrxTestimonialUpdatableFieldsEquals(TrxTestimonial expected, TrxTestimonial actual) {
        assertThat(expected)
            .as("Verify TrxTestimonial relevant properties")
            .satisfies(e -> assertThat(e.getName()).as("check name").isEqualTo(actual.getName()))
            .satisfies(e -> assertThat(e.getFeedback()).as("check feedback").isEqualTo(actual.getFeedback()))
            .satisfies(e -> assertThat(e.getRating()).as("check rating").isEqualTo(actual.getRating()))
            .satisfies(e -> assertThat(e.getDate()).as("check date").isEqualTo(actual.getDate()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTrxTestimonialUpdatableRelationshipsEquals(TrxTestimonial expected, TrxTestimonial actual) {}
}
