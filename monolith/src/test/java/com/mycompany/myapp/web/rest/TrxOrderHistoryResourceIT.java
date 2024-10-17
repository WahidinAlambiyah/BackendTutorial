package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.TrxOrderHistoryAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.TrxOrderHistory;
import com.mycompany.myapp.domain.enumeration.OrderStatus;
import com.mycompany.myapp.domain.enumeration.OrderStatus;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.TrxOrderHistoryRepository;
import com.mycompany.myapp.repository.search.TrxOrderHistorySearchRepository;
import com.mycompany.myapp.service.dto.TrxOrderHistoryDTO;
import com.mycompany.myapp.service.mapper.TrxOrderHistoryMapper;
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
 * Integration tests for the {@link TrxOrderHistoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TrxOrderHistoryResourceIT {

    private static final OrderStatus DEFAULT_PREVIOUS_STATUS = OrderStatus.PENDING;
    private static final OrderStatus UPDATED_PREVIOUS_STATUS = OrderStatus.COMPLETED;

    private static final OrderStatus DEFAULT_NEW_STATUS = OrderStatus.PENDING;
    private static final OrderStatus UPDATED_NEW_STATUS = OrderStatus.COMPLETED;

    private static final Instant DEFAULT_CHANGE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CHANGE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/trx-order-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/trx-order-histories/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TrxOrderHistoryRepository trxOrderHistoryRepository;

    @Autowired
    private TrxOrderHistoryMapper trxOrderHistoryMapper;

    @Autowired
    private TrxOrderHistorySearchRepository trxOrderHistorySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TrxOrderHistory trxOrderHistory;

    private TrxOrderHistory insertedTrxOrderHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrxOrderHistory createEntity(EntityManager em) {
        TrxOrderHistory trxOrderHistory = new TrxOrderHistory()
            .previousStatus(DEFAULT_PREVIOUS_STATUS)
            .newStatus(DEFAULT_NEW_STATUS)
            .changeDate(DEFAULT_CHANGE_DATE);
        return trxOrderHistory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrxOrderHistory createUpdatedEntity(EntityManager em) {
        TrxOrderHistory trxOrderHistory = new TrxOrderHistory()
            .previousStatus(UPDATED_PREVIOUS_STATUS)
            .newStatus(UPDATED_NEW_STATUS)
            .changeDate(UPDATED_CHANGE_DATE);
        return trxOrderHistory;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TrxOrderHistory.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        trxOrderHistory = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedTrxOrderHistory != null) {
            trxOrderHistoryRepository.delete(insertedTrxOrderHistory).block();
            trxOrderHistorySearchRepository.delete(insertedTrxOrderHistory).block();
            insertedTrxOrderHistory = null;
        }
        deleteEntities(em);
    }

    @Test
    void createTrxOrderHistory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderHistorySearchRepository.findAll().collectList().block());
        // Create the TrxOrderHistory
        TrxOrderHistoryDTO trxOrderHistoryDTO = trxOrderHistoryMapper.toDto(trxOrderHistory);
        var returnedTrxOrderHistoryDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderHistoryDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TrxOrderHistoryDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the TrxOrderHistory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTrxOrderHistory = trxOrderHistoryMapper.toEntity(returnedTrxOrderHistoryDTO);
        assertTrxOrderHistoryUpdatableFieldsEquals(returnedTrxOrderHistory, getPersistedTrxOrderHistory(returnedTrxOrderHistory));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderHistorySearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedTrxOrderHistory = returnedTrxOrderHistory;
    }

    @Test
    void createTrxOrderHistoryWithExistingId() throws Exception {
        // Create the TrxOrderHistory with an existing ID
        trxOrderHistory.setId(1L);
        TrxOrderHistoryDTO trxOrderHistoryDTO = trxOrderHistoryMapper.toDto(trxOrderHistory);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderHistorySearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxOrderHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkPreviousStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderHistorySearchRepository.findAll().collectList().block());
        // set the field null
        trxOrderHistory.setPreviousStatus(null);

        // Create the TrxOrderHistory, which fails.
        TrxOrderHistoryDTO trxOrderHistoryDTO = trxOrderHistoryMapper.toDto(trxOrderHistory);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNewStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderHistorySearchRepository.findAll().collectList().block());
        // set the field null
        trxOrderHistory.setNewStatus(null);

        // Create the TrxOrderHistory, which fails.
        TrxOrderHistoryDTO trxOrderHistoryDTO = trxOrderHistoryMapper.toDto(trxOrderHistory);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkChangeDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderHistorySearchRepository.findAll().collectList().block());
        // set the field null
        trxOrderHistory.setChangeDate(null);

        // Create the TrxOrderHistory, which fails.
        TrxOrderHistoryDTO trxOrderHistoryDTO = trxOrderHistoryMapper.toDto(trxOrderHistory);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllTrxOrderHistories() {
        // Initialize the database
        insertedTrxOrderHistory = trxOrderHistoryRepository.save(trxOrderHistory).block();

        // Get all the trxOrderHistoryList
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
            .value(hasItem(trxOrderHistory.getId().intValue()))
            .jsonPath("$.[*].previousStatus")
            .value(hasItem(DEFAULT_PREVIOUS_STATUS.toString()))
            .jsonPath("$.[*].newStatus")
            .value(hasItem(DEFAULT_NEW_STATUS.toString()))
            .jsonPath("$.[*].changeDate")
            .value(hasItem(DEFAULT_CHANGE_DATE.toString()));
    }

    @Test
    void getTrxOrderHistory() {
        // Initialize the database
        insertedTrxOrderHistory = trxOrderHistoryRepository.save(trxOrderHistory).block();

        // Get the trxOrderHistory
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, trxOrderHistory.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(trxOrderHistory.getId().intValue()))
            .jsonPath("$.previousStatus")
            .value(is(DEFAULT_PREVIOUS_STATUS.toString()))
            .jsonPath("$.newStatus")
            .value(is(DEFAULT_NEW_STATUS.toString()))
            .jsonPath("$.changeDate")
            .value(is(DEFAULT_CHANGE_DATE.toString()));
    }

    @Test
    void getTrxOrderHistoriesByIdFiltering() {
        // Initialize the database
        insertedTrxOrderHistory = trxOrderHistoryRepository.save(trxOrderHistory).block();

        Long id = trxOrderHistory.getId();

        defaultTrxOrderHistoryFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTrxOrderHistoryFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTrxOrderHistoryFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllTrxOrderHistoriesByPreviousStatusIsEqualToSomething() {
        // Initialize the database
        insertedTrxOrderHistory = trxOrderHistoryRepository.save(trxOrderHistory).block();

        // Get all the trxOrderHistoryList where previousStatus equals to
        defaultTrxOrderHistoryFiltering(
            "previousStatus.equals=" + DEFAULT_PREVIOUS_STATUS,
            "previousStatus.equals=" + UPDATED_PREVIOUS_STATUS
        );
    }

    @Test
    void getAllTrxOrderHistoriesByPreviousStatusIsInShouldWork() {
        // Initialize the database
        insertedTrxOrderHistory = trxOrderHistoryRepository.save(trxOrderHistory).block();

        // Get all the trxOrderHistoryList where previousStatus in
        defaultTrxOrderHistoryFiltering(
            "previousStatus.in=" + DEFAULT_PREVIOUS_STATUS + "," + UPDATED_PREVIOUS_STATUS,
            "previousStatus.in=" + UPDATED_PREVIOUS_STATUS
        );
    }

    @Test
    void getAllTrxOrderHistoriesByPreviousStatusIsNullOrNotNull() {
        // Initialize the database
        insertedTrxOrderHistory = trxOrderHistoryRepository.save(trxOrderHistory).block();

        // Get all the trxOrderHistoryList where previousStatus is not null
        defaultTrxOrderHistoryFiltering("previousStatus.specified=true", "previousStatus.specified=false");
    }

    @Test
    void getAllTrxOrderHistoriesByNewStatusIsEqualToSomething() {
        // Initialize the database
        insertedTrxOrderHistory = trxOrderHistoryRepository.save(trxOrderHistory).block();

        // Get all the trxOrderHistoryList where newStatus equals to
        defaultTrxOrderHistoryFiltering("newStatus.equals=" + DEFAULT_NEW_STATUS, "newStatus.equals=" + UPDATED_NEW_STATUS);
    }

    @Test
    void getAllTrxOrderHistoriesByNewStatusIsInShouldWork() {
        // Initialize the database
        insertedTrxOrderHistory = trxOrderHistoryRepository.save(trxOrderHistory).block();

        // Get all the trxOrderHistoryList where newStatus in
        defaultTrxOrderHistoryFiltering(
            "newStatus.in=" + DEFAULT_NEW_STATUS + "," + UPDATED_NEW_STATUS,
            "newStatus.in=" + UPDATED_NEW_STATUS
        );
    }

    @Test
    void getAllTrxOrderHistoriesByNewStatusIsNullOrNotNull() {
        // Initialize the database
        insertedTrxOrderHistory = trxOrderHistoryRepository.save(trxOrderHistory).block();

        // Get all the trxOrderHistoryList where newStatus is not null
        defaultTrxOrderHistoryFiltering("newStatus.specified=true", "newStatus.specified=false");
    }

    @Test
    void getAllTrxOrderHistoriesByChangeDateIsEqualToSomething() {
        // Initialize the database
        insertedTrxOrderHistory = trxOrderHistoryRepository.save(trxOrderHistory).block();

        // Get all the trxOrderHistoryList where changeDate equals to
        defaultTrxOrderHistoryFiltering("changeDate.equals=" + DEFAULT_CHANGE_DATE, "changeDate.equals=" + UPDATED_CHANGE_DATE);
    }

    @Test
    void getAllTrxOrderHistoriesByChangeDateIsInShouldWork() {
        // Initialize the database
        insertedTrxOrderHistory = trxOrderHistoryRepository.save(trxOrderHistory).block();

        // Get all the trxOrderHistoryList where changeDate in
        defaultTrxOrderHistoryFiltering(
            "changeDate.in=" + DEFAULT_CHANGE_DATE + "," + UPDATED_CHANGE_DATE,
            "changeDate.in=" + UPDATED_CHANGE_DATE
        );
    }

    @Test
    void getAllTrxOrderHistoriesByChangeDateIsNullOrNotNull() {
        // Initialize the database
        insertedTrxOrderHistory = trxOrderHistoryRepository.save(trxOrderHistory).block();

        // Get all the trxOrderHistoryList where changeDate is not null
        defaultTrxOrderHistoryFiltering("changeDate.specified=true", "changeDate.specified=false");
    }

    private void defaultTrxOrderHistoryFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultTrxOrderHistoryShouldBeFound(shouldBeFound);
        defaultTrxOrderHistoryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTrxOrderHistoryShouldBeFound(String filter) {
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
            .value(hasItem(trxOrderHistory.getId().intValue()))
            .jsonPath("$.[*].previousStatus")
            .value(hasItem(DEFAULT_PREVIOUS_STATUS.toString()))
            .jsonPath("$.[*].newStatus")
            .value(hasItem(DEFAULT_NEW_STATUS.toString()))
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
    private void defaultTrxOrderHistoryShouldNotBeFound(String filter) {
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
    void getNonExistingTrxOrderHistory() {
        // Get the trxOrderHistory
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTrxOrderHistory() throws Exception {
        // Initialize the database
        insertedTrxOrderHistory = trxOrderHistoryRepository.save(trxOrderHistory).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        trxOrderHistorySearchRepository.save(trxOrderHistory).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderHistorySearchRepository.findAll().collectList().block());

        // Update the trxOrderHistory
        TrxOrderHistory updatedTrxOrderHistory = trxOrderHistoryRepository.findById(trxOrderHistory.getId()).block();
        updatedTrxOrderHistory.previousStatus(UPDATED_PREVIOUS_STATUS).newStatus(UPDATED_NEW_STATUS).changeDate(UPDATED_CHANGE_DATE);
        TrxOrderHistoryDTO trxOrderHistoryDTO = trxOrderHistoryMapper.toDto(updatedTrxOrderHistory);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trxOrderHistoryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderHistoryDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxOrderHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTrxOrderHistoryToMatchAllProperties(updatedTrxOrderHistory);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderHistorySearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TrxOrderHistory> trxOrderHistorySearchList = Streamable.of(
                    trxOrderHistorySearchRepository.findAll().collectList().block()
                ).toList();
                TrxOrderHistory testTrxOrderHistorySearch = trxOrderHistorySearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertTrxOrderHistoryAllPropertiesEquals(testTrxOrderHistorySearch, updatedTrxOrderHistory);
                assertTrxOrderHistoryUpdatableFieldsEquals(testTrxOrderHistorySearch, updatedTrxOrderHistory);
            });
    }

    @Test
    void putNonExistingTrxOrderHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderHistorySearchRepository.findAll().collectList().block());
        trxOrderHistory.setId(longCount.incrementAndGet());

        // Create the TrxOrderHistory
        TrxOrderHistoryDTO trxOrderHistoryDTO = trxOrderHistoryMapper.toDto(trxOrderHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trxOrderHistoryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxOrderHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchTrxOrderHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderHistorySearchRepository.findAll().collectList().block());
        trxOrderHistory.setId(longCount.incrementAndGet());

        // Create the TrxOrderHistory
        TrxOrderHistoryDTO trxOrderHistoryDTO = trxOrderHistoryMapper.toDto(trxOrderHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxOrderHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamTrxOrderHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderHistorySearchRepository.findAll().collectList().block());
        trxOrderHistory.setId(longCount.incrementAndGet());

        // Create the TrxOrderHistory
        TrxOrderHistoryDTO trxOrderHistoryDTO = trxOrderHistoryMapper.toDto(trxOrderHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderHistoryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrxOrderHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateTrxOrderHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedTrxOrderHistory = trxOrderHistoryRepository.save(trxOrderHistory).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trxOrderHistory using partial update
        TrxOrderHistory partialUpdatedTrxOrderHistory = new TrxOrderHistory();
        partialUpdatedTrxOrderHistory.setId(trxOrderHistory.getId());

        partialUpdatedTrxOrderHistory.previousStatus(UPDATED_PREVIOUS_STATUS).changeDate(UPDATED_CHANGE_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrxOrderHistory.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrxOrderHistory))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxOrderHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrxOrderHistoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTrxOrderHistory, trxOrderHistory),
            getPersistedTrxOrderHistory(trxOrderHistory)
        );
    }

    @Test
    void fullUpdateTrxOrderHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedTrxOrderHistory = trxOrderHistoryRepository.save(trxOrderHistory).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trxOrderHistory using partial update
        TrxOrderHistory partialUpdatedTrxOrderHistory = new TrxOrderHistory();
        partialUpdatedTrxOrderHistory.setId(trxOrderHistory.getId());

        partialUpdatedTrxOrderHistory.previousStatus(UPDATED_PREVIOUS_STATUS).newStatus(UPDATED_NEW_STATUS).changeDate(UPDATED_CHANGE_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrxOrderHistory.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrxOrderHistory))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxOrderHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrxOrderHistoryUpdatableFieldsEquals(
            partialUpdatedTrxOrderHistory,
            getPersistedTrxOrderHistory(partialUpdatedTrxOrderHistory)
        );
    }

    @Test
    void patchNonExistingTrxOrderHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderHistorySearchRepository.findAll().collectList().block());
        trxOrderHistory.setId(longCount.incrementAndGet());

        // Create the TrxOrderHistory
        TrxOrderHistoryDTO trxOrderHistoryDTO = trxOrderHistoryMapper.toDto(trxOrderHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, trxOrderHistoryDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxOrderHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxOrderHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchTrxOrderHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderHistorySearchRepository.findAll().collectList().block());
        trxOrderHistory.setId(longCount.incrementAndGet());

        // Create the TrxOrderHistory
        TrxOrderHistoryDTO trxOrderHistoryDTO = trxOrderHistoryMapper.toDto(trxOrderHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxOrderHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxOrderHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamTrxOrderHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderHistorySearchRepository.findAll().collectList().block());
        trxOrderHistory.setId(longCount.incrementAndGet());

        // Create the TrxOrderHistory
        TrxOrderHistoryDTO trxOrderHistoryDTO = trxOrderHistoryMapper.toDto(trxOrderHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxOrderHistoryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrxOrderHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteTrxOrderHistory() {
        // Initialize the database
        insertedTrxOrderHistory = trxOrderHistoryRepository.save(trxOrderHistory).block();
        trxOrderHistoryRepository.save(trxOrderHistory).block();
        trxOrderHistorySearchRepository.save(trxOrderHistory).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the trxOrderHistory
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, trxOrderHistory.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderHistorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchTrxOrderHistory() {
        // Initialize the database
        insertedTrxOrderHistory = trxOrderHistoryRepository.save(trxOrderHistory).block();
        trxOrderHistorySearchRepository.save(trxOrderHistory).block();

        // Search the trxOrderHistory
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + trxOrderHistory.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(trxOrderHistory.getId().intValue()))
            .jsonPath("$.[*].previousStatus")
            .value(hasItem(DEFAULT_PREVIOUS_STATUS.toString()))
            .jsonPath("$.[*].newStatus")
            .value(hasItem(DEFAULT_NEW_STATUS.toString()))
            .jsonPath("$.[*].changeDate")
            .value(hasItem(DEFAULT_CHANGE_DATE.toString()));
    }

    protected long getRepositoryCount() {
        return trxOrderHistoryRepository.count().block();
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

    protected TrxOrderHistory getPersistedTrxOrderHistory(TrxOrderHistory trxOrderHistory) {
        return trxOrderHistoryRepository.findById(trxOrderHistory.getId()).block();
    }

    protected void assertPersistedTrxOrderHistoryToMatchAllProperties(TrxOrderHistory expectedTrxOrderHistory) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrxOrderHistoryAllPropertiesEquals(expectedTrxOrderHistory, getPersistedTrxOrderHistory(expectedTrxOrderHistory));
        assertTrxOrderHistoryUpdatableFieldsEquals(expectedTrxOrderHistory, getPersistedTrxOrderHistory(expectedTrxOrderHistory));
    }

    protected void assertPersistedTrxOrderHistoryToMatchUpdatableProperties(TrxOrderHistory expectedTrxOrderHistory) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrxOrderHistoryAllUpdatablePropertiesEquals(expectedTrxOrderHistory, getPersistedTrxOrderHistory(expectedTrxOrderHistory));
        assertTrxOrderHistoryUpdatableFieldsEquals(expectedTrxOrderHistory, getPersistedTrxOrderHistory(expectedTrxOrderHistory));
    }
}
