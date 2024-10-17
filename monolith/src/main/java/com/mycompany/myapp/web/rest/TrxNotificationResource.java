package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.TrxNotificationCriteria;
import com.mycompany.myapp.repository.TrxNotificationRepository;
import com.mycompany.myapp.service.TrxNotificationService;
import com.mycompany.myapp.service.dto.TrxNotificationDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.TrxNotification}.
 */
@RestController
@RequestMapping("/api/trx-notifications")
public class TrxNotificationResource {

    private static final Logger log = LoggerFactory.getLogger(TrxNotificationResource.class);

    private static final String ENTITY_NAME = "trxNotification";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TrxNotificationService trxNotificationService;

    private final TrxNotificationRepository trxNotificationRepository;

    public TrxNotificationResource(TrxNotificationService trxNotificationService, TrxNotificationRepository trxNotificationRepository) {
        this.trxNotificationService = trxNotificationService;
        this.trxNotificationRepository = trxNotificationRepository;
    }

    /**
     * {@code POST  /trx-notifications} : Create a new trxNotification.
     *
     * @param trxNotificationDTO the trxNotificationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new trxNotificationDTO, or with status {@code 400 (Bad Request)} if the trxNotification has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<TrxNotificationDTO>> createTrxNotification(@Valid @RequestBody TrxNotificationDTO trxNotificationDTO)
        throws URISyntaxException {
        log.debug("REST request to save TrxNotification : {}", trxNotificationDTO);
        if (trxNotificationDTO.getId() != null) {
            throw new BadRequestAlertException("A new trxNotification cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return trxNotificationService
            .save(trxNotificationDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/trx-notifications/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /trx-notifications/:id} : Updates an existing trxNotification.
     *
     * @param id the id of the trxNotificationDTO to save.
     * @param trxNotificationDTO the trxNotificationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trxNotificationDTO,
     * or with status {@code 400 (Bad Request)} if the trxNotificationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the trxNotificationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TrxNotificationDTO>> updateTrxNotification(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TrxNotificationDTO trxNotificationDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TrxNotification : {}, {}", id, trxNotificationDTO);
        if (trxNotificationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trxNotificationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trxNotificationRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return trxNotificationService
                    .update(trxNotificationDTO)
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
     * {@code PATCH  /trx-notifications/:id} : Partial updates given fields of an existing trxNotification, field will ignore if it is null
     *
     * @param id the id of the trxNotificationDTO to save.
     * @param trxNotificationDTO the trxNotificationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trxNotificationDTO,
     * or with status {@code 400 (Bad Request)} if the trxNotificationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the trxNotificationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the trxNotificationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TrxNotificationDTO>> partialUpdateTrxNotification(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TrxNotificationDTO trxNotificationDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TrxNotification partially : {}, {}", id, trxNotificationDTO);
        if (trxNotificationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trxNotificationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trxNotificationRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TrxNotificationDTO> result = trxNotificationService.partialUpdate(trxNotificationDTO);

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
     * {@code GET  /trx-notifications} : get all the trxNotifications.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of trxNotifications in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<TrxNotificationDTO>>> getAllTrxNotifications(
        TrxNotificationCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get TrxNotifications by criteria: {}", criteria);
        return trxNotificationService
            .countByCriteria(criteria)
            .zipWith(trxNotificationService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /trx-notifications/count} : count all the trxNotifications.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countTrxNotifications(TrxNotificationCriteria criteria) {
        log.debug("REST request to count TrxNotifications by criteria: {}", criteria);
        return trxNotificationService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /trx-notifications/:id} : get the "id" trxNotification.
     *
     * @param id the id of the trxNotificationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the trxNotificationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TrxNotificationDTO>> getTrxNotification(@PathVariable("id") Long id) {
        log.debug("REST request to get TrxNotification : {}", id);
        Mono<TrxNotificationDTO> trxNotificationDTO = trxNotificationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(trxNotificationDTO);
    }

    /**
     * {@code DELETE  /trx-notifications/:id} : delete the "id" trxNotification.
     *
     * @param id the id of the trxNotificationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTrxNotification(@PathVariable("id") Long id) {
        log.debug("REST request to delete TrxNotification : {}", id);
        return trxNotificationService
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
     * {@code SEARCH  /trx-notifications/_search?query=:query} : search for the trxNotification corresponding
     * to the query.
     *
     * @param query the query of the trxNotification search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<TrxNotificationDTO>>> searchTrxNotifications(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of TrxNotifications for query {}", query);
        return trxNotificationService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(trxNotificationService.search(query, pageable)));
    }
}
