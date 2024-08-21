package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.SubDistrictAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.SubDistrict;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.SubDistrictRepository;
import com.mycompany.myapp.repository.search.SubDistrictSearchRepository;
import com.mycompany.myapp.service.SubDistrictService;
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
 * Integration tests for the {@link SubDistrictResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SubDistrictResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/sub-districts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/sub-districts/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SubDistrictRepository subDistrictRepository;

    @Mock
    private SubDistrictRepository subDistrictRepositoryMock;

    @Mock
    private SubDistrictService subDistrictServiceMock;

    @Autowired
    private SubDistrictSearchRepository subDistrictSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private SubDistrict subDistrict;

    private SubDistrict insertedSubDistrict;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SubDistrict createEntity(EntityManager em) {
        SubDistrict subDistrict = new SubDistrict().name(DEFAULT_NAME).code(DEFAULT_CODE);
        return subDistrict;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SubDistrict createUpdatedEntity(EntityManager em) {
        SubDistrict subDistrict = new SubDistrict().name(UPDATED_NAME).code(UPDATED_CODE);
        return subDistrict;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(SubDistrict.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        subDistrict = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedSubDistrict != null) {
            subDistrictRepository.delete(insertedSubDistrict).block();
            subDistrictSearchRepository.delete(insertedSubDistrict).block();
            insertedSubDistrict = null;
        }
        deleteEntities(em);
    }

    @Test
    void createSubDistrict() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        // Create the SubDistrict
        var returnedSubDistrict = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subDistrict))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(SubDistrict.class)
            .returnResult()
            .getResponseBody();

        // Validate the SubDistrict in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertSubDistrictUpdatableFieldsEquals(returnedSubDistrict, getPersistedSubDistrict(returnedSubDistrict));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedSubDistrict = returnedSubDistrict;
    }

    @Test
    void createSubDistrictWithExistingId() throws Exception {
        // Create the SubDistrict with an existing ID
        subDistrict.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subDistrict))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SubDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        // set the field null
        subDistrict.setName(null);

        // Create the SubDistrict, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subDistrict))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        // set the field null
        subDistrict.setCode(null);

        // Create the SubDistrict, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subDistrict))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllSubDistricts() {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();

        // Get all the subDistrictList
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
            .value(hasItem(subDistrict.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSubDistrictsWithEagerRelationshipsIsEnabled() {
        when(subDistrictServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(subDistrictServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSubDistrictsWithEagerRelationshipsIsNotEnabled() {
        when(subDistrictServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(subDistrictRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getSubDistrict() {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();

        // Get the subDistrict
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, subDistrict.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(subDistrict.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE));
    }

    @Test
    void getNonExistingSubDistrict() {
        // Get the subDistrict
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingSubDistrict() throws Exception {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        subDistrictSearchRepository.save(subDistrict).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());

        // Update the subDistrict
        SubDistrict updatedSubDistrict = subDistrictRepository.findById(subDistrict.getId()).block();
        updatedSubDistrict.name(UPDATED_NAME).code(UPDATED_CODE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedSubDistrict.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedSubDistrict))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SubDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSubDistrictToMatchAllProperties(updatedSubDistrict);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<SubDistrict> subDistrictSearchList = Streamable.of(
                    subDistrictSearchRepository.findAll().collectList().block()
                ).toList();
                SubDistrict testSubDistrictSearch = subDistrictSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertSubDistrictAllPropertiesEquals(testSubDistrictSearch, updatedSubDistrict);
                assertSubDistrictUpdatableFieldsEquals(testSubDistrictSearch, updatedSubDistrict);
            });
    }

    @Test
    void putNonExistingSubDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        subDistrict.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, subDistrict.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subDistrict))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SubDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchSubDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        subDistrict.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subDistrict))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SubDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamSubDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        subDistrict.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subDistrict))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the SubDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateSubDistrictWithPatch() throws Exception {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subDistrict using partial update
        SubDistrict partialUpdatedSubDistrict = new SubDistrict();
        partialUpdatedSubDistrict.setId(subDistrict.getId());

        partialUpdatedSubDistrict.name(UPDATED_NAME).code(UPDATED_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSubDistrict.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSubDistrict))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SubDistrict in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSubDistrictUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSubDistrict, subDistrict),
            getPersistedSubDistrict(subDistrict)
        );
    }

    @Test
    void fullUpdateSubDistrictWithPatch() throws Exception {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subDistrict using partial update
        SubDistrict partialUpdatedSubDistrict = new SubDistrict();
        partialUpdatedSubDistrict.setId(subDistrict.getId());

        partialUpdatedSubDistrict.name(UPDATED_NAME).code(UPDATED_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSubDistrict.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSubDistrict))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SubDistrict in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSubDistrictUpdatableFieldsEquals(partialUpdatedSubDistrict, getPersistedSubDistrict(partialUpdatedSubDistrict));
    }

    @Test
    void patchNonExistingSubDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        subDistrict.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, subDistrict.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(subDistrict))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SubDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchSubDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        subDistrict.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(subDistrict))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SubDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamSubDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        subDistrict.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(subDistrict))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the SubDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteSubDistrict() {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();
        subDistrictRepository.save(subDistrict).block();
        subDistrictSearchRepository.save(subDistrict).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the subDistrict
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, subDistrict.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchSubDistrict() {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();
        subDistrictSearchRepository.save(subDistrict).block();

        // Search the subDistrict
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + subDistrict.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(subDistrict.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE));
    }

    protected long getRepositoryCount() {
        return subDistrictRepository.count().block();
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

    protected SubDistrict getPersistedSubDistrict(SubDistrict subDistrict) {
        return subDistrictRepository.findById(subDistrict.getId()).block();
    }

    protected void assertPersistedSubDistrictToMatchAllProperties(SubDistrict expectedSubDistrict) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSubDistrictAllPropertiesEquals(expectedSubDistrict, getPersistedSubDistrict(expectedSubDistrict));
        assertSubDistrictUpdatableFieldsEquals(expectedSubDistrict, getPersistedSubDistrict(expectedSubDistrict));
    }

    protected void assertPersistedSubDistrictToMatchUpdatableProperties(SubDistrict expectedSubDistrict) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSubDistrictAllUpdatablePropertiesEquals(expectedSubDistrict, getPersistedSubDistrict(expectedSubDistrict));
        assertSubDistrictUpdatableFieldsEquals(expectedSubDistrict, getPersistedSubDistrict(expectedSubDistrict));
    }
}
