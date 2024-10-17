package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.MstLoyaltyProgramAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.MstCustomer;
import com.mycompany.myapp.domain.MstLoyaltyProgram;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MstCustomerRepository;
import com.mycompany.myapp.repository.MstLoyaltyProgramRepository;
import com.mycompany.myapp.repository.search.MstLoyaltyProgramSearchRepository;
import com.mycompany.myapp.service.dto.MstLoyaltyProgramDTO;
import com.mycompany.myapp.service.mapper.MstLoyaltyProgramMapper;
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
 * Integration tests for the {@link MstLoyaltyProgramResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MstLoyaltyProgramResourceIT {

    private static final Integer DEFAULT_POINTS_EARNED = 1;
    private static final Integer UPDATED_POINTS_EARNED = 2;
    private static final Integer SMALLER_POINTS_EARNED = 1 - 1;

    private static final String DEFAULT_MEMBERSHIP_TIER = "AAAAAAAAAA";
    private static final String UPDATED_MEMBERSHIP_TIER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/mst-loyalty-programs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/mst-loyalty-programs/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MstLoyaltyProgramRepository mstLoyaltyProgramRepository;

    @Autowired
    private MstLoyaltyProgramMapper mstLoyaltyProgramMapper;

    @Autowired
    private MstLoyaltyProgramSearchRepository mstLoyaltyProgramSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private MstLoyaltyProgram mstLoyaltyProgram;

    private MstLoyaltyProgram insertedMstLoyaltyProgram;

    @Autowired
    private MstCustomerRepository mstCustomerRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstLoyaltyProgram createEntity(EntityManager em) {
        MstLoyaltyProgram mstLoyaltyProgram = new MstLoyaltyProgram()
            .pointsEarned(DEFAULT_POINTS_EARNED)
            .membershipTier(DEFAULT_MEMBERSHIP_TIER);
        return mstLoyaltyProgram;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstLoyaltyProgram createUpdatedEntity(EntityManager em) {
        MstLoyaltyProgram mstLoyaltyProgram = new MstLoyaltyProgram()
            .pointsEarned(UPDATED_POINTS_EARNED)
            .membershipTier(UPDATED_MEMBERSHIP_TIER);
        return mstLoyaltyProgram;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(MstLoyaltyProgram.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        mstLoyaltyProgram = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedMstLoyaltyProgram != null) {
            mstLoyaltyProgramRepository.delete(insertedMstLoyaltyProgram).block();
            mstLoyaltyProgramSearchRepository.delete(insertedMstLoyaltyProgram).block();
            insertedMstLoyaltyProgram = null;
        }
        deleteEntities(em);
    }

    @Test
    void createMstLoyaltyProgram() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstLoyaltyProgramSearchRepository.findAll().collectList().block());
        // Create the MstLoyaltyProgram
        MstLoyaltyProgramDTO mstLoyaltyProgramDTO = mstLoyaltyProgramMapper.toDto(mstLoyaltyProgram);
        var returnedMstLoyaltyProgramDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstLoyaltyProgramDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MstLoyaltyProgramDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MstLoyaltyProgram in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMstLoyaltyProgram = mstLoyaltyProgramMapper.toEntity(returnedMstLoyaltyProgramDTO);
        assertMstLoyaltyProgramUpdatableFieldsEquals(returnedMstLoyaltyProgram, getPersistedMstLoyaltyProgram(returnedMstLoyaltyProgram));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstLoyaltyProgramSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedMstLoyaltyProgram = returnedMstLoyaltyProgram;
    }

    @Test
    void createMstLoyaltyProgramWithExistingId() throws Exception {
        // Create the MstLoyaltyProgram with an existing ID
        mstLoyaltyProgram.setId(1L);
        MstLoyaltyProgramDTO mstLoyaltyProgramDTO = mstLoyaltyProgramMapper.toDto(mstLoyaltyProgram);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstLoyaltyProgramSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstLoyaltyProgramDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstLoyaltyProgram in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstLoyaltyProgramSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllMstLoyaltyPrograms() {
        // Initialize the database
        insertedMstLoyaltyProgram = mstLoyaltyProgramRepository.save(mstLoyaltyProgram).block();

        // Get all the mstLoyaltyProgramList
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
            .value(hasItem(mstLoyaltyProgram.getId().intValue()))
            .jsonPath("$.[*].pointsEarned")
            .value(hasItem(DEFAULT_POINTS_EARNED))
            .jsonPath("$.[*].membershipTier")
            .value(hasItem(DEFAULT_MEMBERSHIP_TIER));
    }

    @Test
    void getMstLoyaltyProgram() {
        // Initialize the database
        insertedMstLoyaltyProgram = mstLoyaltyProgramRepository.save(mstLoyaltyProgram).block();

        // Get the mstLoyaltyProgram
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, mstLoyaltyProgram.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(mstLoyaltyProgram.getId().intValue()))
            .jsonPath("$.pointsEarned")
            .value(is(DEFAULT_POINTS_EARNED))
            .jsonPath("$.membershipTier")
            .value(is(DEFAULT_MEMBERSHIP_TIER));
    }

    @Test
    void getMstLoyaltyProgramsByIdFiltering() {
        // Initialize the database
        insertedMstLoyaltyProgram = mstLoyaltyProgramRepository.save(mstLoyaltyProgram).block();

        Long id = mstLoyaltyProgram.getId();

        defaultMstLoyaltyProgramFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMstLoyaltyProgramFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMstLoyaltyProgramFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllMstLoyaltyProgramsByPointsEarnedIsEqualToSomething() {
        // Initialize the database
        insertedMstLoyaltyProgram = mstLoyaltyProgramRepository.save(mstLoyaltyProgram).block();

        // Get all the mstLoyaltyProgramList where pointsEarned equals to
        defaultMstLoyaltyProgramFiltering("pointsEarned.equals=" + DEFAULT_POINTS_EARNED, "pointsEarned.equals=" + UPDATED_POINTS_EARNED);
    }

    @Test
    void getAllMstLoyaltyProgramsByPointsEarnedIsInShouldWork() {
        // Initialize the database
        insertedMstLoyaltyProgram = mstLoyaltyProgramRepository.save(mstLoyaltyProgram).block();

        // Get all the mstLoyaltyProgramList where pointsEarned in
        defaultMstLoyaltyProgramFiltering(
            "pointsEarned.in=" + DEFAULT_POINTS_EARNED + "," + UPDATED_POINTS_EARNED,
            "pointsEarned.in=" + UPDATED_POINTS_EARNED
        );
    }

    @Test
    void getAllMstLoyaltyProgramsByPointsEarnedIsNullOrNotNull() {
        // Initialize the database
        insertedMstLoyaltyProgram = mstLoyaltyProgramRepository.save(mstLoyaltyProgram).block();

        // Get all the mstLoyaltyProgramList where pointsEarned is not null
        defaultMstLoyaltyProgramFiltering("pointsEarned.specified=true", "pointsEarned.specified=false");
    }

    @Test
    void getAllMstLoyaltyProgramsByPointsEarnedIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedMstLoyaltyProgram = mstLoyaltyProgramRepository.save(mstLoyaltyProgram).block();

        // Get all the mstLoyaltyProgramList where pointsEarned is greater than or equal to
        defaultMstLoyaltyProgramFiltering(
            "pointsEarned.greaterThanOrEqual=" + DEFAULT_POINTS_EARNED,
            "pointsEarned.greaterThanOrEqual=" + UPDATED_POINTS_EARNED
        );
    }

    @Test
    void getAllMstLoyaltyProgramsByPointsEarnedIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedMstLoyaltyProgram = mstLoyaltyProgramRepository.save(mstLoyaltyProgram).block();

        // Get all the mstLoyaltyProgramList where pointsEarned is less than or equal to
        defaultMstLoyaltyProgramFiltering(
            "pointsEarned.lessThanOrEqual=" + DEFAULT_POINTS_EARNED,
            "pointsEarned.lessThanOrEqual=" + SMALLER_POINTS_EARNED
        );
    }

    @Test
    void getAllMstLoyaltyProgramsByPointsEarnedIsLessThanSomething() {
        // Initialize the database
        insertedMstLoyaltyProgram = mstLoyaltyProgramRepository.save(mstLoyaltyProgram).block();

        // Get all the mstLoyaltyProgramList where pointsEarned is less than
        defaultMstLoyaltyProgramFiltering(
            "pointsEarned.lessThan=" + UPDATED_POINTS_EARNED,
            "pointsEarned.lessThan=" + DEFAULT_POINTS_EARNED
        );
    }

    @Test
    void getAllMstLoyaltyProgramsByPointsEarnedIsGreaterThanSomething() {
        // Initialize the database
        insertedMstLoyaltyProgram = mstLoyaltyProgramRepository.save(mstLoyaltyProgram).block();

        // Get all the mstLoyaltyProgramList where pointsEarned is greater than
        defaultMstLoyaltyProgramFiltering(
            "pointsEarned.greaterThan=" + SMALLER_POINTS_EARNED,
            "pointsEarned.greaterThan=" + DEFAULT_POINTS_EARNED
        );
    }

    @Test
    void getAllMstLoyaltyProgramsByMembershipTierIsEqualToSomething() {
        // Initialize the database
        insertedMstLoyaltyProgram = mstLoyaltyProgramRepository.save(mstLoyaltyProgram).block();

        // Get all the mstLoyaltyProgramList where membershipTier equals to
        defaultMstLoyaltyProgramFiltering(
            "membershipTier.equals=" + DEFAULT_MEMBERSHIP_TIER,
            "membershipTier.equals=" + UPDATED_MEMBERSHIP_TIER
        );
    }

    @Test
    void getAllMstLoyaltyProgramsByMembershipTierIsInShouldWork() {
        // Initialize the database
        insertedMstLoyaltyProgram = mstLoyaltyProgramRepository.save(mstLoyaltyProgram).block();

        // Get all the mstLoyaltyProgramList where membershipTier in
        defaultMstLoyaltyProgramFiltering(
            "membershipTier.in=" + DEFAULT_MEMBERSHIP_TIER + "," + UPDATED_MEMBERSHIP_TIER,
            "membershipTier.in=" + UPDATED_MEMBERSHIP_TIER
        );
    }

    @Test
    void getAllMstLoyaltyProgramsByMembershipTierIsNullOrNotNull() {
        // Initialize the database
        insertedMstLoyaltyProgram = mstLoyaltyProgramRepository.save(mstLoyaltyProgram).block();

        // Get all the mstLoyaltyProgramList where membershipTier is not null
        defaultMstLoyaltyProgramFiltering("membershipTier.specified=true", "membershipTier.specified=false");
    }

    @Test
    void getAllMstLoyaltyProgramsByMembershipTierContainsSomething() {
        // Initialize the database
        insertedMstLoyaltyProgram = mstLoyaltyProgramRepository.save(mstLoyaltyProgram).block();

        // Get all the mstLoyaltyProgramList where membershipTier contains
        defaultMstLoyaltyProgramFiltering(
            "membershipTier.contains=" + DEFAULT_MEMBERSHIP_TIER,
            "membershipTier.contains=" + UPDATED_MEMBERSHIP_TIER
        );
    }

    @Test
    void getAllMstLoyaltyProgramsByMembershipTierNotContainsSomething() {
        // Initialize the database
        insertedMstLoyaltyProgram = mstLoyaltyProgramRepository.save(mstLoyaltyProgram).block();

        // Get all the mstLoyaltyProgramList where membershipTier does not contain
        defaultMstLoyaltyProgramFiltering(
            "membershipTier.doesNotContain=" + UPDATED_MEMBERSHIP_TIER,
            "membershipTier.doesNotContain=" + DEFAULT_MEMBERSHIP_TIER
        );
    }

    @Test
    void getAllMstLoyaltyProgramsByCustomerIsEqualToSomething() {
        MstCustomer customer = MstCustomerResourceIT.createEntity(em);
        mstCustomerRepository.save(customer).block();
        Long customerId = customer.getId();
        mstLoyaltyProgram.setCustomerId(customerId);
        insertedMstLoyaltyProgram = mstLoyaltyProgramRepository.save(mstLoyaltyProgram).block();
        // Get all the mstLoyaltyProgramList where customer equals to customerId
        defaultMstLoyaltyProgramShouldBeFound("customerId.equals=" + customerId);

        // Get all the mstLoyaltyProgramList where customer equals to (customerId + 1)
        defaultMstLoyaltyProgramShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }

    private void defaultMstLoyaltyProgramFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultMstLoyaltyProgramShouldBeFound(shouldBeFound);
        defaultMstLoyaltyProgramShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMstLoyaltyProgramShouldBeFound(String filter) {
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
            .value(hasItem(mstLoyaltyProgram.getId().intValue()))
            .jsonPath("$.[*].pointsEarned")
            .value(hasItem(DEFAULT_POINTS_EARNED))
            .jsonPath("$.[*].membershipTier")
            .value(hasItem(DEFAULT_MEMBERSHIP_TIER));

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
    private void defaultMstLoyaltyProgramShouldNotBeFound(String filter) {
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
    void getNonExistingMstLoyaltyProgram() {
        // Get the mstLoyaltyProgram
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMstLoyaltyProgram() throws Exception {
        // Initialize the database
        insertedMstLoyaltyProgram = mstLoyaltyProgramRepository.save(mstLoyaltyProgram).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        mstLoyaltyProgramSearchRepository.save(mstLoyaltyProgram).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstLoyaltyProgramSearchRepository.findAll().collectList().block());

        // Update the mstLoyaltyProgram
        MstLoyaltyProgram updatedMstLoyaltyProgram = mstLoyaltyProgramRepository.findById(mstLoyaltyProgram.getId()).block();
        updatedMstLoyaltyProgram.pointsEarned(UPDATED_POINTS_EARNED).membershipTier(UPDATED_MEMBERSHIP_TIER);
        MstLoyaltyProgramDTO mstLoyaltyProgramDTO = mstLoyaltyProgramMapper.toDto(updatedMstLoyaltyProgram);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstLoyaltyProgramDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstLoyaltyProgramDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstLoyaltyProgram in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMstLoyaltyProgramToMatchAllProperties(updatedMstLoyaltyProgram);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstLoyaltyProgramSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<MstLoyaltyProgram> mstLoyaltyProgramSearchList = Streamable.of(
                    mstLoyaltyProgramSearchRepository.findAll().collectList().block()
                ).toList();
                MstLoyaltyProgram testMstLoyaltyProgramSearch = mstLoyaltyProgramSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertMstLoyaltyProgramAllPropertiesEquals(testMstLoyaltyProgramSearch, updatedMstLoyaltyProgram);
                assertMstLoyaltyProgramUpdatableFieldsEquals(testMstLoyaltyProgramSearch, updatedMstLoyaltyProgram);
            });
    }

    @Test
    void putNonExistingMstLoyaltyProgram() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstLoyaltyProgramSearchRepository.findAll().collectList().block());
        mstLoyaltyProgram.setId(longCount.incrementAndGet());

        // Create the MstLoyaltyProgram
        MstLoyaltyProgramDTO mstLoyaltyProgramDTO = mstLoyaltyProgramMapper.toDto(mstLoyaltyProgram);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstLoyaltyProgramDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstLoyaltyProgramDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstLoyaltyProgram in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstLoyaltyProgramSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchMstLoyaltyProgram() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstLoyaltyProgramSearchRepository.findAll().collectList().block());
        mstLoyaltyProgram.setId(longCount.incrementAndGet());

        // Create the MstLoyaltyProgram
        MstLoyaltyProgramDTO mstLoyaltyProgramDTO = mstLoyaltyProgramMapper.toDto(mstLoyaltyProgram);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstLoyaltyProgramDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstLoyaltyProgram in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstLoyaltyProgramSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamMstLoyaltyProgram() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstLoyaltyProgramSearchRepository.findAll().collectList().block());
        mstLoyaltyProgram.setId(longCount.incrementAndGet());

        // Create the MstLoyaltyProgram
        MstLoyaltyProgramDTO mstLoyaltyProgramDTO = mstLoyaltyProgramMapper.toDto(mstLoyaltyProgram);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstLoyaltyProgramDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstLoyaltyProgram in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstLoyaltyProgramSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateMstLoyaltyProgramWithPatch() throws Exception {
        // Initialize the database
        insertedMstLoyaltyProgram = mstLoyaltyProgramRepository.save(mstLoyaltyProgram).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstLoyaltyProgram using partial update
        MstLoyaltyProgram partialUpdatedMstLoyaltyProgram = new MstLoyaltyProgram();
        partialUpdatedMstLoyaltyProgram.setId(mstLoyaltyProgram.getId());

        partialUpdatedMstLoyaltyProgram.pointsEarned(UPDATED_POINTS_EARNED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstLoyaltyProgram.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstLoyaltyProgram))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstLoyaltyProgram in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstLoyaltyProgramUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMstLoyaltyProgram, mstLoyaltyProgram),
            getPersistedMstLoyaltyProgram(mstLoyaltyProgram)
        );
    }

    @Test
    void fullUpdateMstLoyaltyProgramWithPatch() throws Exception {
        // Initialize the database
        insertedMstLoyaltyProgram = mstLoyaltyProgramRepository.save(mstLoyaltyProgram).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstLoyaltyProgram using partial update
        MstLoyaltyProgram partialUpdatedMstLoyaltyProgram = new MstLoyaltyProgram();
        partialUpdatedMstLoyaltyProgram.setId(mstLoyaltyProgram.getId());

        partialUpdatedMstLoyaltyProgram.pointsEarned(UPDATED_POINTS_EARNED).membershipTier(UPDATED_MEMBERSHIP_TIER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstLoyaltyProgram.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstLoyaltyProgram))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstLoyaltyProgram in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstLoyaltyProgramUpdatableFieldsEquals(
            partialUpdatedMstLoyaltyProgram,
            getPersistedMstLoyaltyProgram(partialUpdatedMstLoyaltyProgram)
        );
    }

    @Test
    void patchNonExistingMstLoyaltyProgram() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstLoyaltyProgramSearchRepository.findAll().collectList().block());
        mstLoyaltyProgram.setId(longCount.incrementAndGet());

        // Create the MstLoyaltyProgram
        MstLoyaltyProgramDTO mstLoyaltyProgramDTO = mstLoyaltyProgramMapper.toDto(mstLoyaltyProgram);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, mstLoyaltyProgramDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstLoyaltyProgramDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstLoyaltyProgram in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstLoyaltyProgramSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchMstLoyaltyProgram() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstLoyaltyProgramSearchRepository.findAll().collectList().block());
        mstLoyaltyProgram.setId(longCount.incrementAndGet());

        // Create the MstLoyaltyProgram
        MstLoyaltyProgramDTO mstLoyaltyProgramDTO = mstLoyaltyProgramMapper.toDto(mstLoyaltyProgram);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstLoyaltyProgramDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstLoyaltyProgram in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstLoyaltyProgramSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamMstLoyaltyProgram() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstLoyaltyProgramSearchRepository.findAll().collectList().block());
        mstLoyaltyProgram.setId(longCount.incrementAndGet());

        // Create the MstLoyaltyProgram
        MstLoyaltyProgramDTO mstLoyaltyProgramDTO = mstLoyaltyProgramMapper.toDto(mstLoyaltyProgram);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstLoyaltyProgramDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstLoyaltyProgram in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstLoyaltyProgramSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteMstLoyaltyProgram() {
        // Initialize the database
        insertedMstLoyaltyProgram = mstLoyaltyProgramRepository.save(mstLoyaltyProgram).block();
        mstLoyaltyProgramRepository.save(mstLoyaltyProgram).block();
        mstLoyaltyProgramSearchRepository.save(mstLoyaltyProgram).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstLoyaltyProgramSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the mstLoyaltyProgram
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, mstLoyaltyProgram.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstLoyaltyProgramSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchMstLoyaltyProgram() {
        // Initialize the database
        insertedMstLoyaltyProgram = mstLoyaltyProgramRepository.save(mstLoyaltyProgram).block();
        mstLoyaltyProgramSearchRepository.save(mstLoyaltyProgram).block();

        // Search the mstLoyaltyProgram
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + mstLoyaltyProgram.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(mstLoyaltyProgram.getId().intValue()))
            .jsonPath("$.[*].pointsEarned")
            .value(hasItem(DEFAULT_POINTS_EARNED))
            .jsonPath("$.[*].membershipTier")
            .value(hasItem(DEFAULT_MEMBERSHIP_TIER));
    }

    protected long getRepositoryCount() {
        return mstLoyaltyProgramRepository.count().block();
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

    protected MstLoyaltyProgram getPersistedMstLoyaltyProgram(MstLoyaltyProgram mstLoyaltyProgram) {
        return mstLoyaltyProgramRepository.findById(mstLoyaltyProgram.getId()).block();
    }

    protected void assertPersistedMstLoyaltyProgramToMatchAllProperties(MstLoyaltyProgram expectedMstLoyaltyProgram) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstLoyaltyProgramAllPropertiesEquals(expectedMstLoyaltyProgram, getPersistedMstLoyaltyProgram(expectedMstLoyaltyProgram));
        assertMstLoyaltyProgramUpdatableFieldsEquals(expectedMstLoyaltyProgram, getPersistedMstLoyaltyProgram(expectedMstLoyaltyProgram));
    }

    protected void assertPersistedMstLoyaltyProgramToMatchUpdatableProperties(MstLoyaltyProgram expectedMstLoyaltyProgram) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstLoyaltyProgramAllUpdatablePropertiesEquals(expectedMstLoyaltyProgram, getPersistedMstLoyaltyProgram(expectedMstLoyaltyProgram));
        assertMstLoyaltyProgramUpdatableFieldsEquals(expectedMstLoyaltyProgram, getPersistedMstLoyaltyProgram(expectedMstLoyaltyProgram));
    }
}
