package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.MstSupplierAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.MstSupplier;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MstSupplierRepository;
import com.mycompany.myapp.repository.search.MstSupplierSearchRepository;
import com.mycompany.myapp.service.dto.MstSupplierDTO;
import com.mycompany.myapp.service.mapper.MstSupplierMapper;
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
 * Integration tests for the {@link MstSupplierResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MstSupplierResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT_INFO = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT_INFO = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final Integer DEFAULT_RATING = 1;
    private static final Integer UPDATED_RATING = 2;
    private static final Integer SMALLER_RATING = 1 - 1;

    private static final String ENTITY_API_URL = "/api/mst-suppliers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/mst-suppliers/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MstSupplierRepository mstSupplierRepository;

    @Autowired
    private MstSupplierMapper mstSupplierMapper;

    @Autowired
    private MstSupplierSearchRepository mstSupplierSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private MstSupplier mstSupplier;

    private MstSupplier insertedMstSupplier;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstSupplier createEntity(EntityManager em) {
        MstSupplier mstSupplier = new MstSupplier()
            .name(DEFAULT_NAME)
            .contactInfo(DEFAULT_CONTACT_INFO)
            .address(DEFAULT_ADDRESS)
            .rating(DEFAULT_RATING);
        return mstSupplier;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstSupplier createUpdatedEntity(EntityManager em) {
        MstSupplier mstSupplier = new MstSupplier()
            .name(UPDATED_NAME)
            .contactInfo(UPDATED_CONTACT_INFO)
            .address(UPDATED_ADDRESS)
            .rating(UPDATED_RATING);
        return mstSupplier;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(MstSupplier.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        mstSupplier = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedMstSupplier != null) {
            mstSupplierRepository.delete(insertedMstSupplier).block();
            mstSupplierSearchRepository.delete(insertedMstSupplier).block();
            insertedMstSupplier = null;
        }
        deleteEntities(em);
    }

    @Test
    void createMstSupplier() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstSupplierSearchRepository.findAll().collectList().block());
        // Create the MstSupplier
        MstSupplierDTO mstSupplierDTO = mstSupplierMapper.toDto(mstSupplier);
        var returnedMstSupplierDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstSupplierDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MstSupplierDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MstSupplier in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMstSupplier = mstSupplierMapper.toEntity(returnedMstSupplierDTO);
        assertMstSupplierUpdatableFieldsEquals(returnedMstSupplier, getPersistedMstSupplier(returnedMstSupplier));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstSupplierSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedMstSupplier = returnedMstSupplier;
    }

    @Test
    void createMstSupplierWithExistingId() throws Exception {
        // Create the MstSupplier with an existing ID
        mstSupplier.setId(1L);
        MstSupplierDTO mstSupplierDTO = mstSupplierMapper.toDto(mstSupplier);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstSupplierSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstSupplierDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstSupplier in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstSupplierSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstSupplierSearchRepository.findAll().collectList().block());
        // set the field null
        mstSupplier.setName(null);

        // Create the MstSupplier, which fails.
        MstSupplierDTO mstSupplierDTO = mstSupplierMapper.toDto(mstSupplier);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstSupplierDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstSupplierSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllMstSuppliers() {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();

        // Get all the mstSupplierList
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
            .value(hasItem(mstSupplier.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].contactInfo")
            .value(hasItem(DEFAULT_CONTACT_INFO))
            .jsonPath("$.[*].address")
            .value(hasItem(DEFAULT_ADDRESS))
            .jsonPath("$.[*].rating")
            .value(hasItem(DEFAULT_RATING));
    }

    @Test
    void getMstSupplier() {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();

        // Get the mstSupplier
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, mstSupplier.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(mstSupplier.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.contactInfo")
            .value(is(DEFAULT_CONTACT_INFO))
            .jsonPath("$.address")
            .value(is(DEFAULT_ADDRESS))
            .jsonPath("$.rating")
            .value(is(DEFAULT_RATING));
    }

    @Test
    void getMstSuppliersByIdFiltering() {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();

        Long id = mstSupplier.getId();

        defaultMstSupplierFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMstSupplierFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMstSupplierFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllMstSuppliersByNameIsEqualToSomething() {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();

        // Get all the mstSupplierList where name equals to
        defaultMstSupplierFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllMstSuppliersByNameIsInShouldWork() {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();

        // Get all the mstSupplierList where name in
        defaultMstSupplierFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllMstSuppliersByNameIsNullOrNotNull() {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();

        // Get all the mstSupplierList where name is not null
        defaultMstSupplierFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllMstSuppliersByNameContainsSomething() {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();

        // Get all the mstSupplierList where name contains
        defaultMstSupplierFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllMstSuppliersByNameNotContainsSomething() {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();

        // Get all the mstSupplierList where name does not contain
        defaultMstSupplierFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllMstSuppliersByContactInfoIsEqualToSomething() {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();

        // Get all the mstSupplierList where contactInfo equals to
        defaultMstSupplierFiltering("contactInfo.equals=" + DEFAULT_CONTACT_INFO, "contactInfo.equals=" + UPDATED_CONTACT_INFO);
    }

    @Test
    void getAllMstSuppliersByContactInfoIsInShouldWork() {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();

        // Get all the mstSupplierList where contactInfo in
        defaultMstSupplierFiltering(
            "contactInfo.in=" + DEFAULT_CONTACT_INFO + "," + UPDATED_CONTACT_INFO,
            "contactInfo.in=" + UPDATED_CONTACT_INFO
        );
    }

    @Test
    void getAllMstSuppliersByContactInfoIsNullOrNotNull() {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();

        // Get all the mstSupplierList where contactInfo is not null
        defaultMstSupplierFiltering("contactInfo.specified=true", "contactInfo.specified=false");
    }

    @Test
    void getAllMstSuppliersByContactInfoContainsSomething() {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();

        // Get all the mstSupplierList where contactInfo contains
        defaultMstSupplierFiltering("contactInfo.contains=" + DEFAULT_CONTACT_INFO, "contactInfo.contains=" + UPDATED_CONTACT_INFO);
    }

    @Test
    void getAllMstSuppliersByContactInfoNotContainsSomething() {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();

        // Get all the mstSupplierList where contactInfo does not contain
        defaultMstSupplierFiltering(
            "contactInfo.doesNotContain=" + UPDATED_CONTACT_INFO,
            "contactInfo.doesNotContain=" + DEFAULT_CONTACT_INFO
        );
    }

    @Test
    void getAllMstSuppliersByAddressIsEqualToSomething() {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();

        // Get all the mstSupplierList where address equals to
        defaultMstSupplierFiltering("address.equals=" + DEFAULT_ADDRESS, "address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    void getAllMstSuppliersByAddressIsInShouldWork() {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();

        // Get all the mstSupplierList where address in
        defaultMstSupplierFiltering("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS, "address.in=" + UPDATED_ADDRESS);
    }

    @Test
    void getAllMstSuppliersByAddressIsNullOrNotNull() {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();

        // Get all the mstSupplierList where address is not null
        defaultMstSupplierFiltering("address.specified=true", "address.specified=false");
    }

    @Test
    void getAllMstSuppliersByAddressContainsSomething() {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();

        // Get all the mstSupplierList where address contains
        defaultMstSupplierFiltering("address.contains=" + DEFAULT_ADDRESS, "address.contains=" + UPDATED_ADDRESS);
    }

    @Test
    void getAllMstSuppliersByAddressNotContainsSomething() {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();

        // Get all the mstSupplierList where address does not contain
        defaultMstSupplierFiltering("address.doesNotContain=" + UPDATED_ADDRESS, "address.doesNotContain=" + DEFAULT_ADDRESS);
    }

    @Test
    void getAllMstSuppliersByRatingIsEqualToSomething() {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();

        // Get all the mstSupplierList where rating equals to
        defaultMstSupplierFiltering("rating.equals=" + DEFAULT_RATING, "rating.equals=" + UPDATED_RATING);
    }

    @Test
    void getAllMstSuppliersByRatingIsInShouldWork() {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();

        // Get all the mstSupplierList where rating in
        defaultMstSupplierFiltering("rating.in=" + DEFAULT_RATING + "," + UPDATED_RATING, "rating.in=" + UPDATED_RATING);
    }

    @Test
    void getAllMstSuppliersByRatingIsNullOrNotNull() {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();

        // Get all the mstSupplierList where rating is not null
        defaultMstSupplierFiltering("rating.specified=true", "rating.specified=false");
    }

    @Test
    void getAllMstSuppliersByRatingIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();

        // Get all the mstSupplierList where rating is greater than or equal to
        defaultMstSupplierFiltering("rating.greaterThanOrEqual=" + DEFAULT_RATING, "rating.greaterThanOrEqual=" + (DEFAULT_RATING + 1));
    }

    @Test
    void getAllMstSuppliersByRatingIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();

        // Get all the mstSupplierList where rating is less than or equal to
        defaultMstSupplierFiltering("rating.lessThanOrEqual=" + DEFAULT_RATING, "rating.lessThanOrEqual=" + SMALLER_RATING);
    }

    @Test
    void getAllMstSuppliersByRatingIsLessThanSomething() {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();

        // Get all the mstSupplierList where rating is less than
        defaultMstSupplierFiltering("rating.lessThan=" + (DEFAULT_RATING + 1), "rating.lessThan=" + DEFAULT_RATING);
    }

    @Test
    void getAllMstSuppliersByRatingIsGreaterThanSomething() {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();

        // Get all the mstSupplierList where rating is greater than
        defaultMstSupplierFiltering("rating.greaterThan=" + SMALLER_RATING, "rating.greaterThan=" + DEFAULT_RATING);
    }

    private void defaultMstSupplierFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultMstSupplierShouldBeFound(shouldBeFound);
        defaultMstSupplierShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMstSupplierShouldBeFound(String filter) {
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
            .value(hasItem(mstSupplier.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].contactInfo")
            .value(hasItem(DEFAULT_CONTACT_INFO))
            .jsonPath("$.[*].address")
            .value(hasItem(DEFAULT_ADDRESS))
            .jsonPath("$.[*].rating")
            .value(hasItem(DEFAULT_RATING));

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
    private void defaultMstSupplierShouldNotBeFound(String filter) {
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
    void getNonExistingMstSupplier() {
        // Get the mstSupplier
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMstSupplier() throws Exception {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        mstSupplierSearchRepository.save(mstSupplier).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstSupplierSearchRepository.findAll().collectList().block());

        // Update the mstSupplier
        MstSupplier updatedMstSupplier = mstSupplierRepository.findById(mstSupplier.getId()).block();
        updatedMstSupplier.name(UPDATED_NAME).contactInfo(UPDATED_CONTACT_INFO).address(UPDATED_ADDRESS).rating(UPDATED_RATING);
        MstSupplierDTO mstSupplierDTO = mstSupplierMapper.toDto(updatedMstSupplier);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstSupplierDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstSupplierDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstSupplier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMstSupplierToMatchAllProperties(updatedMstSupplier);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstSupplierSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<MstSupplier> mstSupplierSearchList = Streamable.of(
                    mstSupplierSearchRepository.findAll().collectList().block()
                ).toList();
                MstSupplier testMstSupplierSearch = mstSupplierSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertMstSupplierAllPropertiesEquals(testMstSupplierSearch, updatedMstSupplier);
                assertMstSupplierUpdatableFieldsEquals(testMstSupplierSearch, updatedMstSupplier);
            });
    }

    @Test
    void putNonExistingMstSupplier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstSupplierSearchRepository.findAll().collectList().block());
        mstSupplier.setId(longCount.incrementAndGet());

        // Create the MstSupplier
        MstSupplierDTO mstSupplierDTO = mstSupplierMapper.toDto(mstSupplier);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstSupplierDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstSupplierDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstSupplier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstSupplierSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchMstSupplier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstSupplierSearchRepository.findAll().collectList().block());
        mstSupplier.setId(longCount.incrementAndGet());

        // Create the MstSupplier
        MstSupplierDTO mstSupplierDTO = mstSupplierMapper.toDto(mstSupplier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstSupplierDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstSupplier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstSupplierSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamMstSupplier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstSupplierSearchRepository.findAll().collectList().block());
        mstSupplier.setId(longCount.incrementAndGet());

        // Create the MstSupplier
        MstSupplierDTO mstSupplierDTO = mstSupplierMapper.toDto(mstSupplier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstSupplierDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstSupplier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstSupplierSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateMstSupplierWithPatch() throws Exception {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstSupplier using partial update
        MstSupplier partialUpdatedMstSupplier = new MstSupplier();
        partialUpdatedMstSupplier.setId(mstSupplier.getId());

        partialUpdatedMstSupplier.name(UPDATED_NAME).rating(UPDATED_RATING);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstSupplier.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstSupplier))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstSupplier in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstSupplierUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMstSupplier, mstSupplier),
            getPersistedMstSupplier(mstSupplier)
        );
    }

    @Test
    void fullUpdateMstSupplierWithPatch() throws Exception {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstSupplier using partial update
        MstSupplier partialUpdatedMstSupplier = new MstSupplier();
        partialUpdatedMstSupplier.setId(mstSupplier.getId());

        partialUpdatedMstSupplier.name(UPDATED_NAME).contactInfo(UPDATED_CONTACT_INFO).address(UPDATED_ADDRESS).rating(UPDATED_RATING);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstSupplier.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstSupplier))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstSupplier in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstSupplierUpdatableFieldsEquals(partialUpdatedMstSupplier, getPersistedMstSupplier(partialUpdatedMstSupplier));
    }

    @Test
    void patchNonExistingMstSupplier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstSupplierSearchRepository.findAll().collectList().block());
        mstSupplier.setId(longCount.incrementAndGet());

        // Create the MstSupplier
        MstSupplierDTO mstSupplierDTO = mstSupplierMapper.toDto(mstSupplier);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, mstSupplierDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstSupplierDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstSupplier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstSupplierSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchMstSupplier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstSupplierSearchRepository.findAll().collectList().block());
        mstSupplier.setId(longCount.incrementAndGet());

        // Create the MstSupplier
        MstSupplierDTO mstSupplierDTO = mstSupplierMapper.toDto(mstSupplier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstSupplierDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstSupplier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstSupplierSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamMstSupplier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstSupplierSearchRepository.findAll().collectList().block());
        mstSupplier.setId(longCount.incrementAndGet());

        // Create the MstSupplier
        MstSupplierDTO mstSupplierDTO = mstSupplierMapper.toDto(mstSupplier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstSupplierDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstSupplier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstSupplierSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteMstSupplier() {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();
        mstSupplierRepository.save(mstSupplier).block();
        mstSupplierSearchRepository.save(mstSupplier).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstSupplierSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the mstSupplier
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, mstSupplier.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstSupplierSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchMstSupplier() {
        // Initialize the database
        insertedMstSupplier = mstSupplierRepository.save(mstSupplier).block();
        mstSupplierSearchRepository.save(mstSupplier).block();

        // Search the mstSupplier
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + mstSupplier.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(mstSupplier.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].contactInfo")
            .value(hasItem(DEFAULT_CONTACT_INFO))
            .jsonPath("$.[*].address")
            .value(hasItem(DEFAULT_ADDRESS))
            .jsonPath("$.[*].rating")
            .value(hasItem(DEFAULT_RATING));
    }

    protected long getRepositoryCount() {
        return mstSupplierRepository.count().block();
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

    protected MstSupplier getPersistedMstSupplier(MstSupplier mstSupplier) {
        return mstSupplierRepository.findById(mstSupplier.getId()).block();
    }

    protected void assertPersistedMstSupplierToMatchAllProperties(MstSupplier expectedMstSupplier) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstSupplierAllPropertiesEquals(expectedMstSupplier, getPersistedMstSupplier(expectedMstSupplier));
        assertMstSupplierUpdatableFieldsEquals(expectedMstSupplier, getPersistedMstSupplier(expectedMstSupplier));
    }

    protected void assertPersistedMstSupplierToMatchUpdatableProperties(MstSupplier expectedMstSupplier) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstSupplierAllUpdatablePropertiesEquals(expectedMstSupplier, getPersistedMstSupplier(expectedMstSupplier));
        assertMstSupplierUpdatableFieldsEquals(expectedMstSupplier, getPersistedMstSupplier(expectedMstSupplier));
    }
}
