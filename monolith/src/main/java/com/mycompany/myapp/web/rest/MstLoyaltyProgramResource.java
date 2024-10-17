package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.MstLoyaltyProgramCriteria;
import com.mycompany.myapp.repository.MstLoyaltyProgramRepository;
import com.mycompany.myapp.service.MstLoyaltyProgramService;
import com.mycompany.myapp.service.dto.MstLoyaltyProgramDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.MstLoyaltyProgram}.
 */
@RestController
@RequestMapping("/api/mst-loyalty-programs")
public class MstLoyaltyProgramResource {

    private static final Logger log = LoggerFactory.getLogger(MstLoyaltyProgramResource.class);

    private static final String ENTITY_NAME = "mstLoyaltyProgram";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MstLoyaltyProgramService mstLoyaltyProgramService;

    private final MstLoyaltyProgramRepository mstLoyaltyProgramRepository;

    public MstLoyaltyProgramResource(
        MstLoyaltyProgramService mstLoyaltyProgramService,
        MstLoyaltyProgramRepository mstLoyaltyProgramRepository
    ) {
        this.mstLoyaltyProgramService = mstLoyaltyProgramService;
        this.mstLoyaltyProgramRepository = mstLoyaltyProgramRepository;
    }

    /**
     * {@code POST  /mst-loyalty-programs} : Create a new mstLoyaltyProgram.
     *
     * @param mstLoyaltyProgramDTO the mstLoyaltyProgramDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mstLoyaltyProgramDTO, or with status {@code 400 (Bad Request)} if the mstLoyaltyProgram has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MstLoyaltyProgramDTO>> createMstLoyaltyProgram(@RequestBody MstLoyaltyProgramDTO mstLoyaltyProgramDTO)
        throws URISyntaxException {
        log.debug("REST request to save MstLoyaltyProgram : {}", mstLoyaltyProgramDTO);
        if (mstLoyaltyProgramDTO.getId() != null) {
            throw new BadRequestAlertException("A new mstLoyaltyProgram cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return mstLoyaltyProgramService
            .save(mstLoyaltyProgramDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/mst-loyalty-programs/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /mst-loyalty-programs/:id} : Updates an existing mstLoyaltyProgram.
     *
     * @param id the id of the mstLoyaltyProgramDTO to save.
     * @param mstLoyaltyProgramDTO the mstLoyaltyProgramDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstLoyaltyProgramDTO,
     * or with status {@code 400 (Bad Request)} if the mstLoyaltyProgramDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mstLoyaltyProgramDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MstLoyaltyProgramDTO>> updateMstLoyaltyProgram(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MstLoyaltyProgramDTO mstLoyaltyProgramDTO
    ) throws URISyntaxException {
        log.debug("REST request to update MstLoyaltyProgram : {}, {}", id, mstLoyaltyProgramDTO);
        if (mstLoyaltyProgramDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstLoyaltyProgramDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstLoyaltyProgramRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return mstLoyaltyProgramService
                    .update(mstLoyaltyProgramDTO)
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
     * {@code PATCH  /mst-loyalty-programs/:id} : Partial updates given fields of an existing mstLoyaltyProgram, field will ignore if it is null
     *
     * @param id the id of the mstLoyaltyProgramDTO to save.
     * @param mstLoyaltyProgramDTO the mstLoyaltyProgramDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstLoyaltyProgramDTO,
     * or with status {@code 400 (Bad Request)} if the mstLoyaltyProgramDTO is not valid,
     * or with status {@code 404 (Not Found)} if the mstLoyaltyProgramDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the mstLoyaltyProgramDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MstLoyaltyProgramDTO>> partialUpdateMstLoyaltyProgram(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MstLoyaltyProgramDTO mstLoyaltyProgramDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update MstLoyaltyProgram partially : {}, {}", id, mstLoyaltyProgramDTO);
        if (mstLoyaltyProgramDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstLoyaltyProgramDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstLoyaltyProgramRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<MstLoyaltyProgramDTO> result = mstLoyaltyProgramService.partialUpdate(mstLoyaltyProgramDTO);

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
     * {@code GET  /mst-loyalty-programs} : get all the mstLoyaltyPrograms.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mstLoyaltyPrograms in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<MstLoyaltyProgramDTO>>> getAllMstLoyaltyPrograms(
        MstLoyaltyProgramCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get MstLoyaltyPrograms by criteria: {}", criteria);
        return mstLoyaltyProgramService
            .countByCriteria(criteria)
            .zipWith(mstLoyaltyProgramService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /mst-loyalty-programs/count} : count all the mstLoyaltyPrograms.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countMstLoyaltyPrograms(MstLoyaltyProgramCriteria criteria) {
        log.debug("REST request to count MstLoyaltyPrograms by criteria: {}", criteria);
        return mstLoyaltyProgramService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /mst-loyalty-programs/:id} : get the "id" mstLoyaltyProgram.
     *
     * @param id the id of the mstLoyaltyProgramDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mstLoyaltyProgramDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MstLoyaltyProgramDTO>> getMstLoyaltyProgram(@PathVariable("id") Long id) {
        log.debug("REST request to get MstLoyaltyProgram : {}", id);
        Mono<MstLoyaltyProgramDTO> mstLoyaltyProgramDTO = mstLoyaltyProgramService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mstLoyaltyProgramDTO);
    }

    /**
     * {@code DELETE  /mst-loyalty-programs/:id} : delete the "id" mstLoyaltyProgram.
     *
     * @param id the id of the mstLoyaltyProgramDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMstLoyaltyProgram(@PathVariable("id") Long id) {
        log.debug("REST request to delete MstLoyaltyProgram : {}", id);
        return mstLoyaltyProgramService
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
     * {@code SEARCH  /mst-loyalty-programs/_search?query=:query} : search for the mstLoyaltyProgram corresponding
     * to the query.
     *
     * @param query the query of the mstLoyaltyProgram search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<MstLoyaltyProgramDTO>>> searchMstLoyaltyPrograms(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of MstLoyaltyPrograms for query {}", query);
        return mstLoyaltyProgramService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(mstLoyaltyProgramService.search(query, pageable)));
    }
}
