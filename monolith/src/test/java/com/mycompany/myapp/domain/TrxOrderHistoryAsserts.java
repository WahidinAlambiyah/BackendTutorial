package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class TrxOrderHistoryAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTrxOrderHistoryAllPropertiesEquals(TrxOrderHistory expected, TrxOrderHistory actual) {
        assertTrxOrderHistoryAutoGeneratedPropertiesEquals(expected, actual);
        assertTrxOrderHistoryAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTrxOrderHistoryAllUpdatablePropertiesEquals(TrxOrderHistory expected, TrxOrderHistory actual) {
        assertTrxOrderHistoryUpdatableFieldsEquals(expected, actual);
        assertTrxOrderHistoryUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTrxOrderHistoryAutoGeneratedPropertiesEquals(TrxOrderHistory expected, TrxOrderHistory actual) {
        assertThat(expected)
            .as("Verify TrxOrderHistory auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTrxOrderHistoryUpdatableFieldsEquals(TrxOrderHistory expected, TrxOrderHistory actual) {
        assertThat(expected)
            .as("Verify TrxOrderHistory relevant properties")
            .satisfies(e -> assertThat(e.getPreviousStatus()).as("check previousStatus").isEqualTo(actual.getPreviousStatus()))
            .satisfies(e -> assertThat(e.getNewStatus()).as("check newStatus").isEqualTo(actual.getNewStatus()))
            .satisfies(e -> assertThat(e.getChangeDate()).as("check changeDate").isEqualTo(actual.getChangeDate()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTrxOrderHistoryUpdatableRelationshipsEquals(TrxOrderHistory expected, TrxOrderHistory actual) {}
}
