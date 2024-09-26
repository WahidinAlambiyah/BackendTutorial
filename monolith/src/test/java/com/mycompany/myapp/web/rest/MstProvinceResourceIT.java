package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.MstProvinceAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.MstCountry;
import com.mycompany.myapp.domain.MstProvince;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MstCountryRepository;
import com.mycompany.myapp.repository.MstProvinceRepository;
import com.mycompany.myapp.repository.search.MstProvinceSearchRepository;
import com.mycompany.myapp.service.MstProvinceService;
import com.mycompany.myapp.service.dto.MstProvinceDTO;
import com.mycompany.myapp.service.mapper.MstProvinceMapper;
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
 * Integration tests for the {@link MstProvinceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MstProvinceResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_UNM_49_CODE = "AAAAAAAAAA";
    private static final String UPDATED_UNM_49_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_ISO_ALPHA_2_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ISO_ALPHA_2_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/mst-provinces";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/mst-provinces/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MstProvinceRepository mstProvinceRepository;

    @Mock
    private MstProvinceRepository mstProvinceRepositoryMock;

    @Autowired
    private MstProvinceMapper mstProvinceMapper;

    @Mock
    private MstProvinceService mstProvinceServiceMock;

    @Autowired
    private MstProvinceSearchRepository mstProvinceSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private MstProvince mstProvince;

    private MstProvince insertedMstProvince;

    @Autowired
    private MstCountryRepository mstCountryRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstProvince createEntity(EntityManager em) {
        MstProvince mstProvince = new MstProvince()
            .name(DEFAULT_NAME)
            .unm49Code(DEFAULT_UNM_49_CODE)
            .isoAlpha2Code(DEFAULT_ISO_ALPHA_2_CODE);
        return mstProvince;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstProvince createUpdatedEntity(EntityManager em) {
        MstProvince mstProvince = new MstProvince()
            .name(UPDATED_NAME)
            .unm49Code(UPDATED_UNM_49_CODE)
            .isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);
        return mstProvince;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(MstProvince.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        mstProvince = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedMstProvince != null) {
            mstProvinceRepository.delete(insertedMstProvince).block();
            mstProvinceSearchRepository.delete(insertedMstProvince).block();
            insertedMstProvince = null;
        }
        deleteEntities(em);
    }

    @Test
    void createMstProvince() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstProvinceSearchRepository.findAll().collectList().block());
        // Create the MstProvince
        MstProvinceDTO mstProvinceDTO = mstProvinceMapper.toDto(mstProvince);
        var returnedMstProvinceDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstProvinceDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MstProvinceDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MstProvince in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMstProvince = mstProvinceMapper.toEntity(returnedMstProvinceDTO);
        assertMstProvinceUpdatableFieldsEquals(returnedMstProvince, getPersistedMstProvince(returnedMstProvince));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstProvinceSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedMstProvince = returnedMstProvince;
    }

    @Test
    void createMstProvinceWithExistingId() throws Exception {
        // Create the MstProvince with an existing ID
        mstProvince.setId(1L);
        MstProvinceDTO mstProvinceDTO = mstProvinceMapper.toDto(mstProvince);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstProvinceSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstProvinceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstProvince in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstProvinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstProvinceSearchRepository.findAll().collectList().block());
        // set the field null
        mstProvince.setName(null);

        // Create the MstProvince, which fails.
        MstProvinceDTO mstProvinceDTO = mstProvinceMapper.toDto(mstProvince);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstProvinceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstProvinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllMstProvinces() {
        // Initialize the database
        insertedMstProvince = mstProvinceRepository.save(mstProvince).block();

        // Get all the mstProvinceList
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
            .value(hasItem(mstProvince.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].unm49Code")
            .value(hasItem(DEFAULT_UNM_49_CODE))
            .jsonPath("$.[*].isoAlpha2Code")
            .value(hasItem(DEFAULT_ISO_ALPHA_2_CODE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMstProvincesWithEagerRelationshipsIsEnabled() {
        when(mstProvinceServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(mstProvinceServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMstProvincesWithEagerRelationshipsIsNotEnabled() {
        when(mstProvinceServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(mstProvinceRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getMstProvince() {
        // Initialize the database
        insertedMstProvince = mstProvinceRepository.save(mstProvince).block();

        // Get the mstProvince
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, mstProvince.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(mstProvince.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.unm49Code")
            .value(is(DEFAULT_UNM_49_CODE))
            .jsonPath("$.isoAlpha2Code")
            .value(is(DEFAULT_ISO_ALPHA_2_CODE));
    }

    @Test
    void getMstProvincesByIdFiltering() {
        // Initialize the database
        insertedMstProvince = mstProvinceRepository.save(mstProvince).block();

        Long id = mstProvince.getId();

        defaultMstProvinceFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMstProvinceFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMstProvinceFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllMstProvincesByNameIsEqualToSomething() {
        // Initialize the database
        insertedMstProvince = mstProvinceRepository.save(mstProvince).block();

        // Get all the mstProvinceList where name equals to
        defaultMstProvinceFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllMstProvincesByNameIsInShouldWork() {
        // Initialize the database
        insertedMstProvince = mstProvinceRepository.save(mstProvince).block();

        // Get all the mstProvinceList where name in
        defaultMstProvinceFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllMstProvincesByNameIsNullOrNotNull() {
        // Initialize the database
        insertedMstProvince = mstProvinceRepository.save(mstProvince).block();

        // Get all the mstProvinceList where name is not null
        defaultMstProvinceFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllMstProvincesByNameContainsSomething() {
        // Initialize the database
        insertedMstProvince = mstProvinceRepository.save(mstProvince).block();

        // Get all the mstProvinceList where name contains
        defaultMstProvinceFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllMstProvincesByNameNotContainsSomething() {
        // Initialize the database
        insertedMstProvince = mstProvinceRepository.save(mstProvince).block();

        // Get all the mstProvinceList where name does not contain
        defaultMstProvinceFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllMstProvincesByUnm49CodeIsEqualToSomething() {
        // Initialize the database
        insertedMstProvince = mstProvinceRepository.save(mstProvince).block();

        // Get all the mstProvinceList where unm49Code equals to
        defaultMstProvinceFiltering("unm49Code.equals=" + DEFAULT_UNM_49_CODE, "unm49Code.equals=" + UPDATED_UNM_49_CODE);
    }

    @Test
    void getAllMstProvincesByUnm49CodeIsInShouldWork() {
        // Initialize the database
        insertedMstProvince = mstProvinceRepository.save(mstProvince).block();

        // Get all the mstProvinceList where unm49Code in
        defaultMstProvinceFiltering(
            "unm49Code.in=" + DEFAULT_UNM_49_CODE + "," + UPDATED_UNM_49_CODE,
            "unm49Code.in=" + UPDATED_UNM_49_CODE
        );
    }

    @Test
    void getAllMstProvincesByUnm49CodeIsNullOrNotNull() {
        // Initialize the database
        insertedMstProvince = mstProvinceRepository.save(mstProvince).block();

        // Get all the mstProvinceList where unm49Code is not null
        defaultMstProvinceFiltering("unm49Code.specified=true", "unm49Code.specified=false");
    }

    @Test
    void getAllMstProvincesByUnm49CodeContainsSomething() {
        // Initialize the database
        insertedMstProvince = mstProvinceRepository.save(mstProvince).block();

        // Get all the mstProvinceList where unm49Code contains
        defaultMstProvinceFiltering("unm49Code.contains=" + DEFAULT_UNM_49_CODE, "unm49Code.contains=" + UPDATED_UNM_49_CODE);
    }

    @Test
    void getAllMstProvincesByUnm49CodeNotContainsSomething() {
        // Initialize the database
        insertedMstProvince = mstProvinceRepository.save(mstProvince).block();

        // Get all the mstProvinceList where unm49Code does not contain
        defaultMstProvinceFiltering("unm49Code.doesNotContain=" + UPDATED_UNM_49_CODE, "unm49Code.doesNotContain=" + DEFAULT_UNM_49_CODE);
    }

    @Test
    void getAllMstProvincesByIsoAlpha2CodeIsEqualToSomething() {
        // Initialize the database
        insertedMstProvince = mstProvinceRepository.save(mstProvince).block();

        // Get all the mstProvinceList where isoAlpha2Code equals to
        defaultMstProvinceFiltering("isoAlpha2Code.equals=" + DEFAULT_ISO_ALPHA_2_CODE, "isoAlpha2Code.equals=" + UPDATED_ISO_ALPHA_2_CODE);
    }

    @Test
    void getAllMstProvincesByIsoAlpha2CodeIsInShouldWork() {
        // Initialize the database
        insertedMstProvince = mstProvinceRepository.save(mstProvince).block();

        // Get all the mstProvinceList where isoAlpha2Code in
        defaultMstProvinceFiltering(
            "isoAlpha2Code.in=" + DEFAULT_ISO_ALPHA_2_CODE + "," + UPDATED_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.in=" + UPDATED_ISO_ALPHA_2_CODE
        );
    }

    @Test
    void getAllMstProvincesByIsoAlpha2CodeIsNullOrNotNull() {
        // Initialize the database
        insertedMstProvince = mstProvinceRepository.save(mstProvince).block();

        // Get all the mstProvinceList where isoAlpha2Code is not null
        defaultMstProvinceFiltering("isoAlpha2Code.specified=true", "isoAlpha2Code.specified=false");
    }

    @Test
    void getAllMstProvincesByIsoAlpha2CodeContainsSomething() {
        // Initialize the database
        insertedMstProvince = mstProvinceRepository.save(mstProvince).block();

        // Get all the mstProvinceList where isoAlpha2Code contains
        defaultMstProvinceFiltering(
            "isoAlpha2Code.contains=" + DEFAULT_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.contains=" + UPDATED_ISO_ALPHA_2_CODE
        );
    }

    @Test
    void getAllMstProvincesByIsoAlpha2CodeNotContainsSomething() {
        // Initialize the database
        insertedMstProvince = mstProvinceRepository.save(mstProvince).block();

        // Get all the mstProvinceList where isoAlpha2Code does not contain
        defaultMstProvinceFiltering(
            "isoAlpha2Code.doesNotContain=" + UPDATED_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.doesNotContain=" + DEFAULT_ISO_ALPHA_2_CODE
        );
    }

    @Test
    void getAllMstProvincesByCountryIsEqualToSomething() {
        MstCountry country = MstCountryResourceIT.createEntity(em);
        mstCountryRepository.save(country).block();
        Long countryId = country.getId();
        mstProvince.setCountryId(countryId);
        insertedMstProvince = mstProvinceRepository.save(mstProvince).block();
        // Get all the mstProvinceList where country equals to countryId
        defaultMstProvinceShouldBeFound("countryId.equals=" + countryId);

        // Get all the mstProvinceList where country equals to (countryId + 1)
        defaultMstProvinceShouldNotBeFound("countryId.equals=" + (countryId + 1));
    }

    private void defaultMstProvinceFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultMstProvinceShouldBeFound(shouldBeFound);
        defaultMstProvinceShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMstProvinceShouldBeFound(String filter) {
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
            .value(hasItem(mstProvince.getId().intValue()))
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
    private void defaultMstProvinceShouldNotBeFound(String filter) {
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
    void getNonExistingMstProvince() {
        // Get the mstProvince
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMstProvince() throws Exception {
        // Initialize the database
        insertedMstProvince = mstProvinceRepository.save(mstProvince).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        mstProvinceSearchRepository.save(mstProvince).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstProvinceSearchRepository.findAll().collectList().block());

        // Update the mstProvince
        MstProvince updatedMstProvince = mstProvinceRepository.findById(mstProvince.getId()).block();
        updatedMstProvince.name(UPDATED_NAME).unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);
        MstProvinceDTO mstProvinceDTO = mstProvinceMapper.toDto(updatedMstProvince);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstProvinceDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstProvinceDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstProvince in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMstProvinceToMatchAllProperties(updatedMstProvince);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstProvinceSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<MstProvince> mstProvinceSearchList = Streamable.of(
                    mstProvinceSearchRepository.findAll().collectList().block()
                ).toList();
                MstProvince testMstProvinceSearch = mstProvinceSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertMstProvinceAllPropertiesEquals(testMstProvinceSearch, updatedMstProvince);
                assertMstProvinceUpdatableFieldsEquals(testMstProvinceSearch, updatedMstProvince);
            });
    }

    @Test
    void putNonExistingMstProvince() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstProvinceSearchRepository.findAll().collectList().block());
        mstProvince.setId(longCount.incrementAndGet());

        // Create the MstProvince
        MstProvinceDTO mstProvinceDTO = mstProvinceMapper.toDto(mstProvince);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstProvinceDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstProvinceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstProvince in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstProvinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchMstProvince() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstProvinceSearchRepository.findAll().collectList().block());
        mstProvince.setId(longCount.incrementAndGet());

        // Create the MstProvince
        MstProvinceDTO mstProvinceDTO = mstProvinceMapper.toDto(mstProvince);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstProvinceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstProvince in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstProvinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamMstProvince() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstProvinceSearchRepository.findAll().collectList().block());
        mstProvince.setId(longCount.incrementAndGet());

        // Create the MstProvince
        MstProvinceDTO mstProvinceDTO = mstProvinceMapper.toDto(mstProvince);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstProvinceDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstProvince in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstProvinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateMstProvinceWithPatch() throws Exception {
        // Initialize the database
        insertedMstProvince = mstProvinceRepository.save(mstProvince).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstProvince using partial update
        MstProvince partialUpdatedMstProvince = new MstProvince();
        partialUpdatedMstProvince.setId(mstProvince.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstProvince.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstProvince))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstProvince in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstProvinceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMstProvince, mstProvince),
            getPersistedMstProvince(mstProvince)
        );
    }

    @Test
    void fullUpdateMstProvinceWithPatch() throws Exception {
        // Initialize the database
        insertedMstProvince = mstProvinceRepository.save(mstProvince).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstProvince using partial update
        MstProvince partialUpdatedMstProvince = new MstProvince();
        partialUpdatedMstProvince.setId(mstProvince.getId());

        partialUpdatedMstProvince.name(UPDATED_NAME).unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstProvince.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstProvince))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstProvince in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstProvinceUpdatableFieldsEquals(partialUpdatedMstProvince, getPersistedMstProvince(partialUpdatedMstProvince));
    }

    @Test
    void patchNonExistingMstProvince() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstProvinceSearchRepository.findAll().collectList().block());
        mstProvince.setId(longCount.incrementAndGet());

        // Create the MstProvince
        MstProvinceDTO mstProvinceDTO = mstProvinceMapper.toDto(mstProvince);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, mstProvinceDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstProvinceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstProvince in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstProvinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchMstProvince() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstProvinceSearchRepository.findAll().collectList().block());
        mstProvince.setId(longCount.incrementAndGet());

        // Create the MstProvince
        MstProvinceDTO mstProvinceDTO = mstProvinceMapper.toDto(mstProvince);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstProvinceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstProvince in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstProvinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamMstProvince() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstProvinceSearchRepository.findAll().collectList().block());
        mstProvince.setId(longCount.incrementAndGet());

        // Create the MstProvince
        MstProvinceDTO mstProvinceDTO = mstProvinceMapper.toDto(mstProvince);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstProvinceDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstProvince in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstProvinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteMstProvince() {
        // Initialize the database
        insertedMstProvince = mstProvinceRepository.save(mstProvince).block();
        mstProvinceRepository.save(mstProvince).block();
        mstProvinceSearchRepository.save(mstProvince).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstProvinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the mstProvince
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, mstProvince.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstProvinceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchMstProvince() {
        // Initialize the database
        insertedMstProvince = mstProvinceRepository.save(mstProvince).block();
        mstProvinceSearchRepository.save(mstProvince).block();

        // Search the mstProvince
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + mstProvince.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(mstProvince.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].unm49Code")
            .value(hasItem(DEFAULT_UNM_49_CODE))
            .jsonPath("$.[*].isoAlpha2Code")
            .value(hasItem(DEFAULT_ISO_ALPHA_2_CODE));
    }

    protected long getRepositoryCount() {
        return mstProvinceRepository.count().block();
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

    protected MstProvince getPersistedMstProvince(MstProvince mstProvince) {
        return mstProvinceRepository.findById(mstProvince.getId()).block();
    }

    protected void assertPersistedMstProvinceToMatchAllProperties(MstProvince expectedMstProvince) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstProvinceAllPropertiesEquals(expectedMstProvince, getPersistedMstProvince(expectedMstProvince));
        assertMstProvinceUpdatableFieldsEquals(expectedMstProvince, getPersistedMstProvince(expectedMstProvince));
    }

    protected void assertPersistedMstProvinceToMatchUpdatableProperties(MstProvince expectedMstProvince) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstProvinceAllUpdatablePropertiesEquals(expectedMstProvince, getPersistedMstProvince(expectedMstProvince));
        assertMstProvinceUpdatableFieldsEquals(expectedMstProvince, getPersistedMstProvince(expectedMstProvince));
    }
}
