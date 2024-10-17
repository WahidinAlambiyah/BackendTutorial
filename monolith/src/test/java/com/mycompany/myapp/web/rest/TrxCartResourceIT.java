package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.TrxCartAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.MstCustomer;
import com.mycompany.myapp.domain.TrxCart;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MstCustomerRepository;
import com.mycompany.myapp.repository.TrxCartRepository;
import com.mycompany.myapp.repository.search.TrxCartSearchRepository;
import com.mycompany.myapp.service.dto.TrxCartDTO;
import com.mycompany.myapp.service.mapper.TrxCartMapper;
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
 * Integration tests for the {@link TrxCartResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TrxCartResourceIT {

    private static final BigDecimal DEFAULT_TOTAL_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_PRICE = new BigDecimal(2);
    private static final BigDecimal SMALLER_TOTAL_PRICE = new BigDecimal(1 - 1);

    private static final String ENTITY_API_URL = "/api/trx-carts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/trx-carts/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TrxCartRepository trxCartRepository;

    @Autowired
    private TrxCartMapper trxCartMapper;

    @Autowired
    private TrxCartSearchRepository trxCartSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TrxCart trxCart;

    private TrxCart insertedTrxCart;

    @Autowired
    private MstCustomerRepository mstCustomerRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrxCart createEntity(EntityManager em) {
        TrxCart trxCart = new TrxCart().totalPrice(DEFAULT_TOTAL_PRICE);
        return trxCart;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrxCart createUpdatedEntity(EntityManager em) {
        TrxCart trxCart = new TrxCart().totalPrice(UPDATED_TOTAL_PRICE);
        return trxCart;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TrxCart.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        trxCart = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedTrxCart != null) {
            trxCartRepository.delete(insertedTrxCart).block();
            trxCartSearchRepository.delete(insertedTrxCart).block();
            insertedTrxCart = null;
        }
        deleteEntities(em);
    }

    @Test
    void createTrxCart() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxCartSearchRepository.findAll().collectList().block());
        // Create the TrxCart
        TrxCartDTO trxCartDTO = trxCartMapper.toDto(trxCart);
        var returnedTrxCartDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxCartDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TrxCartDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the TrxCart in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTrxCart = trxCartMapper.toEntity(returnedTrxCartDTO);
        assertTrxCartUpdatableFieldsEquals(returnedTrxCart, getPersistedTrxCart(returnedTrxCart));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxCartSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedTrxCart = returnedTrxCart;
    }

    @Test
    void createTrxCartWithExistingId() throws Exception {
        // Create the TrxCart with an existing ID
        trxCart.setId(1L);
        TrxCartDTO trxCartDTO = trxCartMapper.toDto(trxCart);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxCartSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxCartDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxCart in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxCartSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkTotalPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxCartSearchRepository.findAll().collectList().block());
        // set the field null
        trxCart.setTotalPrice(null);

        // Create the TrxCart, which fails.
        TrxCartDTO trxCartDTO = trxCartMapper.toDto(trxCart);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxCartDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxCartSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllTrxCarts() {
        // Initialize the database
        insertedTrxCart = trxCartRepository.save(trxCart).block();

        // Get all the trxCartList
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
            .value(hasItem(trxCart.getId().intValue()))
            .jsonPath("$.[*].totalPrice")
            .value(hasItem(sameNumber(DEFAULT_TOTAL_PRICE)));
    }

    @Test
    void getTrxCart() {
        // Initialize the database
        insertedTrxCart = trxCartRepository.save(trxCart).block();

        // Get the trxCart
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, trxCart.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(trxCart.getId().intValue()))
            .jsonPath("$.totalPrice")
            .value(is(sameNumber(DEFAULT_TOTAL_PRICE)));
    }

    @Test
    void getTrxCartsByIdFiltering() {
        // Initialize the database
        insertedTrxCart = trxCartRepository.save(trxCart).block();

        Long id = trxCart.getId();

        defaultTrxCartFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTrxCartFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTrxCartFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllTrxCartsByTotalPriceIsEqualToSomething() {
        // Initialize the database
        insertedTrxCart = trxCartRepository.save(trxCart).block();

        // Get all the trxCartList where totalPrice equals to
        defaultTrxCartFiltering("totalPrice.equals=" + DEFAULT_TOTAL_PRICE, "totalPrice.equals=" + UPDATED_TOTAL_PRICE);
    }

    @Test
    void getAllTrxCartsByTotalPriceIsInShouldWork() {
        // Initialize the database
        insertedTrxCart = trxCartRepository.save(trxCart).block();

        // Get all the trxCartList where totalPrice in
        defaultTrxCartFiltering("totalPrice.in=" + DEFAULT_TOTAL_PRICE + "," + UPDATED_TOTAL_PRICE, "totalPrice.in=" + UPDATED_TOTAL_PRICE);
    }

    @Test
    void getAllTrxCartsByTotalPriceIsNullOrNotNull() {
        // Initialize the database
        insertedTrxCart = trxCartRepository.save(trxCart).block();

        // Get all the trxCartList where totalPrice is not null
        defaultTrxCartFiltering("totalPrice.specified=true", "totalPrice.specified=false");
    }

    @Test
    void getAllTrxCartsByTotalPriceIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxCart = trxCartRepository.save(trxCart).block();

        // Get all the trxCartList where totalPrice is greater than or equal to
        defaultTrxCartFiltering(
            "totalPrice.greaterThanOrEqual=" + DEFAULT_TOTAL_PRICE,
            "totalPrice.greaterThanOrEqual=" + UPDATED_TOTAL_PRICE
        );
    }

    @Test
    void getAllTrxCartsByTotalPriceIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxCart = trxCartRepository.save(trxCart).block();

        // Get all the trxCartList where totalPrice is less than or equal to
        defaultTrxCartFiltering("totalPrice.lessThanOrEqual=" + DEFAULT_TOTAL_PRICE, "totalPrice.lessThanOrEqual=" + SMALLER_TOTAL_PRICE);
    }

    @Test
    void getAllTrxCartsByTotalPriceIsLessThanSomething() {
        // Initialize the database
        insertedTrxCart = trxCartRepository.save(trxCart).block();

        // Get all the trxCartList where totalPrice is less than
        defaultTrxCartFiltering("totalPrice.lessThan=" + UPDATED_TOTAL_PRICE, "totalPrice.lessThan=" + DEFAULT_TOTAL_PRICE);
    }

    @Test
    void getAllTrxCartsByTotalPriceIsGreaterThanSomething() {
        // Initialize the database
        insertedTrxCart = trxCartRepository.save(trxCart).block();

        // Get all the trxCartList where totalPrice is greater than
        defaultTrxCartFiltering("totalPrice.greaterThan=" + SMALLER_TOTAL_PRICE, "totalPrice.greaterThan=" + DEFAULT_TOTAL_PRICE);
    }

    @Test
    void getAllTrxCartsByCustomerIsEqualToSomething() {
        MstCustomer customer = MstCustomerResourceIT.createEntity(em);
        mstCustomerRepository.save(customer).block();
        Long customerId = customer.getId();
        trxCart.setCustomerId(customerId);
        insertedTrxCart = trxCartRepository.save(trxCart).block();
        // Get all the trxCartList where customer equals to customerId
        defaultTrxCartShouldBeFound("customerId.equals=" + customerId);

        // Get all the trxCartList where customer equals to (customerId + 1)
        defaultTrxCartShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }

    private void defaultTrxCartFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultTrxCartShouldBeFound(shouldBeFound);
        defaultTrxCartShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTrxCartShouldBeFound(String filter) {
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
            .value(hasItem(trxCart.getId().intValue()))
            .jsonPath("$.[*].totalPrice")
            .value(hasItem(sameNumber(DEFAULT_TOTAL_PRICE)));

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
    private void defaultTrxCartShouldNotBeFound(String filter) {
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
    void getNonExistingTrxCart() {
        // Get the trxCart
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTrxCart() throws Exception {
        // Initialize the database
        insertedTrxCart = trxCartRepository.save(trxCart).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        trxCartSearchRepository.save(trxCart).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxCartSearchRepository.findAll().collectList().block());

        // Update the trxCart
        TrxCart updatedTrxCart = trxCartRepository.findById(trxCart.getId()).block();
        updatedTrxCart.totalPrice(UPDATED_TOTAL_PRICE);
        TrxCartDTO trxCartDTO = trxCartMapper.toDto(updatedTrxCart);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trxCartDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxCartDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxCart in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTrxCartToMatchAllProperties(updatedTrxCart);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxCartSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TrxCart> trxCartSearchList = Streamable.of(trxCartSearchRepository.findAll().collectList().block()).toList();
                TrxCart testTrxCartSearch = trxCartSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertTrxCartAllPropertiesEquals(testTrxCartSearch, updatedTrxCart);
                assertTrxCartUpdatableFieldsEquals(testTrxCartSearch, updatedTrxCart);
            });
    }

    @Test
    void putNonExistingTrxCart() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxCartSearchRepository.findAll().collectList().block());
        trxCart.setId(longCount.incrementAndGet());

        // Create the TrxCart
        TrxCartDTO trxCartDTO = trxCartMapper.toDto(trxCart);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trxCartDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxCartDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxCart in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxCartSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchTrxCart() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxCartSearchRepository.findAll().collectList().block());
        trxCart.setId(longCount.incrementAndGet());

        // Create the TrxCart
        TrxCartDTO trxCartDTO = trxCartMapper.toDto(trxCart);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxCartDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxCart in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxCartSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamTrxCart() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxCartSearchRepository.findAll().collectList().block());
        trxCart.setId(longCount.incrementAndGet());

        // Create the TrxCart
        TrxCartDTO trxCartDTO = trxCartMapper.toDto(trxCart);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxCartDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrxCart in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxCartSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateTrxCartWithPatch() throws Exception {
        // Initialize the database
        insertedTrxCart = trxCartRepository.save(trxCart).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trxCart using partial update
        TrxCart partialUpdatedTrxCart = new TrxCart();
        partialUpdatedTrxCart.setId(trxCart.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrxCart.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrxCart))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxCart in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrxCartUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTrxCart, trxCart), getPersistedTrxCart(trxCart));
    }

    @Test
    void fullUpdateTrxCartWithPatch() throws Exception {
        // Initialize the database
        insertedTrxCart = trxCartRepository.save(trxCart).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trxCart using partial update
        TrxCart partialUpdatedTrxCart = new TrxCart();
        partialUpdatedTrxCart.setId(trxCart.getId());

        partialUpdatedTrxCart.totalPrice(UPDATED_TOTAL_PRICE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrxCart.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrxCart))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxCart in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrxCartUpdatableFieldsEquals(partialUpdatedTrxCart, getPersistedTrxCart(partialUpdatedTrxCart));
    }

    @Test
    void patchNonExistingTrxCart() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxCartSearchRepository.findAll().collectList().block());
        trxCart.setId(longCount.incrementAndGet());

        // Create the TrxCart
        TrxCartDTO trxCartDTO = trxCartMapper.toDto(trxCart);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, trxCartDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxCartDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxCart in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxCartSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchTrxCart() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxCartSearchRepository.findAll().collectList().block());
        trxCart.setId(longCount.incrementAndGet());

        // Create the TrxCart
        TrxCartDTO trxCartDTO = trxCartMapper.toDto(trxCart);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxCartDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxCart in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxCartSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamTrxCart() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxCartSearchRepository.findAll().collectList().block());
        trxCart.setId(longCount.incrementAndGet());

        // Create the TrxCart
        TrxCartDTO trxCartDTO = trxCartMapper.toDto(trxCart);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxCartDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrxCart in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxCartSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteTrxCart() {
        // Initialize the database
        insertedTrxCart = trxCartRepository.save(trxCart).block();
        trxCartRepository.save(trxCart).block();
        trxCartSearchRepository.save(trxCart).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxCartSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the trxCart
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, trxCart.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxCartSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchTrxCart() {
        // Initialize the database
        insertedTrxCart = trxCartRepository.save(trxCart).block();
        trxCartSearchRepository.save(trxCart).block();

        // Search the trxCart
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + trxCart.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(trxCart.getId().intValue()))
            .jsonPath("$.[*].totalPrice")
            .value(hasItem(sameNumber(DEFAULT_TOTAL_PRICE)));
    }

    protected long getRepositoryCount() {
        return trxCartRepository.count().block();
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

    protected TrxCart getPersistedTrxCart(TrxCart trxCart) {
        return trxCartRepository.findById(trxCart.getId()).block();
    }

    protected void assertPersistedTrxCartToMatchAllProperties(TrxCart expectedTrxCart) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrxCartAllPropertiesEquals(expectedTrxCart, getPersistedTrxCart(expectedTrxCart));
        assertTrxCartUpdatableFieldsEquals(expectedTrxCart, getPersistedTrxCart(expectedTrxCart));
    }

    protected void assertPersistedTrxCartToMatchUpdatableProperties(TrxCart expectedTrxCart) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrxCartAllUpdatablePropertiesEquals(expectedTrxCart, getPersistedTrxCart(expectedTrxCart));
        assertTrxCartUpdatableFieldsEquals(expectedTrxCart, getPersistedTrxCart(expectedTrxCart));
    }
}
