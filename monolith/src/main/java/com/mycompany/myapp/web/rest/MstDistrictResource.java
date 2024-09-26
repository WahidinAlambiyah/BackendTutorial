package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.MstDistrictCriteria;
import com.mycompany.myapp.repository.MstDistrictRepository;
import com.mycompany.myapp.service.MstDistrictService;
import com.mycompany.myapp.service.dto.MstDistrictDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.MstDistrict}.
 */
@RestController
@RequestMapping("/api/mst-districts")
public class MstDistrictResource {

    private static final Logger log = LoggerFactory.getLogger(MstDistrictResource.class);

    private static final String ENTITY_NAME = "mstDistrict";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MstDistrictService mstDistrictService;

    private final MstDistrictRepository mstDistrictRepository;

    public MstDistrictResource(MstDistrictService mstDistrictService, MstDistrictRepository mstDistrictRepository) {
        this.mstDistrictService = mstDistrictService;
        this.mstDistrictRepository = mstDistrictRepository;
    }

    /**
     * {@code POST  /mst-districts} : Create a new mstDistrict.
     *
     * @param mstDistrictDTO the mstDistrictDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mstDistrictDTO, or with status {@code 400 (Bad Request)} if the mstDistrict has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MstDistrictDTO>> createMstDistrict(@Valid @RequestBody MstDistrictDTO mstDistrictDTO)
        throws URISyntaxException {
        log.debug("REST request to save MstDistrict : {}", mstDistrictDTO);
        if (mstDistrictDTO.getId() != null) {
            throw new BadRequestAlertException("A new mstDistrict cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return mstDistrictService
            .save(mstDistrictDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/mst-districts/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /mst-districts/:id} : Updates an existing mstDistrict.
     *
     * @param id the id of the mstDistrictDTO to save.
     * @param mstDistrictDTO the mstDistrictDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstDistrictDTO,
     * or with status {@code 400 (Bad Request)} if the mstDistrictDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mstDistrictDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MstDistrictDTO>> updateMstDistrict(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MstDistrictDTO mstDistrictDTO
    ) throws URISyntaxException {
        log.debug("REST request to update MstDistrict : {}, {}", id, mstDistrictDTO);
        if (mstDistrictDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstDistrictDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstDistrictRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return mstDistrictService
                    .update(mstDistrictDTO)
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
     * {@code PATCH  /mst-districts/:id} : Partial updates given fields of an existing mstDistrict, field will ignore if it is null
     *
     * @param id the id of the mstDistrictDTO to save.
     * @param mstDistrictDTO the mstDistrictDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstDistrictDTO,
     * or with status {@code 400 (Bad Request)} if the mstDistrictDTO is not valid,
     * or with status {@code 404 (Not Found)} if the mstDistrictDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the mstDistrictDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MstDistrictDTO>> partialUpdateMstDistrict(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MstDistrictDTO mstDistrictDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update MstDistrict partially : {}, {}", id, mstDistrictDTO);
        if (mstDistrictDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstDistrictDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstDistrictRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<MstDistrictDTO> result = mstDistrictService.partialUpdate(mstDistrictDTO);

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
     * {@code GET  /mst-districts} : get all the mstDistricts.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mstDistricts in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<MstDistrictDTO>>> getAllMstDistricts(
        MstDistrictCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get MstDistricts by criteria: {}", criteria);
        return mstDistrictService
            .countByCriteria(criteria)
            .zipWith(mstDistrictService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /mst-districts/count} : count all the mstDistricts.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countMstDistricts(MstDistrictCriteria criteria) {
        log.debug("REST request to count MstDistricts by criteria: {}", criteria);
        return mstDistrictService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /mst-districts/:id} : get the "id" mstDistrict.
     *
     * @param id the id of the mstDistrictDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mstDistrictDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MstDistrictDTO>> getMstDistrict(@PathVariable("id") Long id) {
        log.debug("REST request to get MstDistrict : {}", id);
        Mono<MstDistrictDTO> mstDistrictDTO = mstDistrictService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mstDistrictDTO);
    }

    /**
     * {@code DELETE  /mst-districts/:id} : delete the "id" mstDistrict.
     *
     * @param id the id of the mstDistrictDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMstDistrict(@PathVariable("id") Long id) {
        log.debug("REST request to delete MstDistrict : {}", id);
        return mstDistrictService
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
     * {@code SEARCH  /mst-districts/_search?query=:query} : search for the mstDistrict corresponding
     * to the query.
     *
     * @param query the query of the mstDistrict search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<MstDistrictDTO>>> searchMstDistricts(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of MstDistricts for query {}", query);
        return mstDistrictService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(mstDistrictService.search(query, pageable)));
    }
}
