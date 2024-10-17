package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.TrxOrderItemCriteria;
import com.mycompany.myapp.repository.TrxOrderItemRepository;
import com.mycompany.myapp.service.TrxOrderItemService;
import com.mycompany.myapp.service.dto.TrxOrderItemDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.TrxOrderItem}.
 */
@RestController
@RequestMapping("/api/trx-order-items")
public class TrxOrderItemResource {

    private static final Logger log = LoggerFactory.getLogger(TrxOrderItemResource.class);

    private static final String ENTITY_NAME = "trxOrderItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TrxOrderItemService trxOrderItemService;

    private final TrxOrderItemRepository trxOrderItemRepository;

    public TrxOrderItemResource(TrxOrderItemService trxOrderItemService, TrxOrderItemRepository trxOrderItemRepository) {
        this.trxOrderItemService = trxOrderItemService;
        this.trxOrderItemRepository = trxOrderItemRepository;
    }

    /**
     * {@code POST  /trx-order-items} : Create a new trxOrderItem.
     *
     * @param trxOrderItemDTO the trxOrderItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new trxOrderItemDTO, or with status {@code 400 (Bad Request)} if the trxOrderItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<TrxOrderItemDTO>> createTrxOrderItem(@Valid @RequestBody TrxOrderItemDTO trxOrderItemDTO)
        throws URISyntaxException {
        log.debug("REST request to save TrxOrderItem : {}", trxOrderItemDTO);
        if (trxOrderItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new trxOrderItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return trxOrderItemService
            .save(trxOrderItemDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/trx-order-items/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /trx-order-items/:id} : Updates an existing trxOrderItem.
     *
     * @param id the id of the trxOrderItemDTO to save.
     * @param trxOrderItemDTO the trxOrderItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trxOrderItemDTO,
     * or with status {@code 400 (Bad Request)} if the trxOrderItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the trxOrderItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TrxOrderItemDTO>> updateTrxOrderItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TrxOrderItemDTO trxOrderItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TrxOrderItem : {}, {}", id, trxOrderItemDTO);
        if (trxOrderItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trxOrderItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trxOrderItemRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return trxOrderItemService
                    .update(trxOrderItemDTO)
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
     * {@code PATCH  /trx-order-items/:id} : Partial updates given fields of an existing trxOrderItem, field will ignore if it is null
     *
     * @param id the id of the trxOrderItemDTO to save.
     * @param trxOrderItemDTO the trxOrderItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trxOrderItemDTO,
     * or with status {@code 400 (Bad Request)} if the trxOrderItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the trxOrderItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the trxOrderItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TrxOrderItemDTO>> partialUpdateTrxOrderItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TrxOrderItemDTO trxOrderItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TrxOrderItem partially : {}, {}", id, trxOrderItemDTO);
        if (trxOrderItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trxOrderItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trxOrderItemRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TrxOrderItemDTO> result = trxOrderItemService.partialUpdate(trxOrderItemDTO);

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
     * {@code GET  /trx-order-items} : get all the trxOrderItems.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of trxOrderItems in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<TrxOrderItemDTO>>> getAllTrxOrderItems(
        TrxOrderItemCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get TrxOrderItems by criteria: {}", criteria);
        return trxOrderItemService
            .countByCriteria(criteria)
            .zipWith(trxOrderItemService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /trx-order-items/count} : count all the trxOrderItems.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countTrxOrderItems(TrxOrderItemCriteria criteria) {
        log.debug("REST request to count TrxOrderItems by criteria: {}", criteria);
        return trxOrderItemService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /trx-order-items/:id} : get the "id" trxOrderItem.
     *
     * @param id the id of the trxOrderItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the trxOrderItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TrxOrderItemDTO>> getTrxOrderItem(@PathVariable("id") Long id) {
        log.debug("REST request to get TrxOrderItem : {}", id);
        Mono<TrxOrderItemDTO> trxOrderItemDTO = trxOrderItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(trxOrderItemDTO);
    }

    /**
     * {@code DELETE  /trx-order-items/:id} : delete the "id" trxOrderItem.
     *
     * @param id the id of the trxOrderItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTrxOrderItem(@PathVariable("id") Long id) {
        log.debug("REST request to delete TrxOrderItem : {}", id);
        return trxOrderItemService
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
     * {@code SEARCH  /trx-order-items/_search?query=:query} : search for the trxOrderItem corresponding
     * to the query.
     *
     * @param query the query of the trxOrderItem search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<TrxOrderItemDTO>>> searchTrxOrderItems(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of TrxOrderItems for query {}", query);
        return trxOrderItemService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(trxOrderItemService.search(query, pageable)));
    }
}
