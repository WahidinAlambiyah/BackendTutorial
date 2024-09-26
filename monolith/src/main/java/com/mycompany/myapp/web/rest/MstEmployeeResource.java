package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.MstEmployeeCriteria;
import com.mycompany.myapp.repository.MstEmployeeRepository;
import com.mycompany.myapp.service.MstEmployeeService;
import com.mycompany.myapp.service.dto.MstEmployeeDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.MstEmployee}.
 */
@RestController
@RequestMapping("/api/mst-employees")
public class MstEmployeeResource {

    private static final Logger log = LoggerFactory.getLogger(MstEmployeeResource.class);

    private static final String ENTITY_NAME = "mstEmployee";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MstEmployeeService mstEmployeeService;

    private final MstEmployeeRepository mstEmployeeRepository;

    public MstEmployeeResource(MstEmployeeService mstEmployeeService, MstEmployeeRepository mstEmployeeRepository) {
        this.mstEmployeeService = mstEmployeeService;
        this.mstEmployeeRepository = mstEmployeeRepository;
    }

    /**
     * {@code POST  /mst-employees} : Create a new mstEmployee.
     *
     * @param mstEmployeeDTO the mstEmployeeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mstEmployeeDTO, or with status {@code 400 (Bad Request)} if the mstEmployee has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MstEmployeeDTO>> createMstEmployee(@RequestBody MstEmployeeDTO mstEmployeeDTO) throws URISyntaxException {
        log.debug("REST request to save MstEmployee : {}", mstEmployeeDTO);
        if (mstEmployeeDTO.getId() != null) {
            throw new BadRequestAlertException("A new mstEmployee cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return mstEmployeeService
            .save(mstEmployeeDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/mst-employees/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /mst-employees/:id} : Updates an existing mstEmployee.
     *
     * @param id the id of the mstEmployeeDTO to save.
     * @param mstEmployeeDTO the mstEmployeeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstEmployeeDTO,
     * or with status {@code 400 (Bad Request)} if the mstEmployeeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mstEmployeeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MstEmployeeDTO>> updateMstEmployee(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MstEmployeeDTO mstEmployeeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update MstEmployee : {}, {}", id, mstEmployeeDTO);
        if (mstEmployeeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstEmployeeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstEmployeeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return mstEmployeeService
                    .update(mstEmployeeDTO)
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
     * {@code PATCH  /mst-employees/:id} : Partial updates given fields of an existing mstEmployee, field will ignore if it is null
     *
     * @param id the id of the mstEmployeeDTO to save.
     * @param mstEmployeeDTO the mstEmployeeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstEmployeeDTO,
     * or with status {@code 400 (Bad Request)} if the mstEmployeeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the mstEmployeeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the mstEmployeeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MstEmployeeDTO>> partialUpdateMstEmployee(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MstEmployeeDTO mstEmployeeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update MstEmployee partially : {}, {}", id, mstEmployeeDTO);
        if (mstEmployeeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstEmployeeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstEmployeeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<MstEmployeeDTO> result = mstEmployeeService.partialUpdate(mstEmployeeDTO);

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
     * {@code GET  /mst-employees} : get all the mstEmployees.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mstEmployees in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<MstEmployeeDTO>>> getAllMstEmployees(
        MstEmployeeCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get MstEmployees by criteria: {}", criteria);
        return mstEmployeeService
            .countByCriteria(criteria)
            .zipWith(mstEmployeeService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /mst-employees/count} : count all the mstEmployees.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countMstEmployees(MstEmployeeCriteria criteria) {
        log.debug("REST request to count MstEmployees by criteria: {}", criteria);
        return mstEmployeeService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /mst-employees/:id} : get the "id" mstEmployee.
     *
     * @param id the id of the mstEmployeeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mstEmployeeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MstEmployeeDTO>> getMstEmployee(@PathVariable("id") Long id) {
        log.debug("REST request to get MstEmployee : {}", id);
        Mono<MstEmployeeDTO> mstEmployeeDTO = mstEmployeeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mstEmployeeDTO);
    }

    /**
     * {@code DELETE  /mst-employees/:id} : delete the "id" mstEmployee.
     *
     * @param id the id of the mstEmployeeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMstEmployee(@PathVariable("id") Long id) {
        log.debug("REST request to delete MstEmployee : {}", id);
        return mstEmployeeService
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
     * {@code SEARCH  /mst-employees/_search?query=:query} : search for the mstEmployee corresponding
     * to the query.
     *
     * @param query the query of the mstEmployee search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<MstEmployeeDTO>>> searchMstEmployees(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of MstEmployees for query {}", query);
        return mstEmployeeService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(mstEmployeeService.search(query, pageable)));
    }
}
