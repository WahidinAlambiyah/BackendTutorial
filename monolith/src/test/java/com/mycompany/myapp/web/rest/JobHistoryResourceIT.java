package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.JobHistoryAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.JobHistory;
import com.mycompany.myapp.domain.MstDepartment;
import com.mycompany.myapp.domain.MstEmployee;
import com.mycompany.myapp.domain.MstJob;
import com.mycompany.myapp.domain.enumeration.Language;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.JobHistoryRepository;
import com.mycompany.myapp.repository.MstDepartmentRepository;
import com.mycompany.myapp.repository.MstEmployeeRepository;
import com.mycompany.myapp.repository.MstJobRepository;
import com.mycompany.myapp.repository.search.JobHistorySearchRepository;
import com.mycompany.myapp.service.dto.JobHistoryDTO;
import com.mycompany.myapp.service.mapper.JobHistoryMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link JobHistoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class JobHistoryResourceIT {

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Language DEFAULT_LANGUAGE = Language.FRENCH;
    private static final Language UPDATED_LANGUAGE = Language.ENGLISH;

    private static final String ENTITY_API_URL = "/api/job-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/job-histories/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private JobHistoryRepository jobHistoryRepository;

    @Autowired
    private JobHistoryMapper jobHistoryMapper;

    @Autowired
    private JobHistorySearchRepository jobHistorySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private JobHistory jobHistory;

    private JobHistory insertedJobHistory;

    @Autowired
    private MstJobRepository mstJobRepository;

    @Autowired
    private MstDepartmentRepository mstDepartmentRepository;

    @Autowired
    private MstEmployeeRepository mstEmployeeRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JobHistory createEntity(EntityManager em) {
        JobHistory jobHistory = new JobHistory().startDate(DEFAULT_START_DATE).endDate(DEFAULT_END_DATE).language(DEFAULT_LANGUAGE);
        return jobHistory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JobHistory createUpdatedEntity(EntityManager em) {
        JobHistory jobHistory = new JobHistory().startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE).language(UPDATED_LANGUAGE);
        return jobHistory;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(JobHistory.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        jobHistory = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedJobHistory != null) {
            jobHistoryRepository.delete(insertedJobHistory).block();
            jobHistorySearchRepository.delete(insertedJobHistory).block();
            insertedJobHistory = null;
        }
        deleteEntities(em);
    }

    @Test
    void createJobHistory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobHistorySearchRepository.findAll().collectList().block());
        // Create the JobHistory
        JobHistoryDTO jobHistoryDTO = jobHistoryMapper.toDto(jobHistory);
        var returnedJobHistoryDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(jobHistoryDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(JobHistoryDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the JobHistory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedJobHistory = jobHistoryMapper.toEntity(returnedJobHistoryDTO);
        assertJobHistoryUpdatableFieldsEquals(returnedJobHistory, getPersistedJobHistory(returnedJobHistory));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobHistorySearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedJobHistory = returnedJobHistory;
    }

    @Test
    void createJobHistoryWithExistingId() throws Exception {
        // Create the JobHistory with an existing ID
        jobHistory.setId(1L);
        JobHistoryDTO jobHistoryDTO = jobHistoryMapper.toDto(jobHistory);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobHistorySearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(jobHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the JobHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllJobHistories() {
        // Initialize the database
        insertedJobHistory = jobHistoryRepository.save(jobHistory).block();

        // Get all the jobHistoryList
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
            .value(hasItem(jobHistory.getId().intValue()))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))
            .jsonPath("$.[*].endDate")
            .value(hasItem(DEFAULT_END_DATE.toString()))
            .jsonPath("$.[*].language")
            .value(hasItem(DEFAULT_LANGUAGE.toString()));
    }

    @Test
    void getJobHistory() {
        // Initialize the database
        insertedJobHistory = jobHistoryRepository.save(jobHistory).block();

        // Get the jobHistory
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, jobHistory.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(jobHistory.getId().intValue()))
            .jsonPath("$.startDate")
            .value(is(DEFAULT_START_DATE.toString()))
            .jsonPath("$.endDate")
            .value(is(DEFAULT_END_DATE.toString()))
            .jsonPath("$.language")
            .value(is(DEFAULT_LANGUAGE.toString()));
    }

    @Test
    void getJobHistoriesByIdFiltering() {
        // Initialize the database
        insertedJobHistory = jobHistoryRepository.save(jobHistory).block();

        Long id = jobHistory.getId();

        defaultJobHistoryFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultJobHistoryFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultJobHistoryFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllJobHistoriesByStartDateIsEqualToSomething() {
        // Initialize the database
        insertedJobHistory = jobHistoryRepository.save(jobHistory).block();

        // Get all the jobHistoryList where startDate equals to
        defaultJobHistoryFiltering("startDate.equals=" + DEFAULT_START_DATE, "startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    void getAllJobHistoriesByStartDateIsInShouldWork() {
        // Initialize the database
        insertedJobHistory = jobHistoryRepository.save(jobHistory).block();

        // Get all the jobHistoryList where startDate in
        defaultJobHistoryFiltering("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE, "startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    void getAllJobHistoriesByStartDateIsNullOrNotNull() {
        // Initialize the database
        insertedJobHistory = jobHistoryRepository.save(jobHistory).block();

        // Get all the jobHistoryList where startDate is not null
        defaultJobHistoryFiltering("startDate.specified=true", "startDate.specified=false");
    }

    @Test
    void getAllJobHistoriesByEndDateIsEqualToSomething() {
        // Initialize the database
        insertedJobHistory = jobHistoryRepository.save(jobHistory).block();

        // Get all the jobHistoryList where endDate equals to
        defaultJobHistoryFiltering("endDate.equals=" + DEFAULT_END_DATE, "endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    void getAllJobHistoriesByEndDateIsInShouldWork() {
        // Initialize the database
        insertedJobHistory = jobHistoryRepository.save(jobHistory).block();

        // Get all the jobHistoryList where endDate in
        defaultJobHistoryFiltering("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE, "endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    void getAllJobHistoriesByEndDateIsNullOrNotNull() {
        // Initialize the database
        insertedJobHistory = jobHistoryRepository.save(jobHistory).block();

        // Get all the jobHistoryList where endDate is not null
        defaultJobHistoryFiltering("endDate.specified=true", "endDate.specified=false");
    }

    @Test
    void getAllJobHistoriesByLanguageIsEqualToSomething() {
        // Initialize the database
        insertedJobHistory = jobHistoryRepository.save(jobHistory).block();

        // Get all the jobHistoryList where language equals to
        defaultJobHistoryFiltering("language.equals=" + DEFAULT_LANGUAGE, "language.equals=" + UPDATED_LANGUAGE);
    }

    @Test
    void getAllJobHistoriesByLanguageIsInShouldWork() {
        // Initialize the database
        insertedJobHistory = jobHistoryRepository.save(jobHistory).block();

        // Get all the jobHistoryList where language in
        defaultJobHistoryFiltering("language.in=" + DEFAULT_LANGUAGE + "," + UPDATED_LANGUAGE, "language.in=" + UPDATED_LANGUAGE);
    }

    @Test
    void getAllJobHistoriesByLanguageIsNullOrNotNull() {
        // Initialize the database
        insertedJobHistory = jobHistoryRepository.save(jobHistory).block();

        // Get all the jobHistoryList where language is not null
        defaultJobHistoryFiltering("language.specified=true", "language.specified=false");
    }

    @Test
    void getAllJobHistoriesByJobIsEqualToSomething() {
        MstJob job = MstJobResourceIT.createEntity(em);
        mstJobRepository.save(job).block();
        Long jobId = job.getId();
        jobHistory.setJobId(jobId);
        insertedJobHistory = jobHistoryRepository.save(jobHistory).block();
        // Get all the jobHistoryList where job equals to jobId
        defaultJobHistoryShouldBeFound("jobId.equals=" + jobId);

        // Get all the jobHistoryList where job equals to (jobId + 1)
        defaultJobHistoryShouldNotBeFound("jobId.equals=" + (jobId + 1));
    }

    @Test
    void getAllJobHistoriesByDepartmentIsEqualToSomething() {
        MstDepartment department = MstDepartmentResourceIT.createEntity(em);
        mstDepartmentRepository.save(department).block();
        Long departmentId = department.getId();
        jobHistory.setDepartmentId(departmentId);
        insertedJobHistory = jobHistoryRepository.save(jobHistory).block();
        // Get all the jobHistoryList where department equals to departmentId
        defaultJobHistoryShouldBeFound("departmentId.equals=" + departmentId);

        // Get all the jobHistoryList where department equals to (departmentId + 1)
        defaultJobHistoryShouldNotBeFound("departmentId.equals=" + (departmentId + 1));
    }

    @Test
    void getAllJobHistoriesByEmployeeIsEqualToSomething() {
        MstEmployee employee = MstEmployeeResourceIT.createEntity(em);
        mstEmployeeRepository.save(employee).block();
        Long employeeId = employee.getId();
        jobHistory.setEmployeeId(employeeId);
        insertedJobHistory = jobHistoryRepository.save(jobHistory).block();
        // Get all the jobHistoryList where employee equals to employeeId
        defaultJobHistoryShouldBeFound("employeeId.equals=" + employeeId);

        // Get all the jobHistoryList where employee equals to (employeeId + 1)
        defaultJobHistoryShouldNotBeFound("employeeId.equals=" + (employeeId + 1));
    }

    private void defaultJobHistoryFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultJobHistoryShouldBeFound(shouldBeFound);
        defaultJobHistoryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultJobHistoryShouldBeFound(String filter) {
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
            .value(hasItem(jobHistory.getId().intValue()))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))
            .jsonPath("$.[*].endDate")
            .value(hasItem(DEFAULT_END_DATE.toString()))
            .jsonPath("$.[*].language")
            .value(hasItem(DEFAULT_LANGUAGE.toString()));

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
    private void defaultJobHistoryShouldNotBeFound(String filter) {
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
    void getNonExistingJobHistory() {
        // Get the jobHistory
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingJobHistory() throws Exception {
        // Initialize the database
        insertedJobHistory = jobHistoryRepository.save(jobHistory).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        jobHistorySearchRepository.save(jobHistory).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobHistorySearchRepository.findAll().collectList().block());

        // Update the jobHistory
        JobHistory updatedJobHistory = jobHistoryRepository.findById(jobHistory.getId()).block();
        updatedJobHistory.startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE).language(UPDATED_LANGUAGE);
        JobHistoryDTO jobHistoryDTO = jobHistoryMapper.toDto(updatedJobHistory);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, jobHistoryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(jobHistoryDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the JobHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedJobHistoryToMatchAllProperties(updatedJobHistory);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobHistorySearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<JobHistory> jobHistorySearchList = Streamable.of(jobHistorySearchRepository.findAll().collectList().block()).toList();
                JobHistory testJobHistorySearch = jobHistorySearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertJobHistoryAllPropertiesEquals(testJobHistorySearch, updatedJobHistory);
                assertJobHistoryUpdatableFieldsEquals(testJobHistorySearch, updatedJobHistory);
            });
    }

    @Test
    void putNonExistingJobHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobHistorySearchRepository.findAll().collectList().block());
        jobHistory.setId(longCount.incrementAndGet());

        // Create the JobHistory
        JobHistoryDTO jobHistoryDTO = jobHistoryMapper.toDto(jobHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, jobHistoryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(jobHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the JobHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchJobHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobHistorySearchRepository.findAll().collectList().block());
        jobHistory.setId(longCount.incrementAndGet());

        // Create the JobHistory
        JobHistoryDTO jobHistoryDTO = jobHistoryMapper.toDto(jobHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(jobHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the JobHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamJobHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobHistorySearchRepository.findAll().collectList().block());
        jobHistory.setId(longCount.incrementAndGet());

        // Create the JobHistory
        JobHistoryDTO jobHistoryDTO = jobHistoryMapper.toDto(jobHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(jobHistoryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the JobHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateJobHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedJobHistory = jobHistoryRepository.save(jobHistory).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the jobHistory using partial update
        JobHistory partialUpdatedJobHistory = new JobHistory();
        partialUpdatedJobHistory.setId(jobHistory.getId());

        partialUpdatedJobHistory.endDate(UPDATED_END_DATE).language(UPDATED_LANGUAGE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedJobHistory.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedJobHistory))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the JobHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertJobHistoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedJobHistory, jobHistory),
            getPersistedJobHistory(jobHistory)
        );
    }

    @Test
    void fullUpdateJobHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedJobHistory = jobHistoryRepository.save(jobHistory).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the jobHistory using partial update
        JobHistory partialUpdatedJobHistory = new JobHistory();
        partialUpdatedJobHistory.setId(jobHistory.getId());

        partialUpdatedJobHistory.startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE).language(UPDATED_LANGUAGE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedJobHistory.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedJobHistory))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the JobHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertJobHistoryUpdatableFieldsEquals(partialUpdatedJobHistory, getPersistedJobHistory(partialUpdatedJobHistory));
    }

    @Test
    void patchNonExistingJobHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobHistorySearchRepository.findAll().collectList().block());
        jobHistory.setId(longCount.incrementAndGet());

        // Create the JobHistory
        JobHistoryDTO jobHistoryDTO = jobHistoryMapper.toDto(jobHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, jobHistoryDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(jobHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the JobHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchJobHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobHistorySearchRepository.findAll().collectList().block());
        jobHistory.setId(longCount.incrementAndGet());

        // Create the JobHistory
        JobHistoryDTO jobHistoryDTO = jobHistoryMapper.toDto(jobHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(jobHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the JobHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamJobHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobHistorySearchRepository.findAll().collectList().block());
        jobHistory.setId(longCount.incrementAndGet());

        // Create the JobHistory
        JobHistoryDTO jobHistoryDTO = jobHistoryMapper.toDto(jobHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(jobHistoryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the JobHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteJobHistory() {
        // Initialize the database
        insertedJobHistory = jobHistoryRepository.save(jobHistory).block();
        jobHistoryRepository.save(jobHistory).block();
        jobHistorySearchRepository.save(jobHistory).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the jobHistory
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, jobHistory.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchJobHistory() {
        // Initialize the database
        insertedJobHistory = jobHistoryRepository.save(jobHistory).block();
        jobHistorySearchRepository.save(jobHistory).block();

        // Search the jobHistory
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + jobHistory.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(jobHistory.getId().intValue()))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))
            .jsonPath("$.[*].endDate")
            .value(hasItem(DEFAULT_END_DATE.toString()))
            .jsonPath("$.[*].language")
            .value(hasItem(DEFAULT_LANGUAGE.toString()));
    }

    protected long getRepositoryCount() {
        return jobHistoryRepository.count().block();
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

    protected JobHistory getPersistedJobHistory(JobHistory jobHistory) {
        return jobHistoryRepository.findById(jobHistory.getId()).block();
    }

    protected void assertPersistedJobHistoryToMatchAllProperties(JobHistory expectedJobHistory) {
        // Test fails because reactive api returns an empty object instead of null
        // assertJobHistoryAllPropertiesEquals(expectedJobHistory, getPersistedJobHistory(expectedJobHistory));
        assertJobHistoryUpdatableFieldsEquals(expectedJobHistory, getPersistedJobHistory(expectedJobHistory));
    }

    protected void assertPersistedJobHistoryToMatchUpdatableProperties(JobHistory expectedJobHistory) {
        // Test fails because reactive api returns an empty object instead of null
        // assertJobHistoryAllUpdatablePropertiesEquals(expectedJobHistory, getPersistedJobHistory(expectedJobHistory));
        assertJobHistoryUpdatableFieldsEquals(expectedJobHistory, getPersistedJobHistory(expectedJobHistory));
    }
}
