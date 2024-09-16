package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.EmployeeAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Department;
import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.repository.DepartmentRepository;
import com.mycompany.myapp.repository.EmployeeRepository;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.search.EmployeeSearchRepository;
import com.mycompany.myapp.service.dto.EmployeeDTO;
import com.mycompany.myapp.service.mapper.EmployeeMapper;
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
 * Integration tests for the {@link EmployeeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EmployeeResourceIT {

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

    private static final String ENTITY_API_URL = "/api/employees";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/employees/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private EmployeeSearchRepository employeeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Employee employee;

    private Employee insertedEmployee;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Employee createEntity(EntityManager em) {
        Employee employee = new Employee()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .hireDate(DEFAULT_HIRE_DATE)
            .salary(DEFAULT_SALARY)
            .commissionPct(DEFAULT_COMMISSION_PCT);
        return employee;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Employee createUpdatedEntity(EntityManager em) {
        Employee employee = new Employee()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .hireDate(UPDATED_HIRE_DATE)
            .salary(UPDATED_SALARY)
            .commissionPct(UPDATED_COMMISSION_PCT);
        return employee;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Employee.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        employee = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedEmployee != null) {
            employeeRepository.delete(insertedEmployee).block();
            employeeSearchRepository.delete(insertedEmployee).block();
            insertedEmployee = null;
        }
        deleteEntities(em);
    }

    @Test
    void createEmployee() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeeSearchRepository.findAll().collectList().block());
        // Create the Employee
        EmployeeDTO employeeDTO = employeeMapper.toDto(employee);
        var returnedEmployeeDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(employeeDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(EmployeeDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Employee in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEmployee = employeeMapper.toEntity(returnedEmployeeDTO);
        assertEmployeeUpdatableFieldsEquals(returnedEmployee, getPersistedEmployee(returnedEmployee));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeeSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedEmployee = returnedEmployee;
    }

    @Test
    void createEmployeeWithExistingId() throws Exception {
        // Create the Employee with an existing ID
        employee.setId(1L);
        EmployeeDTO employeeDTO = employeeMapper.toDto(employee);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeeSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(employeeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllEmployees() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList
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
            .value(hasItem(employee.getId().intValue()))
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
    void getEmployee() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get the employee
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, employee.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(employee.getId().intValue()))
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
    void getEmployeesByIdFiltering() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        Long id = employee.getId();

        defaultEmployeeFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultEmployeeFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultEmployeeFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllEmployeesByFirstNameIsEqualToSomething() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where firstName equals to
        defaultEmployeeFiltering("firstName.equals=" + DEFAULT_FIRST_NAME, "firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    void getAllEmployeesByFirstNameIsInShouldWork() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where firstName in
        defaultEmployeeFiltering("firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME, "firstName.in=" + UPDATED_FIRST_NAME);
    }

    @Test
    void getAllEmployeesByFirstNameIsNullOrNotNull() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where firstName is not null
        defaultEmployeeFiltering("firstName.specified=true", "firstName.specified=false");
    }

    @Test
    void getAllEmployeesByFirstNameContainsSomething() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where firstName contains
        defaultEmployeeFiltering("firstName.contains=" + DEFAULT_FIRST_NAME, "firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    void getAllEmployeesByFirstNameNotContainsSomething() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where firstName does not contain
        defaultEmployeeFiltering("firstName.doesNotContain=" + UPDATED_FIRST_NAME, "firstName.doesNotContain=" + DEFAULT_FIRST_NAME);
    }

    @Test
    void getAllEmployeesByLastNameIsEqualToSomething() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where lastName equals to
        defaultEmployeeFiltering("lastName.equals=" + DEFAULT_LAST_NAME, "lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    void getAllEmployeesByLastNameIsInShouldWork() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where lastName in
        defaultEmployeeFiltering("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME, "lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    void getAllEmployeesByLastNameIsNullOrNotNull() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where lastName is not null
        defaultEmployeeFiltering("lastName.specified=true", "lastName.specified=false");
    }

    @Test
    void getAllEmployeesByLastNameContainsSomething() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where lastName contains
        defaultEmployeeFiltering("lastName.contains=" + DEFAULT_LAST_NAME, "lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    void getAllEmployeesByLastNameNotContainsSomething() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where lastName does not contain
        defaultEmployeeFiltering("lastName.doesNotContain=" + UPDATED_LAST_NAME, "lastName.doesNotContain=" + DEFAULT_LAST_NAME);
    }

    @Test
    void getAllEmployeesByEmailIsEqualToSomething() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where email equals to
        defaultEmployeeFiltering("email.equals=" + DEFAULT_EMAIL, "email.equals=" + UPDATED_EMAIL);
    }

    @Test
    void getAllEmployeesByEmailIsInShouldWork() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where email in
        defaultEmployeeFiltering("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL, "email.in=" + UPDATED_EMAIL);
    }

    @Test
    void getAllEmployeesByEmailIsNullOrNotNull() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where email is not null
        defaultEmployeeFiltering("email.specified=true", "email.specified=false");
    }

    @Test
    void getAllEmployeesByEmailContainsSomething() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where email contains
        defaultEmployeeFiltering("email.contains=" + DEFAULT_EMAIL, "email.contains=" + UPDATED_EMAIL);
    }

    @Test
    void getAllEmployeesByEmailNotContainsSomething() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where email does not contain
        defaultEmployeeFiltering("email.doesNotContain=" + UPDATED_EMAIL, "email.doesNotContain=" + DEFAULT_EMAIL);
    }

    @Test
    void getAllEmployeesByPhoneNumberIsEqualToSomething() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where phoneNumber equals to
        defaultEmployeeFiltering("phoneNumber.equals=" + DEFAULT_PHONE_NUMBER, "phoneNumber.equals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    void getAllEmployeesByPhoneNumberIsInShouldWork() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where phoneNumber in
        defaultEmployeeFiltering(
            "phoneNumber.in=" + DEFAULT_PHONE_NUMBER + "," + UPDATED_PHONE_NUMBER,
            "phoneNumber.in=" + UPDATED_PHONE_NUMBER
        );
    }

    @Test
    void getAllEmployeesByPhoneNumberIsNullOrNotNull() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where phoneNumber is not null
        defaultEmployeeFiltering("phoneNumber.specified=true", "phoneNumber.specified=false");
    }

    @Test
    void getAllEmployeesByPhoneNumberContainsSomething() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where phoneNumber contains
        defaultEmployeeFiltering("phoneNumber.contains=" + DEFAULT_PHONE_NUMBER, "phoneNumber.contains=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    void getAllEmployeesByPhoneNumberNotContainsSomething() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where phoneNumber does not contain
        defaultEmployeeFiltering(
            "phoneNumber.doesNotContain=" + UPDATED_PHONE_NUMBER,
            "phoneNumber.doesNotContain=" + DEFAULT_PHONE_NUMBER
        );
    }

    @Test
    void getAllEmployeesByHireDateIsEqualToSomething() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where hireDate equals to
        defaultEmployeeFiltering("hireDate.equals=" + DEFAULT_HIRE_DATE, "hireDate.equals=" + UPDATED_HIRE_DATE);
    }

    @Test
    void getAllEmployeesByHireDateIsInShouldWork() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where hireDate in
        defaultEmployeeFiltering("hireDate.in=" + DEFAULT_HIRE_DATE + "," + UPDATED_HIRE_DATE, "hireDate.in=" + UPDATED_HIRE_DATE);
    }

    @Test
    void getAllEmployeesByHireDateIsNullOrNotNull() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where hireDate is not null
        defaultEmployeeFiltering("hireDate.specified=true", "hireDate.specified=false");
    }

    @Test
    void getAllEmployeesBySalaryIsEqualToSomething() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where salary equals to
        defaultEmployeeFiltering("salary.equals=" + DEFAULT_SALARY, "salary.equals=" + UPDATED_SALARY);
    }

    @Test
    void getAllEmployeesBySalaryIsInShouldWork() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where salary in
        defaultEmployeeFiltering("salary.in=" + DEFAULT_SALARY + "," + UPDATED_SALARY, "salary.in=" + UPDATED_SALARY);
    }

    @Test
    void getAllEmployeesBySalaryIsNullOrNotNull() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where salary is not null
        defaultEmployeeFiltering("salary.specified=true", "salary.specified=false");
    }

    @Test
    void getAllEmployeesBySalaryIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where salary is greater than or equal to
        defaultEmployeeFiltering("salary.greaterThanOrEqual=" + DEFAULT_SALARY, "salary.greaterThanOrEqual=" + UPDATED_SALARY);
    }

    @Test
    void getAllEmployeesBySalaryIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where salary is less than or equal to
        defaultEmployeeFiltering("salary.lessThanOrEqual=" + DEFAULT_SALARY, "salary.lessThanOrEqual=" + SMALLER_SALARY);
    }

    @Test
    void getAllEmployeesBySalaryIsLessThanSomething() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where salary is less than
        defaultEmployeeFiltering("salary.lessThan=" + UPDATED_SALARY, "salary.lessThan=" + DEFAULT_SALARY);
    }

    @Test
    void getAllEmployeesBySalaryIsGreaterThanSomething() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where salary is greater than
        defaultEmployeeFiltering("salary.greaterThan=" + SMALLER_SALARY, "salary.greaterThan=" + DEFAULT_SALARY);
    }

    @Test
    void getAllEmployeesByCommissionPctIsEqualToSomething() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where commissionPct equals to
        defaultEmployeeFiltering("commissionPct.equals=" + DEFAULT_COMMISSION_PCT, "commissionPct.equals=" + UPDATED_COMMISSION_PCT);
    }

    @Test
    void getAllEmployeesByCommissionPctIsInShouldWork() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where commissionPct in
        defaultEmployeeFiltering(
            "commissionPct.in=" + DEFAULT_COMMISSION_PCT + "," + UPDATED_COMMISSION_PCT,
            "commissionPct.in=" + UPDATED_COMMISSION_PCT
        );
    }

    @Test
    void getAllEmployeesByCommissionPctIsNullOrNotNull() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where commissionPct is not null
        defaultEmployeeFiltering("commissionPct.specified=true", "commissionPct.specified=false");
    }

    @Test
    void getAllEmployeesByCommissionPctIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where commissionPct is greater than or equal to
        defaultEmployeeFiltering(
            "commissionPct.greaterThanOrEqual=" + DEFAULT_COMMISSION_PCT,
            "commissionPct.greaterThanOrEqual=" + UPDATED_COMMISSION_PCT
        );
    }

    @Test
    void getAllEmployeesByCommissionPctIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where commissionPct is less than or equal to
        defaultEmployeeFiltering(
            "commissionPct.lessThanOrEqual=" + DEFAULT_COMMISSION_PCT,
            "commissionPct.lessThanOrEqual=" + SMALLER_COMMISSION_PCT
        );
    }

    @Test
    void getAllEmployeesByCommissionPctIsLessThanSomething() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where commissionPct is less than
        defaultEmployeeFiltering("commissionPct.lessThan=" + UPDATED_COMMISSION_PCT, "commissionPct.lessThan=" + DEFAULT_COMMISSION_PCT);
    }

    @Test
    void getAllEmployeesByCommissionPctIsGreaterThanSomething() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        // Get all the employeeList where commissionPct is greater than
        defaultEmployeeFiltering(
            "commissionPct.greaterThan=" + SMALLER_COMMISSION_PCT,
            "commissionPct.greaterThan=" + DEFAULT_COMMISSION_PCT
        );
    }

    @Test
    void getAllEmployeesByManagerIsEqualToSomething() {
        Employee manager = EmployeeResourceIT.createEntity(em);
        employeeRepository.save(manager).block();
        Long managerId = manager.getId();
        employee.setManagerId(managerId);
        insertedEmployee = employeeRepository.save(employee).block();
        // Get all the employeeList where manager equals to managerId
        defaultEmployeeShouldBeFound("managerId.equals=" + managerId);

        // Get all the employeeList where manager equals to (managerId + 1)
        defaultEmployeeShouldNotBeFound("managerId.equals=" + (managerId + 1));
    }

    @Test
    void getAllEmployeesByDepartmentIsEqualToSomething() {
        Department department = DepartmentResourceIT.createEntity(em);
        departmentRepository.save(department).block();
        Long departmentId = department.getId();
        employee.setDepartmentId(departmentId);
        insertedEmployee = employeeRepository.save(employee).block();
        // Get all the employeeList where department equals to departmentId
        defaultEmployeeShouldBeFound("departmentId.equals=" + departmentId);

        // Get all the employeeList where department equals to (departmentId + 1)
        defaultEmployeeShouldNotBeFound("departmentId.equals=" + (departmentId + 1));
    }

    private void defaultEmployeeFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultEmployeeShouldBeFound(shouldBeFound);
        defaultEmployeeShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEmployeeShouldBeFound(String filter) {
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
            .value(hasItem(employee.getId().intValue()))
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
    private void defaultEmployeeShouldNotBeFound(String filter) {
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
    void getNonExistingEmployee() {
        // Get the employee
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingEmployee() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        employeeSearchRepository.save(employee).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeeSearchRepository.findAll().collectList().block());

        // Update the employee
        Employee updatedEmployee = employeeRepository.findById(employee.getId()).block();
        updatedEmployee
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .hireDate(UPDATED_HIRE_DATE)
            .salary(UPDATED_SALARY)
            .commissionPct(UPDATED_COMMISSION_PCT);
        EmployeeDTO employeeDTO = employeeMapper.toDto(updatedEmployee);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, employeeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(employeeDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEmployeeToMatchAllProperties(updatedEmployee);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeeSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Employee> employeeSearchList = Streamable.of(employeeSearchRepository.findAll().collectList().block()).toList();
                Employee testEmployeeSearch = employeeSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertEmployeeAllPropertiesEquals(testEmployeeSearch, updatedEmployee);
                assertEmployeeUpdatableFieldsEquals(testEmployeeSearch, updatedEmployee);
            });
    }

    @Test
    void putNonExistingEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeeSearchRepository.findAll().collectList().block());
        employee.setId(longCount.incrementAndGet());

        // Create the Employee
        EmployeeDTO employeeDTO = employeeMapper.toDto(employee);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, employeeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(employeeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeeSearchRepository.findAll().collectList().block());
        employee.setId(longCount.incrementAndGet());

        // Create the Employee
        EmployeeDTO employeeDTO = employeeMapper.toDto(employee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(employeeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeeSearchRepository.findAll().collectList().block());
        employee.setId(longCount.incrementAndGet());

        // Create the Employee
        EmployeeDTO employeeDTO = employeeMapper.toDto(employee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(employeeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateEmployeeWithPatch() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the employee using partial update
        Employee partialUpdatedEmployee = new Employee();
        partialUpdatedEmployee.setId(employee.getId());

        partialUpdatedEmployee.email(UPDATED_EMAIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEmployee.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEmployee))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Employee in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEmployeeUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedEmployee, employee), getPersistedEmployee(employee));
    }

    @Test
    void fullUpdateEmployeeWithPatch() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the employee using partial update
        Employee partialUpdatedEmployee = new Employee();
        partialUpdatedEmployee.setId(employee.getId());

        partialUpdatedEmployee
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .hireDate(UPDATED_HIRE_DATE)
            .salary(UPDATED_SALARY)
            .commissionPct(UPDATED_COMMISSION_PCT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEmployee.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEmployee))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Employee in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEmployeeUpdatableFieldsEquals(partialUpdatedEmployee, getPersistedEmployee(partialUpdatedEmployee));
    }

    @Test
    void patchNonExistingEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeeSearchRepository.findAll().collectList().block());
        employee.setId(longCount.incrementAndGet());

        // Create the Employee
        EmployeeDTO employeeDTO = employeeMapper.toDto(employee);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, employeeDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(employeeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeeSearchRepository.findAll().collectList().block());
        employee.setId(longCount.incrementAndGet());

        // Create the Employee
        EmployeeDTO employeeDTO = employeeMapper.toDto(employee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(employeeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeeSearchRepository.findAll().collectList().block());
        employee.setId(longCount.incrementAndGet());

        // Create the Employee
        EmployeeDTO employeeDTO = employeeMapper.toDto(employee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(employeeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteEmployee() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();
        employeeRepository.save(employee).block();
        employeeSearchRepository.save(employee).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(employeeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the employee
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, employee.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(employeeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchEmployee() {
        // Initialize the database
        insertedEmployee = employeeRepository.save(employee).block();
        employeeSearchRepository.save(employee).block();

        // Search the employee
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + employee.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(employee.getId().intValue()))
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
        return employeeRepository.count().block();
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

    protected Employee getPersistedEmployee(Employee employee) {
        return employeeRepository.findById(employee.getId()).block();
    }

    protected void assertPersistedEmployeeToMatchAllProperties(Employee expectedEmployee) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEmployeeAllPropertiesEquals(expectedEmployee, getPersistedEmployee(expectedEmployee));
        assertEmployeeUpdatableFieldsEquals(expectedEmployee, getPersistedEmployee(expectedEmployee));
    }

    protected void assertPersistedEmployeeToMatchUpdatableProperties(Employee expectedEmployee) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEmployeeAllUpdatablePropertiesEquals(expectedEmployee, getPersistedEmployee(expectedEmployee));
        assertEmployeeUpdatableFieldsEquals(expectedEmployee, getPersistedEmployee(expectedEmployee));
    }
}
