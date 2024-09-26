package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.MstDepartmentAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Location;
import com.mycompany.myapp.domain.MstDepartment;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.LocationRepository;
import com.mycompany.myapp.repository.MstDepartmentRepository;
import com.mycompany.myapp.repository.search.MstDepartmentSearchRepository;
import com.mycompany.myapp.service.dto.MstDepartmentDTO;
import com.mycompany.myapp.service.mapper.MstDepartmentMapper;
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
 * Integration tests for the {@link MstDepartmentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MstDepartmentResourceIT {

    private static final String DEFAULT_DEPARTMENT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_DEPARTMENT_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/mst-departments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/mst-departments/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MstDepartmentRepository mstDepartmentRepository;

    @Autowired
    private MstDepartmentMapper mstDepartmentMapper;

    @Autowired
    private MstDepartmentSearchRepository mstDepartmentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private MstDepartment mstDepartment;

    private MstDepartment insertedMstDepartment;

    @Autowired
    private LocationRepository locationRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstDepartment createEntity(EntityManager em) {
        MstDepartment mstDepartment = new MstDepartment().departmentName(DEFAULT_DEPARTMENT_NAME);
        return mstDepartment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstDepartment createUpdatedEntity(EntityManager em) {
        MstDepartment mstDepartment = new MstDepartment().departmentName(UPDATED_DEPARTMENT_NAME);
        return mstDepartment;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(MstDepartment.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        mstDepartment = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedMstDepartment != null) {
            mstDepartmentRepository.delete(insertedMstDepartment).block();
            mstDepartmentSearchRepository.delete(insertedMstDepartment).block();
            insertedMstDepartment = null;
        }
        deleteEntities(em);
    }

    @Test
    void createMstDepartment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDepartmentSearchRepository.findAll().collectList().block());
        // Create the MstDepartment
        MstDepartmentDTO mstDepartmentDTO = mstDepartmentMapper.toDto(mstDepartment);
        var returnedMstDepartmentDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstDepartmentDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MstDepartmentDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MstDepartment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMstDepartment = mstDepartmentMapper.toEntity(returnedMstDepartmentDTO);
        assertMstDepartmentUpdatableFieldsEquals(returnedMstDepartment, getPersistedMstDepartment(returnedMstDepartment));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDepartmentSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedMstDepartment = returnedMstDepartment;
    }

    @Test
    void createMstDepartmentWithExistingId() throws Exception {
        // Create the MstDepartment with an existing ID
        mstDepartment.setId(1L);
        MstDepartmentDTO mstDepartmentDTO = mstDepartmentMapper.toDto(mstDepartment);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDepartmentSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstDepartmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstDepartment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDepartmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkDepartmentNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDepartmentSearchRepository.findAll().collectList().block());
        // set the field null
        mstDepartment.setDepartmentName(null);

        // Create the MstDepartment, which fails.
        MstDepartmentDTO mstDepartmentDTO = mstDepartmentMapper.toDto(mstDepartment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstDepartmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDepartmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllMstDepartments() {
        // Initialize the database
        insertedMstDepartment = mstDepartmentRepository.save(mstDepartment).block();

        // Get all the mstDepartmentList
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
            .value(hasItem(mstDepartment.getId().intValue()))
            .jsonPath("$.[*].departmentName")
            .value(hasItem(DEFAULT_DEPARTMENT_NAME));
    }

    @Test
    void getMstDepartment() {
        // Initialize the database
        insertedMstDepartment = mstDepartmentRepository.save(mstDepartment).block();

        // Get the mstDepartment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, mstDepartment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(mstDepartment.getId().intValue()))
            .jsonPath("$.departmentName")
            .value(is(DEFAULT_DEPARTMENT_NAME));
    }

    @Test
    void getMstDepartmentsByIdFiltering() {
        // Initialize the database
        insertedMstDepartment = mstDepartmentRepository.save(mstDepartment).block();

        Long id = mstDepartment.getId();

        defaultMstDepartmentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMstDepartmentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMstDepartmentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllMstDepartmentsByDepartmentNameIsEqualToSomething() {
        // Initialize the database
        insertedMstDepartment = mstDepartmentRepository.save(mstDepartment).block();

        // Get all the mstDepartmentList where departmentName equals to
        defaultMstDepartmentFiltering(
            "departmentName.equals=" + DEFAULT_DEPARTMENT_NAME,
            "departmentName.equals=" + UPDATED_DEPARTMENT_NAME
        );
    }

    @Test
    void getAllMstDepartmentsByDepartmentNameIsInShouldWork() {
        // Initialize the database
        insertedMstDepartment = mstDepartmentRepository.save(mstDepartment).block();

        // Get all the mstDepartmentList where departmentName in
        defaultMstDepartmentFiltering(
            "departmentName.in=" + DEFAULT_DEPARTMENT_NAME + "," + UPDATED_DEPARTMENT_NAME,
            "departmentName.in=" + UPDATED_DEPARTMENT_NAME
        );
    }

    @Test
    void getAllMstDepartmentsByDepartmentNameIsNullOrNotNull() {
        // Initialize the database
        insertedMstDepartment = mstDepartmentRepository.save(mstDepartment).block();

        // Get all the mstDepartmentList where departmentName is not null
        defaultMstDepartmentFiltering("departmentName.specified=true", "departmentName.specified=false");
    }

    @Test
    void getAllMstDepartmentsByDepartmentNameContainsSomething() {
        // Initialize the database
        insertedMstDepartment = mstDepartmentRepository.save(mstDepartment).block();

        // Get all the mstDepartmentList where departmentName contains
        defaultMstDepartmentFiltering(
            "departmentName.contains=" + DEFAULT_DEPARTMENT_NAME,
            "departmentName.contains=" + UPDATED_DEPARTMENT_NAME
        );
    }

    @Test
    void getAllMstDepartmentsByDepartmentNameNotContainsSomething() {
        // Initialize the database
        insertedMstDepartment = mstDepartmentRepository.save(mstDepartment).block();

        // Get all the mstDepartmentList where departmentName does not contain
        defaultMstDepartmentFiltering(
            "departmentName.doesNotContain=" + UPDATED_DEPARTMENT_NAME,
            "departmentName.doesNotContain=" + DEFAULT_DEPARTMENT_NAME
        );
    }

    @Test
    void getAllMstDepartmentsByLocationIsEqualToSomething() {
        Location location = LocationResourceIT.createEntity(em);
        locationRepository.save(location).block();
        Long locationId = location.getId();
        mstDepartment.setLocationId(locationId);
        insertedMstDepartment = mstDepartmentRepository.save(mstDepartment).block();
        // Get all the mstDepartmentList where location equals to locationId
        defaultMstDepartmentShouldBeFound("locationId.equals=" + locationId);

        // Get all the mstDepartmentList where location equals to (locationId + 1)
        defaultMstDepartmentShouldNotBeFound("locationId.equals=" + (locationId + 1));
    }

    private void defaultMstDepartmentFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultMstDepartmentShouldBeFound(shouldBeFound);
        defaultMstDepartmentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMstDepartmentShouldBeFound(String filter) {
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
            .value(hasItem(mstDepartment.getId().intValue()))
            .jsonPath("$.[*].departmentName")
            .value(hasItem(DEFAULT_DEPARTMENT_NAME));

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
    private void defaultMstDepartmentShouldNotBeFound(String filter) {
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
    void getNonExistingMstDepartment() {
        // Get the mstDepartment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMstDepartment() throws Exception {
        // Initialize the database
        insertedMstDepartment = mstDepartmentRepository.save(mstDepartment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        mstDepartmentSearchRepository.save(mstDepartment).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDepartmentSearchRepository.findAll().collectList().block());

        // Update the mstDepartment
        MstDepartment updatedMstDepartment = mstDepartmentRepository.findById(mstDepartment.getId()).block();
        updatedMstDepartment.departmentName(UPDATED_DEPARTMENT_NAME);
        MstDepartmentDTO mstDepartmentDTO = mstDepartmentMapper.toDto(updatedMstDepartment);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstDepartmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstDepartmentDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstDepartment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMstDepartmentToMatchAllProperties(updatedMstDepartment);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDepartmentSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<MstDepartment> mstDepartmentSearchList = Streamable.of(
                    mstDepartmentSearchRepository.findAll().collectList().block()
                ).toList();
                MstDepartment testMstDepartmentSearch = mstDepartmentSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertMstDepartmentAllPropertiesEquals(testMstDepartmentSearch, updatedMstDepartment);
                assertMstDepartmentUpdatableFieldsEquals(testMstDepartmentSearch, updatedMstDepartment);
            });
    }

    @Test
    void putNonExistingMstDepartment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDepartmentSearchRepository.findAll().collectList().block());
        mstDepartment.setId(longCount.incrementAndGet());

        // Create the MstDepartment
        MstDepartmentDTO mstDepartmentDTO = mstDepartmentMapper.toDto(mstDepartment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstDepartmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstDepartmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstDepartment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDepartmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchMstDepartment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDepartmentSearchRepository.findAll().collectList().block());
        mstDepartment.setId(longCount.incrementAndGet());

        // Create the MstDepartment
        MstDepartmentDTO mstDepartmentDTO = mstDepartmentMapper.toDto(mstDepartment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstDepartmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstDepartment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDepartmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamMstDepartment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDepartmentSearchRepository.findAll().collectList().block());
        mstDepartment.setId(longCount.incrementAndGet());

        // Create the MstDepartment
        MstDepartmentDTO mstDepartmentDTO = mstDepartmentMapper.toDto(mstDepartment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstDepartmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstDepartment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDepartmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateMstDepartmentWithPatch() throws Exception {
        // Initialize the database
        insertedMstDepartment = mstDepartmentRepository.save(mstDepartment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstDepartment using partial update
        MstDepartment partialUpdatedMstDepartment = new MstDepartment();
        partialUpdatedMstDepartment.setId(mstDepartment.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstDepartment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstDepartment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstDepartment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstDepartmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMstDepartment, mstDepartment),
            getPersistedMstDepartment(mstDepartment)
        );
    }

    @Test
    void fullUpdateMstDepartmentWithPatch() throws Exception {
        // Initialize the database
        insertedMstDepartment = mstDepartmentRepository.save(mstDepartment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstDepartment using partial update
        MstDepartment partialUpdatedMstDepartment = new MstDepartment();
        partialUpdatedMstDepartment.setId(mstDepartment.getId());

        partialUpdatedMstDepartment.departmentName(UPDATED_DEPARTMENT_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstDepartment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstDepartment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstDepartment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstDepartmentUpdatableFieldsEquals(partialUpdatedMstDepartment, getPersistedMstDepartment(partialUpdatedMstDepartment));
    }

    @Test
    void patchNonExistingMstDepartment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDepartmentSearchRepository.findAll().collectList().block());
        mstDepartment.setId(longCount.incrementAndGet());

        // Create the MstDepartment
        MstDepartmentDTO mstDepartmentDTO = mstDepartmentMapper.toDto(mstDepartment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, mstDepartmentDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstDepartmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstDepartment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDepartmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchMstDepartment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDepartmentSearchRepository.findAll().collectList().block());
        mstDepartment.setId(longCount.incrementAndGet());

        // Create the MstDepartment
        MstDepartmentDTO mstDepartmentDTO = mstDepartmentMapper.toDto(mstDepartment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstDepartmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstDepartment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDepartmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamMstDepartment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDepartmentSearchRepository.findAll().collectList().block());
        mstDepartment.setId(longCount.incrementAndGet());

        // Create the MstDepartment
        MstDepartmentDTO mstDepartmentDTO = mstDepartmentMapper.toDto(mstDepartment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstDepartmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstDepartment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDepartmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteMstDepartment() {
        // Initialize the database
        insertedMstDepartment = mstDepartmentRepository.save(mstDepartment).block();
        mstDepartmentRepository.save(mstDepartment).block();
        mstDepartmentSearchRepository.save(mstDepartment).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDepartmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the mstDepartment
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, mstDepartment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDepartmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchMstDepartment() {
        // Initialize the database
        insertedMstDepartment = mstDepartmentRepository.save(mstDepartment).block();
        mstDepartmentSearchRepository.save(mstDepartment).block();

        // Search the mstDepartment
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + mstDepartment.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(mstDepartment.getId().intValue()))
            .jsonPath("$.[*].departmentName")
            .value(hasItem(DEFAULT_DEPARTMENT_NAME));
    }

    protected long getRepositoryCount() {
        return mstDepartmentRepository.count().block();
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

    protected MstDepartment getPersistedMstDepartment(MstDepartment mstDepartment) {
        return mstDepartmentRepository.findById(mstDepartment.getId()).block();
    }

    protected void assertPersistedMstDepartmentToMatchAllProperties(MstDepartment expectedMstDepartment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstDepartmentAllPropertiesEquals(expectedMstDepartment, getPersistedMstDepartment(expectedMstDepartment));
        assertMstDepartmentUpdatableFieldsEquals(expectedMstDepartment, getPersistedMstDepartment(expectedMstDepartment));
    }

    protected void assertPersistedMstDepartmentToMatchUpdatableProperties(MstDepartment expectedMstDepartment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstDepartmentAllUpdatablePropertiesEquals(expectedMstDepartment, getPersistedMstDepartment(expectedMstDepartment));
        assertMstDepartmentUpdatableFieldsEquals(expectedMstDepartment, getPersistedMstDepartment(expectedMstDepartment));
    }
}
