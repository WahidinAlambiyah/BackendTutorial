package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.TrxOrderStockCriteria;
import com.mycompany.myapp.repository.TrxOrderStockRepository;
import com.mycompany.myapp.service.TrxOrderStockService;
import com.mycompany.myapp.service.dto.TrxOrderStockDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.TrxOrderStock}.
 */
@RestController
@RequestMapping("/api/trx-order-stocks")
public class TrxOrderStockResource {

    private static final Logger log = LoggerFactory.getLogger(TrxOrderStockResource.class);

    private static final String ENTITY_NAME = "trxOrderStock";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TrxOrderStockService trxOrderStockService;

    private final TrxOrderStockRepository trxOrderStockRepository;

    public TrxOrderStockResource(TrxOrderStockService trxOrderStockService, TrxOrderStockRepository trxOrderStockRepository) {
        this.trxOrderStockService = trxOrderStockService;
        this.trxOrderStockRepository = trxOrderStockRepository;
    }

    /**
     * {@code POST  /trx-order-stocks} : Create a new trxOrderStock.
     *
     * @param trxOrderStockDTO the trxOrderStockDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new trxOrderStockDTO, or with status {@code 400 (Bad Request)} if the trxOrderStock has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<TrxOrderStockDTO>> createTrxOrderStock(@Valid @RequestBody TrxOrderStockDTO trxOrderStockDTO)
        throws URISyntaxException {
        log.debug("REST request to save TrxOrderStock : {}", trxOrderStockDTO);
        if (trxOrderStockDTO.getId() != null) {
            throw new BadRequestAlertException("A new trxOrderStock cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return trxOrderStockService
            .save(trxOrderStockDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/trx-order-stocks/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /trx-order-stocks/:id} : Updates an existing trxOrderStock.
     *
     * @param id the id of the trxOrderStockDTO to save.
     * @param trxOrderStockDTO the trxOrderStockDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trxOrderStockDTO,
     * or with status {@code 400 (Bad Request)} if the trxOrderStockDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the trxOrderStockDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TrxOrderStockDTO>> updateTrxOrderStock(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TrxOrderStockDTO trxOrderStockDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TrxOrderStock : {}, {}", id, trxOrderStockDTO);
        if (trxOrderStockDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trxOrderStockDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trxOrderStockRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return trxOrderStockService
                    .update(trxOrderStockDTO)
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
     * {@code PATCH  /trx-order-stocks/:id} : Partial updates given fields of an existing trxOrderStock, field will ignore if it is null
     *
     * @param id the id of the trxOrderStockDTO to save.
     * @param trxOrderStockDTO the trxOrderStockDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trxOrderStockDTO,
     * or with status {@code 400 (Bad Request)} if the trxOrderStockDTO is not valid,
     * or with status {@code 404 (Not Found)} if the trxOrderStockDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the trxOrderStockDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TrxOrderStockDTO>> partialUpdateTrxOrderStock(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TrxOrderStockDTO trxOrderStockDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TrxOrderStock partially : {}, {}", id, trxOrderStockDTO);
        if (trxOrderStockDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trxOrderStockDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trxOrderStockRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TrxOrderStockDTO> result = trxOrderStockService.partialUpdate(trxOrderStockDTO);

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
     * {@code GET  /trx-order-stocks} : get all the trxOrderStocks.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of trxOrderStocks in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<TrxOrderStockDTO>>> getAllTrxOrderStocks(
        TrxOrderStockCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get TrxOrderStocks by criteria: {}", criteria);
        return trxOrderStockService
            .countByCriteria(criteria)
            .zipWith(trxOrderStockService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /trx-order-stocks/count} : count all the trxOrderStocks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countTrxOrderStocks(TrxOrderStockCriteria criteria) {
        log.debug("REST request to count TrxOrderStocks by criteria: {}", criteria);
        return trxOrderStockService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /trx-order-stocks/:id} : get the "id" trxOrderStock.
     *
     * @param id the id of the trxOrderStockDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the trxOrderStockDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TrxOrderStockDTO>> getTrxOrderStock(@PathVariable("id") Long id) {
        log.debug("REST request to get TrxOrderStock : {}", id);
        Mono<TrxOrderStockDTO> trxOrderStockDTO = trxOrderStockService.findOne(id);
        return ResponseUtil.wrapOrNotFound(trxOrderStockDTO);
    }

    /**
     * {@code DELETE  /trx-order-stocks/:id} : delete the "id" trxOrderStock.
     *
     * @param id the id of the trxOrderStockDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTrxOrderStock(@PathVariable("id") Long id) {
        log.debug("REST request to delete TrxOrderStock : {}", id);
        return trxOrderStockService
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
     * {@code SEARCH  /trx-order-stocks/_search?query=:query} : search for the trxOrderStock corresponding
     * to the query.
     *
     * @param query the query of the trxOrderStock search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<TrxOrderStockDTO>>> searchTrxOrderStocks(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of TrxOrderStocks for query {}", query);
        return trxOrderStockService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(trxOrderStockService.search(query, pageable)));
    }
}
