package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.MstSubDistrictAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.MstDistrict;
import com.mycompany.myapp.domain.MstSubDistrict;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MstDistrictRepository;
import com.mycompany.myapp.repository.MstSubDistrictRepository;
import com.mycompany.myapp.repository.search.MstSubDistrictSearchRepository;
import com.mycompany.myapp.service.MstSubDistrictService;
import com.mycompany.myapp.service.dto.MstSubDistrictDTO;
import com.mycompany.myapp.service.mapper.MstSubDistrictMapper;
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
 * Integration tests for the {@link MstSubDistrictResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MstSubDistrictResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_UNM_49_CODE = "AAAAAAAAAA";
    private static final String UPDATED_UNM_49_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_ISO_ALPHA_2_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ISO_ALPHA_2_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/mst-sub-districts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/mst-sub-districts/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MstSubDistrictRepository mstSubDistrictRepository;

    @Mock
    private MstSubDistrictRepository mstSubDistrictRepositoryMock;

    @Autowired
    private MstSubDistrictMapper mstSubDistrictMapper;

    @Mock
    private MstSubDistrictService mstSubDistrictServiceMock;

    @Autowired
    private MstSubDistrictSearchRepository mstSubDistrictSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private MstSubDistrict mstSubDistrict;

    private MstSubDistrict insertedMstSubDistrict;

    @Autowired
    private MstDistrictRepository mstDistrictRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstSubDistrict createEntity(EntityManager em) {
        MstSubDistrict mstSubDistrict = new MstSubDistrict()
            .name(DEFAULT_NAME)
            .unm49Code(DEFAULT_UNM_49_CODE)
            .isoAlpha2Code(DEFAULT_ISO_ALPHA_2_CODE);
        return mstSubDistrict;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstSubDistrict createUpdatedEntity(EntityManager em) {
        MstSubDistrict mstSubDistrict = new MstSubDistrict()
            .name(UPDATED_NAME)
            .unm49Code(UPDATED_UNM_49_CODE)
            .isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);
        return mstSubDistrict;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(MstSubDistrict.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        mstSubDistrict = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedMstSubDistrict != null) {
            mstSubDistrictRepository.delete(insertedMstSubDistrict).block();
            mstSubDistrictSearchRepository.delete(insertedMstSubDistrict).block();
            insertedMstSubDistrict = null;
        }
        deleteEntities(em);
    }

    @Test
    void createMstSubDistrict() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstSubDistrictSearchRepository.findAll().collectList().block());
        // Create the MstSubDistrict
        MstSubDistrictDTO mstSubDistrictDTO = mstSubDistrictMapper.toDto(mstSubDistrict);
        var returnedMstSubDistrictDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstSubDistrictDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MstSubDistrictDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MstSubDistrict in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMstSubDistrict = mstSubDistrictMapper.toEntity(returnedMstSubDistrictDTO);
        assertMstSubDistrictUpdatableFieldsEquals(returnedMstSubDistrict, getPersistedMstSubDistrict(returnedMstSubDistrict));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstSubDistrictSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedMstSubDistrict = returnedMstSubDistrict;
    }

    @Test
    void createMstSubDistrictWithExistingId() throws Exception {
        // Create the MstSubDistrict with an existing ID
        mstSubDistrict.setId(1L);
        MstSubDistrictDTO mstSubDistrictDTO = mstSubDistrictMapper.toDto(mstSubDistrict);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstSubDistrictSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstSubDistrictDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstSubDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstSubDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstSubDistrictSearchRepository.findAll().collectList().block());
        // set the field null
        mstSubDistrict.setName(null);

        // Create the MstSubDistrict, which fails.
        MstSubDistrictDTO mstSubDistrictDTO = mstSubDistrictMapper.toDto(mstSubDistrict);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstSubDistrictDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstSubDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllMstSubDistricts() {
        // Initialize the database
        insertedMstSubDistrict = mstSubDistrictRepository.save(mstSubDistrict).block();

        // Get all the mstSubDistrictList
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
            .value(hasItem(mstSubDistrict.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].unm49Code")
            .value(hasItem(DEFAULT_UNM_49_CODE))
            .jsonPath("$.[*].isoAlpha2Code")
            .value(hasItem(DEFAULT_ISO_ALPHA_2_CODE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMstSubDistrictsWithEagerRelationshipsIsEnabled() {
        when(mstSubDistrictServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(mstSubDistrictServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMstSubDistrictsWithEagerRelationshipsIsNotEnabled() {
        when(mstSubDistrictServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(mstSubDistrictRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getMstSubDistrict() {
        // Initialize the database
        insertedMstSubDistrict = mstSubDistrictRepository.save(mstSubDistrict).block();

        // Get the mstSubDistrict
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, mstSubDistrict.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(mstSubDistrict.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.unm49Code")
            .value(is(DEFAULT_UNM_49_CODE))
            .jsonPath("$.isoAlpha2Code")
            .value(is(DEFAULT_ISO_ALPHA_2_CODE));
    }

    @Test
    void getMstSubDistrictsByIdFiltering() {
        // Initialize the database
        insertedMstSubDistrict = mstSubDistrictRepository.save(mstSubDistrict).block();

        Long id = mstSubDistrict.getId();

        defaultMstSubDistrictFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMstSubDistrictFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMstSubDistrictFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllMstSubDistrictsByNameIsEqualToSomething() {
        // Initialize the database
        insertedMstSubDistrict = mstSubDistrictRepository.save(mstSubDistrict).block();

        // Get all the mstSubDistrictList where name equals to
        defaultMstSubDistrictFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllMstSubDistrictsByNameIsInShouldWork() {
        // Initialize the database
        insertedMstSubDistrict = mstSubDistrictRepository.save(mstSubDistrict).block();

        // Get all the mstSubDistrictList where name in
        defaultMstSubDistrictFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllMstSubDistrictsByNameIsNullOrNotNull() {
        // Initialize the database
        insertedMstSubDistrict = mstSubDistrictRepository.save(mstSubDistrict).block();

        // Get all the mstSubDistrictList where name is not null
        defaultMstSubDistrictFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllMstSubDistrictsByNameContainsSomething() {
        // Initialize the database
        insertedMstSubDistrict = mstSubDistrictRepository.save(mstSubDistrict).block();

        // Get all the mstSubDistrictList where name contains
        defaultMstSubDistrictFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllMstSubDistrictsByNameNotContainsSomething() {
        // Initialize the database
        insertedMstSubDistrict = mstSubDistrictRepository.save(mstSubDistrict).block();

        // Get all the mstSubDistrictList where name does not contain
        defaultMstSubDistrictFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllMstSubDistrictsByUnm49CodeIsEqualToSomething() {
        // Initialize the database
        insertedMstSubDistrict = mstSubDistrictRepository.save(mstSubDistrict).block();

        // Get all the mstSubDistrictList where unm49Code equals to
        defaultMstSubDistrictFiltering("unm49Code.equals=" + DEFAULT_UNM_49_CODE, "unm49Code.equals=" + UPDATED_UNM_49_CODE);
    }

    @Test
    void getAllMstSubDistrictsByUnm49CodeIsInShouldWork() {
        // Initialize the database
        insertedMstSubDistrict = mstSubDistrictRepository.save(mstSubDistrict).block();

        // Get all the mstSubDistrictList where unm49Code in
        defaultMstSubDistrictFiltering(
            "unm49Code.in=" + DEFAULT_UNM_49_CODE + "," + UPDATED_UNM_49_CODE,
            "unm49Code.in=" + UPDATED_UNM_49_CODE
        );
    }

    @Test
    void getAllMstSubDistrictsByUnm49CodeIsNullOrNotNull() {
        // Initialize the database
        insertedMstSubDistrict = mstSubDistrictRepository.save(mstSubDistrict).block();

        // Get all the mstSubDistrictList where unm49Code is not null
        defaultMstSubDistrictFiltering("unm49Code.specified=true", "unm49Code.specified=false");
    }

    @Test
    void getAllMstSubDistrictsByUnm49CodeContainsSomething() {
        // Initialize the database
        insertedMstSubDistrict = mstSubDistrictRepository.save(mstSubDistrict).block();

        // Get all the mstSubDistrictList where unm49Code contains
        defaultMstSubDistrictFiltering("unm49Code.contains=" + DEFAULT_UNM_49_CODE, "unm49Code.contains=" + UPDATED_UNM_49_CODE);
    }

    @Test
    void getAllMstSubDistrictsByUnm49CodeNotContainsSomething() {
        // Initialize the database
        insertedMstSubDistrict = mstSubDistrictRepository.save(mstSubDistrict).block();

        // Get all the mstSubDistrictList where unm49Code does not contain
        defaultMstSubDistrictFiltering(
            "unm49Code.doesNotContain=" + UPDATED_UNM_49_CODE,
            "unm49Code.doesNotContain=" + DEFAULT_UNM_49_CODE
        );
    }

    @Test
    void getAllMstSubDistrictsByIsoAlpha2CodeIsEqualToSomething() {
        // Initialize the database
        insertedMstSubDistrict = mstSubDistrictRepository.save(mstSubDistrict).block();

        // Get all the mstSubDistrictList where isoAlpha2Code equals to
        defaultMstSubDistrictFiltering(
            "isoAlpha2Code.equals=" + DEFAULT_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.equals=" + UPDATED_ISO_ALPHA_2_CODE
        );
    }

    @Test
    void getAllMstSubDistrictsByIsoAlpha2CodeIsInShouldWork() {
        // Initialize the database
        insertedMstSubDistrict = mstSubDistrictRepository.save(mstSubDistrict).block();

        // Get all the mstSubDistrictList where isoAlpha2Code in
        defaultMstSubDistrictFiltering(
            "isoAlpha2Code.in=" + DEFAULT_ISO_ALPHA_2_CODE + "," + UPDATED_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.in=" + UPDATED_ISO_ALPHA_2_CODE
        );
    }

    @Test
    void getAllMstSubDistrictsByIsoAlpha2CodeIsNullOrNotNull() {
        // Initialize the database
        insertedMstSubDistrict = mstSubDistrictRepository.save(mstSubDistrict).block();

        // Get all the mstSubDistrictList where isoAlpha2Code is not null
        defaultMstSubDistrictFiltering("isoAlpha2Code.specified=true", "isoAlpha2Code.specified=false");
    }

    @Test
    void getAllMstSubDistrictsByIsoAlpha2CodeContainsSomething() {
        // Initialize the database
        insertedMstSubDistrict = mstSubDistrictRepository.save(mstSubDistrict).block();

        // Get all the mstSubDistrictList where isoAlpha2Code contains
        defaultMstSubDistrictFiltering(
            "isoAlpha2Code.contains=" + DEFAULT_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.contains=" + UPDATED_ISO_ALPHA_2_CODE
        );
    }

    @Test
    void getAllMstSubDistrictsByIsoAlpha2CodeNotContainsSomething() {
        // Initialize the database
        insertedMstSubDistrict = mstSubDistrictRepository.save(mstSubDistrict).block();

        // Get all the mstSubDistrictList where isoAlpha2Code does not contain
        defaultMstSubDistrictFiltering(
            "isoAlpha2Code.doesNotContain=" + UPDATED_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.doesNotContain=" + DEFAULT_ISO_ALPHA_2_CODE
        );
    }

    @Test
    void getAllMstSubDistrictsByDistrictIsEqualToSomething() {
        MstDistrict district = MstDistrictResourceIT.createEntity(em);
        mstDistrictRepository.save(district).block();
        Long districtId = district.getId();
        mstSubDistrict.setDistrictId(districtId);
        insertedMstSubDistrict = mstSubDistrictRepository.save(mstSubDistrict).block();
        // Get all the mstSubDistrictList where district equals to districtId
        defaultMstSubDistrictShouldBeFound("districtId.equals=" + districtId);

        // Get all the mstSubDistrictList where district equals to (districtId + 1)
        defaultMstSubDistrictShouldNotBeFound("districtId.equals=" + (districtId + 1));
    }

    private void defaultMstSubDistrictFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultMstSubDistrictShouldBeFound(shouldBeFound);
        defaultMstSubDistrictShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMstSubDistrictShouldBeFound(String filter) {
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
            .value(hasItem(mstSubDistrict.getId().intValue()))
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
    private void defaultMstSubDistrictShouldNotBeFound(String filter) {
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
    void getNonExistingMstSubDistrict() {
        // Get the mstSubDistrict
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMstSubDistrict() throws Exception {
        // Initialize the database
        insertedMstSubDistrict = mstSubDistrictRepository.save(mstSubDistrict).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        mstSubDistrictSearchRepository.save(mstSubDistrict).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstSubDistrictSearchRepository.findAll().collectList().block());

        // Update the mstSubDistrict
        MstSubDistrict updatedMstSubDistrict = mstSubDistrictRepository.findById(mstSubDistrict.getId()).block();
        updatedMstSubDistrict.name(UPDATED_NAME).unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);
        MstSubDistrictDTO mstSubDistrictDTO = mstSubDistrictMapper.toDto(updatedMstSubDistrict);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstSubDistrictDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstSubDistrictDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstSubDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMstSubDistrictToMatchAllProperties(updatedMstSubDistrict);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstSubDistrictSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<MstSubDistrict> mstSubDistrictSearchList = Streamable.of(
                    mstSubDistrictSearchRepository.findAll().collectList().block()
                ).toList();
                MstSubDistrict testMstSubDistrictSearch = mstSubDistrictSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertMstSubDistrictAllPropertiesEquals(testMstSubDistrictSearch, updatedMstSubDistrict);
                assertMstSubDistrictUpdatableFieldsEquals(testMstSubDistrictSearch, updatedMstSubDistrict);
            });
    }

    @Test
    void putNonExistingMstSubDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstSubDistrictSearchRepository.findAll().collectList().block());
        mstSubDistrict.setId(longCount.incrementAndGet());

        // Create the MstSubDistrict
        MstSubDistrictDTO mstSubDistrictDTO = mstSubDistrictMapper.toDto(mstSubDistrict);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstSubDistrictDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstSubDistrictDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstSubDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstSubDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchMstSubDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstSubDistrictSearchRepository.findAll().collectList().block());
        mstSubDistrict.setId(longCount.incrementAndGet());

        // Create the MstSubDistrict
        MstSubDistrictDTO mstSubDistrictDTO = mstSubDistrictMapper.toDto(mstSubDistrict);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstSubDistrictDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstSubDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstSubDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamMstSubDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstSubDistrictSearchRepository.findAll().collectList().block());
        mstSubDistrict.setId(longCount.incrementAndGet());

        // Create the MstSubDistrict
        MstSubDistrictDTO mstSubDistrictDTO = mstSubDistrictMapper.toDto(mstSubDistrict);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstSubDistrictDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstSubDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstSubDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateMstSubDistrictWithPatch() throws Exception {
        // Initialize the database
        insertedMstSubDistrict = mstSubDistrictRepository.save(mstSubDistrict).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstSubDistrict using partial update
        MstSubDistrict partialUpdatedMstSubDistrict = new MstSubDistrict();
        partialUpdatedMstSubDistrict.setId(mstSubDistrict.getId());

        partialUpdatedMstSubDistrict.name(UPDATED_NAME).unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstSubDistrict.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstSubDistrict))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstSubDistrict in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstSubDistrictUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMstSubDistrict, mstSubDistrict),
            getPersistedMstSubDistrict(mstSubDistrict)
        );
    }

    @Test
    void fullUpdateMstSubDistrictWithPatch() throws Exception {
        // Initialize the database
        insertedMstSubDistrict = mstSubDistrictRepository.save(mstSubDistrict).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstSubDistrict using partial update
        MstSubDistrict partialUpdatedMstSubDistrict = new MstSubDistrict();
        partialUpdatedMstSubDistrict.setId(mstSubDistrict.getId());

        partialUpdatedMstSubDistrict.name(UPDATED_NAME).unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstSubDistrict.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstSubDistrict))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstSubDistrict in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstSubDistrictUpdatableFieldsEquals(partialUpdatedMstSubDistrict, getPersistedMstSubDistrict(partialUpdatedMstSubDistrict));
    }

    @Test
    void patchNonExistingMstSubDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstSubDistrictSearchRepository.findAll().collectList().block());
        mstSubDistrict.setId(longCount.incrementAndGet());

        // Create the MstSubDistrict
        MstSubDistrictDTO mstSubDistrictDTO = mstSubDistrictMapper.toDto(mstSubDistrict);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, mstSubDistrictDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstSubDistrictDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstSubDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstSubDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchMstSubDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstSubDistrictSearchRepository.findAll().collectList().block());
        mstSubDistrict.setId(longCount.incrementAndGet());

        // Create the MstSubDistrict
        MstSubDistrictDTO mstSubDistrictDTO = mstSubDistrictMapper.toDto(mstSubDistrict);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstSubDistrictDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstSubDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstSubDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamMstSubDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstSubDistrictSearchRepository.findAll().collectList().block());
        mstSubDistrict.setId(longCount.incrementAndGet());

        // Create the MstSubDistrict
        MstSubDistrictDTO mstSubDistrictDTO = mstSubDistrictMapper.toDto(mstSubDistrict);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstSubDistrictDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstSubDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstSubDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteMstSubDistrict() {
        // Initialize the database
        insertedMstSubDistrict = mstSubDistrictRepository.save(mstSubDistrict).block();
        mstSubDistrictRepository.save(mstSubDistrict).block();
        mstSubDistrictSearchRepository.save(mstSubDistrict).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstSubDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the mstSubDistrict
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, mstSubDistrict.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstSubDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchMstSubDistrict() {
        // Initialize the database
        insertedMstSubDistrict = mstSubDistrictRepository.save(mstSubDistrict).block();
        mstSubDistrictSearchRepository.save(mstSubDistrict).block();

        // Search the mstSubDistrict
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + mstSubDistrict.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(mstSubDistrict.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].unm49Code")
            .value(hasItem(DEFAULT_UNM_49_CODE))
            .jsonPath("$.[*].isoAlpha2Code")
            .value(hasItem(DEFAULT_ISO_ALPHA_2_CODE));
    }

    protected long getRepositoryCount() {
        return mstSubDistrictRepository.count().block();
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

    protected MstSubDistrict getPersistedMstSubDistrict(MstSubDistrict mstSubDistrict) {
        return mstSubDistrictRepository.findById(mstSubDistrict.getId()).block();
    }

    protected void assertPersistedMstSubDistrictToMatchAllProperties(MstSubDistrict expectedMstSubDistrict) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstSubDistrictAllPropertiesEquals(expectedMstSubDistrict, getPersistedMstSubDistrict(expectedMstSubDistrict));
        assertMstSubDistrictUpdatableFieldsEquals(expectedMstSubDistrict, getPersistedMstSubDistrict(expectedMstSubDistrict));
    }

    protected void assertPersistedMstSubDistrictToMatchUpdatableProperties(MstSubDistrict expectedMstSubDistrict) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstSubDistrictAllUpdatablePropertiesEquals(expectedMstSubDistrict, getPersistedMstSubDistrict(expectedMstSubDistrict));
        assertMstSubDistrictUpdatableFieldsEquals(expectedMstSubDistrict, getPersistedMstSubDistrict(expectedMstSubDistrict));
    }
}
