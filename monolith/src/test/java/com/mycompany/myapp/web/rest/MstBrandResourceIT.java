package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.MstBrandAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.MstBrand;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MstBrandRepository;
import com.mycompany.myapp.repository.search.MstBrandSearchRepository;
import com.mycompany.myapp.service.dto.MstBrandDTO;
import com.mycompany.myapp.service.mapper.MstBrandMapper;
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
 * Integration tests for the {@link MstBrandResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MstBrandResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LOGO = "AAAAAAAAAA";
    private static final String UPDATED_LOGO = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/mst-brands";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/mst-brands/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MstBrandRepository mstBrandRepository;

    @Autowired
    private MstBrandMapper mstBrandMapper;

    @Autowired
    private MstBrandSearchRepository mstBrandSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private MstBrand mstBrand;

    private MstBrand insertedMstBrand;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstBrand createEntity(EntityManager em) {
        MstBrand mstBrand = new MstBrand().name(DEFAULT_NAME).logo(DEFAULT_LOGO).description(DEFAULT_DESCRIPTION);
        return mstBrand;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstBrand createUpdatedEntity(EntityManager em) {
        MstBrand mstBrand = new MstBrand().name(UPDATED_NAME).logo(UPDATED_LOGO).description(UPDATED_DESCRIPTION);
        return mstBrand;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(MstBrand.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        mstBrand = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedMstBrand != null) {
            mstBrandRepository.delete(insertedMstBrand).block();
            mstBrandSearchRepository.delete(insertedMstBrand).block();
            insertedMstBrand = null;
        }
        deleteEntities(em);
    }

    @Test
    void createMstBrand() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstBrandSearchRepository.findAll().collectList().block());
        // Create the MstBrand
        MstBrandDTO mstBrandDTO = mstBrandMapper.toDto(mstBrand);
        var returnedMstBrandDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstBrandDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MstBrandDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MstBrand in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMstBrand = mstBrandMapper.toEntity(returnedMstBrandDTO);
        assertMstBrandUpdatableFieldsEquals(returnedMstBrand, getPersistedMstBrand(returnedMstBrand));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstBrandSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedMstBrand = returnedMstBrand;
    }

    @Test
    void createMstBrandWithExistingId() throws Exception {
        // Create the MstBrand with an existing ID
        mstBrand.setId(1L);
        MstBrandDTO mstBrandDTO = mstBrandMapper.toDto(mstBrand);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstBrandSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstBrandDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstBrand in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstBrandSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstBrandSearchRepository.findAll().collectList().block());
        // set the field null
        mstBrand.setName(null);

        // Create the MstBrand, which fails.
        MstBrandDTO mstBrandDTO = mstBrandMapper.toDto(mstBrand);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstBrandDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstBrandSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllMstBrands() {
        // Initialize the database
        insertedMstBrand = mstBrandRepository.save(mstBrand).block();

        // Get all the mstBrandList
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
            .value(hasItem(mstBrand.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].logo")
            .value(hasItem(DEFAULT_LOGO))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    @Test
    void getMstBrand() {
        // Initialize the database
        insertedMstBrand = mstBrandRepository.save(mstBrand).block();

        // Get the mstBrand
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, mstBrand.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(mstBrand.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.logo")
            .value(is(DEFAULT_LOGO))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION));
    }

    @Test
    void getMstBrandsByIdFiltering() {
        // Initialize the database
        insertedMstBrand = mstBrandRepository.save(mstBrand).block();

        Long id = mstBrand.getId();

        defaultMstBrandFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMstBrandFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMstBrandFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllMstBrandsByNameIsEqualToSomething() {
        // Initialize the database
        insertedMstBrand = mstBrandRepository.save(mstBrand).block();

        // Get all the mstBrandList where name equals to
        defaultMstBrandFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllMstBrandsByNameIsInShouldWork() {
        // Initialize the database
        insertedMstBrand = mstBrandRepository.save(mstBrand).block();

        // Get all the mstBrandList where name in
        defaultMstBrandFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllMstBrandsByNameIsNullOrNotNull() {
        // Initialize the database
        insertedMstBrand = mstBrandRepository.save(mstBrand).block();

        // Get all the mstBrandList where name is not null
        defaultMstBrandFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllMstBrandsByNameContainsSomething() {
        // Initialize the database
        insertedMstBrand = mstBrandRepository.save(mstBrand).block();

        // Get all the mstBrandList where name contains
        defaultMstBrandFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllMstBrandsByNameNotContainsSomething() {
        // Initialize the database
        insertedMstBrand = mstBrandRepository.save(mstBrand).block();

        // Get all the mstBrandList where name does not contain
        defaultMstBrandFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllMstBrandsByLogoIsEqualToSomething() {
        // Initialize the database
        insertedMstBrand = mstBrandRepository.save(mstBrand).block();

        // Get all the mstBrandList where logo equals to
        defaultMstBrandFiltering("logo.equals=" + DEFAULT_LOGO, "logo.equals=" + UPDATED_LOGO);
    }

    @Test
    void getAllMstBrandsByLogoIsInShouldWork() {
        // Initialize the database
        insertedMstBrand = mstBrandRepository.save(mstBrand).block();

        // Get all the mstBrandList where logo in
        defaultMstBrandFiltering("logo.in=" + DEFAULT_LOGO + "," + UPDATED_LOGO, "logo.in=" + UPDATED_LOGO);
    }

    @Test
    void getAllMstBrandsByLogoIsNullOrNotNull() {
        // Initialize the database
        insertedMstBrand = mstBrandRepository.save(mstBrand).block();

        // Get all the mstBrandList where logo is not null
        defaultMstBrandFiltering("logo.specified=true", "logo.specified=false");
    }

    @Test
    void getAllMstBrandsByLogoContainsSomething() {
        // Initialize the database
        insertedMstBrand = mstBrandRepository.save(mstBrand).block();

        // Get all the mstBrandList where logo contains
        defaultMstBrandFiltering("logo.contains=" + DEFAULT_LOGO, "logo.contains=" + UPDATED_LOGO);
    }

    @Test
    void getAllMstBrandsByLogoNotContainsSomething() {
        // Initialize the database
        insertedMstBrand = mstBrandRepository.save(mstBrand).block();

        // Get all the mstBrandList where logo does not contain
        defaultMstBrandFiltering("logo.doesNotContain=" + UPDATED_LOGO, "logo.doesNotContain=" + DEFAULT_LOGO);
    }

    @Test
    void getAllMstBrandsByDescriptionIsEqualToSomething() {
        // Initialize the database
        insertedMstBrand = mstBrandRepository.save(mstBrand).block();

        // Get all the mstBrandList where description equals to
        defaultMstBrandFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    void getAllMstBrandsByDescriptionIsInShouldWork() {
        // Initialize the database
        insertedMstBrand = mstBrandRepository.save(mstBrand).block();

        // Get all the mstBrandList where description in
        defaultMstBrandFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    void getAllMstBrandsByDescriptionIsNullOrNotNull() {
        // Initialize the database
        insertedMstBrand = mstBrandRepository.save(mstBrand).block();

        // Get all the mstBrandList where description is not null
        defaultMstBrandFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    void getAllMstBrandsByDescriptionContainsSomething() {
        // Initialize the database
        insertedMstBrand = mstBrandRepository.save(mstBrand).block();

        // Get all the mstBrandList where description contains
        defaultMstBrandFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    void getAllMstBrandsByDescriptionNotContainsSomething() {
        // Initialize the database
        insertedMstBrand = mstBrandRepository.save(mstBrand).block();

        // Get all the mstBrandList where description does not contain
        defaultMstBrandFiltering("description.doesNotContain=" + UPDATED_DESCRIPTION, "description.doesNotContain=" + DEFAULT_DESCRIPTION);
    }

    private void defaultMstBrandFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultMstBrandShouldBeFound(shouldBeFound);
        defaultMstBrandShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMstBrandShouldBeFound(String filter) {
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
            .value(hasItem(mstBrand.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].logo")
            .value(hasItem(DEFAULT_LOGO))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));

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
    private void defaultMstBrandShouldNotBeFound(String filter) {
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
    void getNonExistingMstBrand() {
        // Get the mstBrand
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMstBrand() throws Exception {
        // Initialize the database
        insertedMstBrand = mstBrandRepository.save(mstBrand).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        mstBrandSearchRepository.save(mstBrand).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstBrandSearchRepository.findAll().collectList().block());

        // Update the mstBrand
        MstBrand updatedMstBrand = mstBrandRepository.findById(mstBrand.getId()).block();
        updatedMstBrand.name(UPDATED_NAME).logo(UPDATED_LOGO).description(UPDATED_DESCRIPTION);
        MstBrandDTO mstBrandDTO = mstBrandMapper.toDto(updatedMstBrand);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstBrandDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstBrandDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstBrand in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMstBrandToMatchAllProperties(updatedMstBrand);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstBrandSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<MstBrand> mstBrandSearchList = Streamable.of(mstBrandSearchRepository.findAll().collectList().block()).toList();
                MstBrand testMstBrandSearch = mstBrandSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertMstBrandAllPropertiesEquals(testMstBrandSearch, updatedMstBrand);
                assertMstBrandUpdatableFieldsEquals(testMstBrandSearch, updatedMstBrand);
            });
    }

    @Test
    void putNonExistingMstBrand() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstBrandSearchRepository.findAll().collectList().block());
        mstBrand.setId(longCount.incrementAndGet());

        // Create the MstBrand
        MstBrandDTO mstBrandDTO = mstBrandMapper.toDto(mstBrand);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstBrandDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstBrandDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstBrand in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstBrandSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchMstBrand() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstBrandSearchRepository.findAll().collectList().block());
        mstBrand.setId(longCount.incrementAndGet());

        // Create the MstBrand
        MstBrandDTO mstBrandDTO = mstBrandMapper.toDto(mstBrand);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstBrandDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstBrand in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstBrandSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamMstBrand() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstBrandSearchRepository.findAll().collectList().block());
        mstBrand.setId(longCount.incrementAndGet());

        // Create the MstBrand
        MstBrandDTO mstBrandDTO = mstBrandMapper.toDto(mstBrand);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstBrandDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstBrand in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstBrandSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateMstBrandWithPatch() throws Exception {
        // Initialize the database
        insertedMstBrand = mstBrandRepository.save(mstBrand).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstBrand using partial update
        MstBrand partialUpdatedMstBrand = new MstBrand();
        partialUpdatedMstBrand.setId(mstBrand.getId());

        partialUpdatedMstBrand.description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstBrand.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstBrand))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstBrand in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstBrandUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedMstBrand, mstBrand), getPersistedMstBrand(mstBrand));
    }

    @Test
    void fullUpdateMstBrandWithPatch() throws Exception {
        // Initialize the database
        insertedMstBrand = mstBrandRepository.save(mstBrand).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstBrand using partial update
        MstBrand partialUpdatedMstBrand = new MstBrand();
        partialUpdatedMstBrand.setId(mstBrand.getId());

        partialUpdatedMstBrand.name(UPDATED_NAME).logo(UPDATED_LOGO).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstBrand.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstBrand))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstBrand in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstBrandUpdatableFieldsEquals(partialUpdatedMstBrand, getPersistedMstBrand(partialUpdatedMstBrand));
    }

    @Test
    void patchNonExistingMstBrand() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstBrandSearchRepository.findAll().collectList().block());
        mstBrand.setId(longCount.incrementAndGet());

        // Create the MstBrand
        MstBrandDTO mstBrandDTO = mstBrandMapper.toDto(mstBrand);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, mstBrandDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstBrandDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstBrand in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstBrandSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchMstBrand() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstBrandSearchRepository.findAll().collectList().block());
        mstBrand.setId(longCount.incrementAndGet());

        // Create the MstBrand
        MstBrandDTO mstBrandDTO = mstBrandMapper.toDto(mstBrand);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstBrandDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstBrand in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstBrandSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamMstBrand() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstBrandSearchRepository.findAll().collectList().block());
        mstBrand.setId(longCount.incrementAndGet());

        // Create the MstBrand
        MstBrandDTO mstBrandDTO = mstBrandMapper.toDto(mstBrand);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstBrandDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstBrand in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstBrandSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteMstBrand() {
        // Initialize the database
        insertedMstBrand = mstBrandRepository.save(mstBrand).block();
        mstBrandRepository.save(mstBrand).block();
        mstBrandSearchRepository.save(mstBrand).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstBrandSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the mstBrand
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, mstBrand.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstBrandSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchMstBrand() {
        // Initialize the database
        insertedMstBrand = mstBrandRepository.save(mstBrand).block();
        mstBrandSearchRepository.save(mstBrand).block();

        // Search the mstBrand
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + mstBrand.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(mstBrand.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].logo")
            .value(hasItem(DEFAULT_LOGO))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    protected long getRepositoryCount() {
        return mstBrandRepository.count().block();
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

    protected MstBrand getPersistedMstBrand(MstBrand mstBrand) {
        return mstBrandRepository.findById(mstBrand.getId()).block();
    }

    protected void assertPersistedMstBrandToMatchAllProperties(MstBrand expectedMstBrand) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstBrandAllPropertiesEquals(expectedMstBrand, getPersistedMstBrand(expectedMstBrand));
        assertMstBrandUpdatableFieldsEquals(expectedMstBrand, getPersistedMstBrand(expectedMstBrand));
    }

    protected void assertPersistedMstBrandToMatchUpdatableProperties(MstBrand expectedMstBrand) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstBrandAllUpdatablePropertiesEquals(expectedMstBrand, getPersistedMstBrand(expectedMstBrand));
        assertMstBrandUpdatableFieldsEquals(expectedMstBrand, getPersistedMstBrand(expectedMstBrand));
    }
}
