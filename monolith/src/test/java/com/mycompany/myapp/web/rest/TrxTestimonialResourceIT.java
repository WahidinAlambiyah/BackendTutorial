package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.TrxTestimonialAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.TrxTestimonial;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.TrxTestimonialRepository;
import com.mycompany.myapp.repository.search.TrxTestimonialSearchRepository;
import com.mycompany.myapp.service.dto.TrxTestimonialDTO;
import com.mycompany.myapp.service.mapper.TrxTestimonialMapper;
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
 * Integration tests for the {@link TrxTestimonialResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TrxTestimonialResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FEEDBACK = "AAAAAAAAAA";
    private static final String UPDATED_FEEDBACK = "BBBBBBBBBB";

    private static final Integer DEFAULT_RATING = 1;
    private static final Integer UPDATED_RATING = 2;
    private static final Integer SMALLER_RATING = 1 - 1;

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/trx-testimonials";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/trx-testimonials/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TrxTestimonialRepository trxTestimonialRepository;

    @Autowired
    private TrxTestimonialMapper trxTestimonialMapper;

    @Autowired
    private TrxTestimonialSearchRepository trxTestimonialSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TrxTestimonial trxTestimonial;

    private TrxTestimonial insertedTrxTestimonial;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrxTestimonial createEntity(EntityManager em) {
        TrxTestimonial trxTestimonial = new TrxTestimonial()
            .name(DEFAULT_NAME)
            .feedback(DEFAULT_FEEDBACK)
            .rating(DEFAULT_RATING)
            .date(DEFAULT_DATE);
        return trxTestimonial;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrxTestimonial createUpdatedEntity(EntityManager em) {
        TrxTestimonial trxTestimonial = new TrxTestimonial()
            .name(UPDATED_NAME)
            .feedback(UPDATED_FEEDBACK)
            .rating(UPDATED_RATING)
            .date(UPDATED_DATE);
        return trxTestimonial;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TrxTestimonial.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        trxTestimonial = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedTrxTestimonial != null) {
            trxTestimonialRepository.delete(insertedTrxTestimonial).block();
            trxTestimonialSearchRepository.delete(insertedTrxTestimonial).block();
            insertedTrxTestimonial = null;
        }
        deleteEntities(em);
    }

    @Test
    void createTrxTestimonial() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxTestimonialSearchRepository.findAll().collectList().block());
        // Create the TrxTestimonial
        TrxTestimonialDTO trxTestimonialDTO = trxTestimonialMapper.toDto(trxTestimonial);
        var returnedTrxTestimonialDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxTestimonialDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TrxTestimonialDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the TrxTestimonial in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTrxTestimonial = trxTestimonialMapper.toEntity(returnedTrxTestimonialDTO);
        assertTrxTestimonialUpdatableFieldsEquals(returnedTrxTestimonial, getPersistedTrxTestimonial(returnedTrxTestimonial));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxTestimonialSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedTrxTestimonial = returnedTrxTestimonial;
    }

    @Test
    void createTrxTestimonialWithExistingId() throws Exception {
        // Create the TrxTestimonial with an existing ID
        trxTestimonial.setId(1L);
        TrxTestimonialDTO trxTestimonialDTO = trxTestimonialMapper.toDto(trxTestimonial);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxTestimonialSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxTestimonialDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxTestimonial in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxTestimonialSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxTestimonialSearchRepository.findAll().collectList().block());
        // set the field null
        trxTestimonial.setName(null);

        // Create the TrxTestimonial, which fails.
        TrxTestimonialDTO trxTestimonialDTO = trxTestimonialMapper.toDto(trxTestimonial);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxTestimonialDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxTestimonialSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkRatingIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxTestimonialSearchRepository.findAll().collectList().block());
        // set the field null
        trxTestimonial.setRating(null);

        // Create the TrxTestimonial, which fails.
        TrxTestimonialDTO trxTestimonialDTO = trxTestimonialMapper.toDto(trxTestimonial);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxTestimonialDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxTestimonialSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxTestimonialSearchRepository.findAll().collectList().block());
        // set the field null
        trxTestimonial.setDate(null);

        // Create the TrxTestimonial, which fails.
        TrxTestimonialDTO trxTestimonialDTO = trxTestimonialMapper.toDto(trxTestimonial);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxTestimonialDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxTestimonialSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllTrxTestimonials() {
        // Initialize the database
        insertedTrxTestimonial = trxTestimonialRepository.save(trxTestimonial).block();

        // Get all the trxTestimonialList
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
            .value(hasItem(trxTestimonial.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].feedback")
            .value(hasItem(DEFAULT_FEEDBACK.toString()))
            .jsonPath("$.[*].rating")
            .value(hasItem(DEFAULT_RATING))
            .jsonPath("$.[*].date")
            .value(hasItem(DEFAULT_DATE.toString()));
    }

    @Test
    void getTrxTestimonial() {
        // Initialize the database
        insertedTrxTestimonial = trxTestimonialRepository.save(trxTestimonial).block();

        // Get the trxTestimonial
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, trxTestimonial.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(trxTestimonial.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.feedback")
            .value(is(DEFAULT_FEEDBACK.toString()))
            .jsonPath("$.rating")
            .value(is(DEFAULT_RATING))
            .jsonPath("$.date")
            .value(is(DEFAULT_DATE.toString()));
    }

    @Test
    void getTrxTestimonialsByIdFiltering() {
        // Initialize the database
        insertedTrxTestimonial = trxTestimonialRepository.save(trxTestimonial).block();

        Long id = trxTestimonial.getId();

        defaultTrxTestimonialFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTrxTestimonialFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTrxTestimonialFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllTrxTestimonialsByNameIsEqualToSomething() {
        // Initialize the database
        insertedTrxTestimonial = trxTestimonialRepository.save(trxTestimonial).block();

        // Get all the trxTestimonialList where name equals to
        defaultTrxTestimonialFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllTrxTestimonialsByNameIsInShouldWork() {
        // Initialize the database
        insertedTrxTestimonial = trxTestimonialRepository.save(trxTestimonial).block();

        // Get all the trxTestimonialList where name in
        defaultTrxTestimonialFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllTrxTestimonialsByNameIsNullOrNotNull() {
        // Initialize the database
        insertedTrxTestimonial = trxTestimonialRepository.save(trxTestimonial).block();

        // Get all the trxTestimonialList where name is not null
        defaultTrxTestimonialFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllTrxTestimonialsByNameContainsSomething() {
        // Initialize the database
        insertedTrxTestimonial = trxTestimonialRepository.save(trxTestimonial).block();

        // Get all the trxTestimonialList where name contains
        defaultTrxTestimonialFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllTrxTestimonialsByNameNotContainsSomething() {
        // Initialize the database
        insertedTrxTestimonial = trxTestimonialRepository.save(trxTestimonial).block();

        // Get all the trxTestimonialList where name does not contain
        defaultTrxTestimonialFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllTrxTestimonialsByRatingIsEqualToSomething() {
        // Initialize the database
        insertedTrxTestimonial = trxTestimonialRepository.save(trxTestimonial).block();

        // Get all the trxTestimonialList where rating equals to
        defaultTrxTestimonialFiltering("rating.equals=" + DEFAULT_RATING, "rating.equals=" + UPDATED_RATING);
    }

    @Test
    void getAllTrxTestimonialsByRatingIsInShouldWork() {
        // Initialize the database
        insertedTrxTestimonial = trxTestimonialRepository.save(trxTestimonial).block();

        // Get all the trxTestimonialList where rating in
        defaultTrxTestimonialFiltering("rating.in=" + DEFAULT_RATING + "," + UPDATED_RATING, "rating.in=" + UPDATED_RATING);
    }

    @Test
    void getAllTrxTestimonialsByRatingIsNullOrNotNull() {
        // Initialize the database
        insertedTrxTestimonial = trxTestimonialRepository.save(trxTestimonial).block();

        // Get all the trxTestimonialList where rating is not null
        defaultTrxTestimonialFiltering("rating.specified=true", "rating.specified=false");
    }

    @Test
    void getAllTrxTestimonialsByRatingIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxTestimonial = trxTestimonialRepository.save(trxTestimonial).block();

        // Get all the trxTestimonialList where rating is greater than or equal to
        defaultTrxTestimonialFiltering("rating.greaterThanOrEqual=" + DEFAULT_RATING, "rating.greaterThanOrEqual=" + (DEFAULT_RATING + 1));
    }

    @Test
    void getAllTrxTestimonialsByRatingIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxTestimonial = trxTestimonialRepository.save(trxTestimonial).block();

        // Get all the trxTestimonialList where rating is less than or equal to
        defaultTrxTestimonialFiltering("rating.lessThanOrEqual=" + DEFAULT_RATING, "rating.lessThanOrEqual=" + SMALLER_RATING);
    }

    @Test
    void getAllTrxTestimonialsByRatingIsLessThanSomething() {
        // Initialize the database
        insertedTrxTestimonial = trxTestimonialRepository.save(trxTestimonial).block();

        // Get all the trxTestimonialList where rating is less than
        defaultTrxTestimonialFiltering("rating.lessThan=" + (DEFAULT_RATING + 1), "rating.lessThan=" + DEFAULT_RATING);
    }

    @Test
    void getAllTrxTestimonialsByRatingIsGreaterThanSomething() {
        // Initialize the database
        insertedTrxTestimonial = trxTestimonialRepository.save(trxTestimonial).block();

        // Get all the trxTestimonialList where rating is greater than
        defaultTrxTestimonialFiltering("rating.greaterThan=" + SMALLER_RATING, "rating.greaterThan=" + DEFAULT_RATING);
    }

    @Test
    void getAllTrxTestimonialsByDateIsEqualToSomething() {
        // Initialize the database
        insertedTrxTestimonial = trxTestimonialRepository.save(trxTestimonial).block();

        // Get all the trxTestimonialList where date equals to
        defaultTrxTestimonialFiltering("date.equals=" + DEFAULT_DATE, "date.equals=" + UPDATED_DATE);
    }

    @Test
    void getAllTrxTestimonialsByDateIsInShouldWork() {
        // Initialize the database
        insertedTrxTestimonial = trxTestimonialRepository.save(trxTestimonial).block();

        // Get all the trxTestimonialList where date in
        defaultTrxTestimonialFiltering("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE, "date.in=" + UPDATED_DATE);
    }

    @Test
    void getAllTrxTestimonialsByDateIsNullOrNotNull() {
        // Initialize the database
        insertedTrxTestimonial = trxTestimonialRepository.save(trxTestimonial).block();

        // Get all the trxTestimonialList where date is not null
        defaultTrxTestimonialFiltering("date.specified=true", "date.specified=false");
    }

    private void defaultTrxTestimonialFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultTrxTestimonialShouldBeFound(shouldBeFound);
        defaultTrxTestimonialShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTrxTestimonialShouldBeFound(String filter) {
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
            .value(hasItem(trxTestimonial.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].feedback")
            .value(hasItem(DEFAULT_FEEDBACK.toString()))
            .jsonPath("$.[*].rating")
            .value(hasItem(DEFAULT_RATING))
            .jsonPath("$.[*].date")
            .value(hasItem(DEFAULT_DATE.toString()));

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
    private void defaultTrxTestimonialShouldNotBeFound(String filter) {
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
    void getNonExistingTrxTestimonial() {
        // Get the trxTestimonial
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTrxTestimonial() throws Exception {
        // Initialize the database
        insertedTrxTestimonial = trxTestimonialRepository.save(trxTestimonial).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        trxTestimonialSearchRepository.save(trxTestimonial).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxTestimonialSearchRepository.findAll().collectList().block());

        // Update the trxTestimonial
        TrxTestimonial updatedTrxTestimonial = trxTestimonialRepository.findById(trxTestimonial.getId()).block();
        updatedTrxTestimonial.name(UPDATED_NAME).feedback(UPDATED_FEEDBACK).rating(UPDATED_RATING).date(UPDATED_DATE);
        TrxTestimonialDTO trxTestimonialDTO = trxTestimonialMapper.toDto(updatedTrxTestimonial);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trxTestimonialDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxTestimonialDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxTestimonial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTrxTestimonialToMatchAllProperties(updatedTrxTestimonial);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxTestimonialSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TrxTestimonial> trxTestimonialSearchList = Streamable.of(
                    trxTestimonialSearchRepository.findAll().collectList().block()
                ).toList();
                TrxTestimonial testTrxTestimonialSearch = trxTestimonialSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertTrxTestimonialAllPropertiesEquals(testTrxTestimonialSearch, updatedTrxTestimonial);
                assertTrxTestimonialUpdatableFieldsEquals(testTrxTestimonialSearch, updatedTrxTestimonial);
            });
    }

    @Test
    void putNonExistingTrxTestimonial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxTestimonialSearchRepository.findAll().collectList().block());
        trxTestimonial.setId(longCount.incrementAndGet());

        // Create the TrxTestimonial
        TrxTestimonialDTO trxTestimonialDTO = trxTestimonialMapper.toDto(trxTestimonial);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trxTestimonialDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxTestimonialDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxTestimonial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxTestimonialSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchTrxTestimonial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxTestimonialSearchRepository.findAll().collectList().block());
        trxTestimonial.setId(longCount.incrementAndGet());

        // Create the TrxTestimonial
        TrxTestimonialDTO trxTestimonialDTO = trxTestimonialMapper.toDto(trxTestimonial);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxTestimonialDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxTestimonial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxTestimonialSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamTrxTestimonial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxTestimonialSearchRepository.findAll().collectList().block());
        trxTestimonial.setId(longCount.incrementAndGet());

        // Create the TrxTestimonial
        TrxTestimonialDTO trxTestimonialDTO = trxTestimonialMapper.toDto(trxTestimonial);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxTestimonialDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrxTestimonial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxTestimonialSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateTrxTestimonialWithPatch() throws Exception {
        // Initialize the database
        insertedTrxTestimonial = trxTestimonialRepository.save(trxTestimonial).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trxTestimonial using partial update
        TrxTestimonial partialUpdatedTrxTestimonial = new TrxTestimonial();
        partialUpdatedTrxTestimonial.setId(trxTestimonial.getId());

        partialUpdatedTrxTestimonial.feedback(UPDATED_FEEDBACK).date(UPDATED_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrxTestimonial.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrxTestimonial))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxTestimonial in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrxTestimonialUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTrxTestimonial, trxTestimonial),
            getPersistedTrxTestimonial(trxTestimonial)
        );
    }

    @Test
    void fullUpdateTrxTestimonialWithPatch() throws Exception {
        // Initialize the database
        insertedTrxTestimonial = trxTestimonialRepository.save(trxTestimonial).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trxTestimonial using partial update
        TrxTestimonial partialUpdatedTrxTestimonial = new TrxTestimonial();
        partialUpdatedTrxTestimonial.setId(trxTestimonial.getId());

        partialUpdatedTrxTestimonial.name(UPDATED_NAME).feedback(UPDATED_FEEDBACK).rating(UPDATED_RATING).date(UPDATED_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrxTestimonial.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrxTestimonial))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxTestimonial in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrxTestimonialUpdatableFieldsEquals(partialUpdatedTrxTestimonial, getPersistedTrxTestimonial(partialUpdatedTrxTestimonial));
    }

    @Test
    void patchNonExistingTrxTestimonial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxTestimonialSearchRepository.findAll().collectList().block());
        trxTestimonial.setId(longCount.incrementAndGet());

        // Create the TrxTestimonial
        TrxTestimonialDTO trxTestimonialDTO = trxTestimonialMapper.toDto(trxTestimonial);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, trxTestimonialDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxTestimonialDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxTestimonial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxTestimonialSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchTrxTestimonial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxTestimonialSearchRepository.findAll().collectList().block());
        trxTestimonial.setId(longCount.incrementAndGet());

        // Create the TrxTestimonial
        TrxTestimonialDTO trxTestimonialDTO = trxTestimonialMapper.toDto(trxTestimonial);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxTestimonialDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxTestimonial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxTestimonialSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamTrxTestimonial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxTestimonialSearchRepository.findAll().collectList().block());
        trxTestimonial.setId(longCount.incrementAndGet());

        // Create the TrxTestimonial
        TrxTestimonialDTO trxTestimonialDTO = trxTestimonialMapper.toDto(trxTestimonial);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxTestimonialDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrxTestimonial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxTestimonialSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteTrxTestimonial() {
        // Initialize the database
        insertedTrxTestimonial = trxTestimonialRepository.save(trxTestimonial).block();
        trxTestimonialRepository.save(trxTestimonial).block();
        trxTestimonialSearchRepository.save(trxTestimonial).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxTestimonialSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the trxTestimonial
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, trxTestimonial.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxTestimonialSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchTrxTestimonial() {
        // Initialize the database
        insertedTrxTestimonial = trxTestimonialRepository.save(trxTestimonial).block();
        trxTestimonialSearchRepository.save(trxTestimonial).block();

        // Search the trxTestimonial
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + trxTestimonial.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(trxTestimonial.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].feedback")
            .value(hasItem(DEFAULT_FEEDBACK.toString()))
            .jsonPath("$.[*].rating")
            .value(hasItem(DEFAULT_RATING))
            .jsonPath("$.[*].date")
            .value(hasItem(DEFAULT_DATE.toString()));
    }

    protected long getRepositoryCount() {
        return trxTestimonialRepository.count().block();
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

    protected TrxTestimonial getPersistedTrxTestimonial(TrxTestimonial trxTestimonial) {
        return trxTestimonialRepository.findById(trxTestimonial.getId()).block();
    }

    protected void assertPersistedTrxTestimonialToMatchAllProperties(TrxTestimonial expectedTrxTestimonial) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrxTestimonialAllPropertiesEquals(expectedTrxTestimonial, getPersistedTrxTestimonial(expectedTrxTestimonial));
        assertTrxTestimonialUpdatableFieldsEquals(expectedTrxTestimonial, getPersistedTrxTestimonial(expectedTrxTestimonial));
    }

    protected void assertPersistedTrxTestimonialToMatchUpdatableProperties(TrxTestimonial expectedTrxTestimonial) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrxTestimonialAllUpdatablePropertiesEquals(expectedTrxTestimonial, getPersistedTrxTestimonial(expectedTrxTestimonial));
        assertTrxTestimonialUpdatableFieldsEquals(expectedTrxTestimonial, getPersistedTrxTestimonial(expectedTrxTestimonial));
    }
}
