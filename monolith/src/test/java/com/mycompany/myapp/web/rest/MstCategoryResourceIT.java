package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.MstCategoryAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.MstCategory;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MstCategoryRepository;
import com.mycompany.myapp.repository.search.MstCategorySearchRepository;
import com.mycompany.myapp.service.dto.MstCategoryDTO;
import com.mycompany.myapp.service.mapper.MstCategoryMapper;
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
 * Integration tests for the {@link MstCategoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MstCategoryResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/mst-categories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/mst-categories/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MstCategoryRepository mstCategoryRepository;

    @Autowired
    private MstCategoryMapper mstCategoryMapper;

    @Autowired
    private MstCategorySearchRepository mstCategorySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private MstCategory mstCategory;

    private MstCategory insertedMstCategory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstCategory createEntity(EntityManager em) {
        MstCategory mstCategory = new MstCategory().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
        return mstCategory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstCategory createUpdatedEntity(EntityManager em) {
        MstCategory mstCategory = new MstCategory().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        return mstCategory;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(MstCategory.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        mstCategory = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedMstCategory != null) {
            mstCategoryRepository.delete(insertedMstCategory).block();
            mstCategorySearchRepository.delete(insertedMstCategory).block();
            insertedMstCategory = null;
        }
        deleteEntities(em);
    }

    @Test
    void createMstCategory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCategorySearchRepository.findAll().collectList().block());
        // Create the MstCategory
        MstCategoryDTO mstCategoryDTO = mstCategoryMapper.toDto(mstCategory);
        var returnedMstCategoryDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCategoryDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MstCategoryDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MstCategory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMstCategory = mstCategoryMapper.toEntity(returnedMstCategoryDTO);
        assertMstCategoryUpdatableFieldsEquals(returnedMstCategory, getPersistedMstCategory(returnedMstCategory));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCategorySearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedMstCategory = returnedMstCategory;
    }

    @Test
    void createMstCategoryWithExistingId() throws Exception {
        // Create the MstCategory with an existing ID
        mstCategory.setId(1L);
        MstCategoryDTO mstCategoryDTO = mstCategoryMapper.toDto(mstCategory);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCategorySearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCategoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCategorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCategorySearchRepository.findAll().collectList().block());
        // set the field null
        mstCategory.setName(null);

        // Create the MstCategory, which fails.
        MstCategoryDTO mstCategoryDTO = mstCategoryMapper.toDto(mstCategory);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCategoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCategorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllMstCategories() {
        // Initialize the database
        insertedMstCategory = mstCategoryRepository.save(mstCategory).block();

        // Get all the mstCategoryList
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
            .value(hasItem(mstCategory.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    @Test
    void getMstCategory() {
        // Initialize the database
        insertedMstCategory = mstCategoryRepository.save(mstCategory).block();

        // Get the mstCategory
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, mstCategory.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(mstCategory.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION));
    }

    @Test
    void getMstCategoriesByIdFiltering() {
        // Initialize the database
        insertedMstCategory = mstCategoryRepository.save(mstCategory).block();

        Long id = mstCategory.getId();

        defaultMstCategoryFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMstCategoryFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMstCategoryFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllMstCategoriesByNameIsEqualToSomething() {
        // Initialize the database
        insertedMstCategory = mstCategoryRepository.save(mstCategory).block();

        // Get all the mstCategoryList where name equals to
        defaultMstCategoryFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllMstCategoriesByNameIsInShouldWork() {
        // Initialize the database
        insertedMstCategory = mstCategoryRepository.save(mstCategory).block();

        // Get all the mstCategoryList where name in
        defaultMstCategoryFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllMstCategoriesByNameIsNullOrNotNull() {
        // Initialize the database
        insertedMstCategory = mstCategoryRepository.save(mstCategory).block();

        // Get all the mstCategoryList where name is not null
        defaultMstCategoryFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllMstCategoriesByNameContainsSomething() {
        // Initialize the database
        insertedMstCategory = mstCategoryRepository.save(mstCategory).block();

        // Get all the mstCategoryList where name contains
        defaultMstCategoryFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllMstCategoriesByNameNotContainsSomething() {
        // Initialize the database
        insertedMstCategory = mstCategoryRepository.save(mstCategory).block();

        // Get all the mstCategoryList where name does not contain
        defaultMstCategoryFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllMstCategoriesByDescriptionIsEqualToSomething() {
        // Initialize the database
        insertedMstCategory = mstCategoryRepository.save(mstCategory).block();

        // Get all the mstCategoryList where description equals to
        defaultMstCategoryFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    void getAllMstCategoriesByDescriptionIsInShouldWork() {
        // Initialize the database
        insertedMstCategory = mstCategoryRepository.save(mstCategory).block();

        // Get all the mstCategoryList where description in
        defaultMstCategoryFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    void getAllMstCategoriesByDescriptionIsNullOrNotNull() {
        // Initialize the database
        insertedMstCategory = mstCategoryRepository.save(mstCategory).block();

        // Get all the mstCategoryList where description is not null
        defaultMstCategoryFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    void getAllMstCategoriesByDescriptionContainsSomething() {
        // Initialize the database
        insertedMstCategory = mstCategoryRepository.save(mstCategory).block();

        // Get all the mstCategoryList where description contains
        defaultMstCategoryFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    void getAllMstCategoriesByDescriptionNotContainsSomething() {
        // Initialize the database
        insertedMstCategory = mstCategoryRepository.save(mstCategory).block();

        // Get all the mstCategoryList where description does not contain
        defaultMstCategoryFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    private void defaultMstCategoryFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultMstCategoryShouldBeFound(shouldBeFound);
        defaultMstCategoryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMstCategoryShouldBeFound(String filter) {
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
            .value(hasItem(mstCategory.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
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
    private void defaultMstCategoryShouldNotBeFound(String filter) {
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
    void getNonExistingMstCategory() {
        // Get the mstCategory
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMstCategory() throws Exception {
        // Initialize the database
        insertedMstCategory = mstCategoryRepository.save(mstCategory).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        mstCategorySearchRepository.save(mstCategory).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCategorySearchRepository.findAll().collectList().block());

        // Update the mstCategory
        MstCategory updatedMstCategory = mstCategoryRepository.findById(mstCategory.getId()).block();
        updatedMstCategory.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        MstCategoryDTO mstCategoryDTO = mstCategoryMapper.toDto(updatedMstCategory);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstCategoryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCategoryDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMstCategoryToMatchAllProperties(updatedMstCategory);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCategorySearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<MstCategory> mstCategorySearchList = Streamable.of(
                    mstCategorySearchRepository.findAll().collectList().block()
                ).toList();
                MstCategory testMstCategorySearch = mstCategorySearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertMstCategoryAllPropertiesEquals(testMstCategorySearch, updatedMstCategory);
                assertMstCategoryUpdatableFieldsEquals(testMstCategorySearch, updatedMstCategory);
            });
    }

    @Test
    void putNonExistingMstCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCategorySearchRepository.findAll().collectList().block());
        mstCategory.setId(longCount.incrementAndGet());

        // Create the MstCategory
        MstCategoryDTO mstCategoryDTO = mstCategoryMapper.toDto(mstCategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstCategoryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCategoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCategorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchMstCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCategorySearchRepository.findAll().collectList().block());
        mstCategory.setId(longCount.incrementAndGet());

        // Create the MstCategory
        MstCategoryDTO mstCategoryDTO = mstCategoryMapper.toDto(mstCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCategoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCategorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamMstCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCategorySearchRepository.findAll().collectList().block());
        mstCategory.setId(longCount.incrementAndGet());

        // Create the MstCategory
        MstCategoryDTO mstCategoryDTO = mstCategoryMapper.toDto(mstCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCategoryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCategorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateMstCategoryWithPatch() throws Exception {
        // Initialize the database
        insertedMstCategory = mstCategoryRepository.save(mstCategory).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstCategory using partial update
        MstCategory partialUpdatedMstCategory = new MstCategory();
        partialUpdatedMstCategory.setId(mstCategory.getId());

        partialUpdatedMstCategory.description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstCategory.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstCategory))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstCategory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstCategoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMstCategory, mstCategory),
            getPersistedMstCategory(mstCategory)
        );
    }

    @Test
    void fullUpdateMstCategoryWithPatch() throws Exception {
        // Initialize the database
        insertedMstCategory = mstCategoryRepository.save(mstCategory).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstCategory using partial update
        MstCategory partialUpdatedMstCategory = new MstCategory();
        partialUpdatedMstCategory.setId(mstCategory.getId());

        partialUpdatedMstCategory.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstCategory.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstCategory))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstCategory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstCategoryUpdatableFieldsEquals(partialUpdatedMstCategory, getPersistedMstCategory(partialUpdatedMstCategory));
    }

    @Test
    void patchNonExistingMstCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCategorySearchRepository.findAll().collectList().block());
        mstCategory.setId(longCount.incrementAndGet());

        // Create the MstCategory
        MstCategoryDTO mstCategoryDTO = mstCategoryMapper.toDto(mstCategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, mstCategoryDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstCategoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCategorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchMstCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCategorySearchRepository.findAll().collectList().block());
        mstCategory.setId(longCount.incrementAndGet());

        // Create the MstCategory
        MstCategoryDTO mstCategoryDTO = mstCategoryMapper.toDto(mstCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstCategoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCategorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamMstCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCategorySearchRepository.findAll().collectList().block());
        mstCategory.setId(longCount.incrementAndGet());

        // Create the MstCategory
        MstCategoryDTO mstCategoryDTO = mstCategoryMapper.toDto(mstCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstCategoryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCategorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteMstCategory() {
        // Initialize the database
        insertedMstCategory = mstCategoryRepository.save(mstCategory).block();
        mstCategoryRepository.save(mstCategory).block();
        mstCategorySearchRepository.save(mstCategory).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCategorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the mstCategory
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, mstCategory.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCategorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchMstCategory() {
        // Initialize the database
        insertedMstCategory = mstCategoryRepository.save(mstCategory).block();
        mstCategorySearchRepository.save(mstCategory).block();

        // Search the mstCategory
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + mstCategory.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(mstCategory.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    protected long getRepositoryCount() {
        return mstCategoryRepository.count().block();
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

    protected MstCategory getPersistedMstCategory(MstCategory mstCategory) {
        return mstCategoryRepository.findById(mstCategory.getId()).block();
    }

    protected void assertPersistedMstCategoryToMatchAllProperties(MstCategory expectedMstCategory) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstCategoryAllPropertiesEquals(expectedMstCategory, getPersistedMstCategory(expectedMstCategory));
        assertMstCategoryUpdatableFieldsEquals(expectedMstCategory, getPersistedMstCategory(expectedMstCategory));
    }

    protected void assertPersistedMstCategoryToMatchUpdatableProperties(MstCategory expectedMstCategory) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstCategoryAllUpdatablePropertiesEquals(expectedMstCategory, getPersistedMstCategory(expectedMstCategory));
        assertMstCategoryUpdatableFieldsEquals(expectedMstCategory, getPersistedMstCategory(expectedMstCategory));
    }
}
