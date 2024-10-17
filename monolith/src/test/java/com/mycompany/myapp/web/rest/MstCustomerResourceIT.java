package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.MstCustomerAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.MstCustomer;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MstCustomerRepository;
import com.mycompany.myapp.repository.search.MstCustomerSearchRepository;
import com.mycompany.myapp.service.dto.MstCustomerDTO;
import com.mycompany.myapp.service.mapper.MstCustomerMapper;
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
 * Integration tests for the {@link MstCustomerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MstCustomerResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final Integer DEFAULT_LOYALTY_POINTS = 1;
    private static final Integer UPDATED_LOYALTY_POINTS = 2;
    private static final Integer SMALLER_LOYALTY_POINTS = 1 - 1;

    private static final String ENTITY_API_URL = "/api/mst-customers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/mst-customers/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MstCustomerRepository mstCustomerRepository;

    @Autowired
    private MstCustomerMapper mstCustomerMapper;

    @Autowired
    private MstCustomerSearchRepository mstCustomerSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private MstCustomer mstCustomer;

    private MstCustomer insertedMstCustomer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstCustomer createEntity(EntityManager em) {
        MstCustomer mstCustomer = new MstCustomer()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .address(DEFAULT_ADDRESS)
            .loyaltyPoints(DEFAULT_LOYALTY_POINTS);
        return mstCustomer;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstCustomer createUpdatedEntity(EntityManager em) {
        MstCustomer mstCustomer = new MstCustomer()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .address(UPDATED_ADDRESS)
            .loyaltyPoints(UPDATED_LOYALTY_POINTS);
        return mstCustomer;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(MstCustomer.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        mstCustomer = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedMstCustomer != null) {
            mstCustomerRepository.delete(insertedMstCustomer).block();
            mstCustomerSearchRepository.delete(insertedMstCustomer).block();
            insertedMstCustomer = null;
        }
        deleteEntities(em);
    }

    @Test
    void createMstCustomer() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCustomerSearchRepository.findAll().collectList().block());
        // Create the MstCustomer
        MstCustomerDTO mstCustomerDTO = mstCustomerMapper.toDto(mstCustomer);
        var returnedMstCustomerDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCustomerDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MstCustomerDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MstCustomer in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMstCustomer = mstCustomerMapper.toEntity(returnedMstCustomerDTO);
        assertMstCustomerUpdatableFieldsEquals(returnedMstCustomer, getPersistedMstCustomer(returnedMstCustomer));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCustomerSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedMstCustomer = returnedMstCustomer;
    }

    @Test
    void createMstCustomerWithExistingId() throws Exception {
        // Create the MstCustomer with an existing ID
        mstCustomer.setId(1L);
        MstCustomerDTO mstCustomerDTO = mstCustomerMapper.toDto(mstCustomer);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCustomerSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCustomerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstCustomer in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCustomerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkFirstNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCustomerSearchRepository.findAll().collectList().block());
        // set the field null
        mstCustomer.setFirstName(null);

        // Create the MstCustomer, which fails.
        MstCustomerDTO mstCustomerDTO = mstCustomerMapper.toDto(mstCustomer);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCustomerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCustomerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkLastNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCustomerSearchRepository.findAll().collectList().block());
        // set the field null
        mstCustomer.setLastName(null);

        // Create the MstCustomer, which fails.
        MstCustomerDTO mstCustomerDTO = mstCustomerMapper.toDto(mstCustomer);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCustomerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCustomerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCustomerSearchRepository.findAll().collectList().block());
        // set the field null
        mstCustomer.setEmail(null);

        // Create the MstCustomer, which fails.
        MstCustomerDTO mstCustomerDTO = mstCustomerMapper.toDto(mstCustomer);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCustomerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCustomerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllMstCustomers() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList
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
            .value(hasItem(mstCustomer.getId().intValue()))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].phoneNumber")
            .value(hasItem(DEFAULT_PHONE_NUMBER))
            .jsonPath("$.[*].address")
            .value(hasItem(DEFAULT_ADDRESS))
            .jsonPath("$.[*].loyaltyPoints")
            .value(hasItem(DEFAULT_LOYALTY_POINTS));
    }

    @Test
    void getMstCustomer() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get the mstCustomer
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, mstCustomer.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(mstCustomer.getId().intValue()))
            .jsonPath("$.firstName")
            .value(is(DEFAULT_FIRST_NAME))
            .jsonPath("$.lastName")
            .value(is(DEFAULT_LAST_NAME))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL))
            .jsonPath("$.phoneNumber")
            .value(is(DEFAULT_PHONE_NUMBER))
            .jsonPath("$.address")
            .value(is(DEFAULT_ADDRESS))
            .jsonPath("$.loyaltyPoints")
            .value(is(DEFAULT_LOYALTY_POINTS));
    }

    @Test
    void getMstCustomersByIdFiltering() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        Long id = mstCustomer.getId();

        defaultMstCustomerFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMstCustomerFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMstCustomerFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllMstCustomersByFirstNameIsEqualToSomething() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where firstName equals to
        defaultMstCustomerFiltering("firstName.equals=" + DEFAULT_FIRST_NAME, "firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    void getAllMstCustomersByFirstNameIsInShouldWork() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where firstName in
        defaultMstCustomerFiltering("firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME, "firstName.in=" + UPDATED_FIRST_NAME);
    }

    @Test
    void getAllMstCustomersByFirstNameIsNullOrNotNull() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where firstName is not null
        defaultMstCustomerFiltering("firstName.specified=true", "firstName.specified=false");
    }

    @Test
    void getAllMstCustomersByFirstNameContainsSomething() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where firstName contains
        defaultMstCustomerFiltering("firstName.contains=" + DEFAULT_FIRST_NAME, "firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    void getAllMstCustomersByFirstNameNotContainsSomething() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where firstName does not contain
        defaultMstCustomerFiltering("firstName.doesNotContain=" + UPDATED_FIRST_NAME, "firstName.doesNotContain=" + DEFAULT_FIRST_NAME);
    }

    @Test
    void getAllMstCustomersByLastNameIsEqualToSomething() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where lastName equals to
        defaultMstCustomerFiltering("lastName.equals=" + DEFAULT_LAST_NAME, "lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    void getAllMstCustomersByLastNameIsInShouldWork() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where lastName in
        defaultMstCustomerFiltering("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME, "lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    void getAllMstCustomersByLastNameIsNullOrNotNull() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where lastName is not null
        defaultMstCustomerFiltering("lastName.specified=true", "lastName.specified=false");
    }

    @Test
    void getAllMstCustomersByLastNameContainsSomething() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where lastName contains
        defaultMstCustomerFiltering("lastName.contains=" + DEFAULT_LAST_NAME, "lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    void getAllMstCustomersByLastNameNotContainsSomething() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where lastName does not contain
        defaultMstCustomerFiltering("lastName.doesNotContain=" + UPDATED_LAST_NAME, "lastName.doesNotContain=" + DEFAULT_LAST_NAME);
    }

    @Test
    void getAllMstCustomersByEmailIsEqualToSomething() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where email equals to
        defaultMstCustomerFiltering("email.equals=" + DEFAULT_EMAIL, "email.equals=" + UPDATED_EMAIL);
    }

    @Test
    void getAllMstCustomersByEmailIsInShouldWork() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where email in
        defaultMstCustomerFiltering("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL, "email.in=" + UPDATED_EMAIL);
    }

    @Test
    void getAllMstCustomersByEmailIsNullOrNotNull() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where email is not null
        defaultMstCustomerFiltering("email.specified=true", "email.specified=false");
    }

    @Test
    void getAllMstCustomersByEmailContainsSomething() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where email contains
        defaultMstCustomerFiltering("email.contains=" + DEFAULT_EMAIL, "email.contains=" + UPDATED_EMAIL);
    }

    @Test
    void getAllMstCustomersByEmailNotContainsSomething() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where email does not contain
        defaultMstCustomerFiltering("email.doesNotContain=" + UPDATED_EMAIL, "email.doesNotContain=" + DEFAULT_EMAIL);
    }

    @Test
    void getAllMstCustomersByPhoneNumberIsEqualToSomething() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where phoneNumber equals to
        defaultMstCustomerFiltering("phoneNumber.equals=" + DEFAULT_PHONE_NUMBER, "phoneNumber.equals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    void getAllMstCustomersByPhoneNumberIsInShouldWork() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where phoneNumber in
        defaultMstCustomerFiltering(
            "phoneNumber.in=" + DEFAULT_PHONE_NUMBER + "," + UPDATED_PHONE_NUMBER,
            "phoneNumber.in=" + UPDATED_PHONE_NUMBER
        );
    }

    @Test
    void getAllMstCustomersByPhoneNumberIsNullOrNotNull() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where phoneNumber is not null
        defaultMstCustomerFiltering("phoneNumber.specified=true", "phoneNumber.specified=false");
    }

    @Test
    void getAllMstCustomersByPhoneNumberContainsSomething() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where phoneNumber contains
        defaultMstCustomerFiltering("phoneNumber.contains=" + DEFAULT_PHONE_NUMBER, "phoneNumber.contains=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    void getAllMstCustomersByPhoneNumberNotContainsSomething() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where phoneNumber does not contain
        defaultMstCustomerFiltering(
            "phoneNumber.doesNotContain=" + UPDATED_PHONE_NUMBER,
            "phoneNumber.doesNotContain=" + DEFAULT_PHONE_NUMBER
        );
    }

    @Test
    void getAllMstCustomersByAddressIsEqualToSomething() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where address equals to
        defaultMstCustomerFiltering("address.equals=" + DEFAULT_ADDRESS, "address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    void getAllMstCustomersByAddressIsInShouldWork() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where address in
        defaultMstCustomerFiltering("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS, "address.in=" + UPDATED_ADDRESS);
    }

    @Test
    void getAllMstCustomersByAddressIsNullOrNotNull() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where address is not null
        defaultMstCustomerFiltering("address.specified=true", "address.specified=false");
    }

    @Test
    void getAllMstCustomersByAddressContainsSomething() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where address contains
        defaultMstCustomerFiltering("address.contains=" + DEFAULT_ADDRESS, "address.contains=" + UPDATED_ADDRESS);
    }

    @Test
    void getAllMstCustomersByAddressNotContainsSomething() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where address does not contain
        defaultMstCustomerFiltering("address.doesNotContain=" + UPDATED_ADDRESS, "address.doesNotContain=" + DEFAULT_ADDRESS);
    }

    @Test
    void getAllMstCustomersByLoyaltyPointsIsEqualToSomething() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where loyaltyPoints equals to
        defaultMstCustomerFiltering("loyaltyPoints.equals=" + DEFAULT_LOYALTY_POINTS, "loyaltyPoints.equals=" + UPDATED_LOYALTY_POINTS);
    }

    @Test
    void getAllMstCustomersByLoyaltyPointsIsInShouldWork() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where loyaltyPoints in
        defaultMstCustomerFiltering(
            "loyaltyPoints.in=" + DEFAULT_LOYALTY_POINTS + "," + UPDATED_LOYALTY_POINTS,
            "loyaltyPoints.in=" + UPDATED_LOYALTY_POINTS
        );
    }

    @Test
    void getAllMstCustomersByLoyaltyPointsIsNullOrNotNull() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where loyaltyPoints is not null
        defaultMstCustomerFiltering("loyaltyPoints.specified=true", "loyaltyPoints.specified=false");
    }

    @Test
    void getAllMstCustomersByLoyaltyPointsIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where loyaltyPoints is greater than or equal to
        defaultMstCustomerFiltering(
            "loyaltyPoints.greaterThanOrEqual=" + DEFAULT_LOYALTY_POINTS,
            "loyaltyPoints.greaterThanOrEqual=" + UPDATED_LOYALTY_POINTS
        );
    }

    @Test
    void getAllMstCustomersByLoyaltyPointsIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where loyaltyPoints is less than or equal to
        defaultMstCustomerFiltering(
            "loyaltyPoints.lessThanOrEqual=" + DEFAULT_LOYALTY_POINTS,
            "loyaltyPoints.lessThanOrEqual=" + SMALLER_LOYALTY_POINTS
        );
    }

    @Test
    void getAllMstCustomersByLoyaltyPointsIsLessThanSomething() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where loyaltyPoints is less than
        defaultMstCustomerFiltering("loyaltyPoints.lessThan=" + UPDATED_LOYALTY_POINTS, "loyaltyPoints.lessThan=" + DEFAULT_LOYALTY_POINTS);
    }

    @Test
    void getAllMstCustomersByLoyaltyPointsIsGreaterThanSomething() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        // Get all the mstCustomerList where loyaltyPoints is greater than
        defaultMstCustomerFiltering(
            "loyaltyPoints.greaterThan=" + SMALLER_LOYALTY_POINTS,
            "loyaltyPoints.greaterThan=" + DEFAULT_LOYALTY_POINTS
        );
    }

    private void defaultMstCustomerFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultMstCustomerShouldBeFound(shouldBeFound);
        defaultMstCustomerShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMstCustomerShouldBeFound(String filter) {
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
            .value(hasItem(mstCustomer.getId().intValue()))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].phoneNumber")
            .value(hasItem(DEFAULT_PHONE_NUMBER))
            .jsonPath("$.[*].address")
            .value(hasItem(DEFAULT_ADDRESS))
            .jsonPath("$.[*].loyaltyPoints")
            .value(hasItem(DEFAULT_LOYALTY_POINTS));

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
    private void defaultMstCustomerShouldNotBeFound(String filter) {
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
    void getNonExistingMstCustomer() {
        // Get the mstCustomer
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMstCustomer() throws Exception {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        mstCustomerSearchRepository.save(mstCustomer).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCustomerSearchRepository.findAll().collectList().block());

        // Update the mstCustomer
        MstCustomer updatedMstCustomer = mstCustomerRepository.findById(mstCustomer.getId()).block();
        updatedMstCustomer
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .address(UPDATED_ADDRESS)
            .loyaltyPoints(UPDATED_LOYALTY_POINTS);
        MstCustomerDTO mstCustomerDTO = mstCustomerMapper.toDto(updatedMstCustomer);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstCustomerDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCustomerDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstCustomer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMstCustomerToMatchAllProperties(updatedMstCustomer);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCustomerSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<MstCustomer> mstCustomerSearchList = Streamable.of(
                    mstCustomerSearchRepository.findAll().collectList().block()
                ).toList();
                MstCustomer testMstCustomerSearch = mstCustomerSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertMstCustomerAllPropertiesEquals(testMstCustomerSearch, updatedMstCustomer);
                assertMstCustomerUpdatableFieldsEquals(testMstCustomerSearch, updatedMstCustomer);
            });
    }

    @Test
    void putNonExistingMstCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCustomerSearchRepository.findAll().collectList().block());
        mstCustomer.setId(longCount.incrementAndGet());

        // Create the MstCustomer
        MstCustomerDTO mstCustomerDTO = mstCustomerMapper.toDto(mstCustomer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstCustomerDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCustomerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstCustomer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCustomerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchMstCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCustomerSearchRepository.findAll().collectList().block());
        mstCustomer.setId(longCount.incrementAndGet());

        // Create the MstCustomer
        MstCustomerDTO mstCustomerDTO = mstCustomerMapper.toDto(mstCustomer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCustomerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstCustomer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCustomerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamMstCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCustomerSearchRepository.findAll().collectList().block());
        mstCustomer.setId(longCount.incrementAndGet());

        // Create the MstCustomer
        MstCustomerDTO mstCustomerDTO = mstCustomerMapper.toDto(mstCustomer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCustomerDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstCustomer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCustomerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateMstCustomerWithPatch() throws Exception {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstCustomer using partial update
        MstCustomer partialUpdatedMstCustomer = new MstCustomer();
        partialUpdatedMstCustomer.setId(mstCustomer.getId());

        partialUpdatedMstCustomer.lastName(UPDATED_LAST_NAME).phoneNumber(UPDATED_PHONE_NUMBER).loyaltyPoints(UPDATED_LOYALTY_POINTS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstCustomer.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstCustomer))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstCustomer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstCustomerUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMstCustomer, mstCustomer),
            getPersistedMstCustomer(mstCustomer)
        );
    }

    @Test
    void fullUpdateMstCustomerWithPatch() throws Exception {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstCustomer using partial update
        MstCustomer partialUpdatedMstCustomer = new MstCustomer();
        partialUpdatedMstCustomer.setId(mstCustomer.getId());

        partialUpdatedMstCustomer
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .address(UPDATED_ADDRESS)
            .loyaltyPoints(UPDATED_LOYALTY_POINTS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstCustomer.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstCustomer))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstCustomer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstCustomerUpdatableFieldsEquals(partialUpdatedMstCustomer, getPersistedMstCustomer(partialUpdatedMstCustomer));
    }

    @Test
    void patchNonExistingMstCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCustomerSearchRepository.findAll().collectList().block());
        mstCustomer.setId(longCount.incrementAndGet());

        // Create the MstCustomer
        MstCustomerDTO mstCustomerDTO = mstCustomerMapper.toDto(mstCustomer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, mstCustomerDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstCustomerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstCustomer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCustomerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchMstCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCustomerSearchRepository.findAll().collectList().block());
        mstCustomer.setId(longCount.incrementAndGet());

        // Create the MstCustomer
        MstCustomerDTO mstCustomerDTO = mstCustomerMapper.toDto(mstCustomer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstCustomerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstCustomer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCustomerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamMstCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCustomerSearchRepository.findAll().collectList().block());
        mstCustomer.setId(longCount.incrementAndGet());

        // Create the MstCustomer
        MstCustomerDTO mstCustomerDTO = mstCustomerMapper.toDto(mstCustomer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstCustomerDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstCustomer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCustomerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteMstCustomer() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();
        mstCustomerRepository.save(mstCustomer).block();
        mstCustomerSearchRepository.save(mstCustomer).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCustomerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the mstCustomer
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, mstCustomer.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCustomerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchMstCustomer() {
        // Initialize the database
        insertedMstCustomer = mstCustomerRepository.save(mstCustomer).block();
        mstCustomerSearchRepository.save(mstCustomer).block();

        // Search the mstCustomer
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + mstCustomer.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(mstCustomer.getId().intValue()))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].phoneNumber")
            .value(hasItem(DEFAULT_PHONE_NUMBER))
            .jsonPath("$.[*].address")
            .value(hasItem(DEFAULT_ADDRESS))
            .jsonPath("$.[*].loyaltyPoints")
            .value(hasItem(DEFAULT_LOYALTY_POINTS));
    }

    protected long getRepositoryCount() {
        return mstCustomerRepository.count().block();
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

    protected MstCustomer getPersistedMstCustomer(MstCustomer mstCustomer) {
        return mstCustomerRepository.findById(mstCustomer.getId()).block();
    }

    protected void assertPersistedMstCustomerToMatchAllProperties(MstCustomer expectedMstCustomer) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstCustomerAllPropertiesEquals(expectedMstCustomer, getPersistedMstCustomer(expectedMstCustomer));
        assertMstCustomerUpdatableFieldsEquals(expectedMstCustomer, getPersistedMstCustomer(expectedMstCustomer));
    }

    protected void assertPersistedMstCustomerToMatchUpdatableProperties(MstCustomer expectedMstCustomer) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstCustomerAllUpdatablePropertiesEquals(expectedMstCustomer, getPersistedMstCustomer(expectedMstCustomer));
        assertMstCustomerUpdatableFieldsEquals(expectedMstCustomer, getPersistedMstCustomer(expectedMstCustomer));
    }
}
