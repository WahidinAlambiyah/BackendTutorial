package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.TrxEventAsserts.*;
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
import com.mycompany.myapp.domain.TrxEvent;
import com.mycompany.myapp.domain.TrxTestimonial;
import com.mycompany.myapp.domain.enumeration.EventStatus;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MstServiceRepository;
import com.mycompany.myapp.repository.TrxEventRepository;
import com.mycompany.myapp.repository.TrxTestimonialRepository;
import com.mycompany.myapp.repository.search.TrxEventSearchRepository;
import com.mycompany.myapp.service.dto.TrxEventDTO;
import com.mycompany.myapp.service.mapper.TrxEventMapper;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link TrxEventResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TrxEventResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION = "BBBBBBBBBB";

    private static final Integer DEFAULT_CAPACITY = 1;
    private static final Integer UPDATED_CAPACITY = 2;
    private static final Integer SMALLER_CAPACITY = 1 - 1;

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);
    private static final BigDecimal SMALLER_PRICE = new BigDecimal(1 - 1);

    private static final EventStatus DEFAULT_STATUS = EventStatus.UPCOMING;
    private static final EventStatus UPDATED_STATUS = EventStatus.ONGOING;

    private static final String ENTITY_API_URL = "/api/trx-events";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/trx-events/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TrxEventRepository trxEventRepository;

    @Autowired
    private TrxEventMapper trxEventMapper;

    @Autowired
    private TrxEventSearchRepository trxEventSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TrxEvent trxEvent;

    private TrxEvent insertedTrxEvent;

    @Autowired
    private MstServiceRepository mstServiceRepository;

    @Autowired
    private TrxTestimonialRepository trxTestimonialRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrxEvent createEntity(EntityManager em) {
        TrxEvent trxEvent = new TrxEvent()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .date(DEFAULT_DATE)
            .location(DEFAULT_LOCATION)
            .capacity(DEFAULT_CAPACITY)
            .price(DEFAULT_PRICE)
            .status(DEFAULT_STATUS);
        return trxEvent;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrxEvent createUpdatedEntity(EntityManager em) {
        TrxEvent trxEvent = new TrxEvent()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .date(UPDATED_DATE)
            .location(UPDATED_LOCATION)
            .capacity(UPDATED_CAPACITY)
            .price(UPDATED_PRICE)
            .status(UPDATED_STATUS);
        return trxEvent;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TrxEvent.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        trxEvent = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedTrxEvent != null) {
            trxEventRepository.delete(insertedTrxEvent).block();
            trxEventSearchRepository.delete(insertedTrxEvent).block();
            insertedTrxEvent = null;
        }
        deleteEntities(em);
    }

    @Test
    void createTrxEvent() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxEventSearchRepository.findAll().collectList().block());
        // Create the TrxEvent
        TrxEventDTO trxEventDTO = trxEventMapper.toDto(trxEvent);
        var returnedTrxEventDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxEventDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TrxEventDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the TrxEvent in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTrxEvent = trxEventMapper.toEntity(returnedTrxEventDTO);
        assertTrxEventUpdatableFieldsEquals(returnedTrxEvent, getPersistedTrxEvent(returnedTrxEvent));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxEventSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedTrxEvent = returnedTrxEvent;
    }

    @Test
    void createTrxEventWithExistingId() throws Exception {
        // Create the TrxEvent with an existing ID
        trxEvent.setId(1L);
        TrxEventDTO trxEventDTO = trxEventMapper.toDto(trxEvent);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxEventSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxEventSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxEventSearchRepository.findAll().collectList().block());
        // set the field null
        trxEvent.setTitle(null);

        // Create the TrxEvent, which fails.
        TrxEventDTO trxEventDTO = trxEventMapper.toDto(trxEvent);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxEventSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxEventSearchRepository.findAll().collectList().block());
        // set the field null
        trxEvent.setDate(null);

        // Create the TrxEvent, which fails.
        TrxEventDTO trxEventDTO = trxEventMapper.toDto(trxEvent);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxEventSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllTrxEvents() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList
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
            .value(hasItem(trxEvent.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].date")
            .value(hasItem(DEFAULT_DATE.toString()))
            .jsonPath("$.[*].location")
            .value(hasItem(DEFAULT_LOCATION))
            .jsonPath("$.[*].capacity")
            .value(hasItem(DEFAULT_CAPACITY))
            .jsonPath("$.[*].price")
            .value(hasItem(sameNumber(DEFAULT_PRICE)))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }

    @Test
    void getTrxEvent() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get the trxEvent
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, trxEvent.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(trxEvent.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.date")
            .value(is(DEFAULT_DATE.toString()))
            .jsonPath("$.location")
            .value(is(DEFAULT_LOCATION))
            .jsonPath("$.capacity")
            .value(is(DEFAULT_CAPACITY))
            .jsonPath("$.price")
            .value(is(sameNumber(DEFAULT_PRICE)))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()));
    }

    @Test
    void getTrxEventsByIdFiltering() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        Long id = trxEvent.getId();

        defaultTrxEventFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTrxEventFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTrxEventFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllTrxEventsByTitleIsEqualToSomething() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where title equals to
        defaultTrxEventFiltering("title.equals=" + DEFAULT_TITLE, "title.equals=" + UPDATED_TITLE);
    }

    @Test
    void getAllTrxEventsByTitleIsInShouldWork() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where title in
        defaultTrxEventFiltering("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE, "title.in=" + UPDATED_TITLE);
    }

    @Test
    void getAllTrxEventsByTitleIsNullOrNotNull() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where title is not null
        defaultTrxEventFiltering("title.specified=true", "title.specified=false");
    }

    @Test
    void getAllTrxEventsByTitleContainsSomething() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where title contains
        defaultTrxEventFiltering("title.contains=" + DEFAULT_TITLE, "title.contains=" + UPDATED_TITLE);
    }

    @Test
    void getAllTrxEventsByTitleNotContainsSomething() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where title does not contain
        defaultTrxEventFiltering("title.doesNotContain=" + UPDATED_TITLE, "title.doesNotContain=" + DEFAULT_TITLE);
    }

    @Test
    void getAllTrxEventsByDateIsEqualToSomething() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where date equals to
        defaultTrxEventFiltering("date.equals=" + DEFAULT_DATE, "date.equals=" + UPDATED_DATE);
    }

    @Test
    void getAllTrxEventsByDateIsInShouldWork() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where date in
        defaultTrxEventFiltering("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE, "date.in=" + UPDATED_DATE);
    }

    @Test
    void getAllTrxEventsByDateIsNullOrNotNull() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where date is not null
        defaultTrxEventFiltering("date.specified=true", "date.specified=false");
    }

    @Test
    void getAllTrxEventsByLocationIsEqualToSomething() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where location equals to
        defaultTrxEventFiltering("location.equals=" + DEFAULT_LOCATION, "location.equals=" + UPDATED_LOCATION);
    }

    @Test
    void getAllTrxEventsByLocationIsInShouldWork() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where location in
        defaultTrxEventFiltering("location.in=" + DEFAULT_LOCATION + "," + UPDATED_LOCATION, "location.in=" + UPDATED_LOCATION);
    }

    @Test
    void getAllTrxEventsByLocationIsNullOrNotNull() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where location is not null
        defaultTrxEventFiltering("location.specified=true", "location.specified=false");
    }

    @Test
    void getAllTrxEventsByLocationContainsSomething() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where location contains
        defaultTrxEventFiltering("location.contains=" + DEFAULT_LOCATION, "location.contains=" + UPDATED_LOCATION);
    }

    @Test
    void getAllTrxEventsByLocationNotContainsSomething() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where location does not contain
        defaultTrxEventFiltering("location.doesNotContain=" + UPDATED_LOCATION, "location.doesNotContain=" + DEFAULT_LOCATION);
    }

    @Test
    void getAllTrxEventsByCapacityIsEqualToSomething() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where capacity equals to
        defaultTrxEventFiltering("capacity.equals=" + DEFAULT_CAPACITY, "capacity.equals=" + UPDATED_CAPACITY);
    }

    @Test
    void getAllTrxEventsByCapacityIsInShouldWork() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where capacity in
        defaultTrxEventFiltering("capacity.in=" + DEFAULT_CAPACITY + "," + UPDATED_CAPACITY, "capacity.in=" + UPDATED_CAPACITY);
    }

    @Test
    void getAllTrxEventsByCapacityIsNullOrNotNull() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where capacity is not null
        defaultTrxEventFiltering("capacity.specified=true", "capacity.specified=false");
    }

    @Test
    void getAllTrxEventsByCapacityIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where capacity is greater than or equal to
        defaultTrxEventFiltering("capacity.greaterThanOrEqual=" + DEFAULT_CAPACITY, "capacity.greaterThanOrEqual=" + UPDATED_CAPACITY);
    }

    @Test
    void getAllTrxEventsByCapacityIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where capacity is less than or equal to
        defaultTrxEventFiltering("capacity.lessThanOrEqual=" + DEFAULT_CAPACITY, "capacity.lessThanOrEqual=" + SMALLER_CAPACITY);
    }

    @Test
    void getAllTrxEventsByCapacityIsLessThanSomething() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where capacity is less than
        defaultTrxEventFiltering("capacity.lessThan=" + UPDATED_CAPACITY, "capacity.lessThan=" + DEFAULT_CAPACITY);
    }

    @Test
    void getAllTrxEventsByCapacityIsGreaterThanSomething() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where capacity is greater than
        defaultTrxEventFiltering("capacity.greaterThan=" + SMALLER_CAPACITY, "capacity.greaterThan=" + DEFAULT_CAPACITY);
    }

    @Test
    void getAllTrxEventsByPriceIsEqualToSomething() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where price equals to
        defaultTrxEventFiltering("price.equals=" + DEFAULT_PRICE, "price.equals=" + UPDATED_PRICE);
    }

    @Test
    void getAllTrxEventsByPriceIsInShouldWork() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where price in
        defaultTrxEventFiltering("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE, "price.in=" + UPDATED_PRICE);
    }

    @Test
    void getAllTrxEventsByPriceIsNullOrNotNull() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where price is not null
        defaultTrxEventFiltering("price.specified=true", "price.specified=false");
    }

    @Test
    void getAllTrxEventsByPriceIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where price is greater than or equal to
        defaultTrxEventFiltering("price.greaterThanOrEqual=" + DEFAULT_PRICE, "price.greaterThanOrEqual=" + UPDATED_PRICE);
    }

    @Test
    void getAllTrxEventsByPriceIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where price is less than or equal to
        defaultTrxEventFiltering("price.lessThanOrEqual=" + DEFAULT_PRICE, "price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    void getAllTrxEventsByPriceIsLessThanSomething() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where price is less than
        defaultTrxEventFiltering("price.lessThan=" + UPDATED_PRICE, "price.lessThan=" + DEFAULT_PRICE);
    }

    @Test
    void getAllTrxEventsByPriceIsGreaterThanSomething() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where price is greater than
        defaultTrxEventFiltering("price.greaterThan=" + SMALLER_PRICE, "price.greaterThan=" + DEFAULT_PRICE);
    }

    @Test
    void getAllTrxEventsByStatusIsEqualToSomething() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where status equals to
        defaultTrxEventFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    void getAllTrxEventsByStatusIsInShouldWork() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where status in
        defaultTrxEventFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    void getAllTrxEventsByStatusIsNullOrNotNull() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        // Get all the trxEventList where status is not null
        defaultTrxEventFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    void getAllTrxEventsByServiceIsEqualToSomething() {
        MstService service = MstServiceResourceIT.createEntity(em);
        mstServiceRepository.save(service).block();
        Long serviceId = service.getId();
        trxEvent.setServiceId(serviceId);
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();
        // Get all the trxEventList where service equals to serviceId
        defaultTrxEventShouldBeFound("serviceId.equals=" + serviceId);

        // Get all the trxEventList where service equals to (serviceId + 1)
        defaultTrxEventShouldNotBeFound("serviceId.equals=" + (serviceId + 1));
    }

    @Test
    void getAllTrxEventsByTestimonialIsEqualToSomething() {
        TrxTestimonial testimonial = TrxTestimonialResourceIT.createEntity(em);
        trxTestimonialRepository.save(testimonial).block();
        Long testimonialId = testimonial.getId();
        trxEvent.setTestimonialId(testimonialId);
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();
        // Get all the trxEventList where testimonial equals to testimonialId
        defaultTrxEventShouldBeFound("testimonialId.equals=" + testimonialId);

        // Get all the trxEventList where testimonial equals to (testimonialId + 1)
        defaultTrxEventShouldNotBeFound("testimonialId.equals=" + (testimonialId + 1));
    }

    private void defaultTrxEventFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultTrxEventShouldBeFound(shouldBeFound);
        defaultTrxEventShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTrxEventShouldBeFound(String filter) {
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
            .value(hasItem(trxEvent.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].date")
            .value(hasItem(DEFAULT_DATE.toString()))
            .jsonPath("$.[*].location")
            .value(hasItem(DEFAULT_LOCATION))
            .jsonPath("$.[*].capacity")
            .value(hasItem(DEFAULT_CAPACITY))
            .jsonPath("$.[*].price")
            .value(hasItem(sameNumber(DEFAULT_PRICE)))
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
    private void defaultTrxEventShouldNotBeFound(String filter) {
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
    void getNonExistingTrxEvent() {
        // Get the trxEvent
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTrxEvent() throws Exception {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        trxEventSearchRepository.save(trxEvent).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxEventSearchRepository.findAll().collectList().block());

        // Update the trxEvent
        TrxEvent updatedTrxEvent = trxEventRepository.findById(trxEvent.getId()).block();
        updatedTrxEvent
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .date(UPDATED_DATE)
            .location(UPDATED_LOCATION)
            .capacity(UPDATED_CAPACITY)
            .price(UPDATED_PRICE)
            .status(UPDATED_STATUS);
        TrxEventDTO trxEventDTO = trxEventMapper.toDto(updatedTrxEvent);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trxEventDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxEventDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTrxEventToMatchAllProperties(updatedTrxEvent);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxEventSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TrxEvent> trxEventSearchList = Streamable.of(trxEventSearchRepository.findAll().collectList().block()).toList();
                TrxEvent testTrxEventSearch = trxEventSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertTrxEventAllPropertiesEquals(testTrxEventSearch, updatedTrxEvent);
                assertTrxEventUpdatableFieldsEquals(testTrxEventSearch, updatedTrxEvent);
            });
    }

    @Test
    void putNonExistingTrxEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxEventSearchRepository.findAll().collectList().block());
        trxEvent.setId(longCount.incrementAndGet());

        // Create the TrxEvent
        TrxEventDTO trxEventDTO = trxEventMapper.toDto(trxEvent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trxEventDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxEventSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchTrxEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxEventSearchRepository.findAll().collectList().block());
        trxEvent.setId(longCount.incrementAndGet());

        // Create the TrxEvent
        TrxEventDTO trxEventDTO = trxEventMapper.toDto(trxEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxEventSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamTrxEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxEventSearchRepository.findAll().collectList().block());
        trxEvent.setId(longCount.incrementAndGet());

        // Create the TrxEvent
        TrxEventDTO trxEventDTO = trxEventMapper.toDto(trxEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxEventDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrxEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxEventSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateTrxEventWithPatch() throws Exception {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trxEvent using partial update
        TrxEvent partialUpdatedTrxEvent = new TrxEvent();
        partialUpdatedTrxEvent.setId(trxEvent.getId());

        partialUpdatedTrxEvent.description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrxEvent.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrxEvent))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxEvent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrxEventUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTrxEvent, trxEvent), getPersistedTrxEvent(trxEvent));
    }

    @Test
    void fullUpdateTrxEventWithPatch() throws Exception {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trxEvent using partial update
        TrxEvent partialUpdatedTrxEvent = new TrxEvent();
        partialUpdatedTrxEvent.setId(trxEvent.getId());

        partialUpdatedTrxEvent
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .date(UPDATED_DATE)
            .location(UPDATED_LOCATION)
            .capacity(UPDATED_CAPACITY)
            .price(UPDATED_PRICE)
            .status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrxEvent.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrxEvent))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxEvent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrxEventUpdatableFieldsEquals(partialUpdatedTrxEvent, getPersistedTrxEvent(partialUpdatedTrxEvent));
    }

    @Test
    void patchNonExistingTrxEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxEventSearchRepository.findAll().collectList().block());
        trxEvent.setId(longCount.incrementAndGet());

        // Create the TrxEvent
        TrxEventDTO trxEventDTO = trxEventMapper.toDto(trxEvent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, trxEventDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxEventSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchTrxEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxEventSearchRepository.findAll().collectList().block());
        trxEvent.setId(longCount.incrementAndGet());

        // Create the TrxEvent
        TrxEventDTO trxEventDTO = trxEventMapper.toDto(trxEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxEventSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamTrxEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxEventSearchRepository.findAll().collectList().block());
        trxEvent.setId(longCount.incrementAndGet());

        // Create the TrxEvent
        TrxEventDTO trxEventDTO = trxEventMapper.toDto(trxEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxEventDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrxEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxEventSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteTrxEvent() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();
        trxEventRepository.save(trxEvent).block();
        trxEventSearchRepository.save(trxEvent).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxEventSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the trxEvent
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, trxEvent.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxEventSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchTrxEvent() {
        // Initialize the database
        insertedTrxEvent = trxEventRepository.save(trxEvent).block();
        trxEventSearchRepository.save(trxEvent).block();

        // Search the trxEvent
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + trxEvent.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(trxEvent.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].date")
            .value(hasItem(DEFAULT_DATE.toString()))
            .jsonPath("$.[*].location")
            .value(hasItem(DEFAULT_LOCATION))
            .jsonPath("$.[*].capacity")
            .value(hasItem(DEFAULT_CAPACITY))
            .jsonPath("$.[*].price")
            .value(hasItem(sameNumber(DEFAULT_PRICE)))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }

    protected long getRepositoryCount() {
        return trxEventRepository.count().block();
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

    protected TrxEvent getPersistedTrxEvent(TrxEvent trxEvent) {
        return trxEventRepository.findById(trxEvent.getId()).block();
    }

    protected void assertPersistedTrxEventToMatchAllProperties(TrxEvent expectedTrxEvent) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrxEventAllPropertiesEquals(expectedTrxEvent, getPersistedTrxEvent(expectedTrxEvent));
        assertTrxEventUpdatableFieldsEquals(expectedTrxEvent, getPersistedTrxEvent(expectedTrxEvent));
    }

    protected void assertPersistedTrxEventToMatchUpdatableProperties(TrxEvent expectedTrxEvent) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrxEventAllUpdatablePropertiesEquals(expectedTrxEvent, getPersistedTrxEvent(expectedTrxEvent));
        assertTrxEventUpdatableFieldsEquals(expectedTrxEvent, getPersistedTrxEvent(expectedTrxEvent));
    }
}
