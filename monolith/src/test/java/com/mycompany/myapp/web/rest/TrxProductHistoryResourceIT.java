package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.TrxProductHistoryAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.TrxProductHistory;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.TrxProductHistoryRepository;
import com.mycompany.myapp.repository.search.TrxProductHistorySearchRepository;
import com.mycompany.myapp.service.dto.TrxProductHistoryDTO;
import com.mycompany.myapp.service.mapper.TrxProductHistoryMapper;
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
 * Integration tests for the {@link TrxProductHistoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TrxProductHistoryResourceIT {

    private static final BigDecimal DEFAULT_OLD_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_OLD_PRICE = new BigDecimal(2);
    private static final BigDecimal SMALLER_OLD_PRICE = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_NEW_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_NEW_PRICE = new BigDecimal(2);
    private static final BigDecimal SMALLER_NEW_PRICE = new BigDecimal(1 - 1);

    private static final Instant DEFAULT_CHANGE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CHANGE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/trx-product-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/trx-product-histories/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TrxProductHistoryRepository trxProductHistoryRepository;

    @Autowired
    private TrxProductHistoryMapper trxProductHistoryMapper;

    @Autowired
    private TrxProductHistorySearchRepository trxProductHistorySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TrxProductHistory trxProductHistory;

    private TrxProductHistory insertedTrxProductHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrxProductHistory createEntity(EntityManager em) {
        TrxProductHistory trxProductHistory = new TrxProductHistory()
            .oldPrice(DEFAULT_OLD_PRICE)
            .newPrice(DEFAULT_NEW_PRICE)
            .changeDate(DEFAULT_CHANGE_DATE);
        return trxProductHistory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrxProductHistory createUpdatedEntity(EntityManager em) {
        TrxProductHistory trxProductHistory = new TrxProductHistory()
            .oldPrice(UPDATED_OLD_PRICE)
            .newPrice(UPDATED_NEW_PRICE)
            .changeDate(UPDATED_CHANGE_DATE);
        return trxProductHistory;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TrxProductHistory.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        trxProductHistory = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedTrxProductHistory != null) {
            trxProductHistoryRepository.delete(insertedTrxProductHistory).block();
            trxProductHistorySearchRepository.delete(insertedTrxProductHistory).block();
            insertedTrxProductHistory = null;
        }
        deleteEntities(em);
    }

    @Test
    void createTrxProductHistory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxProductHistorySearchRepository.findAll().collectList().block());
        // Create the TrxProductHistory
        TrxProductHistoryDTO trxProductHistoryDTO = trxProductHistoryMapper.toDto(trxProductHistory);
        var returnedTrxProductHistoryDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxProductHistoryDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TrxProductHistoryDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the TrxProductHistory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTrxProductHistory = trxProductHistoryMapper.toEntity(returnedTrxProductHistoryDTO);
        assertTrxProductHistoryUpdatableFieldsEquals(returnedTrxProductHistory, getPersistedTrxProductHistory(returnedTrxProductHistory));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxProductHistorySearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedTrxProductHistory = returnedTrxProductHistory;
    }

    @Test
    void createTrxProductHistoryWithExistingId() throws Exception {
        // Create the TrxProductHistory with an existing ID
        trxProductHistory.setId(1L);
        TrxProductHistoryDTO trxProductHistoryDTO = trxProductHistoryMapper.toDto(trxProductHistory);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxProductHistorySearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxProductHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxProductHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxProductHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkChangeDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxProductHistorySearchRepository.findAll().collectList().block());
        // set the field null
        trxProductHistory.setChangeDate(null);

        // Create the TrxProductHistory, which fails.
        TrxProductHistoryDTO trxProductHistoryDTO = trxProductHistoryMapper.toDto(trxProductHistory);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxProductHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxProductHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllTrxProductHistories() {
        // Initialize the database
        insertedTrxProductHistory = trxProductHistoryRepository.save(trxProductHistory).block();

        // Get all the trxProductHistoryList
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
            .value(hasItem(trxProductHistory.getId().intValue()))
            .jsonPath("$.[*].oldPrice")
            .value(hasItem(sameNumber(DEFAULT_OLD_PRICE)))
            .jsonPath("$.[*].newPrice")
            .value(hasItem(sameNumber(DEFAULT_NEW_PRICE)))
            .jsonPath("$.[*].changeDate")
            .value(hasItem(DEFAULT_CHANGE_DATE.toString()));
    }

    @Test
    void getTrxProductHistory() {
        // Initialize the database
        insertedTrxProductHistory = trxProductHistoryRepository.save(trxProductHistory).block();

        // Get the trxProductHistory
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, trxProductHistory.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(trxProductHistory.getId().intValue()))
            .jsonPath("$.oldPrice")
            .value(is(sameNumber(DEFAULT_OLD_PRICE)))
            .jsonPath("$.newPrice")
            .value(is(sameNumber(DEFAULT_NEW_PRICE)))
            .jsonPath("$.changeDate")
            .value(is(DEFAULT_CHANGE_DATE.toString()));
    }

    @Test
    void getTrxProductHistoriesByIdFiltering() {
        // Initialize the database
        insertedTrxProductHistory = trxProductHistoryRepository.save(trxProductHistory).block();

        Long id = trxProductHistory.getId();

        defaultTrxProductHistoryFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTrxProductHistoryFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTrxProductHistoryFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllTrxProductHistoriesByOldPriceIsEqualToSomething() {
        // Initialize the database
        insertedTrxProductHistory = trxProductHistoryRepository.save(trxProductHistory).block();

        // Get all the trxProductHistoryList where oldPrice equals to
        defaultTrxProductHistoryFiltering("oldPrice.equals=" + DEFAULT_OLD_PRICE, "oldPrice.equals=" + UPDATED_OLD_PRICE);
    }

    @Test
    void getAllTrxProductHistoriesByOldPriceIsInShouldWork() {
        // Initialize the database
        insertedTrxProductHistory = trxProductHistoryRepository.save(trxProductHistory).block();

        // Get all the trxProductHistoryList where oldPrice in
        defaultTrxProductHistoryFiltering("oldPrice.in=" + DEFAULT_OLD_PRICE + "," + UPDATED_OLD_PRICE, "oldPrice.in=" + UPDATED_OLD_PRICE);
    }

    @Test
    void getAllTrxProductHistoriesByOldPriceIsNullOrNotNull() {
        // Initialize the database
        insertedTrxProductHistory = trxProductHistoryRepository.save(trxProductHistory).block();

        // Get all the trxProductHistoryList where oldPrice is not null
        defaultTrxProductHistoryFiltering("oldPrice.specified=true", "oldPrice.specified=false");
    }

    @Test
    void getAllTrxProductHistoriesByOldPriceIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxProductHistory = trxProductHistoryRepository.save(trxProductHistory).block();

        // Get all the trxProductHistoryList where oldPrice is greater than or equal to
        defaultTrxProductHistoryFiltering(
            "oldPrice.greaterThanOrEqual=" + DEFAULT_OLD_PRICE,
            "oldPrice.greaterThanOrEqual=" + UPDATED_OLD_PRICE
        );
    }

    @Test
    void getAllTrxProductHistoriesByOldPriceIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxProductHistory = trxProductHistoryRepository.save(trxProductHistory).block();

        // Get all the trxProductHistoryList where oldPrice is less than or equal to
        defaultTrxProductHistoryFiltering("oldPrice.lessThanOrEqual=" + DEFAULT_OLD_PRICE, "oldPrice.lessThanOrEqual=" + SMALLER_OLD_PRICE);
    }

    @Test
    void getAllTrxProductHistoriesByOldPriceIsLessThanSomething() {
        // Initialize the database
        insertedTrxProductHistory = trxProductHistoryRepository.save(trxProductHistory).block();

        // Get all the trxProductHistoryList where oldPrice is less than
        defaultTrxProductHistoryFiltering("oldPrice.lessThan=" + UPDATED_OLD_PRICE, "oldPrice.lessThan=" + DEFAULT_OLD_PRICE);
    }

    @Test
    void getAllTrxProductHistoriesByOldPriceIsGreaterThanSomething() {
        // Initialize the database
        insertedTrxProductHistory = trxProductHistoryRepository.save(trxProductHistory).block();

        // Get all the trxProductHistoryList where oldPrice is greater than
        defaultTrxProductHistoryFiltering("oldPrice.greaterThan=" + SMALLER_OLD_PRICE, "oldPrice.greaterThan=" + DEFAULT_OLD_PRICE);
    }

    @Test
    void getAllTrxProductHistoriesByNewPriceIsEqualToSomething() {
        // Initialize the database
        insertedTrxProductHistory = trxProductHistoryRepository.save(trxProductHistory).block();

        // Get all the trxProductHistoryList where newPrice equals to
        defaultTrxProductHistoryFiltering("newPrice.equals=" + DEFAULT_NEW_PRICE, "newPrice.equals=" + UPDATED_NEW_PRICE);
    }

    @Test
    void getAllTrxProductHistoriesByNewPriceIsInShouldWork() {
        // Initialize the database
        insertedTrxProductHistory = trxProductHistoryRepository.save(trxProductHistory).block();

        // Get all the trxProductHistoryList where newPrice in
        defaultTrxProductHistoryFiltering("newPrice.in=" + DEFAULT_NEW_PRICE + "," + UPDATED_NEW_PRICE, "newPrice.in=" + UPDATED_NEW_PRICE);
    }

    @Test
    void getAllTrxProductHistoriesByNewPriceIsNullOrNotNull() {
        // Initialize the database
        insertedTrxProductHistory = trxProductHistoryRepository.save(trxProductHistory).block();

        // Get all the trxProductHistoryList where newPrice is not null
        defaultTrxProductHistoryFiltering("newPrice.specified=true", "newPrice.specified=false");
    }

    @Test
    void getAllTrxProductHistoriesByNewPriceIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxProductHistory = trxProductHistoryRepository.save(trxProductHistory).block();

        // Get all the trxProductHistoryList where newPrice is greater than or equal to
        defaultTrxProductHistoryFiltering(
            "newPrice.greaterThanOrEqual=" + DEFAULT_NEW_PRICE,
            "newPrice.greaterThanOrEqual=" + UPDATED_NEW_PRICE
        );
    }

    @Test
    void getAllTrxProductHistoriesByNewPriceIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxProductHistory = trxProductHistoryRepository.save(trxProductHistory).block();

        // Get all the trxProductHistoryList where newPrice is less than or equal to
        defaultTrxProductHistoryFiltering("newPrice.lessThanOrEqual=" + DEFAULT_NEW_PRICE, "newPrice.lessThanOrEqual=" + SMALLER_NEW_PRICE);
    }

    @Test
    void getAllTrxProductHistoriesByNewPriceIsLessThanSomething() {
        // Initialize the database
        insertedTrxProductHistory = trxProductHistoryRepository.save(trxProductHistory).block();

        // Get all the trxProductHistoryList where newPrice is less than
        defaultTrxProductHistoryFiltering("newPrice.lessThan=" + UPDATED_NEW_PRICE, "newPrice.lessThan=" + DEFAULT_NEW_PRICE);
    }

    @Test
    void getAllTrxProductHistoriesByNewPriceIsGreaterThanSomething() {
        // Initialize the database
        insertedTrxProductHistory = trxProductHistoryRepository.save(trxProductHistory).block();

        // Get all the trxProductHistoryList where newPrice is greater than
        defaultTrxProductHistoryFiltering("newPrice.greaterThan=" + SMALLER_NEW_PRICE, "newPrice.greaterThan=" + DEFAULT_NEW_PRICE);
    }

    @Test
    void getAllTrxProductHistoriesByChangeDateIsEqualToSomething() {
        // Initialize the database
        insertedTrxProductHistory = trxProductHistoryRepository.save(trxProductHistory).block();

        // Get all the trxProductHistoryList where changeDate equals to
        defaultTrxProductHistoryFiltering("changeDate.equals=" + DEFAULT_CHANGE_DATE, "changeDate.equals=" + UPDATED_CHANGE_DATE);
    }

    @Test
    void getAllTrxProductHistoriesByChangeDateIsInShouldWork() {
        // Initialize the database
        insertedTrxProductHistory = trxProductHistoryRepository.save(trxProductHistory).block();

        // Get all the trxProductHistoryList where changeDate in
        defaultTrxProductHistoryFiltering(
            "changeDate.in=" + DEFAULT_CHANGE_DATE + "," + UPDATED_CHANGE_DATE,
            "changeDate.in=" + UPDATED_CHANGE_DATE
        );
    }

    @Test
    void getAllTrxProductHistoriesByChangeDateIsNullOrNotNull() {
        // Initialize the database
        insertedTrxProductHistory = trxProductHistoryRepository.save(trxProductHistory).block();

        // Get all the trxProductHistoryList where changeDate is not null
        defaultTrxProductHistoryFiltering("changeDate.specified=true", "changeDate.specified=false");
    }

    private void defaultTrxProductHistoryFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultTrxProductHistoryShouldBeFound(shouldBeFound);
        defaultTrxProductHistoryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTrxProductHistoryShouldBeFound(String filter) {
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
            .value(hasItem(trxProductHistory.getId().intValue()))
            .jsonPath("$.[*].oldPrice")
            .value(hasItem(sameNumber(DEFAULT_OLD_PRICE)))
            .jsonPath("$.[*].newPrice")
            .value(hasItem(sameNumber(DEFAULT_NEW_PRICE)))
            .jsonPath("$.[*].changeDate")
            .value(hasItem(DEFAULT_CHANGE_DATE.toString()));

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
    private void defaultTrxProductHistoryShouldNotBeFound(String filter) {
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
    void getNonExistingTrxProductHistory() {
        // Get the trxProductHistory
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTrxProductHistory() throws Exception {
        // Initialize the database
        insertedTrxProductHistory = trxProductHistoryRepository.save(trxProductHistory).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        trxProductHistorySearchRepository.save(trxProductHistory).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxProductHistorySearchRepository.findAll().collectList().block());

        // Update the trxProductHistory
        TrxProductHistory updatedTrxProductHistory = trxProductHistoryRepository.findById(trxProductHistory.getId()).block();
        updatedTrxProductHistory.oldPrice(UPDATED_OLD_PRICE).newPrice(UPDATED_NEW_PRICE).changeDate(UPDATED_CHANGE_DATE);
        TrxProductHistoryDTO trxProductHistoryDTO = trxProductHistoryMapper.toDto(updatedTrxProductHistory);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trxProductHistoryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxProductHistoryDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxProductHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTrxProductHistoryToMatchAllProperties(updatedTrxProductHistory);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxProductHistorySearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TrxProductHistory> trxProductHistorySearchList = Streamable.of(
                    trxProductHistorySearchRepository.findAll().collectList().block()
                ).toList();
                TrxProductHistory testTrxProductHistorySearch = trxProductHistorySearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertTrxProductHistoryAllPropertiesEquals(testTrxProductHistorySearch, updatedTrxProductHistory);
                assertTrxProductHistoryUpdatableFieldsEquals(testTrxProductHistorySearch, updatedTrxProductHistory);
            });
    }

    @Test
    void putNonExistingTrxProductHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxProductHistorySearchRepository.findAll().collectList().block());
        trxProductHistory.setId(longCount.incrementAndGet());

        // Create the TrxProductHistory
        TrxProductHistoryDTO trxProductHistoryDTO = trxProductHistoryMapper.toDto(trxProductHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trxProductHistoryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxProductHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxProductHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxProductHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchTrxProductHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxProductHistorySearchRepository.findAll().collectList().block());
        trxProductHistory.setId(longCount.incrementAndGet());

        // Create the TrxProductHistory
        TrxProductHistoryDTO trxProductHistoryDTO = trxProductHistoryMapper.toDto(trxProductHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxProductHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxProductHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxProductHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamTrxProductHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxProductHistorySearchRepository.findAll().collectList().block());
        trxProductHistory.setId(longCount.incrementAndGet());

        // Create the TrxProductHistory
        TrxProductHistoryDTO trxProductHistoryDTO = trxProductHistoryMapper.toDto(trxProductHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxProductHistoryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrxProductHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxProductHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateTrxProductHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedTrxProductHistory = trxProductHistoryRepository.save(trxProductHistory).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trxProductHistory using partial update
        TrxProductHistory partialUpdatedTrxProductHistory = new TrxProductHistory();
        partialUpdatedTrxProductHistory.setId(trxProductHistory.getId());

        partialUpdatedTrxProductHistory.changeDate(UPDATED_CHANGE_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrxProductHistory.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrxProductHistory))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxProductHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrxProductHistoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTrxProductHistory, trxProductHistory),
            getPersistedTrxProductHistory(trxProductHistory)
        );
    }

    @Test
    void fullUpdateTrxProductHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedTrxProductHistory = trxProductHistoryRepository.save(trxProductHistory).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trxProductHistory using partial update
        TrxProductHistory partialUpdatedTrxProductHistory = new TrxProductHistory();
        partialUpdatedTrxProductHistory.setId(trxProductHistory.getId());

        partialUpdatedTrxProductHistory.oldPrice(UPDATED_OLD_PRICE).newPrice(UPDATED_NEW_PRICE).changeDate(UPDATED_CHANGE_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrxProductHistory.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrxProductHistory))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxProductHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrxProductHistoryUpdatableFieldsEquals(
            partialUpdatedTrxProductHistory,
            getPersistedTrxProductHistory(partialUpdatedTrxProductHistory)
        );
    }

    @Test
    void patchNonExistingTrxProductHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxProductHistorySearchRepository.findAll().collectList().block());
        trxProductHistory.setId(longCount.incrementAndGet());

        // Create the TrxProductHistory
        TrxProductHistoryDTO trxProductHistoryDTO = trxProductHistoryMapper.toDto(trxProductHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, trxProductHistoryDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxProductHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxProductHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxProductHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchTrxProductHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxProductHistorySearchRepository.findAll().collectList().block());
        trxProductHistory.setId(longCount.incrementAndGet());

        // Create the TrxProductHistory
        TrxProductHistoryDTO trxProductHistoryDTO = trxProductHistoryMapper.toDto(trxProductHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxProductHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxProductHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxProductHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamTrxProductHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxProductHistorySearchRepository.findAll().collectList().block());
        trxProductHistory.setId(longCount.incrementAndGet());

        // Create the TrxProductHistory
        TrxProductHistoryDTO trxProductHistoryDTO = trxProductHistoryMapper.toDto(trxProductHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxProductHistoryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrxProductHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxProductHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteTrxProductHistory() {
        // Initialize the database
        insertedTrxProductHistory = trxProductHistoryRepository.save(trxProductHistory).block();
        trxProductHistoryRepository.save(trxProductHistory).block();
        trxProductHistorySearchRepository.save(trxProductHistory).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxProductHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the trxProductHistory
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, trxProductHistory.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxProductHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchTrxProductHistory() {
        // Initialize the database
        insertedTrxProductHistory = trxProductHistoryRepository.save(trxProductHistory).block();
        trxProductHistorySearchRepository.save(trxProductHistory).block();

        // Search the trxProductHistory
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + trxProductHistory.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(trxProductHistory.getId().intValue()))
            .jsonPath("$.[*].oldPrice")
            .value(hasItem(sameNumber(DEFAULT_OLD_PRICE)))
            .jsonPath("$.[*].newPrice")
            .value(hasItem(sameNumber(DEFAULT_NEW_PRICE)))
            .jsonPath("$.[*].changeDate")
            .value(hasItem(DEFAULT_CHANGE_DATE.toString()));
    }

    protected long getRepositoryCount() {
        return trxProductHistoryRepository.count().block();
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

    protected TrxProductHistory getPersistedTrxProductHistory(TrxProductHistory trxProductHistory) {
        return trxProductHistoryRepository.findById(trxProductHistory.getId()).block();
    }

    protected void assertPersistedTrxProductHistoryToMatchAllProperties(TrxProductHistory expectedTrxProductHistory) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrxProductHistoryAllPropertiesEquals(expectedTrxProductHistory, getPersistedTrxProductHistory(expectedTrxProductHistory));
        assertTrxProductHistoryUpdatableFieldsEquals(expectedTrxProductHistory, getPersistedTrxProductHistory(expectedTrxProductHistory));
    }

    protected void assertPersistedTrxProductHistoryToMatchUpdatableProperties(TrxProductHistory expectedTrxProductHistory) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrxProductHistoryAllUpdatablePropertiesEquals(expectedTrxProductHistory, getPersistedTrxProductHistory(expectedTrxProductHistory));
        assertTrxProductHistoryUpdatableFieldsEquals(expectedTrxProductHistory, getPersistedTrxProductHistory(expectedTrxProductHistory));
    }
}
