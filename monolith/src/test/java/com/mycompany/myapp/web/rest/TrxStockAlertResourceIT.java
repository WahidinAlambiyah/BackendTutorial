package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.TrxStockAlertAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.TrxStockAlert;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.TrxStockAlertRepository;
import com.mycompany.myapp.repository.search.TrxStockAlertSearchRepository;
import com.mycompany.myapp.service.dto.TrxStockAlertDTO;
import com.mycompany.myapp.service.mapper.TrxStockAlertMapper;
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
 * Integration tests for the {@link TrxStockAlertResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TrxStockAlertResourceIT {

    private static final Integer DEFAULT_ALERT_THRESHOLD = 1;
    private static final Integer UPDATED_ALERT_THRESHOLD = 2;
    private static final Integer SMALLER_ALERT_THRESHOLD = 1 - 1;

    private static final Integer DEFAULT_CURRENT_STOCK = 1;
    private static final Integer UPDATED_CURRENT_STOCK = 2;
    private static final Integer SMALLER_CURRENT_STOCK = 1 - 1;

    private static final String ENTITY_API_URL = "/api/trx-stock-alerts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/trx-stock-alerts/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TrxStockAlertRepository trxStockAlertRepository;

    @Autowired
    private TrxStockAlertMapper trxStockAlertMapper;

    @Autowired
    private TrxStockAlertSearchRepository trxStockAlertSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TrxStockAlert trxStockAlert;

    private TrxStockAlert insertedTrxStockAlert;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrxStockAlert createEntity(EntityManager em) {
        TrxStockAlert trxStockAlert = new TrxStockAlert().alertThreshold(DEFAULT_ALERT_THRESHOLD).currentStock(DEFAULT_CURRENT_STOCK);
        return trxStockAlert;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrxStockAlert createUpdatedEntity(EntityManager em) {
        TrxStockAlert trxStockAlert = new TrxStockAlert().alertThreshold(UPDATED_ALERT_THRESHOLD).currentStock(UPDATED_CURRENT_STOCK);
        return trxStockAlert;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TrxStockAlert.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        trxStockAlert = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedTrxStockAlert != null) {
            trxStockAlertRepository.delete(insertedTrxStockAlert).block();
            trxStockAlertSearchRepository.delete(insertedTrxStockAlert).block();
            insertedTrxStockAlert = null;
        }
        deleteEntities(em);
    }

    @Test
    void createTrxStockAlert() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxStockAlertSearchRepository.findAll().collectList().block());
        // Create the TrxStockAlert
        TrxStockAlertDTO trxStockAlertDTO = trxStockAlertMapper.toDto(trxStockAlert);
        var returnedTrxStockAlertDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxStockAlertDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TrxStockAlertDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the TrxStockAlert in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTrxStockAlert = trxStockAlertMapper.toEntity(returnedTrxStockAlertDTO);
        assertTrxStockAlertUpdatableFieldsEquals(returnedTrxStockAlert, getPersistedTrxStockAlert(returnedTrxStockAlert));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxStockAlertSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedTrxStockAlert = returnedTrxStockAlert;
    }

    @Test
    void createTrxStockAlertWithExistingId() throws Exception {
        // Create the TrxStockAlert with an existing ID
        trxStockAlert.setId(1L);
        TrxStockAlertDTO trxStockAlertDTO = trxStockAlertMapper.toDto(trxStockAlert);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxStockAlertSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxStockAlertDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxStockAlert in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxStockAlertSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkAlertThresholdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxStockAlertSearchRepository.findAll().collectList().block());
        // set the field null
        trxStockAlert.setAlertThreshold(null);

        // Create the TrxStockAlert, which fails.
        TrxStockAlertDTO trxStockAlertDTO = trxStockAlertMapper.toDto(trxStockAlert);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxStockAlertDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxStockAlertSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkCurrentStockIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxStockAlertSearchRepository.findAll().collectList().block());
        // set the field null
        trxStockAlert.setCurrentStock(null);

        // Create the TrxStockAlert, which fails.
        TrxStockAlertDTO trxStockAlertDTO = trxStockAlertMapper.toDto(trxStockAlert);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxStockAlertDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxStockAlertSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllTrxStockAlerts() {
        // Initialize the database
        insertedTrxStockAlert = trxStockAlertRepository.save(trxStockAlert).block();

        // Get all the trxStockAlertList
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
            .value(hasItem(trxStockAlert.getId().intValue()))
            .jsonPath("$.[*].alertThreshold")
            .value(hasItem(DEFAULT_ALERT_THRESHOLD))
            .jsonPath("$.[*].currentStock")
            .value(hasItem(DEFAULT_CURRENT_STOCK));
    }

    @Test
    void getTrxStockAlert() {
        // Initialize the database
        insertedTrxStockAlert = trxStockAlertRepository.save(trxStockAlert).block();

        // Get the trxStockAlert
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, trxStockAlert.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(trxStockAlert.getId().intValue()))
            .jsonPath("$.alertThreshold")
            .value(is(DEFAULT_ALERT_THRESHOLD))
            .jsonPath("$.currentStock")
            .value(is(DEFAULT_CURRENT_STOCK));
    }

    @Test
    void getTrxStockAlertsByIdFiltering() {
        // Initialize the database
        insertedTrxStockAlert = trxStockAlertRepository.save(trxStockAlert).block();

        Long id = trxStockAlert.getId();

        defaultTrxStockAlertFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTrxStockAlertFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTrxStockAlertFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllTrxStockAlertsByAlertThresholdIsEqualToSomething() {
        // Initialize the database
        insertedTrxStockAlert = trxStockAlertRepository.save(trxStockAlert).block();

        // Get all the trxStockAlertList where alertThreshold equals to
        defaultTrxStockAlertFiltering(
            "alertThreshold.equals=" + DEFAULT_ALERT_THRESHOLD,
            "alertThreshold.equals=" + UPDATED_ALERT_THRESHOLD
        );
    }

    @Test
    void getAllTrxStockAlertsByAlertThresholdIsInShouldWork() {
        // Initialize the database
        insertedTrxStockAlert = trxStockAlertRepository.save(trxStockAlert).block();

        // Get all the trxStockAlertList where alertThreshold in
        defaultTrxStockAlertFiltering(
            "alertThreshold.in=" + DEFAULT_ALERT_THRESHOLD + "," + UPDATED_ALERT_THRESHOLD,
            "alertThreshold.in=" + UPDATED_ALERT_THRESHOLD
        );
    }

    @Test
    void getAllTrxStockAlertsByAlertThresholdIsNullOrNotNull() {
        // Initialize the database
        insertedTrxStockAlert = trxStockAlertRepository.save(trxStockAlert).block();

        // Get all the trxStockAlertList where alertThreshold is not null
        defaultTrxStockAlertFiltering("alertThreshold.specified=true", "alertThreshold.specified=false");
    }

    @Test
    void getAllTrxStockAlertsByAlertThresholdIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxStockAlert = trxStockAlertRepository.save(trxStockAlert).block();

        // Get all the trxStockAlertList where alertThreshold is greater than or equal to
        defaultTrxStockAlertFiltering(
            "alertThreshold.greaterThanOrEqual=" + DEFAULT_ALERT_THRESHOLD,
            "alertThreshold.greaterThanOrEqual=" + UPDATED_ALERT_THRESHOLD
        );
    }

    @Test
    void getAllTrxStockAlertsByAlertThresholdIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxStockAlert = trxStockAlertRepository.save(trxStockAlert).block();

        // Get all the trxStockAlertList where alertThreshold is less than or equal to
        defaultTrxStockAlertFiltering(
            "alertThreshold.lessThanOrEqual=" + DEFAULT_ALERT_THRESHOLD,
            "alertThreshold.lessThanOrEqual=" + SMALLER_ALERT_THRESHOLD
        );
    }

    @Test
    void getAllTrxStockAlertsByAlertThresholdIsLessThanSomething() {
        // Initialize the database
        insertedTrxStockAlert = trxStockAlertRepository.save(trxStockAlert).block();

        // Get all the trxStockAlertList where alertThreshold is less than
        defaultTrxStockAlertFiltering(
            "alertThreshold.lessThan=" + UPDATED_ALERT_THRESHOLD,
            "alertThreshold.lessThan=" + DEFAULT_ALERT_THRESHOLD
        );
    }

    @Test
    void getAllTrxStockAlertsByAlertThresholdIsGreaterThanSomething() {
        // Initialize the database
        insertedTrxStockAlert = trxStockAlertRepository.save(trxStockAlert).block();

        // Get all the trxStockAlertList where alertThreshold is greater than
        defaultTrxStockAlertFiltering(
            "alertThreshold.greaterThan=" + SMALLER_ALERT_THRESHOLD,
            "alertThreshold.greaterThan=" + DEFAULT_ALERT_THRESHOLD
        );
    }

    @Test
    void getAllTrxStockAlertsByCurrentStockIsEqualToSomething() {
        // Initialize the database
        insertedTrxStockAlert = trxStockAlertRepository.save(trxStockAlert).block();

        // Get all the trxStockAlertList where currentStock equals to
        defaultTrxStockAlertFiltering("currentStock.equals=" + DEFAULT_CURRENT_STOCK, "currentStock.equals=" + UPDATED_CURRENT_STOCK);
    }

    @Test
    void getAllTrxStockAlertsByCurrentStockIsInShouldWork() {
        // Initialize the database
        insertedTrxStockAlert = trxStockAlertRepository.save(trxStockAlert).block();

        // Get all the trxStockAlertList where currentStock in
        defaultTrxStockAlertFiltering(
            "currentStock.in=" + DEFAULT_CURRENT_STOCK + "," + UPDATED_CURRENT_STOCK,
            "currentStock.in=" + UPDATED_CURRENT_STOCK
        );
    }

    @Test
    void getAllTrxStockAlertsByCurrentStockIsNullOrNotNull() {
        // Initialize the database
        insertedTrxStockAlert = trxStockAlertRepository.save(trxStockAlert).block();

        // Get all the trxStockAlertList where currentStock is not null
        defaultTrxStockAlertFiltering("currentStock.specified=true", "currentStock.specified=false");
    }

    @Test
    void getAllTrxStockAlertsByCurrentStockIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxStockAlert = trxStockAlertRepository.save(trxStockAlert).block();

        // Get all the trxStockAlertList where currentStock is greater than or equal to
        defaultTrxStockAlertFiltering(
            "currentStock.greaterThanOrEqual=" + DEFAULT_CURRENT_STOCK,
            "currentStock.greaterThanOrEqual=" + UPDATED_CURRENT_STOCK
        );
    }

    @Test
    void getAllTrxStockAlertsByCurrentStockIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxStockAlert = trxStockAlertRepository.save(trxStockAlert).block();

        // Get all the trxStockAlertList where currentStock is less than or equal to
        defaultTrxStockAlertFiltering(
            "currentStock.lessThanOrEqual=" + DEFAULT_CURRENT_STOCK,
            "currentStock.lessThanOrEqual=" + SMALLER_CURRENT_STOCK
        );
    }

    @Test
    void getAllTrxStockAlertsByCurrentStockIsLessThanSomething() {
        // Initialize the database
        insertedTrxStockAlert = trxStockAlertRepository.save(trxStockAlert).block();

        // Get all the trxStockAlertList where currentStock is less than
        defaultTrxStockAlertFiltering("currentStock.lessThan=" + UPDATED_CURRENT_STOCK, "currentStock.lessThan=" + DEFAULT_CURRENT_STOCK);
    }

    @Test
    void getAllTrxStockAlertsByCurrentStockIsGreaterThanSomething() {
        // Initialize the database
        insertedTrxStockAlert = trxStockAlertRepository.save(trxStockAlert).block();

        // Get all the trxStockAlertList where currentStock is greater than
        defaultTrxStockAlertFiltering(
            "currentStock.greaterThan=" + SMALLER_CURRENT_STOCK,
            "currentStock.greaterThan=" + DEFAULT_CURRENT_STOCK
        );
    }

    private void defaultTrxStockAlertFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultTrxStockAlertShouldBeFound(shouldBeFound);
        defaultTrxStockAlertShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTrxStockAlertShouldBeFound(String filter) {
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
            .value(hasItem(trxStockAlert.getId().intValue()))
            .jsonPath("$.[*].alertThreshold")
            .value(hasItem(DEFAULT_ALERT_THRESHOLD))
            .jsonPath("$.[*].currentStock")
            .value(hasItem(DEFAULT_CURRENT_STOCK));

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
    private void defaultTrxStockAlertShouldNotBeFound(String filter) {
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
    void getNonExistingTrxStockAlert() {
        // Get the trxStockAlert
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTrxStockAlert() throws Exception {
        // Initialize the database
        insertedTrxStockAlert = trxStockAlertRepository.save(trxStockAlert).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        trxStockAlertSearchRepository.save(trxStockAlert).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxStockAlertSearchRepository.findAll().collectList().block());

        // Update the trxStockAlert
        TrxStockAlert updatedTrxStockAlert = trxStockAlertRepository.findById(trxStockAlert.getId()).block();
        updatedTrxStockAlert.alertThreshold(UPDATED_ALERT_THRESHOLD).currentStock(UPDATED_CURRENT_STOCK);
        TrxStockAlertDTO trxStockAlertDTO = trxStockAlertMapper.toDto(updatedTrxStockAlert);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trxStockAlertDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxStockAlertDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxStockAlert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTrxStockAlertToMatchAllProperties(updatedTrxStockAlert);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxStockAlertSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TrxStockAlert> trxStockAlertSearchList = Streamable.of(
                    trxStockAlertSearchRepository.findAll().collectList().block()
                ).toList();
                TrxStockAlert testTrxStockAlertSearch = trxStockAlertSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertTrxStockAlertAllPropertiesEquals(testTrxStockAlertSearch, updatedTrxStockAlert);
                assertTrxStockAlertUpdatableFieldsEquals(testTrxStockAlertSearch, updatedTrxStockAlert);
            });
    }

    @Test
    void putNonExistingTrxStockAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxStockAlertSearchRepository.findAll().collectList().block());
        trxStockAlert.setId(longCount.incrementAndGet());

        // Create the TrxStockAlert
        TrxStockAlertDTO trxStockAlertDTO = trxStockAlertMapper.toDto(trxStockAlert);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trxStockAlertDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxStockAlertDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxStockAlert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxStockAlertSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchTrxStockAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxStockAlertSearchRepository.findAll().collectList().block());
        trxStockAlert.setId(longCount.incrementAndGet());

        // Create the TrxStockAlert
        TrxStockAlertDTO trxStockAlertDTO = trxStockAlertMapper.toDto(trxStockAlert);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxStockAlertDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxStockAlert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxStockAlertSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamTrxStockAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxStockAlertSearchRepository.findAll().collectList().block());
        trxStockAlert.setId(longCount.incrementAndGet());

        // Create the TrxStockAlert
        TrxStockAlertDTO trxStockAlertDTO = trxStockAlertMapper.toDto(trxStockAlert);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxStockAlertDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrxStockAlert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxStockAlertSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateTrxStockAlertWithPatch() throws Exception {
        // Initialize the database
        insertedTrxStockAlert = trxStockAlertRepository.save(trxStockAlert).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trxStockAlert using partial update
        TrxStockAlert partialUpdatedTrxStockAlert = new TrxStockAlert();
        partialUpdatedTrxStockAlert.setId(trxStockAlert.getId());

        partialUpdatedTrxStockAlert.currentStock(UPDATED_CURRENT_STOCK);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrxStockAlert.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrxStockAlert))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxStockAlert in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrxStockAlertUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTrxStockAlert, trxStockAlert),
            getPersistedTrxStockAlert(trxStockAlert)
        );
    }

    @Test
    void fullUpdateTrxStockAlertWithPatch() throws Exception {
        // Initialize the database
        insertedTrxStockAlert = trxStockAlertRepository.save(trxStockAlert).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trxStockAlert using partial update
        TrxStockAlert partialUpdatedTrxStockAlert = new TrxStockAlert();
        partialUpdatedTrxStockAlert.setId(trxStockAlert.getId());

        partialUpdatedTrxStockAlert.alertThreshold(UPDATED_ALERT_THRESHOLD).currentStock(UPDATED_CURRENT_STOCK);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrxStockAlert.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrxStockAlert))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxStockAlert in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrxStockAlertUpdatableFieldsEquals(partialUpdatedTrxStockAlert, getPersistedTrxStockAlert(partialUpdatedTrxStockAlert));
    }

    @Test
    void patchNonExistingTrxStockAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxStockAlertSearchRepository.findAll().collectList().block());
        trxStockAlert.setId(longCount.incrementAndGet());

        // Create the TrxStockAlert
        TrxStockAlertDTO trxStockAlertDTO = trxStockAlertMapper.toDto(trxStockAlert);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, trxStockAlertDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxStockAlertDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxStockAlert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxStockAlertSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchTrxStockAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxStockAlertSearchRepository.findAll().collectList().block());
        trxStockAlert.setId(longCount.incrementAndGet());

        // Create the TrxStockAlert
        TrxStockAlertDTO trxStockAlertDTO = trxStockAlertMapper.toDto(trxStockAlert);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxStockAlertDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxStockAlert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxStockAlertSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamTrxStockAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxStockAlertSearchRepository.findAll().collectList().block());
        trxStockAlert.setId(longCount.incrementAndGet());

        // Create the TrxStockAlert
        TrxStockAlertDTO trxStockAlertDTO = trxStockAlertMapper.toDto(trxStockAlert);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxStockAlertDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrxStockAlert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxStockAlertSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteTrxStockAlert() {
        // Initialize the database
        insertedTrxStockAlert = trxStockAlertRepository.save(trxStockAlert).block();
        trxStockAlertRepository.save(trxStockAlert).block();
        trxStockAlertSearchRepository.save(trxStockAlert).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxStockAlertSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the trxStockAlert
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, trxStockAlert.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxStockAlertSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchTrxStockAlert() {
        // Initialize the database
        insertedTrxStockAlert = trxStockAlertRepository.save(trxStockAlert).block();
        trxStockAlertSearchRepository.save(trxStockAlert).block();

        // Search the trxStockAlert
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + trxStockAlert.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(trxStockAlert.getId().intValue()))
            .jsonPath("$.[*].alertThreshold")
            .value(hasItem(DEFAULT_ALERT_THRESHOLD))
            .jsonPath("$.[*].currentStock")
            .value(hasItem(DEFAULT_CURRENT_STOCK));
    }

    protected long getRepositoryCount() {
        return trxStockAlertRepository.count().block();
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

    protected TrxStockAlert getPersistedTrxStockAlert(TrxStockAlert trxStockAlert) {
        return trxStockAlertRepository.findById(trxStockAlert.getId()).block();
    }

    protected void assertPersistedTrxStockAlertToMatchAllProperties(TrxStockAlert expectedTrxStockAlert) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrxStockAlertAllPropertiesEquals(expectedTrxStockAlert, getPersistedTrxStockAlert(expectedTrxStockAlert));
        assertTrxStockAlertUpdatableFieldsEquals(expectedTrxStockAlert, getPersistedTrxStockAlert(expectedTrxStockAlert));
    }

    protected void assertPersistedTrxStockAlertToMatchUpdatableProperties(TrxStockAlert expectedTrxStockAlert) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrxStockAlertAllUpdatablePropertiesEquals(expectedTrxStockAlert, getPersistedTrxStockAlert(expectedTrxStockAlert));
        assertTrxStockAlertUpdatableFieldsEquals(expectedTrxStockAlert, getPersistedTrxStockAlert(expectedTrxStockAlert));
    }
}
