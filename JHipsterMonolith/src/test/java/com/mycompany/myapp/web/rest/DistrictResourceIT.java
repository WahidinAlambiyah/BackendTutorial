package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.DistrictAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.City;
import com.mycompany.myapp.domain.District;
import com.mycompany.myapp.repository.CityRepository;
import com.mycompany.myapp.repository.DistrictRepository;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.search.DistrictSearchRepository;
import com.mycompany.myapp.service.DistrictService;
import com.mycompany.myapp.service.dto.DistrictDTO;
import com.mycompany.myapp.service.mapper.DistrictMapper;
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
 * Integration tests for the {@link DistrictResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DistrictResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_UNM_49_CODE = "AAAAAAAAAA";
    private static final String UPDATED_UNM_49_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_ISO_ALPHA_2_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ISO_ALPHA_2_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/districts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/districts/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DistrictRepository districtRepository;

    @Mock
    private DistrictRepository districtRepositoryMock;

    @Autowired
    private DistrictMapper districtMapper;

    @Mock
    private DistrictService districtServiceMock;

    @Autowired
    private DistrictSearchRepository districtSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private District district;

    private District insertedDistrict;

    @Autowired
    private CityRepository cityRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static District createEntity(EntityManager em) {
        District district = new District().name(DEFAULT_NAME).unm49Code(DEFAULT_UNM_49_CODE).isoAlpha2Code(DEFAULT_ISO_ALPHA_2_CODE);
        return district;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static District createUpdatedEntity(EntityManager em) {
        District district = new District().name(UPDATED_NAME).unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);
        return district;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(District.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        district = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedDistrict != null) {
            districtRepository.delete(insertedDistrict).block();
            districtSearchRepository.delete(insertedDistrict).block();
            insertedDistrict = null;
        }
        deleteEntities(em);
    }

    @Test
    void createDistrict() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        // Create the District
        DistrictDTO districtDTO = districtMapper.toDto(district);
        var returnedDistrictDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(districtDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(DistrictDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the District in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDistrict = districtMapper.toEntity(returnedDistrictDTO);
        assertDistrictUpdatableFieldsEquals(returnedDistrict, getPersistedDistrict(returnedDistrict));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedDistrict = returnedDistrict;
    }

    @Test
    void createDistrictWithExistingId() throws Exception {
        // Create the District with an existing ID
        district.setId(1L);
        DistrictDTO districtDTO = districtMapper.toDto(district);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(districtDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the District in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        // set the field null
        district.setName(null);

        // Create the District, which fails.
        DistrictDTO districtDTO = districtMapper.toDto(district);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(districtDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllDistricts() {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();

        // Get all the districtList
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
            .value(hasItem(district.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].unm49Code")
            .value(hasItem(DEFAULT_UNM_49_CODE))
            .jsonPath("$.[*].isoAlpha2Code")
            .value(hasItem(DEFAULT_ISO_ALPHA_2_CODE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDistrictsWithEagerRelationshipsIsEnabled() {
        when(districtServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(districtServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDistrictsWithEagerRelationshipsIsNotEnabled() {
        when(districtServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(districtRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getDistrict() {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();

        // Get the district
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, district.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(district.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.unm49Code")
            .value(is(DEFAULT_UNM_49_CODE))
            .jsonPath("$.isoAlpha2Code")
            .value(is(DEFAULT_ISO_ALPHA_2_CODE));
    }

    @Test
    void getDistrictsByIdFiltering() {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();

        Long id = district.getId();

        defaultDistrictFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDistrictFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDistrictFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllDistrictsByNameIsEqualToSomething() {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();

        // Get all the districtList where name equals to
        defaultDistrictFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllDistrictsByNameIsInShouldWork() {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();

        // Get all the districtList where name in
        defaultDistrictFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllDistrictsByNameIsNullOrNotNull() {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();

        // Get all the districtList where name is not null
        defaultDistrictFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllDistrictsByNameContainsSomething() {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();

        // Get all the districtList where name contains
        defaultDistrictFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllDistrictsByNameNotContainsSomething() {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();

        // Get all the districtList where name does not contain
        defaultDistrictFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllDistrictsByUnm49CodeIsEqualToSomething() {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();

        // Get all the districtList where unm49Code equals to
        defaultDistrictFiltering("unm49Code.equals=" + DEFAULT_UNM_49_CODE, "unm49Code.equals=" + UPDATED_UNM_49_CODE);
    }

    @Test
    void getAllDistrictsByUnm49CodeIsInShouldWork() {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();

        // Get all the districtList where unm49Code in
        defaultDistrictFiltering("unm49Code.in=" + DEFAULT_UNM_49_CODE + "," + UPDATED_UNM_49_CODE, "unm49Code.in=" + UPDATED_UNM_49_CODE);
    }

    @Test
    void getAllDistrictsByUnm49CodeIsNullOrNotNull() {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();

        // Get all the districtList where unm49Code is not null
        defaultDistrictFiltering("unm49Code.specified=true", "unm49Code.specified=false");
    }

    @Test
    void getAllDistrictsByUnm49CodeContainsSomething() {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();

        // Get all the districtList where unm49Code contains
        defaultDistrictFiltering("unm49Code.contains=" + DEFAULT_UNM_49_CODE, "unm49Code.contains=" + UPDATED_UNM_49_CODE);
    }

    @Test
    void getAllDistrictsByUnm49CodeNotContainsSomething() {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();

        // Get all the districtList where unm49Code does not contain
        defaultDistrictFiltering("unm49Code.doesNotContain=" + UPDATED_UNM_49_CODE, "unm49Code.doesNotContain=" + DEFAULT_UNM_49_CODE);
    }

    @Test
    void getAllDistrictsByIsoAlpha2CodeIsEqualToSomething() {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();

        // Get all the districtList where isoAlpha2Code equals to
        defaultDistrictFiltering("isoAlpha2Code.equals=" + DEFAULT_ISO_ALPHA_2_CODE, "isoAlpha2Code.equals=" + UPDATED_ISO_ALPHA_2_CODE);
    }

    @Test
    void getAllDistrictsByIsoAlpha2CodeIsInShouldWork() {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();

        // Get all the districtList where isoAlpha2Code in
        defaultDistrictFiltering(
            "isoAlpha2Code.in=" + DEFAULT_ISO_ALPHA_2_CODE + "," + UPDATED_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.in=" + UPDATED_ISO_ALPHA_2_CODE
        );
    }

    @Test
    void getAllDistrictsByIsoAlpha2CodeIsNullOrNotNull() {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();

        // Get all the districtList where isoAlpha2Code is not null
        defaultDistrictFiltering("isoAlpha2Code.specified=true", "isoAlpha2Code.specified=false");
    }

    @Test
    void getAllDistrictsByIsoAlpha2CodeContainsSomething() {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();

        // Get all the districtList where isoAlpha2Code contains
        defaultDistrictFiltering(
            "isoAlpha2Code.contains=" + DEFAULT_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.contains=" + UPDATED_ISO_ALPHA_2_CODE
        );
    }

    @Test
    void getAllDistrictsByIsoAlpha2CodeNotContainsSomething() {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();

        // Get all the districtList where isoAlpha2Code does not contain
        defaultDistrictFiltering(
            "isoAlpha2Code.doesNotContain=" + UPDATED_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.doesNotContain=" + DEFAULT_ISO_ALPHA_2_CODE
        );
    }

    @Test
    void getAllDistrictsByCityIsEqualToSomething() {
        City city = CityResourceIT.createEntity(em);
        cityRepository.save(city).block();
        Long cityId = city.getId();
        district.setCityId(cityId);
        insertedDistrict = districtRepository.save(district).block();
        // Get all the districtList where city equals to cityId
        defaultDistrictShouldBeFound("cityId.equals=" + cityId);

        // Get all the districtList where city equals to (cityId + 1)
        defaultDistrictShouldNotBeFound("cityId.equals=" + (cityId + 1));
    }

    private void defaultDistrictFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultDistrictShouldBeFound(shouldBeFound);
        defaultDistrictShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDistrictShouldBeFound(String filter) {
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
            .value(hasItem(district.getId().intValue()))
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
    private void defaultDistrictShouldNotBeFound(String filter) {
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
    void getNonExistingDistrict() {
        // Get the district
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingDistrict() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        districtSearchRepository.save(district).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());

        // Update the district
        District updatedDistrict = districtRepository.findById(district.getId()).block();
        updatedDistrict.name(UPDATED_NAME).unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);
        DistrictDTO districtDTO = districtMapper.toDto(updatedDistrict);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, districtDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(districtDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the District in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDistrictToMatchAllProperties(updatedDistrict);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<District> districtSearchList = Streamable.of(districtSearchRepository.findAll().collectList().block()).toList();
                District testDistrictSearch = districtSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertDistrictAllPropertiesEquals(testDistrictSearch, updatedDistrict);
                assertDistrictUpdatableFieldsEquals(testDistrictSearch, updatedDistrict);
            });
    }

    @Test
    void putNonExistingDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        district.setId(longCount.incrementAndGet());

        // Create the District
        DistrictDTO districtDTO = districtMapper.toDto(district);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, districtDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(districtDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the District in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        district.setId(longCount.incrementAndGet());

        // Create the District
        DistrictDTO districtDTO = districtMapper.toDto(district);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(districtDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the District in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        district.setId(longCount.incrementAndGet());

        // Create the District
        DistrictDTO districtDTO = districtMapper.toDto(district);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(districtDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the District in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateDistrictWithPatch() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the district using partial update
        District partialUpdatedDistrict = new District();
        partialUpdatedDistrict.setId(district.getId());

        partialUpdatedDistrict.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDistrict.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDistrict))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the District in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDistrictUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedDistrict, district), getPersistedDistrict(district));
    }

    @Test
    void fullUpdateDistrictWithPatch() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the district using partial update
        District partialUpdatedDistrict = new District();
        partialUpdatedDistrict.setId(district.getId());

        partialUpdatedDistrict.name(UPDATED_NAME).unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDistrict.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDistrict))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the District in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDistrictUpdatableFieldsEquals(partialUpdatedDistrict, getPersistedDistrict(partialUpdatedDistrict));
    }

    @Test
    void patchNonExistingDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        district.setId(longCount.incrementAndGet());

        // Create the District
        DistrictDTO districtDTO = districtMapper.toDto(district);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, districtDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(districtDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the District in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        district.setId(longCount.incrementAndGet());

        // Create the District
        DistrictDTO districtDTO = districtMapper.toDto(district);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(districtDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the District in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        district.setId(longCount.incrementAndGet());

        // Create the District
        DistrictDTO districtDTO = districtMapper.toDto(district);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(districtDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the District in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteDistrict() {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();
        districtRepository.save(district).block();
        districtSearchRepository.save(district).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the district
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, district.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(districtSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchDistrict() {
        // Initialize the database
        insertedDistrict = districtRepository.save(district).block();
        districtSearchRepository.save(district).block();

        // Search the district
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + district.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(district.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].unm49Code")
            .value(hasItem(DEFAULT_UNM_49_CODE))
            .jsonPath("$.[*].isoAlpha2Code")
            .value(hasItem(DEFAULT_ISO_ALPHA_2_CODE));
    }

    protected long getRepositoryCount() {
        return districtRepository.count().block();
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

    protected District getPersistedDistrict(District district) {
        return districtRepository.findById(district.getId()).block();
    }

    protected void assertPersistedDistrictToMatchAllProperties(District expectedDistrict) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDistrictAllPropertiesEquals(expectedDistrict, getPersistedDistrict(expectedDistrict));
        assertDistrictUpdatableFieldsEquals(expectedDistrict, getPersistedDistrict(expectedDistrict));
    }

    protected void assertPersistedDistrictToMatchUpdatableProperties(District expectedDistrict) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDistrictAllUpdatablePropertiesEquals(expectedDistrict, getPersistedDistrict(expectedDistrict));
        assertDistrictUpdatableFieldsEquals(expectedDistrict, getPersistedDistrict(expectedDistrict));
    }
}
