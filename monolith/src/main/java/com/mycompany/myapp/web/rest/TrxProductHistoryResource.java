package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.TrxProductHistoryCriteria;
import com.mycompany.myapp.repository.TrxProductHistoryRepository;
import com.mycompany.myapp.service.TrxProductHistoryService;
import com.mycompany.myapp.service.dto.TrxProductHistoryDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.TrxProductHistory}.
 */
@RestController
@RequestMapping("/api/trx-product-histories")
public class TrxProductHistoryResource {

    private static final Logger log = LoggerFactory.getLogger(TrxProductHistoryResource.class);

    private static final String ENTITY_NAME = "trxProductHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TrxProductHistoryService trxProductHistoryService;

    private final TrxProductHistoryRepository trxProductHistoryRepository;

    public TrxProductHistoryResource(
        TrxProductHistoryService trxProductHistoryService,
        TrxProductHistoryRepository trxProductHistoryRepository
    ) {
        this.trxProductHistoryService = trxProductHistoryService;
        this.trxProductHistoryRepository = trxProductHistoryRepository;
    }

    /**
     * {@code POST  /trx-product-histories} : Create a new trxProductHistory.
     *
     * @param trxProductHistoryDTO the trxProductHistoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new trxProductHistoryDTO, or with status {@code 400 (Bad Request)} if the trxProductHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<TrxProductHistoryDTO>> createTrxProductHistory(
        @Valid @RequestBody TrxProductHistoryDTO trxProductHistoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to save TrxProductHistory : {}", trxProductHistoryDTO);
        if (trxProductHistoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new trxProductHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return trxProductHistoryService
            .save(trxProductHistoryDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/trx-product-histories/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /trx-product-histories/:id} : Updates an existing trxProductHistory.
     *
     * @param id the id of the trxProductHistoryDTO to save.
     * @param trxProductHistoryDTO the trxProductHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trxProductHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the trxProductHistoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the trxProductHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TrxProductHistoryDTO>> updateTrxProductHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TrxProductHistoryDTO trxProductHistoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TrxProductHistory : {}, {}", id, trxProductHistoryDTO);
        if (trxProductHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trxProductHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trxProductHistoryRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return trxProductHistoryService
                    .update(trxProductHistoryDTO)
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
     * {@code PATCH  /trx-product-histories/:id} : Partial updates given fields of an existing trxProductHistory, field will ignore if it is null
     *
     * @param id the id of the trxProductHistoryDTO to save.
     * @param trxProductHistoryDTO the trxProductHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trxProductHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the trxProductHistoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the trxProductHistoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the trxProductHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TrxProductHistoryDTO>> partialUpdateTrxProductHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TrxProductHistoryDTO trxProductHistoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TrxProductHistory partially : {}, {}", id, trxProductHistoryDTO);
        if (trxProductHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trxProductHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trxProductHistoryRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TrxProductHistoryDTO> result = trxProductHistoryService.partialUpdate(trxProductHistoryDTO);

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
     * {@code GET  /trx-product-histories} : get all the trxProductHistories.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of trxProductHistories in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<TrxProductHistoryDTO>>> getAllTrxProductHistories(
        TrxProductHistoryCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get TrxProductHistories by criteria: {}", criteria);
        return trxProductHistoryService
            .countByCriteria(criteria)
            .zipWith(trxProductHistoryService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /trx-product-histories/count} : count all the trxProductHistories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countTrxProductHistories(TrxProductHistoryCriteria criteria) {
        log.debug("REST request to count TrxProductHistories by criteria: {}", criteria);
        return trxProductHistoryService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /trx-product-histories/:id} : get the "id" trxProductHistory.
     *
     * @param id the id of the trxProductHistoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the trxProductHistoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TrxProductHistoryDTO>> getTrxProductHistory(@PathVariable("id") Long id) {
        log.debug("REST request to get TrxProductHistory : {}", id);
        Mono<TrxProductHistoryDTO> trxProductHistoryDTO = trxProductHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(trxProductHistoryDTO);
    }

    /**
     * {@code DELETE  /trx-product-histories/:id} : delete the "id" trxProductHistory.
     *
     * @param id the id of the trxProductHistoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTrxProductHistory(@PathVariable("id") Long id) {
        log.debug("REST request to delete TrxProductHistory : {}", id);
        return trxProductHistoryService
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
     * {@code SEARCH  /trx-product-histories/_search?query=:query} : search for the trxProductHistory corresponding
     * to the query.
     *
     * @param query the query of the trxProductHistory search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<TrxProductHistoryDTO>>> searchTrxProductHistories(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of TrxProductHistories for query {}", query);
        return trxProductHistoryService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(trxProductHistoryService.search(query, pageable)));
    }
}
