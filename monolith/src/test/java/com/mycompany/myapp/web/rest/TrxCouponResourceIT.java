package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.TrxCouponAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.TrxCoupon;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.TrxCouponRepository;
import com.mycompany.myapp.repository.search.TrxCouponSearchRepository;
import com.mycompany.myapp.service.dto.TrxCouponDTO;
import com.mycompany.myapp.service.mapper.TrxCouponMapper;
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
 * Integration tests for the {@link TrxCouponResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TrxCouponResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_DISCOUNT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_DISCOUNT_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_DISCOUNT_AMOUNT = new BigDecimal(1 - 1);

    private static final Instant DEFAULT_VALID_UNTIL = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALID_UNTIL = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_MIN_PURCHASE = new BigDecimal(1);
    private static final BigDecimal UPDATED_MIN_PURCHASE = new BigDecimal(2);
    private static final BigDecimal SMALLER_MIN_PURCHASE = new BigDecimal(1 - 1);

    private static final String ENTITY_API_URL = "/api/trx-coupons";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/trx-coupons/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TrxCouponRepository trxCouponRepository;

    @Autowired
    private TrxCouponMapper trxCouponMapper;

    @Autowired
    private TrxCouponSearchRepository trxCouponSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TrxCoupon trxCoupon;

    private TrxCoupon insertedTrxCoupon;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrxCoupon createEntity(EntityManager em) {
        TrxCoupon trxCoupon = new TrxCoupon()
            .code(DEFAULT_CODE)
            .discountAmount(DEFAULT_DISCOUNT_AMOUNT)
            .validUntil(DEFAULT_VALID_UNTIL)
            .minPurchase(DEFAULT_MIN_PURCHASE);
        return trxCoupon;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrxCoupon createUpdatedEntity(EntityManager em) {
        TrxCoupon trxCoupon = new TrxCoupon()
            .code(UPDATED_CODE)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .validUntil(UPDATED_VALID_UNTIL)
            .minPurchase(UPDATED_MIN_PURCHASE);
        return trxCoupon;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TrxCoupon.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        trxCoupon = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedTrxCoupon != null) {
            trxCouponRepository.delete(insertedTrxCoupon).block();
            trxCouponSearchRepository.delete(insertedTrxCoupon).block();
            insertedTrxCoupon = null;
        }
        deleteEntities(em);
    }

    @Test
    void createTrxCoupon() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxCouponSearchRepository.findAll().collectList().block());
        // Create the TrxCoupon
        TrxCouponDTO trxCouponDTO = trxCouponMapper.toDto(trxCoupon);
        var returnedTrxCouponDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxCouponDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TrxCouponDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the TrxCoupon in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTrxCoupon = trxCouponMapper.toEntity(returnedTrxCouponDTO);
        assertTrxCouponUpdatableFieldsEquals(returnedTrxCoupon, getPersistedTrxCoupon(returnedTrxCoupon));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxCouponSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedTrxCoupon = returnedTrxCoupon;
    }

    @Test
    void createTrxCouponWithExistingId() throws Exception {
        // Create the TrxCoupon with an existing ID
        trxCoupon.setId(1L);
        TrxCouponDTO trxCouponDTO = trxCouponMapper.toDto(trxCoupon);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxCouponSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxCouponDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxCoupon in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxCouponSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxCouponSearchRepository.findAll().collectList().block());
        // set the field null
        trxCoupon.setCode(null);

        // Create the TrxCoupon, which fails.
        TrxCouponDTO trxCouponDTO = trxCouponMapper.toDto(trxCoupon);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxCouponDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxCouponSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkDiscountAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxCouponSearchRepository.findAll().collectList().block());
        // set the field null
        trxCoupon.setDiscountAmount(null);

        // Create the TrxCoupon, which fails.
        TrxCouponDTO trxCouponDTO = trxCouponMapper.toDto(trxCoupon);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxCouponDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxCouponSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkValidUntilIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxCouponSearchRepository.findAll().collectList().block());
        // set the field null
        trxCoupon.setValidUntil(null);

        // Create the TrxCoupon, which fails.
        TrxCouponDTO trxCouponDTO = trxCouponMapper.toDto(trxCoupon);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxCouponDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxCouponSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllTrxCoupons() {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();

        // Get all the trxCouponList
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
            .value(hasItem(trxCoupon.getId().intValue()))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].discountAmount")
            .value(hasItem(sameNumber(DEFAULT_DISCOUNT_AMOUNT)))
            .jsonPath("$.[*].validUntil")
            .value(hasItem(DEFAULT_VALID_UNTIL.toString()))
            .jsonPath("$.[*].minPurchase")
            .value(hasItem(sameNumber(DEFAULT_MIN_PURCHASE)));
    }

    @Test
    void getTrxCoupon() {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();

        // Get the trxCoupon
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, trxCoupon.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(trxCoupon.getId().intValue()))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE))
            .jsonPath("$.discountAmount")
            .value(is(sameNumber(DEFAULT_DISCOUNT_AMOUNT)))
            .jsonPath("$.validUntil")
            .value(is(DEFAULT_VALID_UNTIL.toString()))
            .jsonPath("$.minPurchase")
            .value(is(sameNumber(DEFAULT_MIN_PURCHASE)));
    }

    @Test
    void getTrxCouponsByIdFiltering() {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();

        Long id = trxCoupon.getId();

        defaultTrxCouponFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTrxCouponFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTrxCouponFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllTrxCouponsByCodeIsEqualToSomething() {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();

        // Get all the trxCouponList where code equals to
        defaultTrxCouponFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    void getAllTrxCouponsByCodeIsInShouldWork() {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();

        // Get all the trxCouponList where code in
        defaultTrxCouponFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    void getAllTrxCouponsByCodeIsNullOrNotNull() {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();

        // Get all the trxCouponList where code is not null
        defaultTrxCouponFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    void getAllTrxCouponsByCodeContainsSomething() {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();

        // Get all the trxCouponList where code contains
        defaultTrxCouponFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    void getAllTrxCouponsByCodeNotContainsSomething() {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();

        // Get all the trxCouponList where code does not contain
        defaultTrxCouponFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    void getAllTrxCouponsByDiscountAmountIsEqualToSomething() {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();

        // Get all the trxCouponList where discountAmount equals to
        defaultTrxCouponFiltering("discountAmount.equals=" + DEFAULT_DISCOUNT_AMOUNT, "discountAmount.equals=" + UPDATED_DISCOUNT_AMOUNT);
    }

    @Test
    void getAllTrxCouponsByDiscountAmountIsInShouldWork() {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();

        // Get all the trxCouponList where discountAmount in
        defaultTrxCouponFiltering(
            "discountAmount.in=" + DEFAULT_DISCOUNT_AMOUNT + "," + UPDATED_DISCOUNT_AMOUNT,
            "discountAmount.in=" + UPDATED_DISCOUNT_AMOUNT
        );
    }

    @Test
    void getAllTrxCouponsByDiscountAmountIsNullOrNotNull() {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();

        // Get all the trxCouponList where discountAmount is not null
        defaultTrxCouponFiltering("discountAmount.specified=true", "discountAmount.specified=false");
    }

    @Test
    void getAllTrxCouponsByDiscountAmountIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();

        // Get all the trxCouponList where discountAmount is greater than or equal to
        defaultTrxCouponFiltering(
            "discountAmount.greaterThanOrEqual=" + DEFAULT_DISCOUNT_AMOUNT,
            "discountAmount.greaterThanOrEqual=" + UPDATED_DISCOUNT_AMOUNT
        );
    }

    @Test
    void getAllTrxCouponsByDiscountAmountIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();

        // Get all the trxCouponList where discountAmount is less than or equal to
        defaultTrxCouponFiltering(
            "discountAmount.lessThanOrEqual=" + DEFAULT_DISCOUNT_AMOUNT,
            "discountAmount.lessThanOrEqual=" + SMALLER_DISCOUNT_AMOUNT
        );
    }

    @Test
    void getAllTrxCouponsByDiscountAmountIsLessThanSomething() {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();

        // Get all the trxCouponList where discountAmount is less than
        defaultTrxCouponFiltering(
            "discountAmount.lessThan=" + UPDATED_DISCOUNT_AMOUNT,
            "discountAmount.lessThan=" + DEFAULT_DISCOUNT_AMOUNT
        );
    }

    @Test
    void getAllTrxCouponsByDiscountAmountIsGreaterThanSomething() {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();

        // Get all the trxCouponList where discountAmount is greater than
        defaultTrxCouponFiltering(
            "discountAmount.greaterThan=" + SMALLER_DISCOUNT_AMOUNT,
            "discountAmount.greaterThan=" + DEFAULT_DISCOUNT_AMOUNT
        );
    }

    @Test
    void getAllTrxCouponsByValidUntilIsEqualToSomething() {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();

        // Get all the trxCouponList where validUntil equals to
        defaultTrxCouponFiltering("validUntil.equals=" + DEFAULT_VALID_UNTIL, "validUntil.equals=" + UPDATED_VALID_UNTIL);
    }

    @Test
    void getAllTrxCouponsByValidUntilIsInShouldWork() {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();

        // Get all the trxCouponList where validUntil in
        defaultTrxCouponFiltering(
            "validUntil.in=" + DEFAULT_VALID_UNTIL + "," + UPDATED_VALID_UNTIL,
            "validUntil.in=" + UPDATED_VALID_UNTIL
        );
    }

    @Test
    void getAllTrxCouponsByValidUntilIsNullOrNotNull() {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();

        // Get all the trxCouponList where validUntil is not null
        defaultTrxCouponFiltering("validUntil.specified=true", "validUntil.specified=false");
    }

    @Test
    void getAllTrxCouponsByMinPurchaseIsEqualToSomething() {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();

        // Get all the trxCouponList where minPurchase equals to
        defaultTrxCouponFiltering("minPurchase.equals=" + DEFAULT_MIN_PURCHASE, "minPurchase.equals=" + UPDATED_MIN_PURCHASE);
    }

    @Test
    void getAllTrxCouponsByMinPurchaseIsInShouldWork() {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();

        // Get all the trxCouponList where minPurchase in
        defaultTrxCouponFiltering(
            "minPurchase.in=" + DEFAULT_MIN_PURCHASE + "," + UPDATED_MIN_PURCHASE,
            "minPurchase.in=" + UPDATED_MIN_PURCHASE
        );
    }

    @Test
    void getAllTrxCouponsByMinPurchaseIsNullOrNotNull() {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();

        // Get all the trxCouponList where minPurchase is not null
        defaultTrxCouponFiltering("minPurchase.specified=true", "minPurchase.specified=false");
    }

    @Test
    void getAllTrxCouponsByMinPurchaseIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();

        // Get all the trxCouponList where minPurchase is greater than or equal to
        defaultTrxCouponFiltering(
            "minPurchase.greaterThanOrEqual=" + DEFAULT_MIN_PURCHASE,
            "minPurchase.greaterThanOrEqual=" + UPDATED_MIN_PURCHASE
        );
    }

    @Test
    void getAllTrxCouponsByMinPurchaseIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();

        // Get all the trxCouponList where minPurchase is less than or equal to
        defaultTrxCouponFiltering(
            "minPurchase.lessThanOrEqual=" + DEFAULT_MIN_PURCHASE,
            "minPurchase.lessThanOrEqual=" + SMALLER_MIN_PURCHASE
        );
    }

    @Test
    void getAllTrxCouponsByMinPurchaseIsLessThanSomething() {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();

        // Get all the trxCouponList where minPurchase is less than
        defaultTrxCouponFiltering("minPurchase.lessThan=" + UPDATED_MIN_PURCHASE, "minPurchase.lessThan=" + DEFAULT_MIN_PURCHASE);
    }

    @Test
    void getAllTrxCouponsByMinPurchaseIsGreaterThanSomething() {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();

        // Get all the trxCouponList where minPurchase is greater than
        defaultTrxCouponFiltering("minPurchase.greaterThan=" + SMALLER_MIN_PURCHASE, "minPurchase.greaterThan=" + DEFAULT_MIN_PURCHASE);
    }

    private void defaultTrxCouponFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultTrxCouponShouldBeFound(shouldBeFound);
        defaultTrxCouponShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTrxCouponShouldBeFound(String filter) {
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
            .value(hasItem(trxCoupon.getId().intValue()))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].discountAmount")
            .value(hasItem(sameNumber(DEFAULT_DISCOUNT_AMOUNT)))
            .jsonPath("$.[*].validUntil")
            .value(hasItem(DEFAULT_VALID_UNTIL.toString()))
            .jsonPath("$.[*].minPurchase")
            .value(hasItem(sameNumber(DEFAULT_MIN_PURCHASE)));

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
    private void defaultTrxCouponShouldNotBeFound(String filter) {
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
    void getNonExistingTrxCoupon() {
        // Get the trxCoupon
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTrxCoupon() throws Exception {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        trxCouponSearchRepository.save(trxCoupon).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxCouponSearchRepository.findAll().collectList().block());

        // Update the trxCoupon
        TrxCoupon updatedTrxCoupon = trxCouponRepository.findById(trxCoupon.getId()).block();
        updatedTrxCoupon
            .code(UPDATED_CODE)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .validUntil(UPDATED_VALID_UNTIL)
            .minPurchase(UPDATED_MIN_PURCHASE);
        TrxCouponDTO trxCouponDTO = trxCouponMapper.toDto(updatedTrxCoupon);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trxCouponDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxCouponDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxCoupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTrxCouponToMatchAllProperties(updatedTrxCoupon);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxCouponSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TrxCoupon> trxCouponSearchList = Streamable.of(trxCouponSearchRepository.findAll().collectList().block()).toList();
                TrxCoupon testTrxCouponSearch = trxCouponSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertTrxCouponAllPropertiesEquals(testTrxCouponSearch, updatedTrxCoupon);
                assertTrxCouponUpdatableFieldsEquals(testTrxCouponSearch, updatedTrxCoupon);
            });
    }

    @Test
    void putNonExistingTrxCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxCouponSearchRepository.findAll().collectList().block());
        trxCoupon.setId(longCount.incrementAndGet());

        // Create the TrxCoupon
        TrxCouponDTO trxCouponDTO = trxCouponMapper.toDto(trxCoupon);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trxCouponDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxCouponDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxCoupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxCouponSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchTrxCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxCouponSearchRepository.findAll().collectList().block());
        trxCoupon.setId(longCount.incrementAndGet());

        // Create the TrxCoupon
        TrxCouponDTO trxCouponDTO = trxCouponMapper.toDto(trxCoupon);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxCouponDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxCoupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxCouponSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamTrxCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxCouponSearchRepository.findAll().collectList().block());
        trxCoupon.setId(longCount.incrementAndGet());

        // Create the TrxCoupon
        TrxCouponDTO trxCouponDTO = trxCouponMapper.toDto(trxCoupon);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxCouponDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrxCoupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxCouponSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateTrxCouponWithPatch() throws Exception {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trxCoupon using partial update
        TrxCoupon partialUpdatedTrxCoupon = new TrxCoupon();
        partialUpdatedTrxCoupon.setId(trxCoupon.getId());

        partialUpdatedTrxCoupon.validUntil(UPDATED_VALID_UNTIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrxCoupon.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrxCoupon))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxCoupon in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrxCouponUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTrxCoupon, trxCoupon),
            getPersistedTrxCoupon(trxCoupon)
        );
    }

    @Test
    void fullUpdateTrxCouponWithPatch() throws Exception {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trxCoupon using partial update
        TrxCoupon partialUpdatedTrxCoupon = new TrxCoupon();
        partialUpdatedTrxCoupon.setId(trxCoupon.getId());

        partialUpdatedTrxCoupon
            .code(UPDATED_CODE)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .validUntil(UPDATED_VALID_UNTIL)
            .minPurchase(UPDATED_MIN_PURCHASE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrxCoupon.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrxCoupon))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxCoupon in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrxCouponUpdatableFieldsEquals(partialUpdatedTrxCoupon, getPersistedTrxCoupon(partialUpdatedTrxCoupon));
    }

    @Test
    void patchNonExistingTrxCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxCouponSearchRepository.findAll().collectList().block());
        trxCoupon.setId(longCount.incrementAndGet());

        // Create the TrxCoupon
        TrxCouponDTO trxCouponDTO = trxCouponMapper.toDto(trxCoupon);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, trxCouponDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxCouponDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxCoupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxCouponSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchTrxCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxCouponSearchRepository.findAll().collectList().block());
        trxCoupon.setId(longCount.incrementAndGet());

        // Create the TrxCoupon
        TrxCouponDTO trxCouponDTO = trxCouponMapper.toDto(trxCoupon);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxCouponDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxCoupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxCouponSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamTrxCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxCouponSearchRepository.findAll().collectList().block());
        trxCoupon.setId(longCount.incrementAndGet());

        // Create the TrxCoupon
        TrxCouponDTO trxCouponDTO = trxCouponMapper.toDto(trxCoupon);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxCouponDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrxCoupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxCouponSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteTrxCoupon() {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();
        trxCouponRepository.save(trxCoupon).block();
        trxCouponSearchRepository.save(trxCoupon).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxCouponSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the trxCoupon
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, trxCoupon.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxCouponSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchTrxCoupon() {
        // Initialize the database
        insertedTrxCoupon = trxCouponRepository.save(trxCoupon).block();
        trxCouponSearchRepository.save(trxCoupon).block();

        // Search the trxCoupon
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + trxCoupon.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(trxCoupon.getId().intValue()))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].discountAmount")
            .value(hasItem(sameNumber(DEFAULT_DISCOUNT_AMOUNT)))
            .jsonPath("$.[*].validUntil")
            .value(hasItem(DEFAULT_VALID_UNTIL.toString()))
            .jsonPath("$.[*].minPurchase")
            .value(hasItem(sameNumber(DEFAULT_MIN_PURCHASE)));
    }

    protected long getRepositoryCount() {
        return trxCouponRepository.count().block();
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

    protected TrxCoupon getPersistedTrxCoupon(TrxCoupon trxCoupon) {
        return trxCouponRepository.findById(trxCoupon.getId()).block();
    }

    protected void assertPersistedTrxCouponToMatchAllProperties(TrxCoupon expectedTrxCoupon) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrxCouponAllPropertiesEquals(expectedTrxCoupon, getPersistedTrxCoupon(expectedTrxCoupon));
        assertTrxCouponUpdatableFieldsEquals(expectedTrxCoupon, getPersistedTrxCoupon(expectedTrxCoupon));
    }

    protected void assertPersistedTrxCouponToMatchUpdatableProperties(TrxCoupon expectedTrxCoupon) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrxCouponAllUpdatablePropertiesEquals(expectedTrxCoupon, getPersistedTrxCoupon(expectedTrxCoupon));
        assertTrxCouponUpdatableFieldsEquals(expectedTrxCoupon, getPersistedTrxCoupon(expectedTrxCoupon));
    }
}
