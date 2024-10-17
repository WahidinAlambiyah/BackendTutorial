package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.TrxOrderHistoryCriteria;
import com.mycompany.myapp.repository.TrxOrderHistoryRepository;
import com.mycompany.myapp.service.TrxOrderHistoryService;
import com.mycompany.myapp.service.dto.TrxOrderHistoryDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.TrxOrderHistory}.
 */
@RestController
@RequestMapping("/api/trx-order-histories")
public class TrxOrderHistoryResource {

    private static final Logger log = LoggerFactory.getLogger(TrxOrderHistoryResource.class);

    private static final String ENTITY_NAME = "trxOrderHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TrxOrderHistoryService trxOrderHistoryService;

    private final TrxOrderHistoryRepository trxOrderHistoryRepository;

    public TrxOrderHistoryResource(TrxOrderHistoryService trxOrderHistoryService, TrxOrderHistoryRepository trxOrderHistoryRepository) {
        this.trxOrderHistoryService = trxOrderHistoryService;
        this.trxOrderHistoryRepository = trxOrderHistoryRepository;
    }

    /**
     * {@code POST  /trx-order-histories} : Create a new trxOrderHistory.
     *
     * @param trxOrderHistoryDTO the trxOrderHistoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new trxOrderHistoryDTO, or with status {@code 400 (Bad Request)} if the trxOrderHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<TrxOrderHistoryDTO>> createTrxOrderHistory(@Valid @RequestBody TrxOrderHistoryDTO trxOrderHistoryDTO)
        throws URISyntaxException {
        log.debug("REST request to save TrxOrderHistory : {}", trxOrderHistoryDTO);
        if (trxOrderHistoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new trxOrderHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return trxOrderHistoryService
            .save(trxOrderHistoryDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/trx-order-histories/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /trx-order-histories/:id} : Updates an existing trxOrderHistory.
     *
     * @param id the id of the trxOrderHistoryDTO to save.
     * @param trxOrderHistoryDTO the trxOrderHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trxOrderHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the trxOrderHistoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the trxOrderHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TrxOrderHistoryDTO>> updateTrxOrderHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TrxOrderHistoryDTO trxOrderHistoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TrxOrderHistory : {}, {}", id, trxOrderHistoryDTO);
        if (trxOrderHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trxOrderHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trxOrderHistoryRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return trxOrderHistoryService
                    .update(trxOrderHistoryDTO)
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
     * {@code PATCH  /trx-order-histories/:id} : Partial updates given fields of an existing trxOrderHistory, field will ignore if it is null
     *
     * @param id the id of the trxOrderHistoryDTO to save.
     * @param trxOrderHistoryDTO the trxOrderHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trxOrderHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the trxOrderHistoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the trxOrderHistoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the trxOrderHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TrxOrderHistoryDTO>> partialUpdateTrxOrderHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TrxOrderHistoryDTO trxOrderHistoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TrxOrderHistory partially : {}, {}", id, trxOrderHistoryDTO);
        if (trxOrderHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trxOrderHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trxOrderHistoryRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TrxOrderHistoryDTO> result = trxOrderHistoryService.partialUpdate(trxOrderHistoryDTO);

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
     * {@code GET  /trx-order-histories} : get all the trxOrderHistories.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of trxOrderHistories in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<TrxOrderHistoryDTO>>> getAllTrxOrderHistories(
        TrxOrderHistoryCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get TrxOrderHistories by criteria: {}", criteria);
        return trxOrderHistoryService
            .countByCriteria(criteria)
            .zipWith(trxOrderHistoryService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /trx-order-histories/count} : count all the trxOrderHistories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countTrxOrderHistories(TrxOrderHistoryCriteria criteria) {
        log.debug("REST request to count TrxOrderHistories by criteria: {}", criteria);
        return trxOrderHistoryService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /trx-order-histories/:id} : get the "id" trxOrderHistory.
     *
     * @param id the id of the trxOrderHistoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the trxOrderHistoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TrxOrderHistoryDTO>> getTrxOrderHistory(@PathVariable("id") Long id) {
        log.debug("REST request to get TrxOrderHistory : {}", id);
        Mono<TrxOrderHistoryDTO> trxOrderHistoryDTO = trxOrderHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(trxOrderHistoryDTO);
    }

    /**
     * {@code DELETE  /trx-order-histories/:id} : delete the "id" trxOrderHistory.
     *
     * @param id the id of the trxOrderHistoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTrxOrderHistory(@PathVariable("id") Long id) {
        log.debug("REST request to delete TrxOrderHistory : {}", id);
        return trxOrderHistoryService
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
     * {@code SEARCH  /trx-order-histories/_search?query=:query} : search for the trxOrderHistory corresponding
     * to the query.
     *
     * @param query the query of the trxOrderHistory search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<TrxOrderHistoryDTO>>> searchTrxOrderHistories(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of TrxOrderHistories for query {}", query);
        return trxOrderHistoryService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(trxOrderHistoryService.search(query, pageable)));
    }
}
