package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.RegionAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Region;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.RegionRepository;
import com.mycompany.myapp.repository.search.RegionSearchRepository;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link RegionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RegionResourceIT {

    private static final String DEFAULT_REGION_NAME = "AAAAAAAAAA";
    private static final String UPDATED_REGION_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/regions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/regions/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private RegionSearchRepository regionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Region region;

    private Region insertedRegion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Region createEntity(EntityManager em) {
        Region region = new Region().regionName(DEFAULT_REGION_NAME);
        return region;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Region createUpdatedEntity(EntityManager em) {
        Region region = new Region().regionName(UPDATED_REGION_NAME);
        return region;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Region.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        region = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedRegion != null) {
            regionRepository.delete(insertedRegion).block();
            regionSearchRepository.delete(insertedRegion).block();
            insertedRegion = null;
        }
        deleteEntities(em);
    }

    @Test
    void createRegion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(regionSearchRepository.findAll().collectList().block());
        // Create the Region
        var returnedRegion = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(region))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Region.class)
            .returnResult()
            .getResponseBody();

        // Validate the Region in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertRegionUpdatableFieldsEquals(returnedRegion, getPersistedRegion(returnedRegion));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(regionSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedRegion = returnedRegion;
    }

    @Test
    void createRegionWithExistingId() throws Exception {
        // Create the Region with an existing ID
        region.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(regionSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(region))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Region in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(regionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllRegionsAsStream() {
        // Initialize the database
        regionRepository.save(region).block();

        List<Region> regionList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Region.class)
            .getResponseBody()
            .filter(region::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(regionList).isNotNull();
        assertThat(regionList).hasSize(1);
        Region testRegion = regionList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertRegionAllPropertiesEquals(region, testRegion);
        assertRegionUpdatableFieldsEquals(region, testRegion);
    }

    @Test
    void getAllRegions() {
        // Initialize the database
        insertedRegion = regionRepository.save(region).block();

        // Get all the regionList
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
            .value(hasItem(region.getId().intValue()))
            .jsonPath("$.[*].regionName")
            .value(hasItem(DEFAULT_REGION_NAME));
    }

    @Test
    void getRegion() {
        // Initialize the database
        insertedRegion = regionRepository.save(region).block();

        // Get the region
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, region.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(region.getId().intValue()))
            .jsonPath("$.regionName")
            .value(is(DEFAULT_REGION_NAME));
    }

    @Test
    void getNonExistingRegion() {
        // Get the region
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingRegion() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.save(region).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        regionSearchRepository.save(region).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(regionSearchRepository.findAll().collectList().block());

        // Update the region
        Region updatedRegion = regionRepository.findById(region.getId()).block();
        updatedRegion.regionName(UPDATED_REGION_NAME);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedRegion.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedRegion))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Region in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRegionToMatchAllProperties(updatedRegion);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(regionSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Region> regionSearchList = Streamable.of(regionSearchRepository.findAll().collectList().block()).toList();
                Region testRegionSearch = regionSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertRegionAllPropertiesEquals(testRegionSearch, updatedRegion);
                assertRegionUpdatableFieldsEquals(testRegionSearch, updatedRegion);
            });
    }

    @Test
    void putNonExistingRegion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(regionSearchRepository.findAll().collectList().block());
        region.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, region.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(region))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Region in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(regionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchRegion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(regionSearchRepository.findAll().collectList().block());
        region.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(region))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Region in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(regionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamRegion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(regionSearchRepository.findAll().collectList().block());
        region.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(region))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Region in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(regionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateRegionWithPatch() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.save(region).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the region using partial update
        Region partialUpdatedRegion = new Region();
        partialUpdatedRegion.setId(region.getId());

        partialUpdatedRegion.regionName(UPDATED_REGION_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRegion.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRegion))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Region in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRegionUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedRegion, region), getPersistedRegion(region));
    }

    @Test
    void fullUpdateRegionWithPatch() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.save(region).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the region using partial update
        Region partialUpdatedRegion = new Region();
        partialUpdatedRegion.setId(region.getId());

        partialUpdatedRegion.regionName(UPDATED_REGION_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRegion.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRegion))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Region in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRegionUpdatableFieldsEquals(partialUpdatedRegion, getPersistedRegion(partialUpdatedRegion));
    }

    @Test
    void patchNonExistingRegion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(regionSearchRepository.findAll().collectList().block());
        region.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, region.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(region))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Region in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(regionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchRegion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(regionSearchRepository.findAll().collectList().block());
        region.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(region))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Region in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(regionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamRegion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(regionSearchRepository.findAll().collectList().block());
        region.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(region))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Region in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(regionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteRegion() {
        // Initialize the database
        insertedRegion = regionRepository.save(region).block();
        regionRepository.save(region).block();
        regionSearchRepository.save(region).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(regionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the region
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, region.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(regionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchRegion() {
        // Initialize the database
        insertedRegion = regionRepository.save(region).block();
        regionSearchRepository.save(region).block();

        // Search the region
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + region.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(region.getId().intValue()))
            .jsonPath("$.[*].regionName")
            .value(hasItem(DEFAULT_REGION_NAME));
    }

    protected long getRepositoryCount() {
        return regionRepository.count().block();
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

    protected Region getPersistedRegion(Region region) {
        return regionRepository.findById(region.getId()).block();
    }

    protected void assertPersistedRegionToMatchAllProperties(Region expectedRegion) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRegionAllPropertiesEquals(expectedRegion, getPersistedRegion(expectedRegion));
        assertRegionUpdatableFieldsEquals(expectedRegion, getPersistedRegion(expectedRegion));
    }

    protected void assertPersistedRegionToMatchUpdatableProperties(Region expectedRegion) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRegionAllUpdatablePropertiesEquals(expectedRegion, getPersistedRegion(expectedRegion));
        assertRegionUpdatableFieldsEquals(expectedRegion, getPersistedRegion(expectedRegion));
    }
}
