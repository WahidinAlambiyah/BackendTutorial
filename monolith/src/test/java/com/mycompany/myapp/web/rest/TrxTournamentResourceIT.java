package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.TrxTournamentAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.TrxEvent;
import com.mycompany.myapp.domain.TrxTournament;
import com.mycompany.myapp.domain.enumeration.TournamentStatus;
import com.mycompany.myapp.domain.enumeration.TournamentType;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.TrxEventRepository;
import com.mycompany.myapp.repository.TrxTournamentRepository;
import com.mycompany.myapp.repository.search.TrxTournamentSearchRepository;
import com.mycompany.myapp.service.TrxTournamentService;
import com.mycompany.myapp.service.dto.TrxTournamentDTO;
import com.mycompany.myapp.service.mapper.TrxTournamentMapper;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

/**
 * Integration tests for the {@link TrxTournamentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TrxTournamentResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final TournamentType DEFAULT_TYPE = TournamentType.TEAM;
    private static final TournamentType UPDATED_TYPE = TournamentType.SOLO;

    private static final BigDecimal DEFAULT_PRIZE_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRIZE_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_PRIZE_AMOUNT = new BigDecimal(1 - 1);

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION = "BBBBBBBBBB";

    private static final Integer DEFAULT_MAX_PARTICIPANTS = 1;
    private static final Integer UPDATED_MAX_PARTICIPANTS = 2;
    private static final Integer SMALLER_MAX_PARTICIPANTS = 1 - 1;

    private static final TournamentStatus DEFAULT_STATUS = TournamentStatus.UPCOMING;
    private static final TournamentStatus UPDATED_STATUS = TournamentStatus.IN_PROGRESS;

    private static final String ENTITY_API_URL = "/api/trx-tournaments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/trx-tournaments/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TrxTournamentRepository trxTournamentRepository;

    @Mock
    private TrxTournamentRepository trxTournamentRepositoryMock;

    @Autowired
    private TrxTournamentMapper trxTournamentMapper;

    @Mock
    private TrxTournamentService trxTournamentServiceMock;

    @Autowired
    private TrxTournamentSearchRepository trxTournamentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TrxTournament trxTournament;

    private TrxTournament insertedTrxTournament;

    @Autowired
    private TrxEventRepository trxEventRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrxTournament createEntity(EntityManager em) {
        TrxTournament trxTournament = new TrxTournament()
            .name(DEFAULT_NAME)
            .type(DEFAULT_TYPE)
            .prizeAmount(DEFAULT_PRIZE_AMOUNT)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .location(DEFAULT_LOCATION)
            .maxParticipants(DEFAULT_MAX_PARTICIPANTS)
            .status(DEFAULT_STATUS);
        return trxTournament;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrxTournament createUpdatedEntity(EntityManager em) {
        TrxTournament trxTournament = new TrxTournament()
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .prizeAmount(UPDATED_PRIZE_AMOUNT)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .location(UPDATED_LOCATION)
            .maxParticipants(UPDATED_MAX_PARTICIPANTS)
            .status(UPDATED_STATUS);
        return trxTournament;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TrxTournament.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        trxTournament = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedTrxTournament != null) {
            trxTournamentRepository.delete(insertedTrxTournament).block();
            trxTournamentSearchRepository.delete(insertedTrxTournament).block();
            insertedTrxTournament = null;
        }
        deleteEntities(em);
    }

    @Test
    void createTrxTournament() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxTournamentSearchRepository.findAll().collectList().block());
        // Create the TrxTournament
        TrxTournamentDTO trxTournamentDTO = trxTournamentMapper.toDto(trxTournament);
        var returnedTrxTournamentDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxTournamentDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TrxTournamentDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the TrxTournament in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTrxTournament = trxTournamentMapper.toEntity(returnedTrxTournamentDTO);
        assertTrxTournamentUpdatableFieldsEquals(returnedTrxTournament, getPersistedTrxTournament(returnedTrxTournament));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxTournamentSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedTrxTournament = returnedTrxTournament;
    }

    @Test
    void createTrxTournamentWithExistingId() throws Exception {
        // Create the TrxTournament with an existing ID
        trxTournament.setId(1L);
        TrxTournamentDTO trxTournamentDTO = trxTournamentMapper.toDto(trxTournament);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxTournamentSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxTournamentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxTournament in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxTournamentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxTournamentSearchRepository.findAll().collectList().block());
        // set the field null
        trxTournament.setName(null);

        // Create the TrxTournament, which fails.
        TrxTournamentDTO trxTournamentDTO = trxTournamentMapper.toDto(trxTournament);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxTournamentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxTournamentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxTournamentSearchRepository.findAll().collectList().block());
        // set the field null
        trxTournament.setStartDate(null);

        // Create the TrxTournament, which fails.
        TrxTournamentDTO trxTournamentDTO = trxTournamentMapper.toDto(trxTournament);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxTournamentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxTournamentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkEndDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxTournamentSearchRepository.findAll().collectList().block());
        // set the field null
        trxTournament.setEndDate(null);

        // Create the TrxTournament, which fails.
        TrxTournamentDTO trxTournamentDTO = trxTournamentMapper.toDto(trxTournament);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxTournamentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxTournamentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllTrxTournaments() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList
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
            .value(hasItem(trxTournament.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE.toString()))
            .jsonPath("$.[*].prizeAmount")
            .value(hasItem(sameNumber(DEFAULT_PRIZE_AMOUNT)))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))
            .jsonPath("$.[*].endDate")
            .value(hasItem(DEFAULT_END_DATE.toString()))
            .jsonPath("$.[*].location")
            .value(hasItem(DEFAULT_LOCATION))
            .jsonPath("$.[*].maxParticipants")
            .value(hasItem(DEFAULT_MAX_PARTICIPANTS))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTrxTournamentsWithEagerRelationshipsIsEnabled() {
        when(trxTournamentServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(trxTournamentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTrxTournamentsWithEagerRelationshipsIsNotEnabled() {
        when(trxTournamentServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(trxTournamentRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getTrxTournament() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get the trxTournament
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, trxTournament.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(trxTournament.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.type")
            .value(is(DEFAULT_TYPE.toString()))
            .jsonPath("$.prizeAmount")
            .value(is(sameNumber(DEFAULT_PRIZE_AMOUNT)))
            .jsonPath("$.startDate")
            .value(is(DEFAULT_START_DATE.toString()))
            .jsonPath("$.endDate")
            .value(is(DEFAULT_END_DATE.toString()))
            .jsonPath("$.location")
            .value(is(DEFAULT_LOCATION))
            .jsonPath("$.maxParticipants")
            .value(is(DEFAULT_MAX_PARTICIPANTS))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()));
    }

    @Test
    void getTrxTournamentsByIdFiltering() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        Long id = trxTournament.getId();

        defaultTrxTournamentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTrxTournamentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTrxTournamentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllTrxTournamentsByNameIsEqualToSomething() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where name equals to
        defaultTrxTournamentFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllTrxTournamentsByNameIsInShouldWork() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where name in
        defaultTrxTournamentFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllTrxTournamentsByNameIsNullOrNotNull() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where name is not null
        defaultTrxTournamentFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllTrxTournamentsByNameContainsSomething() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where name contains
        defaultTrxTournamentFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllTrxTournamentsByNameNotContainsSomething() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where name does not contain
        defaultTrxTournamentFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllTrxTournamentsByTypeIsEqualToSomething() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where type equals to
        defaultTrxTournamentFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    void getAllTrxTournamentsByTypeIsInShouldWork() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where type in
        defaultTrxTournamentFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    void getAllTrxTournamentsByTypeIsNullOrNotNull() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where type is not null
        defaultTrxTournamentFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    void getAllTrxTournamentsByPrizeAmountIsEqualToSomething() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where prizeAmount equals to
        defaultTrxTournamentFiltering("prizeAmount.equals=" + DEFAULT_PRIZE_AMOUNT, "prizeAmount.equals=" + UPDATED_PRIZE_AMOUNT);
    }

    @Test
    void getAllTrxTournamentsByPrizeAmountIsInShouldWork() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where prizeAmount in
        defaultTrxTournamentFiltering(
            "prizeAmount.in=" + DEFAULT_PRIZE_AMOUNT + "," + UPDATED_PRIZE_AMOUNT,
            "prizeAmount.in=" + UPDATED_PRIZE_AMOUNT
        );
    }

    @Test
    void getAllTrxTournamentsByPrizeAmountIsNullOrNotNull() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where prizeAmount is not null
        defaultTrxTournamentFiltering("prizeAmount.specified=true", "prizeAmount.specified=false");
    }

    @Test
    void getAllTrxTournamentsByPrizeAmountIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where prizeAmount is greater than or equal to
        defaultTrxTournamentFiltering(
            "prizeAmount.greaterThanOrEqual=" + DEFAULT_PRIZE_AMOUNT,
            "prizeAmount.greaterThanOrEqual=" + UPDATED_PRIZE_AMOUNT
        );
    }

    @Test
    void getAllTrxTournamentsByPrizeAmountIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where prizeAmount is less than or equal to
        defaultTrxTournamentFiltering(
            "prizeAmount.lessThanOrEqual=" + DEFAULT_PRIZE_AMOUNT,
            "prizeAmount.lessThanOrEqual=" + SMALLER_PRIZE_AMOUNT
        );
    }

    @Test
    void getAllTrxTournamentsByPrizeAmountIsLessThanSomething() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where prizeAmount is less than
        defaultTrxTournamentFiltering("prizeAmount.lessThan=" + UPDATED_PRIZE_AMOUNT, "prizeAmount.lessThan=" + DEFAULT_PRIZE_AMOUNT);
    }

    @Test
    void getAllTrxTournamentsByPrizeAmountIsGreaterThanSomething() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where prizeAmount is greater than
        defaultTrxTournamentFiltering("prizeAmount.greaterThan=" + SMALLER_PRIZE_AMOUNT, "prizeAmount.greaterThan=" + DEFAULT_PRIZE_AMOUNT);
    }

    @Test
    void getAllTrxTournamentsByStartDateIsEqualToSomething() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where startDate equals to
        defaultTrxTournamentFiltering("startDate.equals=" + DEFAULT_START_DATE, "startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    void getAllTrxTournamentsByStartDateIsInShouldWork() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where startDate in
        defaultTrxTournamentFiltering(
            "startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE,
            "startDate.in=" + UPDATED_START_DATE
        );
    }

    @Test
    void getAllTrxTournamentsByStartDateIsNullOrNotNull() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where startDate is not null
        defaultTrxTournamentFiltering("startDate.specified=true", "startDate.specified=false");
    }

    @Test
    void getAllTrxTournamentsByEndDateIsEqualToSomething() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where endDate equals to
        defaultTrxTournamentFiltering("endDate.equals=" + DEFAULT_END_DATE, "endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    void getAllTrxTournamentsByEndDateIsInShouldWork() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where endDate in
        defaultTrxTournamentFiltering("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE, "endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    void getAllTrxTournamentsByEndDateIsNullOrNotNull() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where endDate is not null
        defaultTrxTournamentFiltering("endDate.specified=true", "endDate.specified=false");
    }

    @Test
    void getAllTrxTournamentsByLocationIsEqualToSomething() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where location equals to
        defaultTrxTournamentFiltering("location.equals=" + DEFAULT_LOCATION, "location.equals=" + UPDATED_LOCATION);
    }

    @Test
    void getAllTrxTournamentsByLocationIsInShouldWork() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where location in
        defaultTrxTournamentFiltering("location.in=" + DEFAULT_LOCATION + "," + UPDATED_LOCATION, "location.in=" + UPDATED_LOCATION);
    }

    @Test
    void getAllTrxTournamentsByLocationIsNullOrNotNull() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where location is not null
        defaultTrxTournamentFiltering("location.specified=true", "location.specified=false");
    }

    @Test
    void getAllTrxTournamentsByLocationContainsSomething() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where location contains
        defaultTrxTournamentFiltering("location.contains=" + DEFAULT_LOCATION, "location.contains=" + UPDATED_LOCATION);
    }

    @Test
    void getAllTrxTournamentsByLocationNotContainsSomething() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where location does not contain
        defaultTrxTournamentFiltering("location.doesNotContain=" + UPDATED_LOCATION, "location.doesNotContain=" + DEFAULT_LOCATION);
    }

    @Test
    void getAllTrxTournamentsByMaxParticipantsIsEqualToSomething() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where maxParticipants equals to
        defaultTrxTournamentFiltering(
            "maxParticipants.equals=" + DEFAULT_MAX_PARTICIPANTS,
            "maxParticipants.equals=" + UPDATED_MAX_PARTICIPANTS
        );
    }

    @Test
    void getAllTrxTournamentsByMaxParticipantsIsInShouldWork() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where maxParticipants in
        defaultTrxTournamentFiltering(
            "maxParticipants.in=" + DEFAULT_MAX_PARTICIPANTS + "," + UPDATED_MAX_PARTICIPANTS,
            "maxParticipants.in=" + UPDATED_MAX_PARTICIPANTS
        );
    }

    @Test
    void getAllTrxTournamentsByMaxParticipantsIsNullOrNotNull() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where maxParticipants is not null
        defaultTrxTournamentFiltering("maxParticipants.specified=true", "maxParticipants.specified=false");
    }

    @Test
    void getAllTrxTournamentsByMaxParticipantsIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where maxParticipants is greater than or equal to
        defaultTrxTournamentFiltering(
            "maxParticipants.greaterThanOrEqual=" + DEFAULT_MAX_PARTICIPANTS,
            "maxParticipants.greaterThanOrEqual=" + UPDATED_MAX_PARTICIPANTS
        );
    }

    @Test
    void getAllTrxTournamentsByMaxParticipantsIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where maxParticipants is less than or equal to
        defaultTrxTournamentFiltering(
            "maxParticipants.lessThanOrEqual=" + DEFAULT_MAX_PARTICIPANTS,
            "maxParticipants.lessThanOrEqual=" + SMALLER_MAX_PARTICIPANTS
        );
    }

    @Test
    void getAllTrxTournamentsByMaxParticipantsIsLessThanSomething() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where maxParticipants is less than
        defaultTrxTournamentFiltering(
            "maxParticipants.lessThan=" + UPDATED_MAX_PARTICIPANTS,
            "maxParticipants.lessThan=" + DEFAULT_MAX_PARTICIPANTS
        );
    }

    @Test
    void getAllTrxTournamentsByMaxParticipantsIsGreaterThanSomething() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where maxParticipants is greater than
        defaultTrxTournamentFiltering(
            "maxParticipants.greaterThan=" + SMALLER_MAX_PARTICIPANTS,
            "maxParticipants.greaterThan=" + DEFAULT_MAX_PARTICIPANTS
        );
    }

    @Test
    void getAllTrxTournamentsByStatusIsEqualToSomething() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where status equals to
        defaultTrxTournamentFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    void getAllTrxTournamentsByStatusIsInShouldWork() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where status in
        defaultTrxTournamentFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    void getAllTrxTournamentsByStatusIsNullOrNotNull() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        // Get all the trxTournamentList where status is not null
        defaultTrxTournamentFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    void getAllTrxTournamentsByEventIsEqualToSomething() {
        TrxEvent event = TrxEventResourceIT.createEntity(em);
        trxEventRepository.save(event).block();
        Long eventId = event.getId();
        trxTournament.setEventId(eventId);
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();
        // Get all the trxTournamentList where event equals to eventId
        defaultTrxTournamentShouldBeFound("eventId.equals=" + eventId);

        // Get all the trxTournamentList where event equals to (eventId + 1)
        defaultTrxTournamentShouldNotBeFound("eventId.equals=" + (eventId + 1));
    }

    private void defaultTrxTournamentFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultTrxTournamentShouldBeFound(shouldBeFound);
        defaultTrxTournamentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTrxTournamentShouldBeFound(String filter) {
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
            .value(hasItem(trxTournament.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE.toString()))
            .jsonPath("$.[*].prizeAmount")
            .value(hasItem(sameNumber(DEFAULT_PRIZE_AMOUNT)))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))
            .jsonPath("$.[*].endDate")
            .value(hasItem(DEFAULT_END_DATE.toString()))
            .jsonPath("$.[*].location")
            .value(hasItem(DEFAULT_LOCATION))
            .jsonPath("$.[*].maxParticipants")
            .value(hasItem(DEFAULT_MAX_PARTICIPANTS))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));

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
    private void defaultTrxTournamentShouldNotBeFound(String filter) {
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
    void getNonExistingTrxTournament() {
        // Get the trxTournament
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTrxTournament() throws Exception {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        trxTournamentSearchRepository.save(trxTournament).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxTournamentSearchRepository.findAll().collectList().block());

        // Update the trxTournament
        TrxTournament updatedTrxTournament = trxTournamentRepository.findById(trxTournament.getId()).block();
        updatedTrxTournament
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .prizeAmount(UPDATED_PRIZE_AMOUNT)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .location(UPDATED_LOCATION)
            .maxParticipants(UPDATED_MAX_PARTICIPANTS)
            .status(UPDATED_STATUS);
        TrxTournamentDTO trxTournamentDTO = trxTournamentMapper.toDto(updatedTrxTournament);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trxTournamentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxTournamentDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxTournament in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTrxTournamentToMatchAllProperties(updatedTrxTournament);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxTournamentSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TrxTournament> trxTournamentSearchList = Streamable.of(
                    trxTournamentSearchRepository.findAll().collectList().block()
                ).toList();
                TrxTournament testTrxTournamentSearch = trxTournamentSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertTrxTournamentAllPropertiesEquals(testTrxTournamentSearch, updatedTrxTournament);
                assertTrxTournamentUpdatableFieldsEquals(testTrxTournamentSearch, updatedTrxTournament);
            });
    }

    @Test
    void putNonExistingTrxTournament() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxTournamentSearchRepository.findAll().collectList().block());
        trxTournament.setId(longCount.incrementAndGet());

        // Create the TrxTournament
        TrxTournamentDTO trxTournamentDTO = trxTournamentMapper.toDto(trxTournament);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trxTournamentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxTournamentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxTournament in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxTournamentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchTrxTournament() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxTournamentSearchRepository.findAll().collectList().block());
        trxTournament.setId(longCount.incrementAndGet());

        // Create the TrxTournament
        TrxTournamentDTO trxTournamentDTO = trxTournamentMapper.toDto(trxTournament);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxTournamentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxTournament in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxTournamentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamTrxTournament() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxTournamentSearchRepository.findAll().collectList().block());
        trxTournament.setId(longCount.incrementAndGet());

        // Create the TrxTournament
        TrxTournamentDTO trxTournamentDTO = trxTournamentMapper.toDto(trxTournament);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxTournamentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrxTournament in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxTournamentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateTrxTournamentWithPatch() throws Exception {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trxTournament using partial update
        TrxTournament partialUpdatedTrxTournament = new TrxTournament();
        partialUpdatedTrxTournament.setId(trxTournament.getId());

        partialUpdatedTrxTournament.startDate(UPDATED_START_DATE).maxParticipants(UPDATED_MAX_PARTICIPANTS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrxTournament.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrxTournament))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxTournament in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrxTournamentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTrxTournament, trxTournament),
            getPersistedTrxTournament(trxTournament)
        );
    }

    @Test
    void fullUpdateTrxTournamentWithPatch() throws Exception {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trxTournament using partial update
        TrxTournament partialUpdatedTrxTournament = new TrxTournament();
        partialUpdatedTrxTournament.setId(trxTournament.getId());

        partialUpdatedTrxTournament
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .prizeAmount(UPDATED_PRIZE_AMOUNT)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .location(UPDATED_LOCATION)
            .maxParticipants(UPDATED_MAX_PARTICIPANTS)
            .status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrxTournament.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrxTournament))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxTournament in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrxTournamentUpdatableFieldsEquals(partialUpdatedTrxTournament, getPersistedTrxTournament(partialUpdatedTrxTournament));
    }

    @Test
    void patchNonExistingTrxTournament() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxTournamentSearchRepository.findAll().collectList().block());
        trxTournament.setId(longCount.incrementAndGet());

        // Create the TrxTournament
        TrxTournamentDTO trxTournamentDTO = trxTournamentMapper.toDto(trxTournament);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, trxTournamentDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxTournamentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxTournament in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxTournamentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchTrxTournament() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxTournamentSearchRepository.findAll().collectList().block());
        trxTournament.setId(longCount.incrementAndGet());

        // Create the TrxTournament
        TrxTournamentDTO trxTournamentDTO = trxTournamentMapper.toDto(trxTournament);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxTournamentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxTournament in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxTournamentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamTrxTournament() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxTournamentSearchRepository.findAll().collectList().block());
        trxTournament.setId(longCount.incrementAndGet());

        // Create the TrxTournament
        TrxTournamentDTO trxTournamentDTO = trxTournamentMapper.toDto(trxTournament);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxTournamentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrxTournament in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxTournamentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteTrxTournament() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();
        trxTournamentRepository.save(trxTournament).block();
        trxTournamentSearchRepository.save(trxTournament).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxTournamentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the trxTournament
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, trxTournament.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxTournamentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchTrxTournament() {
        // Initialize the database
        insertedTrxTournament = trxTournamentRepository.save(trxTournament).block();
        trxTournamentSearchRepository.save(trxTournament).block();

        // Search the trxTournament
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + trxTournament.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(trxTournament.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE.toString()))
            .jsonPath("$.[*].prizeAmount")
            .value(hasItem(sameNumber(DEFAULT_PRIZE_AMOUNT)))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))
            .jsonPath("$.[*].endDate")
            .value(hasItem(DEFAULT_END_DATE.toString()))
            .jsonPath("$.[*].location")
            .value(hasItem(DEFAULT_LOCATION))
            .jsonPath("$.[*].maxParticipants")
            .value(hasItem(DEFAULT_MAX_PARTICIPANTS))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }

    protected long getRepositoryCount() {
        return trxTournamentRepository.count().block();
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

    protected TrxTournament getPersistedTrxTournament(TrxTournament trxTournament) {
        return trxTournamentRepository.findById(trxTournament.getId()).block();
    }

    protected void assertPersistedTrxTournamentToMatchAllProperties(TrxTournament expectedTrxTournament) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrxTournamentAllPropertiesEquals(expectedTrxTournament, getPersistedTrxTournament(expectedTrxTournament));
        assertTrxTournamentUpdatableFieldsEquals(expectedTrxTournament, getPersistedTrxTournament(expectedTrxTournament));
    }

    protected void assertPersistedTrxTournamentToMatchUpdatableProperties(TrxTournament expectedTrxTournament) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrxTournamentAllUpdatablePropertiesEquals(expectedTrxTournament, getPersistedTrxTournament(expectedTrxTournament));
        assertTrxTournamentUpdatableFieldsEquals(expectedTrxTournament, getPersistedTrxTournament(expectedTrxTournament));
    }
}
