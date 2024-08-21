package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.RegionCriteria;
import com.mycompany.myapp.repository.RegionRepository;
import com.mycompany.myapp.service.RegionService;
import com.mycompany.myapp.service.dto.RegionDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Region}.
 */
@RestController
@RequestMapping("/api/regions")
public class RegionResource {

    private static final Logger log = LoggerFactory.getLogger(RegionResource.class);

    private static final String ENTITY_NAME = "region";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RegionService regionService;

    private final RegionRepository regionRepository;

    public RegionResource(RegionService regionService, RegionRepository regionRepository) {
        this.regionService = regionService;
        this.regionRepository = regionRepository;
    }

    /**
     * {@code POST  /regions} : Create a new region.
     *
     * @param regionDTO the regionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new regionDTO, or with status {@code 400 (Bad Request)} if the region has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<RegionDTO>> createRegion(@Valid @RequestBody RegionDTO regionDTO) throws URISyntaxException {
        log.debug("REST request to save Region : {}", regionDTO);
        if (regionDTO.getId() != null) {
            throw new BadRequestAlertException("A new region cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return regionService
            .save(regionDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/regions/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /regions/:id} : Updates an existing region.
     *
     * @param id the id of the regionDTO to save.
     * @param regionDTO the regionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated regionDTO,
     * or with status {@code 400 (Bad Request)} if the regionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the regionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<RegionDTO>> updateRegion(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RegionDTO regionDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Region : {}, {}", id, regionDTO);
        if (regionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, regionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return regionRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return regionService
                    .update(regionDTO)
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
     * {@code PATCH  /regions/:id} : Partial updates given fields of an existing region, field will ignore if it is null
     *
     * @param id the id of the regionDTO to save.
     * @param regionDTO the regionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated regionDTO,
     * or with status {@code 400 (Bad Request)} if the regionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the regionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the regionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<RegionDTO>> partialUpdateRegion(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RegionDTO regionDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Region partially : {}, {}", id, regionDTO);
        if (regionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, regionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return regionRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<RegionDTO> result = regionService.partialUpdate(regionDTO);

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
     * {@code GET  /regions} : get all the regions.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of regions in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<RegionDTO>>> getAllRegions(
        RegionCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get Regions by criteria: {}", criteria);
        return regionService
            .countByCriteria(criteria)
            .zipWith(regionService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /regions/count} : count all the regions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countRegions(RegionCriteria criteria) {
        log.debug("REST request to count Regions by criteria: {}", criteria);
        return regionService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /regions/:id} : get the "id" region.
     *
     * @param id the id of the regionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the regionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<RegionDTO>> getRegion(@PathVariable("id") Long id) {
        log.debug("REST request to get Region : {}", id);
        Mono<RegionDTO> regionDTO = regionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(regionDTO);
    }

    /**
     * {@code DELETE  /regions/:id} : delete the "id" region.
     *
     * @param id the id of the regionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteRegion(@PathVariable("id") Long id) {
        log.debug("REST request to delete Region : {}", id);
        return regionService
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
     * {@code SEARCH  /regions/_search?query=:query} : search for the region corresponding
     * to the query.
     *
     * @param query the query of the region search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<RegionDTO>>> searchRegions(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of Regions for query {}", query);
        return regionService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(regionService.search(query, pageable)));
    }
}
