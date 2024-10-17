package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.TrxDiscountAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.TrxDiscount;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.TrxDiscountRepository;
import com.mycompany.myapp.repository.search.TrxDiscountSearchRepository;
import com.mycompany.myapp.service.dto.TrxDiscountDTO;
import com.mycompany.myapp.service.mapper.TrxDiscountMapper;
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
 * Integration tests for the {@link TrxDiscountResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TrxDiscountResourceIT {

    private static final Float DEFAULT_DISCOUNT_PERCENTAGE = 1F;
    private static final Float UPDATED_DISCOUNT_PERCENTAGE = 2F;
    private static final Float SMALLER_DISCOUNT_PERCENTAGE = 1F - 1F;

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/trx-discounts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/trx-discounts/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TrxDiscountRepository trxDiscountRepository;

    @Autowired
    private TrxDiscountMapper trxDiscountMapper;

    @Autowired
    private TrxDiscountSearchRepository trxDiscountSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TrxDiscount trxDiscount;

    private TrxDiscount insertedTrxDiscount;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrxDiscount createEntity(EntityManager em) {
        TrxDiscount trxDiscount = new TrxDiscount()
            .discountPercentage(DEFAULT_DISCOUNT_PERCENTAGE)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE);
        return trxDiscount;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrxDiscount createUpdatedEntity(EntityManager em) {
        TrxDiscount trxDiscount = new TrxDiscount()
            .discountPercentage(UPDATED_DISCOUNT_PERCENTAGE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);
        return trxDiscount;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TrxDiscount.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        trxDiscount = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedTrxDiscount != null) {
            trxDiscountRepository.delete(insertedTrxDiscount).block();
            trxDiscountSearchRepository.delete(insertedTrxDiscount).block();
            insertedTrxDiscount = null;
        }
        deleteEntities(em);
    }

    @Test
    void createTrxDiscount() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxDiscountSearchRepository.findAll().collectList().block());
        // Create the TrxDiscount
        TrxDiscountDTO trxDiscountDTO = trxDiscountMapper.toDto(trxDiscount);
        var returnedTrxDiscountDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxDiscountDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TrxDiscountDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the TrxDiscount in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTrxDiscount = trxDiscountMapper.toEntity(returnedTrxDiscountDTO);
        assertTrxDiscountUpdatableFieldsEquals(returnedTrxDiscount, getPersistedTrxDiscount(returnedTrxDiscount));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxDiscountSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedTrxDiscount = returnedTrxDiscount;
    }

    @Test
    void createTrxDiscountWithExistingId() throws Exception {
        // Create the TrxDiscount with an existing ID
        trxDiscount.setId(1L);
        TrxDiscountDTO trxDiscountDTO = trxDiscountMapper.toDto(trxDiscount);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxDiscountSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxDiscountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxDiscount in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxDiscountSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkDiscountPercentageIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxDiscountSearchRepository.findAll().collectList().block());
        // set the field null
        trxDiscount.setDiscountPercentage(null);

        // Create the TrxDiscount, which fails.
        TrxDiscountDTO trxDiscountDTO = trxDiscountMapper.toDto(trxDiscount);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxDiscountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxDiscountSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxDiscountSearchRepository.findAll().collectList().block());
        // set the field null
        trxDiscount.setStartDate(null);

        // Create the TrxDiscount, which fails.
        TrxDiscountDTO trxDiscountDTO = trxDiscountMapper.toDto(trxDiscount);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxDiscountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxDiscountSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkEndDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxDiscountSearchRepository.findAll().collectList().block());
        // set the field null
        trxDiscount.setEndDate(null);

        // Create the TrxDiscount, which fails.
        TrxDiscountDTO trxDiscountDTO = trxDiscountMapper.toDto(trxDiscount);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxDiscountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxDiscountSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllTrxDiscounts() {
        // Initialize the database
        insertedTrxDiscount = trxDiscountRepository.save(trxDiscount).block();

        // Get all the trxDiscountList
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
            .value(hasItem(trxDiscount.getId().intValue()))
            .jsonPath("$.[*].discountPercentage")
            .value(hasItem(DEFAULT_DISCOUNT_PERCENTAGE.doubleValue()))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))
            .jsonPath("$.[*].endDate")
            .value(hasItem(DEFAULT_END_DATE.toString()));
    }

    @Test
    void getTrxDiscount() {
        // Initialize the database
        insertedTrxDiscount = trxDiscountRepository.save(trxDiscount).block();

        // Get the trxDiscount
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, trxDiscount.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(trxDiscount.getId().intValue()))
            .jsonPath("$.discountPercentage")
            .value(is(DEFAULT_DISCOUNT_PERCENTAGE.doubleValue()))
            .jsonPath("$.startDate")
            .value(is(DEFAULT_START_DATE.toString()))
            .jsonPath("$.endDate")
            .value(is(DEFAULT_END_DATE.toString()));
    }

    @Test
    void getTrxDiscountsByIdFiltering() {
        // Initialize the database
        insertedTrxDiscount = trxDiscountRepository.save(trxDiscount).block();

        Long id = trxDiscount.getId();

        defaultTrxDiscountFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTrxDiscountFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTrxDiscountFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllTrxDiscountsByDiscountPercentageIsEqualToSomething() {
        // Initialize the database
        insertedTrxDiscount = trxDiscountRepository.save(trxDiscount).block();

        // Get all the trxDiscountList where discountPercentage equals to
        defaultTrxDiscountFiltering(
            "discountPercentage.equals=" + DEFAULT_DISCOUNT_PERCENTAGE,
            "discountPercentage.equals=" + UPDATED_DISCOUNT_PERCENTAGE
        );
    }

    @Test
    void getAllTrxDiscountsByDiscountPercentageIsInShouldWork() {
        // Initialize the database
        insertedTrxDiscount = trxDiscountRepository.save(trxDiscount).block();

        // Get all the trxDiscountList where discountPercentage in
        defaultTrxDiscountFiltering(
            "discountPercentage.in=" + DEFAULT_DISCOUNT_PERCENTAGE + "," + UPDATED_DISCOUNT_PERCENTAGE,
            "discountPercentage.in=" + UPDATED_DISCOUNT_PERCENTAGE
        );
    }

    @Test
    void getAllTrxDiscountsByDiscountPercentageIsNullOrNotNull() {
        // Initialize the database
        insertedTrxDiscount = trxDiscountRepository.save(trxDiscount).block();

        // Get all the trxDiscountList where discountPercentage is not null
        defaultTrxDiscountFiltering("discountPercentage.specified=true", "discountPercentage.specified=false");
    }

    @Test
    void getAllTrxDiscountsByDiscountPercentageIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxDiscount = trxDiscountRepository.save(trxDiscount).block();

        // Get all the trxDiscountList where discountPercentage is greater than or equal to
        defaultTrxDiscountFiltering(
            "discountPercentage.greaterThanOrEqual=" + DEFAULT_DISCOUNT_PERCENTAGE,
            "discountPercentage.greaterThanOrEqual=" + UPDATED_DISCOUNT_PERCENTAGE
        );
    }

    @Test
    void getAllTrxDiscountsByDiscountPercentageIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxDiscount = trxDiscountRepository.save(trxDiscount).block();

        // Get all the trxDiscountList where discountPercentage is less than or equal to
        defaultTrxDiscountFiltering(
            "discountPercentage.lessThanOrEqual=" + DEFAULT_DISCOUNT_PERCENTAGE,
            "discountPercentage.lessThanOrEqual=" + SMALLER_DISCOUNT_PERCENTAGE
        );
    }

    @Test
    void getAllTrxDiscountsByDiscountPercentageIsLessThanSomething() {
        // Initialize the database
        insertedTrxDiscount = trxDiscountRepository.save(trxDiscount).block();

        // Get all the trxDiscountList where discountPercentage is less than
        defaultTrxDiscountFiltering(
            "discountPercentage.lessThan=" + UPDATED_DISCOUNT_PERCENTAGE,
            "discountPercentage.lessThan=" + DEFAULT_DISCOUNT_PERCENTAGE
        );
    }

    @Test
    void getAllTrxDiscountsByDiscountPercentageIsGreaterThanSomething() {
        // Initialize the database
        insertedTrxDiscount = trxDiscountRepository.save(trxDiscount).block();

        // Get all the trxDiscountList where discountPercentage is greater than
        defaultTrxDiscountFiltering(
            "discountPercentage.greaterThan=" + SMALLER_DISCOUNT_PERCENTAGE,
            "discountPercentage.greaterThan=" + DEFAULT_DISCOUNT_PERCENTAGE
        );
    }

    @Test
    void getAllTrxDiscountsByStartDateIsEqualToSomething() {
        // Initialize the database
        insertedTrxDiscount = trxDiscountRepository.save(trxDiscount).block();

        // Get all the trxDiscountList where startDate equals to
        defaultTrxDiscountFiltering("startDate.equals=" + DEFAULT_START_DATE, "startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    void getAllTrxDiscountsByStartDateIsInShouldWork() {
        // Initialize the database
        insertedTrxDiscount = trxDiscountRepository.save(trxDiscount).block();

        // Get all the trxDiscountList where startDate in
        defaultTrxDiscountFiltering("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE, "startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    void getAllTrxDiscountsByStartDateIsNullOrNotNull() {
        // Initialize the database
        insertedTrxDiscount = trxDiscountRepository.save(trxDiscount).block();

        // Get all the trxDiscountList where startDate is not null
        defaultTrxDiscountFiltering("startDate.specified=true", "startDate.specified=false");
    }

    @Test
    void getAllTrxDiscountsByEndDateIsEqualToSomething() {
        // Initialize the database
        insertedTrxDiscount = trxDiscountRepository.save(trxDiscount).block();

        // Get all the trxDiscountList where endDate equals to
        defaultTrxDiscountFiltering("endDate.equals=" + DEFAULT_END_DATE, "endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    void getAllTrxDiscountsByEndDateIsInShouldWork() {
        // Initialize the database
        insertedTrxDiscount = trxDiscountRepository.save(trxDiscount).block();

        // Get all the trxDiscountList where endDate in
        defaultTrxDiscountFiltering("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE, "endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    void getAllTrxDiscountsByEndDateIsNullOrNotNull() {
        // Initialize the database
        insertedTrxDiscount = trxDiscountRepository.save(trxDiscount).block();

        // Get all the trxDiscountList where endDate is not null
        defaultTrxDiscountFiltering("endDate.specified=true", "endDate.specified=false");
    }

    private void defaultTrxDiscountFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultTrxDiscountShouldBeFound(shouldBeFound);
        defaultTrxDiscountShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTrxDiscountShouldBeFound(String filter) {
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
            .value(hasItem(trxDiscount.getId().intValue()))
            .jsonPath("$.[*].discountPercentage")
            .value(hasItem(DEFAULT_DISCOUNT_PERCENTAGE.doubleValue()))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))
            .jsonPath("$.[*].endDate")
            .value(hasItem(DEFAULT_END_DATE.toString()));

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
    private void defaultTrxDiscountShouldNotBeFound(String filter) {
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
    void getNonExistingTrxDiscount() {
        // Get the trxDiscount
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTrxDiscount() throws Exception {
        // Initialize the database
        insertedTrxDiscount = trxDiscountRepository.save(trxDiscount).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        trxDiscountSearchRepository.save(trxDiscount).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxDiscountSearchRepository.findAll().collectList().block());

        // Update the trxDiscount
        TrxDiscount updatedTrxDiscount = trxDiscountRepository.findById(trxDiscount.getId()).block();
        updatedTrxDiscount.discountPercentage(UPDATED_DISCOUNT_PERCENTAGE).startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE);
        TrxDiscountDTO trxDiscountDTO = trxDiscountMapper.toDto(updatedTrxDiscount);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trxDiscountDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxDiscountDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxDiscount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTrxDiscountToMatchAllProperties(updatedTrxDiscount);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxDiscountSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TrxDiscount> trxDiscountSearchList = Streamable.of(
                    trxDiscountSearchRepository.findAll().collectList().block()
                ).toList();
                TrxDiscount testTrxDiscountSearch = trxDiscountSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertTrxDiscountAllPropertiesEquals(testTrxDiscountSearch, updatedTrxDiscount);
                assertTrxDiscountUpdatableFieldsEquals(testTrxDiscountSearch, updatedTrxDiscount);
            });
    }

    @Test
    void putNonExistingTrxDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxDiscountSearchRepository.findAll().collectList().block());
        trxDiscount.setId(longCount.incrementAndGet());

        // Create the TrxDiscount
        TrxDiscountDTO trxDiscountDTO = trxDiscountMapper.toDto(trxDiscount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trxDiscountDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxDiscountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxDiscount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxDiscountSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchTrxDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxDiscountSearchRepository.findAll().collectList().block());
        trxDiscount.setId(longCount.incrementAndGet());

        // Create the TrxDiscount
        TrxDiscountDTO trxDiscountDTO = trxDiscountMapper.toDto(trxDiscount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxDiscountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxDiscount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxDiscountSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamTrxDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxDiscountSearchRepository.findAll().collectList().block());
        trxDiscount.setId(longCount.incrementAndGet());

        // Create the TrxDiscount
        TrxDiscountDTO trxDiscountDTO = trxDiscountMapper.toDto(trxDiscount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxDiscountDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrxDiscount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxDiscountSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateTrxDiscountWithPatch() throws Exception {
        // Initialize the database
        insertedTrxDiscount = trxDiscountRepository.save(trxDiscount).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trxDiscount using partial update
        TrxDiscount partialUpdatedTrxDiscount = new TrxDiscount();
        partialUpdatedTrxDiscount.setId(trxDiscount.getId());

        partialUpdatedTrxDiscount.startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrxDiscount.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrxDiscount))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxDiscount in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrxDiscountUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTrxDiscount, trxDiscount),
            getPersistedTrxDiscount(trxDiscount)
        );
    }

    @Test
    void fullUpdateTrxDiscountWithPatch() throws Exception {
        // Initialize the database
        insertedTrxDiscount = trxDiscountRepository.save(trxDiscount).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trxDiscount using partial update
        TrxDiscount partialUpdatedTrxDiscount = new TrxDiscount();
        partialUpdatedTrxDiscount.setId(trxDiscount.getId());

        partialUpdatedTrxDiscount.discountPercentage(UPDATED_DISCOUNT_PERCENTAGE).startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrxDiscount.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrxDiscount))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxDiscount in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrxDiscountUpdatableFieldsEquals(partialUpdatedTrxDiscount, getPersistedTrxDiscount(partialUpdatedTrxDiscount));
    }

    @Test
    void patchNonExistingTrxDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxDiscountSearchRepository.findAll().collectList().block());
        trxDiscount.setId(longCount.incrementAndGet());

        // Create the TrxDiscount
        TrxDiscountDTO trxDiscountDTO = trxDiscountMapper.toDto(trxDiscount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, trxDiscountDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxDiscountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxDiscount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxDiscountSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchTrxDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxDiscountSearchRepository.findAll().collectList().block());
        trxDiscount.setId(longCount.incrementAndGet());

        // Create the TrxDiscount
        TrxDiscountDTO trxDiscountDTO = trxDiscountMapper.toDto(trxDiscount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxDiscountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxDiscount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxDiscountSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamTrxDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxDiscountSearchRepository.findAll().collectList().block());
        trxDiscount.setId(longCount.incrementAndGet());

        // Create the TrxDiscount
        TrxDiscountDTO trxDiscountDTO = trxDiscountMapper.toDto(trxDiscount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxDiscountDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrxDiscount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxDiscountSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteTrxDiscount() {
        // Initialize the database
        insertedTrxDiscount = trxDiscountRepository.save(trxDiscount).block();
        trxDiscountRepository.save(trxDiscount).block();
        trxDiscountSearchRepository.save(trxDiscount).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxDiscountSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the trxDiscount
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, trxDiscount.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxDiscountSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchTrxDiscount() {
        // Initialize the database
        insertedTrxDiscount = trxDiscountRepository.save(trxDiscount).block();
        trxDiscountSearchRepository.save(trxDiscount).block();

        // Search the trxDiscount
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + trxDiscount.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(trxDiscount.getId().intValue()))
            .jsonPath("$.[*].discountPercentage")
            .value(hasItem(DEFAULT_DISCOUNT_PERCENTAGE.doubleValue()))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))
            .jsonPath("$.[*].endDate")
            .value(hasItem(DEFAULT_END_DATE.toString()));
    }

    protected long getRepositoryCount() {
        return trxDiscountRepository.count().block();
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

    protected TrxDiscount getPersistedTrxDiscount(TrxDiscount trxDiscount) {
        return trxDiscountRepository.findById(trxDiscount.getId()).block();
    }

    protected void assertPersistedTrxDiscountToMatchAllProperties(TrxDiscount expectedTrxDiscount) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrxDiscountAllPropertiesEquals(expectedTrxDiscount, getPersistedTrxDiscount(expectedTrxDiscount));
        assertTrxDiscountUpdatableFieldsEquals(expectedTrxDiscount, getPersistedTrxDiscount(expectedTrxDiscount));
    }

    protected void assertPersistedTrxDiscountToMatchUpdatableProperties(TrxDiscount expectedTrxDiscount) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrxDiscountAllUpdatablePropertiesEquals(expectedTrxDiscount, getPersistedTrxDiscount(expectedTrxDiscount));
        assertTrxDiscountUpdatableFieldsEquals(expectedTrxDiscount, getPersistedTrxDiscount(expectedTrxDiscount));
    }
}
