package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.MstPostalCodeCriteria;
import com.mycompany.myapp.repository.MstPostalCodeRepository;
import com.mycompany.myapp.service.MstPostalCodeService;
import com.mycompany.myapp.service.dto.MstPostalCodeDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.MstPostalCode}.
 */
@RestController
@RequestMapping("/api/mst-postal-codes")
public class MstPostalCodeResource {

    private static final Logger log = LoggerFactory.getLogger(MstPostalCodeResource.class);

    private static final String ENTITY_NAME = "mstPostalCode";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MstPostalCodeService mstPostalCodeService;

    private final MstPostalCodeRepository mstPostalCodeRepository;

    public MstPostalCodeResource(MstPostalCodeService mstPostalCodeService, MstPostalCodeRepository mstPostalCodeRepository) {
        this.mstPostalCodeService = mstPostalCodeService;
        this.mstPostalCodeRepository = mstPostalCodeRepository;
    }

    /**
     * {@code POST  /mst-postal-codes} : Create a new mstPostalCode.
     *
     * @param mstPostalCodeDTO the mstPostalCodeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mstPostalCodeDTO, or with status {@code 400 (Bad Request)} if the mstPostalCode has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MstPostalCodeDTO>> createMstPostalCode(@Valid @RequestBody MstPostalCodeDTO mstPostalCodeDTO)
        throws URISyntaxException {
        log.debug("REST request to save MstPostalCode : {}", mstPostalCodeDTO);
        if (mstPostalCodeDTO.getId() != null) {
            throw new BadRequestAlertException("A new mstPostalCode cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return mstPostalCodeService
            .save(mstPostalCodeDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/mst-postal-codes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /mst-postal-codes/:id} : Updates an existing mstPostalCode.
     *
     * @param id the id of the mstPostalCodeDTO to save.
     * @param mstPostalCodeDTO the mstPostalCodeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstPostalCodeDTO,
     * or with status {@code 400 (Bad Request)} if the mstPostalCodeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mstPostalCodeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MstPostalCodeDTO>> updateMstPostalCode(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MstPostalCodeDTO mstPostalCodeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update MstPostalCode : {}, {}", id, mstPostalCodeDTO);
        if (mstPostalCodeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstPostalCodeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstPostalCodeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return mstPostalCodeService
                    .update(mstPostalCodeDTO)
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
     * {@code PATCH  /mst-postal-codes/:id} : Partial updates given fields of an existing mstPostalCode, field will ignore if it is null
     *
     * @param id the id of the mstPostalCodeDTO to save.
     * @param mstPostalCodeDTO the mstPostalCodeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstPostalCodeDTO,
     * or with status {@code 400 (Bad Request)} if the mstPostalCodeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the mstPostalCodeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the mstPostalCodeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MstPostalCodeDTO>> partialUpdateMstPostalCode(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MstPostalCodeDTO mstPostalCodeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update MstPostalCode partially : {}, {}", id, mstPostalCodeDTO);
        if (mstPostalCodeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstPostalCodeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstPostalCodeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<MstPostalCodeDTO> result = mstPostalCodeService.partialUpdate(mstPostalCodeDTO);

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
     * {@code GET  /mst-postal-codes} : get all the mstPostalCodes.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mstPostalCodes in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<MstPostalCodeDTO>>> getAllMstPostalCodes(
        MstPostalCodeCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get MstPostalCodes by criteria: {}", criteria);
        return mstPostalCodeService
            .countByCriteria(criteria)
            .zipWith(mstPostalCodeService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /mst-postal-codes/count} : count all the mstPostalCodes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countMstPostalCodes(MstPostalCodeCriteria criteria) {
        log.debug("REST request to count MstPostalCodes by criteria: {}", criteria);
        return mstPostalCodeService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /mst-postal-codes/:id} : get the "id" mstPostalCode.
     *
     * @param id the id of the mstPostalCodeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mstPostalCodeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MstPostalCodeDTO>> getMstPostalCode(@PathVariable("id") Long id) {
        log.debug("REST request to get MstPostalCode : {}", id);
        Mono<MstPostalCodeDTO> mstPostalCodeDTO = mstPostalCodeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mstPostalCodeDTO);
    }

    /**
     * {@code DELETE  /mst-postal-codes/:id} : delete the "id" mstPostalCode.
     *
     * @param id the id of the mstPostalCodeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMstPostalCode(@PathVariable("id") Long id) {
        log.debug("REST request to delete MstPostalCode : {}", id);
        return mstPostalCodeService
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
     * {@code SEARCH  /mst-postal-codes/_search?query=:query} : search for the mstPostalCode corresponding
     * to the query.
     *
     * @param query the query of the mstPostalCode search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<MstPostalCodeDTO>>> searchMstPostalCodes(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of MstPostalCodes for query {}", query);
        return mstPostalCodeService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(mstPostalCodeService.search(query, pageable)));
    }
}
