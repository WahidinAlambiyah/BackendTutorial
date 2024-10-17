package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.MstProductAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.MstBrand;
import com.mycompany.myapp.domain.MstCategory;
import com.mycompany.myapp.domain.MstProduct;
import com.mycompany.myapp.domain.MstSupplier;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MstBrandRepository;
import com.mycompany.myapp.repository.MstCategoryRepository;
import com.mycompany.myapp.repository.MstProductRepository;
import com.mycompany.myapp.repository.MstSupplierRepository;
import com.mycompany.myapp.repository.search.MstProductSearchRepository;
import com.mycompany.myapp.service.dto.MstProductDTO;
import com.mycompany.myapp.service.mapper.MstProductMapper;
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
 * Integration tests for the {@link MstProductResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MstProductResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);
    private static final BigDecimal SMALLER_PRICE = new BigDecimal(1 - 1);

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;
    private static final Integer SMALLER_QUANTITY = 1 - 1;

    private static final String DEFAULT_BARCODE = "AAAAAAAAAA";
    private static final String UPDATED_BARCODE = "BBBBBBBBBB";

    private static final String DEFAULT_UNIT_SIZE = "AAAAAAAAAA";
    private static final String UPDATED_UNIT_SIZE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/mst-products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/mst-products/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MstProductRepository mstProductRepository;

    @Autowired
    private MstProductMapper mstProductMapper;

    @Autowired
    private MstProductSearchRepository mstProductSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private MstProduct mstProduct;

    private MstProduct insertedMstProduct;

    @Autowired
    private MstCategoryRepository mstCategoryRepository;

    @Autowired
    private MstBrandRepository mstBrandRepository;

    @Autowired
    private MstSupplierRepository mstSupplierRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstProduct createEntity(EntityManager em) {
        MstProduct mstProduct = new MstProduct()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .price(DEFAULT_PRICE)
            .quantity(DEFAULT_QUANTITY)
            .barcode(DEFAULT_BARCODE)
            .unitSize(DEFAULT_UNIT_SIZE);
        return mstProduct;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstProduct createUpdatedEntity(EntityManager em) {
        MstProduct mstProduct = new MstProduct()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .quantity(UPDATED_QUANTITY)
            .barcode(UPDATED_BARCODE)
            .unitSize(UPDATED_UNIT_SIZE);
        return mstProduct;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(MstProduct.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        mstProduct = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedMstProduct != null) {
            mstProductRepository.delete(insertedMstProduct).block();
            mstProductSearchRepository.delete(insertedMstProduct).block();
            insertedMstProduct = null;
        }
        deleteEntities(em);
    }

    @Test
    void createMstProduct() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstProductSearchRepository.findAll().collectList().block());
        // Create the MstProduct
        MstProductDTO mstProductDTO = mstProductMapper.toDto(mstProduct);
        var returnedMstProductDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstProductDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MstProductDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MstProduct in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMstProduct = mstProductMapper.toEntity(returnedMstProductDTO);
        assertMstProductUpdatableFieldsEquals(returnedMstProduct, getPersistedMstProduct(returnedMstProduct));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstProductSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedMstProduct = returnedMstProduct;
    }

    @Test
    void createMstProductWithExistingId() throws Exception {
        // Create the MstProduct with an existing ID
        mstProduct.setId(1L);
        MstProductDTO mstProductDTO = mstProductMapper.toDto(mstProduct);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstProductSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstProductDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstProductSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstProductSearchRepository.findAll().collectList().block());
        // set the field null
        mstProduct.setName(null);

        // Create the MstProduct, which fails.
        MstProductDTO mstProductDTO = mstProductMapper.toDto(mstProduct);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstProductDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstProductSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstProductSearchRepository.findAll().collectList().block());
        // set the field null
        mstProduct.setPrice(null);

        // Create the MstProduct, which fails.
        MstProductDTO mstProductDTO = mstProductMapper.toDto(mstProduct);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstProductDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstProductSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstProductSearchRepository.findAll().collectList().block());
        // set the field null
        mstProduct.setQuantity(null);

        // Create the MstProduct, which fails.
        MstProductDTO mstProductDTO = mstProductMapper.toDto(mstProduct);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstProductDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstProductSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllMstProducts() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList
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
            .value(hasItem(mstProduct.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].price")
            .value(hasItem(sameNumber(DEFAULT_PRICE)))
            .jsonPath("$.[*].quantity")
            .value(hasItem(DEFAULT_QUANTITY))
            .jsonPath("$.[*].barcode")
            .value(hasItem(DEFAULT_BARCODE))
            .jsonPath("$.[*].unitSize")
            .value(hasItem(DEFAULT_UNIT_SIZE));
    }

    @Test
    void getMstProduct() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get the mstProduct
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, mstProduct.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(mstProduct.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.price")
            .value(is(sameNumber(DEFAULT_PRICE)))
            .jsonPath("$.quantity")
            .value(is(DEFAULT_QUANTITY))
            .jsonPath("$.barcode")
            .value(is(DEFAULT_BARCODE))
            .jsonPath("$.unitSize")
            .value(is(DEFAULT_UNIT_SIZE));
    }

    @Test
    void getMstProductsByIdFiltering() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        Long id = mstProduct.getId();

        defaultMstProductFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMstProductFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMstProductFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllMstProductsByNameIsEqualToSomething() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where name equals to
        defaultMstProductFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllMstProductsByNameIsInShouldWork() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where name in
        defaultMstProductFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllMstProductsByNameIsNullOrNotNull() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where name is not null
        defaultMstProductFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllMstProductsByNameContainsSomething() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where name contains
        defaultMstProductFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllMstProductsByNameNotContainsSomething() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where name does not contain
        defaultMstProductFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllMstProductsByDescriptionIsEqualToSomething() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where description equals to
        defaultMstProductFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    void getAllMstProductsByDescriptionIsInShouldWork() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where description in
        defaultMstProductFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    void getAllMstProductsByDescriptionIsNullOrNotNull() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where description is not null
        defaultMstProductFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    void getAllMstProductsByDescriptionContainsSomething() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where description contains
        defaultMstProductFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    void getAllMstProductsByDescriptionNotContainsSomething() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where description does not contain
        defaultMstProductFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    @Test
    void getAllMstProductsByPriceIsEqualToSomething() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where price equals to
        defaultMstProductFiltering("price.equals=" + DEFAULT_PRICE, "price.equals=" + UPDATED_PRICE);
    }

    @Test
    void getAllMstProductsByPriceIsInShouldWork() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where price in
        defaultMstProductFiltering("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE, "price.in=" + UPDATED_PRICE);
    }

    @Test
    void getAllMstProductsByPriceIsNullOrNotNull() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where price is not null
        defaultMstProductFiltering("price.specified=true", "price.specified=false");
    }

    @Test
    void getAllMstProductsByPriceIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where price is greater than or equal to
        defaultMstProductFiltering("price.greaterThanOrEqual=" + DEFAULT_PRICE, "price.greaterThanOrEqual=" + UPDATED_PRICE);
    }

    @Test
    void getAllMstProductsByPriceIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where price is less than or equal to
        defaultMstProductFiltering("price.lessThanOrEqual=" + DEFAULT_PRICE, "price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    void getAllMstProductsByPriceIsLessThanSomething() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where price is less than
        defaultMstProductFiltering("price.lessThan=" + UPDATED_PRICE, "price.lessThan=" + DEFAULT_PRICE);
    }

    @Test
    void getAllMstProductsByPriceIsGreaterThanSomething() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where price is greater than
        defaultMstProductFiltering("price.greaterThan=" + SMALLER_PRICE, "price.greaterThan=" + DEFAULT_PRICE);
    }

    @Test
    void getAllMstProductsByQuantityIsEqualToSomething() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where quantity equals to
        defaultMstProductFiltering("quantity.equals=" + DEFAULT_QUANTITY, "quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    void getAllMstProductsByQuantityIsInShouldWork() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where quantity in
        defaultMstProductFiltering("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY, "quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    void getAllMstProductsByQuantityIsNullOrNotNull() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where quantity is not null
        defaultMstProductFiltering("quantity.specified=true", "quantity.specified=false");
    }

    @Test
    void getAllMstProductsByQuantityIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where quantity is greater than or equal to
        defaultMstProductFiltering("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY, "quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    void getAllMstProductsByQuantityIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where quantity is less than or equal to
        defaultMstProductFiltering("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY, "quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    void getAllMstProductsByQuantityIsLessThanSomething() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where quantity is less than
        defaultMstProductFiltering("quantity.lessThan=" + UPDATED_QUANTITY, "quantity.lessThan=" + DEFAULT_QUANTITY);
    }

    @Test
    void getAllMstProductsByQuantityIsGreaterThanSomething() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where quantity is greater than
        defaultMstProductFiltering("quantity.greaterThan=" + SMALLER_QUANTITY, "quantity.greaterThan=" + DEFAULT_QUANTITY);
    }

    @Test
    void getAllMstProductsByBarcodeIsEqualToSomething() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where barcode equals to
        defaultMstProductFiltering("barcode.equals=" + DEFAULT_BARCODE, "barcode.equals=" + UPDATED_BARCODE);
    }

    @Test
    void getAllMstProductsByBarcodeIsInShouldWork() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where barcode in
        defaultMstProductFiltering("barcode.in=" + DEFAULT_BARCODE + "," + UPDATED_BARCODE, "barcode.in=" + UPDATED_BARCODE);
    }

    @Test
    void getAllMstProductsByBarcodeIsNullOrNotNull() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where barcode is not null
        defaultMstProductFiltering("barcode.specified=true", "barcode.specified=false");
    }

    @Test
    void getAllMstProductsByBarcodeContainsSomething() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where barcode contains
        defaultMstProductFiltering("barcode.contains=" + DEFAULT_BARCODE, "barcode.contains=" + UPDATED_BARCODE);
    }

    @Test
    void getAllMstProductsByBarcodeNotContainsSomething() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where barcode does not contain
        defaultMstProductFiltering("barcode.doesNotContain=" + UPDATED_BARCODE, "barcode.doesNotContain=" + DEFAULT_BARCODE);
    }

    @Test
    void getAllMstProductsByUnitSizeIsEqualToSomething() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where unitSize equals to
        defaultMstProductFiltering("unitSize.equals=" + DEFAULT_UNIT_SIZE, "unitSize.equals=" + UPDATED_UNIT_SIZE);
    }

    @Test
    void getAllMstProductsByUnitSizeIsInShouldWork() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where unitSize in
        defaultMstProductFiltering("unitSize.in=" + DEFAULT_UNIT_SIZE + "," + UPDATED_UNIT_SIZE, "unitSize.in=" + UPDATED_UNIT_SIZE);
    }

    @Test
    void getAllMstProductsByUnitSizeIsNullOrNotNull() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where unitSize is not null
        defaultMstProductFiltering("unitSize.specified=true", "unitSize.specified=false");
    }

    @Test
    void getAllMstProductsByUnitSizeContainsSomething() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where unitSize contains
        defaultMstProductFiltering("unitSize.contains=" + DEFAULT_UNIT_SIZE, "unitSize.contains=" + UPDATED_UNIT_SIZE);
    }

    @Test
    void getAllMstProductsByUnitSizeNotContainsSomething() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        // Get all the mstProductList where unitSize does not contain
        defaultMstProductFiltering("unitSize.doesNotContain=" + UPDATED_UNIT_SIZE, "unitSize.doesNotContain=" + DEFAULT_UNIT_SIZE);
    }

    @Test
    void getAllMstProductsByCategoryIsEqualToSomething() {
        MstCategory category = MstCategoryResourceIT.createEntity(em);
        mstCategoryRepository.save(category).block();
        Long categoryId = category.getId();
        mstProduct.setCategoryId(categoryId);
        insertedMstProduct = mstProductRepository.save(mstProduct).block();
        // Get all the mstProductList where category equals to categoryId
        defaultMstProductShouldBeFound("categoryId.equals=" + categoryId);

        // Get all the mstProductList where category equals to (categoryId + 1)
        defaultMstProductShouldNotBeFound("categoryId.equals=" + (categoryId + 1));
    }

    @Test
    void getAllMstProductsByBrandIsEqualToSomething() {
        MstBrand brand = MstBrandResourceIT.createEntity(em);
        mstBrandRepository.save(brand).block();
        Long brandId = brand.getId();
        mstProduct.setBrandId(brandId);
        insertedMstProduct = mstProductRepository.save(mstProduct).block();
        // Get all the mstProductList where brand equals to brandId
        defaultMstProductShouldBeFound("brandId.equals=" + brandId);

        // Get all the mstProductList where brand equals to (brandId + 1)
        defaultMstProductShouldNotBeFound("brandId.equals=" + (brandId + 1));
    }

    @Test
    void getAllMstProductsByMstSupplierIsEqualToSomething() {
        MstSupplier mstSupplier = MstSupplierResourceIT.createEntity(em);
        mstSupplierRepository.save(mstSupplier).block();
        Long mstSupplierId = mstSupplier.getId();
        mstProduct.setMstSupplierId(mstSupplierId);
        insertedMstProduct = mstProductRepository.save(mstProduct).block();
        // Get all the mstProductList where mstSupplier equals to mstSupplierId
        defaultMstProductShouldBeFound("mstSupplierId.equals=" + mstSupplierId);

        // Get all the mstProductList where mstSupplier equals to (mstSupplierId + 1)
        defaultMstProductShouldNotBeFound("mstSupplierId.equals=" + (mstSupplierId + 1));
    }

    private void defaultMstProductFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultMstProductShouldBeFound(shouldBeFound);
        defaultMstProductShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMstProductShouldBeFound(String filter) {
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
            .value(hasItem(mstProduct.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].price")
            .value(hasItem(sameNumber(DEFAULT_PRICE)))
            .jsonPath("$.[*].quantity")
            .value(hasItem(DEFAULT_QUANTITY))
            .jsonPath("$.[*].barcode")
            .value(hasItem(DEFAULT_BARCODE))
            .jsonPath("$.[*].unitSize")
            .value(hasItem(DEFAULT_UNIT_SIZE));

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
    private void defaultMstProductShouldNotBeFound(String filter) {
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
    void getNonExistingMstProduct() {
        // Get the mstProduct
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMstProduct() throws Exception {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        mstProductSearchRepository.save(mstProduct).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstProductSearchRepository.findAll().collectList().block());

        // Update the mstProduct
        MstProduct updatedMstProduct = mstProductRepository.findById(mstProduct.getId()).block();
        updatedMstProduct
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .quantity(UPDATED_QUANTITY)
            .barcode(UPDATED_BARCODE)
            .unitSize(UPDATED_UNIT_SIZE);
        MstProductDTO mstProductDTO = mstProductMapper.toDto(updatedMstProduct);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstProductDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstProductDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMstProductToMatchAllProperties(updatedMstProduct);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstProductSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<MstProduct> mstProductSearchList = Streamable.of(mstProductSearchRepository.findAll().collectList().block()).toList();
                MstProduct testMstProductSearch = mstProductSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertMstProductAllPropertiesEquals(testMstProductSearch, updatedMstProduct);
                assertMstProductUpdatableFieldsEquals(testMstProductSearch, updatedMstProduct);
            });
    }

    @Test
    void putNonExistingMstProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstProductSearchRepository.findAll().collectList().block());
        mstProduct.setId(longCount.incrementAndGet());

        // Create the MstProduct
        MstProductDTO mstProductDTO = mstProductMapper.toDto(mstProduct);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstProductDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstProductDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstProductSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchMstProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstProductSearchRepository.findAll().collectList().block());
        mstProduct.setId(longCount.incrementAndGet());

        // Create the MstProduct
        MstProductDTO mstProductDTO = mstProductMapper.toDto(mstProduct);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstProductDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstProductSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamMstProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstProductSearchRepository.findAll().collectList().block());
        mstProduct.setId(longCount.incrementAndGet());

        // Create the MstProduct
        MstProductDTO mstProductDTO = mstProductMapper.toDto(mstProduct);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstProductDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstProductSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateMstProductWithPatch() throws Exception {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstProduct using partial update
        MstProduct partialUpdatedMstProduct = new MstProduct();
        partialUpdatedMstProduct.setId(mstProduct.getId());

        partialUpdatedMstProduct.name(UPDATED_NAME).price(UPDATED_PRICE).quantity(UPDATED_QUANTITY).unitSize(UPDATED_UNIT_SIZE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstProduct.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstProduct))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstProduct in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstProductUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMstProduct, mstProduct),
            getPersistedMstProduct(mstProduct)
        );
    }

    @Test
    void fullUpdateMstProductWithPatch() throws Exception {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstProduct using partial update
        MstProduct partialUpdatedMstProduct = new MstProduct();
        partialUpdatedMstProduct.setId(mstProduct.getId());

        partialUpdatedMstProduct
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .quantity(UPDATED_QUANTITY)
            .barcode(UPDATED_BARCODE)
            .unitSize(UPDATED_UNIT_SIZE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstProduct.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstProduct))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstProduct in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstProductUpdatableFieldsEquals(partialUpdatedMstProduct, getPersistedMstProduct(partialUpdatedMstProduct));
    }

    @Test
    void patchNonExistingMstProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstProductSearchRepository.findAll().collectList().block());
        mstProduct.setId(longCount.incrementAndGet());

        // Create the MstProduct
        MstProductDTO mstProductDTO = mstProductMapper.toDto(mstProduct);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, mstProductDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstProductDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstProductSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchMstProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstProductSearchRepository.findAll().collectList().block());
        mstProduct.setId(longCount.incrementAndGet());

        // Create the MstProduct
        MstProductDTO mstProductDTO = mstProductMapper.toDto(mstProduct);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstProductDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstProductSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamMstProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstProductSearchRepository.findAll().collectList().block());
        mstProduct.setId(longCount.incrementAndGet());

        // Create the MstProduct
        MstProductDTO mstProductDTO = mstProductMapper.toDto(mstProduct);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstProductDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstProductSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteMstProduct() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();
        mstProductRepository.save(mstProduct).block();
        mstProductSearchRepository.save(mstProduct).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstProductSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the mstProduct
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, mstProduct.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstProductSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchMstProduct() {
        // Initialize the database
        insertedMstProduct = mstProductRepository.save(mstProduct).block();
        mstProductSearchRepository.save(mstProduct).block();

        // Search the mstProduct
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + mstProduct.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(mstProduct.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].price")
            .value(hasItem(sameNumber(DEFAULT_PRICE)))
            .jsonPath("$.[*].quantity")
            .value(hasItem(DEFAULT_QUANTITY))
            .jsonPath("$.[*].barcode")
            .value(hasItem(DEFAULT_BARCODE))
            .jsonPath("$.[*].unitSize")
            .value(hasItem(DEFAULT_UNIT_SIZE));
    }

    protected long getRepositoryCount() {
        return mstProductRepository.count().block();
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

    protected MstProduct getPersistedMstProduct(MstProduct mstProduct) {
        return mstProductRepository.findById(mstProduct.getId()).block();
    }

    protected void assertPersistedMstProductToMatchAllProperties(MstProduct expectedMstProduct) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstProductAllPropertiesEquals(expectedMstProduct, getPersistedMstProduct(expectedMstProduct));
        assertMstProductUpdatableFieldsEquals(expectedMstProduct, getPersistedMstProduct(expectedMstProduct));
    }

    protected void assertPersistedMstProductToMatchUpdatableProperties(MstProduct expectedMstProduct) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstProductAllUpdatablePropertiesEquals(expectedMstProduct, getPersistedMstProduct(expectedMstProduct));
        assertMstProductUpdatableFieldsEquals(expectedMstProduct, getPersistedMstProduct(expectedMstProduct));
    }
}
