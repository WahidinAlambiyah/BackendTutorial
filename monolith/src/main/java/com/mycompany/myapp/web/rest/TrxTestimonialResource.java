package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.TrxTestimonialCriteria;
import com.mycompany.myapp.repository.TrxTestimonialRepository;
import com.mycompany.myapp.service.TrxTestimonialService;
import com.mycompany.myapp.service.dto.TrxTestimonialDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.TrxTestimonial}.
 */
@RestController
@RequestMapping("/api/trx-testimonials")
public class TrxTestimonialResource {

    private static final Logger log = LoggerFactory.getLogger(TrxTestimonialResource.class);

    private static final String ENTITY_NAME = "trxTestimonial";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TrxTestimonialService trxTestimonialService;

    private final TrxTestimonialRepository trxTestimonialRepository;

    public TrxTestimonialResource(TrxTestimonialService trxTestimonialService, TrxTestimonialRepository trxTestimonialRepository) {
        this.trxTestimonialService = trxTestimonialService;
        this.trxTestimonialRepository = trxTestimonialRepository;
    }

    /**
     * {@code POST  /trx-testimonials} : Create a new trxTestimonial.
     *
     * @param trxTestimonialDTO the trxTestimonialDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new trxTestimonialDTO, or with status {@code 400 (Bad Request)} if the trxTestimonial has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<TrxTestimonialDTO>> createTrxTestimonial(@Valid @RequestBody TrxTestimonialDTO trxTestimonialDTO)
        throws URISyntaxException {
        log.debug("REST request to save TrxTestimonial : {}", trxTestimonialDTO);
        if (trxTestimonialDTO.getId() != null) {
            throw new BadRequestAlertException("A new trxTestimonial cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return trxTestimonialService
            .save(trxTestimonialDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/trx-testimonials/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /trx-testimonials/:id} : Updates an existing trxTestimonial.
     *
     * @param id the id of the trxTestimonialDTO to save.
     * @param trxTestimonialDTO the trxTestimonialDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trxTestimonialDTO,
     * or with status {@code 400 (Bad Request)} if the trxTestimonialDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the trxTestimonialDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TrxTestimonialDTO>> updateTrxTestimonial(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TrxTestimonialDTO trxTestimonialDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TrxTestimonial : {}, {}", id, trxTestimonialDTO);
        if (trxTestimonialDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trxTestimonialDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trxTestimonialRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return trxTestimonialService
                    .update(trxTestimonialDTO)
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
     * {@code PATCH  /trx-testimonials/:id} : Partial updates given fields of an existing trxTestimonial, field will ignore if it is null
     *
     * @param id the id of the trxTestimonialDTO to save.
     * @param trxTestimonialDTO the trxTestimonialDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trxTestimonialDTO,
     * or with status {@code 400 (Bad Request)} if the trxTestimonialDTO is not valid,
     * or with status {@code 404 (Not Found)} if the trxTestimonialDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the trxTestimonialDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TrxTestimonialDTO>> partialUpdateTrxTestimonial(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TrxTestimonialDTO trxTestimonialDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TrxTestimonial partially : {}, {}", id, trxTestimonialDTO);
        if (trxTestimonialDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trxTestimonialDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trxTestimonialRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TrxTestimonialDTO> result = trxTestimonialService.partialUpdate(trxTestimonialDTO);

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
     * {@code GET  /trx-testimonials} : get all the trxTestimonials.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of trxTestimonials in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<TrxTestimonialDTO>>> getAllTrxTestimonials(
        TrxTestimonialCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get TrxTestimonials by criteria: {}", criteria);
        return trxTestimonialService
            .countByCriteria(criteria)
            .zipWith(trxTestimonialService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /trx-testimonials/count} : count all the trxTestimonials.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countTrxTestimonials(TrxTestimonialCriteria criteria) {
        log.debug("REST request to count TrxTestimonials by criteria: {}", criteria);
        return trxTestimonialService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /trx-testimonials/:id} : get the "id" trxTestimonial.
     *
     * @param id the id of the trxTestimonialDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the trxTestimonialDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TrxTestimonialDTO>> getTrxTestimonial(@PathVariable("id") Long id) {
        log.debug("REST request to get TrxTestimonial : {}", id);
        Mono<TrxTestimonialDTO> trxTestimonialDTO = trxTestimonialService.findOne(id);
        return ResponseUtil.wrapOrNotFound(trxTestimonialDTO);
    }

    /**
     * {@code DELETE  /trx-testimonials/:id} : delete the "id" trxTestimonial.
     *
     * @param id the id of the trxTestimonialDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTrxTestimonial(@PathVariable("id") Long id) {
        log.debug("REST request to delete TrxTestimonial : {}", id);
        return trxTestimonialService
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
     * {@code SEARCH  /trx-testimonials/_search?query=:query} : search for the trxTestimonial corresponding
     * to the query.
     *
     * @param query the query of the trxTestimonial search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<TrxTestimonialDTO>>> searchTrxTestimonials(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of TrxTestimonials for query {}", query);
        return trxTestimonialService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(trxTestimonialService.search(query, pageable)));
    }
}
