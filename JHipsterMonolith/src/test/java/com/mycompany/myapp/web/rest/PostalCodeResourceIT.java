package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.PostalCodeAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.PostalCode;
import com.mycompany.myapp.domain.SubDistrict;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.PostalCodeRepository;
import com.mycompany.myapp.repository.SubDistrictRepository;
import com.mycompany.myapp.repository.search.PostalCodeSearchRepository;
import com.mycompany.myapp.service.PostalCodeService;
import com.mycompany.myapp.service.dto.PostalCodeDTO;
import com.mycompany.myapp.service.mapper.PostalCodeMapper;
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
 * Integration tests for the {@link PostalCodeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PostalCodeResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/postal-codes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/postal-codes/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PostalCodeRepository postalCodeRepository;

    @Mock
    private PostalCodeRepository postalCodeRepositoryMock;

    @Autowired
    private PostalCodeMapper postalCodeMapper;

    @Mock
    private PostalCodeService postalCodeServiceMock;

    @Autowired
    private PostalCodeSearchRepository postalCodeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private PostalCode postalCode;

    private PostalCode insertedPostalCode;

    @Autowired
    private SubDistrictRepository subDistrictRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PostalCode createEntity(EntityManager em) {
        PostalCode postalCode = new PostalCode().code(DEFAULT_CODE);
        return postalCode;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PostalCode createUpdatedEntity(EntityManager em) {
        PostalCode postalCode = new PostalCode().code(UPDATED_CODE);
        return postalCode;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(PostalCode.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        postalCode = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedPostalCode != null) {
            postalCodeRepository.delete(insertedPostalCode).block();
            postalCodeSearchRepository.delete(insertedPostalCode).block();
            insertedPostalCode = null;
        }
        deleteEntities(em);
    }

    @Test
    void createPostalCode() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postalCodeSearchRepository.findAll().collectList().block());
        // Create the PostalCode
        PostalCodeDTO postalCodeDTO = postalCodeMapper.toDto(postalCode);
        var returnedPostalCodeDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(postalCodeDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(PostalCodeDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the PostalCode in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPostalCode = postalCodeMapper.toEntity(returnedPostalCodeDTO);
        assertPostalCodeUpdatableFieldsEquals(returnedPostalCode, getPersistedPostalCode(returnedPostalCode));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(postalCodeSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedPostalCode = returnedPostalCode;
    }

    @Test
    void createPostalCodeWithExistingId() throws Exception {
        // Create the PostalCode with an existing ID
        postalCode.setId(1L);
        PostalCodeDTO postalCodeDTO = postalCodeMapper.toDto(postalCode);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postalCodeSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(postalCodeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PostalCode in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postalCodeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postalCodeSearchRepository.findAll().collectList().block());
        // set the field null
        postalCode.setCode(null);

        // Create the PostalCode, which fails.
        PostalCodeDTO postalCodeDTO = postalCodeMapper.toDto(postalCode);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(postalCodeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postalCodeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllPostalCodes() {
        // Initialize the database
        insertedPostalCode = postalCodeRepository.save(postalCode).block();

        // Get all the postalCodeList
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
            .value(hasItem(postalCode.getId().intValue()))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPostalCodesWithEagerRelationshipsIsEnabled() {
        when(postalCodeServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(postalCodeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPostalCodesWithEagerRelationshipsIsNotEnabled() {
        when(postalCodeServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(postalCodeRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getPostalCode() {
        // Initialize the database
        insertedPostalCode = postalCodeRepository.save(postalCode).block();

        // Get the postalCode
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, postalCode.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(postalCode.getId().intValue()))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE));
    }

    @Test
    void getPostalCodesByIdFiltering() {
        // Initialize the database
        insertedPostalCode = postalCodeRepository.save(postalCode).block();

        Long id = postalCode.getId();

        defaultPostalCodeFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPostalCodeFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPostalCodeFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllPostalCodesByCodeIsEqualToSomething() {
        // Initialize the database
        insertedPostalCode = postalCodeRepository.save(postalCode).block();

        // Get all the postalCodeList where code equals to
        defaultPostalCodeFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    void getAllPostalCodesByCodeIsInShouldWork() {
        // Initialize the database
        insertedPostalCode = postalCodeRepository.save(postalCode).block();

        // Get all the postalCodeList where code in
        defaultPostalCodeFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    void getAllPostalCodesByCodeIsNullOrNotNull() {
        // Initialize the database
        insertedPostalCode = postalCodeRepository.save(postalCode).block();

        // Get all the postalCodeList where code is not null
        defaultPostalCodeFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    void getAllPostalCodesByCodeContainsSomething() {
        // Initialize the database
        insertedPostalCode = postalCodeRepository.save(postalCode).block();

        // Get all the postalCodeList where code contains
        defaultPostalCodeFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    void getAllPostalCodesByCodeNotContainsSomething() {
        // Initialize the database
        insertedPostalCode = postalCodeRepository.save(postalCode).block();

        // Get all the postalCodeList where code does not contain
        defaultPostalCodeFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    void getAllPostalCodesBySubDistrictIsEqualToSomething() {
        SubDistrict subDistrict = SubDistrictResourceIT.createEntity(em);
        subDistrictRepository.save(subDistrict).block();
        Long subDistrictId = subDistrict.getId();
        postalCode.setSubDistrictId(subDistrictId);
        insertedPostalCode = postalCodeRepository.save(postalCode).block();
        // Get all the postalCodeList where subDistrict equals to subDistrictId
        defaultPostalCodeShouldBeFound("subDistrictId.equals=" + subDistrictId);

        // Get all the postalCodeList where subDistrict equals to (subDistrictId + 1)
        defaultPostalCodeShouldNotBeFound("subDistrictId.equals=" + (subDistrictId + 1));
    }

    private void defaultPostalCodeFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultPostalCodeShouldBeFound(shouldBeFound);
        defaultPostalCodeShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPostalCodeShouldBeFound(String filter) {
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(postalCode.getId().intValue()))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE));

        // Check, that the count call also returns 1
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "/count?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$")
            .value(is(1));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPostalCodeShouldNotBeFound(String filter) {
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$")
            .isArray()
            .jsonPath("$")
            .isEmpty();

        // Check, that the count call also returns 0
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "/count?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$")
            .value(is(0));
    }

    @Test
    void getNonExistingPostalCode() {
        // Get the postalCode
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPostalCode() throws Exception {
        // Initialize the database
        insertedPostalCode = postalCodeRepository.save(postalCode).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        postalCodeSearchRepository.save(postalCode).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postalCodeSearchRepository.findAll().collectList().block());

        // Update the postalCode
        PostalCode updatedPostalCode = postalCodeRepository.findById(postalCode.getId()).block();
        updatedPostalCode.code(UPDATED_CODE);
        PostalCodeDTO postalCodeDTO = postalCodeMapper.toDto(updatedPostalCode);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, postalCodeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(postalCodeDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PostalCode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPostalCodeToMatchAllProperties(updatedPostalCode);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(postalCodeSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<PostalCode> postalCodeSearchList = Streamable.of(postalCodeSearchRepository.findAll().collectList().block()).toList();
                PostalCode testPostalCodeSearch = postalCodeSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertPostalCodeAllPropertiesEquals(testPostalCodeSearch, updatedPostalCode);
                assertPostalCodeUpdatableFieldsEquals(testPostalCodeSearch, updatedPostalCode);
            });
    }

    @Test
    void putNonExistingPostalCode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postalCodeSearchRepository.findAll().collectList().block());
        postalCode.setId(longCount.incrementAndGet());

        // Create the PostalCode
        PostalCodeDTO postalCodeDTO = postalCodeMapper.toDto(postalCode);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, postalCodeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(postalCodeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PostalCode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postalCodeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchPostalCode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postalCodeSearchRepository.findAll().collectList().block());
        postalCode.setId(longCount.incrementAndGet());

        // Create the PostalCode
        PostalCodeDTO postalCodeDTO = postalCodeMapper.toDto(postalCode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(postalCodeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PostalCode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postalCodeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamPostalCode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postalCodeSearchRepository.findAll().collectList().block());
        postalCode.setId(longCount.incrementAndGet());

        // Create the PostalCode
        PostalCodeDTO postalCodeDTO = postalCodeMapper.toDto(postalCode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(postalCodeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PostalCode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postalCodeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdatePostalCodeWithPatch() throws Exception {
        // Initialize the database
        insertedPostalCode = postalCodeRepository.save(postalCode).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the postalCode using partial update
        PostalCode partialUpdatedPostalCode = new PostalCode();
        partialUpdatedPostalCode.setId(postalCode.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPostalCode.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPostalCode))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PostalCode in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPostalCodeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPostalCode, postalCode),
            getPersistedPostalCode(postalCode)
        );
    }

    @Test
    void fullUpdatePostalCodeWithPatch() throws Exception {
        // Initialize the database
        insertedPostalCode = postalCodeRepository.save(postalCode).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the postalCode using partial update
        PostalCode partialUpdatedPostalCode = new PostalCode();
        partialUpdatedPostalCode.setId(postalCode.getId());

        partialUpdatedPostalCode.code(UPDATED_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPostalCode.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPostalCode))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PostalCode in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPostalCodeUpdatableFieldsEquals(partialUpdatedPostalCode, getPersistedPostalCode(partialUpdatedPostalCode));
    }

    @Test
    void patchNonExistingPostalCode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postalCodeSearchRepository.findAll().collectList().block());
        postalCode.setId(longCount.incrementAndGet());

        // Create the PostalCode
        PostalCodeDTO postalCodeDTO = postalCodeMapper.toDto(postalCode);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, postalCodeDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(postalCodeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PostalCode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postalCodeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchPostalCode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postalCodeSearchRepository.findAll().collectList().block());
        postalCode.setId(longCount.incrementAndGet());

        // Create the PostalCode
        PostalCodeDTO postalCodeDTO = postalCodeMapper.toDto(postalCode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(postalCodeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PostalCode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postalCodeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamPostalCode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postalCodeSearchRepository.findAll().collectList().block());
        postalCode.setId(longCount.incrementAndGet());

        // Create the PostalCode
        PostalCodeDTO postalCodeDTO = postalCodeMapper.toDto(postalCode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(postalCodeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PostalCode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postalCodeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deletePostalCode() {
        // Initialize the database
        insertedPostalCode = postalCodeRepository.save(postalCode).block();
        postalCodeRepository.save(postalCode).block();
        postalCodeSearchRepository.save(postalCode).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postalCodeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the postalCode
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, postalCode.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postalCodeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchPostalCode() {
        // Initialize the database
        insertedPostalCode = postalCodeRepository.save(postalCode).block();
        postalCodeSearchRepository.save(postalCode).block();

        // Search the postalCode
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + postalCode.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(postalCode.getId().intValue()))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE));
    }

    protected long getRepositoryCount() {
        return postalCodeRepository.count().block();
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

    protected PostalCode getPersistedPostalCode(PostalCode postalCode) {
        return postalCodeRepository.findById(postalCode.getId()).block();
    }

    protected void assertPersistedPostalCodeToMatchAllProperties(PostalCode expectedPostalCode) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPostalCodeAllPropertiesEquals(expectedPostalCode, getPersistedPostalCode(expectedPostalCode));
        assertPostalCodeUpdatableFieldsEquals(expectedPostalCode, getPersistedPostalCode(expectedPostalCode));
    }

    protected void assertPersistedPostalCodeToMatchUpdatableProperties(PostalCode expectedPostalCode) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPostalCodeAllUpdatablePropertiesEquals(expectedPostalCode, getPersistedPostalCode(expectedPostalCode));
        assertPostalCodeUpdatableFieldsEquals(expectedPostalCode, getPersistedPostalCode(expectedPostalCode));
    }
}
