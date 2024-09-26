package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.MstJobCriteria;
import com.mycompany.myapp.repository.MstJobRepository;
import com.mycompany.myapp.service.MstJobService;
import com.mycompany.myapp.service.dto.MstJobDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.MstJob}.
 */
@RestController
@RequestMapping("/api/mst-jobs")
public class MstJobResource {

    private static final Logger log = LoggerFactory.getLogger(MstJobResource.class);

    private static final String ENTITY_NAME = "mstJob";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MstJobService mstJobService;

    private final MstJobRepository mstJobRepository;

    public MstJobResource(MstJobService mstJobService, MstJobRepository mstJobRepository) {
        this.mstJobService = mstJobService;
        this.mstJobRepository = mstJobRepository;
    }

    /**
     * {@code POST  /mst-jobs} : Create a new mstJob.
     *
     * @param mstJobDTO the mstJobDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mstJobDTO, or with status {@code 400 (Bad Request)} if the mstJob has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MstJobDTO>> createMstJob(@RequestBody MstJobDTO mstJobDTO) throws URISyntaxException {
        log.debug("REST request to save MstJob : {}", mstJobDTO);
        if (mstJobDTO.getId() != null) {
            throw new BadRequestAlertException("A new mstJob cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return mstJobService
            .save(mstJobDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/mst-jobs/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /mst-jobs/:id} : Updates an existing mstJob.
     *
     * @param id the id of the mstJobDTO to save.
     * @param mstJobDTO the mstJobDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstJobDTO,
     * or with status {@code 400 (Bad Request)} if the mstJobDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mstJobDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MstJobDTO>> updateMstJob(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MstJobDTO mstJobDTO
    ) throws URISyntaxException {
        log.debug("REST request to update MstJob : {}, {}", id, mstJobDTO);
        if (mstJobDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstJobDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstJobRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return mstJobService
                    .update(mstJobDTO)
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
     * {@code PATCH  /mst-jobs/:id} : Partial updates given fields of an existing mstJob, field will ignore if it is null
     *
     * @param id the id of the mstJobDTO to save.
     * @param mstJobDTO the mstJobDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstJobDTO,
     * or with status {@code 400 (Bad Request)} if the mstJobDTO is not valid,
     * or with status {@code 404 (Not Found)} if the mstJobDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the mstJobDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MstJobDTO>> partialUpdateMstJob(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MstJobDTO mstJobDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update MstJob partially : {}, {}", id, mstJobDTO);
        if (mstJobDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstJobDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstJobRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<MstJobDTO> result = mstJobService.partialUpdate(mstJobDTO);

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
     * {@code GET  /mst-jobs} : get all the mstJobs.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mstJobs in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<MstJobDTO>>> getAllMstJobs(
        MstJobCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get MstJobs by criteria: {}", criteria);
        return mstJobService
            .countByCriteria(criteria)
            .zipWith(mstJobService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /mst-jobs/count} : count all the mstJobs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countMstJobs(MstJobCriteria criteria) {
        log.debug("REST request to count MstJobs by criteria: {}", criteria);
        return mstJobService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /mst-jobs/:id} : get the "id" mstJob.
     *
     * @param id the id of the mstJobDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mstJobDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MstJobDTO>> getMstJob(@PathVariable("id") Long id) {
        log.debug("REST request to get MstJob : {}", id);
        Mono<MstJobDTO> mstJobDTO = mstJobService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mstJobDTO);
    }

    /**
     * {@code DELETE  /mst-jobs/:id} : delete the "id" mstJob.
     *
     * @param id the id of the mstJobDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMstJob(@PathVariable("id") Long id) {
        log.debug("REST request to delete MstJob : {}", id);
        return mstJobService
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
     * {@code SEARCH  /mst-jobs/_search?query=:query} : search for the mstJob corresponding
     * to the query.
     *
     * @param query the query of the mstJob search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<MstJobDTO>>> searchMstJobs(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of MstJobs for query {}", query);
        return mstJobService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(mstJobService.search(query, pageable)));
    }
}
