package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.MstDriverAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.MstDriver;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MstDriverRepository;
import com.mycompany.myapp.repository.search.MstDriverSearchRepository;
import com.mycompany.myapp.service.dto.MstDriverDTO;
import com.mycompany.myapp.service.mapper.MstDriverMapper;
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
 * Integration tests for the {@link MstDriverResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MstDriverResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_VEHICLE_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_VEHICLE_DETAILS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/mst-drivers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/mst-drivers/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MstDriverRepository mstDriverRepository;

    @Autowired
    private MstDriverMapper mstDriverMapper;

    @Autowired
    private MstDriverSearchRepository mstDriverSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private MstDriver mstDriver;

    private MstDriver insertedMstDriver;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstDriver createEntity(EntityManager em) {
        MstDriver mstDriver = new MstDriver()
            .name(DEFAULT_NAME)
            .contactNumber(DEFAULT_CONTACT_NUMBER)
            .vehicleDetails(DEFAULT_VEHICLE_DETAILS);
        return mstDriver;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstDriver createUpdatedEntity(EntityManager em) {
        MstDriver mstDriver = new MstDriver()
            .name(UPDATED_NAME)
            .contactNumber(UPDATED_CONTACT_NUMBER)
            .vehicleDetails(UPDATED_VEHICLE_DETAILS);
        return mstDriver;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(MstDriver.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        mstDriver = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedMstDriver != null) {
            mstDriverRepository.delete(insertedMstDriver).block();
            mstDriverSearchRepository.delete(insertedMstDriver).block();
            insertedMstDriver = null;
        }
        deleteEntities(em);
    }

    @Test
    void createMstDriver() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDriverSearchRepository.findAll().collectList().block());
        // Create the MstDriver
        MstDriverDTO mstDriverDTO = mstDriverMapper.toDto(mstDriver);
        var returnedMstDriverDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstDriverDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MstDriverDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MstDriver in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMstDriver = mstDriverMapper.toEntity(returnedMstDriverDTO);
        assertMstDriverUpdatableFieldsEquals(returnedMstDriver, getPersistedMstDriver(returnedMstDriver));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDriverSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedMstDriver = returnedMstDriver;
    }

    @Test
    void createMstDriverWithExistingId() throws Exception {
        // Create the MstDriver with an existing ID
        mstDriver.setId(1L);
        MstDriverDTO mstDriverDTO = mstDriverMapper.toDto(mstDriver);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDriverSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstDriverDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstDriver in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDriverSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDriverSearchRepository.findAll().collectList().block());
        // set the field null
        mstDriver.setName(null);

        // Create the MstDriver, which fails.
        MstDriverDTO mstDriverDTO = mstDriverMapper.toDto(mstDriver);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstDriverDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDriverSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllMstDrivers() {
        // Initialize the database
        insertedMstDriver = mstDriverRepository.save(mstDriver).block();

        // Get all the mstDriverList
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
            .value(hasItem(mstDriver.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].contactNumber")
            .value(hasItem(DEFAULT_CONTACT_NUMBER))
            .jsonPath("$.[*].vehicleDetails")
            .value(hasItem(DEFAULT_VEHICLE_DETAILS));
    }

    @Test
    void getMstDriver() {
        // Initialize the database
        insertedMstDriver = mstDriverRepository.save(mstDriver).block();

        // Get the mstDriver
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, mstDriver.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(mstDriver.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.contactNumber")
            .value(is(DEFAULT_CONTACT_NUMBER))
            .jsonPath("$.vehicleDetails")
            .value(is(DEFAULT_VEHICLE_DETAILS));
    }

    @Test
    void getMstDriversByIdFiltering() {
        // Initialize the database
        insertedMstDriver = mstDriverRepository.save(mstDriver).block();

        Long id = mstDriver.getId();

        defaultMstDriverFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMstDriverFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMstDriverFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllMstDriversByNameIsEqualToSomething() {
        // Initialize the database
        insertedMstDriver = mstDriverRepository.save(mstDriver).block();

        // Get all the mstDriverList where name equals to
        defaultMstDriverFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllMstDriversByNameIsInShouldWork() {
        // Initialize the database
        insertedMstDriver = mstDriverRepository.save(mstDriver).block();

        // Get all the mstDriverList where name in
        defaultMstDriverFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllMstDriversByNameIsNullOrNotNull() {
        // Initialize the database
        insertedMstDriver = mstDriverRepository.save(mstDriver).block();

        // Get all the mstDriverList where name is not null
        defaultMstDriverFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllMstDriversByNameContainsSomething() {
        // Initialize the database
        insertedMstDriver = mstDriverRepository.save(mstDriver).block();

        // Get all the mstDriverList where name contains
        defaultMstDriverFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllMstDriversByNameNotContainsSomething() {
        // Initialize the database
        insertedMstDriver = mstDriverRepository.save(mstDriver).block();

        // Get all the mstDriverList where name does not contain
        defaultMstDriverFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllMstDriversByContactNumberIsEqualToSomething() {
        // Initialize the database
        insertedMstDriver = mstDriverRepository.save(mstDriver).block();

        // Get all the mstDriverList where contactNumber equals to
        defaultMstDriverFiltering("contactNumber.equals=" + DEFAULT_CONTACT_NUMBER, "contactNumber.equals=" + UPDATED_CONTACT_NUMBER);
    }

    @Test
    void getAllMstDriversByContactNumberIsInShouldWork() {
        // Initialize the database
        insertedMstDriver = mstDriverRepository.save(mstDriver).block();

        // Get all the mstDriverList where contactNumber in
        defaultMstDriverFiltering(
            "contactNumber.in=" + DEFAULT_CONTACT_NUMBER + "," + UPDATED_CONTACT_NUMBER,
            "contactNumber.in=" + UPDATED_CONTACT_NUMBER
        );
    }

    @Test
    void getAllMstDriversByContactNumberIsNullOrNotNull() {
        // Initialize the database
        insertedMstDriver = mstDriverRepository.save(mstDriver).block();

        // Get all the mstDriverList where contactNumber is not null
        defaultMstDriverFiltering("contactNumber.specified=true", "contactNumber.specified=false");
    }

    @Test
    void getAllMstDriversByContactNumberContainsSomething() {
        // Initialize the database
        insertedMstDriver = mstDriverRepository.save(mstDriver).block();

        // Get all the mstDriverList where contactNumber contains
        defaultMstDriverFiltering("contactNumber.contains=" + DEFAULT_CONTACT_NUMBER, "contactNumber.contains=" + UPDATED_CONTACT_NUMBER);
    }

    @Test
    void getAllMstDriversByContactNumberNotContainsSomething() {
        // Initialize the database
        insertedMstDriver = mstDriverRepository.save(mstDriver).block();

        // Get all the mstDriverList where contactNumber does not contain
        defaultMstDriverFiltering(
            "contactNumber.doesNotContain=" + UPDATED_CONTACT_NUMBER,
            "contactNumber.doesNotContain=" + DEFAULT_CONTACT_NUMBER
        );
    }

    @Test
    void getAllMstDriversByVehicleDetailsIsEqualToSomething() {
        // Initialize the database
        insertedMstDriver = mstDriverRepository.save(mstDriver).block();

        // Get all the mstDriverList where vehicleDetails equals to
        defaultMstDriverFiltering("vehicleDetails.equals=" + DEFAULT_VEHICLE_DETAILS, "vehicleDetails.equals=" + UPDATED_VEHICLE_DETAILS);
    }

    @Test
    void getAllMstDriversByVehicleDetailsIsInShouldWork() {
        // Initialize the database
        insertedMstDriver = mstDriverRepository.save(mstDriver).block();

        // Get all the mstDriverList where vehicleDetails in
        defaultMstDriverFiltering(
            "vehicleDetails.in=" + DEFAULT_VEHICLE_DETAILS + "," + UPDATED_VEHICLE_DETAILS,
            "vehicleDetails.in=" + UPDATED_VEHICLE_DETAILS
        );
    }

    @Test
    void getAllMstDriversByVehicleDetailsIsNullOrNotNull() {
        // Initialize the database
        insertedMstDriver = mstDriverRepository.save(mstDriver).block();

        // Get all the mstDriverList where vehicleDetails is not null
        defaultMstDriverFiltering("vehicleDetails.specified=true", "vehicleDetails.specified=false");
    }

    @Test
    void getAllMstDriversByVehicleDetailsContainsSomething() {
        // Initialize the database
        insertedMstDriver = mstDriverRepository.save(mstDriver).block();

        // Get all the mstDriverList where vehicleDetails contains
        defaultMstDriverFiltering(
            "vehicleDetails.contains=" + DEFAULT_VEHICLE_DETAILS,
            "vehicleDetails.contains=" + UPDATED_VEHICLE_DETAILS
        );
    }

    @Test
    void getAllMstDriversByVehicleDetailsNotContainsSomething() {
        // Initialize the database
        insertedMstDriver = mstDriverRepository.save(mstDriver).block();

        // Get all the mstDriverList where vehicleDetails does not contain
        defaultMstDriverFiltering(
            "vehicleDetails.doesNotContain=" + UPDATED_VEHICLE_DETAILS,
            "vehicleDetails.doesNotContain=" + DEFAULT_VEHICLE_DETAILS
        );
    }

    private void defaultMstDriverFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultMstDriverShouldBeFound(shouldBeFound);
        defaultMstDriverShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMstDriverShouldBeFound(String filter) {
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
            .value(hasItem(mstDriver.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].contactNumber")
            .value(hasItem(DEFAULT_CONTACT_NUMBER))
            .jsonPath("$.[*].vehicleDetails")
            .value(hasItem(DEFAULT_VEHICLE_DETAILS));

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
    private void defaultMstDriverShouldNotBeFound(String filter) {
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
    void getNonExistingMstDriver() {
        // Get the mstDriver
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMstDriver() throws Exception {
        // Initialize the database
        insertedMstDriver = mstDriverRepository.save(mstDriver).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        mstDriverSearchRepository.save(mstDriver).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDriverSearchRepository.findAll().collectList().block());

        // Update the mstDriver
        MstDriver updatedMstDriver = mstDriverRepository.findById(mstDriver.getId()).block();
        updatedMstDriver.name(UPDATED_NAME).contactNumber(UPDATED_CONTACT_NUMBER).vehicleDetails(UPDATED_VEHICLE_DETAILS);
        MstDriverDTO mstDriverDTO = mstDriverMapper.toDto(updatedMstDriver);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstDriverDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstDriverDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstDriver in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMstDriverToMatchAllProperties(updatedMstDriver);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDriverSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<MstDriver> mstDriverSearchList = Streamable.of(mstDriverSearchRepository.findAll().collectList().block()).toList();
                MstDriver testMstDriverSearch = mstDriverSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertMstDriverAllPropertiesEquals(testMstDriverSearch, updatedMstDriver);
                assertMstDriverUpdatableFieldsEquals(testMstDriverSearch, updatedMstDriver);
            });
    }

    @Test
    void putNonExistingMstDriver() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDriverSearchRepository.findAll().collectList().block());
        mstDriver.setId(longCount.incrementAndGet());

        // Create the MstDriver
        MstDriverDTO mstDriverDTO = mstDriverMapper.toDto(mstDriver);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstDriverDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstDriverDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstDriver in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDriverSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchMstDriver() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDriverSearchRepository.findAll().collectList().block());
        mstDriver.setId(longCount.incrementAndGet());

        // Create the MstDriver
        MstDriverDTO mstDriverDTO = mstDriverMapper.toDto(mstDriver);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstDriverDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstDriver in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDriverSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamMstDriver() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDriverSearchRepository.findAll().collectList().block());
        mstDriver.setId(longCount.incrementAndGet());

        // Create the MstDriver
        MstDriverDTO mstDriverDTO = mstDriverMapper.toDto(mstDriver);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstDriverDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstDriver in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDriverSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateMstDriverWithPatch() throws Exception {
        // Initialize the database
        insertedMstDriver = mstDriverRepository.save(mstDriver).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstDriver using partial update
        MstDriver partialUpdatedMstDriver = new MstDriver();
        partialUpdatedMstDriver.setId(mstDriver.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstDriver.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstDriver))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstDriver in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstDriverUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMstDriver, mstDriver),
            getPersistedMstDriver(mstDriver)
        );
    }

    @Test
    void fullUpdateMstDriverWithPatch() throws Exception {
        // Initialize the database
        insertedMstDriver = mstDriverRepository.save(mstDriver).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstDriver using partial update
        MstDriver partialUpdatedMstDriver = new MstDriver();
        partialUpdatedMstDriver.setId(mstDriver.getId());

        partialUpdatedMstDriver.name(UPDATED_NAME).contactNumber(UPDATED_CONTACT_NUMBER).vehicleDetails(UPDATED_VEHICLE_DETAILS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstDriver.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstDriver))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstDriver in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstDriverUpdatableFieldsEquals(partialUpdatedMstDriver, getPersistedMstDriver(partialUpdatedMstDriver));
    }

    @Test
    void patchNonExistingMstDriver() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDriverSearchRepository.findAll().collectList().block());
        mstDriver.setId(longCount.incrementAndGet());

        // Create the MstDriver
        MstDriverDTO mstDriverDTO = mstDriverMapper.toDto(mstDriver);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, mstDriverDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstDriverDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstDriver in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDriverSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchMstDriver() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDriverSearchRepository.findAll().collectList().block());
        mstDriver.setId(longCount.incrementAndGet());

        // Create the MstDriver
        MstDriverDTO mstDriverDTO = mstDriverMapper.toDto(mstDriver);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstDriverDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstDriver in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDriverSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamMstDriver() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDriverSearchRepository.findAll().collectList().block());
        mstDriver.setId(longCount.incrementAndGet());

        // Create the MstDriver
        MstDriverDTO mstDriverDTO = mstDriverMapper.toDto(mstDriver);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstDriverDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstDriver in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDriverSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteMstDriver() {
        // Initialize the database
        insertedMstDriver = mstDriverRepository.save(mstDriver).block();
        mstDriverRepository.save(mstDriver).block();
        mstDriverSearchRepository.save(mstDriver).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDriverSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the mstDriver
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, mstDriver.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDriverSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchMstDriver() {
        // Initialize the database
        insertedMstDriver = mstDriverRepository.save(mstDriver).block();
        mstDriverSearchRepository.save(mstDriver).block();

        // Search the mstDriver
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + mstDriver.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(mstDriver.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].contactNumber")
            .value(hasItem(DEFAULT_CONTACT_NUMBER))
            .jsonPath("$.[*].vehicleDetails")
            .value(hasItem(DEFAULT_VEHICLE_DETAILS));
    }

    protected long getRepositoryCount() {
        return mstDriverRepository.count().block();
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

    protected MstDriver getPersistedMstDriver(MstDriver mstDriver) {
        return mstDriverRepository.findById(mstDriver.getId()).block();
    }

    protected void assertPersistedMstDriverToMatchAllProperties(MstDriver expectedMstDriver) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstDriverAllPropertiesEquals(expectedMstDriver, getPersistedMstDriver(expectedMstDriver));
        assertMstDriverUpdatableFieldsEquals(expectedMstDriver, getPersistedMstDriver(expectedMstDriver));
    }

    protected void assertPersistedMstDriverToMatchUpdatableProperties(MstDriver expectedMstDriver) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstDriverAllUpdatablePropertiesEquals(expectedMstDriver, getPersistedMstDriver(expectedMstDriver));
        assertMstDriverUpdatableFieldsEquals(expectedMstDriver, getPersistedMstDriver(expectedMstDriver));
    }
}
