package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.StockAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.MstProduct;
import com.mycompany.myapp.domain.Stock;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MstProductRepository;
import com.mycompany.myapp.repository.StockRepository;
import com.mycompany.myapp.repository.search.StockSearchRepository;
import com.mycompany.myapp.service.dto.StockDTO;
import com.mycompany.myapp.service.mapper.StockMapper;
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
 * Integration tests for the {@link StockResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class StockResourceIT {

    private static final Integer DEFAULT_QUANTITY_AVAILABLE = 1;
    private static final Integer UPDATED_QUANTITY_AVAILABLE = 2;
    private static final Integer SMALLER_QUANTITY_AVAILABLE = 1 - 1;

    private static final Integer DEFAULT_REORDER_LEVEL = 1;
    private static final Integer UPDATED_REORDER_LEVEL = 2;
    private static final Integer SMALLER_REORDER_LEVEL = 1 - 1;

    private static final Instant DEFAULT_EXPIRY_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPIRY_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/stocks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/stocks/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private StockSearchRepository stockSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Stock stock;

    private Stock insertedStock;

    @Autowired
    private MstProductRepository mstProductRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Stock createEntity(EntityManager em) {
        Stock stock = new Stock()
            .quantityAvailable(DEFAULT_QUANTITY_AVAILABLE)
            .reorderLevel(DEFAULT_REORDER_LEVEL)
            .expiryDate(DEFAULT_EXPIRY_DATE);
        return stock;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Stock createUpdatedEntity(EntityManager em) {
        Stock stock = new Stock()
            .quantityAvailable(UPDATED_QUANTITY_AVAILABLE)
            .reorderLevel(UPDATED_REORDER_LEVEL)
            .expiryDate(UPDATED_EXPIRY_DATE);
        return stock;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Stock.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        stock = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedStock != null) {
            stockRepository.delete(insertedStock).block();
            stockSearchRepository.delete(insertedStock).block();
            insertedStock = null;
        }
        deleteEntities(em);
    }

    @Test
    void createStock() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockSearchRepository.findAll().collectList().block());
        // Create the Stock
        StockDTO stockDTO = stockMapper.toDto(stock);
        var returnedStockDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(stockDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(StockDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Stock in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStock = stockMapper.toEntity(returnedStockDTO);
        assertStockUpdatableFieldsEquals(returnedStock, getPersistedStock(returnedStock));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedStock = returnedStock;
    }

    @Test
    void createStockWithExistingId() throws Exception {
        // Create the Stock with an existing ID
        stock.setId(1L);
        StockDTO stockDTO = stockMapper.toDto(stock);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(stockDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Stock in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkQuantityAvailableIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockSearchRepository.findAll().collectList().block());
        // set the field null
        stock.setQuantityAvailable(null);

        // Create the Stock, which fails.
        StockDTO stockDTO = stockMapper.toDto(stock);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(stockDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllStocks() {
        // Initialize the database
        insertedStock = stockRepository.save(stock).block();

        // Get all the stockList
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
            .value(hasItem(stock.getId().intValue()))
            .jsonPath("$.[*].quantityAvailable")
            .value(hasItem(DEFAULT_QUANTITY_AVAILABLE))
            .jsonPath("$.[*].reorderLevel")
            .value(hasItem(DEFAULT_REORDER_LEVEL))
            .jsonPath("$.[*].expiryDate")
            .value(hasItem(DEFAULT_EXPIRY_DATE.toString()));
    }

    @Test
    void getStock() {
        // Initialize the database
        insertedStock = stockRepository.save(stock).block();

        // Get the stock
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, stock.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(stock.getId().intValue()))
            .jsonPath("$.quantityAvailable")
            .value(is(DEFAULT_QUANTITY_AVAILABLE))
            .jsonPath("$.reorderLevel")
            .value(is(DEFAULT_REORDER_LEVEL))
            .jsonPath("$.expiryDate")
            .value(is(DEFAULT_EXPIRY_DATE.toString()));
    }

    @Test
    void getStocksByIdFiltering() {
        // Initialize the database
        insertedStock = stockRepository.save(stock).block();

        Long id = stock.getId();

        defaultStockFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultStockFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultStockFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllStocksByQuantityAvailableIsEqualToSomething() {
        // Initialize the database
        insertedStock = stockRepository.save(stock).block();

        // Get all the stockList where quantityAvailable equals to
        defaultStockFiltering(
            "quantityAvailable.equals=" + DEFAULT_QUANTITY_AVAILABLE,
            "quantityAvailable.equals=" + UPDATED_QUANTITY_AVAILABLE
        );
    }

    @Test
    void getAllStocksByQuantityAvailableIsInShouldWork() {
        // Initialize the database
        insertedStock = stockRepository.save(stock).block();

        // Get all the stockList where quantityAvailable in
        defaultStockFiltering(
            "quantityAvailable.in=" + DEFAULT_QUANTITY_AVAILABLE + "," + UPDATED_QUANTITY_AVAILABLE,
            "quantityAvailable.in=" + UPDATED_QUANTITY_AVAILABLE
        );
    }

    @Test
    void getAllStocksByQuantityAvailableIsNullOrNotNull() {
        // Initialize the database
        insertedStock = stockRepository.save(stock).block();

        // Get all the stockList where quantityAvailable is not null
        defaultStockFiltering("quantityAvailable.specified=true", "quantityAvailable.specified=false");
    }

    @Test
    void getAllStocksByQuantityAvailableIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedStock = stockRepository.save(stock).block();

        // Get all the stockList where quantityAvailable is greater than or equal to
        defaultStockFiltering(
            "quantityAvailable.greaterThanOrEqual=" + DEFAULT_QUANTITY_AVAILABLE,
            "quantityAvailable.greaterThanOrEqual=" + UPDATED_QUANTITY_AVAILABLE
        );
    }

    @Test
    void getAllStocksByQuantityAvailableIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedStock = stockRepository.save(stock).block();

        // Get all the stockList where quantityAvailable is less than or equal to
        defaultStockFiltering(
            "quantityAvailable.lessThanOrEqual=" + DEFAULT_QUANTITY_AVAILABLE,
            "quantityAvailable.lessThanOrEqual=" + SMALLER_QUANTITY_AVAILABLE
        );
    }

    @Test
    void getAllStocksByQuantityAvailableIsLessThanSomething() {
        // Initialize the database
        insertedStock = stockRepository.save(stock).block();

        // Get all the stockList where quantityAvailable is less than
        defaultStockFiltering(
            "quantityAvailable.lessThan=" + UPDATED_QUANTITY_AVAILABLE,
            "quantityAvailable.lessThan=" + DEFAULT_QUANTITY_AVAILABLE
        );
    }

    @Test
    void getAllStocksByQuantityAvailableIsGreaterThanSomething() {
        // Initialize the database
        insertedStock = stockRepository.save(stock).block();

        // Get all the stockList where quantityAvailable is greater than
        defaultStockFiltering(
            "quantityAvailable.greaterThan=" + SMALLER_QUANTITY_AVAILABLE,
            "quantityAvailable.greaterThan=" + DEFAULT_QUANTITY_AVAILABLE
        );
    }

    @Test
    void getAllStocksByReorderLevelIsEqualToSomething() {
        // Initialize the database
        insertedStock = stockRepository.save(stock).block();

        // Get all the stockList where reorderLevel equals to
        defaultStockFiltering("reorderLevel.equals=" + DEFAULT_REORDER_LEVEL, "reorderLevel.equals=" + UPDATED_REORDER_LEVEL);
    }

    @Test
    void getAllStocksByReorderLevelIsInShouldWork() {
        // Initialize the database
        insertedStock = stockRepository.save(stock).block();

        // Get all the stockList where reorderLevel in
        defaultStockFiltering(
            "reorderLevel.in=" + DEFAULT_REORDER_LEVEL + "," + UPDATED_REORDER_LEVEL,
            "reorderLevel.in=" + UPDATED_REORDER_LEVEL
        );
    }

    @Test
    void getAllStocksByReorderLevelIsNullOrNotNull() {
        // Initialize the database
        insertedStock = stockRepository.save(stock).block();

        // Get all the stockList where reorderLevel is not null
        defaultStockFiltering("reorderLevel.specified=true", "reorderLevel.specified=false");
    }

    @Test
    void getAllStocksByReorderLevelIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedStock = stockRepository.save(stock).block();

        // Get all the stockList where reorderLevel is greater than or equal to
        defaultStockFiltering(
            "reorderLevel.greaterThanOrEqual=" + DEFAULT_REORDER_LEVEL,
            "reorderLevel.greaterThanOrEqual=" + UPDATED_REORDER_LEVEL
        );
    }

    @Test
    void getAllStocksByReorderLevelIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedStock = stockRepository.save(stock).block();

        // Get all the stockList where reorderLevel is less than or equal to
        defaultStockFiltering(
            "reorderLevel.lessThanOrEqual=" + DEFAULT_REORDER_LEVEL,
            "reorderLevel.lessThanOrEqual=" + SMALLER_REORDER_LEVEL
        );
    }

    @Test
    void getAllStocksByReorderLevelIsLessThanSomething() {
        // Initialize the database
        insertedStock = stockRepository.save(stock).block();

        // Get all the stockList where reorderLevel is less than
        defaultStockFiltering("reorderLevel.lessThan=" + UPDATED_REORDER_LEVEL, "reorderLevel.lessThan=" + DEFAULT_REORDER_LEVEL);
    }

    @Test
    void getAllStocksByReorderLevelIsGreaterThanSomething() {
        // Initialize the database
        insertedStock = stockRepository.save(stock).block();

        // Get all the stockList where reorderLevel is greater than
        defaultStockFiltering("reorderLevel.greaterThan=" + SMALLER_REORDER_LEVEL, "reorderLevel.greaterThan=" + DEFAULT_REORDER_LEVEL);
    }

    @Test
    void getAllStocksByExpiryDateIsEqualToSomething() {
        // Initialize the database
        insertedStock = stockRepository.save(stock).block();

        // Get all the stockList where expiryDate equals to
        defaultStockFiltering("expiryDate.equals=" + DEFAULT_EXPIRY_DATE, "expiryDate.equals=" + UPDATED_EXPIRY_DATE);
    }

    @Test
    void getAllStocksByExpiryDateIsInShouldWork() {
        // Initialize the database
        insertedStock = stockRepository.save(stock).block();

        // Get all the stockList where expiryDate in
        defaultStockFiltering("expiryDate.in=" + DEFAULT_EXPIRY_DATE + "," + UPDATED_EXPIRY_DATE, "expiryDate.in=" + UPDATED_EXPIRY_DATE);
    }

    @Test
    void getAllStocksByExpiryDateIsNullOrNotNull() {
        // Initialize the database
        insertedStock = stockRepository.save(stock).block();

        // Get all the stockList where expiryDate is not null
        defaultStockFiltering("expiryDate.specified=true", "expiryDate.specified=false");
    }

    @Test
    void getAllStocksByProductIsEqualToSomething() {
        MstProduct product = MstProductResourceIT.createEntity(em);
        mstProductRepository.save(product).block();
        Long productId = product.getId();
        stock.setProductId(productId);
        insertedStock = stockRepository.save(stock).block();
        // Get all the stockList where product equals to productId
        defaultStockShouldBeFound("productId.equals=" + productId);

        // Get all the stockList where product equals to (productId + 1)
        defaultStockShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    private void defaultStockFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultStockShouldBeFound(shouldBeFound);
        defaultStockShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStockShouldBeFound(String filter) {
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
            .value(hasItem(stock.getId().intValue()))
            .jsonPath("$.[*].quantityAvailable")
            .value(hasItem(DEFAULT_QUANTITY_AVAILABLE))
            .jsonPath("$.[*].reorderLevel")
            .value(hasItem(DEFAULT_REORDER_LEVEL))
            .jsonPath("$.[*].expiryDate")
            .value(hasItem(DEFAULT_EXPIRY_DATE.toString()));

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
    private void defaultStockShouldNotBeFound(String filter) {
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
    void getNonExistingStock() {
        // Get the stock
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingStock() throws Exception {
        // Initialize the database
        insertedStock = stockRepository.save(stock).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockSearchRepository.save(stock).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockSearchRepository.findAll().collectList().block());

        // Update the stock
        Stock updatedStock = stockRepository.findById(stock.getId()).block();
        updatedStock.quantityAvailable(UPDATED_QUANTITY_AVAILABLE).reorderLevel(UPDATED_REORDER_LEVEL).expiryDate(UPDATED_EXPIRY_DATE);
        StockDTO stockDTO = stockMapper.toDto(updatedStock);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, stockDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(stockDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Stock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStockToMatchAllProperties(updatedStock);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Stock> stockSearchList = Streamable.of(stockSearchRepository.findAll().collectList().block()).toList();
                Stock testStockSearch = stockSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertStockAllPropertiesEquals(testStockSearch, updatedStock);
                assertStockUpdatableFieldsEquals(testStockSearch, updatedStock);
            });
    }

    @Test
    void putNonExistingStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockSearchRepository.findAll().collectList().block());
        stock.setId(longCount.incrementAndGet());

        // Create the Stock
        StockDTO stockDTO = stockMapper.toDto(stock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, stockDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(stockDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Stock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockSearchRepository.findAll().collectList().block());
        stock.setId(longCount.incrementAndGet());

        // Create the Stock
        StockDTO stockDTO = stockMapper.toDto(stock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(stockDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Stock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockSearchRepository.findAll().collectList().block());
        stock.setId(longCount.incrementAndGet());

        // Create the Stock
        StockDTO stockDTO = stockMapper.toDto(stock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(stockDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Stock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateStockWithPatch() throws Exception {
        // Initialize the database
        insertedStock = stockRepository.save(stock).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stock using partial update
        Stock partialUpdatedStock = new Stock();
        partialUpdatedStock.setId(stock.getId());

        partialUpdatedStock.reorderLevel(UPDATED_REORDER_LEVEL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedStock.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedStock))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Stock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStockUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedStock, stock), getPersistedStock(stock));
    }

    @Test
    void fullUpdateStockWithPatch() throws Exception {
        // Initialize the database
        insertedStock = stockRepository.save(stock).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stock using partial update
        Stock partialUpdatedStock = new Stock();
        partialUpdatedStock.setId(stock.getId());

        partialUpdatedStock
            .quantityAvailable(UPDATED_QUANTITY_AVAILABLE)
            .reorderLevel(UPDATED_REORDER_LEVEL)
            .expiryDate(UPDATED_EXPIRY_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedStock.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedStock))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Stock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStockUpdatableFieldsEquals(partialUpdatedStock, getPersistedStock(partialUpdatedStock));
    }

    @Test
    void patchNonExistingStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockSearchRepository.findAll().collectList().block());
        stock.setId(longCount.incrementAndGet());

        // Create the Stock
        StockDTO stockDTO = stockMapper.toDto(stock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, stockDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(stockDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Stock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockSearchRepository.findAll().collectList().block());
        stock.setId(longCount.incrementAndGet());

        // Create the Stock
        StockDTO stockDTO = stockMapper.toDto(stock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(stockDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Stock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockSearchRepository.findAll().collectList().block());
        stock.setId(longCount.incrementAndGet());

        // Create the Stock
        StockDTO stockDTO = stockMapper.toDto(stock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(stockDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Stock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteStock() {
        // Initialize the database
        insertedStock = stockRepository.save(stock).block();
        stockRepository.save(stock).block();
        stockSearchRepository.save(stock).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stockSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the stock
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, stock.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stockSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchStock() {
        // Initialize the database
        insertedStock = stockRepository.save(stock).block();
        stockSearchRepository.save(stock).block();

        // Search the stock
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + stock.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(stock.getId().intValue()))
            .jsonPath("$.[*].quantityAvailable")
            .value(hasItem(DEFAULT_QUANTITY_AVAILABLE))
            .jsonPath("$.[*].reorderLevel")
            .value(hasItem(DEFAULT_REORDER_LEVEL))
            .jsonPath("$.[*].expiryDate")
            .value(hasItem(DEFAULT_EXPIRY_DATE.toString()));
    }

    protected long getRepositoryCount() {
        return stockRepository.count().block();
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

    protected Stock getPersistedStock(Stock stock) {
        return stockRepository.findById(stock.getId()).block();
    }

    protected void assertPersistedStockToMatchAllProperties(Stock expectedStock) {
        // Test fails because reactive api returns an empty object instead of null
        // assertStockAllPropertiesEquals(expectedStock, getPersistedStock(expectedStock));
        assertStockUpdatableFieldsEquals(expectedStock, getPersistedStock(expectedStock));
    }

    protected void assertPersistedStockToMatchUpdatableProperties(Stock expectedStock) {
        // Test fails because reactive api returns an empty object instead of null
        // assertStockAllUpdatablePropertiesEquals(expectedStock, getPersistedStock(expectedStock));
        assertStockUpdatableFieldsEquals(expectedStock, getPersistedStock(expectedStock));
    }
}
