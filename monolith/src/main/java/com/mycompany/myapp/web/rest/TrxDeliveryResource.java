package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.TrxDeliveryCriteria;
import com.mycompany.myapp.repository.TrxDeliveryRepository;
import com.mycompany.myapp.service.TrxDeliveryService;
import com.mycompany.myapp.service.dto.TrxDeliveryDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.TrxDelivery}.
 */
@RestController
@RequestMapping("/api/trx-deliveries")
public class TrxDeliveryResource {

    private static final Logger log = LoggerFactory.getLogger(TrxDeliveryResource.class);

    private static final String ENTITY_NAME = "trxDelivery";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TrxDeliveryService trxDeliveryService;

    private final TrxDeliveryRepository trxDeliveryRepository;

    public TrxDeliveryResource(TrxDeliveryService trxDeliveryService, TrxDeliveryRepository trxDeliveryRepository) {
        this.trxDeliveryService = trxDeliveryService;
        this.trxDeliveryRepository = trxDeliveryRepository;
    }

    /**
     * {@code POST  /trx-deliveries} : Create a new trxDelivery.
     *
     * @param trxDeliveryDTO the trxDeliveryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new trxDeliveryDTO, or with status {@code 400 (Bad Request)} if the trxDelivery has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<TrxDeliveryDTO>> createTrxDelivery(@Valid @RequestBody TrxDeliveryDTO trxDeliveryDTO)
        throws URISyntaxException {
        log.debug("REST request to save TrxDelivery : {}", trxDeliveryDTO);
        if (trxDeliveryDTO.getId() != null) {
            throw new BadRequestAlertException("A new trxDelivery cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return trxDeliveryService
            .save(trxDeliveryDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/trx-deliveries/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /trx-deliveries/:id} : Updates an existing trxDelivery.
     *
     * @param id the id of the trxDeliveryDTO to save.
     * @param trxDeliveryDTO the trxDeliveryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trxDeliveryDTO,
     * or with status {@code 400 (Bad Request)} if the trxDeliveryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the trxDeliveryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TrxDeliveryDTO>> updateTrxDelivery(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TrxDeliveryDTO trxDeliveryDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TrxDelivery : {}, {}", id, trxDeliveryDTO);
        if (trxDeliveryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trxDeliveryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trxDeliveryRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return trxDeliveryService
                    .update(trxDeliveryDTO)
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
     * {@code PATCH  /trx-deliveries/:id} : Partial updates given fields of an existing trxDelivery, field will ignore if it is null
     *
     * @param id the id of the trxDeliveryDTO to save.
     * @param trxDeliveryDTO the trxDeliveryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trxDeliveryDTO,
     * or with status {@code 400 (Bad Request)} if the trxDeliveryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the trxDeliveryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the trxDeliveryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TrxDeliveryDTO>> partialUpdateTrxDelivery(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TrxDeliveryDTO trxDeliveryDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TrxDelivery partially : {}, {}", id, trxDeliveryDTO);
        if (trxDeliveryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trxDeliveryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trxDeliveryRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TrxDeliveryDTO> result = trxDeliveryService.partialUpdate(trxDeliveryDTO);

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
     * {@code GET  /trx-deliveries} : get all the trxDeliveries.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of trxDeliveries in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<TrxDeliveryDTO>>> getAllTrxDeliveries(
        TrxDeliveryCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get TrxDeliveries by criteria: {}", criteria);
        return trxDeliveryService
            .countByCriteria(criteria)
            .zipWith(trxDeliveryService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /trx-deliveries/count} : count all the trxDeliveries.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countTrxDeliveries(TrxDeliveryCriteria criteria) {
        log.debug("REST request to count TrxDeliveries by criteria: {}", criteria);
        return trxDeliveryService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /trx-deliveries/:id} : get the "id" trxDelivery.
     *
     * @param id the id of the trxDeliveryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the trxDeliveryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TrxDeliveryDTO>> getTrxDelivery(@PathVariable("id") Long id) {
        log.debug("REST request to get TrxDelivery : {}", id);
        Mono<TrxDeliveryDTO> trxDeliveryDTO = trxDeliveryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(trxDeliveryDTO);
    }

    /**
     * {@code DELETE  /trx-deliveries/:id} : delete the "id" trxDelivery.
     *
     * @param id the id of the trxDeliveryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTrxDelivery(@PathVariable("id") Long id) {
        log.debug("REST request to delete TrxDelivery : {}", id);
        return trxDeliveryService
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
     * {@code SEARCH  /trx-deliveries/_search?query=:query} : search for the trxDelivery corresponding
     * to the query.
     *
     * @param query the query of the trxDelivery search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<TrxDeliveryDTO>>> searchTrxDeliveries(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of TrxDeliveries for query {}", query);
        return trxDeliveryService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(trxDeliveryService.search(query, pageable)));
    }
}
