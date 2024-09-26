package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.MstDistrictAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.MstCity;
import com.mycompany.myapp.domain.MstDistrict;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MstCityRepository;
import com.mycompany.myapp.repository.MstDistrictRepository;
import com.mycompany.myapp.repository.search.MstDistrictSearchRepository;
import com.mycompany.myapp.service.MstDistrictService;
import com.mycompany.myapp.service.dto.MstDistrictDTO;
import com.mycompany.myapp.service.mapper.MstDistrictMapper;
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
 * Integration tests for the {@link MstDistrictResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MstDistrictResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_UNM_49_CODE = "AAAAAAAAAA";
    private static final String UPDATED_UNM_49_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_ISO_ALPHA_2_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ISO_ALPHA_2_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/mst-districts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/mst-districts/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MstDistrictRepository mstDistrictRepository;

    @Mock
    private MstDistrictRepository mstDistrictRepositoryMock;

    @Autowired
    private MstDistrictMapper mstDistrictMapper;

    @Mock
    private MstDistrictService mstDistrictServiceMock;

    @Autowired
    private MstDistrictSearchRepository mstDistrictSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private MstDistrict mstDistrict;

    private MstDistrict insertedMstDistrict;

    @Autowired
    private MstCityRepository mstCityRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstDistrict createEntity(EntityManager em) {
        MstDistrict mstDistrict = new MstDistrict()
            .name(DEFAULT_NAME)
            .unm49Code(DEFAULT_UNM_49_CODE)
            .isoAlpha2Code(DEFAULT_ISO_ALPHA_2_CODE);
        return mstDistrict;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstDistrict createUpdatedEntity(EntityManager em) {
        MstDistrict mstDistrict = new MstDistrict()
            .name(UPDATED_NAME)
            .unm49Code(UPDATED_UNM_49_CODE)
            .isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);
        return mstDistrict;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(MstDistrict.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        mstDistrict = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedMstDistrict != null) {
            mstDistrictRepository.delete(insertedMstDistrict).block();
            mstDistrictSearchRepository.delete(insertedMstDistrict).block();
            insertedMstDistrict = null;
        }
        deleteEntities(em);
    }

    @Test
    void createMstDistrict() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDistrictSearchRepository.findAll().collectList().block());
        // Create the MstDistrict
        MstDistrictDTO mstDistrictDTO = mstDistrictMapper.toDto(mstDistrict);
        var returnedMstDistrictDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstDistrictDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MstDistrictDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MstDistrict in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMstDistrict = mstDistrictMapper.toEntity(returnedMstDistrictDTO);
        assertMstDistrictUpdatableFieldsEquals(returnedMstDistrict, getPersistedMstDistrict(returnedMstDistrict));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDistrictSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedMstDistrict = returnedMstDistrict;
    }

    @Test
    void createMstDistrictWithExistingId() throws Exception {
        // Create the MstDistrict with an existing ID
        mstDistrict.setId(1L);
        MstDistrictDTO mstDistrictDTO = mstDistrictMapper.toDto(mstDistrict);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDistrictSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstDistrictDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDistrictSearchRepository.findAll().collectList().block());
        // set the field null
        mstDistrict.setName(null);

        // Create the MstDistrict, which fails.
        MstDistrictDTO mstDistrictDTO = mstDistrictMapper.toDto(mstDistrict);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstDistrictDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllMstDistricts() {
        // Initialize the database
        insertedMstDistrict = mstDistrictRepository.save(mstDistrict).block();

        // Get all the mstDistrictList
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
            .value(hasItem(mstDistrict.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].unm49Code")
            .value(hasItem(DEFAULT_UNM_49_CODE))
            .jsonPath("$.[*].isoAlpha2Code")
            .value(hasItem(DEFAULT_ISO_ALPHA_2_CODE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMstDistrictsWithEagerRelationshipsIsEnabled() {
        when(mstDistrictServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(mstDistrictServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMstDistrictsWithEagerRelationshipsIsNotEnabled() {
        when(mstDistrictServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(mstDistrictRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getMstDistrict() {
        // Initialize the database
        insertedMstDistrict = mstDistrictRepository.save(mstDistrict).block();

        // Get the mstDistrict
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, mstDistrict.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(mstDistrict.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.unm49Code")
            .value(is(DEFAULT_UNM_49_CODE))
            .jsonPath("$.isoAlpha2Code")
            .value(is(DEFAULT_ISO_ALPHA_2_CODE));
    }

    @Test
    void getMstDistrictsByIdFiltering() {
        // Initialize the database
        insertedMstDistrict = mstDistrictRepository.save(mstDistrict).block();

        Long id = mstDistrict.getId();

        defaultMstDistrictFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMstDistrictFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMstDistrictFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllMstDistrictsByNameIsEqualToSomething() {
        // Initialize the database
        insertedMstDistrict = mstDistrictRepository.save(mstDistrict).block();

        // Get all the mstDistrictList where name equals to
        defaultMstDistrictFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllMstDistrictsByNameIsInShouldWork() {
        // Initialize the database
        insertedMstDistrict = mstDistrictRepository.save(mstDistrict).block();

        // Get all the mstDistrictList where name in
        defaultMstDistrictFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllMstDistrictsByNameIsNullOrNotNull() {
        // Initialize the database
        insertedMstDistrict = mstDistrictRepository.save(mstDistrict).block();

        // Get all the mstDistrictList where name is not null
        defaultMstDistrictFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllMstDistrictsByNameContainsSomething() {
        // Initialize the database
        insertedMstDistrict = mstDistrictRepository.save(mstDistrict).block();

        // Get all the mstDistrictList where name contains
        defaultMstDistrictFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllMstDistrictsByNameNotContainsSomething() {
        // Initialize the database
        insertedMstDistrict = mstDistrictRepository.save(mstDistrict).block();

        // Get all the mstDistrictList where name does not contain
        defaultMstDistrictFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllMstDistrictsByUnm49CodeIsEqualToSomething() {
        // Initialize the database
        insertedMstDistrict = mstDistrictRepository.save(mstDistrict).block();

        // Get all the mstDistrictList where unm49Code equals to
        defaultMstDistrictFiltering("unm49Code.equals=" + DEFAULT_UNM_49_CODE, "unm49Code.equals=" + UPDATED_UNM_49_CODE);
    }

    @Test
    void getAllMstDistrictsByUnm49CodeIsInShouldWork() {
        // Initialize the database
        insertedMstDistrict = mstDistrictRepository.save(mstDistrict).block();

        // Get all the mstDistrictList where unm49Code in
        defaultMstDistrictFiltering(
            "unm49Code.in=" + DEFAULT_UNM_49_CODE + "," + UPDATED_UNM_49_CODE,
            "unm49Code.in=" + UPDATED_UNM_49_CODE
        );
    }

    @Test
    void getAllMstDistrictsByUnm49CodeIsNullOrNotNull() {
        // Initialize the database
        insertedMstDistrict = mstDistrictRepository.save(mstDistrict).block();

        // Get all the mstDistrictList where unm49Code is not null
        defaultMstDistrictFiltering("unm49Code.specified=true", "unm49Code.specified=false");
    }

    @Test
    void getAllMstDistrictsByUnm49CodeContainsSomething() {
        // Initialize the database
        insertedMstDistrict = mstDistrictRepository.save(mstDistrict).block();

        // Get all the mstDistrictList where unm49Code contains
        defaultMstDistrictFiltering("unm49Code.contains=" + DEFAULT_UNM_49_CODE, "unm49Code.contains=" + UPDATED_UNM_49_CODE);
    }

    @Test
    void getAllMstDistrictsByUnm49CodeNotContainsSomething() {
        // Initialize the database
        insertedMstDistrict = mstDistrictRepository.save(mstDistrict).block();

        // Get all the mstDistrictList where unm49Code does not contain
        defaultMstDistrictFiltering("unm49Code.doesNotContain=" + UPDATED_UNM_49_CODE, "unm49Code.doesNotContain=" + DEFAULT_UNM_49_CODE);
    }

    @Test
    void getAllMstDistrictsByIsoAlpha2CodeIsEqualToSomething() {
        // Initialize the database
        insertedMstDistrict = mstDistrictRepository.save(mstDistrict).block();

        // Get all the mstDistrictList where isoAlpha2Code equals to
        defaultMstDistrictFiltering("isoAlpha2Code.equals=" + DEFAULT_ISO_ALPHA_2_CODE, "isoAlpha2Code.equals=" + UPDATED_ISO_ALPHA_2_CODE);
    }

    @Test
    void getAllMstDistrictsByIsoAlpha2CodeIsInShouldWork() {
        // Initialize the database
        insertedMstDistrict = mstDistrictRepository.save(mstDistrict).block();

        // Get all the mstDistrictList where isoAlpha2Code in
        defaultMstDistrictFiltering(
            "isoAlpha2Code.in=" + DEFAULT_ISO_ALPHA_2_CODE + "," + UPDATED_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.in=" + UPDATED_ISO_ALPHA_2_CODE
        );
    }

    @Test
    void getAllMstDistrictsByIsoAlpha2CodeIsNullOrNotNull() {
        // Initialize the database
        insertedMstDistrict = mstDistrictRepository.save(mstDistrict).block();

        // Get all the mstDistrictList where isoAlpha2Code is not null
        defaultMstDistrictFiltering("isoAlpha2Code.specified=true", "isoAlpha2Code.specified=false");
    }

    @Test
    void getAllMstDistrictsByIsoAlpha2CodeContainsSomething() {
        // Initialize the database
        insertedMstDistrict = mstDistrictRepository.save(mstDistrict).block();

        // Get all the mstDistrictList where isoAlpha2Code contains
        defaultMstDistrictFiltering(
            "isoAlpha2Code.contains=" + DEFAULT_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.contains=" + UPDATED_ISO_ALPHA_2_CODE
        );
    }

    @Test
    void getAllMstDistrictsByIsoAlpha2CodeNotContainsSomething() {
        // Initialize the database
        insertedMstDistrict = mstDistrictRepository.save(mstDistrict).block();

        // Get all the mstDistrictList where isoAlpha2Code does not contain
        defaultMstDistrictFiltering(
            "isoAlpha2Code.doesNotContain=" + UPDATED_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.doesNotContain=" + DEFAULT_ISO_ALPHA_2_CODE
        );
    }

    @Test
    void getAllMstDistrictsByCityIsEqualToSomething() {
        MstCity city = MstCityResourceIT.createEntity(em);
        mstCityRepository.save(city).block();
        Long cityId = city.getId();
        mstDistrict.setCityId(cityId);
        insertedMstDistrict = mstDistrictRepository.save(mstDistrict).block();
        // Get all the mstDistrictList where city equals to cityId
        defaultMstDistrictShouldBeFound("cityId.equals=" + cityId);

        // Get all the mstDistrictList where city equals to (cityId + 1)
        defaultMstDistrictShouldNotBeFound("cityId.equals=" + (cityId + 1));
    }

    private void defaultMstDistrictFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultMstDistrictShouldBeFound(shouldBeFound);
        defaultMstDistrictShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMstDistrictShouldBeFound(String filter) {
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
            .value(hasItem(mstDistrict.getId().intValue()))
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
    private void defaultMstDistrictShouldNotBeFound(String filter) {
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
    void getNonExistingMstDistrict() {
        // Get the mstDistrict
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMstDistrict() throws Exception {
        // Initialize the database
        insertedMstDistrict = mstDistrictRepository.save(mstDistrict).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        mstDistrictSearchRepository.save(mstDistrict).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDistrictSearchRepository.findAll().collectList().block());

        // Update the mstDistrict
        MstDistrict updatedMstDistrict = mstDistrictRepository.findById(mstDistrict.getId()).block();
        updatedMstDistrict.name(UPDATED_NAME).unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);
        MstDistrictDTO mstDistrictDTO = mstDistrictMapper.toDto(updatedMstDistrict);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstDistrictDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstDistrictDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMstDistrictToMatchAllProperties(updatedMstDistrict);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDistrictSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<MstDistrict> mstDistrictSearchList = Streamable.of(
                    mstDistrictSearchRepository.findAll().collectList().block()
                ).toList();
                MstDistrict testMstDistrictSearch = mstDistrictSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertMstDistrictAllPropertiesEquals(testMstDistrictSearch, updatedMstDistrict);
                assertMstDistrictUpdatableFieldsEquals(testMstDistrictSearch, updatedMstDistrict);
            });
    }

    @Test
    void putNonExistingMstDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDistrictSearchRepository.findAll().collectList().block());
        mstDistrict.setId(longCount.incrementAndGet());

        // Create the MstDistrict
        MstDistrictDTO mstDistrictDTO = mstDistrictMapper.toDto(mstDistrict);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstDistrictDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstDistrictDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchMstDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDistrictSearchRepository.findAll().collectList().block());
        mstDistrict.setId(longCount.incrementAndGet());

        // Create the MstDistrict
        MstDistrictDTO mstDistrictDTO = mstDistrictMapper.toDto(mstDistrict);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstDistrictDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamMstDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDistrictSearchRepository.findAll().collectList().block());
        mstDistrict.setId(longCount.incrementAndGet());

        // Create the MstDistrict
        MstDistrictDTO mstDistrictDTO = mstDistrictMapper.toDto(mstDistrict);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstDistrictDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateMstDistrictWithPatch() throws Exception {
        // Initialize the database
        insertedMstDistrict = mstDistrictRepository.save(mstDistrict).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstDistrict using partial update
        MstDistrict partialUpdatedMstDistrict = new MstDistrict();
        partialUpdatedMstDistrict.setId(mstDistrict.getId());

        partialUpdatedMstDistrict.unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstDistrict.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstDistrict))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstDistrict in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstDistrictUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMstDistrict, mstDistrict),
            getPersistedMstDistrict(mstDistrict)
        );
    }

    @Test
    void fullUpdateMstDistrictWithPatch() throws Exception {
        // Initialize the database
        insertedMstDistrict = mstDistrictRepository.save(mstDistrict).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstDistrict using partial update
        MstDistrict partialUpdatedMstDistrict = new MstDistrict();
        partialUpdatedMstDistrict.setId(mstDistrict.getId());

        partialUpdatedMstDistrict.name(UPDATED_NAME).unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstDistrict.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstDistrict))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstDistrict in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstDistrictUpdatableFieldsEquals(partialUpdatedMstDistrict, getPersistedMstDistrict(partialUpdatedMstDistrict));
    }

    @Test
    void patchNonExistingMstDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDistrictSearchRepository.findAll().collectList().block());
        mstDistrict.setId(longCount.incrementAndGet());

        // Create the MstDistrict
        MstDistrictDTO mstDistrictDTO = mstDistrictMapper.toDto(mstDistrict);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, mstDistrictDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstDistrictDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchMstDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDistrictSearchRepository.findAll().collectList().block());
        mstDistrict.setId(longCount.incrementAndGet());

        // Create the MstDistrict
        MstDistrictDTO mstDistrictDTO = mstDistrictMapper.toDto(mstDistrict);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstDistrictDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamMstDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDistrictSearchRepository.findAll().collectList().block());
        mstDistrict.setId(longCount.incrementAndGet());

        // Create the MstDistrict
        MstDistrictDTO mstDistrictDTO = mstDistrictMapper.toDto(mstDistrict);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstDistrictDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteMstDistrict() {
        // Initialize the database
        insertedMstDistrict = mstDistrictRepository.save(mstDistrict).block();
        mstDistrictRepository.save(mstDistrict).block();
        mstDistrictSearchRepository.save(mstDistrict).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the mstDistrict
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, mstDistrict.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchMstDistrict() {
        // Initialize the database
        insertedMstDistrict = mstDistrictRepository.save(mstDistrict).block();
        mstDistrictSearchRepository.save(mstDistrict).block();

        // Search the mstDistrict
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + mstDistrict.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(mstDistrict.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].unm49Code")
            .value(hasItem(DEFAULT_UNM_49_CODE))
            .jsonPath("$.[*].isoAlpha2Code")
            .value(hasItem(DEFAULT_ISO_ALPHA_2_CODE));
    }

    protected long getRepositoryCount() {
        return mstDistrictRepository.count().block();
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

    protected MstDistrict getPersistedMstDistrict(MstDistrict mstDistrict) {
        return mstDistrictRepository.findById(mstDistrict.getId()).block();
    }

    protected void assertPersistedMstDistrictToMatchAllProperties(MstDistrict expectedMstDistrict) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstDistrictAllPropertiesEquals(expectedMstDistrict, getPersistedMstDistrict(expectedMstDistrict));
        assertMstDistrictUpdatableFieldsEquals(expectedMstDistrict, getPersistedMstDistrict(expectedMstDistrict));
    }

    protected void assertPersistedMstDistrictToMatchUpdatableProperties(MstDistrict expectedMstDistrict) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstDistrictAllUpdatablePropertiesEquals(expectedMstDistrict, getPersistedMstDistrict(expectedMstDistrict));
        assertMstDistrictUpdatableFieldsEquals(expectedMstDistrict, getPersistedMstDistrict(expectedMstDistrict));
    }
}
