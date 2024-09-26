package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.MstRegionCriteria;
import com.mycompany.myapp.repository.MstRegionRepository;
import com.mycompany.myapp.service.MstRegionService;
import com.mycompany.myapp.service.dto.MstRegionDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.MstRegion}.
 */
@RestController
@RequestMapping("/api/mst-regions")
public class MstRegionResource {

    private static final Logger log = LoggerFactory.getLogger(MstRegionResource.class);

    private static final String ENTITY_NAME = "mstRegion";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MstRegionService mstRegionService;

    private final MstRegionRepository mstRegionRepository;

    public MstRegionResource(MstRegionService mstRegionService, MstRegionRepository mstRegionRepository) {
        this.mstRegionService = mstRegionService;
        this.mstRegionRepository = mstRegionRepository;
    }

    /**
     * {@code POST  /mst-regions} : Create a new mstRegion.
     *
     * @param mstRegionDTO the mstRegionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mstRegionDTO, or with status {@code 400 (Bad Request)} if the mstRegion has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MstRegionDTO>> createMstRegion(@Valid @RequestBody MstRegionDTO mstRegionDTO) throws URISyntaxException {
        log.debug("REST request to save MstRegion : {}", mstRegionDTO);
        if (mstRegionDTO.getId() != null) {
            throw new BadRequestAlertException("A new mstRegion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return mstRegionService
            .save(mstRegionDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/mst-regions/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /mst-regions/:id} : Updates an existing mstRegion.
     *
     * @param id the id of the mstRegionDTO to save.
     * @param mstRegionDTO the mstRegionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstRegionDTO,
     * or with status {@code 400 (Bad Request)} if the mstRegionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mstRegionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MstRegionDTO>> updateMstRegion(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MstRegionDTO mstRegionDTO
    ) throws URISyntaxException {
        log.debug("REST request to update MstRegion : {}, {}", id, mstRegionDTO);
        if (mstRegionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstRegionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstRegionRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return mstRegionService
                    .update(mstRegionDTO)
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
     * {@code PATCH  /mst-regions/:id} : Partial updates given fields of an existing mstRegion, field will ignore if it is null
     *
     * @param id the id of the mstRegionDTO to save.
     * @param mstRegionDTO the mstRegionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstRegionDTO,
     * or with status {@code 400 (Bad Request)} if the mstRegionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the mstRegionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the mstRegionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MstRegionDTO>> partialUpdateMstRegion(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MstRegionDTO mstRegionDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update MstRegion partially : {}, {}", id, mstRegionDTO);
        if (mstRegionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstRegionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstRegionRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<MstRegionDTO> result = mstRegionService.partialUpdate(mstRegionDTO);

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
     * {@code GET  /mst-regions} : get all the mstRegions.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mstRegions in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<MstRegionDTO>>> getAllMstRegions(
        MstRegionCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get MstRegions by criteria: {}", criteria);
        return mstRegionService
            .countByCriteria(criteria)
            .zipWith(mstRegionService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /mst-regions/count} : count all the mstRegions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countMstRegions(MstRegionCriteria criteria) {
        log.debug("REST request to count MstRegions by criteria: {}", criteria);
        return mstRegionService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /mst-regions/:id} : get the "id" mstRegion.
     *
     * @param id the id of the mstRegionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mstRegionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MstRegionDTO>> getMstRegion(@PathVariable("id") Long id) {
        log.debug("REST request to get MstRegion : {}", id);
        Mono<MstRegionDTO> mstRegionDTO = mstRegionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mstRegionDTO);
    }

    /**
     * {@code DELETE  /mst-regions/:id} : delete the "id" mstRegion.
     *
     * @param id the id of the mstRegionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMstRegion(@PathVariable("id") Long id) {
        log.debug("REST request to delete MstRegion : {}", id);
        return mstRegionService
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
     * {@code SEARCH  /mst-regions/_search?query=:query} : search for the mstRegion corresponding
     * to the query.
     *
     * @param query the query of the mstRegion search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<MstRegionDTO>>> searchMstRegions(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of MstRegions for query {}", query);
        return mstRegionService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(mstRegionService.search(query, pageable)));
    }
}
