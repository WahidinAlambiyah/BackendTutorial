package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.MstTaskAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.MstTask;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MstTaskRepository;
import com.mycompany.myapp.repository.search.MstTaskSearchRepository;
import com.mycompany.myapp.service.dto.MstTaskDTO;
import com.mycompany.myapp.service.mapper.MstTaskMapper;
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
 * Integration tests for the {@link MstTaskResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MstTaskResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/mst-tasks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/mst-tasks/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MstTaskRepository mstTaskRepository;

    @Autowired
    private MstTaskMapper mstTaskMapper;

    @Autowired
    private MstTaskSearchRepository mstTaskSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private MstTask mstTask;

    private MstTask insertedMstTask;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstTask createEntity(EntityManager em) {
        MstTask mstTask = new MstTask().title(DEFAULT_TITLE).description(DEFAULT_DESCRIPTION);
        return mstTask;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstTask createUpdatedEntity(EntityManager em) {
        MstTask mstTask = new MstTask().title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);
        return mstTask;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(MstTask.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        mstTask = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedMstTask != null) {
            mstTaskRepository.delete(insertedMstTask).block();
            mstTaskSearchRepository.delete(insertedMstTask).block();
            insertedMstTask = null;
        }
        deleteEntities(em);
    }

    @Test
    void createMstTask() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstTaskSearchRepository.findAll().collectList().block());
        // Create the MstTask
        MstTaskDTO mstTaskDTO = mstTaskMapper.toDto(mstTask);
        var returnedMstTaskDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstTaskDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MstTaskDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MstTask in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMstTask = mstTaskMapper.toEntity(returnedMstTaskDTO);
        assertMstTaskUpdatableFieldsEquals(returnedMstTask, getPersistedMstTask(returnedMstTask));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstTaskSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedMstTask = returnedMstTask;
    }

    @Test
    void createMstTaskWithExistingId() throws Exception {
        // Create the MstTask with an existing ID
        mstTask.setId(1L);
        MstTaskDTO mstTaskDTO = mstTaskMapper.toDto(mstTask);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstTaskSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstTaskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstTask in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstTaskSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllMstTasks() {
        // Initialize the database
        insertedMstTask = mstTaskRepository.save(mstTask).block();

        // Get all the mstTaskList
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
            .value(hasItem(mstTask.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    @Test
    void getMstTask() {
        // Initialize the database
        insertedMstTask = mstTaskRepository.save(mstTask).block();

        // Get the mstTask
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, mstTask.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(mstTask.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION));
    }

    @Test
    void getMstTasksByIdFiltering() {
        // Initialize the database
        insertedMstTask = mstTaskRepository.save(mstTask).block();

        Long id = mstTask.getId();

        defaultMstTaskFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMstTaskFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMstTaskFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllMstTasksByTitleIsEqualToSomething() {
        // Initialize the database
        insertedMstTask = mstTaskRepository.save(mstTask).block();

        // Get all the mstTaskList where title equals to
        defaultMstTaskFiltering("title.equals=" + DEFAULT_TITLE, "title.equals=" + UPDATED_TITLE);
    }

    @Test
    void getAllMstTasksByTitleIsInShouldWork() {
        // Initialize the database
        insertedMstTask = mstTaskRepository.save(mstTask).block();

        // Get all the mstTaskList where title in
        defaultMstTaskFiltering("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE, "title.in=" + UPDATED_TITLE);
    }

    @Test
    void getAllMstTasksByTitleIsNullOrNotNull() {
        // Initialize the database
        insertedMstTask = mstTaskRepository.save(mstTask).block();

        // Get all the mstTaskList where title is not null
        defaultMstTaskFiltering("title.specified=true", "title.specified=false");
    }

    @Test
    void getAllMstTasksByTitleContainsSomething() {
        // Initialize the database
        insertedMstTask = mstTaskRepository.save(mstTask).block();

        // Get all the mstTaskList where title contains
        defaultMstTaskFiltering("title.contains=" + DEFAULT_TITLE, "title.contains=" + UPDATED_TITLE);
    }

    @Test
    void getAllMstTasksByTitleNotContainsSomething() {
        // Initialize the database
        insertedMstTask = mstTaskRepository.save(mstTask).block();

        // Get all the mstTaskList where title does not contain
        defaultMstTaskFiltering("title.doesNotContain=" + UPDATED_TITLE, "title.doesNotContain=" + DEFAULT_TITLE);
    }

    @Test
    void getAllMstTasksByDescriptionIsEqualToSomething() {
        // Initialize the database
        insertedMstTask = mstTaskRepository.save(mstTask).block();

        // Get all the mstTaskList where description equals to
        defaultMstTaskFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    void getAllMstTasksByDescriptionIsInShouldWork() {
        // Initialize the database
        insertedMstTask = mstTaskRepository.save(mstTask).block();

        // Get all the mstTaskList where description in
        defaultMstTaskFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    void getAllMstTasksByDescriptionIsNullOrNotNull() {
        // Initialize the database
        insertedMstTask = mstTaskRepository.save(mstTask).block();

        // Get all the mstTaskList where description is not null
        defaultMstTaskFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    void getAllMstTasksByDescriptionContainsSomething() {
        // Initialize the database
        insertedMstTask = mstTaskRepository.save(mstTask).block();

        // Get all the mstTaskList where description contains
        defaultMstTaskFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    void getAllMstTasksByDescriptionNotContainsSomething() {
        // Initialize the database
        insertedMstTask = mstTaskRepository.save(mstTask).block();

        // Get all the mstTaskList where description does not contain
        defaultMstTaskFiltering("description.doesNotContain=" + UPDATED_DESCRIPTION, "description.doesNotContain=" + DEFAULT_DESCRIPTION);
    }

    private void defaultMstTaskFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultMstTaskShouldBeFound(shouldBeFound);
        defaultMstTaskShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMstTaskShouldBeFound(String filter) {
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
            .value(hasItem(mstTask.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
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
    private void defaultMstTaskShouldNotBeFound(String filter) {
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
    void getNonExistingMstTask() {
        // Get the mstTask
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMstTask() throws Exception {
        // Initialize the database
        insertedMstTask = mstTaskRepository.save(mstTask).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        mstTaskSearchRepository.save(mstTask).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstTaskSearchRepository.findAll().collectList().block());

        // Update the mstTask
        MstTask updatedMstTask = mstTaskRepository.findById(mstTask.getId()).block();
        updatedMstTask.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);
        MstTaskDTO mstTaskDTO = mstTaskMapper.toDto(updatedMstTask);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstTaskDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstTaskDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMstTaskToMatchAllProperties(updatedMstTask);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstTaskSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<MstTask> mstTaskSearchList = Streamable.of(mstTaskSearchRepository.findAll().collectList().block()).toList();
                MstTask testMstTaskSearch = mstTaskSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertMstTaskAllPropertiesEquals(testMstTaskSearch, updatedMstTask);
                assertMstTaskUpdatableFieldsEquals(testMstTaskSearch, updatedMstTask);
            });
    }

    @Test
    void putNonExistingMstTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstTaskSearchRepository.findAll().collectList().block());
        mstTask.setId(longCount.incrementAndGet());

        // Create the MstTask
        MstTaskDTO mstTaskDTO = mstTaskMapper.toDto(mstTask);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstTaskDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstTaskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstTaskSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchMstTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstTaskSearchRepository.findAll().collectList().block());
        mstTask.setId(longCount.incrementAndGet());

        // Create the MstTask
        MstTaskDTO mstTaskDTO = mstTaskMapper.toDto(mstTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstTaskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstTaskSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamMstTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstTaskSearchRepository.findAll().collectList().block());
        mstTask.setId(longCount.incrementAndGet());

        // Create the MstTask
        MstTaskDTO mstTaskDTO = mstTaskMapper.toDto(mstTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstTaskDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstTaskSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateMstTaskWithPatch() throws Exception {
        // Initialize the database
        insertedMstTask = mstTaskRepository.save(mstTask).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstTask using partial update
        MstTask partialUpdatedMstTask = new MstTask();
        partialUpdatedMstTask.setId(mstTask.getId());

        partialUpdatedMstTask.title(UPDATED_TITLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstTask.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstTask))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstTask in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstTaskUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedMstTask, mstTask), getPersistedMstTask(mstTask));
    }

    @Test
    void fullUpdateMstTaskWithPatch() throws Exception {
        // Initialize the database
        insertedMstTask = mstTaskRepository.save(mstTask).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstTask using partial update
        MstTask partialUpdatedMstTask = new MstTask();
        partialUpdatedMstTask.setId(mstTask.getId());

        partialUpdatedMstTask.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstTask.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstTask))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstTask in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstTaskUpdatableFieldsEquals(partialUpdatedMstTask, getPersistedMstTask(partialUpdatedMstTask));
    }

    @Test
    void patchNonExistingMstTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstTaskSearchRepository.findAll().collectList().block());
        mstTask.setId(longCount.incrementAndGet());

        // Create the MstTask
        MstTaskDTO mstTaskDTO = mstTaskMapper.toDto(mstTask);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, mstTaskDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstTaskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstTaskSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchMstTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstTaskSearchRepository.findAll().collectList().block());
        mstTask.setId(longCount.incrementAndGet());

        // Create the MstTask
        MstTaskDTO mstTaskDTO = mstTaskMapper.toDto(mstTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstTaskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstTaskSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamMstTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstTaskSearchRepository.findAll().collectList().block());
        mstTask.setId(longCount.incrementAndGet());

        // Create the MstTask
        MstTaskDTO mstTaskDTO = mstTaskMapper.toDto(mstTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstTaskDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstTaskSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteMstTask() {
        // Initialize the database
        insertedMstTask = mstTaskRepository.save(mstTask).block();
        mstTaskRepository.save(mstTask).block();
        mstTaskSearchRepository.save(mstTask).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstTaskSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the mstTask
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, mstTask.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstTaskSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchMstTask() {
        // Initialize the database
        insertedMstTask = mstTaskRepository.save(mstTask).block();
        mstTaskSearchRepository.save(mstTask).block();

        // Search the mstTask
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + mstTask.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(mstTask.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    protected long getRepositoryCount() {
        return mstTaskRepository.count().block();
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

    protected MstTask getPersistedMstTask(MstTask mstTask) {
        return mstTaskRepository.findById(mstTask.getId()).block();
    }

    protected void assertPersistedMstTaskToMatchAllProperties(MstTask expectedMstTask) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstTaskAllPropertiesEquals(expectedMstTask, getPersistedMstTask(expectedMstTask));
        assertMstTaskUpdatableFieldsEquals(expectedMstTask, getPersistedMstTask(expectedMstTask));
    }

    protected void assertPersistedMstTaskToMatchUpdatableProperties(MstTask expectedMstTask) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstTaskAllUpdatablePropertiesEquals(expectedMstTask, getPersistedMstTask(expectedMstTask));
        assertMstTaskUpdatableFieldsEquals(expectedMstTask, getPersistedMstTask(expectedMstTask));
    }
}
