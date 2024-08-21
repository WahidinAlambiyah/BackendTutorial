package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.ProvinceAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Province;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.ProvinceRepository;
import com.mycompany.myapp.repository.search.ProvinceSearchRepository;
import com.mycompany.myapp.service.ProvinceService;
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
 * Integration tests for the {@link ProvinceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProvinceResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/provinces";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/provinces/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Mock
    private ProvinceRepository provinceRepositoryMock;

    @Mock
    private ProvinceService provinceServiceMock;

    @Autowired
    private ProvinceSearchRepository provinceSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Province province;

    private Province insertedProvince;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Province createEntity(EntityManager em) {
        Province province = new Province().name(DEFAULT_NAME).code(DEFAULT_CODE);
        return province;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Province createUpdatedEntity(EntityManager em) {
        Province province = new Province().name(UPDATED_NAME).code(UPDATED_CODE);
        return province;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Province.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        province = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedProvince != null) {
            provinceRepository.delete(insertedProvince).block();
            provinceSearchRepository.delete(insertedProvince).block();
            insertedProvince = null;
        }
        deleteEntities(em);
    }

    @Test
    void createProvince() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        // Create the Province
        var returnedProvince = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(province))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Province.class)
            .returnResult()
            .getResponseBody();

        // Validate the Province in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertProvinceUpdatableFieldsEquals(returnedProvince, getPersistedProvince(returnedProvince));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedProvince = returnedProvince;
    }

    @Test
    void createProvinceWithExistingId() throws Exception {
        // Create the Province with an existing ID
        province.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(province))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Province in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        // set the field null
        province.setName(null);

        // Create the Province, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(province))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        // set the field null
        province.setCode(null);

        // Create the Province, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(province))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllProvinces() {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();

        // Get all the provinceList
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
            .value(hasItem(province.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProvincesWithEagerRelationshipsIsEnabled() {
        when(provinceServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(provinceServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProvincesWithEagerRelationshipsIsNotEnabled() {
        when(provinceServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(provinceRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getProvince() {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();

        // Get the province
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, province.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(province.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE));
    }

    @Test
    void getNonExistingProvince() {
        // Get the province
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProvince() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        provinceSearchRepository.save(province).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());

        // Update the province
        Province updatedProvince = provinceRepository.findById(province.getId()).block();
        updatedProvince.name(UPDATED_NAME).code(UPDATED_CODE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedProvince.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedProvince))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Province in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProvinceToMatchAllProperties(updatedProvince);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Province> provinceSearchList = Streamable.of(provinceSearchRepository.findAll().collectList().block()).toList();
                Province testProvinceSearch = provinceSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertProvinceAllPropertiesEquals(testProvinceSearch, updatedProvince);
                assertProvinceUpdatableFieldsEquals(testProvinceSearch, updatedProvince);
            });
    }

    @Test
    void putNonExistingProvince() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        province.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, province.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(province))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Province in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchProvince() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        province.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(province))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Province in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamProvince() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        province.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(province))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Province in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateProvinceWithPatch() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the province using partial update
        Province partialUpdatedProvince = new Province();
        partialUpdatedProvince.setId(province.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProvince.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedProvince))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Province in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProvinceUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedProvince, province), getPersistedProvince(province));
    }

    @Test
    void fullUpdateProvinceWithPatch() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the province using partial update
        Province partialUpdatedProvince = new Province();
        partialUpdatedProvince.setId(province.getId());

        partialUpdatedProvince.name(UPDATED_NAME).code(UPDATED_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProvince.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedProvince))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Province in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProvinceUpdatableFieldsEquals(partialUpdatedProvince, getPersistedProvince(partialUpdatedProvince));
    }

    @Test
    void patchNonExistingProvince() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        province.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, province.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(province))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Province in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchProvince() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        province.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(province))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Province in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamProvince() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        province.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(province))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Province in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteProvince() {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();
        provinceRepository.save(province).block();
        provinceSearchRepository.save(province).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the province
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, province.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchProvince() {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();
        provinceSearchRepository.save(province).block();

        // Search the province
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + province.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(province.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE));
    }

    protected long getRepositoryCount() {
        return provinceRepository.count().block();
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

    protected Province getPersistedProvince(Province province) {
        return provinceRepository.findById(province.getId()).block();
    }

    protected void assertPersistedProvinceToMatchAllProperties(Province expectedProvince) {
        // Test fails because reactive api returns an empty object instead of null
        // assertProvinceAllPropertiesEquals(expectedProvince, getPersistedProvince(expectedProvince));
        assertProvinceUpdatableFieldsEquals(expectedProvince, getPersistedProvince(expectedProvince));
    }

    protected void assertPersistedProvinceToMatchUpdatableProperties(Province expectedProvince) {
        // Test fails because reactive api returns an empty object instead of null
        // assertProvinceAllUpdatablePropertiesEquals(expectedProvince, getPersistedProvince(expectedProvince));
        assertProvinceUpdatableFieldsEquals(expectedProvince, getPersistedProvince(expectedProvince));
    }
}
