package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.TrxDiscountCriteria;
import com.mycompany.myapp.repository.TrxDiscountRepository;
import com.mycompany.myapp.service.TrxDiscountService;
import com.mycompany.myapp.service.dto.TrxDiscountDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.TrxDiscount}.
 */
@RestController
@RequestMapping("/api/trx-discounts")
public class TrxDiscountResource {

    private static final Logger log = LoggerFactory.getLogger(TrxDiscountResource.class);

    private static final String ENTITY_NAME = "trxDiscount";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TrxDiscountService trxDiscountService;

    private final TrxDiscountRepository trxDiscountRepository;

    public TrxDiscountResource(TrxDiscountService trxDiscountService, TrxDiscountRepository trxDiscountRepository) {
        this.trxDiscountService = trxDiscountService;
        this.trxDiscountRepository = trxDiscountRepository;
    }

    /**
     * {@code POST  /trx-discounts} : Create a new trxDiscount.
     *
     * @param trxDiscountDTO the trxDiscountDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new trxDiscountDTO, or with status {@code 400 (Bad Request)} if the trxDiscount has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<TrxDiscountDTO>> createTrxDiscount(@Valid @RequestBody TrxDiscountDTO trxDiscountDTO)
        throws URISyntaxException {
        log.debug("REST request to save TrxDiscount : {}", trxDiscountDTO);
        if (trxDiscountDTO.getId() != null) {
            throw new BadRequestAlertException("A new trxDiscount cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return trxDiscountService
            .save(trxDiscountDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/trx-discounts/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /trx-discounts/:id} : Updates an existing trxDiscount.
     *
     * @param id the id of the trxDiscountDTO to save.
     * @param trxDiscountDTO the trxDiscountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trxDiscountDTO,
     * or with status {@code 400 (Bad Request)} if the trxDiscountDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the trxDiscountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TrxDiscountDTO>> updateTrxDiscount(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TrxDiscountDTO trxDiscountDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TrxDiscount : {}, {}", id, trxDiscountDTO);
        if (trxDiscountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trxDiscountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trxDiscountRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return trxDiscountService
                    .update(trxDiscountDTO)
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
     * {@code PATCH  /trx-discounts/:id} : Partial updates given fields of an existing trxDiscount, field will ignore if it is null
     *
     * @param id the id of the trxDiscountDTO to save.
     * @param trxDiscountDTO the trxDiscountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trxDiscountDTO,
     * or with status {@code 400 (Bad Request)} if the trxDiscountDTO is not valid,
     * or with status {@code 404 (Not Found)} if the trxDiscountDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the trxDiscountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TrxDiscountDTO>> partialUpdateTrxDiscount(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TrxDiscountDTO trxDiscountDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TrxDiscount partially : {}, {}", id, trxDiscountDTO);
        if (trxDiscountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trxDiscountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trxDiscountRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TrxDiscountDTO> result = trxDiscountService.partialUpdate(trxDiscountDTO);

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
     * {@code GET  /trx-discounts} : get all the trxDiscounts.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of trxDiscounts in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<TrxDiscountDTO>>> getAllTrxDiscounts(
        TrxDiscountCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get TrxDiscounts by criteria: {}", criteria);
        return trxDiscountService
            .countByCriteria(criteria)
            .zipWith(trxDiscountService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /trx-discounts/count} : count all the trxDiscounts.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countTrxDiscounts(TrxDiscountCriteria criteria) {
        log.debug("REST request to count TrxDiscounts by criteria: {}", criteria);
        return trxDiscountService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /trx-discounts/:id} : get the "id" trxDiscount.
     *
     * @param id the id of the trxDiscountDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the trxDiscountDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TrxDiscountDTO>> getTrxDiscount(@PathVariable("id") Long id) {
        log.debug("REST request to get TrxDiscount : {}", id);
        Mono<TrxDiscountDTO> trxDiscountDTO = trxDiscountService.findOne(id);
        return ResponseUtil.wrapOrNotFound(trxDiscountDTO);
    }

    /**
     * {@code DELETE  /trx-discounts/:id} : delete the "id" trxDiscount.
     *
     * @param id the id of the trxDiscountDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTrxDiscount(@PathVariable("id") Long id) {
        log.debug("REST request to delete TrxDiscount : {}", id);
        return trxDiscountService
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
     * {@code SEARCH  /trx-discounts/_search?query=:query} : search for the trxDiscount corresponding
     * to the query.
     *
     * @param query the query of the trxDiscount search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<TrxDiscountDTO>>> searchTrxDiscounts(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of TrxDiscounts for query {}", query);
        return trxDiscountService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(trxDiscountService.search(query, pageable)));
    }
}
