package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.TournamentAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Tournament;
import com.mycompany.myapp.domain.enumeration.TournamentStatus;
import com.mycompany.myapp.domain.enumeration.TournamentType;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.TournamentRepository;
import com.mycompany.myapp.repository.search.TournamentSearchRepository;
import java.math.BigDecimal;
import java.time.Duration;
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
 * Integration tests for the {@link TournamentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TournamentResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final TournamentType DEFAULT_TYPE = TournamentType.TEAM;
    private static final TournamentType UPDATED_TYPE = TournamentType.SOLO;

    private static final BigDecimal DEFAULT_PRIZE_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRIZE_AMOUNT = new BigDecimal(2);

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION = "BBBBBBBBBB";

    private static final Integer DEFAULT_MAX_PARTICIPANTS = 1;
    private static final Integer UPDATED_MAX_PARTICIPANTS = 2;

    private static final TournamentStatus DEFAULT_STATUS = TournamentStatus.UPCOMING;
    private static final TournamentStatus UPDATED_STATUS = TournamentStatus.IN_PROGRESS;

    private static final String ENTITY_API_URL = "/api/tournaments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/tournaments/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Mock
    private TournamentRepository tournamentRepositoryMock;

    @Autowired
    private TournamentSearchRepository tournamentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Tournament tournament;

    private Tournament insertedTournament;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tournament createEntity(EntityManager em) {
        Tournament tournament = new Tournament()
            .name(DEFAULT_NAME)
            .type(DEFAULT_TYPE)
            .prizeAmount(DEFAULT_PRIZE_AMOUNT)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .location(DEFAULT_LOCATION)
            .maxParticipants(DEFAULT_MAX_PARTICIPANTS)
            .status(DEFAULT_STATUS);
        return tournament;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tournament createUpdatedEntity(EntityManager em) {
        Tournament tournament = new Tournament()
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .prizeAmount(UPDATED_PRIZE_AMOUNT)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .location(UPDATED_LOCATION)
            .maxParticipants(UPDATED_MAX_PARTICIPANTS)
            .status(UPDATED_STATUS);
        return tournament;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Tournament.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        tournament = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedTournament != null) {
            tournamentRepository.delete(insertedTournament).block();
            tournamentSearchRepository.delete(insertedTournament).block();
            insertedTournament = null;
        }
        deleteEntities(em);
    }

    @Test
    void createTournament() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tournamentSearchRepository.findAll().collectList().block());
        // Create the Tournament
        var returnedTournament = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tournament))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Tournament.class)
            .returnResult()
            .getResponseBody();

        // Validate the Tournament in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertTournamentUpdatableFieldsEquals(returnedTournament, getPersistedTournament(returnedTournament));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(tournamentSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedTournament = returnedTournament;
    }

    @Test
    void createTournamentWithExistingId() throws Exception {
        // Create the Tournament with an existing ID
        tournament.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tournamentSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tournament))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tournament in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tournamentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tournamentSearchRepository.findAll().collectList().block());
        // set the field null
        tournament.setName(null);

        // Create the Tournament, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tournament))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tournamentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tournamentSearchRepository.findAll().collectList().block());
        // set the field null
        tournament.setStartDate(null);

        // Create the Tournament, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tournament))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tournamentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkEndDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tournamentSearchRepository.findAll().collectList().block());
        // set the field null
        tournament.setEndDate(null);

        // Create the Tournament, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tournament))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tournamentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllTournamentsAsStream() {
        // Initialize the database
        tournamentRepository.save(tournament).block();

        List<Tournament> tournamentList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Tournament.class)
            .getResponseBody()
            .filter(tournament::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(tournamentList).isNotNull();
        assertThat(tournamentList).hasSize(1);
        Tournament testTournament = tournamentList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertTournamentAllPropertiesEquals(tournament, testTournament);
        assertTournamentUpdatableFieldsEquals(tournament, testTournament);
    }

    @Test
    void getAllTournaments() {
        // Initialize the database
        insertedTournament = tournamentRepository.save(tournament).block();

        // Get all the tournamentList
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
            .value(hasItem(tournament.getId().intValue()))
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
    void getAllTournamentsWithEagerRelationshipsIsEnabled() {
        when(tournamentRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(tournamentRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTournamentsWithEagerRelationshipsIsNotEnabled() {
        when(tournamentRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(tournamentRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getTournament() {
        // Initialize the database
        insertedTournament = tournamentRepository.save(tournament).block();

        // Get the tournament
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, tournament.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(tournament.getId().intValue()))
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
    void getNonExistingTournament() {
        // Get the tournament
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTournament() throws Exception {
        // Initialize the database
        insertedTournament = tournamentRepository.save(tournament).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        tournamentSearchRepository.save(tournament).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tournamentSearchRepository.findAll().collectList().block());

        // Update the tournament
        Tournament updatedTournament = tournamentRepository.findById(tournament.getId()).block();
        updatedTournament
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .prizeAmount(UPDATED_PRIZE_AMOUNT)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .location(UPDATED_LOCATION)
            .maxParticipants(UPDATED_MAX_PARTICIPANTS)
            .status(UPDATED_STATUS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedTournament.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedTournament))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tournament in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTournamentToMatchAllProperties(updatedTournament);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(tournamentSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Tournament> tournamentSearchList = Streamable.of(tournamentSearchRepository.findAll().collectList().block()).toList();
                Tournament testTournamentSearch = tournamentSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertTournamentAllPropertiesEquals(testTournamentSearch, updatedTournament);
                assertTournamentUpdatableFieldsEquals(testTournamentSearch, updatedTournament);
            });
    }

    @Test
    void putNonExistingTournament() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tournamentSearchRepository.findAll().collectList().block());
        tournament.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, tournament.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tournament))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tournament in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tournamentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchTournament() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tournamentSearchRepository.findAll().collectList().block());
        tournament.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tournament))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tournament in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tournamentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamTournament() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tournamentSearchRepository.findAll().collectList().block());
        tournament.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tournament))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Tournament in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tournamentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateTournamentWithPatch() throws Exception {
        // Initialize the database
        insertedTournament = tournamentRepository.save(tournament).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tournament using partial update
        Tournament partialUpdatedTournament = new Tournament();
        partialUpdatedTournament.setId(tournament.getId());

        partialUpdatedTournament
            .type(UPDATED_TYPE)
            .prizeAmount(UPDATED_PRIZE_AMOUNT)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTournament.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTournament))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tournament in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTournamentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTournament, tournament),
            getPersistedTournament(tournament)
        );
    }

    @Test
    void fullUpdateTournamentWithPatch() throws Exception {
        // Initialize the database
        insertedTournament = tournamentRepository.save(tournament).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tournament using partial update
        Tournament partialUpdatedTournament = new Tournament();
        partialUpdatedTournament.setId(tournament.getId());

        partialUpdatedTournament
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
            .uri(ENTITY_API_URL_ID, partialUpdatedTournament.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTournament))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tournament in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTournamentUpdatableFieldsEquals(partialUpdatedTournament, getPersistedTournament(partialUpdatedTournament));
    }

    @Test
    void patchNonExistingTournament() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tournamentSearchRepository.findAll().collectList().block());
        tournament.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, tournament.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(tournament))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tournament in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tournamentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchTournament() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tournamentSearchRepository.findAll().collectList().block());
        tournament.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(tournament))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tournament in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tournamentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamTournament() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tournamentSearchRepository.findAll().collectList().block());
        tournament.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(tournament))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Tournament in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tournamentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteTournament() {
        // Initialize the database
        insertedTournament = tournamentRepository.save(tournament).block();
        tournamentRepository.save(tournament).block();
        tournamentSearchRepository.save(tournament).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tournamentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the tournament
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, tournament.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tournamentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchTournament() {
        // Initialize the database
        insertedTournament = tournamentRepository.save(tournament).block();
        tournamentSearchRepository.save(tournament).block();

        // Search the tournament
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + tournament.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(tournament.getId().intValue()))
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
        return tournamentRepository.count().block();
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

    protected Tournament getPersistedTournament(Tournament tournament) {
        return tournamentRepository.findById(tournament.getId()).block();
    }

    protected void assertPersistedTournamentToMatchAllProperties(Tournament expectedTournament) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTournamentAllPropertiesEquals(expectedTournament, getPersistedTournament(expectedTournament));
        assertTournamentUpdatableFieldsEquals(expectedTournament, getPersistedTournament(expectedTournament));
    }

    protected void assertPersistedTournamentToMatchUpdatableProperties(Tournament expectedTournament) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTournamentAllUpdatablePropertiesEquals(expectedTournament, getPersistedTournament(expectedTournament));
        assertTournamentUpdatableFieldsEquals(expectedTournament, getPersistedTournament(expectedTournament));
    }
}
