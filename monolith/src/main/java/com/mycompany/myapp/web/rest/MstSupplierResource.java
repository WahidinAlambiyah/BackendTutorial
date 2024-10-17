package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.MstSupplierCriteria;
import com.mycompany.myapp.repository.MstSupplierRepository;
import com.mycompany.myapp.service.MstSupplierService;
import com.mycompany.myapp.service.dto.MstSupplierDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.MstSupplier}.
 */
@RestController
@RequestMapping("/api/mst-suppliers")
public class MstSupplierResource {

    private static final Logger log = LoggerFactory.getLogger(MstSupplierResource.class);

    private static final String ENTITY_NAME = "mstSupplier";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MstSupplierService mstSupplierService;

    private final MstSupplierRepository mstSupplierRepository;

    public MstSupplierResource(MstSupplierService mstSupplierService, MstSupplierRepository mstSupplierRepository) {
        this.mstSupplierService = mstSupplierService;
        this.mstSupplierRepository = mstSupplierRepository;
    }

    /**
     * {@code POST  /mst-suppliers} : Create a new mstSupplier.
     *
     * @param mstSupplierDTO the mstSupplierDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mstSupplierDTO, or with status {@code 400 (Bad Request)} if the mstSupplier has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MstSupplierDTO>> createMstSupplier(@Valid @RequestBody MstSupplierDTO mstSupplierDTO)
        throws URISyntaxException {
        log.debug("REST request to save MstSupplier : {}", mstSupplierDTO);
        if (mstSupplierDTO.getId() != null) {
            throw new BadRequestAlertException("A new mstSupplier cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return mstSupplierService
            .save(mstSupplierDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/mst-suppliers/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /mst-suppliers/:id} : Updates an existing mstSupplier.
     *
     * @param id the id of the mstSupplierDTO to save.
     * @param mstSupplierDTO the mstSupplierDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstSupplierDTO,
     * or with status {@code 400 (Bad Request)} if the mstSupplierDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mstSupplierDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MstSupplierDTO>> updateMstSupplier(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MstSupplierDTO mstSupplierDTO
    ) throws URISyntaxException {
        log.debug("REST request to update MstSupplier : {}, {}", id, mstSupplierDTO);
        if (mstSupplierDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstSupplierDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstSupplierRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return mstSupplierService
                    .update(mstSupplierDTO)
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
     * {@code PATCH  /mst-suppliers/:id} : Partial updates given fields of an existing mstSupplier, field will ignore if it is null
     *
     * @param id the id of the mstSupplierDTO to save.
     * @param mstSupplierDTO the mstSupplierDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstSupplierDTO,
     * or with status {@code 400 (Bad Request)} if the mstSupplierDTO is not valid,
     * or with status {@code 404 (Not Found)} if the mstSupplierDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the mstSupplierDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MstSupplierDTO>> partialUpdateMstSupplier(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MstSupplierDTO mstSupplierDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update MstSupplier partially : {}, {}", id, mstSupplierDTO);
        if (mstSupplierDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstSupplierDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstSupplierRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<MstSupplierDTO> result = mstSupplierService.partialUpdate(mstSupplierDTO);

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
     * {@code GET  /mst-suppliers} : get all the mstSuppliers.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mstSuppliers in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<MstSupplierDTO>>> getAllMstSuppliers(
        MstSupplierCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get MstSuppliers by criteria: {}", criteria);
        return mstSupplierService
            .countByCriteria(criteria)
            .zipWith(mstSupplierService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /mst-suppliers/count} : count all the mstSuppliers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countMstSuppliers(MstSupplierCriteria criteria) {
        log.debug("REST request to count MstSuppliers by criteria: {}", criteria);
        return mstSupplierService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /mst-suppliers/:id} : get the "id" mstSupplier.
     *
     * @param id the id of the mstSupplierDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mstSupplierDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MstSupplierDTO>> getMstSupplier(@PathVariable("id") Long id) {
        log.debug("REST request to get MstSupplier : {}", id);
        Mono<MstSupplierDTO> mstSupplierDTO = mstSupplierService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mstSupplierDTO);
    }

    /**
     * {@code DELETE  /mst-suppliers/:id} : delete the "id" mstSupplier.
     *
     * @param id the id of the mstSupplierDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMstSupplier(@PathVariable("id") Long id) {
        log.debug("REST request to delete MstSupplier : {}", id);
        return mstSupplierService
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
     * {@code SEARCH  /mst-suppliers/_search?query=:query} : search for the mstSupplier corresponding
     * to the query.
     *
     * @param query the query of the mstSupplier search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<MstSupplierDTO>>> searchMstSuppliers(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of MstSuppliers for query {}", query);
        return mstSupplierService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(mstSupplierService.search(query, pageable)));
    }
}
