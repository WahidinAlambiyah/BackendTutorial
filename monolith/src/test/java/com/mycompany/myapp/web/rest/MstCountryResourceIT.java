package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.MstCountryAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.MstCountry;
import com.mycompany.myapp.domain.MstRegion;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MstCountryRepository;
import com.mycompany.myapp.repository.MstRegionRepository;
import com.mycompany.myapp.repository.search.MstCountrySearchRepository;
import com.mycompany.myapp.service.MstCountryService;
import com.mycompany.myapp.service.dto.MstCountryDTO;
import com.mycompany.myapp.service.mapper.MstCountryMapper;
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
 * Integration tests for the {@link MstCountryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MstCountryResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_UNM_49_CODE = "AAAAAAAAAA";
    private static final String UPDATED_UNM_49_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_ISO_ALPHA_2_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ISO_ALPHA_2_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/mst-countries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/mst-countries/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MstCountryRepository mstCountryRepository;

    @Mock
    private MstCountryRepository mstCountryRepositoryMock;

    @Autowired
    private MstCountryMapper mstCountryMapper;

    @Mock
    private MstCountryService mstCountryServiceMock;

    @Autowired
    private MstCountrySearchRepository mstCountrySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private MstCountry mstCountry;

    private MstCountry insertedMstCountry;

    @Autowired
    private MstRegionRepository mstRegionRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstCountry createEntity(EntityManager em) {
        MstCountry mstCountry = new MstCountry().name(DEFAULT_NAME).unm49Code(DEFAULT_UNM_49_CODE).isoAlpha2Code(DEFAULT_ISO_ALPHA_2_CODE);
        return mstCountry;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstCountry createUpdatedEntity(EntityManager em) {
        MstCountry mstCountry = new MstCountry().name(UPDATED_NAME).unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);
        return mstCountry;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(MstCountry.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        mstCountry = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedMstCountry != null) {
            mstCountryRepository.delete(insertedMstCountry).block();
            mstCountrySearchRepository.delete(insertedMstCountry).block();
            insertedMstCountry = null;
        }
        deleteEntities(em);
    }

    @Test
    void createMstCountry() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCountrySearchRepository.findAll().collectList().block());
        // Create the MstCountry
        MstCountryDTO mstCountryDTO = mstCountryMapper.toDto(mstCountry);
        var returnedMstCountryDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCountryDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MstCountryDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MstCountry in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMstCountry = mstCountryMapper.toEntity(returnedMstCountryDTO);
        assertMstCountryUpdatableFieldsEquals(returnedMstCountry, getPersistedMstCountry(returnedMstCountry));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCountrySearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedMstCountry = returnedMstCountry;
    }

    @Test
    void createMstCountryWithExistingId() throws Exception {
        // Create the MstCountry with an existing ID
        mstCountry.setId(1L);
        MstCountryDTO mstCountryDTO = mstCountryMapper.toDto(mstCountry);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCountrySearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCountryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstCountry in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCountrySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCountrySearchRepository.findAll().collectList().block());
        // set the field null
        mstCountry.setName(null);

        // Create the MstCountry, which fails.
        MstCountryDTO mstCountryDTO = mstCountryMapper.toDto(mstCountry);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCountryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCountrySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllMstCountries() {
        // Initialize the database
        insertedMstCountry = mstCountryRepository.save(mstCountry).block();

        // Get all the mstCountryList
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
            .value(hasItem(mstCountry.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].unm49Code")
            .value(hasItem(DEFAULT_UNM_49_CODE))
            .jsonPath("$.[*].isoAlpha2Code")
            .value(hasItem(DEFAULT_ISO_ALPHA_2_CODE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMstCountriesWithEagerRelationshipsIsEnabled() {
        when(mstCountryServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(mstCountryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMstCountriesWithEagerRelationshipsIsNotEnabled() {
        when(mstCountryServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(mstCountryRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getMstCountry() {
        // Initialize the database
        insertedMstCountry = mstCountryRepository.save(mstCountry).block();

        // Get the mstCountry
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, mstCountry.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(mstCountry.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.unm49Code")
            .value(is(DEFAULT_UNM_49_CODE))
            .jsonPath("$.isoAlpha2Code")
            .value(is(DEFAULT_ISO_ALPHA_2_CODE));
    }

    @Test
    void getMstCountriesByIdFiltering() {
        // Initialize the database
        insertedMstCountry = mstCountryRepository.save(mstCountry).block();

        Long id = mstCountry.getId();

        defaultMstCountryFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMstCountryFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMstCountryFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllMstCountriesByNameIsEqualToSomething() {
        // Initialize the database
        insertedMstCountry = mstCountryRepository.save(mstCountry).block();

        // Get all the mstCountryList where name equals to
        defaultMstCountryFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllMstCountriesByNameIsInShouldWork() {
        // Initialize the database
        insertedMstCountry = mstCountryRepository.save(mstCountry).block();

        // Get all the mstCountryList where name in
        defaultMstCountryFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllMstCountriesByNameIsNullOrNotNull() {
        // Initialize the database
        insertedMstCountry = mstCountryRepository.save(mstCountry).block();

        // Get all the mstCountryList where name is not null
        defaultMstCountryFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllMstCountriesByNameContainsSomething() {
        // Initialize the database
        insertedMstCountry = mstCountryRepository.save(mstCountry).block();

        // Get all the mstCountryList where name contains
        defaultMstCountryFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllMstCountriesByNameNotContainsSomething() {
        // Initialize the database
        insertedMstCountry = mstCountryRepository.save(mstCountry).block();

        // Get all the mstCountryList where name does not contain
        defaultMstCountryFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllMstCountriesByUnm49CodeIsEqualToSomething() {
        // Initialize the database
        insertedMstCountry = mstCountryRepository.save(mstCountry).block();

        // Get all the mstCountryList where unm49Code equals to
        defaultMstCountryFiltering("unm49Code.equals=" + DEFAULT_UNM_49_CODE, "unm49Code.equals=" + UPDATED_UNM_49_CODE);
    }

    @Test
    void getAllMstCountriesByUnm49CodeIsInShouldWork() {
        // Initialize the database
        insertedMstCountry = mstCountryRepository.save(mstCountry).block();

        // Get all the mstCountryList where unm49Code in
        defaultMstCountryFiltering(
            "unm49Code.in=" + DEFAULT_UNM_49_CODE + "," + UPDATED_UNM_49_CODE,
            "unm49Code.in=" + UPDATED_UNM_49_CODE
        );
    }

    @Test
    void getAllMstCountriesByUnm49CodeIsNullOrNotNull() {
        // Initialize the database
        insertedMstCountry = mstCountryRepository.save(mstCountry).block();

        // Get all the mstCountryList where unm49Code is not null
        defaultMstCountryFiltering("unm49Code.specified=true", "unm49Code.specified=false");
    }

    @Test
    void getAllMstCountriesByUnm49CodeContainsSomething() {
        // Initialize the database
        insertedMstCountry = mstCountryRepository.save(mstCountry).block();

        // Get all the mstCountryList where unm49Code contains
        defaultMstCountryFiltering("unm49Code.contains=" + DEFAULT_UNM_49_CODE, "unm49Code.contains=" + UPDATED_UNM_49_CODE);
    }

    @Test
    void getAllMstCountriesByUnm49CodeNotContainsSomething() {
        // Initialize the database
        insertedMstCountry = mstCountryRepository.save(mstCountry).block();

        // Get all the mstCountryList where unm49Code does not contain
        defaultMstCountryFiltering("unm49Code.doesNotContain=" + UPDATED_UNM_49_CODE, "unm49Code.doesNotContain=" + DEFAULT_UNM_49_CODE);
    }

    @Test
    void getAllMstCountriesByIsoAlpha2CodeIsEqualToSomething() {
        // Initialize the database
        insertedMstCountry = mstCountryRepository.save(mstCountry).block();

        // Get all the mstCountryList where isoAlpha2Code equals to
        defaultMstCountryFiltering("isoAlpha2Code.equals=" + DEFAULT_ISO_ALPHA_2_CODE, "isoAlpha2Code.equals=" + UPDATED_ISO_ALPHA_2_CODE);
    }

    @Test
    void getAllMstCountriesByIsoAlpha2CodeIsInShouldWork() {
        // Initialize the database
        insertedMstCountry = mstCountryRepository.save(mstCountry).block();

        // Get all the mstCountryList where isoAlpha2Code in
        defaultMstCountryFiltering(
            "isoAlpha2Code.in=" + DEFAULT_ISO_ALPHA_2_CODE + "," + UPDATED_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.in=" + UPDATED_ISO_ALPHA_2_CODE
        );
    }

    @Test
    void getAllMstCountriesByIsoAlpha2CodeIsNullOrNotNull() {
        // Initialize the database
        insertedMstCountry = mstCountryRepository.save(mstCountry).block();

        // Get all the mstCountryList where isoAlpha2Code is not null
        defaultMstCountryFiltering("isoAlpha2Code.specified=true", "isoAlpha2Code.specified=false");
    }

    @Test
    void getAllMstCountriesByIsoAlpha2CodeContainsSomething() {
        // Initialize the database
        insertedMstCountry = mstCountryRepository.save(mstCountry).block();

        // Get all the mstCountryList where isoAlpha2Code contains
        defaultMstCountryFiltering(
            "isoAlpha2Code.contains=" + DEFAULT_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.contains=" + UPDATED_ISO_ALPHA_2_CODE
        );
    }

    @Test
    void getAllMstCountriesByIsoAlpha2CodeNotContainsSomething() {
        // Initialize the database
        insertedMstCountry = mstCountryRepository.save(mstCountry).block();

        // Get all the mstCountryList where isoAlpha2Code does not contain
        defaultMstCountryFiltering(
            "isoAlpha2Code.doesNotContain=" + UPDATED_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.doesNotContain=" + DEFAULT_ISO_ALPHA_2_CODE
        );
    }

    @Test
    void getAllMstCountriesByRegionIsEqualToSomething() {
        MstRegion region = MstRegionResourceIT.createEntity(em);
        mstRegionRepository.save(region).block();
        Long regionId = region.getId();
        mstCountry.setRegionId(regionId);
        insertedMstCountry = mstCountryRepository.save(mstCountry).block();
        // Get all the mstCountryList where region equals to regionId
        defaultMstCountryShouldBeFound("regionId.equals=" + regionId);

        // Get all the mstCountryList where region equals to (regionId + 1)
        defaultMstCountryShouldNotBeFound("regionId.equals=" + (regionId + 1));
    }

    private void defaultMstCountryFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultMstCountryShouldBeFound(shouldBeFound);
        defaultMstCountryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMstCountryShouldBeFound(String filter) {
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
            .value(hasItem(mstCountry.getId().intValue()))
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
    private void defaultMstCountryShouldNotBeFound(String filter) {
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
    void getNonExistingMstCountry() {
        // Get the mstCountry
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMstCountry() throws Exception {
        // Initialize the database
        insertedMstCountry = mstCountryRepository.save(mstCountry).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        mstCountrySearchRepository.save(mstCountry).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCountrySearchRepository.findAll().collectList().block());

        // Update the mstCountry
        MstCountry updatedMstCountry = mstCountryRepository.findById(mstCountry.getId()).block();
        updatedMstCountry.name(UPDATED_NAME).unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);
        MstCountryDTO mstCountryDTO = mstCountryMapper.toDto(updatedMstCountry);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstCountryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCountryDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstCountry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMstCountryToMatchAllProperties(updatedMstCountry);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCountrySearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<MstCountry> mstCountrySearchList = Streamable.of(mstCountrySearchRepository.findAll().collectList().block()).toList();
                MstCountry testMstCountrySearch = mstCountrySearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertMstCountryAllPropertiesEquals(testMstCountrySearch, updatedMstCountry);
                assertMstCountryUpdatableFieldsEquals(testMstCountrySearch, updatedMstCountry);
            });
    }

    @Test
    void putNonExistingMstCountry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCountrySearchRepository.findAll().collectList().block());
        mstCountry.setId(longCount.incrementAndGet());

        // Create the MstCountry
        MstCountryDTO mstCountryDTO = mstCountryMapper.toDto(mstCountry);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstCountryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCountryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstCountry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCountrySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchMstCountry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCountrySearchRepository.findAll().collectList().block());
        mstCountry.setId(longCount.incrementAndGet());

        // Create the MstCountry
        MstCountryDTO mstCountryDTO = mstCountryMapper.toDto(mstCountry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCountryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstCountry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCountrySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamMstCountry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCountrySearchRepository.findAll().collectList().block());
        mstCountry.setId(longCount.incrementAndGet());

        // Create the MstCountry
        MstCountryDTO mstCountryDTO = mstCountryMapper.toDto(mstCountry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstCountryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstCountry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCountrySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateMstCountryWithPatch() throws Exception {
        // Initialize the database
        insertedMstCountry = mstCountryRepository.save(mstCountry).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstCountry using partial update
        MstCountry partialUpdatedMstCountry = new MstCountry();
        partialUpdatedMstCountry.setId(mstCountry.getId());

        partialUpdatedMstCountry.name(UPDATED_NAME).unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstCountry.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstCountry))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstCountry in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstCountryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMstCountry, mstCountry),
            getPersistedMstCountry(mstCountry)
        );
    }

    @Test
    void fullUpdateMstCountryWithPatch() throws Exception {
        // Initialize the database
        insertedMstCountry = mstCountryRepository.save(mstCountry).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstCountry using partial update
        MstCountry partialUpdatedMstCountry = new MstCountry();
        partialUpdatedMstCountry.setId(mstCountry.getId());

        partialUpdatedMstCountry.name(UPDATED_NAME).unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstCountry.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstCountry))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstCountry in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstCountryUpdatableFieldsEquals(partialUpdatedMstCountry, getPersistedMstCountry(partialUpdatedMstCountry));
    }

    @Test
    void patchNonExistingMstCountry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCountrySearchRepository.findAll().collectList().block());
        mstCountry.setId(longCount.incrementAndGet());

        // Create the MstCountry
        MstCountryDTO mstCountryDTO = mstCountryMapper.toDto(mstCountry);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, mstCountryDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstCountryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstCountry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCountrySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchMstCountry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCountrySearchRepository.findAll().collectList().block());
        mstCountry.setId(longCount.incrementAndGet());

        // Create the MstCountry
        MstCountryDTO mstCountryDTO = mstCountryMapper.toDto(mstCountry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstCountryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstCountry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCountrySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamMstCountry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCountrySearchRepository.findAll().collectList().block());
        mstCountry.setId(longCount.incrementAndGet());

        // Create the MstCountry
        MstCountryDTO mstCountryDTO = mstCountryMapper.toDto(mstCountry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstCountryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstCountry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCountrySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteMstCountry() {
        // Initialize the database
        insertedMstCountry = mstCountryRepository.save(mstCountry).block();
        mstCountryRepository.save(mstCountry).block();
        mstCountrySearchRepository.save(mstCountry).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstCountrySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the mstCountry
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, mstCountry.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstCountrySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchMstCountry() {
        // Initialize the database
        insertedMstCountry = mstCountryRepository.save(mstCountry).block();
        mstCountrySearchRepository.save(mstCountry).block();

        // Search the mstCountry
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + mstCountry.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(mstCountry.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].unm49Code")
            .value(hasItem(DEFAULT_UNM_49_CODE))
            .jsonPath("$.[*].isoAlpha2Code")
            .value(hasItem(DEFAULT_ISO_ALPHA_2_CODE));
    }

    protected long getRepositoryCount() {
        return mstCountryRepository.count().block();
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

    protected MstCountry getPersistedMstCountry(MstCountry mstCountry) {
        return mstCountryRepository.findById(mstCountry.getId()).block();
    }

    protected void assertPersistedMstCountryToMatchAllProperties(MstCountry expectedMstCountry) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstCountryAllPropertiesEquals(expectedMstCountry, getPersistedMstCountry(expectedMstCountry));
        assertMstCountryUpdatableFieldsEquals(expectedMstCountry, getPersistedMstCountry(expectedMstCountry));
    }

    protected void assertPersistedMstCountryToMatchUpdatableProperties(MstCountry expectedMstCountry) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstCountryAllUpdatablePropertiesEquals(expectedMstCountry, getPersistedMstCountry(expectedMstCountry));
        assertMstCountryUpdatableFieldsEquals(expectedMstCountry, getPersistedMstCountry(expectedMstCountry));
    }
}
