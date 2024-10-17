package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.MstJobAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.MstEmployee;
import com.mycompany.myapp.domain.MstJob;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MstEmployeeRepository;
import com.mycompany.myapp.repository.MstJobRepository;
import com.mycompany.myapp.repository.search.MstJobSearchRepository;
import com.mycompany.myapp.service.MstJobService;
import com.mycompany.myapp.service.dto.MstJobDTO;
import com.mycompany.myapp.service.mapper.MstJobMapper;
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
 * Integration tests for the {@link MstJobResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MstJobResourceIT {

    private static final String DEFAULT_JOB_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_JOB_TITLE = "BBBBBBBBBB";

    private static final Long DEFAULT_MIN_SALARY = 1L;
    private static final Long UPDATED_MIN_SALARY = 2L;
    private static final Long SMALLER_MIN_SALARY = 1L - 1L;

    private static final Long DEFAULT_MAX_SALARY = 1L;
    private static final Long UPDATED_MAX_SALARY = 2L;
    private static final Long SMALLER_MAX_SALARY = 1L - 1L;

    private static final String ENTITY_API_URL = "/api/mst-jobs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/mst-jobs/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MstJobRepository mstJobRepository;

    @Mock
    private MstJobRepository mstJobRepositoryMock;

    @Autowired
    private MstJobMapper mstJobMapper;

    @Mock
    private MstJobService mstJobServiceMock;

    @Autowired
    private MstJobSearchRepository mstJobSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private MstJob mstJob;

    private MstJob insertedMstJob;

    @Autowired
    private MstEmployeeRepository mstEmployeeRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstJob createEntity(EntityManager em) {
        MstJob mstJob = new MstJob().jobTitle(DEFAULT_JOB_TITLE).minSalary(DEFAULT_MIN_SALARY).maxSalary(DEFAULT_MAX_SALARY);
        return mstJob;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstJob createUpdatedEntity(EntityManager em) {
        MstJob mstJob = new MstJob().jobTitle(UPDATED_JOB_TITLE).minSalary(UPDATED_MIN_SALARY).maxSalary(UPDATED_MAX_SALARY);
        return mstJob;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_mst_job__task").block();
            em.deleteAll(MstJob.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        mstJob = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedMstJob != null) {
            mstJobRepository.delete(insertedMstJob).block();
            mstJobSearchRepository.delete(insertedMstJob).block();
            insertedMstJob = null;
        }
        deleteEntities(em);
    }

    @Test
    void createMstJob() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstJobSearchRepository.findAll().collectList().block());
        // Create the MstJob
        MstJobDTO mstJobDTO = mstJobMapper.toDto(mstJob);
        var returnedMstJobDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstJobDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MstJobDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MstJob in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMstJob = mstJobMapper.toEntity(returnedMstJobDTO);
        assertMstJobUpdatableFieldsEquals(returnedMstJob, getPersistedMstJob(returnedMstJob));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstJobSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedMstJob = returnedMstJob;
    }

    @Test
    void createMstJobWithExistingId() throws Exception {
        // Create the MstJob with an existing ID
        mstJob.setId(1L);
        MstJobDTO mstJobDTO = mstJobMapper.toDto(mstJob);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstJobSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstJobDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstJob in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstJobSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllMstJobs() {
        // Initialize the database
        insertedMstJob = mstJobRepository.save(mstJob).block();

        // Get all the mstJobList
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
            .value(hasItem(mstJob.getId().intValue()))
            .jsonPath("$.[*].jobTitle")
            .value(hasItem(DEFAULT_JOB_TITLE))
            .jsonPath("$.[*].minSalary")
            .value(hasItem(DEFAULT_MIN_SALARY.intValue()))
            .jsonPath("$.[*].maxSalary")
            .value(hasItem(DEFAULT_MAX_SALARY.intValue()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMstJobsWithEagerRelationshipsIsEnabled() {
        when(mstJobServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(mstJobServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMstJobsWithEagerRelationshipsIsNotEnabled() {
        when(mstJobServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(mstJobRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getMstJob() {
        // Initialize the database
        insertedMstJob = mstJobRepository.save(mstJob).block();

        // Get the mstJob
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, mstJob.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(mstJob.getId().intValue()))
            .jsonPath("$.jobTitle")
            .value(is(DEFAULT_JOB_TITLE))
            .jsonPath("$.minSalary")
            .value(is(DEFAULT_MIN_SALARY.intValue()))
            .jsonPath("$.maxSalary")
            .value(is(DEFAULT_MAX_SALARY.intValue()));
    }

    @Test
    void getMstJobsByIdFiltering() {
        // Initialize the database
        insertedMstJob = mstJobRepository.save(mstJob).block();

        Long id = mstJob.getId();

        defaultMstJobFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMstJobFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMstJobFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllMstJobsByJobTitleIsEqualToSomething() {
        // Initialize the database
        insertedMstJob = mstJobRepository.save(mstJob).block();

        // Get all the mstJobList where jobTitle equals to
        defaultMstJobFiltering("jobTitle.equals=" + DEFAULT_JOB_TITLE, "jobTitle.equals=" + UPDATED_JOB_TITLE);
    }

    @Test
    void getAllMstJobsByJobTitleIsInShouldWork() {
        // Initialize the database
        insertedMstJob = mstJobRepository.save(mstJob).block();

        // Get all the mstJobList where jobTitle in
        defaultMstJobFiltering("jobTitle.in=" + DEFAULT_JOB_TITLE + "," + UPDATED_JOB_TITLE, "jobTitle.in=" + UPDATED_JOB_TITLE);
    }

    @Test
    void getAllMstJobsByJobTitleIsNullOrNotNull() {
        // Initialize the database
        insertedMstJob = mstJobRepository.save(mstJob).block();

        // Get all the mstJobList where jobTitle is not null
        defaultMstJobFiltering("jobTitle.specified=true", "jobTitle.specified=false");
    }

    @Test
    void getAllMstJobsByJobTitleContainsSomething() {
        // Initialize the database
        insertedMstJob = mstJobRepository.save(mstJob).block();

        // Get all the mstJobList where jobTitle contains
        defaultMstJobFiltering("jobTitle.contains=" + DEFAULT_JOB_TITLE, "jobTitle.contains=" + UPDATED_JOB_TITLE);
    }

    @Test
    void getAllMstJobsByJobTitleNotContainsSomething() {
        // Initialize the database
        insertedMstJob = mstJobRepository.save(mstJob).block();

        // Get all the mstJobList where jobTitle does not contain
        defaultMstJobFiltering("jobTitle.doesNotContain=" + UPDATED_JOB_TITLE, "jobTitle.doesNotContain=" + DEFAULT_JOB_TITLE);
    }

    @Test
    void getAllMstJobsByMinSalaryIsEqualToSomething() {
        // Initialize the database
        insertedMstJob = mstJobRepository.save(mstJob).block();

        // Get all the mstJobList where minSalary equals to
        defaultMstJobFiltering("minSalary.equals=" + DEFAULT_MIN_SALARY, "minSalary.equals=" + UPDATED_MIN_SALARY);
    }

    @Test
    void getAllMstJobsByMinSalaryIsInShouldWork() {
        // Initialize the database
        insertedMstJob = mstJobRepository.save(mstJob).block();

        // Get all the mstJobList where minSalary in
        defaultMstJobFiltering("minSalary.in=" + DEFAULT_MIN_SALARY + "," + UPDATED_MIN_SALARY, "minSalary.in=" + UPDATED_MIN_SALARY);
    }

    @Test
    void getAllMstJobsByMinSalaryIsNullOrNotNull() {
        // Initialize the database
        insertedMstJob = mstJobRepository.save(mstJob).block();

        // Get all the mstJobList where minSalary is not null
        defaultMstJobFiltering("minSalary.specified=true", "minSalary.specified=false");
    }

    @Test
    void getAllMstJobsByMinSalaryIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedMstJob = mstJobRepository.save(mstJob).block();

        // Get all the mstJobList where minSalary is greater than or equal to
        defaultMstJobFiltering("minSalary.greaterThanOrEqual=" + DEFAULT_MIN_SALARY, "minSalary.greaterThanOrEqual=" + UPDATED_MIN_SALARY);
    }

    @Test
    void getAllMstJobsByMinSalaryIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedMstJob = mstJobRepository.save(mstJob).block();

        // Get all the mstJobList where minSalary is less than or equal to
        defaultMstJobFiltering("minSalary.lessThanOrEqual=" + DEFAULT_MIN_SALARY, "minSalary.lessThanOrEqual=" + SMALLER_MIN_SALARY);
    }

    @Test
    void getAllMstJobsByMinSalaryIsLessThanSomething() {
        // Initialize the database
        insertedMstJob = mstJobRepository.save(mstJob).block();

        // Get all the mstJobList where minSalary is less than
        defaultMstJobFiltering("minSalary.lessThan=" + UPDATED_MIN_SALARY, "minSalary.lessThan=" + DEFAULT_MIN_SALARY);
    }

    @Test
    void getAllMstJobsByMinSalaryIsGreaterThanSomething() {
        // Initialize the database
        insertedMstJob = mstJobRepository.save(mstJob).block();

        // Get all the mstJobList where minSalary is greater than
        defaultMstJobFiltering("minSalary.greaterThan=" + SMALLER_MIN_SALARY, "minSalary.greaterThan=" + DEFAULT_MIN_SALARY);
    }

    @Test
    void getAllMstJobsByMaxSalaryIsEqualToSomething() {
        // Initialize the database
        insertedMstJob = mstJobRepository.save(mstJob).block();

        // Get all the mstJobList where maxSalary equals to
        defaultMstJobFiltering("maxSalary.equals=" + DEFAULT_MAX_SALARY, "maxSalary.equals=" + UPDATED_MAX_SALARY);
    }

    @Test
    void getAllMstJobsByMaxSalaryIsInShouldWork() {
        // Initialize the database
        insertedMstJob = mstJobRepository.save(mstJob).block();

        // Get all the mstJobList where maxSalary in
        defaultMstJobFiltering("maxSalary.in=" + DEFAULT_MAX_SALARY + "," + UPDATED_MAX_SALARY, "maxSalary.in=" + UPDATED_MAX_SALARY);
    }

    @Test
    void getAllMstJobsByMaxSalaryIsNullOrNotNull() {
        // Initialize the database
        insertedMstJob = mstJobRepository.save(mstJob).block();

        // Get all the mstJobList where maxSalary is not null
        defaultMstJobFiltering("maxSalary.specified=true", "maxSalary.specified=false");
    }

    @Test
    void getAllMstJobsByMaxSalaryIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedMstJob = mstJobRepository.save(mstJob).block();

        // Get all the mstJobList where maxSalary is greater than or equal to
        defaultMstJobFiltering("maxSalary.greaterThanOrEqual=" + DEFAULT_MAX_SALARY, "maxSalary.greaterThanOrEqual=" + UPDATED_MAX_SALARY);
    }

    @Test
    void getAllMstJobsByMaxSalaryIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedMstJob = mstJobRepository.save(mstJob).block();

        // Get all the mstJobList where maxSalary is less than or equal to
        defaultMstJobFiltering("maxSalary.lessThanOrEqual=" + DEFAULT_MAX_SALARY, "maxSalary.lessThanOrEqual=" + SMALLER_MAX_SALARY);
    }

    @Test
    void getAllMstJobsByMaxSalaryIsLessThanSomething() {
        // Initialize the database
        insertedMstJob = mstJobRepository.save(mstJob).block();

        // Get all the mstJobList where maxSalary is less than
        defaultMstJobFiltering("maxSalary.lessThan=" + UPDATED_MAX_SALARY, "maxSalary.lessThan=" + DEFAULT_MAX_SALARY);
    }

    @Test
    void getAllMstJobsByMaxSalaryIsGreaterThanSomething() {
        // Initialize the database
        insertedMstJob = mstJobRepository.save(mstJob).block();

        // Get all the mstJobList where maxSalary is greater than
        defaultMstJobFiltering("maxSalary.greaterThan=" + SMALLER_MAX_SALARY, "maxSalary.greaterThan=" + DEFAULT_MAX_SALARY);
    }

    @Test
    void getAllMstJobsByEmployeeIsEqualToSomething() {
        MstEmployee employee = MstEmployeeResourceIT.createEntity(em);
        mstEmployeeRepository.save(employee).block();
        Long employeeId = employee.getId();
        mstJob.setEmployeeId(employeeId);
        insertedMstJob = mstJobRepository.save(mstJob).block();
        // Get all the mstJobList where employee equals to employeeId
        defaultMstJobShouldBeFound("employeeId.equals=" + employeeId);

        // Get all the mstJobList where employee equals to (employeeId + 1)
        defaultMstJobShouldNotBeFound("employeeId.equals=" + (employeeId + 1));
    }

    private void defaultMstJobFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultMstJobShouldBeFound(shouldBeFound);
        defaultMstJobShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMstJobShouldBeFound(String filter) {
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
            .value(hasItem(mstJob.getId().intValue()))
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
    private void defaultMstJobShouldNotBeFound(String filter) {
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
    void getNonExistingMstJob() {
        // Get the mstJob
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMstJob() throws Exception {
        // Initialize the database
        insertedMstJob = mstJobRepository.save(mstJob).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        mstJobSearchRepository.save(mstJob).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstJobSearchRepository.findAll().collectList().block());

        // Update the mstJob
        MstJob updatedMstJob = mstJobRepository.findById(mstJob.getId()).block();
        updatedMstJob.jobTitle(UPDATED_JOB_TITLE).minSalary(UPDATED_MIN_SALARY).maxSalary(UPDATED_MAX_SALARY);
        MstJobDTO mstJobDTO = mstJobMapper.toDto(updatedMstJob);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstJobDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstJobDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMstJobToMatchAllProperties(updatedMstJob);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstJobSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<MstJob> mstJobSearchList = Streamable.of(mstJobSearchRepository.findAll().collectList().block()).toList();
                MstJob testMstJobSearch = mstJobSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertMstJobAllPropertiesEquals(testMstJobSearch, updatedMstJob);
                assertMstJobUpdatableFieldsEquals(testMstJobSearch, updatedMstJob);
            });
    }

    @Test
    void putNonExistingMstJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstJobSearchRepository.findAll().collectList().block());
        mstJob.setId(longCount.incrementAndGet());

        // Create the MstJob
        MstJobDTO mstJobDTO = mstJobMapper.toDto(mstJob);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstJobDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstJobDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstJobSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchMstJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstJobSearchRepository.findAll().collectList().block());
        mstJob.setId(longCount.incrementAndGet());

        // Create the MstJob
        MstJobDTO mstJobDTO = mstJobMapper.toDto(mstJob);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstJobDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstJobSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamMstJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstJobSearchRepository.findAll().collectList().block());
        mstJob.setId(longCount.incrementAndGet());

        // Create the MstJob
        MstJobDTO mstJobDTO = mstJobMapper.toDto(mstJob);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstJobDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstJobSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateMstJobWithPatch() throws Exception {
        // Initialize the database
        insertedMstJob = mstJobRepository.save(mstJob).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstJob using partial update
        MstJob partialUpdatedMstJob = new MstJob();
        partialUpdatedMstJob.setId(mstJob.getId());

        partialUpdatedMstJob.jobTitle(UPDATED_JOB_TITLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstJob.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstJob))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstJob in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstJobUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedMstJob, mstJob), getPersistedMstJob(mstJob));
    }

    @Test
    void fullUpdateMstJobWithPatch() throws Exception {
        // Initialize the database
        insertedMstJob = mstJobRepository.save(mstJob).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstJob using partial update
        MstJob partialUpdatedMstJob = new MstJob();
        partialUpdatedMstJob.setId(mstJob.getId());

        partialUpdatedMstJob.jobTitle(UPDATED_JOB_TITLE).minSalary(UPDATED_MIN_SALARY).maxSalary(UPDATED_MAX_SALARY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstJob.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstJob))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstJob in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstJobUpdatableFieldsEquals(partialUpdatedMstJob, getPersistedMstJob(partialUpdatedMstJob));
    }

    @Test
    void patchNonExistingMstJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstJobSearchRepository.findAll().collectList().block());
        mstJob.setId(longCount.incrementAndGet());

        // Create the MstJob
        MstJobDTO mstJobDTO = mstJobMapper.toDto(mstJob);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, mstJobDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstJobDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstJobSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchMstJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstJobSearchRepository.findAll().collectList().block());
        mstJob.setId(longCount.incrementAndGet());

        // Create the MstJob
        MstJobDTO mstJobDTO = mstJobMapper.toDto(mstJob);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstJobDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstJobSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamMstJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstJobSearchRepository.findAll().collectList().block());
        mstJob.setId(longCount.incrementAndGet());

        // Create the MstJob
        MstJobDTO mstJobDTO = mstJobMapper.toDto(mstJob);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstJobDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstJobSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteMstJob() {
        // Initialize the database
        insertedMstJob = mstJobRepository.save(mstJob).block();
        mstJobRepository.save(mstJob).block();
        mstJobSearchRepository.save(mstJob).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstJobSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the mstJob
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, mstJob.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstJobSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchMstJob() {
        // Initialize the database
        insertedMstJob = mstJobRepository.save(mstJob).block();
        mstJobSearchRepository.save(mstJob).block();

        // Search the mstJob
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + mstJob.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(mstJob.getId().intValue()))
            .jsonPath("$.[*].jobTitle")
            .value(hasItem(DEFAULT_JOB_TITLE))
            .jsonPath("$.[*].minSalary")
            .value(hasItem(DEFAULT_MIN_SALARY.intValue()))
            .jsonPath("$.[*].maxSalary")
            .value(hasItem(DEFAULT_MAX_SALARY.intValue()));
    }

    protected long getRepositoryCount() {
        return mstJobRepository.count().block();
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

    protected MstJob getPersistedMstJob(MstJob mstJob) {
        return mstJobRepository.findById(mstJob.getId()).block();
    }

    protected void assertPersistedMstJobToMatchAllProperties(MstJob expectedMstJob) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstJobAllPropertiesEquals(expectedMstJob, getPersistedMstJob(expectedMstJob));
        assertMstJobUpdatableFieldsEquals(expectedMstJob, getPersistedMstJob(expectedMstJob));
    }

    protected void assertPersistedMstJobToMatchUpdatableProperties(MstJob expectedMstJob) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstJobAllUpdatablePropertiesEquals(expectedMstJob, getPersistedMstJob(expectedMstJob));
        assertMstJobUpdatableFieldsEquals(expectedMstJob, getPersistedMstJob(expectedMstJob));
    }
}
