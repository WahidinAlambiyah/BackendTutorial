package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.JobAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.domain.Job;
import com.mycompany.myapp.repository.EmployeeRepository;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.JobRepository;
import com.mycompany.myapp.repository.search.JobSearchRepository;
import com.mycompany.myapp.service.JobService;
import com.mycompany.myapp.service.dto.JobDTO;
import com.mycompany.myapp.service.mapper.JobMapper;
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
 * Integration tests for the {@link JobResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class JobResourceIT {

    private static final String DEFAULT_JOB_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_JOB_TITLE = "BBBBBBBBBB";

    private static final Long DEFAULT_MIN_SALARY = 1L;
    private static final Long UPDATED_MIN_SALARY = 2L;
    private static final Long SMALLER_MIN_SALARY = 1L - 1L;

    private static final Long DEFAULT_MAX_SALARY = 1L;
    private static final Long UPDATED_MAX_SALARY = 2L;
    private static final Long SMALLER_MAX_SALARY = 1L - 1L;

    private static final String ENTITY_API_URL = "/api/jobs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/jobs/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private JobRepository jobRepository;

    @Mock
    private JobRepository jobRepositoryMock;

    @Autowired
    private JobMapper jobMapper;

    @Mock
    private JobService jobServiceMock;

    @Autowired
    private JobSearchRepository jobSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Job job;

    private Job insertedJob;

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Job createEntity(EntityManager em) {
        Job job = new Job().jobTitle(DEFAULT_JOB_TITLE).minSalary(DEFAULT_MIN_SALARY).maxSalary(DEFAULT_MAX_SALARY);
        return job;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Job createUpdatedEntity(EntityManager em) {
        Job job = new Job().jobTitle(UPDATED_JOB_TITLE).minSalary(UPDATED_MIN_SALARY).maxSalary(UPDATED_MAX_SALARY);
        return job;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_job__task").block();
            em.deleteAll(Job.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        job = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedJob != null) {
            jobRepository.delete(insertedJob).block();
            jobSearchRepository.delete(insertedJob).block();
            insertedJob = null;
        }
        deleteEntities(em);
    }

    @Test
    void createJob() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobSearchRepository.findAll().collectList().block());
        // Create the Job
        JobDTO jobDTO = jobMapper.toDto(job);
        var returnedJobDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(jobDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(JobDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Job in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedJob = jobMapper.toEntity(returnedJobDTO);
        assertJobUpdatableFieldsEquals(returnedJob, getPersistedJob(returnedJob));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedJob = returnedJob;
    }

    @Test
    void createJobWithExistingId() throws Exception {
        // Create the Job with an existing ID
        job.setId(1L);
        JobDTO jobDTO = jobMapper.toDto(job);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(jobDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Job in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllJobs() {
        // Initialize the database
        insertedJob = jobRepository.save(job).block();

        // Get all the jobList
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
            .value(hasItem(job.getId().intValue()))
            .jsonPath("$.[*].jobTitle")
            .value(hasItem(DEFAULT_JOB_TITLE))
            .jsonPath("$.[*].minSalary")
            .value(hasItem(DEFAULT_MIN_SALARY.intValue()))
            .jsonPath("$.[*].maxSalary")
            .value(hasItem(DEFAULT_MAX_SALARY.intValue()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllJobsWithEagerRelationshipsIsEnabled() {
        when(jobServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(jobServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllJobsWithEagerRelationshipsIsNotEnabled() {
        when(jobServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(jobRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getJob() {
        // Initialize the database
        insertedJob = jobRepository.save(job).block();

        // Get the job
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, job.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(job.getId().intValue()))
            .jsonPath("$.jobTitle")
            .value(is(DEFAULT_JOB_TITLE))
            .jsonPath("$.minSalary")
            .value(is(DEFAULT_MIN_SALARY.intValue()))
            .jsonPath("$.maxSalary")
            .value(is(DEFAULT_MAX_SALARY.intValue()));
    }

    @Test
    void getJobsByIdFiltering() {
        // Initialize the database
        insertedJob = jobRepository.save(job).block();

        Long id = job.getId();

        defaultJobFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultJobFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultJobFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllJobsByJobTitleIsEqualToSomething() {
        // Initialize the database
        insertedJob = jobRepository.save(job).block();

        // Get all the jobList where jobTitle equals to
        defaultJobFiltering("jobTitle.equals=" + DEFAULT_JOB_TITLE, "jobTitle.equals=" + UPDATED_JOB_TITLE);
    }

    @Test
    void getAllJobsByJobTitleIsInShouldWork() {
        // Initialize the database
        insertedJob = jobRepository.save(job).block();

        // Get all the jobList where jobTitle in
        defaultJobFiltering("jobTitle.in=" + DEFAULT_JOB_TITLE + "," + UPDATED_JOB_TITLE, "jobTitle.in=" + UPDATED_JOB_TITLE);
    }

    @Test
    void getAllJobsByJobTitleIsNullOrNotNull() {
        // Initialize the database
        insertedJob = jobRepository.save(job).block();

        // Get all the jobList where jobTitle is not null
        defaultJobFiltering("jobTitle.specified=true", "jobTitle.specified=false");
    }

    @Test
    void getAllJobsByJobTitleContainsSomething() {
        // Initialize the database
        insertedJob = jobRepository.save(job).block();

        // Get all the jobList where jobTitle contains
        defaultJobFiltering("jobTitle.contains=" + DEFAULT_JOB_TITLE, "jobTitle.contains=" + UPDATED_JOB_TITLE);
    }

    @Test
    void getAllJobsByJobTitleNotContainsSomething() {
        // Initialize the database
        insertedJob = jobRepository.save(job).block();

        // Get all the jobList where jobTitle does not contain
        defaultJobFiltering("jobTitle.doesNotContain=" + UPDATED_JOB_TITLE, "jobTitle.doesNotContain=" + DEFAULT_JOB_TITLE);
    }

    @Test
    void getAllJobsByMinSalaryIsEqualToSomething() {
        // Initialize the database
        insertedJob = jobRepository.save(job).block();

        // Get all the jobList where minSalary equals to
        defaultJobFiltering("minSalary.equals=" + DEFAULT_MIN_SALARY, "minSalary.equals=" + UPDATED_MIN_SALARY);
    }

    @Test
    void getAllJobsByMinSalaryIsInShouldWork() {
        // Initialize the database
        insertedJob = jobRepository.save(job).block();

        // Get all the jobList where minSalary in
        defaultJobFiltering("minSalary.in=" + DEFAULT_MIN_SALARY + "," + UPDATED_MIN_SALARY, "minSalary.in=" + UPDATED_MIN_SALARY);
    }

    @Test
    void getAllJobsByMinSalaryIsNullOrNotNull() {
        // Initialize the database
        insertedJob = jobRepository.save(job).block();

        // Get all the jobList where minSalary is not null
        defaultJobFiltering("minSalary.specified=true", "minSalary.specified=false");
    }

    @Test
    void getAllJobsByMinSalaryIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedJob = jobRepository.save(job).block();

        // Get all the jobList where minSalary is greater than or equal to
        defaultJobFiltering("minSalary.greaterThanOrEqual=" + DEFAULT_MIN_SALARY, "minSalary.greaterThanOrEqual=" + UPDATED_MIN_SALARY);
    }

    @Test
    void getAllJobsByMinSalaryIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedJob = jobRepository.save(job).block();

        // Get all the jobList where minSalary is less than or equal to
        defaultJobFiltering("minSalary.lessThanOrEqual=" + DEFAULT_MIN_SALARY, "minSalary.lessThanOrEqual=" + SMALLER_MIN_SALARY);
    }

    @Test
    void getAllJobsByMinSalaryIsLessThanSomething() {
        // Initialize the database
        insertedJob = jobRepository.save(job).block();

        // Get all the jobList where minSalary is less than
        defaultJobFiltering("minSalary.lessThan=" + UPDATED_MIN_SALARY, "minSalary.lessThan=" + DEFAULT_MIN_SALARY);
    }

    @Test
    void getAllJobsByMinSalaryIsGreaterThanSomething() {
        // Initialize the database
        insertedJob = jobRepository.save(job).block();

        // Get all the jobList where minSalary is greater than
        defaultJobFiltering("minSalary.greaterThan=" + SMALLER_MIN_SALARY, "minSalary.greaterThan=" + DEFAULT_MIN_SALARY);
    }

    @Test
    void getAllJobsByMaxSalaryIsEqualToSomething() {
        // Initialize the database
        insertedJob = jobRepository.save(job).block();

        // Get all the jobList where maxSalary equals to
        defaultJobFiltering("maxSalary.equals=" + DEFAULT_MAX_SALARY, "maxSalary.equals=" + UPDATED_MAX_SALARY);
    }

    @Test
    void getAllJobsByMaxSalaryIsInShouldWork() {
        // Initialize the database
        insertedJob = jobRepository.save(job).block();

        // Get all the jobList where maxSalary in
        defaultJobFiltering("maxSalary.in=" + DEFAULT_MAX_SALARY + "," + UPDATED_MAX_SALARY, "maxSalary.in=" + UPDATED_MAX_SALARY);
    }

    @Test
    void getAllJobsByMaxSalaryIsNullOrNotNull() {
        // Initialize the database
        insertedJob = jobRepository.save(job).block();

        // Get all the jobList where maxSalary is not null
        defaultJobFiltering("maxSalary.specified=true", "maxSalary.specified=false");
    }

    @Test
    void getAllJobsByMaxSalaryIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedJob = jobRepository.save(job).block();

        // Get all the jobList where maxSalary is greater than or equal to
        defaultJobFiltering("maxSalary.greaterThanOrEqual=" + DEFAULT_MAX_SALARY, "maxSalary.greaterThanOrEqual=" + UPDATED_MAX_SALARY);
    }

    @Test
    void getAllJobsByMaxSalaryIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedJob = jobRepository.save(job).block();

        // Get all the jobList where maxSalary is less than or equal to
        defaultJobFiltering("maxSalary.lessThanOrEqual=" + DEFAULT_MAX_SALARY, "maxSalary.lessThanOrEqual=" + SMALLER_MAX_SALARY);
    }

    @Test
    void getAllJobsByMaxSalaryIsLessThanSomething() {
        // Initialize the database
        insertedJob = jobRepository.save(job).block();

        // Get all the jobList where maxSalary is less than
        defaultJobFiltering("maxSalary.lessThan=" + UPDATED_MAX_SALARY, "maxSalary.lessThan=" + DEFAULT_MAX_SALARY);
    }

    @Test
    void getAllJobsByMaxSalaryIsGreaterThanSomething() {
        // Initialize the database
        insertedJob = jobRepository.save(job).block();

        // Get all the jobList where maxSalary is greater than
        defaultJobFiltering("maxSalary.greaterThan=" + SMALLER_MAX_SALARY, "maxSalary.greaterThan=" + DEFAULT_MAX_SALARY);
    }

    @Test
    void getAllJobsByEmployeeIsEqualToSomething() {
        Employee employee = EmployeeResourceIT.createEntity(em);
        employeeRepository.save(employee).block();
        Long employeeId = employee.getId();
        job.setEmployeeId(employeeId);
        insertedJob = jobRepository.save(job).block();
        // Get all the jobList where employee equals to employeeId
        defaultJobShouldBeFound("employeeId.equals=" + employeeId);

        // Get all the jobList where employee equals to (employeeId + 1)
        defaultJobShouldNotBeFound("employeeId.equals=" + (employeeId + 1));
    }

    private void defaultJobFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultJobShouldBeFound(shouldBeFound);
        defaultJobShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultJobShouldBeFound(String filter) {
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
            .value(hasItem(job.getId().intValue()))
            .jsonPath("$.[*].jobTitle")
            .value(hasItem(DEFAULT_JOB_TITLE))
            .jsonPath("$.[*].minSalary")
            .value(hasItem(DEFAULT_MIN_SALARY.intValue()))
            .jsonPath("$.[*].maxSalary")
            .value(hasItem(DEFAULT_MAX_SALARY.intValue()));

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
    private void defaultJobShouldNotBeFound(String filter) {
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
    void getNonExistingJob() {
        // Get the job
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingJob() throws Exception {
        // Initialize the database
        insertedJob = jobRepository.save(job).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        jobSearchRepository.save(job).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobSearchRepository.findAll().collectList().block());

        // Update the job
        Job updatedJob = jobRepository.findById(job.getId()).block();
        updatedJob.jobTitle(UPDATED_JOB_TITLE).minSalary(UPDATED_MIN_SALARY).maxSalary(UPDATED_MAX_SALARY);
        JobDTO jobDTO = jobMapper.toDto(updatedJob);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, jobDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(jobDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Job in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedJobToMatchAllProperties(updatedJob);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Job> jobSearchList = Streamable.of(jobSearchRepository.findAll().collectList().block()).toList();
                Job testJobSearch = jobSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertJobAllPropertiesEquals(testJobSearch, updatedJob);
                assertJobUpdatableFieldsEquals(testJobSearch, updatedJob);
            });
    }

    @Test
    void putNonExistingJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobSearchRepository.findAll().collectList().block());
        job.setId(longCount.incrementAndGet());

        // Create the Job
        JobDTO jobDTO = jobMapper.toDto(job);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, jobDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(jobDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Job in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobSearchRepository.findAll().collectList().block());
        job.setId(longCount.incrementAndGet());

        // Create the Job
        JobDTO jobDTO = jobMapper.toDto(job);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(jobDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Job in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobSearchRepository.findAll().collectList().block());
        job.setId(longCount.incrementAndGet());

        // Create the Job
        JobDTO jobDTO = jobMapper.toDto(job);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(jobDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Job in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateJobWithPatch() throws Exception {
        // Initialize the database
        insertedJob = jobRepository.save(job).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the job using partial update
        Job partialUpdatedJob = new Job();
        partialUpdatedJob.setId(job.getId());

        partialUpdatedJob.minSalary(UPDATED_MIN_SALARY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedJob.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedJob))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Job in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertJobUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedJob, job), getPersistedJob(job));
    }

    @Test
    void fullUpdateJobWithPatch() throws Exception {
        // Initialize the database
        insertedJob = jobRepository.save(job).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the job using partial update
        Job partialUpdatedJob = new Job();
        partialUpdatedJob.setId(job.getId());

        partialUpdatedJob.jobTitle(UPDATED_JOB_TITLE).minSalary(UPDATED_MIN_SALARY).maxSalary(UPDATED_MAX_SALARY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedJob.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedJob))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Job in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertJobUpdatableFieldsEquals(partialUpdatedJob, getPersistedJob(partialUpdatedJob));
    }

    @Test
    void patchNonExistingJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobSearchRepository.findAll().collectList().block());
        job.setId(longCount.incrementAndGet());

        // Create the Job
        JobDTO jobDTO = jobMapper.toDto(job);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, jobDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(jobDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Job in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobSearchRepository.findAll().collectList().block());
        job.setId(longCount.incrementAndGet());

        // Create the Job
        JobDTO jobDTO = jobMapper.toDto(job);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(jobDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Job in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobSearchRepository.findAll().collectList().block());
        job.setId(longCount.incrementAndGet());

        // Create the Job
        JobDTO jobDTO = jobMapper.toDto(job);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(jobDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Job in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteJob() {
        // Initialize the database
        insertedJob = jobRepository.save(job).block();
        jobRepository.save(job).block();
        jobSearchRepository.save(job).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(jobSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the job
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, job.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(jobSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchJob() {
        // Initialize the database
        insertedJob = jobRepository.save(job).block();
        jobSearchRepository.save(job).block();

        // Search the job
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + job.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(job.getId().intValue()))
            .jsonPath("$.[*].jobTitle")
            .value(hasItem(DEFAULT_JOB_TITLE))
            .jsonPath("$.[*].minSalary")
            .value(hasItem(DEFAULT_MIN_SALARY.intValue()))
            .jsonPath("$.[*].maxSalary")
            .value(hasItem(DEFAULT_MAX_SALARY.intValue()));
    }

    protected long getRepositoryCount() {
        return jobRepository.count().block();
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

    protected Job getPersistedJob(Job job) {
        return jobRepository.findById(job.getId()).block();
    }

    protected void assertPersistedJobToMatchAllProperties(Job expectedJob) {
        // Test fails because reactive api returns an empty object instead of null
        // assertJobAllPropertiesEquals(expectedJob, getPersistedJob(expectedJob));
        assertJobUpdatableFieldsEquals(expectedJob, getPersistedJob(expectedJob));
    }

    protected void assertPersistedJobToMatchUpdatableProperties(Job expectedJob) {
        // Test fails because reactive api returns an empty object instead of null
        // assertJobAllUpdatablePropertiesEquals(expectedJob, getPersistedJob(expectedJob));
        assertJobUpdatableFieldsEquals(expectedJob, getPersistedJob(expectedJob));
    }
}
