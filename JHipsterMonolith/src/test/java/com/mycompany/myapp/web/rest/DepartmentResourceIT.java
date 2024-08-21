package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.DepartmentAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Department;
import com.mycompany.myapp.domain.Location;
import com.mycompany.myapp.repository.DepartmentRepository;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.LocationRepository;
import com.mycompany.myapp.repository.search.DepartmentSearchRepository;
import com.mycompany.myapp.service.dto.DepartmentDTO;
import com.mycompany.myapp.service.mapper.DepartmentMapper;
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
 * Integration tests for the {@link DepartmentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DepartmentResourceIT {

    private static final String DEFAULT_DEPARTMENT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_DEPARTMENT_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/departments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/departments/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private DepartmentSearchRepository departmentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Department department;

    private Department insertedDepartment;

    @Autowired
    private LocationRepository locationRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Department createEntity(EntityManager em) {
        Department department = new Department().departmentName(DEFAULT_DEPARTMENT_NAME);
        return department;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Department createUpdatedEntity(EntityManager em) {
        Department department = new Department().departmentName(UPDATED_DEPARTMENT_NAME);
        return department;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Department.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        department = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedDepartment != null) {
            departmentRepository.delete(insertedDepartment).block();
            departmentSearchRepository.delete(insertedDepartment).block();
            insertedDepartment = null;
        }
        deleteEntities(em);
    }

    @Test
    void createDepartment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(departmentSearchRepository.findAll().collectList().block());
        // Create the Department
        DepartmentDTO departmentDTO = departmentMapper.toDto(department);
        var returnedDepartmentDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(departmentDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(DepartmentDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Department in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDepartment = departmentMapper.toEntity(returnedDepartmentDTO);
        assertDepartmentUpdatableFieldsEquals(returnedDepartment, getPersistedDepartment(returnedDepartment));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(departmentSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedDepartment = returnedDepartment;
    }

    @Test
    void createDepartmentWithExistingId() throws Exception {
        // Create the Department with an existing ID
        department.setId(1L);
        DepartmentDTO departmentDTO = departmentMapper.toDto(department);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(departmentSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(departmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Department in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(departmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkDepartmentNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(departmentSearchRepository.findAll().collectList().block());
        // set the field null
        department.setDepartmentName(null);

        // Create the Department, which fails.
        DepartmentDTO departmentDTO = departmentMapper.toDto(department);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(departmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(departmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllDepartments() {
        // Initialize the database
        insertedDepartment = departmentRepository.save(department).block();

        // Get all the departmentList
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
            .value(hasItem(department.getId().intValue()))
            .jsonPath("$.[*].departmentName")
            .value(hasItem(DEFAULT_DEPARTMENT_NAME));
    }

    @Test
    void getDepartment() {
        // Initialize the database
        insertedDepartment = departmentRepository.save(department).block();

        // Get the department
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, department.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(department.getId().intValue()))
            .jsonPath("$.departmentName")
            .value(is(DEFAULT_DEPARTMENT_NAME));
    }

    @Test
    void getDepartmentsByIdFiltering() {
        // Initialize the database
        insertedDepartment = departmentRepository.save(department).block();

        Long id = department.getId();

        defaultDepartmentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDepartmentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDepartmentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllDepartmentsByDepartmentNameIsEqualToSomething() {
        // Initialize the database
        insertedDepartment = departmentRepository.save(department).block();

        // Get all the departmentList where departmentName equals to
        defaultDepartmentFiltering("departmentName.equals=" + DEFAULT_DEPARTMENT_NAME, "departmentName.equals=" + UPDATED_DEPARTMENT_NAME);
    }

    @Test
    void getAllDepartmentsByDepartmentNameIsInShouldWork() {
        // Initialize the database
        insertedDepartment = departmentRepository.save(department).block();

        // Get all the departmentList where departmentName in
        defaultDepartmentFiltering(
            "departmentName.in=" + DEFAULT_DEPARTMENT_NAME + "," + UPDATED_DEPARTMENT_NAME,
            "departmentName.in=" + UPDATED_DEPARTMENT_NAME
        );
    }

    @Test
    void getAllDepartmentsByDepartmentNameIsNullOrNotNull() {
        // Initialize the database
        insertedDepartment = departmentRepository.save(department).block();

        // Get all the departmentList where departmentName is not null
        defaultDepartmentFiltering("departmentName.specified=true", "departmentName.specified=false");
    }

    @Test
    void getAllDepartmentsByDepartmentNameContainsSomething() {
        // Initialize the database
        insertedDepartment = departmentRepository.save(department).block();

        // Get all the departmentList where departmentName contains
        defaultDepartmentFiltering(
            "departmentName.contains=" + DEFAULT_DEPARTMENT_NAME,
            "departmentName.contains=" + UPDATED_DEPARTMENT_NAME
        );
    }

    @Test
    void getAllDepartmentsByDepartmentNameNotContainsSomething() {
        // Initialize the database
        insertedDepartment = departmentRepository.save(department).block();

        // Get all the departmentList where departmentName does not contain
        defaultDepartmentFiltering(
            "departmentName.doesNotContain=" + UPDATED_DEPARTMENT_NAME,
            "departmentName.doesNotContain=" + DEFAULT_DEPARTMENT_NAME
        );
    }

    @Test
    void getAllDepartmentsByLocationIsEqualToSomething() {
        Location location = LocationResourceIT.createEntity(em);
        locationRepository.save(location).block();
        Long locationId = location.getId();
        department.setLocationId(locationId);
        insertedDepartment = departmentRepository.save(department).block();
        // Get all the departmentList where location equals to locationId
        defaultDepartmentShouldBeFound("locationId.equals=" + locationId);

        // Get all the departmentList where location equals to (locationId + 1)
        defaultDepartmentShouldNotBeFound("locationId.equals=" + (locationId + 1));
    }

    private void defaultDepartmentFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultDepartmentShouldBeFound(shouldBeFound);
        defaultDepartmentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDepartmentShouldBeFound(String filter) {
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
            .value(hasItem(department.getId().intValue()))
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
    private void defaultDepartmentShouldNotBeFound(String filter) {
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
    void getNonExistingDepartment() {
        // Get the department
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingDepartment() throws Exception {
        // Initialize the database
        insertedDepartment = departmentRepository.save(department).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        departmentSearchRepository.save(department).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(departmentSearchRepository.findAll().collectList().block());

        // Update the department
        Department updatedDepartment = departmentRepository.findById(department.getId()).block();
        updatedDepartment.departmentName(UPDATED_DEPARTMENT_NAME);
        DepartmentDTO departmentDTO = departmentMapper.toDto(updatedDepartment);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, departmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(departmentDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Department in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDepartmentToMatchAllProperties(updatedDepartment);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(departmentSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Department> departmentSearchList = Streamable.of(departmentSearchRepository.findAll().collectList().block()).toList();
                Department testDepartmentSearch = departmentSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertDepartmentAllPropertiesEquals(testDepartmentSearch, updatedDepartment);
                assertDepartmentUpdatableFieldsEquals(testDepartmentSearch, updatedDepartment);
            });
    }

    @Test
    void putNonExistingDepartment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(departmentSearchRepository.findAll().collectList().block());
        department.setId(longCount.incrementAndGet());

        // Create the Department
        DepartmentDTO departmentDTO = departmentMapper.toDto(department);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, departmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(departmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Department in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(departmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchDepartment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(departmentSearchRepository.findAll().collectList().block());
        department.setId(longCount.incrementAndGet());

        // Create the Department
        DepartmentDTO departmentDTO = departmentMapper.toDto(department);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(departmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Department in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(departmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamDepartment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(departmentSearchRepository.findAll().collectList().block());
        department.setId(longCount.incrementAndGet());

        // Create the Department
        DepartmentDTO departmentDTO = departmentMapper.toDto(department);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(departmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Department in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(departmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateDepartmentWithPatch() throws Exception {
        // Initialize the database
        insertedDepartment = departmentRepository.save(department).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the department using partial update
        Department partialUpdatedDepartment = new Department();
        partialUpdatedDepartment.setId(department.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDepartment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDepartment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Department in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDepartmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDepartment, department),
            getPersistedDepartment(department)
        );
    }

    @Test
    void fullUpdateDepartmentWithPatch() throws Exception {
        // Initialize the database
        insertedDepartment = departmentRepository.save(department).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the department using partial update
        Department partialUpdatedDepartment = new Department();
        partialUpdatedDepartment.setId(department.getId());

        partialUpdatedDepartment.departmentName(UPDATED_DEPARTMENT_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDepartment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDepartment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Department in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDepartmentUpdatableFieldsEquals(partialUpdatedDepartment, getPersistedDepartment(partialUpdatedDepartment));
    }

    @Test
    void patchNonExistingDepartment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(departmentSearchRepository.findAll().collectList().block());
        department.setId(longCount.incrementAndGet());

        // Create the Department
        DepartmentDTO departmentDTO = departmentMapper.toDto(department);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, departmentDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(departmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Department in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(departmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchDepartment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(departmentSearchRepository.findAll().collectList().block());
        department.setId(longCount.incrementAndGet());

        // Create the Department
        DepartmentDTO departmentDTO = departmentMapper.toDto(department);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(departmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Department in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(departmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamDepartment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(departmentSearchRepository.findAll().collectList().block());
        department.setId(longCount.incrementAndGet());

        // Create the Department
        DepartmentDTO departmentDTO = departmentMapper.toDto(department);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(departmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Department in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(departmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteDepartment() {
        // Initialize the database
        insertedDepartment = departmentRepository.save(department).block();
        departmentRepository.save(department).block();
        departmentSearchRepository.save(department).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(departmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the department
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, department.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(departmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchDepartment() {
        // Initialize the database
        insertedDepartment = departmentRepository.save(department).block();
        departmentSearchRepository.save(department).block();

        // Search the department
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + department.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(department.getId().intValue()))
            .jsonPath("$.[*].departmentName")
            .value(hasItem(DEFAULT_DEPARTMENT_NAME));
    }

    protected long getRepositoryCount() {
        return departmentRepository.count().block();
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

    protected Department getPersistedDepartment(Department department) {
        return departmentRepository.findById(department.getId()).block();
    }

    protected void assertPersistedDepartmentToMatchAllProperties(Department expectedDepartment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDepartmentAllPropertiesEquals(expectedDepartment, getPersistedDepartment(expectedDepartment));
        assertDepartmentUpdatableFieldsEquals(expectedDepartment, getPersistedDepartment(expectedDepartment));
    }

    protected void assertPersistedDepartmentToMatchUpdatableProperties(Department expectedDepartment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDepartmentAllUpdatablePropertiesEquals(expectedDepartment, getPersistedDepartment(expectedDepartment));
        assertDepartmentUpdatableFieldsEquals(expectedDepartment, getPersistedDepartment(expectedDepartment));
    }
}
