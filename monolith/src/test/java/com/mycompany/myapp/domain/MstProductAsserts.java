package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.AssertUtils.bigDecimalCompareTo;
import static org.assertj.core.api.Assertions.assertThat;

public class MstProductAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMstProductAllPropertiesEquals(MstProduct expected, MstProduct actual) {
        assertMstProductAutoGeneratedPropertiesEquals(expected, actual);
        assertMstProductAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMstProductAllUpdatablePropertiesEquals(MstProduct expected, MstProduct actual) {
        assertMstProductUpdatableFieldsEquals(expected, actual);
        assertMstProductUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMstProductAutoGeneratedPropertiesEquals(MstProduct expected, MstProduct actual) {
        assertThat(expected)
            .as("Verify MstProduct auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMstProductUpdatableFieldsEquals(MstProduct expected, MstProduct actual) {
        assertThat(expected)
            .as("Verify MstProduct relevant properties")
            .satisfies(e -> assertThat(e.getName()).as("check name").isEqualTo(actual.getName()))
            .satisfies(e -> assertThat(e.getDescription()).as("check description").isEqualTo(actual.getDescription()))
            .satisfies(e -> assertThat(e.getPrice()).as("check price").usingComparator(bigDecimalCompareTo).isEqualTo(actual.getPrice()))
            .satisfies(e -> assertThat(e.getQuantity()).as("check quantity").isEqualTo(actual.getQuantity()))
            .satisfies(e -> assertThat(e.getBarcode()).as("check barcode").isEqualTo(actual.getBarcode()))
            .satisfies(e -> assertThat(e.getUnitSize()).as("check unitSize").isEqualTo(actual.getUnitSize()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMstProductUpdatableRelationshipsEquals(MstProduct expected, MstProduct actual) {
        assertThat(expected)
            .as("Verify MstProduct relationships")
            .satisfies(e -> assertThat(e.getCategory()).as("check category").isEqualTo(actual.getCategory()))
            .satisfies(e -> assertThat(e.getBrand()).as("check brand").isEqualTo(actual.getBrand()))
            .satisfies(e -> assertThat(e.getMstSupplier()).as("check mstSupplier").isEqualTo(actual.getMstSupplier()));
    }
}
