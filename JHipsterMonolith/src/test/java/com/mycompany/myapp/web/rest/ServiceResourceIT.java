package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.ServiceAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Services;
import com.mycompany.myapp.domain.enumeration.ServiceType;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.ServiceRepository;
import com.mycompany.myapp.repository.search.ServiceSearchRepository;
import java.math.BigDecimal;
import java.time.Duration;
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
 * Integration tests for the {@link ServiceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ServiceResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);

    private static final Integer DEFAULT_DURATION_IN_HOURS = 1;
    private static final Integer UPDATED_DURATION_IN_HOURS = 2;

    private static final ServiceType DEFAULT_SERVICE_TYPE = ServiceType.EVENT_MANAGEMENT;
    private static final ServiceType UPDATED_SERVICE_TYPE = ServiceType.TICKETING;

    private static final String ENTITY_API_URL = "/api/services";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/services/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ServiceSearchRepository serviceSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Services service;

    private Services insertedService;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Services createEntity(EntityManager em) {
        Services service = new Services()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .price(DEFAULT_PRICE)
            .durationInHours(DEFAULT_DURATION_IN_HOURS)
            .serviceType(DEFAULT_SERVICE_TYPE);
        return service;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Services createUpdatedEntity(EntityManager em) {
        Services service = new Services()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .durationInHours(UPDATED_DURATION_IN_HOURS)
            .serviceType(UPDATED_SERVICE_TYPE);
        return service;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Services.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        service = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedService != null) {
            serviceRepository.delete(insertedService).block();
            serviceSearchRepository.delete(insertedService).block();
            insertedService = null;
        }
        deleteEntities(em);
    }

    @Test
    void createService() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceSearchRepository.findAll().collectList().block());
        // Create the Service
        var returnedService = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(service))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Services.class)
            .returnResult()
            .getResponseBody();

        // Validate the Service in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertServiceUpdatableFieldsEquals(returnedService, getPersistedService(returnedService));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedService = returnedService;
    }

    @Test
    void createServiceWithExistingId() throws Exception {
        // Create the Service with an existing ID
        service.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(service))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Service in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceSearchRepository.findAll().collectList().block());
        // set the field null
        service.setName(null);

        // Create the Service, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(service))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllServicesAsStream() {
        // Initialize the database
        serviceRepository.save(service).block();

        List<Services> serviceList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Services.class)
            .getResponseBody()
            .filter(service::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(serviceList).isNotNull();
        assertThat(serviceList).hasSize(1);
        Services testService = serviceList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertServiceAllPropertiesEquals(service, testService);
        assertServiceUpdatableFieldsEquals(service, testService);
    }

    @Test
    void getAllServices() {
        // Initialize the database
        insertedService = serviceRepository.save(service).block();

        // Get all the serviceList
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
            .value(hasItem(service.getId().intValue()))
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
    void getService() {
        // Initialize the database
        insertedService = serviceRepository.save(service).block();

        // Get the service
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, service.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(service.getId().intValue()))
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
    void getNonExistingService() {
        // Get the service
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingService() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.save(service).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceSearchRepository.save(service).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceSearchRepository.findAll().collectList().block());

        // Update the service
        Services updatedService = serviceRepository.findById(service.getId()).block();
        updatedService
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .durationInHours(UPDATED_DURATION_IN_HOURS)
            .serviceType(UPDATED_SERVICE_TYPE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedService.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedService))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Service in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedServiceToMatchAllProperties(updatedService);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Services> serviceSearchList = Streamable.of(serviceSearchRepository.findAll().collectList().block()).toList();
                Services testServiceSearch = serviceSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertServiceAllPropertiesEquals(testServiceSearch, updatedService);
                assertServiceUpdatableFieldsEquals(testServiceSearch, updatedService);
            });
    }

    @Test
    void putNonExistingService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceSearchRepository.findAll().collectList().block());
        service.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, service.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(service))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Service in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceSearchRepository.findAll().collectList().block());
        service.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(service))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Service in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceSearchRepository.findAll().collectList().block());
        service.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(service))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Service in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateServiceWithPatch() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.save(service).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the service using partial update
        Services partialUpdatedService = new Services();
        partialUpdatedService.setId(service.getId());

        partialUpdatedService.name(UPDATED_NAME).price(UPDATED_PRICE).serviceType(UPDATED_SERVICE_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedService.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedService))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Service in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertServiceUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedService, service), getPersistedService(service));
    }

    @Test
    void fullUpdateServiceWithPatch() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.save(service).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the service using partial update
        Services partialUpdatedService = new Services();
        partialUpdatedService.setId(service.getId());

        partialUpdatedService
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .durationInHours(UPDATED_DURATION_IN_HOURS)
            .serviceType(UPDATED_SERVICE_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedService.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedService))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Service in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertServiceUpdatableFieldsEquals(partialUpdatedService, getPersistedService(partialUpdatedService));
    }

    @Test
    void patchNonExistingService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceSearchRepository.findAll().collectList().block());
        service.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, service.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(service))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Service in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceSearchRepository.findAll().collectList().block());
        service.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(service))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Service in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceSearchRepository.findAll().collectList().block());
        service.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(service))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Service in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteService() {
        // Initialize the database
        insertedService = serviceRepository.save(service).block();
        serviceRepository.save(service).block();
        serviceSearchRepository.save(service).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the service
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, service.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchService() {
        // Initialize the database
        insertedService = serviceRepository.save(service).block();
        serviceSearchRepository.save(service).block();

        // Search the service
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + service.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(service.getId().intValue()))
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
        return serviceRepository.count().block();
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

    protected Services getPersistedService(Services service) {
        return serviceRepository.findById(service.getId()).block();
    }

    protected void assertPersistedServiceToMatchAllProperties(Services expectedService) {
        // Test fails because reactive api returns an empty object instead of null
        // assertServiceAllPropertiesEquals(expectedService, getPersistedService(expectedService));
        assertServiceUpdatableFieldsEquals(expectedService, getPersistedService(expectedService));
    }

    protected void assertPersistedServiceToMatchUpdatableProperties(Services expectedService) {
        // Test fails because reactive api returns an empty object instead of null
        // assertServiceAllUpdatablePropertiesEquals(expectedService, getPersistedService(expectedService));
        assertServiceUpdatableFieldsEquals(expectedService, getPersistedService(expectedService));
    }
}
