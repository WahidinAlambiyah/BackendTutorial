package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.TrxOrderItemAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.MstProduct;
import com.mycompany.myapp.domain.TrxOrder;
import com.mycompany.myapp.domain.TrxOrderItem;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MstProductRepository;
import com.mycompany.myapp.repository.TrxOrderItemRepository;
import com.mycompany.myapp.repository.TrxOrderRepository;
import com.mycompany.myapp.repository.search.TrxOrderItemSearchRepository;
import com.mycompany.myapp.service.dto.TrxOrderItemDTO;
import com.mycompany.myapp.service.mapper.TrxOrderItemMapper;
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
 * Integration tests for the {@link TrxOrderItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TrxOrderItemResourceIT {

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;
    private static final Integer SMALLER_QUANTITY = 1 - 1;

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);
    private static final BigDecimal SMALLER_PRICE = new BigDecimal(1 - 1);

    private static final String ENTITY_API_URL = "/api/trx-order-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/trx-order-items/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TrxOrderItemRepository trxOrderItemRepository;

    @Autowired
    private TrxOrderItemMapper trxOrderItemMapper;

    @Autowired
    private TrxOrderItemSearchRepository trxOrderItemSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TrxOrderItem trxOrderItem;

    private TrxOrderItem insertedTrxOrderItem;

    @Autowired
    private TrxOrderRepository trxOrderRepository;

    @Autowired
    private MstProductRepository mstProductRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrxOrderItem createEntity(EntityManager em) {
        TrxOrderItem trxOrderItem = new TrxOrderItem().quantity(DEFAULT_QUANTITY).price(DEFAULT_PRICE);
        return trxOrderItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrxOrderItem createUpdatedEntity(EntityManager em) {
        TrxOrderItem trxOrderItem = new TrxOrderItem().quantity(UPDATED_QUANTITY).price(UPDATED_PRICE);
        return trxOrderItem;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TrxOrderItem.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        trxOrderItem = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedTrxOrderItem != null) {
            trxOrderItemRepository.delete(insertedTrxOrderItem).block();
            trxOrderItemSearchRepository.delete(insertedTrxOrderItem).block();
            insertedTrxOrderItem = null;
        }
        deleteEntities(em);
    }

    @Test
    void createTrxOrderItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderItemSearchRepository.findAll().collectList().block());
        // Create the TrxOrderItem
        TrxOrderItemDTO trxOrderItemDTO = trxOrderItemMapper.toDto(trxOrderItem);
        var returnedTrxOrderItemDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderItemDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TrxOrderItemDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the TrxOrderItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTrxOrderItem = trxOrderItemMapper.toEntity(returnedTrxOrderItemDTO);
        assertTrxOrderItemUpdatableFieldsEquals(returnedTrxOrderItem, getPersistedTrxOrderItem(returnedTrxOrderItem));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderItemSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedTrxOrderItem = returnedTrxOrderItem;
    }

    @Test
    void createTrxOrderItemWithExistingId() throws Exception {
        // Create the TrxOrderItem with an existing ID
        trxOrderItem.setId(1L);
        TrxOrderItemDTO trxOrderItemDTO = trxOrderItemMapper.toDto(trxOrderItem);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderItemSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderItemSearchRepository.findAll().collectList().block());
        // set the field null
        trxOrderItem.setQuantity(null);

        // Create the TrxOrderItem, which fails.
        TrxOrderItemDTO trxOrderItemDTO = trxOrderItemMapper.toDto(trxOrderItem);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderItemSearchRepository.findAll().collectList().block());
        // set the field null
        trxOrderItem.setPrice(null);

        // Create the TrxOrderItem, which fails.
        TrxOrderItemDTO trxOrderItemDTO = trxOrderItemMapper.toDto(trxOrderItem);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllTrxOrderItems() {
        // Initialize the database
        insertedTrxOrderItem = trxOrderItemRepository.save(trxOrderItem).block();

        // Get all the trxOrderItemList
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
            .value(hasItem(trxOrderItem.getId().intValue()))
            .jsonPath("$.[*].quantity")
            .value(hasItem(DEFAULT_QUANTITY))
            .jsonPath("$.[*].price")
            .value(hasItem(sameNumber(DEFAULT_PRICE)));
    }

    @Test
    void getTrxOrderItem() {
        // Initialize the database
        insertedTrxOrderItem = trxOrderItemRepository.save(trxOrderItem).block();

        // Get the trxOrderItem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, trxOrderItem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(trxOrderItem.getId().intValue()))
            .jsonPath("$.quantity")
            .value(is(DEFAULT_QUANTITY))
            .jsonPath("$.price")
            .value(is(sameNumber(DEFAULT_PRICE)));
    }

    @Test
    void getTrxOrderItemsByIdFiltering() {
        // Initialize the database
        insertedTrxOrderItem = trxOrderItemRepository.save(trxOrderItem).block();

        Long id = trxOrderItem.getId();

        defaultTrxOrderItemFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTrxOrderItemFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTrxOrderItemFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllTrxOrderItemsByQuantityIsEqualToSomething() {
        // Initialize the database
        insertedTrxOrderItem = trxOrderItemRepository.save(trxOrderItem).block();

        // Get all the trxOrderItemList where quantity equals to
        defaultTrxOrderItemFiltering("quantity.equals=" + DEFAULT_QUANTITY, "quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    void getAllTrxOrderItemsByQuantityIsInShouldWork() {
        // Initialize the database
        insertedTrxOrderItem = trxOrderItemRepository.save(trxOrderItem).block();

        // Get all the trxOrderItemList where quantity in
        defaultTrxOrderItemFiltering("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY, "quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    void getAllTrxOrderItemsByQuantityIsNullOrNotNull() {
        // Initialize the database
        insertedTrxOrderItem = trxOrderItemRepository.save(trxOrderItem).block();

        // Get all the trxOrderItemList where quantity is not null
        defaultTrxOrderItemFiltering("quantity.specified=true", "quantity.specified=false");
    }

    @Test
    void getAllTrxOrderItemsByQuantityIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxOrderItem = trxOrderItemRepository.save(trxOrderItem).block();

        // Get all the trxOrderItemList where quantity is greater than or equal to
        defaultTrxOrderItemFiltering("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY, "quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    void getAllTrxOrderItemsByQuantityIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxOrderItem = trxOrderItemRepository.save(trxOrderItem).block();

        // Get all the trxOrderItemList where quantity is less than or equal to
        defaultTrxOrderItemFiltering("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY, "quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    void getAllTrxOrderItemsByQuantityIsLessThanSomething() {
        // Initialize the database
        insertedTrxOrderItem = trxOrderItemRepository.save(trxOrderItem).block();

        // Get all the trxOrderItemList where quantity is less than
        defaultTrxOrderItemFiltering("quantity.lessThan=" + UPDATED_QUANTITY, "quantity.lessThan=" + DEFAULT_QUANTITY);
    }

    @Test
    void getAllTrxOrderItemsByQuantityIsGreaterThanSomething() {
        // Initialize the database
        insertedTrxOrderItem = trxOrderItemRepository.save(trxOrderItem).block();

        // Get all the trxOrderItemList where quantity is greater than
        defaultTrxOrderItemFiltering("quantity.greaterThan=" + SMALLER_QUANTITY, "quantity.greaterThan=" + DEFAULT_QUANTITY);
    }

    @Test
    void getAllTrxOrderItemsByPriceIsEqualToSomething() {
        // Initialize the database
        insertedTrxOrderItem = trxOrderItemRepository.save(trxOrderItem).block();

        // Get all the trxOrderItemList where price equals to
        defaultTrxOrderItemFiltering("price.equals=" + DEFAULT_PRICE, "price.equals=" + UPDATED_PRICE);
    }

    @Test
    void getAllTrxOrderItemsByPriceIsInShouldWork() {
        // Initialize the database
        insertedTrxOrderItem = trxOrderItemRepository.save(trxOrderItem).block();

        // Get all the trxOrderItemList where price in
        defaultTrxOrderItemFiltering("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE, "price.in=" + UPDATED_PRICE);
    }

    @Test
    void getAllTrxOrderItemsByPriceIsNullOrNotNull() {
        // Initialize the database
        insertedTrxOrderItem = trxOrderItemRepository.save(trxOrderItem).block();

        // Get all the trxOrderItemList where price is not null
        defaultTrxOrderItemFiltering("price.specified=true", "price.specified=false");
    }

    @Test
    void getAllTrxOrderItemsByPriceIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxOrderItem = trxOrderItemRepository.save(trxOrderItem).block();

        // Get all the trxOrderItemList where price is greater than or equal to
        defaultTrxOrderItemFiltering("price.greaterThanOrEqual=" + DEFAULT_PRICE, "price.greaterThanOrEqual=" + UPDATED_PRICE);
    }

    @Test
    void getAllTrxOrderItemsByPriceIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedTrxOrderItem = trxOrderItemRepository.save(trxOrderItem).block();

        // Get all the trxOrderItemList where price is less than or equal to
        defaultTrxOrderItemFiltering("price.lessThanOrEqual=" + DEFAULT_PRICE, "price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    void getAllTrxOrderItemsByPriceIsLessThanSomething() {
        // Initialize the database
        insertedTrxOrderItem = trxOrderItemRepository.save(trxOrderItem).block();

        // Get all the trxOrderItemList where price is less than
        defaultTrxOrderItemFiltering("price.lessThan=" + UPDATED_PRICE, "price.lessThan=" + DEFAULT_PRICE);
    }

    @Test
    void getAllTrxOrderItemsByPriceIsGreaterThanSomething() {
        // Initialize the database
        insertedTrxOrderItem = trxOrderItemRepository.save(trxOrderItem).block();

        // Get all the trxOrderItemList where price is greater than
        defaultTrxOrderItemFiltering("price.greaterThan=" + SMALLER_PRICE, "price.greaterThan=" + DEFAULT_PRICE);
    }

    @Test
    void getAllTrxOrderItemsByOrderIsEqualToSomething() {
        TrxOrder order = TrxOrderResourceIT.createEntity(em);
        trxOrderRepository.save(order).block();
        Long orderId = order.getId();
        trxOrderItem.setOrderId(orderId);
        insertedTrxOrderItem = trxOrderItemRepository.save(trxOrderItem).block();
        // Get all the trxOrderItemList where order equals to orderId
        defaultTrxOrderItemShouldBeFound("orderId.equals=" + orderId);

        // Get all the trxOrderItemList where order equals to (orderId + 1)
        defaultTrxOrderItemShouldNotBeFound("orderId.equals=" + (orderId + 1));
    }

    @Test
    void getAllTrxOrderItemsByProductIsEqualToSomething() {
        MstProduct product = MstProductResourceIT.createEntity(em);
        mstProductRepository.save(product).block();
        Long productId = product.getId();
        trxOrderItem.setProductId(productId);
        insertedTrxOrderItem = trxOrderItemRepository.save(trxOrderItem).block();
        // Get all the trxOrderItemList where product equals to productId
        defaultTrxOrderItemShouldBeFound("productId.equals=" + productId);

        // Get all the trxOrderItemList where product equals to (productId + 1)
        defaultTrxOrderItemShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    private void defaultTrxOrderItemFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultTrxOrderItemShouldBeFound(shouldBeFound);
        defaultTrxOrderItemShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTrxOrderItemShouldBeFound(String filter) {
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
            .value(hasItem(trxOrderItem.getId().intValue()))
            .jsonPath("$.[*].quantity")
            .value(hasItem(DEFAULT_QUANTITY))
            .jsonPath("$.[*].price")
            .value(hasItem(sameNumber(DEFAULT_PRICE)));

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
    private void defaultTrxOrderItemShouldNotBeFound(String filter) {
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
    void getNonExistingTrxOrderItem() {
        // Get the trxOrderItem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTrxOrderItem() throws Exception {
        // Initialize the database
        insertedTrxOrderItem = trxOrderItemRepository.save(trxOrderItem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        trxOrderItemSearchRepository.save(trxOrderItem).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderItemSearchRepository.findAll().collectList().block());

        // Update the trxOrderItem
        TrxOrderItem updatedTrxOrderItem = trxOrderItemRepository.findById(trxOrderItem.getId()).block();
        updatedTrxOrderItem.quantity(UPDATED_QUANTITY).price(UPDATED_PRICE);
        TrxOrderItemDTO trxOrderItemDTO = trxOrderItemMapper.toDto(updatedTrxOrderItem);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trxOrderItemDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderItemDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTrxOrderItemToMatchAllProperties(updatedTrxOrderItem);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderItemSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TrxOrderItem> trxOrderItemSearchList = Streamable.of(
                    trxOrderItemSearchRepository.findAll().collectList().block()
                ).toList();
                TrxOrderItem testTrxOrderItemSearch = trxOrderItemSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertTrxOrderItemAllPropertiesEquals(testTrxOrderItemSearch, updatedTrxOrderItem);
                assertTrxOrderItemUpdatableFieldsEquals(testTrxOrderItemSearch, updatedTrxOrderItem);
            });
    }

    @Test
    void putNonExistingTrxOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderItemSearchRepository.findAll().collectList().block());
        trxOrderItem.setId(longCount.incrementAndGet());

        // Create the TrxOrderItem
        TrxOrderItemDTO trxOrderItemDTO = trxOrderItemMapper.toDto(trxOrderItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, trxOrderItemDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchTrxOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderItemSearchRepository.findAll().collectList().block());
        trxOrderItem.setId(longCount.incrementAndGet());

        // Create the TrxOrderItem
        TrxOrderItemDTO trxOrderItemDTO = trxOrderItemMapper.toDto(trxOrderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamTrxOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderItemSearchRepository.findAll().collectList().block());
        trxOrderItem.setId(longCount.incrementAndGet());

        // Create the TrxOrderItem
        TrxOrderItemDTO trxOrderItemDTO = trxOrderItemMapper.toDto(trxOrderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(trxOrderItemDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrxOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateTrxOrderItemWithPatch() throws Exception {
        // Initialize the database
        insertedTrxOrderItem = trxOrderItemRepository.save(trxOrderItem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trxOrderItem using partial update
        TrxOrderItem partialUpdatedTrxOrderItem = new TrxOrderItem();
        partialUpdatedTrxOrderItem.setId(trxOrderItem.getId());

        partialUpdatedTrxOrderItem.quantity(UPDATED_QUANTITY).price(UPDATED_PRICE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrxOrderItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrxOrderItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxOrderItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrxOrderItemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTrxOrderItem, trxOrderItem),
            getPersistedTrxOrderItem(trxOrderItem)
        );
    }

    @Test
    void fullUpdateTrxOrderItemWithPatch() throws Exception {
        // Initialize the database
        insertedTrxOrderItem = trxOrderItemRepository.save(trxOrderItem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trxOrderItem using partial update
        TrxOrderItem partialUpdatedTrxOrderItem = new TrxOrderItem();
        partialUpdatedTrxOrderItem.setId(trxOrderItem.getId());

        partialUpdatedTrxOrderItem.quantity(UPDATED_QUANTITY).price(UPDATED_PRICE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrxOrderItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTrxOrderItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TrxOrderItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrxOrderItemUpdatableFieldsEquals(partialUpdatedTrxOrderItem, getPersistedTrxOrderItem(partialUpdatedTrxOrderItem));
    }

    @Test
    void patchNonExistingTrxOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderItemSearchRepository.findAll().collectList().block());
        trxOrderItem.setId(longCount.incrementAndGet());

        // Create the TrxOrderItem
        TrxOrderItemDTO trxOrderItemDTO = trxOrderItemMapper.toDto(trxOrderItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, trxOrderItemDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxOrderItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchTrxOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderItemSearchRepository.findAll().collectList().block());
        trxOrderItem.setId(longCount.incrementAndGet());

        // Create the TrxOrderItem
        TrxOrderItemDTO trxOrderItemDTO = trxOrderItemMapper.toDto(trxOrderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxOrderItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TrxOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamTrxOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderItemSearchRepository.findAll().collectList().block());
        trxOrderItem.setId(longCount.incrementAndGet());

        // Create the TrxOrderItem
        TrxOrderItemDTO trxOrderItemDTO = trxOrderItemMapper.toDto(trxOrderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(trxOrderItemDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TrxOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteTrxOrderItem() {
        // Initialize the database
        insertedTrxOrderItem = trxOrderItemRepository.save(trxOrderItem).block();
        trxOrderItemRepository.save(trxOrderItem).block();
        trxOrderItemSearchRepository.save(trxOrderItem).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(trxOrderItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the trxOrderItem
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, trxOrderItem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(trxOrderItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchTrxOrderItem() {
        // Initialize the database
        insertedTrxOrderItem = trxOrderItemRepository.save(trxOrderItem).block();
        trxOrderItemSearchRepository.save(trxOrderItem).block();

        // Search the trxOrderItem
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + trxOrderItem.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(trxOrderItem.getId().intValue()))
            .jsonPath("$.[*].quantity")
            .value(hasItem(DEFAULT_QUANTITY))
            .jsonPath("$.[*].price")
            .value(hasItem(sameNumber(DEFAULT_PRICE)));
    }

    protected long getRepositoryCount() {
        return trxOrderItemRepository.count().block();
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

    protected TrxOrderItem getPersistedTrxOrderItem(TrxOrderItem trxOrderItem) {
        return trxOrderItemRepository.findById(trxOrderItem.getId()).block();
    }

    protected void assertPersistedTrxOrderItemToMatchAllProperties(TrxOrderItem expectedTrxOrderItem) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrxOrderItemAllPropertiesEquals(expectedTrxOrderItem, getPersistedTrxOrderItem(expectedTrxOrderItem));
        assertTrxOrderItemUpdatableFieldsEquals(expectedTrxOrderItem, getPersistedTrxOrderItem(expectedTrxOrderItem));
    }

    protected void assertPersistedTrxOrderItemToMatchUpdatableProperties(TrxOrderItem expectedTrxOrderItem) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTrxOrderItemAllUpdatablePropertiesEquals(expectedTrxOrderItem, getPersistedTrxOrderItem(expectedTrxOrderItem));
        assertTrxOrderItemUpdatableFieldsEquals(expectedTrxOrderItem, getPersistedTrxOrderItem(expectedTrxOrderItem));
    }
}
