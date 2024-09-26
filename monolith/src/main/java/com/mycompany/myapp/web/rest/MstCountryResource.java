package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.MstCountryCriteria;
import com.mycompany.myapp.repository.MstCountryRepository;
import com.mycompany.myapp.service.MstCountryService;
import com.mycompany.myapp.service.dto.MstCountryDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.MstCountry}.
 */
@RestController
@RequestMapping("/api/mst-countries")
public class MstCountryResource {

    private static final Logger log = LoggerFactory.getLogger(MstCountryResource.class);

    private static final String ENTITY_NAME = "mstCountry";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MstCountryService mstCountryService;

    private final MstCountryRepository mstCountryRepository;

    public MstCountryResource(MstCountryService mstCountryService, MstCountryRepository mstCountryRepository) {
        this.mstCountryService = mstCountryService;
        this.mstCountryRepository = mstCountryRepository;
    }

    /**
     * {@code POST  /mst-countries} : Create a new mstCountry.
     *
     * @param mstCountryDTO the mstCountryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mstCountryDTO, or with status {@code 400 (Bad Request)} if the mstCountry has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MstCountryDTO>> createMstCountry(@Valid @RequestBody MstCountryDTO mstCountryDTO) throws URISyntaxException {
        log.debug("REST request to save MstCountry : {}", mstCountryDTO);
        if (mstCountryDTO.getId() != null) {
            throw new BadRequestAlertException("A new mstCountry cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return mstCountryService
            .save(mstCountryDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/mst-countries/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /mst-countries/:id} : Updates an existing mstCountry.
     *
     * @param id the id of the mstCountryDTO to save.
     * @param mstCountryDTO the mstCountryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstCountryDTO,
     * or with status {@code 400 (Bad Request)} if the mstCountryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mstCountryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MstCountryDTO>> updateMstCountry(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MstCountryDTO mstCountryDTO
    ) throws URISyntaxException {
        log.debug("REST request to update MstCountry : {}, {}", id, mstCountryDTO);
        if (mstCountryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstCountryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstCountryRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return mstCountryService
                    .update(mstCountryDTO)
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
     * {@code PATCH  /mst-countries/:id} : Partial updates given fields of an existing mstCountry, field will ignore if it is null
     *
     * @param id the id of the mstCountryDTO to save.
     * @param mstCountryDTO the mstCountryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstCountryDTO,
     * or with status {@code 400 (Bad Request)} if the mstCountryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the mstCountryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the mstCountryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MstCountryDTO>> partialUpdateMstCountry(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MstCountryDTO mstCountryDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update MstCountry partially : {}, {}", id, mstCountryDTO);
        if (mstCountryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstCountryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstCountryRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<MstCountryDTO> result = mstCountryService.partialUpdate(mstCountryDTO);

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
     * {@code GET  /mst-countries} : get all the mstCountries.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mstCountries in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<MstCountryDTO>>> getAllMstCountries(
        MstCountryCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get MstCountries by criteria: {}", criteria);
        return mstCountryService
            .countByCriteria(criteria)
            .zipWith(mstCountryService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /mst-countries/count} : count all the mstCountries.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countMstCountries(MstCountryCriteria criteria) {
        log.debug("REST request to count MstCountries by criteria: {}", criteria);
        return mstCountryService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /mst-countries/:id} : get the "id" mstCountry.
     *
     * @param id the id of the mstCountryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mstCountryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MstCountryDTO>> getMstCountry(@PathVariable("id") Long id) {
        log.debug("REST request to get MstCountry : {}", id);
        Mono<MstCountryDTO> mstCountryDTO = mstCountryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mstCountryDTO);
    }

    /**
     * {@code DELETE  /mst-countries/:id} : delete the "id" mstCountry.
     *
     * @param id the id of the mstCountryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMstCountry(@PathVariable("id") Long id) {
        log.debug("REST request to delete MstCountry : {}", id);
        return mstCountryService
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
     * {@code SEARCH  /mst-countries/_search?query=:query} : search for the mstCountry corresponding
     * to the query.
     *
     * @param query the query of the mstCountry search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<MstCountryDTO>>> searchMstCountries(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of MstCountries for query {}", query);
        return mstCountryService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(mstCountryService.search(query, pageable)));
    }
}
