package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.ProvinceAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Country;
import com.mycompany.myapp.domain.Province;
import com.mycompany.myapp.repository.CountryRepository;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.ProvinceRepository;
import com.mycompany.myapp.repository.search.ProvinceSearchRepository;
import com.mycompany.myapp.service.ProvinceService;
import com.mycompany.myapp.service.dto.ProvinceDTO;
import com.mycompany.myapp.service.mapper.ProvinceMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

/**
 * Integration tests for the {@link ProvinceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProvinceResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_UNM_49_CODE = "AAAAAAAAAA";
    private static final String UPDATED_UNM_49_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_ISO_ALPHA_2_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ISO_ALPHA_2_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/provinces";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/provinces/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Mock
    private ProvinceRepository provinceRepositoryMock;

    @Autowired
    private ProvinceMapper provinceMapper;

    @Mock
    private ProvinceService provinceServiceMock;

    @Autowired
    private ProvinceSearchRepository provinceSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Province province;

    private Province insertedProvince;

    @Autowired
    private CountryRepository countryRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Province createEntity(EntityManager em) {
        Province province = new Province().name(DEFAULT_NAME).unm49Code(DEFAULT_UNM_49_CODE).isoAlpha2Code(DEFAULT_ISO_ALPHA_2_CODE);
        return province;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Province createUpdatedEntity(EntityManager em) {
        Province province = new Province().name(UPDATED_NAME).unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);
        return province;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Province.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        province = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedProvince != null) {
            provinceRepository.delete(insertedProvince).block();
            provinceSearchRepository.delete(insertedProvince).block();
            insertedProvince = null;
        }
        deleteEntities(em);
    }

    @Test
    void createProvince() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        // Create the Province
        ProvinceDTO provinceDTO = provinceMapper.toDto(province);
        var returnedProvinceDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(provinceDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ProvinceDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Province in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProvince = provinceMapper.toEntity(returnedProvinceDTO);
        assertProvinceUpdatableFieldsEquals(returnedProvince, getPersistedProvince(returnedProvince));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedProvince = returnedProvince;
    }

    @Test
    void createProvinceWithExistingId() throws Exception {
        // Create the Province with an existing ID
        province.setId(1L);
        ProvinceDTO provinceDTO = provinceMapper.toDto(province);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(provinceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Province in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        // set the field null
        province.setName(null);

        // Create the Province, which fails.
        ProvinceDTO provinceDTO = provinceMapper.toDto(province);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(provinceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllProvinces() {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();

        // Get all the provinceList
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
            .value(hasItem(province.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].unm49Code")
            .value(hasItem(DEFAULT_UNM_49_CODE))
            .jsonPath("$.[*].isoAlpha2Code")
            .value(hasItem(DEFAULT_ISO_ALPHA_2_CODE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProvincesWithEagerRelationshipsIsEnabled() {
        when(provinceServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(provinceServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProvincesWithEagerRelationshipsIsNotEnabled() {
        when(provinceServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(provinceRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getProvince() {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();

        // Get the province
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, province.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(province.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.unm49Code")
            .value(is(DEFAULT_UNM_49_CODE))
            .jsonPath("$.isoAlpha2Code")
            .value(is(DEFAULT_ISO_ALPHA_2_CODE));
    }

    @Test
    void getProvincesByIdFiltering() {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();

        Long id = province.getId();

        defaultProvinceFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultProvinceFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultProvinceFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllProvincesByNameIsEqualToSomething() {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();

        // Get all the provinceList where name equals to
        defaultProvinceFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllProvincesByNameIsInShouldWork() {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();

        // Get all the provinceList where name in
        defaultProvinceFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllProvincesByNameIsNullOrNotNull() {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();

        // Get all the provinceList where name is not null
        defaultProvinceFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllProvincesByNameContainsSomething() {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();

        // Get all the provinceList where name contains
        defaultProvinceFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllProvincesByNameNotContainsSomething() {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();

        // Get all the provinceList where name does not contain
        defaultProvinceFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllProvincesByUnm49CodeIsEqualToSomething() {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();

        // Get all the provinceList where unm49Code equals to
        defaultProvinceFiltering("unm49Code.equals=" + DEFAULT_UNM_49_CODE, "unm49Code.equals=" + UPDATED_UNM_49_CODE);
    }

    @Test
    void getAllProvincesByUnm49CodeIsInShouldWork() {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();

        // Get all the provinceList where unm49Code in
        defaultProvinceFiltering("unm49Code.in=" + DEFAULT_UNM_49_CODE + "," + UPDATED_UNM_49_CODE, "unm49Code.in=" + UPDATED_UNM_49_CODE);
    }

    @Test
    void getAllProvincesByUnm49CodeIsNullOrNotNull() {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();

        // Get all the provinceList where unm49Code is not null
        defaultProvinceFiltering("unm49Code.specified=true", "unm49Code.specified=false");
    }

    @Test
    void getAllProvincesByUnm49CodeContainsSomething() {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();

        // Get all the provinceList where unm49Code contains
        defaultProvinceFiltering("unm49Code.contains=" + DEFAULT_UNM_49_CODE, "unm49Code.contains=" + UPDATED_UNM_49_CODE);
    }

    @Test
    void getAllProvincesByUnm49CodeNotContainsSomething() {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();

        // Get all the provinceList where unm49Code does not contain
        defaultProvinceFiltering("unm49Code.doesNotContain=" + UPDATED_UNM_49_CODE, "unm49Code.doesNotContain=" + DEFAULT_UNM_49_CODE);
    }

    @Test
    void getAllProvincesByIsoAlpha2CodeIsEqualToSomething() {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();

        // Get all the provinceList where isoAlpha2Code equals to
        defaultProvinceFiltering("isoAlpha2Code.equals=" + DEFAULT_ISO_ALPHA_2_CODE, "isoAlpha2Code.equals=" + UPDATED_ISO_ALPHA_2_CODE);
    }

    @Test
    void getAllProvincesByIsoAlpha2CodeIsInShouldWork() {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();

        // Get all the provinceList where isoAlpha2Code in
        defaultProvinceFiltering(
            "isoAlpha2Code.in=" + DEFAULT_ISO_ALPHA_2_CODE + "," + UPDATED_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.in=" + UPDATED_ISO_ALPHA_2_CODE
        );
    }

    @Test
    void getAllProvincesByIsoAlpha2CodeIsNullOrNotNull() {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();

        // Get all the provinceList where isoAlpha2Code is not null
        defaultProvinceFiltering("isoAlpha2Code.specified=true", "isoAlpha2Code.specified=false");
    }

    @Test
    void getAllProvincesByIsoAlpha2CodeContainsSomething() {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();

        // Get all the provinceList where isoAlpha2Code contains
        defaultProvinceFiltering(
            "isoAlpha2Code.contains=" + DEFAULT_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.contains=" + UPDATED_ISO_ALPHA_2_CODE
        );
    }

    @Test
    void getAllProvincesByIsoAlpha2CodeNotContainsSomething() {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();

        // Get all the provinceList where isoAlpha2Code does not contain
        defaultProvinceFiltering(
            "isoAlpha2Code.doesNotContain=" + UPDATED_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.doesNotContain=" + DEFAULT_ISO_ALPHA_2_CODE
        );
    }

    @Test
    void getAllProvincesByCountryIsEqualToSomething() {
        Country country = CountryResourceIT.createEntity(em);
        countryRepository.save(country).block();
        Long countryId = country.getId();
        province.setCountryId(countryId);
        insertedProvince = provinceRepository.save(province).block();
        // Get all the provinceList where country equals to countryId
        defaultProvinceShouldBeFound("countryId.equals=" + countryId);

        // Get all the provinceList where country equals to (countryId + 1)
        defaultProvinceShouldNotBeFound("countryId.equals=" + (countryId + 1));
    }

    private void defaultProvinceFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultProvinceShouldBeFound(shouldBeFound);
        defaultProvinceShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProvinceShouldBeFound(String filter) {
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
            .value(hasItem(province.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].unm49Code")
            .value(hasItem(DEFAULT_UNM_49_CODE))
            .jsonPath("$.[*].isoAlpha2Code")
            .value(hasItem(DEFAULT_ISO_ALPHA_2_CODE));

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
    private void defaultProvinceShouldNotBeFound(String filter) {
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
    void getNonExistingProvince() {
        // Get the province
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProvince() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        provinceSearchRepository.save(province).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());

        // Update the province
        Province updatedProvince = provinceRepository.findById(province.getId()).block();
        updatedProvince.name(UPDATED_NAME).unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);
        ProvinceDTO provinceDTO = provinceMapper.toDto(updatedProvince);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, provinceDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(provinceDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Province in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProvinceToMatchAllProperties(updatedProvince);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Province> provinceSearchList = Streamable.of(provinceSearchRepository.findAll().collectList().block()).toList();
                Province testProvinceSearch = provinceSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertProvinceAllPropertiesEquals(testProvinceSearch, updatedProvince);
                assertProvinceUpdatableFieldsEquals(testProvinceSearch, updatedProvince);
            });
    }

    @Test
    void putNonExistingProvince() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        province.setId(longCount.incrementAndGet());

        // Create the Province
        ProvinceDTO provinceDTO = provinceMapper.toDto(province);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, provinceDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(provinceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Province in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchProvince() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        province.setId(longCount.incrementAndGet());

        // Create the Province
        ProvinceDTO provinceDTO = provinceMapper.toDto(province);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(provinceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Province in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamProvince() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        province.setId(longCount.incrementAndGet());

        // Create the Province
        ProvinceDTO provinceDTO = provinceMapper.toDto(province);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(provinceDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Province in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateProvinceWithPatch() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the province using partial update
        Province partialUpdatedProvince = new Province();
        partialUpdatedProvince.setId(province.getId());

        partialUpdatedProvince.isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProvince.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedProvince))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Province in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProvinceUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedProvince, province), getPersistedProvince(province));
    }

    @Test
    void fullUpdateProvinceWithPatch() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the province using partial update
        Province partialUpdatedProvince = new Province();
        partialUpdatedProvince.setId(province.getId());

        partialUpdatedProvince.name(UPDATED_NAME).unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProvince.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedProvince))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Province in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProvinceUpdatableFieldsEquals(partialUpdatedProvince, getPersistedProvince(partialUpdatedProvince));
    }

    @Test
    void patchNonExistingProvince() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        province.setId(longCount.incrementAndGet());

        // Create the Province
        ProvinceDTO provinceDTO = provinceMapper.toDto(province);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, provinceDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(provinceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Province in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchProvince() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        province.setId(longCount.incrementAndGet());

        // Create the Province
        ProvinceDTO provinceDTO = provinceMapper.toDto(province);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(provinceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Province in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamProvince() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        province.setId(longCount.incrementAndGet());

        // Create the Province
        ProvinceDTO provinceDTO = provinceMapper.toDto(province);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(provinceDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Province in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteProvince() {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();
        provinceRepository.save(province).block();
        provinceSearchRepository.save(province).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the province
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, province.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(provinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchProvince() {
        // Initialize the database
        insertedProvince = provinceRepository.save(province).block();
        provinceSearchRepository.save(province).block();

        // Search the province
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + province.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(province.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].unm49Code")
            .value(hasItem(DEFAULT_UNM_49_CODE))
            .jsonPath("$.[*].isoAlpha2Code")
            .value(hasItem(DEFAULT_ISO_ALPHA_2_CODE));
    }

    protected long getRepositoryCount() {
        return provinceRepository.count().block();
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

    protected Province getPersistedProvince(Province province) {
        return provinceRepository.findById(province.getId()).block();
    }

    protected void assertPersistedProvinceToMatchAllProperties(Province expectedProvince) {
        // Test fails because reactive api returns an empty object instead of null
        // assertProvinceAllPropertiesEquals(expectedProvince, getPersistedProvince(expectedProvince));
        assertProvinceUpdatableFieldsEquals(expectedProvince, getPersistedProvince(expectedProvince));
    }

    protected void assertPersistedProvinceToMatchUpdatableProperties(Province expectedProvince) {
        // Test fails because reactive api returns an empty object instead of null
        // assertProvinceAllUpdatablePropertiesEquals(expectedProvince, getPersistedProvince(expectedProvince));
        assertProvinceUpdatableFieldsEquals(expectedProvince, getPersistedProvince(expectedProvince));
    }
}
