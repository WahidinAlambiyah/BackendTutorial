package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.MstServiceAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.MstService;
import com.mycompany.myapp.domain.TrxTestimonial;
import com.mycompany.myapp.domain.enumeration.ServiceType;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MstServiceRepository;
import com.mycompany.myapp.repository.TrxTestimonialRepository;
import com.mycompany.myapp.repository.search.MstServiceSearchRepository;
import com.mycompany.myapp.service.dto.MstServiceDTO;
import com.mycompany.myapp.service.mapper.MstServiceMapper;
import java.math.BigDecimal;
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
 * Integration tests for the {@link MstServiceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MstServiceResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);
    private static final BigDecimal SMALLER_PRICE = new BigDecimal(1 - 1);

    private static final Integer DEFAULT_DURATION_IN_HOURS = 1;
    private static final Integer UPDATED_DURATION_IN_HOURS = 2;
    private static final Integer SMALLER_DURATION_IN_HOURS = 1 - 1;

    private static final ServiceType DEFAULT_SERVICE_TYPE = ServiceType.EVENT_MANAGEMENT;
    private static final ServiceType UPDATED_SERVICE_TYPE = ServiceType.TICKETING;

    private static final String ENTITY_API_URL = "/api/mst-services";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/mst-services/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MstServiceRepository mstServiceRepository;

    @Autowired
    private MstServiceMapper mstServiceMapper;

    @Autowired
    private MstServiceSearchRepository mstServiceSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private MstService mstService;

    private MstService insertedMstService;

    @Autowired
    private TrxTestimonialRepository trxTestimonialRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstService createEntity(EntityManager em) {
        MstService mstService = new MstService()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .price(DEFAULT_PRICE)
            .durationInHours(DEFAULT_DURATION_IN_HOURS)
            .serviceType(DEFAULT_SERVICE_TYPE);
        return mstService;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstService createUpdatedEntity(EntityManager em) {
        MstService mstService = new MstService()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .durationInHours(UPDATED_DURATION_IN_HOURS)
            .serviceType(UPDATED_SERVICE_TYPE);
        return mstService;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(MstService.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        mstService = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedMstService != null) {
            mstServiceRepository.delete(insertedMstService).block();
            mstServiceSearchRepository.delete(insertedMstService).block();
            insertedMstService = null;
        }
        deleteEntities(em);
    }

    @Test
    void createMstService() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstServiceSearchRepository.findAll().collectList().block());
        // Create the MstService
        MstServiceDTO mstServiceDTO = mstServiceMapper.toDto(mstService);
        var returnedMstServiceDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstServiceDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MstServiceDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MstService in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMstService = mstServiceMapper.toEntity(returnedMstServiceDTO);
        assertMstServiceUpdatableFieldsEquals(returnedMstService, getPersistedMstService(returnedMstService));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstServiceSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedMstService = returnedMstService;
    }

    @Test
    void createMstServiceWithExistingId() throws Exception {
        // Create the MstService with an existing ID
        mstService.setId(1L);
        MstServiceDTO mstServiceDTO = mstServiceMapper.toDto(mstService);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstServiceSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstServiceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstService in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstServiceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstServiceSearchRepository.findAll().collectList().block());
        // set the field null
        mstService.setName(null);

        // Create the MstService, which fails.
        MstServiceDTO mstServiceDTO = mstServiceMapper.toDto(mstService);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstServiceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstServiceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllMstServices() {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();

        // Get all the mstServiceList
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
            .value(hasItem(mstService.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].price")
            .value(hasItem(sameNumber(DEFAULT_PRICE)))
            .jsonPath("$.[*].durationInHours")
            .value(hasItem(DEFAULT_DURATION_IN_HOURS))
            .jsonPath("$.[*].serviceType")
            .value(hasItem(DEFAULT_SERVICE_TYPE.toString()));
    }

    @Test
    void getMstService() {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();

        // Get the mstService
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, mstService.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(mstService.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.price")
            .value(is(sameNumber(DEFAULT_PRICE)))
            .jsonPath("$.durationInHours")
            .value(is(DEFAULT_DURATION_IN_HOURS))
            .jsonPath("$.serviceType")
            .value(is(DEFAULT_SERVICE_TYPE.toString()));
    }

    @Test
    void getMstServicesByIdFiltering() {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();

        Long id = mstService.getId();

        defaultMstServiceFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMstServiceFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMstServiceFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllMstServicesByNameIsEqualToSomething() {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();

        // Get all the mstServiceList where name equals to
        defaultMstServiceFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllMstServicesByNameIsInShouldWork() {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();

        // Get all the mstServiceList where name in
        defaultMstServiceFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllMstServicesByNameIsNullOrNotNull() {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();

        // Get all the mstServiceList where name is not null
        defaultMstServiceFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllMstServicesByNameContainsSomething() {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();

        // Get all the mstServiceList where name contains
        defaultMstServiceFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllMstServicesByNameNotContainsSomething() {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();

        // Get all the mstServiceList where name does not contain
        defaultMstServiceFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllMstServicesByPriceIsEqualToSomething() {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();

        // Get all the mstServiceList where price equals to
        defaultMstServiceFiltering("price.equals=" + DEFAULT_PRICE, "price.equals=" + UPDATED_PRICE);
    }

    @Test
    void getAllMstServicesByPriceIsInShouldWork() {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();

        // Get all the mstServiceList where price in
        defaultMstServiceFiltering("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE, "price.in=" + UPDATED_PRICE);
    }

    @Test
    void getAllMstServicesByPriceIsNullOrNotNull() {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();

        // Get all the mstServiceList where price is not null
        defaultMstServiceFiltering("price.specified=true", "price.specified=false");
    }

    @Test
    void getAllMstServicesByPriceIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();

        // Get all the mstServiceList where price is greater than or equal to
        defaultMstServiceFiltering("price.greaterThanOrEqual=" + DEFAULT_PRICE, "price.greaterThanOrEqual=" + UPDATED_PRICE);
    }

    @Test
    void getAllMstServicesByPriceIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();

        // Get all the mstServiceList where price is less than or equal to
        defaultMstServiceFiltering("price.lessThanOrEqual=" + DEFAULT_PRICE, "price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    void getAllMstServicesByPriceIsLessThanSomething() {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();

        // Get all the mstServiceList where price is less than
        defaultMstServiceFiltering("price.lessThan=" + UPDATED_PRICE, "price.lessThan=" + DEFAULT_PRICE);
    }

    @Test
    void getAllMstServicesByPriceIsGreaterThanSomething() {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();

        // Get all the mstServiceList where price is greater than
        defaultMstServiceFiltering("price.greaterThan=" + SMALLER_PRICE, "price.greaterThan=" + DEFAULT_PRICE);
    }

    @Test
    void getAllMstServicesByDurationInHoursIsEqualToSomething() {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();

        // Get all the mstServiceList where durationInHours equals to
        defaultMstServiceFiltering(
            "durationInHours.equals=" + DEFAULT_DURATION_IN_HOURS,
            "durationInHours.equals=" + UPDATED_DURATION_IN_HOURS
        );
    }

    @Test
    void getAllMstServicesByDurationInHoursIsInShouldWork() {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();

        // Get all the mstServiceList where durationInHours in
        defaultMstServiceFiltering(
            "durationInHours.in=" + DEFAULT_DURATION_IN_HOURS + "," + UPDATED_DURATION_IN_HOURS,
            "durationInHours.in=" + UPDATED_DURATION_IN_HOURS
        );
    }

    @Test
    void getAllMstServicesByDurationInHoursIsNullOrNotNull() {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();

        // Get all the mstServiceList where durationInHours is not null
        defaultMstServiceFiltering("durationInHours.specified=true", "durationInHours.specified=false");
    }

    @Test
    void getAllMstServicesByDurationInHoursIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();

        // Get all the mstServiceList where durationInHours is greater than or equal to
        defaultMstServiceFiltering(
            "durationInHours.greaterThanOrEqual=" + DEFAULT_DURATION_IN_HOURS,
            "durationInHours.greaterThanOrEqual=" + UPDATED_DURATION_IN_HOURS
        );
    }

    @Test
    void getAllMstServicesByDurationInHoursIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();

        // Get all the mstServiceList where durationInHours is less than or equal to
        defaultMstServiceFiltering(
            "durationInHours.lessThanOrEqual=" + DEFAULT_DURATION_IN_HOURS,
            "durationInHours.lessThanOrEqual=" + SMALLER_DURATION_IN_HOURS
        );
    }

    @Test
    void getAllMstServicesByDurationInHoursIsLessThanSomething() {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();

        // Get all the mstServiceList where durationInHours is less than
        defaultMstServiceFiltering(
            "durationInHours.lessThan=" + UPDATED_DURATION_IN_HOURS,
            "durationInHours.lessThan=" + DEFAULT_DURATION_IN_HOURS
        );
    }

    @Test
    void getAllMstServicesByDurationInHoursIsGreaterThanSomething() {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();

        // Get all the mstServiceList where durationInHours is greater than
        defaultMstServiceFiltering(
            "durationInHours.greaterThan=" + SMALLER_DURATION_IN_HOURS,
            "durationInHours.greaterThan=" + DEFAULT_DURATION_IN_HOURS
        );
    }

    @Test
    void getAllMstServicesByServiceTypeIsEqualToSomething() {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();

        // Get all the mstServiceList where serviceType equals to
        defaultMstServiceFiltering("serviceType.equals=" + DEFAULT_SERVICE_TYPE, "serviceType.equals=" + UPDATED_SERVICE_TYPE);
    }

    @Test
    void getAllMstServicesByServiceTypeIsInShouldWork() {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();

        // Get all the mstServiceList where serviceType in
        defaultMstServiceFiltering(
            "serviceType.in=" + DEFAULT_SERVICE_TYPE + "," + UPDATED_SERVICE_TYPE,
            "serviceType.in=" + UPDATED_SERVICE_TYPE
        );
    }

    @Test
    void getAllMstServicesByServiceTypeIsNullOrNotNull() {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();

        // Get all the mstServiceList where serviceType is not null
        defaultMstServiceFiltering("serviceType.specified=true", "serviceType.specified=false");
    }

    @Test
    void getAllMstServicesByTestimonialIsEqualToSomething() {
        TrxTestimonial testimonial = TrxTestimonialResourceIT.createEntity(em);
        trxTestimonialRepository.save(testimonial).block();
        Long testimonialId = testimonial.getId();
        mstService.setTestimonialId(testimonialId);
        insertedMstService = mstServiceRepository.save(mstService).block();
        // Get all the mstServiceList where testimonial equals to testimonialId
        defaultMstServiceShouldBeFound("testimonialId.equals=" + testimonialId);

        // Get all the mstServiceList where testimonial equals to (testimonialId + 1)
        defaultMstServiceShouldNotBeFound("testimonialId.equals=" + (testimonialId + 1));
    }

    private void defaultMstServiceFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultMstServiceShouldBeFound(shouldBeFound);
        defaultMstServiceShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMstServiceShouldBeFound(String filter) {
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
            .value(hasItem(mstService.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].price")
            .value(hasItem(sameNumber(DEFAULT_PRICE)))
            .jsonPath("$.[*].durationInHours")
            .value(hasItem(DEFAULT_DURATION_IN_HOURS))
            .jsonPath("$.[*].serviceType")
            .value(hasItem(DEFAULT_SERVICE_TYPE.toString()));

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
    private void defaultMstServiceShouldNotBeFound(String filter) {
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
    void getNonExistingMstService() {
        // Get the mstService
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMstService() throws Exception {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        mstServiceSearchRepository.save(mstService).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstServiceSearchRepository.findAll().collectList().block());

        // Update the mstService
        MstService updatedMstService = mstServiceRepository.findById(mstService.getId()).block();
        updatedMstService
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .durationInHours(UPDATED_DURATION_IN_HOURS)
            .serviceType(UPDATED_SERVICE_TYPE);
        MstServiceDTO mstServiceDTO = mstServiceMapper.toDto(updatedMstService);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstServiceDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstServiceDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMstServiceToMatchAllProperties(updatedMstService);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstServiceSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<MstService> mstServiceSearchList = Streamable.of(mstServiceSearchRepository.findAll().collectList().block()).toList();
                MstService testMstServiceSearch = mstServiceSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertMstServiceAllPropertiesEquals(testMstServiceSearch, updatedMstService);
                assertMstServiceUpdatableFieldsEquals(testMstServiceSearch, updatedMstService);
            });
    }

    @Test
    void putNonExistingMstService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstServiceSearchRepository.findAll().collectList().block());
        mstService.setId(longCount.incrementAndGet());

        // Create the MstService
        MstServiceDTO mstServiceDTO = mstServiceMapper.toDto(mstService);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstServiceDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstServiceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstServiceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchMstService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstServiceSearchRepository.findAll().collectList().block());
        mstService.setId(longCount.incrementAndGet());

        // Create the MstService
        MstServiceDTO mstServiceDTO = mstServiceMapper.toDto(mstService);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstServiceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstServiceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamMstService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstServiceSearchRepository.findAll().collectList().block());
        mstService.setId(longCount.incrementAndGet());

        // Create the MstService
        MstServiceDTO mstServiceDTO = mstServiceMapper.toDto(mstService);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstServiceDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstServiceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateMstServiceWithPatch() throws Exception {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstService using partial update
        MstService partialUpdatedMstService = new MstService();
        partialUpdatedMstService.setId(mstService.getId());

        partialUpdatedMstService.name(UPDATED_NAME).price(UPDATED_PRICE).durationInHours(UPDATED_DURATION_IN_HOURS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstService.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstService))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstService in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstServiceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMstService, mstService),
            getPersistedMstService(mstService)
        );
    }

    @Test
    void fullUpdateMstServiceWithPatch() throws Exception {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstService using partial update
        MstService partialUpdatedMstService = new MstService();
        partialUpdatedMstService.setId(mstService.getId());

        partialUpdatedMstService
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .durationInHours(UPDATED_DURATION_IN_HOURS)
            .serviceType(UPDATED_SERVICE_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstService.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstService))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstService in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstServiceUpdatableFieldsEquals(partialUpdatedMstService, getPersistedMstService(partialUpdatedMstService));
    }

    @Test
    void patchNonExistingMstService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstServiceSearchRepository.findAll().collectList().block());
        mstService.setId(longCount.incrementAndGet());

        // Create the MstService
        MstServiceDTO mstServiceDTO = mstServiceMapper.toDto(mstService);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, mstServiceDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstServiceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstServiceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchMstService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstServiceSearchRepository.findAll().collectList().block());
        mstService.setId(longCount.incrementAndGet());

        // Create the MstService
        MstServiceDTO mstServiceDTO = mstServiceMapper.toDto(mstService);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstServiceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstServiceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamMstService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstServiceSearchRepository.findAll().collectList().block());
        mstService.setId(longCount.incrementAndGet());

        // Create the MstService
        MstServiceDTO mstServiceDTO = mstServiceMapper.toDto(mstService);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstServiceDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstServiceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteMstService() {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();
        mstServiceRepository.save(mstService).block();
        mstServiceSearchRepository.save(mstService).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstServiceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the mstService
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, mstService.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstServiceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchMstService() {
        // Initialize the database
        insertedMstService = mstServiceRepository.save(mstService).block();
        mstServiceSearchRepository.save(mstService).block();

        // Search the mstService
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + mstService.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(mstService.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].price")
            .value(hasItem(sameNumber(DEFAULT_PRICE)))
            .jsonPath("$.[*].durationInHours")
            .value(hasItem(DEFAULT_DURATION_IN_HOURS))
            .jsonPath("$.[*].serviceType")
            .value(hasItem(DEFAULT_SERVICE_TYPE.toString()));
    }

    protected long getRepositoryCount() {
        return mstServiceRepository.count().block();
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

    protected MstService getPersistedMstService(MstService mstService) {
        return mstServiceRepository.findById(mstService.getId()).block();
    }

    protected void assertPersistedMstServiceToMatchAllProperties(MstService expectedMstService) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstServiceAllPropertiesEquals(expectedMstService, getPersistedMstService(expectedMstService));
        assertMstServiceUpdatableFieldsEquals(expectedMstService, getPersistedMstService(expectedMstService));
    }

    protected void assertPersistedMstServiceToMatchUpdatableProperties(MstService expectedMstService) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstServiceAllUpdatablePropertiesEquals(expectedMstService, getPersistedMstService(expectedMstService));
        assertMstServiceUpdatableFieldsEquals(expectedMstService, getPersistedMstService(expectedMstService));
    }
}
