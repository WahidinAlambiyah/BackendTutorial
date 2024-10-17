package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.TrxStockAlertCriteria;
import com.mycompany.myapp.repository.TrxStockAlertRepository;
import com.mycompany.myapp.service.TrxStockAlertService;
import com.mycompany.myapp.service.dto.TrxStockAlertDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.TrxStockAlert}.
 */
@RestController
@RequestMapping("/api/trx-stock-alerts")
public class TrxStockAlertResource {

    private static final Logger log = LoggerFactory.getLogger(TrxStockAlertResource.class);

    private static final String ENTITY_NAME = "trxStockAlert";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TrxStockAlertService trxStockAlertService;

    private final TrxStockAlertRepository trxStockAlertRepository;

    public TrxStockAlertResource(TrxStockAlertService trxStockAlertService, TrxStockAlertRepository trxStockAlertRepository) {
        this.trxStockAlertService = trxStockAlertService;
        this.trxStockAlertRepository = trxStockAlertRepository;
    }

    /**
     * {@code POST  /trx-stock-alerts} : Create a new trxStockAlert.
     *
     * @param trxStockAlertDTO the trxStockAlertDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new trxStockAlertDTO, or with status {@code 400 (Bad Request)} if the trxStockAlert has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<TrxStockAlertDTO>> createTrxStockAlert(@Valid @RequestBody TrxStockAlertDTO trxStockAlertDTO)
        throws URISyntaxException {
        log.debug("REST request to save TrxStockAlert : {}", trxStockAlertDTO);
        if (trxStockAlertDTO.getId() != null) {
            throw new BadRequestAlertException("A new trxStockAlert cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return trxStockAlertService
            .save(trxStockAlertDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/trx-stock-alerts/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /trx-stock-alerts/:id} : Updates an existing trxStockAlert.
     *
     * @param id the id of the trxStockAlertDTO to save.
     * @param trxStockAlertDTO the trxStockAlertDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trxStockAlertDTO,
     * or with status {@code 400 (Bad Request)} if the trxStockAlertDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the trxStockAlertDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TrxStockAlertDTO>> updateTrxStockAlert(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TrxStockAlertDTO trxStockAlertDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TrxStockAlert : {}, {}", id, trxStockAlertDTO);
        if (trxStockAlertDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trxStockAlertDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trxStockAlertRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return trxStockAlertService
                    .update(trxStockAlertDTO)
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
     * {@code PATCH  /trx-stock-alerts/:id} : Partial updates given fields of an existing trxStockAlert, field will ignore if it is null
     *
     * @param id the id of the trxStockAlertDTO to save.
     * @param trxStockAlertDTO the trxStockAlertDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trxStockAlertDTO,
     * or with status {@code 400 (Bad Request)} if the trxStockAlertDTO is not valid,
     * or with status {@code 404 (Not Found)} if the trxStockAlertDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the trxStockAlertDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TrxStockAlertDTO>> partialUpdateTrxStockAlert(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TrxStockAlertDTO trxStockAlertDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TrxStockAlert partially : {}, {}", id, trxStockAlertDTO);
        if (trxStockAlertDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trxStockAlertDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trxStockAlertRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TrxStockAlertDTO> result = trxStockAlertService.partialUpdate(trxStockAlertDTO);

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
     * {@code GET  /trx-stock-alerts} : get all the trxStockAlerts.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of trxStockAlerts in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<TrxStockAlertDTO>>> getAllTrxStockAlerts(
        TrxStockAlertCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get TrxStockAlerts by criteria: {}", criteria);
        return trxStockAlertService
            .countByCriteria(criteria)
            .zipWith(trxStockAlertService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /trx-stock-alerts/count} : count all the trxStockAlerts.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countTrxStockAlerts(TrxStockAlertCriteria criteria) {
        log.debug("REST request to count TrxStockAlerts by criteria: {}", criteria);
        return trxStockAlertService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /trx-stock-alerts/:id} : get the "id" trxStockAlert.
     *
     * @param id the id of the trxStockAlertDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the trxStockAlertDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TrxStockAlertDTO>> getTrxStockAlert(@PathVariable("id") Long id) {
        log.debug("REST request to get TrxStockAlert : {}", id);
        Mono<TrxStockAlertDTO> trxStockAlertDTO = trxStockAlertService.findOne(id);
        return ResponseUtil.wrapOrNotFound(trxStockAlertDTO);
    }

    /**
     * {@code DELETE  /trx-stock-alerts/:id} : delete the "id" trxStockAlert.
     *
     * @param id the id of the trxStockAlertDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTrxStockAlert(@PathVariable("id") Long id) {
        log.debug("REST request to delete TrxStockAlert : {}", id);
        return trxStockAlertService
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
     * {@code SEARCH  /trx-stock-alerts/_search?query=:query} : search for the trxStockAlert corresponding
     * to the query.
     *
     * @param query the query of the trxStockAlert search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<TrxStockAlertDTO>>> searchTrxStockAlerts(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of TrxStockAlerts for query {}", query);
        return trxStockAlertService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(trxStockAlertService.search(query, pageable)));
    }
}
