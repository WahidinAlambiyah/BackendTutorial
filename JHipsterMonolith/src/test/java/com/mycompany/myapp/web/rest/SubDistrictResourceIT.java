package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.SubDistrictAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.District;
import com.mycompany.myapp.domain.SubDistrict;
import com.mycompany.myapp.repository.DistrictRepository;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.SubDistrictRepository;
import com.mycompany.myapp.repository.search.SubDistrictSearchRepository;
import com.mycompany.myapp.service.SubDistrictService;
import com.mycompany.myapp.service.dto.SubDistrictDTO;
import com.mycompany.myapp.service.mapper.SubDistrictMapper;
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
 * Integration tests for the {@link SubDistrictResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SubDistrictResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_UNM_49_CODE = "AAAAAAAAAA";
    private static final String UPDATED_UNM_49_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_ISO_ALPHA_2_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ISO_ALPHA_2_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/sub-districts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/sub-districts/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SubDistrictRepository subDistrictRepository;

    @Mock
    private SubDistrictRepository subDistrictRepositoryMock;

    @Autowired
    private SubDistrictMapper subDistrictMapper;

    @Mock
    private SubDistrictService subDistrictServiceMock;

    @Autowired
    private SubDistrictSearchRepository subDistrictSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private SubDistrict subDistrict;

    private SubDistrict insertedSubDistrict;

    @Autowired
    private DistrictRepository districtRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SubDistrict createEntity(EntityManager em) {
        SubDistrict subDistrict = new SubDistrict()
            .name(DEFAULT_NAME)
            .unm49Code(DEFAULT_UNM_49_CODE)
            .isoAlpha2Code(DEFAULT_ISO_ALPHA_2_CODE);
        return subDistrict;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SubDistrict createUpdatedEntity(EntityManager em) {
        SubDistrict subDistrict = new SubDistrict()
            .name(UPDATED_NAME)
            .unm49Code(UPDATED_UNM_49_CODE)
            .isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);
        return subDistrict;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(SubDistrict.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        subDistrict = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedSubDistrict != null) {
            subDistrictRepository.delete(insertedSubDistrict).block();
            subDistrictSearchRepository.delete(insertedSubDistrict).block();
            insertedSubDistrict = null;
        }
        deleteEntities(em);
    }

    @Test
    void createSubDistrict() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        // Create the SubDistrict
        SubDistrictDTO subDistrictDTO = subDistrictMapper.toDto(subDistrict);
        var returnedSubDistrictDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subDistrictDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(SubDistrictDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the SubDistrict in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSubDistrict = subDistrictMapper.toEntity(returnedSubDistrictDTO);
        assertSubDistrictUpdatableFieldsEquals(returnedSubDistrict, getPersistedSubDistrict(returnedSubDistrict));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedSubDistrict = returnedSubDistrict;
    }

    @Test
    void createSubDistrictWithExistingId() throws Exception {
        // Create the SubDistrict with an existing ID
        subDistrict.setId(1L);
        SubDistrictDTO subDistrictDTO = subDistrictMapper.toDto(subDistrict);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subDistrictDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SubDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        // set the field null
        subDistrict.setName(null);

        // Create the SubDistrict, which fails.
        SubDistrictDTO subDistrictDTO = subDistrictMapper.toDto(subDistrict);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subDistrictDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllSubDistricts() {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();

        // Get all the subDistrictList
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
            .value(hasItem(subDistrict.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].unm49Code")
            .value(hasItem(DEFAULT_UNM_49_CODE))
            .jsonPath("$.[*].isoAlpha2Code")
            .value(hasItem(DEFAULT_ISO_ALPHA_2_CODE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSubDistrictsWithEagerRelationshipsIsEnabled() {
        when(subDistrictServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(subDistrictServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSubDistrictsWithEagerRelationshipsIsNotEnabled() {
        when(subDistrictServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(subDistrictRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getSubDistrict() {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();

        // Get the subDistrict
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, subDistrict.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(subDistrict.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.unm49Code")
            .value(is(DEFAULT_UNM_49_CODE))
            .jsonPath("$.isoAlpha2Code")
            .value(is(DEFAULT_ISO_ALPHA_2_CODE));
    }

    @Test
    void getSubDistrictsByIdFiltering() {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();

        Long id = subDistrict.getId();

        defaultSubDistrictFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSubDistrictFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSubDistrictFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllSubDistrictsByNameIsEqualToSomething() {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();

        // Get all the subDistrictList where name equals to
        defaultSubDistrictFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllSubDistrictsByNameIsInShouldWork() {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();

        // Get all the subDistrictList where name in
        defaultSubDistrictFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllSubDistrictsByNameIsNullOrNotNull() {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();

        // Get all the subDistrictList where name is not null
        defaultSubDistrictFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllSubDistrictsByNameContainsSomething() {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();

        // Get all the subDistrictList where name contains
        defaultSubDistrictFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllSubDistrictsByNameNotContainsSomething() {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();

        // Get all the subDistrictList where name does not contain
        defaultSubDistrictFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllSubDistrictsByUnm49CodeIsEqualToSomething() {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();

        // Get all the subDistrictList where unm49Code equals to
        defaultSubDistrictFiltering("unm49Code.equals=" + DEFAULT_UNM_49_CODE, "unm49Code.equals=" + UPDATED_UNM_49_CODE);
    }

    @Test
    void getAllSubDistrictsByUnm49CodeIsInShouldWork() {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();

        // Get all the subDistrictList where unm49Code in
        defaultSubDistrictFiltering(
            "unm49Code.in=" + DEFAULT_UNM_49_CODE + "," + UPDATED_UNM_49_CODE,
            "unm49Code.in=" + UPDATED_UNM_49_CODE
        );
    }

    @Test
    void getAllSubDistrictsByUnm49CodeIsNullOrNotNull() {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();

        // Get all the subDistrictList where unm49Code is not null
        defaultSubDistrictFiltering("unm49Code.specified=true", "unm49Code.specified=false");
    }

    @Test
    void getAllSubDistrictsByUnm49CodeContainsSomething() {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();

        // Get all the subDistrictList where unm49Code contains
        defaultSubDistrictFiltering("unm49Code.contains=" + DEFAULT_UNM_49_CODE, "unm49Code.contains=" + UPDATED_UNM_49_CODE);
    }

    @Test
    void getAllSubDistrictsByUnm49CodeNotContainsSomething() {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();

        // Get all the subDistrictList where unm49Code does not contain
        defaultSubDistrictFiltering("unm49Code.doesNotContain=" + UPDATED_UNM_49_CODE, "unm49Code.doesNotContain=" + DEFAULT_UNM_49_CODE);
    }

    @Test
    void getAllSubDistrictsByIsoAlpha2CodeIsEqualToSomething() {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();

        // Get all the subDistrictList where isoAlpha2Code equals to
        defaultSubDistrictFiltering("isoAlpha2Code.equals=" + DEFAULT_ISO_ALPHA_2_CODE, "isoAlpha2Code.equals=" + UPDATED_ISO_ALPHA_2_CODE);
    }

    @Test
    void getAllSubDistrictsByIsoAlpha2CodeIsInShouldWork() {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();

        // Get all the subDistrictList where isoAlpha2Code in
        defaultSubDistrictFiltering(
            "isoAlpha2Code.in=" + DEFAULT_ISO_ALPHA_2_CODE + "," + UPDATED_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.in=" + UPDATED_ISO_ALPHA_2_CODE
        );
    }

    @Test
    void getAllSubDistrictsByIsoAlpha2CodeIsNullOrNotNull() {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();

        // Get all the subDistrictList where isoAlpha2Code is not null
        defaultSubDistrictFiltering("isoAlpha2Code.specified=true", "isoAlpha2Code.specified=false");
    }

    @Test
    void getAllSubDistrictsByIsoAlpha2CodeContainsSomething() {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();

        // Get all the subDistrictList where isoAlpha2Code contains
        defaultSubDistrictFiltering(
            "isoAlpha2Code.contains=" + DEFAULT_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.contains=" + UPDATED_ISO_ALPHA_2_CODE
        );
    }

    @Test
    void getAllSubDistrictsByIsoAlpha2CodeNotContainsSomething() {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();

        // Get all the subDistrictList where isoAlpha2Code does not contain
        defaultSubDistrictFiltering(
            "isoAlpha2Code.doesNotContain=" + UPDATED_ISO_ALPHA_2_CODE,
            "isoAlpha2Code.doesNotContain=" + DEFAULT_ISO_ALPHA_2_CODE
        );
    }

    @Test
    void getAllSubDistrictsByDistrictIsEqualToSomething() {
        District district = DistrictResourceIT.createEntity(em);
        districtRepository.save(district).block();
        Long districtId = district.getId();
        subDistrict.setDistrictId(districtId);
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();
        // Get all the subDistrictList where district equals to districtId
        defaultSubDistrictShouldBeFound("districtId.equals=" + districtId);

        // Get all the subDistrictList where district equals to (districtId + 1)
        defaultSubDistrictShouldNotBeFound("districtId.equals=" + (districtId + 1));
    }

    private void defaultSubDistrictFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultSubDistrictShouldBeFound(shouldBeFound);
        defaultSubDistrictShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSubDistrictShouldBeFound(String filter) {
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
            .value(hasItem(subDistrict.getId().intValue()))
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
    private void defaultSubDistrictShouldNotBeFound(String filter) {
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
    void getNonExistingSubDistrict() {
        // Get the subDistrict
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingSubDistrict() throws Exception {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        subDistrictSearchRepository.save(subDistrict).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());

        // Update the subDistrict
        SubDistrict updatedSubDistrict = subDistrictRepository.findById(subDistrict.getId()).block();
        updatedSubDistrict.name(UPDATED_NAME).unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);
        SubDistrictDTO subDistrictDTO = subDistrictMapper.toDto(updatedSubDistrict);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, subDistrictDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subDistrictDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SubDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSubDistrictToMatchAllProperties(updatedSubDistrict);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<SubDistrict> subDistrictSearchList = Streamable.of(
                    subDistrictSearchRepository.findAll().collectList().block()
                ).toList();
                SubDistrict testSubDistrictSearch = subDistrictSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertSubDistrictAllPropertiesEquals(testSubDistrictSearch, updatedSubDistrict);
                assertSubDistrictUpdatableFieldsEquals(testSubDistrictSearch, updatedSubDistrict);
            });
    }

    @Test
    void putNonExistingSubDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        subDistrict.setId(longCount.incrementAndGet());

        // Create the SubDistrict
        SubDistrictDTO subDistrictDTO = subDistrictMapper.toDto(subDistrict);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, subDistrictDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subDistrictDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SubDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchSubDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        subDistrict.setId(longCount.incrementAndGet());

        // Create the SubDistrict
        SubDistrictDTO subDistrictDTO = subDistrictMapper.toDto(subDistrict);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subDistrictDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SubDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamSubDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        subDistrict.setId(longCount.incrementAndGet());

        // Create the SubDistrict
        SubDistrictDTO subDistrictDTO = subDistrictMapper.toDto(subDistrict);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subDistrictDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the SubDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateSubDistrictWithPatch() throws Exception {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subDistrict using partial update
        SubDistrict partialUpdatedSubDistrict = new SubDistrict();
        partialUpdatedSubDistrict.setId(subDistrict.getId());

        partialUpdatedSubDistrict.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSubDistrict.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSubDistrict))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SubDistrict in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSubDistrictUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSubDistrict, subDistrict),
            getPersistedSubDistrict(subDistrict)
        );
    }

    @Test
    void fullUpdateSubDistrictWithPatch() throws Exception {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subDistrict using partial update
        SubDistrict partialUpdatedSubDistrict = new SubDistrict();
        partialUpdatedSubDistrict.setId(subDistrict.getId());

        partialUpdatedSubDistrict.name(UPDATED_NAME).unm49Code(UPDATED_UNM_49_CODE).isoAlpha2Code(UPDATED_ISO_ALPHA_2_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSubDistrict.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSubDistrict))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SubDistrict in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSubDistrictUpdatableFieldsEquals(partialUpdatedSubDistrict, getPersistedSubDistrict(partialUpdatedSubDistrict));
    }

    @Test
    void patchNonExistingSubDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        subDistrict.setId(longCount.incrementAndGet());

        // Create the SubDistrict
        SubDistrictDTO subDistrictDTO = subDistrictMapper.toDto(subDistrict);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, subDistrictDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(subDistrictDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SubDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchSubDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        subDistrict.setId(longCount.incrementAndGet());

        // Create the SubDistrict
        SubDistrictDTO subDistrictDTO = subDistrictMapper.toDto(subDistrict);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(subDistrictDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SubDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamSubDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        subDistrict.setId(longCount.incrementAndGet());

        // Create the SubDistrict
        SubDistrictDTO subDistrictDTO = subDistrictMapper.toDto(subDistrict);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(subDistrictDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the SubDistrict in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteSubDistrict() {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();
        subDistrictRepository.save(subDistrict).block();
        subDistrictSearchRepository.save(subDistrict).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the subDistrict
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, subDistrict.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subDistrictSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchSubDistrict() {
        // Initialize the database
        insertedSubDistrict = subDistrictRepository.save(subDistrict).block();
        subDistrictSearchRepository.save(subDistrict).block();

        // Search the subDistrict
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + subDistrict.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(subDistrict.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].unm49Code")
            .value(hasItem(DEFAULT_UNM_49_CODE))
            .jsonPath("$.[*].isoAlpha2Code")
            .value(hasItem(DEFAULT_ISO_ALPHA_2_CODE));
    }

    protected long getRepositoryCount() {
        return subDistrictRepository.count().block();
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

    protected SubDistrict getPersistedSubDistrict(SubDistrict subDistrict) {
        return subDistrictRepository.findById(subDistrict.getId()).block();
    }

    protected void assertPersistedSubDistrictToMatchAllProperties(SubDistrict expectedSubDistrict) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSubDistrictAllPropertiesEquals(expectedSubDistrict, getPersistedSubDistrict(expectedSubDistrict));
        assertSubDistrictUpdatableFieldsEquals(expectedSubDistrict, getPersistedSubDistrict(expectedSubDistrict));
    }

    protected void assertPersistedSubDistrictToMatchUpdatableProperties(SubDistrict expectedSubDistrict) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSubDistrictAllUpdatablePropertiesEquals(expectedSubDistrict, getPersistedSubDistrict(expectedSubDistrict));
        assertSubDistrictUpdatableFieldsEquals(expectedSubDistrict, getPersistedSubDistrict(expectedSubDistrict));
    }
}
