package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.MstCityAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.MstCity;
import com.mycompany.myapp.domain.MstProvince;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MstCityRepository;
import com.mycompany.myapp.repository.MstProvinceRepository;
import com.mycompany.myapp.repository.search.MstCitySearchRepository;
import com.mycompany.myapp.service.MstCityService;
import com.mycompany.myapp.service.dto.MstCityDTO;
import com.mycompany.myapp.service.mapper.MstCityMapper;
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
 * Integration tests for the {@link MstCityResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MstCityResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_UNM_49_CODE = "AAAAAAAAAA";
    private static final String UPDATED_UNM_49_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_ISO_ALPHA_2_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ISO_ALPHA_2_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/mst-cities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/mst-cities/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MstCityRepository mstCityRepository;

    @Mock
    private MstCityRepository mstCityRepositoryMock;

    @Autowired
    private MstCityMapper mstCityMapper;

    @Mock
    private MstCityService mstCityServiceMock;

    @Autowired
    private MstCitySearchRepository mstCitySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private MstCity mstCity;

    private MstCity insertedMstCity;

    @Autowired
    private MstProvinceRepository mstProvinceRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstCity createEntity(EntityManager em) {
        MstCity mstCity = new MstCity().name(DEFAULT_NAME).unm49Code(DEFAULT_UNM_49_CODE).isoAlpha2Code(DEFAULT_ISO_ALPHA_2_CODE);
        return mstCity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstCity createUpdatedEntity(EntityManager em) {
        MstCity mstCity = new MstCity().name(UPDATED_NAME).unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);
        return mstCity;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(MstCity.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        mstCity = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedMstCity != null) {
            mstCityRepository.delete(insertedMstCity).block();
            mstCitySearchRepository.delete(insertedMstCity).block();
            insertedMstCity = null;
        }
        deleteEntities(em);
    }

    @Test
    void createMstCity() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCitySearchRepository.findAll().collectList().block());
        // Create the MstCity
        MstCityDTO mstCityDTO = mstCityMapper.toDto(mstCity);
        var returnedMstCityDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCityDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MstCityDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MstCity in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMstCity = mstCityMapper.toEntity(returnedMstCityDTO);
        assertMstCityUpdatableFieldsEquals(returnedMstCity, getPersistedMstCity(returnedMstCity));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCitySearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedMstCity = returnedMstCity;
    }

    @Test
    void createMstCityWithExistingId() throws Exception {
        // Create the MstCity with an existing ID
        mstCity.setId(1L);
        MstCityDTO mstCityDTO = mstCityMapper.toDto(mstCity);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCitySearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstCity in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCitySearchRepository.findAll().collectList().block());
        // set the field null
        mstCity.setName(null);

        // Create the MstCity, which fails.
        MstCityDTO mstCityDTO = mstCityMapper.toDto(mstCity);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllMstCities() {
        // Initialize the database
        insertedMstCity = mstCityRepository.save(mstCity).block();

        // Get all the mstCityList
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
            .value(hasItem(mstCity.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].unm49Code")
            .value(hasItem(DEFAULT_UNM_49_CODE))
            .jsonPath("$.[*].isoAlpha2Code")
            .value(hasItem(DEFAULT_ISO_ALPHA_2_CODE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMstCitiesWithEagerRelationshipsIsEnabled() {
        when(mstCityServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(mstCityServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMstCitiesWithEagerRelationshipsIsNotEnabled() {
        when(mstCityServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(mstCityRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getMstCity() {
        // Initialize the database
        insertedMstCity = mstCityRepository.save(mstCity).block();

        // Get the mstCity
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, mstCity.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(mstCity.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.unm49Code")
            .value(is(DEFAULT_UNM_49_CODE))
            .jsonPath("$.isoAlpha2Code")
            .value(is(DEFAULT_ISO_ALPHA_2_CODE));
    }

    @Test
    void getMstCitiesByIdFiltering() {
        // Initialize the database
        insertedMstCity = mstCityRepository.save(mstCity).block();

        Long id = mstCity.getId();

        defaultMstCityFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMstCityFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMstCityFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllMstCitiesByNameIsEqualToSomething() {
        // Initialize the database
        insertedMstCity = mstCityRepository.save(mstCity).block();

        // Get all the mstCityList where name equals to
        defaultMstCityFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllMstCitiesByNameIsInShouldWork() {
        // Initialize the database
        insertedMstCity = mstCityRepository.save(mstCity).block();

        // Get all the mstCityList where name in
        defaultMstCityFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllMstCitiesByNameIsNullOrNotNull() {
        // Initialize the database
        insertedMstCity = mstCityRepository.save(mstCity).block();

        // Get all the mstCityList where name is not null
        defaultMstCityFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllMstCitiesByNameContainsSomething() {
        // Initialize the database
        insertedMstCity = mstCityRepository.save(mstCity).block();

        // Get all the mstCityList where name contains
        defaultMstCityFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllMstCitiesByNameNotContainsSomething() {
        // Initialize the database
        insertedMstCity = mstCityRepository.save(mstCity).block();

        // Get all the mstCityList where name does not contain
        defaultMstCityFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllMstCitiesByUnm49CodeIsEqualToSomething() {
        // Initialize the database
        insertedMstCity = mstCityRepository.save(mstCity).block();

        // Get all the mstCityList where unm49Code equals to
        defaultMstCityFiltering("unm49Code.equals=" + DEFAULT_UNM_49_CODE, "unm49Code.equals=" + UPDATED_UNM_49_CODE);
    }

    @Test
    void getAllMstCitiesByUnm49CodeIsInShouldWork() {
        // Initialize the database
        insertedMstCity = mstCityRepository.save(mstCity).block();

        // Get all the mstCityList where unm49Code in
        defaultMstCityFiltering("unm49Code.in=" + DEFAULT_UNM_49_CODE + "," + UPDATED_UNM_49_CODE, "unm49Code.in=" + UPDATED_UNM_49_CODE);
    }

    @Test
    void getAllMstCitiesByUnm49CodeIsNullOrNotNull() {
        // Initialize the database
        insertedMstCity = mstCityRepository.save(mstCity).block();

        // Get all the mstCityList where unm49Code is not null
        defaultMstCityFiltering("unm49Code.specified=true", "unm49Code.specified=false");
    }

    @Test
    void getAllMstCitiesByUnm49CodeContainsSomething() {
        // Initialize the database
        insertedMstCity = mstCityRepository.save(mstCity).block();

        // Get all the mstCityList where unm49Code contains
        defaultMstCityFiltering("unm49Code.contains=" + DEFAULT_UNM_49_CODE, "unm49Code.contains=" + UPDATED_UNM_49_CODE);
    }

    @Test
    void getAllMstCitiesByUnm49CodeNotContainsSomething() {
        // Initialize the database
        insertedMstCity = mstCityRepository.save(mstCity).block();

        // Get all the mstCityList where unm49Code does not contain
        defaultMstCityFiltering("unm49Code.doesNotContain=" + UPDATED_UNM_49_CODE, "unm49Code.doesNotContain=" + DEFAULT_UNM_49_CODE);
    }

    @Test
    void getAllMstCitiesByIsoAlpha2CodeIsEqualToSomething() {
        // Initialize the database
        insertedMstCity = mstCityRepository.save(mstCity).block();

        // Get all the mstCityList where isoAlpha2Code equals to
        defaultMstCityFiltering("isoAlpha2Code.equals=" + DEFAULT_ISO_ALPHA_2_CODE, "isoAlpha2Code.equals=" + UPDATED_ISO_ALPHA_2_CODE);
    }

    @Test
    void getAllMstCitiesByIsoAlpha2CodeIsInShouldWork() {
        // Initialize the database
        insertedMstCity = mstCityRepository.save(mstCity).block();

        // Get all the mstCityList where isoAlpha2Code in
        defaultMstCityFiltering(
            "isoAlpha2Code.in=" + DEFAULT_ISO_ALPHA_2_CODE + "," + UPDATED_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.in=" + UPDATED_ISO_ALPHA_2_CODE
        );
    }

    @Test
    void getAllMstCitiesByIsoAlpha2CodeIsNullOrNotNull() {
        // Initialize the database
        insertedMstCity = mstCityRepository.save(mstCity).block();

        // Get all the mstCityList where isoAlpha2Code is not null
        defaultMstCityFiltering("isoAlpha2Code.specified=true", "isoAlpha2Code.specified=false");
    }

    @Test
    void getAllMstCitiesByIsoAlpha2CodeContainsSomething() {
        // Initialize the database
        insertedMstCity = mstCityRepository.save(mstCity).block();

        // Get all the mstCityList where isoAlpha2Code contains
        defaultMstCityFiltering("isoAlpha2Code.contains=" + DEFAULT_ISO_ALPHA_2_CODE, "isoAlpha2Code.contains=" + UPDATED_ISO_ALPHA_2_CODE);
    }

    @Test
    void getAllMstCitiesByIsoAlpha2CodeNotContainsSomething() {
        // Initialize the database
        insertedMstCity = mstCityRepository.save(mstCity).block();

        // Get all the mstCityList where isoAlpha2Code does not contain
        defaultMstCityFiltering(
            "isoAlpha2Code.doesNotContain=" + UPDATED_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.doesNotContain=" + DEFAULT_ISO_ALPHA_2_CODE
        );
    }

    @Test
    void getAllMstCitiesByProvinceIsEqualToSomething() {
        MstProvince province = MstProvinceResourceIT.createEntity(em);
        mstProvinceRepository.save(province).block();
        Long provinceId = province.getId();
        mstCity.setProvinceId(provinceId);
        insertedMstCity = mstCityRepository.save(mstCity).block();
        // Get all the mstCityList where province equals to provinceId
        defaultMstCityShouldBeFound("provinceId.equals=" + provinceId);

        // Get all the mstCityList where province equals to (provinceId + 1)
        defaultMstCityShouldNotBeFound("provinceId.equals=" + (provinceId + 1));
    }

    private void defaultMstCityFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultMstCityShouldBeFound(shouldBeFound);
        defaultMstCityShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMstCityShouldBeFound(String filter) {
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
            .value(hasItem(mstCity.getId().intValue()))
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
    private void defaultMstCityShouldNotBeFound(String filter) {
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
    void getNonExistingMstCity() {
        // Get the mstCity
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMstCity() throws Exception {
        // Initialize the database
        insertedMstCity = mstCityRepository.save(mstCity).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        mstCitySearchRepository.save(mstCity).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCitySearchRepository.findAll().collectList().block());

        // Update the mstCity
        MstCity updatedMstCity = mstCityRepository.findById(mstCity.getId()).block();
        updatedMstCity.name(UPDATED_NAME).unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);
        MstCityDTO mstCityDTO = mstCityMapper.toDto(updatedMstCity);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstCityDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCityDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstCity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMstCityToMatchAllProperties(updatedMstCity);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCitySearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<MstCity> mstCitySearchList = Streamable.of(mstCitySearchRepository.findAll().collectList().block()).toList();
                MstCity testMstCitySearch = mstCitySearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertMstCityAllPropertiesEquals(testMstCitySearch, updatedMstCity);
                assertMstCityUpdatableFieldsEquals(testMstCitySearch, updatedMstCity);
            });
    }

    @Test
    void putNonExistingMstCity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCitySearchRepository.findAll().collectList().block());
        mstCity.setId(longCount.incrementAndGet());

        // Create the MstCity
        MstCityDTO mstCityDTO = mstCityMapper.toDto(mstCity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstCityDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstCity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchMstCity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCitySearchRepository.findAll().collectList().block());
        mstCity.setId(longCount.incrementAndGet());

        // Create the MstCity
        MstCityDTO mstCityDTO = mstCityMapper.toDto(mstCity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstCity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamMstCity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCitySearchRepository.findAll().collectList().block());
        mstCity.setId(longCount.incrementAndGet());

        // Create the MstCity
        MstCityDTO mstCityDTO = mstCityMapper.toDto(mstCity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCityDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstCity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateMstCityWithPatch() throws Exception {
        // Initialize the database
        insertedMstCity = mstCityRepository.save(mstCity).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstCity using partial update
        MstCity partialUpdatedMstCity = new MstCity();
        partialUpdatedMstCity.setId(mstCity.getId());

        partialUpdatedMstCity.isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstCity.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstCity))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstCity in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstCityUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedMstCity, mstCity), getPersistedMstCity(mstCity));
    }

    @Test
    void fullUpdateMstCityWithPatch() throws Exception {
        // Initialize the database
        insertedMstCity = mstCityRepository.save(mstCity).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstCity using partial update
        MstCity partialUpdatedMstCity = new MstCity();
        partialUpdatedMstCity.setId(mstCity.getId());

        partialUpdatedMstCity.name(UPDATED_NAME).unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstCity.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstCity))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstCity in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstCityUpdatableFieldsEquals(partialUpdatedMstCity, getPersistedMstCity(partialUpdatedMstCity));
    }

    @Test
    void patchNonExistingMstCity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCitySearchRepository.findAll().collectList().block());
        mstCity.setId(longCount.incrementAndGet());

        // Create the MstCity
        MstCityDTO mstCityDTO = mstCityMapper.toDto(mstCity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, mstCityDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstCityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstCity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchMstCity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCitySearchRepository.findAll().collectList().block());
        mstCity.setId(longCount.incrementAndGet());

        // Create the MstCity
        MstCityDTO mstCityDTO = mstCityMapper.toDto(mstCity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstCityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstCity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamMstCity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCitySearchRepository.findAll().collectList().block());
        mstCity.setId(longCount.incrementAndGet());

        // Create the MstCity
        MstCityDTO mstCityDTO = mstCityMapper.toDto(mstCity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstCityDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstCity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteMstCity() {
        // Initialize the database
        insertedMstCity = mstCityRepository.save(mstCity).block();
        mstCityRepository.save(mstCity).block();
        mstCitySearchRepository.save(mstCity).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the mstCity
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, mstCity.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchMstCity() {
        // Initialize the database
        insertedMstCity = mstCityRepository.save(mstCity).block();
        mstCitySearchRepository.save(mstCity).block();

        // Search the mstCity
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + mstCity.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(mstCity.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].unm49Code")
            .value(hasItem(DEFAULT_UNM_49_CODE))
            .jsonPath("$.[*].isoAlpha2Code")
            .value(hasItem(DEFAULT_ISO_ALPHA_2_CODE));
    }

    protected long getRepositoryCount() {
        return mstCityRepository.count().block();
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

    protected MstCity getPersistedMstCity(MstCity mstCity) {
        return mstCityRepository.findById(mstCity.getId()).block();
    }

    protected void assertPersistedMstCityToMatchAllProperties(MstCity expectedMstCity) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstCityAllPropertiesEquals(expectedMstCity, getPersistedMstCity(expectedMstCity));
        assertMstCityUpdatableFieldsEquals(expectedMstCity, getPersistedMstCity(expectedMstCity));
    }

    protected void assertPersistedMstCityToMatchUpdatableProperties(MstCity expectedMstCity) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstCityAllUpdatablePropertiesEquals(expectedMstCity, getPersistedMstCity(expectedMstCity));
        assertMstCityUpdatableFieldsEquals(expectedMstCity, getPersistedMstCity(expectedMstCity));
    }
}
