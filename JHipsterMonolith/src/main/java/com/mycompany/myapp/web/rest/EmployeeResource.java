package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.EmployeeCriteria;
import com.mycompany.myapp.repository.EmployeeRepository;
import com.mycompany.myapp.service.EmployeeService;
import com.mycompany.myapp.service.dto.EmployeeDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Employee}.
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeResource {

    private static final Logger log = LoggerFactory.getLogger(EmployeeResource.class);

    private static final String ENTITY_NAME = "employee";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EmployeeService employeeService;

    private final EmployeeRepository employeeRepository;

    public EmployeeResource(EmployeeService employeeService, EmployeeRepository employeeRepository) {
        this.employeeService = employeeService;
        this.employeeRepository = employeeRepository;
    }

    /**
     * {@code POST  /employees} : Create a new employee.
     *
     * @param employeeDTO the employeeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new employeeDTO, or with status {@code 400 (Bad Request)} if the employee has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<EmployeeDTO>> createEmployee(@RequestBody EmployeeDTO employeeDTO) throws URISyntaxException {
        log.debug("REST request to save Employee : {}", employeeDTO);
        if (employeeDTO.getId() != null) {
            throw new BadRequestAlertException("A new employee cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return employeeService
            .save(employeeDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/employees/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /employees/:id} : Updates an existing employee.
     *
     * @param id the id of the employeeDTO to save.
     * @param employeeDTO the employeeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated employeeDTO,
     * or with status {@code 400 (Bad Request)} if the employeeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the employeeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<EmployeeDTO>> updateEmployee(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody EmployeeDTO employeeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Employee : {}, {}", id, employeeDTO);
        if (employeeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, employeeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return employeeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return employeeService
                    .update(employeeDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(
                        result ->
                            ResponseEntity.ok()
                                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                                .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /employees/:id} : Partial updates given fields of an existing employee, field will ignore if it is null
     *
     * @param id the id of the employeeDTO to save.
     * @param employeeDTO the employeeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated employeeDTO,
     * or with status {@code 400 (Bad Request)} if the employeeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the employeeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the employeeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<EmployeeDTO>> partialUpdateEmployee(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody EmployeeDTO employeeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Employee partially : {}, {}", id, employeeDTO);
        if (employeeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, employeeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return employeeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<EmployeeDTO> result = employeeService.partialUpdate(employeeDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(
                        res ->
                            ResponseEntity.ok()
                                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                                .body(res)
                    );
            });
    }

    /**
     * {@code GET  /employees} : get all the employees.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of employees in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<EmployeeDTO>>> getAllEmployees(
        EmployeeCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get Employees by criteria: {}", criteria);
        return employeeService
            .countByCriteria(criteria)
            .zipWith(employeeService.findByCriteria(criteria, pageable).collectList())
            .map(
                countWithEntities ->
                    ResponseEntity.ok()
                        .headers(
                            PaginationUtil.generatePaginationHttpHeaders(
                                ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                                new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                            )
                        )
                        .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /employees/count} : count all the employees.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countEmployees(EmployeeCriteria criteria) {
        log.debug("REST request to count Employees by criteria: {}", criteria);
        return employeeService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /employees/:id} : get the "id" employee.
     *
     * @param id the id of the employeeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the employeeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<EmployeeDTO>> getEmployee(@PathVariable("id") Long id) {
        log.debug("REST request to get Employee : {}", id);
        Mono<EmployeeDTO> employeeDTO = employeeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(employeeDTO);
    }

    /**
     * {@code DELETE  /employees/:id} : delete the "id" employee.
     *
     * @param id the id of the employeeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteEmployee(@PathVariable("id") Long id) {
        log.debug("REST request to delete Employee : {}", id);
        return employeeService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }

    /**
     * {@code SEARCH  /employees/_search?query=:query} : search for the employee corresponding
     * to the query.
     *
     * @param query the query of the employee search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<EmployeeDTO>>> searchEmployees(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of Employees for query {}", query);
        return employeeService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(employeeService.search(query, pageable)));
    }
}
