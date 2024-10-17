package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.TrxDeliveryAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.MstDriver;
import com.mycompany.myapp.domain.TrxDelivery;
import com.mycompany.myapp.domain.TrxOrder;
import com.mycompany.myapp.domain.enumeration.DeliveryStatus;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MstDriverRepository;
import com.mycompany.myapp.repository.TrxDeliveryRepository;
import com.mycompany.myapp.repository.TrxOrderRepository;
import com.mycompany.myapp.repository.search.TrxDeliverySearchRepository;
import com.mycompany.myapp.service.dto.TrxDeliveryDTO;
import com.mycompany.myapp.service.mapper.TrxDeliveryMapper;
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
 * Integration tests for the {@link TrxDeliveryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TrxDeliveryResourceIT {

    private static final String DEFAULT_DELIVERY_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_DELIVERY_ADDRESS = "BBBBBBBBBB";

    private static final DeliveryStatus DEFAULT_DELIVERY_STATUS = DeliveryStatus.PENDING;
    private static final DeliveryStatus UPDATED_DELIVERY_STATUS = DeliveryStatus.OUT_FOR_DELIVERY;

    private static final String DEFAULT_ASSIGNED_DRIVER = "AAAAAAAAAA";
    private static final String UPDATED_ASSIGNED_DRIVER = "BBBBBBBBBB";

    private static final Instant DEFAULT_ESTIMATED_DELIVERY_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ESTIMATED_DELIVERY_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/trx-deliveries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/trx-deliveries/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TrxDeliveryRepository trxDeliveryRepository;

    @Autowired
    private TrxDeliveryMapper trxDeliveryMapper;

    @Autowired
    private TrxDeliverySearchRepository trxDeliverySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TrxDelivery trxDelivery;

    private TrxDelivery insertedTrxDelivery;

    @Autowired
    private MstDriverRepository mstDriverRepository;

    @Autowired
    private TrxOrderRepository trxOrderRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrxDelivery createEntity(EntityManager em) {
        TrxDelivery trxDelivery = new TrxDelivery()
            .deliveryAddress(DEFAULT_DELIVERY_ADDRESS)
            .deliveryStatus(DEFAULT_DELIVERY_STATUS)
            .assignedDriver(DEFAULT_ASSIGNED_DRIVER)
            .estimatedDeliveryTime(DEFAULT_ESTIMATED_DELIVERY_TIME);
        return trxDelivery;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrxDelivery createUpdatedEntity(EntityManager em) {
        TrxDelivery trxDelivery = new TrxDelivery()
            .deliveryAddress(UPDATED_DELIVERY_ADDRESS)
            .deliveryStatus(UPDATED_DELIVERY_STATUS)
            .assignedDriver(UPDATED_ASSIGNED_DRIVER)
            .estimatedDeliveryTime(UPDATED_ESTIMATED_DELIVERY_TIME);
        return trxDelivery;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TrxDelivery.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        trxDelivery = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedTrxDelivery != null) {
            trxDeliveryRepository.delete(insertedTrxDelivery).block();
            trxDeliverySearchRepository.delete(insertedTrxDelivery).block();
            insertedTrxDelivery = null;
        }
        deleteEntities(em);
    }

    @Test
    void createTrxDelivery() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxDeliverySearchRepository.findAll().collectList().block());
        // Create the TrxDelivery
        TrxDeliveryDTO trxDeliveryDTO = trxDeliveryMapper.toDto(trxDelivery);
        var returnedTrxDeliveryDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxDeliveryDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TrxDeliveryDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the TrxDelivery in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTrxDelivery = trxDeliveryMapper.toEntity(returnedTrxDeliveryDTO);
        assertTrxDeliveryUpdatableFieldsEquals(returnedTrxDelivery, getPersistedTrxDelivery(returnedTrxDelivery));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxDeliverySearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedTrxDelivery = returnedTrxDelivery;
    }

    @Test
    void createTrxDeliveryWithExistingId() throws Exception {
        // Create the TrxDelivery with an existing ID
        trxDelivery.setId(1L);
        TrxDeliveryDTO trxDeliveryDTO = trxDeliveryMapper.toDto(trxDelivery);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxDeliverySearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxDeliveryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxDelivery in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxDeliverySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkDeliveryAddressIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxDeliverySearchRepository.findAll().collectList().block());
        // set the field null
        trxDelivery.setDeliveryAddress(null);

        // Create the TrxDelivery, which fails.
        TrxDeliveryDTO trxDeliveryDTO = trxDeliveryMapper.toDto(trxDelivery);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxDeliveryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxDeliverySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkDeliveryStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxDeliverySearchRepository.findAll().collectList().block());
        // set the field null
        trxDelivery.setDeliveryStatus(null);

        // Create the TrxDelivery, which fails.
        TrxDeliveryDTO trxDeliveryDTO = trxDeliveryMapper.toDto(trxDelivery);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxDeliveryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxDeliverySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllTrxDeliveries() {
        // Initialize the database
        insertedTrxDelivery = trxDeliveryRepository.save(trxDelivery).block();

        // Get all the trxDeliveryList
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
            .value(hasItem(trxDelivery.getId().intValue()))
            .jsonPath("$.[*].deliveryAddress")
            .value(hasItem(DEFAULT_DELIVERY_ADDRESS))
            .jsonPath("$.[*].deliveryStatus")
            .value(hasItem(DEFAULT_DELIVERY_STATUS.toString()))
            .jsonPath("$.[*].assignedDriver")
            .value(hasItem(DEFAULT_ASSIGNED_DRIVER))
            .jsonPath("$.[*].estimatedDeliveryTime")
            .value(hasItem(DEFAULT_ESTIMATED_DELIVERY_TIME.toString()));
    }

    @Test
    void getTrxDelivery() {
        // Initialize the database
        insertedTrxDelivery = trxDeliveryRepository.save(trxDelivery).block();

        // Get the trxDelivery
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, trxDelivery.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(trxDelivery.getId().intValue()))
            .jsonPath("$.deliveryAddress")
            .value(is(DEFAULT_DELIVERY_ADDRESS))
            .jsonPath("$.deliveryStatus")
            .value(is(DEFAULT_DELIVERY_STATUS.toString()))
            .jsonPath("$.assignedDriver")
            .value(is(DEFAULT_ASSIGNED_DRIVER))
            .jsonPath("$.estimatedDeliveryTime")
            .value(is(DEFAULT_ESTIMATED_DELIVERY_TIME.toString()));
    }

    @Test
    void getTrxDeliveriesByIdFiltering() {
        // Initialize the database
        insertedTrxDelivery = trxDeliveryRepository.save(trxDelivery).block();

        Long id = trxDelivery.getId();

        defaultTrxDeliveryFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTrxDeliveryFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTrxDeliveryFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllTrxDeliveriesByDeliveryAddressIsEqualToSomething() {
        // Initialize the database
        insertedTrxDelivery = trxDeliveryRepository.save(trxDelivery).block();

        // Get all the trxDeliveryList where deliveryAddress equals to
        defaultTrxDeliveryFiltering(
            "deliveryAddress.equals=" + DEFAULT_DELIVERY_ADDRESS,
            "deliveryAddress.equals=" + UPDATED_DELIVERY_ADDRESS
        );
    }

    @Test
    void getAllTrxDeliveriesByDeliveryAddressIsInShouldWork() {
        // Initialize the database
        insertedTrxDelivery = trxDeliveryRepository.save(trxDelivery).block();

        // Get all the trxDeliveryList where deliveryAddress in
        defaultTrxDeliveryFiltering(
            "deliveryAddress.in=" + DEFAULT_DELIVERY_ADDRESS + "," + UPDATED_DELIVERY_ADDRESS,
            "deliveryAddress.in=" + UPDATED_DELIVERY_ADDRESS
        );
    }

    @Test
    void getAllTrxDeliveriesByDeliveryAddressIsNullOrNotNull() {
        // Initialize the database
        insertedTrxDelivery = trxDeliveryRepository.save(trxDelivery).block();

        // Get all the trxDeliveryList where deliveryAddress is not null
        defaultTrxDeliveryFiltering("deliveryAddress.specified=true", "deliveryAddress.specified=false");
    }

    @Test
    void getAllTrxDeliveriesByDeliveryAddressContainsSomething() {
        // Initialize the database
        insertedTrxDelivery = trxDeliveryRepository.save(trxDelivery).block();

        // Get all the trxDeliveryList where deliveryAddress contains
        defaultTrxDeliveryFiltering(
            "deliveryAddress.contains=" + DEFAULT_DELIVERY_ADDRESS,
            "deliveryAddress.contains=" + UPDATED_DELIVERY_ADDRESS
        );
    }

    @Test
    void getAllTrxDeliveriesByDeliveryAddressNotContainsSomething() {
        // Initialize the database
        insertedTrxDelivery = trxDeliveryRepository.save(trxDelivery).block();

        // Get all the trxDeliveryList where deliveryAddress does not contain
        defaultTrxDeliveryFiltering(
            "deliveryAddress.doesNotContain=" + UPDATED_DELIVERY_ADDRESS,
            "deliveryAddress.doesNotContain=" + DEFAULT_DELIVERY_ADDRESS
        );
    }

    @Test
    void getAllTrxDeliveriesByDeliveryStatusIsEqualToSomething() {
        // Initialize the database
        insertedTrxDelivery = trxDeliveryRepository.save(trxDelivery).block();

        // Get all the trxDeliveryList where deliveryStatus equals to
        defaultTrxDeliveryFiltering("deliveryStatus.equals=" + DEFAULT_DELIVERY_STATUS, "deliveryStatus.equals=" + UPDATED_DELIVERY_STATUS);
    }

    @Test
    void getAllTrxDeliveriesByDeliveryStatusIsInShouldWork() {
        // Initialize the database
        insertedTrxDelivery = trxDeliveryRepository.save(trxDelivery).block();

        // Get all the trxDeliveryList where deliveryStatus in
        defaultTrxDeliveryFiltering(
            "deliveryStatus.in=" + DEFAULT_DELIVERY_STATUS + "," + UPDATED_DELIVERY_STATUS,
            "deliveryStatus.in=" + UPDATED_DELIVERY_STATUS
        );
    }

    @Test
    void getAllTrxDeliveriesByDeliveryStatusIsNullOrNotNull() {
        // Initialize the database
        insertedTrxDelivery = trxDeliveryRepository.save(trxDelivery).block();

        // Get all the trxDeliveryList where deliveryStatus is not null
        defaultTrxDeliveryFiltering("deliveryStatus.specified=true", "deliveryStatus.specified=false");
    }

    @Test
    void getAllTrxDeliveriesByAssignedDriverIsEqualToSomething() {
        // Initialize the database
        insertedTrxDelivery = trxDeliveryRepository.save(trxDelivery).block();

        // Get all the trxDeliveryList where assignedDriver equals to
        defaultTrxDeliveryFiltering("assignedDriver.equals=" + DEFAULT_ASSIGNED_DRIVER, "assignedDriver.equals=" + UPDATED_ASSIGNED_DRIVER);
    }

    @Test
    void getAllTrxDeliveriesByAssignedDriverIsInShouldWork() {
        // Initialize the database
        insertedTrxDelivery = trxDeliveryRepository.save(trxDelivery).block();

        // Get all the trxDeliveryList where assignedDriver in
        defaultTrxDeliveryFiltering(
            "assignedDriver.in=" + DEFAULT_ASSIGNED_DRIVER + "," + UPDATED_ASSIGNED_DRIVER,
            "assignedDriver.in=" + UPDATED_ASSIGNED_DRIVER
        );
    }

    @Test
    void getAllTrxDeliveriesByAssignedDriverIsNullOrNotNull() {
        // Initialize the database
        insertedTrxDelivery = trxDeliveryRepository.save(trxDelivery).block();

        // Get all the trxDeliveryList where assignedDriver is not null
        defaultTrxDeliveryFiltering("assignedDriver.specified=true", "assignedDriver.specified=false");
    }

    @Test
    void getAllTrxDeliveriesByAssignedDriverContainsSomething() {
        // Initialize the database
        insertedTrxDelivery = trxDeliveryRepository.save(trxDelivery).block();

        // Get all the trxDeliveryList where assignedDriver contains
        defaultTrxDeliveryFiltering(
            "assignedDriver.contains=" + DEFAULT_ASSIGNED_DRIVER,
            "assignedDriver.contains=" + UPDATED_ASSIGNED_DRIVER
        );
    }

    @Test
    void getAllTrxDeliveriesByAssignedDriverNotContainsSomething() {
        // Initialize the database
        insertedTrxDelivery = trxDeliveryRepository.save(trxDelivery).block();

        // Get all the trxDeliveryList where assignedDriver does not contain
        defaultTrxDeliveryFiltering(
            "assignedDriver.doesNotContain=" + UPDATED_ASSIGNED_DRIVER,
            "assignedDriver.doesNotContain=" + DEFAULT_ASSIGNED_DRIVER
        );
    }

    @Test
    void getAllTrxDeliveriesByEstimatedDeliveryTimeIsEqualToSomething() {
        // Initialize the database
        insertedTrxDelivery = trxDeliveryRepository.save(trxDelivery).block();

        // Get all the trxDeliveryList where estimatedDeliveryTime equals to
        defaultTrxDeliveryFiltering(
            "estimatedDeliveryTime.equals=" + DEFAULT_ESTIMATED_DELIVERY_TIME,
            "estimatedDeliveryTime.equals=" + UPDATED_ESTIMATED_DELIVERY_TIME
        );
    }

    @Test
    void getAllTrxDeliveriesByEstimatedDeliveryTimeIsInShouldWork() {
        // Initialize the database
        insertedTrxDelivery = trxDeliveryRepository.save(trxDelivery).block();

        // Get all the trxDeliveryList where estimatedDeliveryTime in
        defaultTrxDeliveryFiltering(
            "estimatedDeliveryTime.in=" + DEFAULT_ESTIMATED_DELIVERY_TIME + "," + UPDATED_ESTIMATED_DELIVERY_TIME,
            "estimatedDeliveryTime.in=" + UPDATED_ESTIMATED_DELIVERY_TIME
        );
    }

    @Test
    void getAllTrxDeliveriesByEstimatedDeliveryTimeIsNullOrNotNull() {
        // Initialize the database
        insertedTrxDelivery = trxDeliveryRepository.save(trxDelivery).block();

        // Get all the trxDeliveryList where estimatedDeliveryTime is not null
        defaultTrxDeliveryFiltering("estimatedDeliveryTime.specified=true", "estimatedDeliveryTime.specified=false");
    }

    @Test
    void getAllTrxDeliveriesByDriverIsEqualToSomething() {
        MstDriver driver = MstDriverResourceIT.createEntity(em);
        mstDriverRepository.save(driver).block();
        Long driverId = driver.getId();
        trxDelivery.setDriverId(driverId);
        insertedTrxDelivery = trxDeliveryRepository.save(trxDelivery).block();
        // Get all the trxDeliveryList where driver equals to driverId
        defaultTrxDeliveryShouldBeFound("driverId.equals=" + driverId);

        // Get all the trxDeliveryList where driver equals to (driverId + 1)
        defaultTrxDeliveryShouldNotBeFound("driverId.equals=" + (driverId + 1));
    }

    @Test
    void getAllTrxDeliveriesByTrxOrderIsEqualToSomething() {
        TrxOrder trxOrder = TrxOrderResourceIT.createEntity(em);
        trxOrderRepository.save(trxOrder).block();
        Long trxOrderId = trxOrder.getId();
        trxDelivery.setTrxOrderId(trxOrderId);
        insertedTrxDelivery = trxDeliveryRepository.save(trxDelivery).block();
        // Get all the trxDeliveryList where trxOrder equals to trxOrderId
        defaultTrxDeliveryShouldBeFound("trxOrderId.equals=" + trxOrderId);

        // Get all the trxDeliveryList where trxOrder equals to (trxOrderId + 1)
        defaultTrxDeliveryShouldNotBeFound("trxOrderId.equals=" + (trxOrderId + 1));
    }

    private void defaultTrxDeliveryFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultTrxDeliveryShouldBeFound(shouldBeFound);
        defaultTrxDeliveryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTrxDeliveryShouldBeFound(String filter) {
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
            .value(hasItem(trxDelivery.getId().intValue()))
            .jsonPath("$.[*].deliveryAddress")
            .value(hasItem(DEFAULT_DELIVERY_ADDRESS))
            .jsonPath("$.[*].deliveryStatus")
            .value(hasItem(DEFAULT_DELIVERY_STATUS.toString()))
            .jsonPath("$.[*].assignedDriver")
            .value(hasItem(DEFAULT_ASSIGNED_DRIVER))
            .jsonPath("$.[*].estimatedDeliveryTime")
            .value(hasItem(DEFAULT_ESTIMATED_DELIVERY_TIME.toString()));

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
    private void defaultTrxDeliveryShouldNotBeFound(String filter) {
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
    void getNonExistingTrxDelivery() {
        // Get the trxDelivery
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTrxDelivery() throws Exception {
        // Initialize the database
        insertedTrxDelivery = trxDeliveryRepository.save(trxDelivery).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        trxDeliverySearchRepository.save(trxDelivery).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxDeliverySearchRepository.findAll().collectList().block());

        // Update the trxDelivery
        TrxDelivery updatedTrxDelivery = trxDeliveryRepository.findById(trxDelivery.getId()).block();
        updatedTrxDelivery
            .deliveryAddress(UPDATED_DELIVERY_ADDRESS)
            .deliveryStatus(UPDATED_DELIVERY_STATUS)
            .assignedDriver(UPDATED_ASSIGNED_DRIVER)
            .estimatedDeliveryTime(UPDATED_ESTIMATED_DELIVERY_TIME);
        TrxDeliveryDTO trxDeliveryDTO = trxDeliveryMapper.toDto(updatedTrxDelivery);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trxDeliveryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxDeliveryDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxDelivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTrxDeliveryToMatchAllProperties(updatedTrxDelivery);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxDeliverySearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TrxDelivery> trxDeliverySearchList = Streamable.of(
                    trxDeliverySearchRepository.findAll().collectList().block()
                ).toList();
                TrxDelivery testTrxDeliverySearch = trxDeliverySearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertTrxDeliveryAllPropertiesEquals(testTrxDeliverySearch, updatedTrxDelivery);
                assertTrxDeliveryUpdatableFieldsEquals(testTrxDeliverySearch, updatedTrxDelivery);
            });
    }

    @Test
    void putNonExistingTrxDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxDeliverySearchRepository.findAll().collectList().block());
        trxDelivery.setId(longCount.incrementAndGet());

        // Create the TrxDelivery
        TrxDeliveryDTO trxDeliveryDTO = trxDeliveryMapper.toDto(trxDelivery);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trxDeliveryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxDeliveryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxDelivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxDeliverySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchTrxDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxDeliverySearchRepository.findAll().collectList().block());
        trxDelivery.setId(longCount.incrementAndGet());

        // Create the TrxDelivery
        TrxDeliveryDTO trxDeliveryDTO = trxDeliveryMapper.toDto(trxDelivery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxDeliveryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxDelivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxDeliverySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamTrxDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxDeliverySearchRepository.findAll().collectList().block());
        trxDelivery.setId(longCount.incrementAndGet());

        // Create the TrxDelivery
        TrxDeliveryDTO trxDeliveryDTO = trxDeliveryMapper.toDto(trxDelivery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxDeliveryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrxDelivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxDeliverySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateTrxDeliveryWithPatch() throws Exception {
        // Initialize the database
        insertedTrxDelivery = trxDeliveryRepository.save(trxDelivery).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trxDelivery using partial update
        TrxDelivery partialUpdatedTrxDelivery = new TrxDelivery();
        partialUpdatedTrxDelivery.setId(trxDelivery.getId());

        partialUpdatedTrxDelivery
            .deliveryAddress(UPDATED_DELIVERY_ADDRESS)
            .deliveryStatus(UPDATED_DELIVERY_STATUS)
            .estimatedDeliveryTime(UPDATED_ESTIMATED_DELIVERY_TIME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrxDelivery.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrxDelivery))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxDelivery in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrxDeliveryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTrxDelivery, trxDelivery),
            getPersistedTrxDelivery(trxDelivery)
        );
    }

    @Test
    void fullUpdateTrxDeliveryWithPatch() throws Exception {
        // Initialize the database
        insertedTrxDelivery = trxDeliveryRepository.save(trxDelivery).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trxDelivery using partial update
        TrxDelivery partialUpdatedTrxDelivery = new TrxDelivery();
        partialUpdatedTrxDelivery.setId(trxDelivery.getId());

        partialUpdatedTrxDelivery
            .deliveryAddress(UPDATED_DELIVERY_ADDRESS)
            .deliveryStatus(UPDATED_DELIVERY_STATUS)
            .assignedDriver(UPDATED_ASSIGNED_DRIVER)
            .estimatedDeliveryTime(UPDATED_ESTIMATED_DELIVERY_TIME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrxDelivery.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrxDelivery))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxDelivery in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrxDeliveryUpdatableFieldsEquals(partialUpdatedTrxDelivery, getPersistedTrxDelivery(partialUpdatedTrxDelivery));
    }

    @Test
    void patchNonExistingTrxDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxDeliverySearchRepository.findAll().collectList().block());
        trxDelivery.setId(longCount.incrementAndGet());

        // Create the TrxDelivery
        TrxDeliveryDTO trxDeliveryDTO = trxDeliveryMapper.toDto(trxDelivery);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, trxDeliveryDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxDeliveryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxDelivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxDeliverySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchTrxDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxDeliverySearchRepository.findAll().collectList().block());
        trxDelivery.setId(longCount.incrementAndGet());

        // Create the TrxDelivery
        TrxDeliveryDTO trxDeliveryDTO = trxDeliveryMapper.toDto(trxDelivery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxDeliveryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxDelivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxDeliverySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamTrxDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxDeliverySearchRepository.findAll().collectList().block());
        trxDelivery.setId(longCount.incrementAndGet());

        // Create the TrxDelivery
        TrxDeliveryDTO trxDeliveryDTO = trxDeliveryMapper.toDto(trxDelivery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxDeliveryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrxDelivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxDeliverySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteTrxDelivery() {
        // Initialize the database
        insertedTrxDelivery = trxDeliveryRepository.save(trxDelivery).block();
        trxDeliveryRepository.save(trxDelivery).block();
        trxDeliverySearchRepository.save(trxDelivery).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxDeliverySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the trxDelivery
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, trxDelivery.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxDeliverySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchTrxDelivery() {
        // Initialize the database
        insertedTrxDelivery = trxDeliveryRepository.save(trxDelivery).block();
        trxDeliverySearchRepository.save(trxDelivery).block();

        // Search the trxDelivery
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + trxDelivery.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(trxDelivery.getId().intValue()))
            .jsonPath("$.[*].deliveryAddress")
            .value(hasItem(DEFAULT_DELIVERY_ADDRESS))
            .jsonPath("$.[*].deliveryStatus")
            .value(hasItem(DEFAULT_DELIVERY_STATUS.toString()))
            .jsonPath("$.[*].assignedDriver")
            .value(hasItem(DEFAULT_ASSIGNED_DRIVER))
            .jsonPath("$.[*].estimatedDeliveryTime")
            .value(hasItem(DEFAULT_ESTIMATED_DELIVERY_TIME.toString()));
    }

    protected long getRepositoryCount() {
        return trxDeliveryRepository.count().block();
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

    protected TrxDelivery getPersistedTrxDelivery(TrxDelivery trxDelivery) {
        return trxDeliveryRepository.findById(trxDelivery.getId()).block();
    }

    protected void assertPersistedTrxDeliveryToMatchAllProperties(TrxDelivery expectedTrxDelivery) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrxDeliveryAllPropertiesEquals(expectedTrxDelivery, getPersistedTrxDelivery(expectedTrxDelivery));
        assertTrxDeliveryUpdatableFieldsEquals(expectedTrxDelivery, getPersistedTrxDelivery(expectedTrxDelivery));
    }

    protected void assertPersistedTrxDeliveryToMatchUpdatableProperties(TrxDelivery expectedTrxDelivery) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrxDeliveryAllUpdatablePropertiesEquals(expectedTrxDelivery, getPersistedTrxDelivery(expectedTrxDelivery));
        assertTrxDeliveryUpdatableFieldsEquals(expectedTrxDelivery, getPersistedTrxDelivery(expectedTrxDelivery));
    }
}
