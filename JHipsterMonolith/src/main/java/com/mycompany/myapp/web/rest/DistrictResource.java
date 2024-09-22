package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.DistrictCriteria;
import com.mycompany.myapp.repository.DistrictRepository;
import com.mycompany.myapp.service.DistrictService;
import com.mycompany.myapp.service.dto.DistrictDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.District}.
 */
@RestController
@RequestMapping("/api/districts")
public class DistrictResource {

    private static final Logger log = LoggerFactory.getLogger(DistrictResource.class);

    private static final String ENTITY_NAME = "district";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DistrictService districtService;

    private final DistrictRepository districtRepository;

    public DistrictResource(DistrictService districtService, DistrictRepository districtRepository) {
        this.districtService = districtService;
        this.districtRepository = districtRepository;
    }

    /**
     * {@code POST  /districts} : Create a new district.
     *
     * @param districtDTO the districtDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new districtDTO, or with status {@code 400 (Bad Request)} if the district has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<DistrictDTO>> createDistrict(@Valid @RequestBody DistrictDTO districtDTO) throws URISyntaxException {
        log.debug("REST request to save District : {}", districtDTO);
        if (districtDTO.getId() != null) {
            throw new BadRequestAlertException("A new district cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return districtService
            .save(districtDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/districts/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /districts/:id} : Updates an existing district.
     *
     * @param id the id of the districtDTO to save.
     * @param districtDTO the districtDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated districtDTO,
     * or with status {@code 400 (Bad Request)} if the districtDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the districtDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<DistrictDTO>> updateDistrict(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DistrictDTO districtDTO
    ) throws URISyntaxException {
        log.debug("REST request to update District : {}, {}", id, districtDTO);
        if (districtDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, districtDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return districtRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return districtService
                    .update(districtDTO)
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
     * {@code PATCH  /districts/:id} : Partial updates given fields of an existing district, field will ignore if it is null
     *
     * @param id the id of the districtDTO to save.
     * @param districtDTO the districtDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated districtDTO,
     * or with status {@code 400 (Bad Request)} if the districtDTO is not valid,
     * or with status {@code 404 (Not Found)} if the districtDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the districtDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<DistrictDTO>> partialUpdateDistrict(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DistrictDTO districtDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update District partially : {}, {}", id, districtDTO);
        if (districtDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, districtDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return districtRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<DistrictDTO> result = districtService.partialUpdate(districtDTO);

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
     * {@code GET  /districts} : get all the districts.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of districts in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<DistrictDTO>>> getAllDistricts(
        DistrictCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get Districts by criteria: {}", criteria);
        return districtService
            .countByCriteria(criteria)
            .zipWith(districtService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /districts/count} : count all the districts.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countDistricts(DistrictCriteria criteria) {
        log.debug("REST request to count Districts by criteria: {}", criteria);
        return districtService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /districts/:id} : get the "id" district.
     *
     * @param id the id of the districtDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the districtDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<DistrictDTO>> getDistrict(@PathVariable("id") Long id) {
        log.debug("REST request to get District : {}", id);
        Mono<DistrictDTO> districtDTO = districtService.findOne(id);
        return ResponseUtil.wrapOrNotFound(districtDTO);
    }

    /**
     * {@code DELETE  /districts/:id} : delete the "id" district.
     *
     * @param id the id of the districtDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteDistrict(@PathVariable("id") Long id) {
        log.debug("REST request to delete District : {}", id);
        return districtService
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
     * {@code SEARCH  /districts/_search?query=:query} : search for the district corresponding
     * to the query.
     *
     * @param query the query of the district search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<DistrictDTO>>> searchDistricts(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of Districts for query {}", query);
        return districtService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(districtService.search(query, pageable)));
    }
}
