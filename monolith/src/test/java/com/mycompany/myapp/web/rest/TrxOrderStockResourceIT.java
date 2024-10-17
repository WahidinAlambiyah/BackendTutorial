package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.TrxOrderStockAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.MstSupplier;
import com.mycompany.myapp.domain.TrxOrderStock;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MstSupplierRepository;
import com.mycompany.myapp.repository.TrxOrderStockRepository;
import com.mycompany.myapp.repository.search.TrxOrderStockSearchRepository;
import com.mycompany.myapp.service.dto.TrxOrderStockDTO;
import com.mycompany.myapp.service.mapper.TrxOrderStockMapper;
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
 * Integration tests for the {@link TrxOrderStockResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TrxOrderStockResourceIT {

    private static final Integer DEFAULT_QUANTITY_ORDERED = 1;
    private static final Integer UPDATED_QUANTITY_ORDERED = 2;
    private static final Integer SMALLER_QUANTITY_ORDERED = 1 - 1;

    private static final Instant DEFAULT_ORDER_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ORDER_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_EXPECTED_ARRIVAL_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPECTED_ARRIVAL_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/trx-order-stocks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/trx-order-stocks/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TrxOrderStockRepository trxOrderStockRepository;

    @Autowired
    private TrxOrderStockMapper trxOrderStockMapper;

    @Autowired
    private TrxOrderStockSearchRepository trxOrderStockSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TrxOrderStock trxOrderStock;

    private TrxOrderStock insertedTrxOrderStock;

    @Autowired
    private MstSupplierRepository mstSupplierRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrxOrderStock createEntity(EntityManager em) {
        TrxOrderStock trxOrderStock = new TrxOrderStock()
            .quantityOrdered(DEFAULT_QUANTITY_ORDERED)
            .orderDate(DEFAULT_ORDER_DATE)
            .expectedArrivalDate(DEFAULT_EXPECTED_ARRIVAL_DATE);
        return trxOrderStock;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrxOrderStock createUpdatedEntity(EntityManager em) {
        TrxOrderStock trxOrderStock = new TrxOrderStock()
            .quantityOrdered(UPDATED_QUANTITY_ORDERED)
            .orderDate(UPDATED_ORDER_DATE)
            .expectedArrivalDate(UPDATED_EXPECTED_ARRIVAL_DATE);
        return trxOrderStock;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TrxOrderStock.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        trxOrderStock = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedTrxOrderStock != null) {
            trxOrderStockRepository.delete(insertedTrxOrderStock).block();
            trxOrderStockSearchRepository.delete(insertedTrxOrderStock).block();
            insertedTrxOrderStock = null;
        }
        deleteEntities(em);
    }

    @Test
    void createTrxOrderStock() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderStockSearchRepository.findAll().collectList().block());
        // Create the TrxOrderStock
        TrxOrderStockDTO trxOrderStockDTO = trxOrderStockMapper.toDto(trxOrderStock);
        var returnedTrxOrderStockDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderStockDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TrxOrderStockDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the TrxOrderStock in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTrxOrderStock = trxOrderStockMapper.toEntity(returnedTrxOrderStockDTO);
        assertTrxOrderStockUpdatableFieldsEquals(returnedTrxOrderStock, getPersistedTrxOrderStock(returnedTrxOrderStock));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderStockSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedTrxOrderStock = returnedTrxOrderStock;
    }

    @Test
    void createTrxOrderStockWithExistingId() throws Exception {
        // Create the TrxOrderStock with an existing ID
        trxOrderStock.setId(1L);
        TrxOrderStockDTO trxOrderStockDTO = trxOrderStockMapper.toDto(trxOrderStock);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderStockSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderStockDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxOrderStock in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderStockSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkQuantityOrderedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderStockSearchRepository.findAll().collectList().block());
        // set the field null
        trxOrderStock.setQuantityOrdered(null);

        // Create the TrxOrderStock, which fails.
        TrxOrderStockDTO trxOrderStockDTO = trxOrderStockMapper.toDto(trxOrderStock);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderStockDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderStockSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkOrderDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderStockSearchRepository.findAll().collectList().block());
        // set the field null
        trxOrderStock.setOrderDate(null);

        // Create the TrxOrderStock, which fails.
        TrxOrderStockDTO trxOrderStockDTO = trxOrderStockMapper.toDto(trxOrderStock);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderStockDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderStockSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllTrxOrderStocks() {
        // Initialize the database
        insertedTrxOrderStock = trxOrderStockRepository.save(trxOrderStock).block();

        // Get all the trxOrderStockList
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
            .value(hasItem(trxOrderStock.getId().intValue()))
            .jsonPath("$.[*].quantityOrdered")
            .value(hasItem(DEFAULT_QUANTITY_ORDERED))
            .jsonPath("$.[*].orderDate")
            .value(hasItem(DEFAULT_ORDER_DATE.toString()))
            .jsonPath("$.[*].expectedArrivalDate")
            .value(hasItem(DEFAULT_EXPECTED_ARRIVAL_DATE.toString()));
    }

    @Test
    void getTrxOrderStock() {
        // Initialize the database
        insertedTrxOrderStock = trxOrderStockRepository.save(trxOrderStock).block();

        // Get the trxOrderStock
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, trxOrderStock.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(trxOrderStock.getId().intValue()))
            .jsonPath("$.quantityOrdered")
            .value(is(DEFAULT_QUANTITY_ORDERED))
            .jsonPath("$.orderDate")
            .value(is(DEFAULT_ORDER_DATE.toString()))
            .jsonPath("$.expectedArrivalDate")
            .value(is(DEFAULT_EXPECTED_ARRIVAL_DATE.toString()));
    }

    @Test
    void getTrxOrderStocksByIdFiltering() {
        // Initialize the database
        insertedTrxOrderStock = trxOrderStockRepository.save(trxOrderStock).block();

        Long id = trxOrderStock.getId();

        defaultTrxOrderStockFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTrxOrderStockFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTrxOrderStockFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllTrxOrderStocksByQuantityOrderedIsEqualToSomething() {
        // Initialize the database
        insertedTrxOrderStock = trxOrderStockRepository.save(trxOrderStock).block();

        // Get all the trxOrderStockList where quantityOrdered equals to
        defaultTrxOrderStockFiltering(
            "quantityOrdered.equals=" + DEFAULT_QUANTITY_ORDERED,
            "quantityOrdered.equals=" + UPDATED_QUANTITY_ORDERED
        );
    }

    @Test
    void getAllTrxOrderStocksByQuantityOrderedIsInShouldWork() {
        // Initialize the database
        insertedTrxOrderStock = trxOrderStockRepository.save(trxOrderStock).block();

        // Get all the trxOrderStockList where quantityOrdered in
        defaultTrxOrderStockFiltering(
            "quantityOrdered.in=" + DEFAULT_QUANTITY_ORDERED + "," + UPDATED_QUANTITY_ORDERED,
            "quantityOrdered.in=" + UPDATED_QUANTITY_ORDERED
        );
    }

    @Test
    void getAllTrxOrderStocksByQuantityOrderedIsNullOrNotNull() {
        // Initialize the database
        insertedTrxOrderStock = trxOrderStockRepository.save(trxOrderStock).block();

        // Get all the trxOrderStockList where quantityOrdered is not null
        defaultTrxOrderStockFiltering("quantityOrdered.specified=true", "quantityOrdered.specified=false");
    }

    @Test
    void getAllTrxOrderStocksByQuantityOrderedIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxOrderStock = trxOrderStockRepository.save(trxOrderStock).block();

        // Get all the trxOrderStockList where quantityOrdered is greater than or equal to
        defaultTrxOrderStockFiltering(
            "quantityOrdered.greaterThanOrEqual=" + DEFAULT_QUANTITY_ORDERED,
            "quantityOrdered.greaterThanOrEqual=" + UPDATED_QUANTITY_ORDERED
        );
    }

    @Test
    void getAllTrxOrderStocksByQuantityOrderedIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxOrderStock = trxOrderStockRepository.save(trxOrderStock).block();

        // Get all the trxOrderStockList where quantityOrdered is less than or equal to
        defaultTrxOrderStockFiltering(
            "quantityOrdered.lessThanOrEqual=" + DEFAULT_QUANTITY_ORDERED,
            "quantityOrdered.lessThanOrEqual=" + SMALLER_QUANTITY_ORDERED
        );
    }

    @Test
    void getAllTrxOrderStocksByQuantityOrderedIsLessThanSomething() {
        // Initialize the database
        insertedTrxOrderStock = trxOrderStockRepository.save(trxOrderStock).block();

        // Get all the trxOrderStockList where quantityOrdered is less than
        defaultTrxOrderStockFiltering(
            "quantityOrdered.lessThan=" + UPDATED_QUANTITY_ORDERED,
            "quantityOrdered.lessThan=" + DEFAULT_QUANTITY_ORDERED
        );
    }

    @Test
    void getAllTrxOrderStocksByQuantityOrderedIsGreaterThanSomething() {
        // Initialize the database
        insertedTrxOrderStock = trxOrderStockRepository.save(trxOrderStock).block();

        // Get all the trxOrderStockList where quantityOrdered is greater than
        defaultTrxOrderStockFiltering(
            "quantityOrdered.greaterThan=" + SMALLER_QUANTITY_ORDERED,
            "quantityOrdered.greaterThan=" + DEFAULT_QUANTITY_ORDERED
        );
    }

    @Test
    void getAllTrxOrderStocksByOrderDateIsEqualToSomething() {
        // Initialize the database
        insertedTrxOrderStock = trxOrderStockRepository.save(trxOrderStock).block();

        // Get all the trxOrderStockList where orderDate equals to
        defaultTrxOrderStockFiltering("orderDate.equals=" + DEFAULT_ORDER_DATE, "orderDate.equals=" + UPDATED_ORDER_DATE);
    }

    @Test
    void getAllTrxOrderStocksByOrderDateIsInShouldWork() {
        // Initialize the database
        insertedTrxOrderStock = trxOrderStockRepository.save(trxOrderStock).block();

        // Get all the trxOrderStockList where orderDate in
        defaultTrxOrderStockFiltering(
            "orderDate.in=" + DEFAULT_ORDER_DATE + "," + UPDATED_ORDER_DATE,
            "orderDate.in=" + UPDATED_ORDER_DATE
        );
    }

    @Test
    void getAllTrxOrderStocksByOrderDateIsNullOrNotNull() {
        // Initialize the database
        insertedTrxOrderStock = trxOrderStockRepository.save(trxOrderStock).block();

        // Get all the trxOrderStockList where orderDate is not null
        defaultTrxOrderStockFiltering("orderDate.specified=true", "orderDate.specified=false");
    }

    @Test
    void getAllTrxOrderStocksByExpectedArrivalDateIsEqualToSomething() {
        // Initialize the database
        insertedTrxOrderStock = trxOrderStockRepository.save(trxOrderStock).block();

        // Get all the trxOrderStockList where expectedArrivalDate equals to
        defaultTrxOrderStockFiltering(
            "expectedArrivalDate.equals=" + DEFAULT_EXPECTED_ARRIVAL_DATE,
            "expectedArrivalDate.equals=" + UPDATED_EXPECTED_ARRIVAL_DATE
        );
    }

    @Test
    void getAllTrxOrderStocksByExpectedArrivalDateIsInShouldWork() {
        // Initialize the database
        insertedTrxOrderStock = trxOrderStockRepository.save(trxOrderStock).block();

        // Get all the trxOrderStockList where expectedArrivalDate in
        defaultTrxOrderStockFiltering(
            "expectedArrivalDate.in=" + DEFAULT_EXPECTED_ARRIVAL_DATE + "," + UPDATED_EXPECTED_ARRIVAL_DATE,
            "expectedArrivalDate.in=" + UPDATED_EXPECTED_ARRIVAL_DATE
        );
    }

    @Test
    void getAllTrxOrderStocksByExpectedArrivalDateIsNullOrNotNull() {
        // Initialize the database
        insertedTrxOrderStock = trxOrderStockRepository.save(trxOrderStock).block();

        // Get all the trxOrderStockList where expectedArrivalDate is not null
        defaultTrxOrderStockFiltering("expectedArrivalDate.specified=true", "expectedArrivalDate.specified=false");
    }

    @Test
    void getAllTrxOrderStocksBySupplierIsEqualToSomething() {
        MstSupplier supplier = MstSupplierResourceIT.createEntity(em);
        mstSupplierRepository.save(supplier).block();
        Long supplierId = supplier.getId();
        trxOrderStock.setSupplierId(supplierId);
        insertedTrxOrderStock = trxOrderStockRepository.save(trxOrderStock).block();
        // Get all the trxOrderStockList where supplier equals to supplierId
        defaultTrxOrderStockShouldBeFound("supplierId.equals=" + supplierId);

        // Get all the trxOrderStockList where supplier equals to (supplierId + 1)
        defaultTrxOrderStockShouldNotBeFound("supplierId.equals=" + (supplierId + 1));
    }

    private void defaultTrxOrderStockFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultTrxOrderStockShouldBeFound(shouldBeFound);
        defaultTrxOrderStockShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTrxOrderStockShouldBeFound(String filter) {
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
            .value(hasItem(trxOrderStock.getId().intValue()))
            .jsonPath("$.[*].quantityOrdered")
            .value(hasItem(DEFAULT_QUANTITY_ORDERED))
            .jsonPath("$.[*].orderDate")
            .value(hasItem(DEFAULT_ORDER_DATE.toString()))
            .jsonPath("$.[*].expectedArrivalDate")
            .value(hasItem(DEFAULT_EXPECTED_ARRIVAL_DATE.toString()));

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
    private void defaultTrxOrderStockShouldNotBeFound(String filter) {
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
    void getNonExistingTrxOrderStock() {
        // Get the trxOrderStock
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTrxOrderStock() throws Exception {
        // Initialize the database
        insertedTrxOrderStock = trxOrderStockRepository.save(trxOrderStock).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        trxOrderStockSearchRepository.save(trxOrderStock).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderStockSearchRepository.findAll().collectList().block());

        // Update the trxOrderStock
        TrxOrderStock updatedTrxOrderStock = trxOrderStockRepository.findById(trxOrderStock.getId()).block();
        updatedTrxOrderStock
            .quantityOrdered(UPDATED_QUANTITY_ORDERED)
            .orderDate(UPDATED_ORDER_DATE)
            .expectedArrivalDate(UPDATED_EXPECTED_ARRIVAL_DATE);
        TrxOrderStockDTO trxOrderStockDTO = trxOrderStockMapper.toDto(updatedTrxOrderStock);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trxOrderStockDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderStockDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxOrderStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTrxOrderStockToMatchAllProperties(updatedTrxOrderStock);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderStockSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TrxOrderStock> trxOrderStockSearchList = Streamable.of(
                    trxOrderStockSearchRepository.findAll().collectList().block()
                ).toList();
                TrxOrderStock testTrxOrderStockSearch = trxOrderStockSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertTrxOrderStockAllPropertiesEquals(testTrxOrderStockSearch, updatedTrxOrderStock);
                assertTrxOrderStockUpdatableFieldsEquals(testTrxOrderStockSearch, updatedTrxOrderStock);
            });
    }

    @Test
    void putNonExistingTrxOrderStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderStockSearchRepository.findAll().collectList().block());
        trxOrderStock.setId(longCount.incrementAndGet());

        // Create the TrxOrderStock
        TrxOrderStockDTO trxOrderStockDTO = trxOrderStockMapper.toDto(trxOrderStock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trxOrderStockDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderStockDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxOrderStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderStockSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchTrxOrderStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderStockSearchRepository.findAll().collectList().block());
        trxOrderStock.setId(longCount.incrementAndGet());

        // Create the TrxOrderStock
        TrxOrderStockDTO trxOrderStockDTO = trxOrderStockMapper.toDto(trxOrderStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderStockDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxOrderStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderStockSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamTrxOrderStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderStockSearchRepository.findAll().collectList().block());
        trxOrderStock.setId(longCount.incrementAndGet());

        // Create the TrxOrderStock
        TrxOrderStockDTO trxOrderStockDTO = trxOrderStockMapper.toDto(trxOrderStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderStockDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrxOrderStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderStockSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateTrxOrderStockWithPatch() throws Exception {
        // Initialize the database
        insertedTrxOrderStock = trxOrderStockRepository.save(trxOrderStock).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trxOrderStock using partial update
        TrxOrderStock partialUpdatedTrxOrderStock = new TrxOrderStock();
        partialUpdatedTrxOrderStock.setId(trxOrderStock.getId());

        partialUpdatedTrxOrderStock.quantityOrdered(UPDATED_QUANTITY_ORDERED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrxOrderStock.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrxOrderStock))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxOrderStock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrxOrderStockUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTrxOrderStock, trxOrderStock),
            getPersistedTrxOrderStock(trxOrderStock)
        );
    }

    @Test
    void fullUpdateTrxOrderStockWithPatch() throws Exception {
        // Initialize the database
        insertedTrxOrderStock = trxOrderStockRepository.save(trxOrderStock).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trxOrderStock using partial update
        TrxOrderStock partialUpdatedTrxOrderStock = new TrxOrderStock();
        partialUpdatedTrxOrderStock.setId(trxOrderStock.getId());

        partialUpdatedTrxOrderStock
            .quantityOrdered(UPDATED_QUANTITY_ORDERED)
            .orderDate(UPDATED_ORDER_DATE)
            .expectedArrivalDate(UPDATED_EXPECTED_ARRIVAL_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrxOrderStock.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrxOrderStock))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxOrderStock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrxOrderStockUpdatableFieldsEquals(partialUpdatedTrxOrderStock, getPersistedTrxOrderStock(partialUpdatedTrxOrderStock));
    }

    @Test
    void patchNonExistingTrxOrderStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderStockSearchRepository.findAll().collectList().block());
        trxOrderStock.setId(longCount.incrementAndGet());

        // Create the TrxOrderStock
        TrxOrderStockDTO trxOrderStockDTO = trxOrderStockMapper.toDto(trxOrderStock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, trxOrderStockDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxOrderStockDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxOrderStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderStockSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchTrxOrderStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderStockSearchRepository.findAll().collectList().block());
        trxOrderStock.setId(longCount.incrementAndGet());

        // Create the TrxOrderStock
        TrxOrderStockDTO trxOrderStockDTO = trxOrderStockMapper.toDto(trxOrderStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxOrderStockDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxOrderStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderStockSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamTrxOrderStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderStockSearchRepository.findAll().collectList().block());
        trxOrderStock.setId(longCount.incrementAndGet());

        // Create the TrxOrderStock
        TrxOrderStockDTO trxOrderStockDTO = trxOrderStockMapper.toDto(trxOrderStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxOrderStockDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrxOrderStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderStockSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteTrxOrderStock() {
        // Initialize the database
        insertedTrxOrderStock = trxOrderStockRepository.save(trxOrderStock).block();
        trxOrderStockRepository.save(trxOrderStock).block();
        trxOrderStockSearchRepository.save(trxOrderStock).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderStockSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the trxOrderStock
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, trxOrderStock.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderStockSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchTrxOrderStock() {
        // Initialize the database
        insertedTrxOrderStock = trxOrderStockRepository.save(trxOrderStock).block();
        trxOrderStockSearchRepository.save(trxOrderStock).block();

        // Search the trxOrderStock
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + trxOrderStock.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(trxOrderStock.getId().intValue()))
            .jsonPath("$.[*].quantityOrdered")
            .value(hasItem(DEFAULT_QUANTITY_ORDERED))
            .jsonPath("$.[*].orderDate")
            .value(hasItem(DEFAULT_ORDER_DATE.toString()))
            .jsonPath("$.[*].expectedArrivalDate")
            .value(hasItem(DEFAULT_EXPECTED_ARRIVAL_DATE.toString()));
    }

    protected long getRepositoryCount() {
        return trxOrderStockRepository.count().block();
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

    protected TrxOrderStock getPersistedTrxOrderStock(TrxOrderStock trxOrderStock) {
        return trxOrderStockRepository.findById(trxOrderStock.getId()).block();
    }

    protected void assertPersistedTrxOrderStockToMatchAllProperties(TrxOrderStock expectedTrxOrderStock) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrxOrderStockAllPropertiesEquals(expectedTrxOrderStock, getPersistedTrxOrderStock(expectedTrxOrderStock));
        assertTrxOrderStockUpdatableFieldsEquals(expectedTrxOrderStock, getPersistedTrxOrderStock(expectedTrxOrderStock));
    }

    protected void assertPersistedTrxOrderStockToMatchUpdatableProperties(TrxOrderStock expectedTrxOrderStock) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrxOrderStockAllUpdatablePropertiesEquals(expectedTrxOrderStock, getPersistedTrxOrderStock(expectedTrxOrderStock));
        assertTrxOrderStockUpdatableFieldsEquals(expectedTrxOrderStock, getPersistedTrxOrderStock(expectedTrxOrderStock));
    }
}
