package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.TrxNotificationAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.MstCustomer;
import com.mycompany.myapp.domain.TrxNotification;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MstCustomerRepository;
import com.mycompany.myapp.repository.TrxNotificationRepository;
import com.mycompany.myapp.repository.search.TrxNotificationSearchRepository;
import com.mycompany.myapp.service.dto.TrxNotificationDTO;
import com.mycompany.myapp.service.mapper.TrxNotificationMapper;
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
 * Integration tests for the {@link TrxNotificationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TrxNotificationResourceIT {

    private static final String DEFAULT_RECIPIENT = "AAAAAAAAAA";
    private static final String UPDATED_RECIPIENT = "BBBBBBBBBB";

    private static final String DEFAULT_MESSAGE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_SENT_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SENT_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/trx-notifications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/trx-notifications/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TrxNotificationRepository trxNotificationRepository;

    @Autowired
    private TrxNotificationMapper trxNotificationMapper;

    @Autowired
    private TrxNotificationSearchRepository trxNotificationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TrxNotification trxNotification;

    private TrxNotification insertedTrxNotification;

    @Autowired
    private MstCustomerRepository mstCustomerRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrxNotification createEntity(EntityManager em) {
        TrxNotification trxNotification = new TrxNotification()
            .recipient(DEFAULT_RECIPIENT)
            .messageType(DEFAULT_MESSAGE_TYPE)
            .content(DEFAULT_CONTENT)
            .sentAt(DEFAULT_SENT_AT);
        return trxNotification;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrxNotification createUpdatedEntity(EntityManager em) {
        TrxNotification trxNotification = new TrxNotification()
            .recipient(UPDATED_RECIPIENT)
            .messageType(UPDATED_MESSAGE_TYPE)
            .content(UPDATED_CONTENT)
            .sentAt(UPDATED_SENT_AT);
        return trxNotification;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TrxNotification.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        trxNotification = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedTrxNotification != null) {
            trxNotificationRepository.delete(insertedTrxNotification).block();
            trxNotificationSearchRepository.delete(insertedTrxNotification).block();
            insertedTrxNotification = null;
        }
        deleteEntities(em);
    }

    @Test
    void createTrxNotification() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxNotificationSearchRepository.findAll().collectList().block());
        // Create the TrxNotification
        TrxNotificationDTO trxNotificationDTO = trxNotificationMapper.toDto(trxNotification);
        var returnedTrxNotificationDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxNotificationDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TrxNotificationDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the TrxNotification in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTrxNotification = trxNotificationMapper.toEntity(returnedTrxNotificationDTO);
        assertTrxNotificationUpdatableFieldsEquals(returnedTrxNotification, getPersistedTrxNotification(returnedTrxNotification));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxNotificationSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedTrxNotification = returnedTrxNotification;
    }

    @Test
    void createTrxNotificationWithExistingId() throws Exception {
        // Create the TrxNotification with an existing ID
        trxNotification.setId(1L);
        TrxNotificationDTO trxNotificationDTO = trxNotificationMapper.toDto(trxNotification);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxNotificationSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxNotificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxNotificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkRecipientIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxNotificationSearchRepository.findAll().collectList().block());
        // set the field null
        trxNotification.setRecipient(null);

        // Create the TrxNotification, which fails.
        TrxNotificationDTO trxNotificationDTO = trxNotificationMapper.toDto(trxNotification);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxNotificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxNotificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkMessageTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxNotificationSearchRepository.findAll().collectList().block());
        // set the field null
        trxNotification.setMessageType(null);

        // Create the TrxNotification, which fails.
        TrxNotificationDTO trxNotificationDTO = trxNotificationMapper.toDto(trxNotification);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxNotificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxNotificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkContentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxNotificationSearchRepository.findAll().collectList().block());
        // set the field null
        trxNotification.setContent(null);

        // Create the TrxNotification, which fails.
        TrxNotificationDTO trxNotificationDTO = trxNotificationMapper.toDto(trxNotification);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxNotificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxNotificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkSentAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxNotificationSearchRepository.findAll().collectList().block());
        // set the field null
        trxNotification.setSentAt(null);

        // Create the TrxNotification, which fails.
        TrxNotificationDTO trxNotificationDTO = trxNotificationMapper.toDto(trxNotification);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxNotificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxNotificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllTrxNotifications() {
        // Initialize the database
        insertedTrxNotification = trxNotificationRepository.save(trxNotification).block();

        // Get all the trxNotificationList
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
            .value(hasItem(trxNotification.getId().intValue()))
            .jsonPath("$.[*].recipient")
            .value(hasItem(DEFAULT_RECIPIENT))
            .jsonPath("$.[*].messageType")
            .value(hasItem(DEFAULT_MESSAGE_TYPE))
            .jsonPath("$.[*].content")
            .value(hasItem(DEFAULT_CONTENT))
            .jsonPath("$.[*].sentAt")
            .value(hasItem(DEFAULT_SENT_AT.toString()));
    }

    @Test
    void getTrxNotification() {
        // Initialize the database
        insertedTrxNotification = trxNotificationRepository.save(trxNotification).block();

        // Get the trxNotification
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, trxNotification.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(trxNotification.getId().intValue()))
            .jsonPath("$.recipient")
            .value(is(DEFAULT_RECIPIENT))
            .jsonPath("$.messageType")
            .value(is(DEFAULT_MESSAGE_TYPE))
            .jsonPath("$.content")
            .value(is(DEFAULT_CONTENT))
            .jsonPath("$.sentAt")
            .value(is(DEFAULT_SENT_AT.toString()));
    }

    @Test
    void getTrxNotificationsByIdFiltering() {
        // Initialize the database
        insertedTrxNotification = trxNotificationRepository.save(trxNotification).block();

        Long id = trxNotification.getId();

        defaultTrxNotificationFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTrxNotificationFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTrxNotificationFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllTrxNotificationsByRecipientIsEqualToSomething() {
        // Initialize the database
        insertedTrxNotification = trxNotificationRepository.save(trxNotification).block();

        // Get all the trxNotificationList where recipient equals to
        defaultTrxNotificationFiltering("recipient.equals=" + DEFAULT_RECIPIENT, "recipient.equals=" + UPDATED_RECIPIENT);
    }

    @Test
    void getAllTrxNotificationsByRecipientIsInShouldWork() {
        // Initialize the database
        insertedTrxNotification = trxNotificationRepository.save(trxNotification).block();

        // Get all the trxNotificationList where recipient in
        defaultTrxNotificationFiltering("recipient.in=" + DEFAULT_RECIPIENT + "," + UPDATED_RECIPIENT, "recipient.in=" + UPDATED_RECIPIENT);
    }

    @Test
    void getAllTrxNotificationsByRecipientIsNullOrNotNull() {
        // Initialize the database
        insertedTrxNotification = trxNotificationRepository.save(trxNotification).block();

        // Get all the trxNotificationList where recipient is not null
        defaultTrxNotificationFiltering("recipient.specified=true", "recipient.specified=false");
    }

    @Test
    void getAllTrxNotificationsByRecipientContainsSomething() {
        // Initialize the database
        insertedTrxNotification = trxNotificationRepository.save(trxNotification).block();

        // Get all the trxNotificationList where recipient contains
        defaultTrxNotificationFiltering("recipient.contains=" + DEFAULT_RECIPIENT, "recipient.contains=" + UPDATED_RECIPIENT);
    }

    @Test
    void getAllTrxNotificationsByRecipientNotContainsSomething() {
        // Initialize the database
        insertedTrxNotification = trxNotificationRepository.save(trxNotification).block();

        // Get all the trxNotificationList where recipient does not contain
        defaultTrxNotificationFiltering("recipient.doesNotContain=" + UPDATED_RECIPIENT, "recipient.doesNotContain=" + DEFAULT_RECIPIENT);
    }

    @Test
    void getAllTrxNotificationsByMessageTypeIsEqualToSomething() {
        // Initialize the database
        insertedTrxNotification = trxNotificationRepository.save(trxNotification).block();

        // Get all the trxNotificationList where messageType equals to
        defaultTrxNotificationFiltering("messageType.equals=" + DEFAULT_MESSAGE_TYPE, "messageType.equals=" + UPDATED_MESSAGE_TYPE);
    }

    @Test
    void getAllTrxNotificationsByMessageTypeIsInShouldWork() {
        // Initialize the database
        insertedTrxNotification = trxNotificationRepository.save(trxNotification).block();

        // Get all the trxNotificationList where messageType in
        defaultTrxNotificationFiltering(
            "messageType.in=" + DEFAULT_MESSAGE_TYPE + "," + UPDATED_MESSAGE_TYPE,
            "messageType.in=" + UPDATED_MESSAGE_TYPE
        );
    }

    @Test
    void getAllTrxNotificationsByMessageTypeIsNullOrNotNull() {
        // Initialize the database
        insertedTrxNotification = trxNotificationRepository.save(trxNotification).block();

        // Get all the trxNotificationList where messageType is not null
        defaultTrxNotificationFiltering("messageType.specified=true", "messageType.specified=false");
    }

    @Test
    void getAllTrxNotificationsByMessageTypeContainsSomething() {
        // Initialize the database
        insertedTrxNotification = trxNotificationRepository.save(trxNotification).block();

        // Get all the trxNotificationList where messageType contains
        defaultTrxNotificationFiltering("messageType.contains=" + DEFAULT_MESSAGE_TYPE, "messageType.contains=" + UPDATED_MESSAGE_TYPE);
    }

    @Test
    void getAllTrxNotificationsByMessageTypeNotContainsSomething() {
        // Initialize the database
        insertedTrxNotification = trxNotificationRepository.save(trxNotification).block();

        // Get all the trxNotificationList where messageType does not contain
        defaultTrxNotificationFiltering(
            "messageType.doesNotContain=" + UPDATED_MESSAGE_TYPE,
            "messageType.doesNotContain=" + DEFAULT_MESSAGE_TYPE
        );
    }

    @Test
    void getAllTrxNotificationsByContentIsEqualToSomething() {
        // Initialize the database
        insertedTrxNotification = trxNotificationRepository.save(trxNotification).block();

        // Get all the trxNotificationList where content equals to
        defaultTrxNotificationFiltering("content.equals=" + DEFAULT_CONTENT, "content.equals=" + UPDATED_CONTENT);
    }

    @Test
    void getAllTrxNotificationsByContentIsInShouldWork() {
        // Initialize the database
        insertedTrxNotification = trxNotificationRepository.save(trxNotification).block();

        // Get all the trxNotificationList where content in
        defaultTrxNotificationFiltering("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT, "content.in=" + UPDATED_CONTENT);
    }

    @Test
    void getAllTrxNotificationsByContentIsNullOrNotNull() {
        // Initialize the database
        insertedTrxNotification = trxNotificationRepository.save(trxNotification).block();

        // Get all the trxNotificationList where content is not null
        defaultTrxNotificationFiltering("content.specified=true", "content.specified=false");
    }

    @Test
    void getAllTrxNotificationsByContentContainsSomething() {
        // Initialize the database
        insertedTrxNotification = trxNotificationRepository.save(trxNotification).block();

        // Get all the trxNotificationList where content contains
        defaultTrxNotificationFiltering("content.contains=" + DEFAULT_CONTENT, "content.contains=" + UPDATED_CONTENT);
    }

    @Test
    void getAllTrxNotificationsByContentNotContainsSomething() {
        // Initialize the database
        insertedTrxNotification = trxNotificationRepository.save(trxNotification).block();

        // Get all the trxNotificationList where content does not contain
        defaultTrxNotificationFiltering("content.doesNotContain=" + UPDATED_CONTENT, "content.doesNotContain=" + DEFAULT_CONTENT);
    }

    @Test
    void getAllTrxNotificationsBySentAtIsEqualToSomething() {
        // Initialize the database
        insertedTrxNotification = trxNotificationRepository.save(trxNotification).block();

        // Get all the trxNotificationList where sentAt equals to
        defaultTrxNotificationFiltering("sentAt.equals=" + DEFAULT_SENT_AT, "sentAt.equals=" + UPDATED_SENT_AT);
    }

    @Test
    void getAllTrxNotificationsBySentAtIsInShouldWork() {
        // Initialize the database
        insertedTrxNotification = trxNotificationRepository.save(trxNotification).block();

        // Get all the trxNotificationList where sentAt in
        defaultTrxNotificationFiltering("sentAt.in=" + DEFAULT_SENT_AT + "," + UPDATED_SENT_AT, "sentAt.in=" + UPDATED_SENT_AT);
    }

    @Test
    void getAllTrxNotificationsBySentAtIsNullOrNotNull() {
        // Initialize the database
        insertedTrxNotification = trxNotificationRepository.save(trxNotification).block();

        // Get all the trxNotificationList where sentAt is not null
        defaultTrxNotificationFiltering("sentAt.specified=true", "sentAt.specified=false");
    }

    @Test
    void getAllTrxNotificationsByCustomerIsEqualToSomething() {
        MstCustomer customer = MstCustomerResourceIT.createEntity(em);
        mstCustomerRepository.save(customer).block();
        Long customerId = customer.getId();
        trxNotification.setCustomerId(customerId);
        insertedTrxNotification = trxNotificationRepository.save(trxNotification).block();
        // Get all the trxNotificationList where customer equals to customerId
        defaultTrxNotificationShouldBeFound("customerId.equals=" + customerId);

        // Get all the trxNotificationList where customer equals to (customerId + 1)
        defaultTrxNotificationShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }

    private void defaultTrxNotificationFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultTrxNotificationShouldBeFound(shouldBeFound);
        defaultTrxNotificationShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTrxNotificationShouldBeFound(String filter) {
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
            .value(hasItem(trxNotification.getId().intValue()))
            .jsonPath("$.[*].recipient")
            .value(hasItem(DEFAULT_RECIPIENT))
            .jsonPath("$.[*].messageType")
            .value(hasItem(DEFAULT_MESSAGE_TYPE))
            .jsonPath("$.[*].content")
            .value(hasItem(DEFAULT_CONTENT))
            .jsonPath("$.[*].sentAt")
            .value(hasItem(DEFAULT_SENT_AT.toString()));

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
    private void defaultTrxNotificationShouldNotBeFound(String filter) {
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
    void getNonExistingTrxNotification() {
        // Get the trxNotification
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTrxNotification() throws Exception {
        // Initialize the database
        insertedTrxNotification = trxNotificationRepository.save(trxNotification).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        trxNotificationSearchRepository.save(trxNotification).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxNotificationSearchRepository.findAll().collectList().block());

        // Update the trxNotification
        TrxNotification updatedTrxNotification = trxNotificationRepository.findById(trxNotification.getId()).block();
        updatedTrxNotification
            .recipient(UPDATED_RECIPIENT)
            .messageType(UPDATED_MESSAGE_TYPE)
            .content(UPDATED_CONTENT)
            .sentAt(UPDATED_SENT_AT);
        TrxNotificationDTO trxNotificationDTO = trxNotificationMapper.toDto(updatedTrxNotification);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trxNotificationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxNotificationDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTrxNotificationToMatchAllProperties(updatedTrxNotification);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxNotificationSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TrxNotification> trxNotificationSearchList = Streamable.of(
                    trxNotificationSearchRepository.findAll().collectList().block()
                ).toList();
                TrxNotification testTrxNotificationSearch = trxNotificationSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertTrxNotificationAllPropertiesEquals(testTrxNotificationSearch, updatedTrxNotification);
                assertTrxNotificationUpdatableFieldsEquals(testTrxNotificationSearch, updatedTrxNotification);
            });
    }

    @Test
    void putNonExistingTrxNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxNotificationSearchRepository.findAll().collectList().block());
        trxNotification.setId(longCount.incrementAndGet());

        // Create the TrxNotification
        TrxNotificationDTO trxNotificationDTO = trxNotificationMapper.toDto(trxNotification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trxNotificationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxNotificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxNotificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchTrxNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxNotificationSearchRepository.findAll().collectList().block());
        trxNotification.setId(longCount.incrementAndGet());

        // Create the TrxNotification
        TrxNotificationDTO trxNotificationDTO = trxNotificationMapper.toDto(trxNotification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxNotificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxNotificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamTrxNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxNotificationSearchRepository.findAll().collectList().block());
        trxNotification.setId(longCount.incrementAndGet());

        // Create the TrxNotification
        TrxNotificationDTO trxNotificationDTO = trxNotificationMapper.toDto(trxNotification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxNotificationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrxNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxNotificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateTrxNotificationWithPatch() throws Exception {
        // Initialize the database
        insertedTrxNotification = trxNotificationRepository.save(trxNotification).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trxNotification using partial update
        TrxNotification partialUpdatedTrxNotification = new TrxNotification();
        partialUpdatedTrxNotification.setId(trxNotification.getId());

        partialUpdatedTrxNotification.messageType(UPDATED_MESSAGE_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrxNotification.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrxNotification))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxNotification in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrxNotificationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTrxNotification, trxNotification),
            getPersistedTrxNotification(trxNotification)
        );
    }

    @Test
    void fullUpdateTrxNotificationWithPatch() throws Exception {
        // Initialize the database
        insertedTrxNotification = trxNotificationRepository.save(trxNotification).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trxNotification using partial update
        TrxNotification partialUpdatedTrxNotification = new TrxNotification();
        partialUpdatedTrxNotification.setId(trxNotification.getId());

        partialUpdatedTrxNotification
            .recipient(UPDATED_RECIPIENT)
            .messageType(UPDATED_MESSAGE_TYPE)
            .content(UPDATED_CONTENT)
            .sentAt(UPDATED_SENT_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrxNotification.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrxNotification))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxNotification in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrxNotificationUpdatableFieldsEquals(
            partialUpdatedTrxNotification,
            getPersistedTrxNotification(partialUpdatedTrxNotification)
        );
    }

    @Test
    void patchNonExistingTrxNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxNotificationSearchRepository.findAll().collectList().block());
        trxNotification.setId(longCount.incrementAndGet());

        // Create the TrxNotification
        TrxNotificationDTO trxNotificationDTO = trxNotificationMapper.toDto(trxNotification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, trxNotificationDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxNotificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxNotificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchTrxNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxNotificationSearchRepository.findAll().collectList().block());
        trxNotification.setId(longCount.incrementAndGet());

        // Create the TrxNotification
        TrxNotificationDTO trxNotificationDTO = trxNotificationMapper.toDto(trxNotification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxNotificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxNotificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamTrxNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxNotificationSearchRepository.findAll().collectList().block());
        trxNotification.setId(longCount.incrementAndGet());

        // Create the TrxNotification
        TrxNotificationDTO trxNotificationDTO = trxNotificationMapper.toDto(trxNotification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxNotificationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrxNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxNotificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteTrxNotification() {
        // Initialize the database
        insertedTrxNotification = trxNotificationRepository.save(trxNotification).block();
        trxNotificationRepository.save(trxNotification).block();
        trxNotificationSearchRepository.save(trxNotification).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxNotificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the trxNotification
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, trxNotification.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxNotificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchTrxNotification() {
        // Initialize the database
        insertedTrxNotification = trxNotificationRepository.save(trxNotification).block();
        trxNotificationSearchRepository.save(trxNotification).block();

        // Search the trxNotification
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + trxNotification.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(trxNotification.getId().intValue()))
            .jsonPath("$.[*].recipient")
            .value(hasItem(DEFAULT_RECIPIENT))
            .jsonPath("$.[*].messageType")
            .value(hasItem(DEFAULT_MESSAGE_TYPE))
            .jsonPath("$.[*].content")
            .value(hasItem(DEFAULT_CONTENT))
            .jsonPath("$.[*].sentAt")
            .value(hasItem(DEFAULT_SENT_AT.toString()));
    }

    protected long getRepositoryCount() {
        return trxNotificationRepository.count().block();
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

    protected TrxNotification getPersistedTrxNotification(TrxNotification trxNotification) {
        return trxNotificationRepository.findById(trxNotification.getId()).block();
    }

    protected void assertPersistedTrxNotificationToMatchAllProperties(TrxNotification expectedTrxNotification) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrxNotificationAllPropertiesEquals(expectedTrxNotification, getPersistedTrxNotification(expectedTrxNotification));
        assertTrxNotificationUpdatableFieldsEquals(expectedTrxNotification, getPersistedTrxNotification(expectedTrxNotification));
    }

    protected void assertPersistedTrxNotificationToMatchUpdatableProperties(TrxNotification expectedTrxNotification) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrxNotificationAllUpdatablePropertiesEquals(expectedTrxNotification, getPersistedTrxNotification(expectedTrxNotification));
        assertTrxNotificationUpdatableFieldsEquals(expectedTrxNotification, getPersistedTrxNotification(expectedTrxNotification));
    }
}
