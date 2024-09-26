package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.MstDepartmentCriteria;
import com.mycompany.myapp.repository.MstDepartmentRepository;
import com.mycompany.myapp.service.MstDepartmentService;
import com.mycompany.myapp.service.dto.MstDepartmentDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import com.mycompany.myapp.web.rest.errors.ElasticsearchExceptionMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.MstDepartment}.
 */
@RestController
@RequestMapping("/api/mst-departments")
public class MstDepartmentResource {

    private static final Logger log = LoggerFactory.getLogger(MstDepartmentResource.class);

    private static final String ENTITY_NAME = "mstDepartment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MstDepartmentService mstDepartmentService;

    private final MstDepartmentRepository mstDepartmentRepository;

    public MstDepartmentResource(MstDepartmentService mstDepartmentService, MstDepartmentRepository mstDepartmentRepository) {
        this.mstDepartmentService = mstDepartmentService;
        this.mstDepartmentRepository = mstDepartmentRepository;
    }

    /**
     * {@code POST  /mst-departments} : Create a new mstDepartment.
     *
     * @param mstDepartmentDTO the mstDepartmentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mstDepartmentDTO, or with status {@code 400 (Bad Request)} if the mstDepartment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MstDepartmentDTO>> createMstDepartment(@Valid @RequestBody MstDepartmentDTO mstDepartmentDTO)
        throws URISyntaxException {
        log.debug("REST request to save MstDepartment : {}", mstDepartmentDTO);
        if (mstDepartmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new mstDepartment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return mstDepartmentService
            .save(mstDepartmentDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/mst-departments/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /mst-departments/:id} : Updates an existing mstDepartment.
     *
     * @param id the id of the mstDepartmentDTO to save.
     * @param mstDepartmentDTO the mstDepartmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstDepartmentDTO,
     * or with status {@code 400 (Bad Request)} if the mstDepartmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mstDepartmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MstDepartmentDTO>> updateMstDepartment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MstDepartmentDTO mstDepartmentDTO
    ) throws URISyntaxException {
        log.debug("REST request to update MstDepartment : {}, {}", id, mstDepartmentDTO);
        if (mstDepartmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstDepartmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstDepartmentRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return mstDepartmentService
                    .update(mstDepartmentDTO)
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
     * {@code PATCH  /mst-departments/:id} : Partial updates given fields of an existing mstDepartment, field will ignore if it is null
     *
     * @param id the id of the mstDepartmentDTO to save.
     * @param mstDepartmentDTO the mstDepartmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstDepartmentDTO,
     * or with status {@code 400 (Bad Request)} if the mstDepartmentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the mstDepartmentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the mstDepartmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MstDepartmentDTO>> partialUpdateMstDepartment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MstDepartmentDTO mstDepartmentDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update MstDepartment partially : {}, {}", id, mstDepartmentDTO);
        if (mstDepartmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstDepartmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstDepartmentRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<MstDepartmentDTO> result = mstDepartmentService.partialUpdate(mstDepartmentDTO);

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
     * {@code GET  /mst-departments} : get all the mstDepartments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mstDepartments in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<MstDepartmentDTO> getAllMstDepartments(MstDepartmentCriteria criteria) {
        log.debug("REST request to get MstDepartments by criteria: {}", criteria);
        return mstDepartmentService.findByCriteria(criteria);
    }

    /**
     * {@code GET  /mst-departments/count} : count all the mstDepartments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countMstDepartments(MstDepartmentCriteria criteria) {
        log.debug("REST request to count MstDepartments by criteria: {}", criteria);
        return mstDepartmentService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /mst-departments/:id} : get the "id" mstDepartment.
     *
     * @param id the id of the mstDepartmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mstDepartmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MstDepartmentDTO>> getMstDepartment(@PathVariable("id") Long id) {
        log.debug("REST request to get MstDepartment : {}", id);
        Mono<MstDepartmentDTO> mstDepartmentDTO = mstDepartmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mstDepartmentDTO);
    }

    /**
     * {@code DELETE  /mst-departments/:id} : delete the "id" mstDepartment.
     *
     * @param id the id of the mstDepartmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMstDepartment(@PathVariable("id") Long id) {
        log.debug("REST request to delete MstDepartment : {}", id);
        return mstDepartmentService
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
     * {@code SEARCH  /mst-departments/_search?query=:query} : search for the mstDepartment corresponding
     * to the query.
     *
     * @param query the query of the mstDepartment search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<List<MstDepartmentDTO>> searchMstDepartments(@RequestParam("query") String query) {
        log.debug("REST request to search MstDepartments for query {}", query);
        try {
            return mstDepartmentService.search(query).collectList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
