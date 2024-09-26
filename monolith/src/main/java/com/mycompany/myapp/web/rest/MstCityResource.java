package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.MstCityCriteria;
import com.mycompany.myapp.repository.MstCityRepository;
import com.mycompany.myapp.service.MstCityService;
import com.mycompany.myapp.service.dto.MstCityDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.MstCity}.
 */
@RestController
@RequestMapping("/api/mst-cities")
public class MstCityResource {

    private static final Logger log = LoggerFactory.getLogger(MstCityResource.class);

    private static final String ENTITY_NAME = "mstCity";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MstCityService mstCityService;

    private final MstCityRepository mstCityRepository;

    public MstCityResource(MstCityService mstCityService, MstCityRepository mstCityRepository) {
        this.mstCityService = mstCityService;
        this.mstCityRepository = mstCityRepository;
    }

    /**
     * {@code POST  /mst-cities} : Create a new mstCity.
     *
     * @param mstCityDTO the mstCityDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mstCityDTO, or with status {@code 400 (Bad Request)} if the mstCity has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MstCityDTO>> createMstCity(@Valid @RequestBody MstCityDTO mstCityDTO) throws URISyntaxException {
        log.debug("REST request to save MstCity : {}", mstCityDTO);
        if (mstCityDTO.getId() != null) {
            throw new BadRequestAlertException("A new mstCity cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return mstCityService
            .save(mstCityDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/mst-cities/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /mst-cities/:id} : Updates an existing mstCity.
     *
     * @param id the id of the mstCityDTO to save.
     * @param mstCityDTO the mstCityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstCityDTO,
     * or with status {@code 400 (Bad Request)} if the mstCityDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mstCityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MstCityDTO>> updateMstCity(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MstCityDTO mstCityDTO
    ) throws URISyntaxException {
        log.debug("REST request to update MstCity : {}, {}", id, mstCityDTO);
        if (mstCityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstCityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstCityRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return mstCityService
                    .update(mstCityDTO)
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
     * {@code PATCH  /mst-cities/:id} : Partial updates given fields of an existing mstCity, field will ignore if it is null
     *
     * @param id the id of the mstCityDTO to save.
     * @param mstCityDTO the mstCityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstCityDTO,
     * or with status {@code 400 (Bad Request)} if the mstCityDTO is not valid,
     * or with status {@code 404 (Not Found)} if the mstCityDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the mstCityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MstCityDTO>> partialUpdateMstCity(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MstCityDTO mstCityDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update MstCity partially : {}, {}", id, mstCityDTO);
        if (mstCityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstCityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstCityRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<MstCityDTO> result = mstCityService.partialUpdate(mstCityDTO);

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
     * {@code GET  /mst-cities} : get all the mstCities.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mstCities in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<MstCityDTO>>> getAllMstCities(
        MstCityCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get MstCities by criteria: {}", criteria);
        return mstCityService
            .countByCriteria(criteria)
            .zipWith(mstCityService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /mst-cities/count} : count all the mstCities.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countMstCities(MstCityCriteria criteria) {
        log.debug("REST request to count MstCities by criteria: {}", criteria);
        return mstCityService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /mst-cities/:id} : get the "id" mstCity.
     *
     * @param id the id of the mstCityDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mstCityDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MstCityDTO>> getMstCity(@PathVariable("id") Long id) {
        log.debug("REST request to get MstCity : {}", id);
        Mono<MstCityDTO> mstCityDTO = mstCityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mstCityDTO);
    }

    /**
     * {@code DELETE  /mst-cities/:id} : delete the "id" mstCity.
     *
     * @param id the id of the mstCityDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMstCity(@PathVariable("id") Long id) {
        log.debug("REST request to delete MstCity : {}", id);
        return mstCityService
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
     * {@code SEARCH  /mst-cities/_search?query=:query} : search for the mstCity corresponding
     * to the query.
     *
     * @param query the query of the mstCity search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<MstCityDTO>>> searchMstCities(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of MstCities for query {}", query);
        return mstCityService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(mstCityService.search(query, pageable)));
    }
}
