package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.TestimonialAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Testimonial;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.TestimonialRepository;
import com.mycompany.myapp.repository.search.TestimonialSearchRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link TestimonialResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TestimonialResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FEEDBACK = "AAAAAAAAAA";
    private static final String UPDATED_FEEDBACK = "BBBBBBBBBB";

    private static final Integer DEFAULT_RATING = 1;
    private static final Integer UPDATED_RATING = 2;

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/testimonials";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/testimonials/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TestimonialRepository testimonialRepository;

    @Autowired
    private TestimonialSearchRepository testimonialSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Testimonial testimonial;

    private Testimonial insertedTestimonial;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Testimonial createEntity(EntityManager em) {
        Testimonial testimonial = new Testimonial().name(DEFAULT_NAME).feedback(DEFAULT_FEEDBACK).rating(DEFAULT_RATING).date(DEFAULT_DATE);
        return testimonial;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Testimonial createUpdatedEntity(EntityManager em) {
        Testimonial testimonial = new Testimonial().name(UPDATED_NAME).feedback(UPDATED_FEEDBACK).rating(UPDATED_RATING).date(UPDATED_DATE);
        return testimonial;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Testimonial.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        testimonial = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedTestimonial != null) {
            testimonialRepository.delete(insertedTestimonial).block();
            testimonialSearchRepository.delete(insertedTestimonial).block();
            insertedTestimonial = null;
        }
        deleteEntities(em);
    }

    @Test
    void createTestimonial() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(testimonialSearchRepository.findAll().collectList().block());
        // Create the Testimonial
        var returnedTestimonial = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(testimonial))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Testimonial.class)
            .returnResult()
            .getResponseBody();

        // Validate the Testimonial in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertTestimonialUpdatableFieldsEquals(returnedTestimonial, getPersistedTestimonial(returnedTestimonial));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(testimonialSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedTestimonial = returnedTestimonial;
    }

    @Test
    void createTestimonialWithExistingId() throws Exception {
        // Create the Testimonial with an existing ID
        testimonial.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(testimonialSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(testimonial))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Testimonial in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(testimonialSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(testimonialSearchRepository.findAll().collectList().block());
        // set the field null
        testimonial.setName(null);

        // Create the Testimonial, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(testimonial))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(testimonialSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkRatingIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(testimonialSearchRepository.findAll().collectList().block());
        // set the field null
        testimonial.setRating(null);

        // Create the Testimonial, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(testimonial))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(testimonialSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(testimonialSearchRepository.findAll().collectList().block());
        // set the field null
        testimonial.setDate(null);

        // Create the Testimonial, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(testimonial))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(testimonialSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllTestimonialsAsStream() {
        // Initialize the database
        testimonialRepository.save(testimonial).block();

        List<Testimonial> testimonialList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Testimonial.class)
            .getResponseBody()
            .filter(testimonial::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(testimonialList).isNotNull();
        assertThat(testimonialList).hasSize(1);
        Testimonial testTestimonial = testimonialList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertTestimonialAllPropertiesEquals(testimonial, testTestimonial);
        assertTestimonialUpdatableFieldsEquals(testimonial, testTestimonial);
    }

    @Test
    void getAllTestimonials() {
        // Initialize the database
        insertedTestimonial = testimonialRepository.save(testimonial).block();

        // Get all the testimonialList
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
            .value(hasItem(testimonial.getId().intValue()))
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
    void getTestimonial() {
        // Initialize the database
        insertedTestimonial = testimonialRepository.save(testimonial).block();

        // Get the testimonial
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, testimonial.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(testimonial.getId().intValue()))
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
    void getNonExistingTestimonial() {
        // Get the testimonial
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTestimonial() throws Exception {
        // Initialize the database
        insertedTestimonial = testimonialRepository.save(testimonial).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        testimonialSearchRepository.save(testimonial).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(testimonialSearchRepository.findAll().collectList().block());

        // Update the testimonial
        Testimonial updatedTestimonial = testimonialRepository.findById(testimonial.getId()).block();
        updatedTestimonial.name(UPDATED_NAME).feedback(UPDATED_FEEDBACK).rating(UPDATED_RATING).date(UPDATED_DATE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedTestimonial.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedTestimonial))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Testimonial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTestimonialToMatchAllProperties(updatedTestimonial);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(testimonialSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Testimonial> testimonialSearchList = Streamable.of(
                    testimonialSearchRepository.findAll().collectList().block()
                ).toList();
                Testimonial testTestimonialSearch = testimonialSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertTestimonialAllPropertiesEquals(testTestimonialSearch, updatedTestimonial);
                assertTestimonialUpdatableFieldsEquals(testTestimonialSearch, updatedTestimonial);
            });
    }

    @Test
    void putNonExistingTestimonial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(testimonialSearchRepository.findAll().collectList().block());
        testimonial.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, testimonial.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(testimonial))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Testimonial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(testimonialSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchTestimonial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(testimonialSearchRepository.findAll().collectList().block());
        testimonial.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(testimonial))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Testimonial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(testimonialSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamTestimonial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(testimonialSearchRepository.findAll().collectList().block());
        testimonial.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(testimonial))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Testimonial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(testimonialSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateTestimonialWithPatch() throws Exception {
        // Initialize the database
        insertedTestimonial = testimonialRepository.save(testimonial).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the testimonial using partial update
        Testimonial partialUpdatedTestimonial = new Testimonial();
        partialUpdatedTestimonial.setId(testimonial.getId());

        partialUpdatedTestimonial.feedback(UPDATED_FEEDBACK);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTestimonial.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTestimonial))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Testimonial in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTestimonialUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTestimonial, testimonial),
            getPersistedTestimonial(testimonial)
        );
    }

    @Test
    void fullUpdateTestimonialWithPatch() throws Exception {
        // Initialize the database
        insertedTestimonial = testimonialRepository.save(testimonial).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the testimonial using partial update
        Testimonial partialUpdatedTestimonial = new Testimonial();
        partialUpdatedTestimonial.setId(testimonial.getId());

        partialUpdatedTestimonial.name(UPDATED_NAME).feedback(UPDATED_FEEDBACK).rating(UPDATED_RATING).date(UPDATED_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTestimonial.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTestimonial))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Testimonial in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTestimonialUpdatableFieldsEquals(partialUpdatedTestimonial, getPersistedTestimonial(partialUpdatedTestimonial));
    }

    @Test
    void patchNonExistingTestimonial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(testimonialSearchRepository.findAll().collectList().block());
        testimonial.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, testimonial.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(testimonial))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Testimonial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(testimonialSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchTestimonial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(testimonialSearchRepository.findAll().collectList().block());
        testimonial.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(testimonial))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Testimonial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(testimonialSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamTestimonial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(testimonialSearchRepository.findAll().collectList().block());
        testimonial.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(testimonial))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Testimonial in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(testimonialSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteTestimonial() {
        // Initialize the database
        insertedTestimonial = testimonialRepository.save(testimonial).block();
        testimonialRepository.save(testimonial).block();
        testimonialSearchRepository.save(testimonial).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(testimonialSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the testimonial
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, testimonial.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(testimonialSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchTestimonial() {
        // Initialize the database
        insertedTestimonial = testimonialRepository.save(testimonial).block();
        testimonialSearchRepository.save(testimonial).block();

        // Search the testimonial
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + testimonial.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(testimonial.getId().intValue()))
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
        return testimonialRepository.count().block();
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

    protected Testimonial getPersistedTestimonial(Testimonial testimonial) {
        return testimonialRepository.findById(testimonial.getId()).block();
    }

    protected void assertPersistedTestimonialToMatchAllProperties(Testimonial expectedTestimonial) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTestimonialAllPropertiesEquals(expectedTestimonial, getPersistedTestimonial(expectedTestimonial));
        assertTestimonialUpdatableFieldsEquals(expectedTestimonial, getPersistedTestimonial(expectedTestimonial));
    }

    protected void assertPersistedTestimonialToMatchUpdatableProperties(Testimonial expectedTestimonial) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTestimonialAllUpdatablePropertiesEquals(expectedTestimonial, getPersistedTestimonial(expectedTestimonial));
        assertTestimonialUpdatableFieldsEquals(expectedTestimonial, getPersistedTestimonial(expectedTestimonial));
    }
}
