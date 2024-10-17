package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.MstRegionAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.MstRegion;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MstRegionRepository;
import com.mycompany.myapp.repository.search.MstRegionSearchRepository;
import com.mycompany.myapp.service.dto.MstRegionDTO;
import com.mycompany.myapp.service.mapper.MstRegionMapper;
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
 * Integration tests for the {@link MstRegionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MstRegionResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_UNM_49_CODE = "AAAAAAAAAA";
    private static final String UPDATED_UNM_49_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_ISO_ALPHA_2_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ISO_ALPHA_2_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/mst-regions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/mst-regions/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MstRegionRepository mstRegionRepository;

    @Autowired
    private MstRegionMapper mstRegionMapper;

    @Autowired
    private MstRegionSearchRepository mstRegionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private MstRegion mstRegion;

    private MstRegion insertedMstRegion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstRegion createEntity(EntityManager em) {
        MstRegion mstRegion = new MstRegion().name(DEFAULT_NAME).unm49Code(DEFAULT_UNM_49_CODE).isoAlpha2Code(DEFAULT_ISO_ALPHA_2_CODE);
        return mstRegion;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstRegion createUpdatedEntity(EntityManager em) {
        MstRegion mstRegion = new MstRegion().name(UPDATED_NAME).unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);
        return mstRegion;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(MstRegion.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        mstRegion = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedMstRegion != null) {
            mstRegionRepository.delete(insertedMstRegion).block();
            mstRegionSearchRepository.delete(insertedMstRegion).block();
            insertedMstRegion = null;
        }
        deleteEntities(em);
    }

    @Test
    void createMstRegion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstRegionSearchRepository.findAll().collectList().block());
        // Create the MstRegion
        MstRegionDTO mstRegionDTO = mstRegionMapper.toDto(mstRegion);
        var returnedMstRegionDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstRegionDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MstRegionDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MstRegion in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMstRegion = mstRegionMapper.toEntity(returnedMstRegionDTO);
        assertMstRegionUpdatableFieldsEquals(returnedMstRegion, getPersistedMstRegion(returnedMstRegion));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstRegionSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedMstRegion = returnedMstRegion;
    }

    @Test
    void createMstRegionWithExistingId() throws Exception {
        // Create the MstRegion with an existing ID
        mstRegion.setId(1L);
        MstRegionDTO mstRegionDTO = mstRegionMapper.toDto(mstRegion);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstRegionSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstRegionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstRegion in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstRegionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstRegionSearchRepository.findAll().collectList().block());
        // set the field null
        mstRegion.setName(null);

        // Create the MstRegion, which fails.
        MstRegionDTO mstRegionDTO = mstRegionMapper.toDto(mstRegion);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstRegionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstRegionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllMstRegions() {
        // Initialize the database
        insertedMstRegion = mstRegionRepository.save(mstRegion).block();

        // Get all the mstRegionList
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
            .value(hasItem(mstRegion.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].unm49Code")
            .value(hasItem(DEFAULT_UNM_49_CODE))
            .jsonPath("$.[*].isoAlpha2Code")
            .value(hasItem(DEFAULT_ISO_ALPHA_2_CODE));
    }

    @Test
    void getMstRegion() {
        // Initialize the database
        insertedMstRegion = mstRegionRepository.save(mstRegion).block();

        // Get the mstRegion
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, mstRegion.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(mstRegion.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.unm49Code")
            .value(is(DEFAULT_UNM_49_CODE))
            .jsonPath("$.isoAlpha2Code")
            .value(is(DEFAULT_ISO_ALPHA_2_CODE));
    }

    @Test
    void getMstRegionsByIdFiltering() {
        // Initialize the database
        insertedMstRegion = mstRegionRepository.save(mstRegion).block();

        Long id = mstRegion.getId();

        defaultMstRegionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMstRegionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMstRegionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllMstRegionsByNameIsEqualToSomething() {
        // Initialize the database
        insertedMstRegion = mstRegionRepository.save(mstRegion).block();

        // Get all the mstRegionList where name equals to
        defaultMstRegionFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllMstRegionsByNameIsInShouldWork() {
        // Initialize the database
        insertedMstRegion = mstRegionRepository.save(mstRegion).block();

        // Get all the mstRegionList where name in
        defaultMstRegionFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllMstRegionsByNameIsNullOrNotNull() {
        // Initialize the database
        insertedMstRegion = mstRegionRepository.save(mstRegion).block();

        // Get all the mstRegionList where name is not null
        defaultMstRegionFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllMstRegionsByNameContainsSomething() {
        // Initialize the database
        insertedMstRegion = mstRegionRepository.save(mstRegion).block();

        // Get all the mstRegionList where name contains
        defaultMstRegionFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllMstRegionsByNameNotContainsSomething() {
        // Initialize the database
        insertedMstRegion = mstRegionRepository.save(mstRegion).block();

        // Get all the mstRegionList where name does not contain
        defaultMstRegionFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllMstRegionsByUnm49CodeIsEqualToSomething() {
        // Initialize the database
        insertedMstRegion = mstRegionRepository.save(mstRegion).block();

        // Get all the mstRegionList where unm49Code equals to
        defaultMstRegionFiltering("unm49Code.equals=" + DEFAULT_UNM_49_CODE, "unm49Code.equals=" + UPDATED_UNM_49_CODE);
    }

    @Test
    void getAllMstRegionsByUnm49CodeIsInShouldWork() {
        // Initialize the database
        insertedMstRegion = mstRegionRepository.save(mstRegion).block();

        // Get all the mstRegionList where unm49Code in
        defaultMstRegionFiltering("unm49Code.in=" + DEFAULT_UNM_49_CODE + "," + UPDATED_UNM_49_CODE, "unm49Code.in=" + UPDATED_UNM_49_CODE);
    }

    @Test
    void getAllMstRegionsByUnm49CodeIsNullOrNotNull() {
        // Initialize the database
        insertedMstRegion = mstRegionRepository.save(mstRegion).block();

        // Get all the mstRegionList where unm49Code is not null
        defaultMstRegionFiltering("unm49Code.specified=true", "unm49Code.specified=false");
    }

    @Test
    void getAllMstRegionsByUnm49CodeContainsSomething() {
        // Initialize the database
        insertedMstRegion = mstRegionRepository.save(mstRegion).block();

        // Get all the mstRegionList where unm49Code contains
        defaultMstRegionFiltering("unm49Code.contains=" + DEFAULT_UNM_49_CODE, "unm49Code.contains=" + UPDATED_UNM_49_CODE);
    }

    @Test
    void getAllMstRegionsByUnm49CodeNotContainsSomething() {
        // Initialize the database
        insertedMstRegion = mstRegionRepository.save(mstRegion).block();

        // Get all the mstRegionList where unm49Code does not contain
        defaultMstRegionFiltering("unm49Code.doesNotContain=" + UPDATED_UNM_49_CODE, "unm49Code.doesNotContain=" + DEFAULT_UNM_49_CODE);
    }

    @Test
    void getAllMstRegionsByIsoAlpha2CodeIsEqualToSomething() {
        // Initialize the database
        insertedMstRegion = mstRegionRepository.save(mstRegion).block();

        // Get all the mstRegionList where isoAlpha2Code equals to
        defaultMstRegionFiltering("isoAlpha2Code.equals=" + DEFAULT_ISO_ALPHA_2_CODE, "isoAlpha2Code.equals=" + UPDATED_ISO_ALPHA_2_CODE);
    }

    @Test
    void getAllMstRegionsByIsoAlpha2CodeIsInShouldWork() {
        // Initialize the database
        insertedMstRegion = mstRegionRepository.save(mstRegion).block();

        // Get all the mstRegionList where isoAlpha2Code in
        defaultMstRegionFiltering(
            "isoAlpha2Code.in=" + DEFAULT_ISO_ALPHA_2_CODE + "," + UPDATED_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.in=" + UPDATED_ISO_ALPHA_2_CODE
        );
    }

    @Test
    void getAllMstRegionsByIsoAlpha2CodeIsNullOrNotNull() {
        // Initialize the database
        insertedMstRegion = mstRegionRepository.save(mstRegion).block();

        // Get all the mstRegionList where isoAlpha2Code is not null
        defaultMstRegionFiltering("isoAlpha2Code.specified=true", "isoAlpha2Code.specified=false");
    }

    @Test
    void getAllMstRegionsByIsoAlpha2CodeContainsSomething() {
        // Initialize the database
        insertedMstRegion = mstRegionRepository.save(mstRegion).block();

        // Get all the mstRegionList where isoAlpha2Code contains
        defaultMstRegionFiltering(
            "isoAlpha2Code.contains=" + DEFAULT_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.contains=" + UPDATED_ISO_ALPHA_2_CODE
        );
    }

    @Test
    void getAllMstRegionsByIsoAlpha2CodeNotContainsSomething() {
        // Initialize the database
        insertedMstRegion = mstRegionRepository.save(mstRegion).block();

        // Get all the mstRegionList where isoAlpha2Code does not contain
        defaultMstRegionFiltering(
            "isoAlpha2Code.doesNotContain=" + UPDATED_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.doesNotContain=" + DEFAULT_ISO_ALPHA_2_CODE
        );
    }

    private void defaultMstRegionFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultMstRegionShouldBeFound(shouldBeFound);
        defaultMstRegionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMstRegionShouldBeFound(String filter) {
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
            .value(hasItem(mstRegion.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].unm49Code")
            .value(hasItem(DEFAULT_UNM_49_CODE))
            .jsonPath("$.[*].isoAlpha2Code")
            .value(hasItem(DEFAULT_ISO_ALPHA_2_CODE));

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
    private void defaultMstRegionShouldNotBeFound(String filter) {
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
    void getNonExistingMstRegion() {
        // Get the mstRegion
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMstRegion() throws Exception {
        // Initialize the database
        insertedMstRegion = mstRegionRepository.save(mstRegion).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        mstRegionSearchRepository.save(mstRegion).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstRegionSearchRepository.findAll().collectList().block());

        // Update the mstRegion
        MstRegion updatedMstRegion = mstRegionRepository.findById(mstRegion.getId()).block();
        updatedMstRegion.name(UPDATED_NAME).unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);
        MstRegionDTO mstRegionDTO = mstRegionMapper.toDto(updatedMstRegion);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstRegionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstRegionDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstRegion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMstRegionToMatchAllProperties(updatedMstRegion);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstRegionSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<MstRegion> mstRegionSearchList = Streamable.of(mstRegionSearchRepository.findAll().collectList().block()).toList();
                MstRegion testMstRegionSearch = mstRegionSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertMstRegionAllPropertiesEquals(testMstRegionSearch, updatedMstRegion);
                assertMstRegionUpdatableFieldsEquals(testMstRegionSearch, updatedMstRegion);
            });
    }

    @Test
    void putNonExistingMstRegion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstRegionSearchRepository.findAll().collectList().block());
        mstRegion.setId(longCount.incrementAndGet());

        // Create the MstRegion
        MstRegionDTO mstRegionDTO = mstRegionMapper.toDto(mstRegion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstRegionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstRegionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstRegion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstRegionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchMstRegion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstRegionSearchRepository.findAll().collectList().block());
        mstRegion.setId(longCount.incrementAndGet());

        // Create the MstRegion
        MstRegionDTO mstRegionDTO = mstRegionMapper.toDto(mstRegion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstRegionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstRegion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstRegionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamMstRegion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstRegionSearchRepository.findAll().collectList().block());
        mstRegion.setId(longCount.incrementAndGet());

        // Create the MstRegion
        MstRegionDTO mstRegionDTO = mstRegionMapper.toDto(mstRegion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstRegionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstRegion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstRegionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateMstRegionWithPatch() throws Exception {
        // Initialize the database
        insertedMstRegion = mstRegionRepository.save(mstRegion).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstRegion using partial update
        MstRegion partialUpdatedMstRegion = new MstRegion();
        partialUpdatedMstRegion.setId(mstRegion.getId());

        partialUpdatedMstRegion.isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstRegion.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstRegion))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstRegion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstRegionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMstRegion, mstRegion),
            getPersistedMstRegion(mstRegion)
        );
    }

    @Test
    void fullUpdateMstRegionWithPatch() throws Exception {
        // Initialize the database
        insertedMstRegion = mstRegionRepository.save(mstRegion).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstRegion using partial update
        MstRegion partialUpdatedMstRegion = new MstRegion();
        partialUpdatedMstRegion.setId(mstRegion.getId());

        partialUpdatedMstRegion.name(UPDATED_NAME).unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstRegion.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstRegion))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstRegion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstRegionUpdatableFieldsEquals(partialUpdatedMstRegion, getPersistedMstRegion(partialUpdatedMstRegion));
    }

    @Test
    void patchNonExistingMstRegion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstRegionSearchRepository.findAll().collectList().block());
        mstRegion.setId(longCount.incrementAndGet());

        // Create the MstRegion
        MstRegionDTO mstRegionDTO = mstRegionMapper.toDto(mstRegion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, mstRegionDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstRegionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstRegion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstRegionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchMstRegion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstRegionSearchRepository.findAll().collectList().block());
        mstRegion.setId(longCount.incrementAndGet());

        // Create the MstRegion
        MstRegionDTO mstRegionDTO = mstRegionMapper.toDto(mstRegion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstRegionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstRegion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstRegionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamMstRegion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstRegionSearchRepository.findAll().collectList().block());
        mstRegion.setId(longCount.incrementAndGet());

        // Create the MstRegion
        MstRegionDTO mstRegionDTO = mstRegionMapper.toDto(mstRegion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstRegionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstRegion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstRegionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteMstRegion() {
        // Initialize the database
        insertedMstRegion = mstRegionRepository.save(mstRegion).block();
        mstRegionRepository.save(mstRegion).block();
        mstRegionSearchRepository.save(mstRegion).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstRegionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the mstRegion
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, mstRegion.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstRegionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchMstRegion() {
        // Initialize the database
        insertedMstRegion = mstRegionRepository.save(mstRegion).block();
        mstRegionSearchRepository.save(mstRegion).block();

        // Search the mstRegion
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + mstRegion.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(mstRegion.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].unm49Code")
            .value(hasItem(DEFAULT_UNM_49_CODE))
            .jsonPath("$.[*].isoAlpha2Code")
            .value(hasItem(DEFAULT_ISO_ALPHA_2_CODE));
    }

    protected long getRepositoryCount() {
        return mstRegionRepository.count().block();
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

    protected MstRegion getPersistedMstRegion(MstRegion mstRegion) {
        return mstRegionRepository.findById(mstRegion.getId()).block();
    }

    protected void assertPersistedMstRegionToMatchAllProperties(MstRegion expectedMstRegion) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstRegionAllPropertiesEquals(expectedMstRegion, getPersistedMstRegion(expectedMstRegion));
        assertMstRegionUpdatableFieldsEquals(expectedMstRegion, getPersistedMstRegion(expectedMstRegion));
    }

    protected void assertPersistedMstRegionToMatchUpdatableProperties(MstRegion expectedMstRegion) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstRegionAllUpdatablePropertiesEquals(expectedMstRegion, getPersistedMstRegion(expectedMstRegion));
        assertMstRegionUpdatableFieldsEquals(expectedMstRegion, getPersistedMstRegion(expectedMstRegion));
    }
}
