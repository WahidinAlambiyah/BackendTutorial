package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.MstSubDistrictCriteria;
import com.mycompany.myapp.repository.MstSubDistrictRepository;
import com.mycompany.myapp.service.MstSubDistrictService;
import com.mycompany.myapp.service.dto.MstSubDistrictDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.MstSubDistrict}.
 */
@RestController
@RequestMapping("/api/mst-sub-districts")
public class MstSubDistrictResource {

    private static final Logger log = LoggerFactory.getLogger(MstSubDistrictResource.class);

    private static final String ENTITY_NAME = "mstSubDistrict";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MstSubDistrictService mstSubDistrictService;

    private final MstSubDistrictRepository mstSubDistrictRepository;

    public MstSubDistrictResource(MstSubDistrictService mstSubDistrictService, MstSubDistrictRepository mstSubDistrictRepository) {
        this.mstSubDistrictService = mstSubDistrictService;
        this.mstSubDistrictRepository = mstSubDistrictRepository;
    }

    /**
     * {@code POST  /mst-sub-districts} : Create a new mstSubDistrict.
     *
     * @param mstSubDistrictDTO the mstSubDistrictDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mstSubDistrictDTO, or with status {@code 400 (Bad Request)} if the mstSubDistrict has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MstSubDistrictDTO>> createMstSubDistrict(@Valid @RequestBody MstSubDistrictDTO mstSubDistrictDTO)
        throws URISyntaxException {
        log.debug("REST request to save MstSubDistrict : {}", mstSubDistrictDTO);
        if (mstSubDistrictDTO.getId() != null) {
            throw new BadRequestAlertException("A new mstSubDistrict cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return mstSubDistrictService
            .save(mstSubDistrictDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/mst-sub-districts/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /mst-sub-districts/:id} : Updates an existing mstSubDistrict.
     *
     * @param id the id of the mstSubDistrictDTO to save.
     * @param mstSubDistrictDTO the mstSubDistrictDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstSubDistrictDTO,
     * or with status {@code 400 (Bad Request)} if the mstSubDistrictDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mstSubDistrictDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MstSubDistrictDTO>> updateMstSubDistrict(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MstSubDistrictDTO mstSubDistrictDTO
    ) throws URISyntaxException {
        log.debug("REST request to update MstSubDistrict : {}, {}", id, mstSubDistrictDTO);
        if (mstSubDistrictDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstSubDistrictDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstSubDistrictRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return mstSubDistrictService
                    .update(mstSubDistrictDTO)
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
     * {@code PATCH  /mst-sub-districts/:id} : Partial updates given fields of an existing mstSubDistrict, field will ignore if it is null
     *
     * @param id the id of the mstSubDistrictDTO to save.
     * @param mstSubDistrictDTO the mstSubDistrictDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstSubDistrictDTO,
     * or with status {@code 400 (Bad Request)} if the mstSubDistrictDTO is not valid,
     * or with status {@code 404 (Not Found)} if the mstSubDistrictDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the mstSubDistrictDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MstSubDistrictDTO>> partialUpdateMstSubDistrict(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MstSubDistrictDTO mstSubDistrictDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update MstSubDistrict partially : {}, {}", id, mstSubDistrictDTO);
        if (mstSubDistrictDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstSubDistrictDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstSubDistrictRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<MstSubDistrictDTO> result = mstSubDistrictService.partialUpdate(mstSubDistrictDTO);

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
     * {@code GET  /mst-sub-districts} : get all the mstSubDistricts.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mstSubDistricts in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<MstSubDistrictDTO>>> getAllMstSubDistricts(
        MstSubDistrictCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get MstSubDistricts by criteria: {}", criteria);
        return mstSubDistrictService
            .countByCriteria(criteria)
            .zipWith(mstSubDistrictService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /mst-sub-districts/count} : count all the mstSubDistricts.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countMstSubDistricts(MstSubDistrictCriteria criteria) {
        log.debug("REST request to count MstSubDistricts by criteria: {}", criteria);
        return mstSubDistrictService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /mst-sub-districts/:id} : get the "id" mstSubDistrict.
     *
     * @param id the id of the mstSubDistrictDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mstSubDistrictDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MstSubDistrictDTO>> getMstSubDistrict(@PathVariable("id") Long id) {
        log.debug("REST request to get MstSubDistrict : {}", id);
        Mono<MstSubDistrictDTO> mstSubDistrictDTO = mstSubDistrictService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mstSubDistrictDTO);
    }

    /**
     * {@code DELETE  /mst-sub-districts/:id} : delete the "id" mstSubDistrict.
     *
     * @param id the id of the mstSubDistrictDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMstSubDistrict(@PathVariable("id") Long id) {
        log.debug("REST request to delete MstSubDistrict : {}", id);
        return mstSubDistrictService
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
     * {@code SEARCH  /mst-sub-districts/_search?query=:query} : search for the mstSubDistrict corresponding
     * to the query.
     *
     * @param query the query of the mstSubDistrict search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<MstSubDistrictDTO>>> searchMstSubDistricts(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of MstSubDistricts for query {}", query);
        return mstSubDistrictService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(mstSubDistrictService.search(query, pageable)));
    }
}
