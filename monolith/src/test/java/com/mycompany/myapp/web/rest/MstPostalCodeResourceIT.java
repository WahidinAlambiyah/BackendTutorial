package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.MstPostalCodeAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.MstPostalCode;
import com.mycompany.myapp.domain.MstSubDistrict;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MstPostalCodeRepository;
import com.mycompany.myapp.repository.MstSubDistrictRepository;
import com.mycompany.myapp.repository.search.MstPostalCodeSearchRepository;
import com.mycompany.myapp.service.MstPostalCodeService;
import com.mycompany.myapp.service.dto.MstPostalCodeDTO;
import com.mycompany.myapp.service.mapper.MstPostalCodeMapper;
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
 * Integration tests for the {@link MstPostalCodeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MstPostalCodeResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/mst-postal-codes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/mst-postal-codes/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MstPostalCodeRepository mstPostalCodeRepository;

    @Mock
    private MstPostalCodeRepository mstPostalCodeRepositoryMock;

    @Autowired
    private MstPostalCodeMapper mstPostalCodeMapper;

    @Mock
    private MstPostalCodeService mstPostalCodeServiceMock;

    @Autowired
    private MstPostalCodeSearchRepository mstPostalCodeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private MstPostalCode mstPostalCode;

    private MstPostalCode insertedMstPostalCode;

    @Autowired
    private MstSubDistrictRepository mstSubDistrictRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstPostalCode createEntity(EntityManager em) {
        MstPostalCode mstPostalCode = new MstPostalCode().code(DEFAULT_CODE);
        return mstPostalCode;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstPostalCode createUpdatedEntity(EntityManager em) {
        MstPostalCode mstPostalCode = new MstPostalCode().code(UPDATED_CODE);
        return mstPostalCode;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(MstPostalCode.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        mstPostalCode = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedMstPostalCode != null) {
            mstPostalCodeRepository.delete(insertedMstPostalCode).block();
            mstPostalCodeSearchRepository.delete(insertedMstPostalCode).block();
            insertedMstPostalCode = null;
        }
        deleteEntities(em);
    }

    @Test
    void createMstPostalCode() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstPostalCodeSearchRepository.findAll().collectList().block());
        // Create the MstPostalCode
        MstPostalCodeDTO mstPostalCodeDTO = mstPostalCodeMapper.toDto(mstPostalCode);
        var returnedMstPostalCodeDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstPostalCodeDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MstPostalCodeDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MstPostalCode in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMstPostalCode = mstPostalCodeMapper.toEntity(returnedMstPostalCodeDTO);
        assertMstPostalCodeUpdatableFieldsEquals(returnedMstPostalCode, getPersistedMstPostalCode(returnedMstPostalCode));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstPostalCodeSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedMstPostalCode = returnedMstPostalCode;
    }

    @Test
    void createMstPostalCodeWithExistingId() throws Exception {
        // Create the MstPostalCode with an existing ID
        mstPostalCode.setId(1L);
        MstPostalCodeDTO mstPostalCodeDTO = mstPostalCodeMapper.toDto(mstPostalCode);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstPostalCodeSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstPostalCodeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstPostalCode in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstPostalCodeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstPostalCodeSearchRepository.findAll().collectList().block());
        // set the field null
        mstPostalCode.setCode(null);

        // Create the MstPostalCode, which fails.
        MstPostalCodeDTO mstPostalCodeDTO = mstPostalCodeMapper.toDto(mstPostalCode);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstPostalCodeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstPostalCodeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllMstPostalCodes() {
        // Initialize the database
        insertedMstPostalCode = mstPostalCodeRepository.save(mstPostalCode).block();

        // Get all the mstPostalCodeList
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
            .value(hasItem(mstPostalCode.getId().intValue()))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMstPostalCodesWithEagerRelationshipsIsEnabled() {
        when(mstPostalCodeServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(mstPostalCodeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMstPostalCodesWithEagerRelationshipsIsNotEnabled() {
        when(mstPostalCodeServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(mstPostalCodeRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getMstPostalCode() {
        // Initialize the database
        insertedMstPostalCode = mstPostalCodeRepository.save(mstPostalCode).block();

        // Get the mstPostalCode
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, mstPostalCode.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(mstPostalCode.getId().intValue()))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE));
    }

    @Test
    void getMstPostalCodesByIdFiltering() {
        // Initialize the database
        insertedMstPostalCode = mstPostalCodeRepository.save(mstPostalCode).block();

        Long id = mstPostalCode.getId();

        defaultMstPostalCodeFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMstPostalCodeFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMstPostalCodeFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllMstPostalCodesByCodeIsEqualToSomething() {
        // Initialize the database
        insertedMstPostalCode = mstPostalCodeRepository.save(mstPostalCode).block();

        // Get all the mstPostalCodeList where code equals to
        defaultMstPostalCodeFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    void getAllMstPostalCodesByCodeIsInShouldWork() {
        // Initialize the database
        insertedMstPostalCode = mstPostalCodeRepository.save(mstPostalCode).block();

        // Get all the mstPostalCodeList where code in
        defaultMstPostalCodeFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    void getAllMstPostalCodesByCodeIsNullOrNotNull() {
        // Initialize the database
        insertedMstPostalCode = mstPostalCodeRepository.save(mstPostalCode).block();

        // Get all the mstPostalCodeList where code is not null
        defaultMstPostalCodeFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    void getAllMstPostalCodesByCodeContainsSomething() {
        // Initialize the database
        insertedMstPostalCode = mstPostalCodeRepository.save(mstPostalCode).block();

        // Get all the mstPostalCodeList where code contains
        defaultMstPostalCodeFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    void getAllMstPostalCodesByCodeNotContainsSomething() {
        // Initialize the database
        insertedMstPostalCode = mstPostalCodeRepository.save(mstPostalCode).block();

        // Get all the mstPostalCodeList where code does not contain
        defaultMstPostalCodeFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    void getAllMstPostalCodesBySubDistrictIsEqualToSomething() {
        MstSubDistrict subDistrict = MstSubDistrictResourceIT.createEntity(em);
        mstSubDistrictRepository.save(subDistrict).block();
        Long subDistrictId = subDistrict.getId();
        mstPostalCode.setSubDistrictId(subDistrictId);
        insertedMstPostalCode = mstPostalCodeRepository.save(mstPostalCode).block();
        // Get all the mstPostalCodeList where subDistrict equals to subDistrictId
        defaultMstPostalCodeShouldBeFound("subDistrictId.equals=" + subDistrictId);

        // Get all the mstPostalCodeList where subDistrict equals to (subDistrictId + 1)
        defaultMstPostalCodeShouldNotBeFound("subDistrictId.equals=" + (subDistrictId + 1));
    }

    private void defaultMstPostalCodeFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultMstPostalCodeShouldBeFound(shouldBeFound);
        defaultMstPostalCodeShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMstPostalCodeShouldBeFound(String filter) {
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
            .value(hasItem(mstPostalCode.getId().intValue()))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE));

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
    private void defaultMstPostalCodeShouldNotBeFound(String filter) {
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
    void getNonExistingMstPostalCode() {
        // Get the mstPostalCode
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMstPostalCode() throws Exception {
        // Initialize the database
        insertedMstPostalCode = mstPostalCodeRepository.save(mstPostalCode).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        mstPostalCodeSearchRepository.save(mstPostalCode).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstPostalCodeSearchRepository.findAll().collectList().block());

        // Update the mstPostalCode
        MstPostalCode updatedMstPostalCode = mstPostalCodeRepository.findById(mstPostalCode.getId()).block();
        updatedMstPostalCode.code(UPDATED_CODE);
        MstPostalCodeDTO mstPostalCodeDTO = mstPostalCodeMapper.toDto(updatedMstPostalCode);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstPostalCodeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstPostalCodeDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstPostalCode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMstPostalCodeToMatchAllProperties(updatedMstPostalCode);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstPostalCodeSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<MstPostalCode> mstPostalCodeSearchList = Streamable.of(
                    mstPostalCodeSearchRepository.findAll().collectList().block()
                ).toList();
                MstPostalCode testMstPostalCodeSearch = mstPostalCodeSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertMstPostalCodeAllPropertiesEquals(testMstPostalCodeSearch, updatedMstPostalCode);
                assertMstPostalCodeUpdatableFieldsEquals(testMstPostalCodeSearch, updatedMstPostalCode);
            });
    }

    @Test
    void putNonExistingMstPostalCode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstPostalCodeSearchRepository.findAll().collectList().block());
        mstPostalCode.setId(longCount.incrementAndGet());

        // Create the MstPostalCode
        MstPostalCodeDTO mstPostalCodeDTO = mstPostalCodeMapper.toDto(mstPostalCode);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstPostalCodeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstPostalCodeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstPostalCode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstPostalCodeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchMstPostalCode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstPostalCodeSearchRepository.findAll().collectList().block());
        mstPostalCode.setId(longCount.incrementAndGet());

        // Create the MstPostalCode
        MstPostalCodeDTO mstPostalCodeDTO = mstPostalCodeMapper.toDto(mstPostalCode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstPostalCodeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstPostalCode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstPostalCodeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamMstPostalCode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstPostalCodeSearchRepository.findAll().collectList().block());
        mstPostalCode.setId(longCount.incrementAndGet());

        // Create the MstPostalCode
        MstPostalCodeDTO mstPostalCodeDTO = mstPostalCodeMapper.toDto(mstPostalCode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstPostalCodeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstPostalCode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstPostalCodeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateMstPostalCodeWithPatch() throws Exception {
        // Initialize the database
        insertedMstPostalCode = mstPostalCodeRepository.save(mstPostalCode).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstPostalCode using partial update
        MstPostalCode partialUpdatedMstPostalCode = new MstPostalCode();
        partialUpdatedMstPostalCode.setId(mstPostalCode.getId());

        partialUpdatedMstPostalCode.code(UPDATED_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstPostalCode.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstPostalCode))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstPostalCode in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstPostalCodeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMstPostalCode, mstPostalCode),
            getPersistedMstPostalCode(mstPostalCode)
        );
    }

    @Test
    void fullUpdateMstPostalCodeWithPatch() throws Exception {
        // Initialize the database
        insertedMstPostalCode = mstPostalCodeRepository.save(mstPostalCode).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstPostalCode using partial update
        MstPostalCode partialUpdatedMstPostalCode = new MstPostalCode();
        partialUpdatedMstPostalCode.setId(mstPostalCode.getId());

        partialUpdatedMstPostalCode.code(UPDATED_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstPostalCode.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstPostalCode))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstPostalCode in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstPostalCodeUpdatableFieldsEquals(partialUpdatedMstPostalCode, getPersistedMstPostalCode(partialUpdatedMstPostalCode));
    }

    @Test
    void patchNonExistingMstPostalCode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstPostalCodeSearchRepository.findAll().collectList().block());
        mstPostalCode.setId(longCount.incrementAndGet());

        // Create the MstPostalCode
        MstPostalCodeDTO mstPostalCodeDTO = mstPostalCodeMapper.toDto(mstPostalCode);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, mstPostalCodeDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstPostalCodeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstPostalCode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstPostalCodeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchMstPostalCode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstPostalCodeSearchRepository.findAll().collectList().block());
        mstPostalCode.setId(longCount.incrementAndGet());

        // Create the MstPostalCode
        MstPostalCodeDTO mstPostalCodeDTO = mstPostalCodeMapper.toDto(mstPostalCode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstPostalCodeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstPostalCode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstPostalCodeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamMstPostalCode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstPostalCodeSearchRepository.findAll().collectList().block());
        mstPostalCode.setId(longCount.incrementAndGet());

        // Create the MstPostalCode
        MstPostalCodeDTO mstPostalCodeDTO = mstPostalCodeMapper.toDto(mstPostalCode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstPostalCodeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstPostalCode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstPostalCodeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteMstPostalCode() {
        // Initialize the database
        insertedMstPostalCode = mstPostalCodeRepository.save(mstPostalCode).block();
        mstPostalCodeRepository.save(mstPostalCode).block();
        mstPostalCodeSearchRepository.save(mstPostalCode).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstPostalCodeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the mstPostalCode
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, mstPostalCode.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstPostalCodeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchMstPostalCode() {
        // Initialize the database
        insertedMstPostalCode = mstPostalCodeRepository.save(mstPostalCode).block();
        mstPostalCodeSearchRepository.save(mstPostalCode).block();

        // Search the mstPostalCode
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + mstPostalCode.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(mstPostalCode.getId().intValue()))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE));
    }

    protected long getRepositoryCount() {
        return mstPostalCodeRepository.count().block();
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

    protected MstPostalCode getPersistedMstPostalCode(MstPostalCode mstPostalCode) {
        return mstPostalCodeRepository.findById(mstPostalCode.getId()).block();
    }

    protected void assertPersistedMstPostalCodeToMatchAllProperties(MstPostalCode expectedMstPostalCode) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstPostalCodeAllPropertiesEquals(expectedMstPostalCode, getPersistedMstPostalCode(expectedMstPostalCode));
        assertMstPostalCodeUpdatableFieldsEquals(expectedMstPostalCode, getPersistedMstPostalCode(expectedMstPostalCode));
    }

    protected void assertPersistedMstPostalCodeToMatchUpdatableProperties(MstPostalCode expectedMstPostalCode) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstPostalCodeAllUpdatablePropertiesEquals(expectedMstPostalCode, getPersistedMstPostalCode(expectedMstPostalCode));
        assertMstPostalCodeUpdatableFieldsEquals(expectedMstPostalCode, getPersistedMstPostalCode(expectedMstPostalCode));
    }
}
