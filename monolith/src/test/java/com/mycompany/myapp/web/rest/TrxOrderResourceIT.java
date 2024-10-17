package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.TrxOrderAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.MstCustomer;
import com.mycompany.myapp.domain.TrxOrder;
import com.mycompany.myapp.domain.enumeration.OrderStatus;
import com.mycompany.myapp.domain.enumeration.PaymentMethod;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MstCustomerRepository;
import com.mycompany.myapp.repository.TrxOrderRepository;
import com.mycompany.myapp.repository.search.TrxOrderSearchRepository;
import com.mycompany.myapp.service.dto.TrxOrderDTO;
import com.mycompany.myapp.service.mapper.TrxOrderMapper;
import java.math.BigDecimal;
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
 * Integration tests for the {@link TrxOrderResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TrxOrderResourceIT {

    private static final Instant DEFAULT_ORDER_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ORDER_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DELIVERY_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DELIVERY_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final OrderStatus DEFAULT_ORDER_STATUS = OrderStatus.PENDING;
    private static final OrderStatus UPDATED_ORDER_STATUS = OrderStatus.COMPLETED;

    private static final PaymentMethod DEFAULT_PAYMENT_METHOD = PaymentMethod.CASH;
    private static final PaymentMethod UPDATED_PAYMENT_METHOD = PaymentMethod.CARD;

    private static final BigDecimal DEFAULT_TOTAL_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_TOTAL_AMOUNT = new BigDecimal(1 - 1);

    private static final String ENTITY_API_URL = "/api/trx-orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/trx-orders/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TrxOrderRepository trxOrderRepository;

    @Autowired
    private TrxOrderMapper trxOrderMapper;

    @Autowired
    private TrxOrderSearchRepository trxOrderSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TrxOrder trxOrder;

    private TrxOrder insertedTrxOrder;

    @Autowired
    private MstCustomerRepository mstCustomerRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrxOrder createEntity(EntityManager em) {
        TrxOrder trxOrder = new TrxOrder()
            .orderDate(DEFAULT_ORDER_DATE)
            .deliveryDate(DEFAULT_DELIVERY_DATE)
            .orderStatus(DEFAULT_ORDER_STATUS)
            .paymentMethod(DEFAULT_PAYMENT_METHOD)
            .totalAmount(DEFAULT_TOTAL_AMOUNT);
        return trxOrder;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrxOrder createUpdatedEntity(EntityManager em) {
        TrxOrder trxOrder = new TrxOrder()
            .orderDate(UPDATED_ORDER_DATE)
            .deliveryDate(UPDATED_DELIVERY_DATE)
            .orderStatus(UPDATED_ORDER_STATUS)
            .paymentMethod(UPDATED_PAYMENT_METHOD)
            .totalAmount(UPDATED_TOTAL_AMOUNT);
        return trxOrder;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TrxOrder.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        trxOrder = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedTrxOrder != null) {
            trxOrderRepository.delete(insertedTrxOrder).block();
            trxOrderSearchRepository.delete(insertedTrxOrder).block();
            insertedTrxOrder = null;
        }
        deleteEntities(em);
    }

    @Test
    void createTrxOrder() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderSearchRepository.findAll().collectList().block());
        // Create the TrxOrder
        TrxOrderDTO trxOrderDTO = trxOrderMapper.toDto(trxOrder);
        var returnedTrxOrderDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TrxOrderDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the TrxOrder in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTrxOrder = trxOrderMapper.toEntity(returnedTrxOrderDTO);
        assertTrxOrderUpdatableFieldsEquals(returnedTrxOrder, getPersistedTrxOrder(returnedTrxOrder));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedTrxOrder = returnedTrxOrder;
    }

    @Test
    void createTrxOrderWithExistingId() throws Exception {
        // Create the TrxOrder with an existing ID
        trxOrder.setId(1L);
        TrxOrderDTO trxOrderDTO = trxOrderMapper.toDto(trxOrder);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkOrderDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderSearchRepository.findAll().collectList().block());
        // set the field null
        trxOrder.setOrderDate(null);

        // Create the TrxOrder, which fails.
        TrxOrderDTO trxOrderDTO = trxOrderMapper.toDto(trxOrder);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkOrderStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderSearchRepository.findAll().collectList().block());
        // set the field null
        trxOrder.setOrderStatus(null);

        // Create the TrxOrder, which fails.
        TrxOrderDTO trxOrderDTO = trxOrderMapper.toDto(trxOrder);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkPaymentMethodIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderSearchRepository.findAll().collectList().block());
        // set the field null
        trxOrder.setPaymentMethod(null);

        // Create the TrxOrder, which fails.
        TrxOrderDTO trxOrderDTO = trxOrderMapper.toDto(trxOrder);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkTotalAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderSearchRepository.findAll().collectList().block());
        // set the field null
        trxOrder.setTotalAmount(null);

        // Create the TrxOrder, which fails.
        TrxOrderDTO trxOrderDTO = trxOrderMapper.toDto(trxOrder);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllTrxOrders() {
        // Initialize the database
        insertedTrxOrder = trxOrderRepository.save(trxOrder).block();

        // Get all the trxOrderList
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
            .value(hasItem(trxOrder.getId().intValue()))
            .jsonPath("$.[*].orderDate")
            .value(hasItem(DEFAULT_ORDER_DATE.toString()))
            .jsonPath("$.[*].deliveryDate")
            .value(hasItem(DEFAULT_DELIVERY_DATE.toString()))
            .jsonPath("$.[*].orderStatus")
            .value(hasItem(DEFAULT_ORDER_STATUS.toString()))
            .jsonPath("$.[*].paymentMethod")
            .value(hasItem(DEFAULT_PAYMENT_METHOD.toString()))
            .jsonPath("$.[*].totalAmount")
            .value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT)));
    }

    @Test
    void getTrxOrder() {
        // Initialize the database
        insertedTrxOrder = trxOrderRepository.save(trxOrder).block();

        // Get the trxOrder
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, trxOrder.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(trxOrder.getId().intValue()))
            .jsonPath("$.orderDate")
            .value(is(DEFAULT_ORDER_DATE.toString()))
            .jsonPath("$.deliveryDate")
            .value(is(DEFAULT_DELIVERY_DATE.toString()))
            .jsonPath("$.orderStatus")
            .value(is(DEFAULT_ORDER_STATUS.toString()))
            .jsonPath("$.paymentMethod")
            .value(is(DEFAULT_PAYMENT_METHOD.toString()))
            .jsonPath("$.totalAmount")
            .value(is(sameNumber(DEFAULT_TOTAL_AMOUNT)));
    }

    @Test
    void getTrxOrdersByIdFiltering() {
        // Initialize the database
        insertedTrxOrder = trxOrderRepository.save(trxOrder).block();

        Long id = trxOrder.getId();

        defaultTrxOrderFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTrxOrderFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTrxOrderFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllTrxOrdersByOrderDateIsEqualToSomething() {
        // Initialize the database
        insertedTrxOrder = trxOrderRepository.save(trxOrder).block();

        // Get all the trxOrderList where orderDate equals to
        defaultTrxOrderFiltering("orderDate.equals=" + DEFAULT_ORDER_DATE, "orderDate.equals=" + UPDATED_ORDER_DATE);
    }

    @Test
    void getAllTrxOrdersByOrderDateIsInShouldWork() {
        // Initialize the database
        insertedTrxOrder = trxOrderRepository.save(trxOrder).block();

        // Get all the trxOrderList where orderDate in
        defaultTrxOrderFiltering("orderDate.in=" + DEFAULT_ORDER_DATE + "," + UPDATED_ORDER_DATE, "orderDate.in=" + UPDATED_ORDER_DATE);
    }

    @Test
    void getAllTrxOrdersByOrderDateIsNullOrNotNull() {
        // Initialize the database
        insertedTrxOrder = trxOrderRepository.save(trxOrder).block();

        // Get all the trxOrderList where orderDate is not null
        defaultTrxOrderFiltering("orderDate.specified=true", "orderDate.specified=false");
    }

    @Test
    void getAllTrxOrdersByDeliveryDateIsEqualToSomething() {
        // Initialize the database
        insertedTrxOrder = trxOrderRepository.save(trxOrder).block();

        // Get all the trxOrderList where deliveryDate equals to
        defaultTrxOrderFiltering("deliveryDate.equals=" + DEFAULT_DELIVERY_DATE, "deliveryDate.equals=" + UPDATED_DELIVERY_DATE);
    }

    @Test
    void getAllTrxOrdersByDeliveryDateIsInShouldWork() {
        // Initialize the database
        insertedTrxOrder = trxOrderRepository.save(trxOrder).block();

        // Get all the trxOrderList where deliveryDate in
        defaultTrxOrderFiltering(
            "deliveryDate.in=" + DEFAULT_DELIVERY_DATE + "," + UPDATED_DELIVERY_DATE,
            "deliveryDate.in=" + UPDATED_DELIVERY_DATE
        );
    }

    @Test
    void getAllTrxOrdersByDeliveryDateIsNullOrNotNull() {
        // Initialize the database
        insertedTrxOrder = trxOrderRepository.save(trxOrder).block();

        // Get all the trxOrderList where deliveryDate is not null
        defaultTrxOrderFiltering("deliveryDate.specified=true", "deliveryDate.specified=false");
    }

    @Test
    void getAllTrxOrdersByOrderStatusIsEqualToSomething() {
        // Initialize the database
        insertedTrxOrder = trxOrderRepository.save(trxOrder).block();

        // Get all the trxOrderList where orderStatus equals to
        defaultTrxOrderFiltering("orderStatus.equals=" + DEFAULT_ORDER_STATUS, "orderStatus.equals=" + UPDATED_ORDER_STATUS);
    }

    @Test
    void getAllTrxOrdersByOrderStatusIsInShouldWork() {
        // Initialize the database
        insertedTrxOrder = trxOrderRepository.save(trxOrder).block();

        // Get all the trxOrderList where orderStatus in
        defaultTrxOrderFiltering(
            "orderStatus.in=" + DEFAULT_ORDER_STATUS + "," + UPDATED_ORDER_STATUS,
            "orderStatus.in=" + UPDATED_ORDER_STATUS
        );
    }

    @Test
    void getAllTrxOrdersByOrderStatusIsNullOrNotNull() {
        // Initialize the database
        insertedTrxOrder = trxOrderRepository.save(trxOrder).block();

        // Get all the trxOrderList where orderStatus is not null
        defaultTrxOrderFiltering("orderStatus.specified=true", "orderStatus.specified=false");
    }

    @Test
    void getAllTrxOrdersByPaymentMethodIsEqualToSomething() {
        // Initialize the database
        insertedTrxOrder = trxOrderRepository.save(trxOrder).block();

        // Get all the trxOrderList where paymentMethod equals to
        defaultTrxOrderFiltering("paymentMethod.equals=" + DEFAULT_PAYMENT_METHOD, "paymentMethod.equals=" + UPDATED_PAYMENT_METHOD);
    }

    @Test
    void getAllTrxOrdersByPaymentMethodIsInShouldWork() {
        // Initialize the database
        insertedTrxOrder = trxOrderRepository.save(trxOrder).block();

        // Get all the trxOrderList where paymentMethod in
        defaultTrxOrderFiltering(
            "paymentMethod.in=" + DEFAULT_PAYMENT_METHOD + "," + UPDATED_PAYMENT_METHOD,
            "paymentMethod.in=" + UPDATED_PAYMENT_METHOD
        );
    }

    @Test
    void getAllTrxOrdersByPaymentMethodIsNullOrNotNull() {
        // Initialize the database
        insertedTrxOrder = trxOrderRepository.save(trxOrder).block();

        // Get all the trxOrderList where paymentMethod is not null
        defaultTrxOrderFiltering("paymentMethod.specified=true", "paymentMethod.specified=false");
    }

    @Test
    void getAllTrxOrdersByTotalAmountIsEqualToSomething() {
        // Initialize the database
        insertedTrxOrder = trxOrderRepository.save(trxOrder).block();

        // Get all the trxOrderList where totalAmount equals to
        defaultTrxOrderFiltering("totalAmount.equals=" + DEFAULT_TOTAL_AMOUNT, "totalAmount.equals=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    void getAllTrxOrdersByTotalAmountIsInShouldWork() {
        // Initialize the database
        insertedTrxOrder = trxOrderRepository.save(trxOrder).block();

        // Get all the trxOrderList where totalAmount in
        defaultTrxOrderFiltering(
            "totalAmount.in=" + DEFAULT_TOTAL_AMOUNT + "," + UPDATED_TOTAL_AMOUNT,
            "totalAmount.in=" + UPDATED_TOTAL_AMOUNT
        );
    }

    @Test
    void getAllTrxOrdersByTotalAmountIsNullOrNotNull() {
        // Initialize the database
        insertedTrxOrder = trxOrderRepository.save(trxOrder).block();

        // Get all the trxOrderList where totalAmount is not null
        defaultTrxOrderFiltering("totalAmount.specified=true", "totalAmount.specified=false");
    }

    @Test
    void getAllTrxOrdersByTotalAmountIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxOrder = trxOrderRepository.save(trxOrder).block();

        // Get all the trxOrderList where totalAmount is greater than or equal to
        defaultTrxOrderFiltering(
            "totalAmount.greaterThanOrEqual=" + DEFAULT_TOTAL_AMOUNT,
            "totalAmount.greaterThanOrEqual=" + UPDATED_TOTAL_AMOUNT
        );
    }

    @Test
    void getAllTrxOrdersByTotalAmountIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxOrder = trxOrderRepository.save(trxOrder).block();

        // Get all the trxOrderList where totalAmount is less than or equal to
        defaultTrxOrderFiltering(
            "totalAmount.lessThanOrEqual=" + DEFAULT_TOTAL_AMOUNT,
            "totalAmount.lessThanOrEqual=" + SMALLER_TOTAL_AMOUNT
        );
    }

    @Test
    void getAllTrxOrdersByTotalAmountIsLessThanSomething() {
        // Initialize the database
        insertedTrxOrder = trxOrderRepository.save(trxOrder).block();

        // Get all the trxOrderList where totalAmount is less than
        defaultTrxOrderFiltering("totalAmount.lessThan=" + UPDATED_TOTAL_AMOUNT, "totalAmount.lessThan=" + DEFAULT_TOTAL_AMOUNT);
    }

    @Test
    void getAllTrxOrdersByTotalAmountIsGreaterThanSomething() {
        // Initialize the database
        insertedTrxOrder = trxOrderRepository.save(trxOrder).block();

        // Get all the trxOrderList where totalAmount is greater than
        defaultTrxOrderFiltering("totalAmount.greaterThan=" + SMALLER_TOTAL_AMOUNT, "totalAmount.greaterThan=" + DEFAULT_TOTAL_AMOUNT);
    }

    @Test
    void getAllTrxOrdersByMstCustomerIsEqualToSomething() {
        MstCustomer mstCustomer = MstCustomerResourceIT.createEntity(em);
        mstCustomerRepository.save(mstCustomer).block();
        Long mstCustomerId = mstCustomer.getId();
        trxOrder.setMstCustomerId(mstCustomerId);
        insertedTrxOrder = trxOrderRepository.save(trxOrder).block();
        // Get all the trxOrderList where mstCustomer equals to mstCustomerId
        defaultTrxOrderShouldBeFound("mstCustomerId.equals=" + mstCustomerId);

        // Get all the trxOrderList where mstCustomer equals to (mstCustomerId + 1)
        defaultTrxOrderShouldNotBeFound("mstCustomerId.equals=" + (mstCustomerId + 1));
    }

    private void defaultTrxOrderFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultTrxOrderShouldBeFound(shouldBeFound);
        defaultTrxOrderShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTrxOrderShouldBeFound(String filter) {
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
            .value(hasItem(trxOrder.getId().intValue()))
            .jsonPath("$.[*].orderDate")
            .value(hasItem(DEFAULT_ORDER_DATE.toString()))
            .jsonPath("$.[*].deliveryDate")
            .value(hasItem(DEFAULT_DELIVERY_DATE.toString()))
            .jsonPath("$.[*].orderStatus")
            .value(hasItem(DEFAULT_ORDER_STATUS.toString()))
            .jsonPath("$.[*].paymentMethod")
            .value(hasItem(DEFAULT_PAYMENT_METHOD.toString()))
            .jsonPath("$.[*].totalAmount")
            .value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT)));

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
    private void defaultTrxOrderShouldNotBeFound(String filter) {
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
    void getNonExistingTrxOrder() {
        // Get the trxOrder
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTrxOrder() throws Exception {
        // Initialize the database
        insertedTrxOrder = trxOrderRepository.save(trxOrder).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        trxOrderSearchRepository.save(trxOrder).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderSearchRepository.findAll().collectList().block());

        // Update the trxOrder
        TrxOrder updatedTrxOrder = trxOrderRepository.findById(trxOrder.getId()).block();
        updatedTrxOrder
            .orderDate(UPDATED_ORDER_DATE)
            .deliveryDate(UPDATED_DELIVERY_DATE)
            .orderStatus(UPDATED_ORDER_STATUS)
            .paymentMethod(UPDATED_PAYMENT_METHOD)
            .totalAmount(UPDATED_TOTAL_AMOUNT);
        TrxOrderDTO trxOrderDTO = trxOrderMapper.toDto(updatedTrxOrder);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trxOrderDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTrxOrderToMatchAllProperties(updatedTrxOrder);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TrxOrder> trxOrderSearchList = Streamable.of(trxOrderSearchRepository.findAll().collectList().block()).toList();
                TrxOrder testTrxOrderSearch = trxOrderSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertTrxOrderAllPropertiesEquals(testTrxOrderSearch, updatedTrxOrder);
                assertTrxOrderUpdatableFieldsEquals(testTrxOrderSearch, updatedTrxOrder);
            });
    }

    @Test
    void putNonExistingTrxOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderSearchRepository.findAll().collectList().block());
        trxOrder.setId(longCount.incrementAndGet());

        // Create the TrxOrder
        TrxOrderDTO trxOrderDTO = trxOrderMapper.toDto(trxOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trxOrderDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchTrxOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderSearchRepository.findAll().collectList().block());
        trxOrder.setId(longCount.incrementAndGet());

        // Create the TrxOrder
        TrxOrderDTO trxOrderDTO = trxOrderMapper.toDto(trxOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamTrxOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderSearchRepository.findAll().collectList().block());
        trxOrder.setId(longCount.incrementAndGet());

        // Create the TrxOrder
        TrxOrderDTO trxOrderDTO = trxOrderMapper.toDto(trxOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrxOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateTrxOrderWithPatch() throws Exception {
        // Initialize the database
        insertedTrxOrder = trxOrderRepository.save(trxOrder).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trxOrder using partial update
        TrxOrder partialUpdatedTrxOrder = new TrxOrder();
        partialUpdatedTrxOrder.setId(trxOrder.getId());

        partialUpdatedTrxOrder.orderDate(UPDATED_ORDER_DATE).deliveryDate(UPDATED_DELIVERY_DATE).paymentMethod(UPDATED_PAYMENT_METHOD);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrxOrder.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrxOrder))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxOrder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrxOrderUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTrxOrder, trxOrder), getPersistedTrxOrder(trxOrder));
    }

    @Test
    void fullUpdateTrxOrderWithPatch() throws Exception {
        // Initialize the database
        insertedTrxOrder = trxOrderRepository.save(trxOrder).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trxOrder using partial update
        TrxOrder partialUpdatedTrxOrder = new TrxOrder();
        partialUpdatedTrxOrder.setId(trxOrder.getId());

        partialUpdatedTrxOrder
            .orderDate(UPDATED_ORDER_DATE)
            .deliveryDate(UPDATED_DELIVERY_DATE)
            .orderStatus(UPDATED_ORDER_STATUS)
            .paymentMethod(UPDATED_PAYMENT_METHOD)
            .totalAmount(UPDATED_TOTAL_AMOUNT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrxOrder.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrxOrder))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxOrder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrxOrderUpdatableFieldsEquals(partialUpdatedTrxOrder, getPersistedTrxOrder(partialUpdatedTrxOrder));
    }

    @Test
    void patchNonExistingTrxOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderSearchRepository.findAll().collectList().block());
        trxOrder.setId(longCount.incrementAndGet());

        // Create the TrxOrder
        TrxOrderDTO trxOrderDTO = trxOrderMapper.toDto(trxOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, trxOrderDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchTrxOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderSearchRepository.findAll().collectList().block());
        trxOrder.setId(longCount.incrementAndGet());

        // Create the TrxOrder
        TrxOrderDTO trxOrderDTO = trxOrderMapper.toDto(trxOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamTrxOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderSearchRepository.findAll().collectList().block());
        trxOrder.setId(longCount.incrementAndGet());

        // Create the TrxOrder
        TrxOrderDTO trxOrderDTO = trxOrderMapper.toDto(trxOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxOrderDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrxOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteTrxOrder() {
        // Initialize the database
        insertedTrxOrder = trxOrderRepository.save(trxOrder).block();
        trxOrderRepository.save(trxOrder).block();
        trxOrderSearchRepository.save(trxOrder).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the trxOrder
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, trxOrder.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchTrxOrder() {
        // Initialize the database
        insertedTrxOrder = trxOrderRepository.save(trxOrder).block();
        trxOrderSearchRepository.save(trxOrder).block();

        // Search the trxOrder
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + trxOrder.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(trxOrder.getId().intValue()))
            .jsonPath("$.[*].orderDate")
            .value(hasItem(DEFAULT_ORDER_DATE.toString()))
            .jsonPath("$.[*].deliveryDate")
            .value(hasItem(DEFAULT_DELIVERY_DATE.toString()))
            .jsonPath("$.[*].orderStatus")
            .value(hasItem(DEFAULT_ORDER_STATUS.toString()))
            .jsonPath("$.[*].paymentMethod")
            .value(hasItem(DEFAULT_PAYMENT_METHOD.toString()))
            .jsonPath("$.[*].totalAmount")
            .value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT)));
    }

    protected long getRepositoryCount() {
        return trxOrderRepository.count().block();
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

    protected TrxOrder getPersistedTrxOrder(TrxOrder trxOrder) {
        return trxOrderRepository.findById(trxOrder.getId()).block();
    }

    protected void assertPersistedTrxOrderToMatchAllProperties(TrxOrder expectedTrxOrder) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrxOrderAllPropertiesEquals(expectedTrxOrder, getPersistedTrxOrder(expectedTrxOrder));
        assertTrxOrderUpdatableFieldsEquals(expectedTrxOrder, getPersistedTrxOrder(expectedTrxOrder));
    }

    protected void assertPersistedTrxOrderToMatchUpdatableProperties(TrxOrder expectedTrxOrder) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrxOrderAllUpdatablePropertiesEquals(expectedTrxOrder, getPersistedTrxOrder(expectedTrxOrder));
        assertTrxOrderUpdatableFieldsEquals(expectedTrxOrder, getPersistedTrxOrder(expectedTrxOrder));
    }
}
