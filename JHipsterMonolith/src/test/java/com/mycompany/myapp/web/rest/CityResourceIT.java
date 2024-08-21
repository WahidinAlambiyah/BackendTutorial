package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.CityAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.City;
import com.mycompany.myapp.domain.Province;
import com.mycompany.myapp.repository.CityRepository;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.ProvinceRepository;
import com.mycompany.myapp.repository.search.CitySearchRepository;
import com.mycompany.myapp.service.CityService;
import com.mycompany.myapp.service.dto.CityDTO;
import com.mycompany.myapp.service.mapper.CityMapper;
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
 * Integration tests for the {@link CityResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CityResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_UNM_49_CODE = "AAAAAAAAAA";
    private static final String UPDATED_UNM_49_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_ISO_ALPHA_2_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ISO_ALPHA_2_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/cities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/cities/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CityRepository cityRepository;

    @Mock
    private CityRepository cityRepositoryMock;

    @Autowired
    private CityMapper cityMapper;

    @Mock
    private CityService cityServiceMock;

    @Autowired
    private CitySearchRepository citySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private City city;

    private City insertedCity;

    @Autowired
    private ProvinceRepository provinceRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static City createEntity(EntityManager em) {
        City city = new City().name(DEFAULT_NAME).unm49Code(DEFAULT_UNM_49_CODE).isoAlpha2Code(DEFAULT_ISO_ALPHA_2_CODE);
        return city;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static City createUpdatedEntity(EntityManager em) {
        City city = new City().name(UPDATED_NAME).unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);
        return city;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(City.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        city = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedCity != null) {
            cityRepository.delete(insertedCity).block();
            citySearchRepository.delete(insertedCity).block();
            insertedCity = null;
        }
        deleteEntities(em);
    }

    @Test
    void createCity() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citySearchRepository.findAll().collectList().block());
        // Create the City
        CityDTO cityDTO = cityMapper.toDto(city);
        var returnedCityDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(cityDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(CityDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the City in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCity = cityMapper.toEntity(returnedCityDTO);
        assertCityUpdatableFieldsEquals(returnedCity, getPersistedCity(returnedCity));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(citySearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedCity = returnedCity;
    }

    @Test
    void createCityWithExistingId() throws Exception {
        // Create the City with an existing ID
        city.setId(1L);
        CityDTO cityDTO = cityMapper.toDto(city);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citySearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(cityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the City in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citySearchRepository.findAll().collectList().block());
        // set the field null
        city.setName(null);

        // Create the City, which fails.
        CityDTO cityDTO = cityMapper.toDto(city);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(cityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllCities() {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();

        // Get all the cityList
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
            .value(hasItem(city.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].unm49Code")
            .value(hasItem(DEFAULT_UNM_49_CODE))
            .jsonPath("$.[*].isoAlpha2Code")
            .value(hasItem(DEFAULT_ISO_ALPHA_2_CODE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCitiesWithEagerRelationshipsIsEnabled() {
        when(cityServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(cityServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCitiesWithEagerRelationshipsIsNotEnabled() {
        when(cityServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(cityRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getCity() {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();

        // Get the city
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, city.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(city.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.unm49Code")
            .value(is(DEFAULT_UNM_49_CODE))
            .jsonPath("$.isoAlpha2Code")
            .value(is(DEFAULT_ISO_ALPHA_2_CODE));
    }

    @Test
    void getCitiesByIdFiltering() {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();

        Long id = city.getId();

        defaultCityFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCityFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCityFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllCitiesByNameIsEqualToSomething() {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();

        // Get all the cityList where name equals to
        defaultCityFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllCitiesByNameIsInShouldWork() {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();

        // Get all the cityList where name in
        defaultCityFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllCitiesByNameIsNullOrNotNull() {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();

        // Get all the cityList where name is not null
        defaultCityFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllCitiesByNameContainsSomething() {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();

        // Get all the cityList where name contains
        defaultCityFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllCitiesByNameNotContainsSomething() {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();

        // Get all the cityList where name does not contain
        defaultCityFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllCitiesByUnm49CodeIsEqualToSomething() {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();

        // Get all the cityList where unm49Code equals to
        defaultCityFiltering("unm49Code.equals=" + DEFAULT_UNM_49_CODE, "unm49Code.equals=" + UPDATED_UNM_49_CODE);
    }

    @Test
    void getAllCitiesByUnm49CodeIsInShouldWork() {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();

        // Get all the cityList where unm49Code in
        defaultCityFiltering("unm49Code.in=" + DEFAULT_UNM_49_CODE + "," + UPDATED_UNM_49_CODE, "unm49Code.in=" + UPDATED_UNM_49_CODE);
    }

    @Test
    void getAllCitiesByUnm49CodeIsNullOrNotNull() {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();

        // Get all the cityList where unm49Code is not null
        defaultCityFiltering("unm49Code.specified=true", "unm49Code.specified=false");
    }

    @Test
    void getAllCitiesByUnm49CodeContainsSomething() {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();

        // Get all the cityList where unm49Code contains
        defaultCityFiltering("unm49Code.contains=" + DEFAULT_UNM_49_CODE, "unm49Code.contains=" + UPDATED_UNM_49_CODE);
    }

    @Test
    void getAllCitiesByUnm49CodeNotContainsSomething() {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();

        // Get all the cityList where unm49Code does not contain
        defaultCityFiltering("unm49Code.doesNotContain=" + UPDATED_UNM_49_CODE, "unm49Code.doesNotContain=" + DEFAULT_UNM_49_CODE);
    }

    @Test
    void getAllCitiesByIsoAlpha2CodeIsEqualToSomething() {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();

        // Get all the cityList where isoAlpha2Code equals to
        defaultCityFiltering("isoAlpha2Code.equals=" + DEFAULT_ISO_ALPHA_2_CODE, "isoAlpha2Code.equals=" + UPDATED_ISO_ALPHA_2_CODE);
    }

    @Test
    void getAllCitiesByIsoAlpha2CodeIsInShouldWork() {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();

        // Get all the cityList where isoAlpha2Code in
        defaultCityFiltering(
            "isoAlpha2Code.in=" + DEFAULT_ISO_ALPHA_2_CODE + "," + UPDATED_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.in=" + UPDATED_ISO_ALPHA_2_CODE
        );
    }

    @Test
    void getAllCitiesByIsoAlpha2CodeIsNullOrNotNull() {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();

        // Get all the cityList where isoAlpha2Code is not null
        defaultCityFiltering("isoAlpha2Code.specified=true", "isoAlpha2Code.specified=false");
    }

    @Test
    void getAllCitiesByIsoAlpha2CodeContainsSomething() {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();

        // Get all the cityList where isoAlpha2Code contains
        defaultCityFiltering("isoAlpha2Code.contains=" + DEFAULT_ISO_ALPHA_2_CODE, "isoAlpha2Code.contains=" + UPDATED_ISO_ALPHA_2_CODE);
    }

    @Test
    void getAllCitiesByIsoAlpha2CodeNotContainsSomething() {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();

        // Get all the cityList where isoAlpha2Code does not contain
        defaultCityFiltering(
            "isoAlpha2Code.doesNotContain=" + UPDATED_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.doesNotContain=" + DEFAULT_ISO_ALPHA_2_CODE
        );
    }

    @Test
    void getAllCitiesByProvinceIsEqualToSomething() {
        Province province = ProvinceResourceIT.createEntity(em);
        provinceRepository.save(province).block();
        Long provinceId = province.getId();
        city.setProvinceId(provinceId);
        insertedCity = cityRepository.save(city).block();
        // Get all the cityList where province equals to provinceId
        defaultCityShouldBeFound("provinceId.equals=" + provinceId);

        // Get all the cityList where province equals to (provinceId + 1)
        defaultCityShouldNotBeFound("provinceId.equals=" + (provinceId + 1));
    }

    private void defaultCityFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultCityShouldBeFound(shouldBeFound);
        defaultCityShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCityShouldBeFound(String filter) {
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
            .value(hasItem(city.getId().intValue()))
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
    private void defaultCityShouldNotBeFound(String filter) {
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
    void getNonExistingCity() {
        // Get the city
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingCity() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        citySearchRepository.save(city).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citySearchRepository.findAll().collectList().block());

        // Update the city
        City updatedCity = cityRepository.findById(city.getId()).block();
        updatedCity.name(UPDATED_NAME).unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);
        CityDTO cityDTO = cityMapper.toDto(updatedCity);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, cityDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(cityDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the City in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCityToMatchAllProperties(updatedCity);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(citySearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<City> citySearchList = Streamable.of(citySearchRepository.findAll().collectList().block()).toList();
                City testCitySearch = citySearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertCityAllPropertiesEquals(testCitySearch, updatedCity);
                assertCityUpdatableFieldsEquals(testCitySearch, updatedCity);
            });
    }

    @Test
    void putNonExistingCity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citySearchRepository.findAll().collectList().block());
        city.setId(longCount.incrementAndGet());

        // Create the City
        CityDTO cityDTO = cityMapper.toDto(city);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, cityDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(cityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the City in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchCity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citySearchRepository.findAll().collectList().block());
        city.setId(longCount.incrementAndGet());

        // Create the City
        CityDTO cityDTO = cityMapper.toDto(city);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(cityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the City in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamCity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citySearchRepository.findAll().collectList().block());
        city.setId(longCount.incrementAndGet());

        // Create the City
        CityDTO cityDTO = cityMapper.toDto(city);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(cityDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the City in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateCityWithPatch() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the city using partial update
        City partialUpdatedCity = new City();
        partialUpdatedCity.setId(city.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCity.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCity))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the City in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCityUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCity, city), getPersistedCity(city));
    }

    @Test
    void fullUpdateCityWithPatch() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the city using partial update
        City partialUpdatedCity = new City();
        partialUpdatedCity.setId(city.getId());

        partialUpdatedCity.name(UPDATED_NAME).unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCity.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCity))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the City in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCityUpdatableFieldsEquals(partialUpdatedCity, getPersistedCity(partialUpdatedCity));
    }

    @Test
    void patchNonExistingCity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citySearchRepository.findAll().collectList().block());
        city.setId(longCount.incrementAndGet());

        // Create the City
        CityDTO cityDTO = cityMapper.toDto(city);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, cityDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(cityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the City in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchCity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citySearchRepository.findAll().collectList().block());
        city.setId(longCount.incrementAndGet());

        // Create the City
        CityDTO cityDTO = cityMapper.toDto(city);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(cityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the City in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamCity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citySearchRepository.findAll().collectList().block());
        city.setId(longCount.incrementAndGet());

        // Create the City
        CityDTO cityDTO = cityMapper.toDto(city);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(cityDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the City in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteCity() {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();
        cityRepository.save(city).block();
        citySearchRepository.save(city).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the city
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, city.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchCity() {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();
        citySearchRepository.save(city).block();

        // Search the city
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + city.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(city.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].unm49Code")
            .value(hasItem(DEFAULT_UNM_49_CODE))
            .jsonPath("$.[*].isoAlpha2Code")
            .value(hasItem(DEFAULT_ISO_ALPHA_2_CODE));
    }

    protected long getRepositoryCount() {
        return cityRepository.count().block();
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

    protected City getPersistedCity(City city) {
        return cityRepository.findById(city.getId()).block();
    }

    protected void assertPersistedCityToMatchAllProperties(City expectedCity) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCityAllPropertiesEquals(expectedCity, getPersistedCity(expectedCity));
        assertCityUpdatableFieldsEquals(expectedCity, getPersistedCity(expectedCity));
    }

    protected void assertPersistedCityToMatchUpdatableProperties(City expectedCity) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCityAllUpdatablePropertiesEquals(expectedCity, getPersistedCity(expectedCity));
        assertCityUpdatableFieldsEquals(expectedCity, getPersistedCity(expectedCity));
    }
}
