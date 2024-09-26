package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.MstEmployeeAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.MstDepartment;
import com.mycompany.myapp.domain.MstEmployee;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MstDepartmentRepository;
import com.mycompany.myapp.repository.MstEmployeeRepository;
import com.mycompany.myapp.repository.search.MstEmployeeSearchRepository;
import com.mycompany.myapp.service.dto.MstEmployeeDTO;
import com.mycompany.myapp.service.mapper.MstEmployeeMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link MstEmployeeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MstEmployeeResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final Instant DEFAULT_HIRE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_HIRE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Long DEFAULT_SALARY = 1L;
    private static final Long UPDATED_SALARY = 2L;
    private static final Long SMALLER_SALARY = 1L - 1L;

    private static final Long DEFAULT_COMMISSION_PCT = 1L;
    private static final Long UPDATED_COMMISSION_PCT = 2L;
    private static final Long SMALLER_COMMISSION_PCT = 1L - 1L;

    private static final String ENTITY_API_URL = "/api/mst-employees";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/mst-employees/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MstEmployeeMapper mstEmployeeMapper;

    @Autowired
    private MstEmployeeSearchRepository mstEmployeeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private MstEmployee mstEmployee;

    private MstEmployee insertedMstEmployee;

    @Autowired
    private MstEmployeeRepository mstEmployeeRepository;

    @Autowired
    private MstDepartmentRepository mstDepartmentRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstEmployee createEntity(EntityManager em) {
        MstEmployee mstEmployee = new MstEmployee()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .hireDate(DEFAULT_HIRE_DATE)
            .salary(DEFAULT_SALARY)
            .commissionPct(DEFAULT_COMMISSION_PCT);
        return mstEmployee;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MstEmployee createUpdatedEntity(EntityManager em) {
        MstEmployee mstEmployee = new MstEmployee()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .hireDate(UPDATED_HIRE_DATE)
            .salary(UPDATED_SALARY)
            .commissionPct(UPDATED_COMMISSION_PCT);
        return mstEmployee;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(MstEmployee.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        mstEmployee = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedMstEmployee != null) {
            mstEmployeeRepository.delete(insertedMstEmployee).block();
            mstEmployeeSearchRepository.delete(insertedMstEmployee).block();
            insertedMstEmployee = null;
        }
        deleteEntities(em);
    }

    @Test
    void createMstEmployee() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstEmployeeSearchRepository.findAll().collectList().block());
        // Create the MstEmployee
        MstEmployeeDTO mstEmployeeDTO = mstEmployeeMapper.toDto(mstEmployee);
        var returnedMstEmployeeDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstEmployeeDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MstEmployeeDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MstEmployee in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMstEmployee = mstEmployeeMapper.toEntity(returnedMstEmployeeDTO);
        assertMstEmployeeUpdatableFieldsEquals(returnedMstEmployee, getPersistedMstEmployee(returnedMstEmployee));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstEmployeeSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedMstEmployee = returnedMstEmployee;
    }

    @Test
    void createMstEmployeeWithExistingId() throws Exception {
        // Create the MstEmployee with an existing ID
        mstEmployee.setId(1L);
        MstEmployeeDTO mstEmployeeDTO = mstEmployeeMapper.toDto(mstEmployee);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstEmployeeSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstEmployeeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstEmployee in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstEmployeeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllMstEmployees() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList
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
            .value(hasItem(mstEmployee.getId().intValue()))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].phoneNumber")
            .value(hasItem(DEFAULT_PHONE_NUMBER))
            .jsonPath("$.[*].hireDate")
            .value(hasItem(DEFAULT_HIRE_DATE.toString()))
            .jsonPath("$.[*].salary")
            .value(hasItem(DEFAULT_SALARY.intValue()))
            .jsonPath("$.[*].commissionPct")
            .value(hasItem(DEFAULT_COMMISSION_PCT.intValue()));
    }

    @Test
    void getMstEmployee() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get the mstEmployee
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, mstEmployee.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(mstEmployee.getId().intValue()))
            .jsonPath("$.firstName")
            .value(is(DEFAULT_FIRST_NAME))
            .jsonPath("$.lastName")
            .value(is(DEFAULT_LAST_NAME))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL))
            .jsonPath("$.phoneNumber")
            .value(is(DEFAULT_PHONE_NUMBER))
            .jsonPath("$.hireDate")
            .value(is(DEFAULT_HIRE_DATE.toString()))
            .jsonPath("$.salary")
            .value(is(DEFAULT_SALARY.intValue()))
            .jsonPath("$.commissionPct")
            .value(is(DEFAULT_COMMISSION_PCT.intValue()));
    }

    @Test
    void getMstEmployeesByIdFiltering() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        Long id = mstEmployee.getId();

        defaultMstEmployeeFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMstEmployeeFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMstEmployeeFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllMstEmployeesByFirstNameIsEqualToSomething() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where firstName equals to
        defaultMstEmployeeFiltering("firstName.equals=" + DEFAULT_FIRST_NAME, "firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    void getAllMstEmployeesByFirstNameIsInShouldWork() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where firstName in
        defaultMstEmployeeFiltering("firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME, "firstName.in=" + UPDATED_FIRST_NAME);
    }

    @Test
    void getAllMstEmployeesByFirstNameIsNullOrNotNull() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where firstName is not null
        defaultMstEmployeeFiltering("firstName.specified=true", "firstName.specified=false");
    }

    @Test
    void getAllMstEmployeesByFirstNameContainsSomething() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where firstName contains
        defaultMstEmployeeFiltering("firstName.contains=" + DEFAULT_FIRST_NAME, "firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    void getAllMstEmployeesByFirstNameNotContainsSomething() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where firstName does not contain
        defaultMstEmployeeFiltering("firstName.doesNotContain=" + UPDATED_FIRST_NAME, "firstName.doesNotContain=" + DEFAULT_FIRST_NAME);
    }

    @Test
    void getAllMstEmployeesByLastNameIsEqualToSomething() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where lastName equals to
        defaultMstEmployeeFiltering("lastName.equals=" + DEFAULT_LAST_NAME, "lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    void getAllMstEmployeesByLastNameIsInShouldWork() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where lastName in
        defaultMstEmployeeFiltering("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME, "lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    void getAllMstEmployeesByLastNameIsNullOrNotNull() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where lastName is not null
        defaultMstEmployeeFiltering("lastName.specified=true", "lastName.specified=false");
    }

    @Test
    void getAllMstEmployeesByLastNameContainsSomething() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where lastName contains
        defaultMstEmployeeFiltering("lastName.contains=" + DEFAULT_LAST_NAME, "lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    void getAllMstEmployeesByLastNameNotContainsSomething() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where lastName does not contain
        defaultMstEmployeeFiltering("lastName.doesNotContain=" + UPDATED_LAST_NAME, "lastName.doesNotContain=" + DEFAULT_LAST_NAME);
    }

    @Test
    void getAllMstEmployeesByEmailIsEqualToSomething() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where email equals to
        defaultMstEmployeeFiltering("email.equals=" + DEFAULT_EMAIL, "email.equals=" + UPDATED_EMAIL);
    }

    @Test
    void getAllMstEmployeesByEmailIsInShouldWork() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where email in
        defaultMstEmployeeFiltering("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL, "email.in=" + UPDATED_EMAIL);
    }

    @Test
    void getAllMstEmployeesByEmailIsNullOrNotNull() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where email is not null
        defaultMstEmployeeFiltering("email.specified=true", "email.specified=false");
    }

    @Test
    void getAllMstEmployeesByEmailContainsSomething() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where email contains
        defaultMstEmployeeFiltering("email.contains=" + DEFAULT_EMAIL, "email.contains=" + UPDATED_EMAIL);
    }

    @Test
    void getAllMstEmployeesByEmailNotContainsSomething() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where email does not contain
        defaultMstEmployeeFiltering("email.doesNotContain=" + UPDATED_EMAIL, "email.doesNotContain=" + DEFAULT_EMAIL);
    }

    @Test
    void getAllMstEmployeesByPhoneNumberIsEqualToSomething() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where phoneNumber equals to
        defaultMstEmployeeFiltering("phoneNumber.equals=" + DEFAULT_PHONE_NUMBER, "phoneNumber.equals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    void getAllMstEmployeesByPhoneNumberIsInShouldWork() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where phoneNumber in
        defaultMstEmployeeFiltering(
            "phoneNumber.in=" + DEFAULT_PHONE_NUMBER + "," + UPDATED_PHONE_NUMBER,
            "phoneNumber.in=" + UPDATED_PHONE_NUMBER
        );
    }

    @Test
    void getAllMstEmployeesByPhoneNumberIsNullOrNotNull() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where phoneNumber is not null
        defaultMstEmployeeFiltering("phoneNumber.specified=true", "phoneNumber.specified=false");
    }

    @Test
    void getAllMstEmployeesByPhoneNumberContainsSomething() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where phoneNumber contains
        defaultMstEmployeeFiltering("phoneNumber.contains=" + DEFAULT_PHONE_NUMBER, "phoneNumber.contains=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    void getAllMstEmployeesByPhoneNumberNotContainsSomething() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where phoneNumber does not contain
        defaultMstEmployeeFiltering(
            "phoneNumber.doesNotContain=" + UPDATED_PHONE_NUMBER,
            "phoneNumber.doesNotContain=" + DEFAULT_PHONE_NUMBER
        );
    }

    @Test
    void getAllMstEmployeesByHireDateIsEqualToSomething() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where hireDate equals to
        defaultMstEmployeeFiltering("hireDate.equals=" + DEFAULT_HIRE_DATE, "hireDate.equals=" + UPDATED_HIRE_DATE);
    }

    @Test
    void getAllMstEmployeesByHireDateIsInShouldWork() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where hireDate in
        defaultMstEmployeeFiltering("hireDate.in=" + DEFAULT_HIRE_DATE + "," + UPDATED_HIRE_DATE, "hireDate.in=" + UPDATED_HIRE_DATE);
    }

    @Test
    void getAllMstEmployeesByHireDateIsNullOrNotNull() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where hireDate is not null
        defaultMstEmployeeFiltering("hireDate.specified=true", "hireDate.specified=false");
    }

    @Test
    void getAllMstEmployeesBySalaryIsEqualToSomething() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where salary equals to
        defaultMstEmployeeFiltering("salary.equals=" + DEFAULT_SALARY, "salary.equals=" + UPDATED_SALARY);
    }

    @Test
    void getAllMstEmployeesBySalaryIsInShouldWork() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where salary in
        defaultMstEmployeeFiltering("salary.in=" + DEFAULT_SALARY + "," + UPDATED_SALARY, "salary.in=" + UPDATED_SALARY);
    }

    @Test
    void getAllMstEmployeesBySalaryIsNullOrNotNull() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where salary is not null
        defaultMstEmployeeFiltering("salary.specified=true", "salary.specified=false");
    }

    @Test
    void getAllMstEmployeesBySalaryIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where salary is greater than or equal to
        defaultMstEmployeeFiltering("salary.greaterThanOrEqual=" + DEFAULT_SALARY, "salary.greaterThanOrEqual=" + UPDATED_SALARY);
    }

    @Test
    void getAllMstEmployeesBySalaryIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where salary is less than or equal to
        defaultMstEmployeeFiltering("salary.lessThanOrEqual=" + DEFAULT_SALARY, "salary.lessThanOrEqual=" + SMALLER_SALARY);
    }

    @Test
    void getAllMstEmployeesBySalaryIsLessThanSomething() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where salary is less than
        defaultMstEmployeeFiltering("salary.lessThan=" + UPDATED_SALARY, "salary.lessThan=" + DEFAULT_SALARY);
    }

    @Test
    void getAllMstEmployeesBySalaryIsGreaterThanSomething() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where salary is greater than
        defaultMstEmployeeFiltering("salary.greaterThan=" + SMALLER_SALARY, "salary.greaterThan=" + DEFAULT_SALARY);
    }

    @Test
    void getAllMstEmployeesByCommissionPctIsEqualToSomething() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where commissionPct equals to
        defaultMstEmployeeFiltering("commissionPct.equals=" + DEFAULT_COMMISSION_PCT, "commissionPct.equals=" + UPDATED_COMMISSION_PCT);
    }

    @Test
    void getAllMstEmployeesByCommissionPctIsInShouldWork() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where commissionPct in
        defaultMstEmployeeFiltering(
            "commissionPct.in=" + DEFAULT_COMMISSION_PCT + "," + UPDATED_COMMISSION_PCT,
            "commissionPct.in=" + UPDATED_COMMISSION_PCT
        );
    }

    @Test
    void getAllMstEmployeesByCommissionPctIsNullOrNotNull() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where commissionPct is not null
        defaultMstEmployeeFiltering("commissionPct.specified=true", "commissionPct.specified=false");
    }

    @Test
    void getAllMstEmployeesByCommissionPctIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where commissionPct is greater than or equal to
        defaultMstEmployeeFiltering(
            "commissionPct.greaterThanOrEqual=" + DEFAULT_COMMISSION_PCT,
            "commissionPct.greaterThanOrEqual=" + UPDATED_COMMISSION_PCT
        );
    }

    @Test
    void getAllMstEmployeesByCommissionPctIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where commissionPct is less than or equal to
        defaultMstEmployeeFiltering(
            "commissionPct.lessThanOrEqual=" + DEFAULT_COMMISSION_PCT,
            "commissionPct.lessThanOrEqual=" + SMALLER_COMMISSION_PCT
        );
    }

    @Test
    void getAllMstEmployeesByCommissionPctIsLessThanSomething() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where commissionPct is less than
        defaultMstEmployeeFiltering("commissionPct.lessThan=" + UPDATED_COMMISSION_PCT, "commissionPct.lessThan=" + DEFAULT_COMMISSION_PCT);
    }

    @Test
    void getAllMstEmployeesByCommissionPctIsGreaterThanSomething() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        // Get all the mstEmployeeList where commissionPct is greater than
        defaultMstEmployeeFiltering(
            "commissionPct.greaterThan=" + SMALLER_COMMISSION_PCT,
            "commissionPct.greaterThan=" + DEFAULT_COMMISSION_PCT
        );
    }

    @Test
    void getAllMstEmployeesByManagerIsEqualToSomething() {
        MstEmployee manager = MstEmployeeResourceIT.createEntity(em);
        mstEmployeeRepository.save(manager).block();
        Long managerId = manager.getId();
        mstEmployee.setManagerId(managerId);
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();
        // Get all the mstEmployeeList where manager equals to managerId
        defaultMstEmployeeShouldBeFound("managerId.equals=" + managerId);

        // Get all the mstEmployeeList where manager equals to (managerId + 1)
        defaultMstEmployeeShouldNotBeFound("managerId.equals=" + (managerId + 1));
    }

    @Test
    void getAllMstEmployeesByDepartmentIsEqualToSomething() {
        MstDepartment department = MstDepartmentResourceIT.createEntity(em);
        mstDepartmentRepository.save(department).block();
        Long departmentId = department.getId();
        mstEmployee.setDepartmentId(departmentId);
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();
        // Get all the mstEmployeeList where department equals to departmentId
        defaultMstEmployeeShouldBeFound("departmentId.equals=" + departmentId);

        // Get all the mstEmployeeList where department equals to (departmentId + 1)
        defaultMstEmployeeShouldNotBeFound("departmentId.equals=" + (departmentId + 1));
    }

    private void defaultMstEmployeeFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultMstEmployeeShouldBeFound(shouldBeFound);
        defaultMstEmployeeShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMstEmployeeShouldBeFound(String filter) {
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
            .value(hasItem(mstEmployee.getId().intValue()))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].phoneNumber")
            .value(hasItem(DEFAULT_PHONE_NUMBER))
            .jsonPath("$.[*].hireDate")
            .value(hasItem(DEFAULT_HIRE_DATE.toString()))
            .jsonPath("$.[*].salary")
            .value(hasItem(DEFAULT_SALARY.intValue()))
            .jsonPath("$.[*].commissionPct")
            .value(hasItem(DEFAULT_COMMISSION_PCT.intValue()));

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
    private void defaultMstEmployeeShouldNotBeFound(String filter) {
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
    void getNonExistingMstEmployee() {
        // Get the mstEmployee
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMstEmployee() throws Exception {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        mstEmployeeSearchRepository.save(mstEmployee).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstEmployeeSearchRepository.findAll().collectList().block());

        // Update the mstEmployee
        MstEmployee updatedMstEmployee = mstEmployeeRepository.findById(mstEmployee.getId()).block();
        updatedMstEmployee
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .hireDate(UPDATED_HIRE_DATE)
            .salary(UPDATED_SALARY)
            .commissionPct(UPDATED_COMMISSION_PCT);
        MstEmployeeDTO mstEmployeeDTO = mstEmployeeMapper.toDto(updatedMstEmployee);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstEmployeeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstEmployeeDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstEmployee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMstEmployeeToMatchAllProperties(updatedMstEmployee);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstEmployeeSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<MstEmployee> mstEmployeeSearchList = Streamable.of(
                    mstEmployeeSearchRepository.findAll().collectList().block()
                ).toList();
                MstEmployee testMstEmployeeSearch = mstEmployeeSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertMstEmployeeAllPropertiesEquals(testMstEmployeeSearch, updatedMstEmployee);
                assertMstEmployeeUpdatableFieldsEquals(testMstEmployeeSearch, updatedMstEmployee);
            });
    }

    @Test
    void putNonExistingMstEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstEmployeeSearchRepository.findAll().collectList().block());
        mstEmployee.setId(longCount.incrementAndGet());

        // Create the MstEmployee
        MstEmployeeDTO mstEmployeeDTO = mstEmployeeMapper.toDto(mstEmployee);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mstEmployeeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstEmployeeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstEmployee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstEmployeeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchMstEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstEmployeeSearchRepository.findAll().collectList().block());
        mstEmployee.setId(longCount.incrementAndGet());

        // Create the MstEmployee
        MstEmployeeDTO mstEmployeeDTO = mstEmployeeMapper.toDto(mstEmployee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstEmployeeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstEmployee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstEmployeeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamMstEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstEmployeeSearchRepository.findAll().collectList().block());
        mstEmployee.setId(longCount.incrementAndGet());

        // Create the MstEmployee
        MstEmployeeDTO mstEmployeeDTO = mstEmployeeMapper.toDto(mstEmployee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mstEmployeeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstEmployee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstEmployeeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateMstEmployeeWithPatch() throws Exception {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstEmployee using partial update
        MstEmployee partialUpdatedMstEmployee = new MstEmployee();
        partialUpdatedMstEmployee.setId(mstEmployee.getId());

        partialUpdatedMstEmployee
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .hireDate(UPDATED_HIRE_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstEmployee.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstEmployee))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstEmployee in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstEmployeeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMstEmployee, mstEmployee),
            getPersistedMstEmployee(mstEmployee)
        );
    }

    @Test
    void fullUpdateMstEmployeeWithPatch() throws Exception {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mstEmployee using partial update
        MstEmployee partialUpdatedMstEmployee = new MstEmployee();
        partialUpdatedMstEmployee.setId(mstEmployee.getId());

        partialUpdatedMstEmployee
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .hireDate(UPDATED_HIRE_DATE)
            .salary(UPDATED_SALARY)
            .commissionPct(UPDATED_COMMISSION_PCT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMstEmployee.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMstEmployee))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MstEmployee in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMstEmployeeUpdatableFieldsEquals(partialUpdatedMstEmployee, getPersistedMstEmployee(partialUpdatedMstEmployee));
    }

    @Test
    void patchNonExistingMstEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstEmployeeSearchRepository.findAll().collectList().block());
        mstEmployee.setId(longCount.incrementAndGet());

        // Create the MstEmployee
        MstEmployeeDTO mstEmployeeDTO = mstEmployeeMapper.toDto(mstEmployee);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, mstEmployeeDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstEmployeeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstEmployee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstEmployeeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchMstEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstEmployeeSearchRepository.findAll().collectList().block());
        mstEmployee.setId(longCount.incrementAndGet());

        // Create the MstEmployee
        MstEmployeeDTO mstEmployeeDTO = mstEmployeeMapper.toDto(mstEmployee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstEmployeeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MstEmployee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstEmployeeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamMstEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstEmployeeSearchRepository.findAll().collectList().block());
        mstEmployee.setId(longCount.incrementAndGet());

        // Create the MstEmployee
        MstEmployeeDTO mstEmployeeDTO = mstEmployeeMapper.toDto(mstEmployee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mstEmployeeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MstEmployee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstEmployeeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteMstEmployee() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();
        mstEmployeeRepository.save(mstEmployee).block();
        mstEmployeeSearchRepository.save(mstEmployee).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mstEmployeeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the mstEmployee
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, mstEmployee.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mstEmployeeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchMstEmployee() {
        // Initialize the database
        insertedMstEmployee = mstEmployeeRepository.save(mstEmployee).block();
        mstEmployeeSearchRepository.save(mstEmployee).block();

        // Search the mstEmployee
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + mstEmployee.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(mstEmployee.getId().intValue()))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].phoneNumber")
            .value(hasItem(DEFAULT_PHONE_NUMBER))
            .jsonPath("$.[*].hireDate")
            .value(hasItem(DEFAULT_HIRE_DATE.toString()))
            .jsonPath("$.[*].salary")
            .value(hasItem(DEFAULT_SALARY.intValue()))
            .jsonPath("$.[*].commissionPct")
            .value(hasItem(DEFAULT_COMMISSION_PCT.intValue()));
    }

    protected long getRepositoryCount() {
        return mstEmployeeRepository.count().block();
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

    protected MstEmployee getPersistedMstEmployee(MstEmployee mstEmployee) {
        return mstEmployeeRepository.findById(mstEmployee.getId()).block();
    }

    protected void assertPersistedMstEmployeeToMatchAllProperties(MstEmployee expectedMstEmployee) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstEmployeeAllPropertiesEquals(expectedMstEmployee, getPersistedMstEmployee(expectedMstEmployee));
        assertMstEmployeeUpdatableFieldsEquals(expectedMstEmployee, getPersistedMstEmployee(expectedMstEmployee));
    }

    protected void assertPersistedMstEmployeeToMatchUpdatableProperties(MstEmployee expectedMstEmployee) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMstEmployeeAllUpdatablePropertiesEquals(expectedMstEmployee, getPersistedMstEmployee(expectedMstEmployee));
        assertMstEmployeeUpdatableFieldsEquals(expectedMstEmployee, getPersistedMstEmployee(expectedMstEmployee));
    }
}
