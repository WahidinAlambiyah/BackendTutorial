package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.TrxOrderCriteria;
import com.mycompany.myapp.repository.TrxOrderRepository;
import com.mycompany.myapp.service.TrxOrderService;
import com.mycompany.myapp.service.dto.TrxOrderDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.TrxOrder}.
 */
@RestController
@RequestMapping("/api/trx-orders")
public class TrxOrderResource {

    private static final Logger log = LoggerFactory.getLogger(TrxOrderResource.class);

    private static final String ENTITY_NAME = "trxOrder";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TrxOrderService trxOrderService;

    private final TrxOrderRepository trxOrderRepository;

    public TrxOrderResource(TrxOrderService trxOrderService, TrxOrderRepository trxOrderRepository) {
        this.trxOrderService = trxOrderService;
        this.trxOrderRepository = trxOrderRepository;
    }

    /**
     * {@code POST  /trx-orders} : Create a new trxOrder.
     *
     * @param trxOrderDTO the trxOrderDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new trxOrderDTO, or with status {@code 400 (Bad Request)} if the trxOrder has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<TrxOrderDTO>> createTrxOrder(@Valid @RequestBody TrxOrderDTO trxOrderDTO) throws URISyntaxException {
        log.debug("REST request to save TrxOrder : {}", trxOrderDTO);
        if (trxOrderDTO.getId() != null) {
            throw new BadRequestAlertException("A new trxOrder cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return trxOrderService
            .save(trxOrderDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/trx-orders/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /trx-orders/:id} : Updates an existing trxOrder.
     *
     * @param id the id of the trxOrderDTO to save.
     * @param trxOrderDTO the trxOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trxOrderDTO,
     * or with status {@code 400 (Bad Request)} if the trxOrderDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the trxOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TrxOrderDTO>> updateTrxOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TrxOrderDTO trxOrderDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TrxOrder : {}, {}", id, trxOrderDTO);
        if (trxOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trxOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trxOrderRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return trxOrderService
                    .update(trxOrderDTO)
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
     * {@code PATCH  /trx-orders/:id} : Partial updates given fields of an existing trxOrder, field will ignore if it is null
     *
     * @param id the id of the trxOrderDTO to save.
     * @param trxOrderDTO the trxOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trxOrderDTO,
     * or with status {@code 400 (Bad Request)} if the trxOrderDTO is not valid,
     * or with status {@code 404 (Not Found)} if the trxOrderDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the trxOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TrxOrderDTO>> partialUpdateTrxOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TrxOrderDTO trxOrderDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TrxOrder partially : {}, {}", id, trxOrderDTO);
        if (trxOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trxOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trxOrderRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TrxOrderDTO> result = trxOrderService.partialUpdate(trxOrderDTO);

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
     * {@code GET  /trx-orders} : get all the trxOrders.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of trxOrders in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<TrxOrderDTO>>> getAllTrxOrders(
        TrxOrderCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get TrxOrders by criteria: {}", criteria);
        return trxOrderService
            .countByCriteria(criteria)
            .zipWith(trxOrderService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /trx-orders/count} : count all the trxOrders.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countTrxOrders(TrxOrderCriteria criteria) {
        log.debug("REST request to count TrxOrders by criteria: {}", criteria);
        return trxOrderService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /trx-orders/:id} : get the "id" trxOrder.
     *
     * @param id the id of the trxOrderDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the trxOrderDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TrxOrderDTO>> getTrxOrder(@PathVariable("id") Long id) {
        log.debug("REST request to get TrxOrder : {}", id);
        Mono<TrxOrderDTO> trxOrderDTO = trxOrderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(trxOrderDTO);
    }

    /**
     * {@code DELETE  /trx-orders/:id} : delete the "id" trxOrder.
     *
     * @param id the id of the trxOrderDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTrxOrder(@PathVariable("id") Long id) {
        log.debug("REST request to delete TrxOrder : {}", id);
        return trxOrderService
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
     * {@code SEARCH  /trx-orders/_search?query=:query} : search for the trxOrder corresponding
     * to the query.
     *
     * @param query the query of the trxOrder search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<TrxOrderDTO>>> searchTrxOrders(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of TrxOrders for query {}", query);
        return trxOrderService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(trxOrderService.search(query, pageable)));
    }
}
