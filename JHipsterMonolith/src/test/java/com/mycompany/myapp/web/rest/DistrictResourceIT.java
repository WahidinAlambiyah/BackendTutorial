package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.DistrictAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.District;
import com.mycompany.myapp.repository.DistrictRepository;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.search.DistrictSearchRepository;
import com.mycompany.myapp.service.DistrictService;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

/**
 * Integration tests for the {@link DistrictResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DistrictResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/districts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/districts/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DistrictRepository districtRepository;

    @Mock
    private DistrictRepository districtRepositoryMock;

    @Mock
    private DistrictService districtServiceMock;

    @Autowired
    private DistrictSearchRepository districtSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private District district;

    private District insertedDistrict;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static District createEntity(EntityManager em) {
        District district = new District().name(DEFAULT_NAME).code(DEFAULT_CODE);
        return district;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static District createUpdatedEntity(EntityManager em) {
        District district = new District().name(UPDATED_NAME).code(UPDATED_CODE);
        return district;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(District.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        district = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedDistrict != null) {
            districtRepository.delete(insertedDistrict).block();
            districtSearchRepository.delete(insertedDistrict).block();
            insertedDistrict = null;
        }
        deleteEntities(em);
    }

    @Test
    void createDistrict() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        // Create the District
        var returnedDistrict = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(district))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(District.class)
            .returnResult()
            .getResponseBody();

        // Validate the District in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertDistrictUpdatableFieldsEquals(returnedDistrict, getPersistedDistrict(returnedDistrict));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedDistrict = returnedDistrict;
    }

    @Test
    void createDistrictWithExistingId() throws Exception {
        // Create the District with an existing ID
        district.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(district))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the District in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        // set the field null
        district.setName(null);

        // Create the District, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(district))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        // set the field null
        district.setCode(null);

        // Create the District, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(district))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllDistricts() {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();

        // Get all the districtList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(district.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDistrictsWithEagerRelationshipsIsEnabled() {
        when(districtServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(districtServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDistrictsWithEagerRelationshipsIsNotEnabled() {
        when(districtServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(districtRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getDistrict() {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();

        // Get the district
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, district.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(district.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE));
    }

    @Test
    void getNonExistingDistrict() {
        // Get the district
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingDistrict() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        districtSearchRepository.save(district).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());

        // Update the district
        District updatedDistrict = districtRepository.findById(district.getId()).block();
        updatedDistrict.name(UPDATED_NAME).code(UPDATED_CODE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedDistrict.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedDistrict))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the District in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDistrictToMatchAllProperties(updatedDistrict);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<District> districtSearchList = Streamable.of(districtSearchRepository.findAll().collectList().block()).toList();
                District testDistrictSearch = districtSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertDistrictAllPropertiesEquals(testDistrictSearch, updatedDistrict);
                assertDistrictUpdatableFieldsEquals(testDistrictSearch, updatedDistrict);
            });
    }

    @Test
    void putNonExistingDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        district.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, district.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(district))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the District in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        district.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(district))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the District in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        district.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(district))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the District in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateDistrictWithPatch() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the district using partial update
        District partialUpdatedDistrict = new District();
        partialUpdatedDistrict.setId(district.getId());

        partialUpdatedDistrict.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDistrict.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDistrict))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the District in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDistrictUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedDistrict, district), getPersistedDistrict(district));
    }

    @Test
    void fullUpdateDistrictWithPatch() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the district using partial update
        District partialUpdatedDistrict = new District();
        partialUpdatedDistrict.setId(district.getId());

        partialUpdatedDistrict.name(UPDATED_NAME).code(UPDATED_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDistrict.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDistrict))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the District in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDistrictUpdatableFieldsEquals(partialUpdatedDistrict, getPersistedDistrict(partialUpdatedDistrict));
    }

    @Test
    void patchNonExistingDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        district.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, district.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(district))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the District in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        district.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(district))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the District in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        district.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(district))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the District in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteDistrict() {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();
        districtRepository.save(district).block();
        districtSearchRepository.save(district).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the district
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, district.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchDistrict() {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();
        districtSearchRepository.save(district).block();

        // Search the district
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + district.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(district.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE));
    }

    protected long getRepositoryCount() {
        return districtRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected District getPersistedDistrict(District district) {
        return districtRepository.findById(district.getId()).block();
    }

    protected void assertPersistedDistrictToMatchAllProperties(District expectedDistrict) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDistrictAllPropertiesEquals(expectedDistrict, getPersistedDistrict(expectedDistrict));
        assertDistrictUpdatableFieldsEquals(expectedDistrict, getPersistedDistrict(expectedDistrict));
    }

    protected void assertPersistedDistrictToMatchUpdatableProperties(District expectedDistrict) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDistrictAllUpdatablePropertiesEquals(expectedDistrict, getPersistedDistrict(expectedDistrict));
        assertDistrictUpdatableFieldsEquals(expectedDistrict, getPersistedDistrict(expectedDistrict));
    }
}
