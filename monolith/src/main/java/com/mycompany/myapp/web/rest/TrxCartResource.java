package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.TrxCartCriteria;
import com.mycompany.myapp.repository.TrxCartRepository;
import com.mycompany.myapp.service.TrxCartService;
import com.mycompany.myapp.service.dto.TrxCartDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.TrxCart}.
 */
@RestController
@RequestMapping("/api/trx-carts")
public class TrxCartResource {

    private static final Logger log = LoggerFactory.getLogger(TrxCartResource.class);

    private static final String ENTITY_NAME = "trxCart";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TrxCartService trxCartService;

    private final TrxCartRepository trxCartRepository;

    public TrxCartResource(TrxCartService trxCartService, TrxCartRepository trxCartRepository) {
        this.trxCartService = trxCartService;
        this.trxCartRepository = trxCartRepository;
    }

    /**
     * {@code POST  /trx-carts} : Create a new trxCart.
     *
     * @param trxCartDTO the trxCartDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new trxCartDTO, or with status {@code 400 (Bad Request)} if the trxCart has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<TrxCartDTO>> createTrxCart(@Valid @RequestBody TrxCartDTO trxCartDTO) throws URISyntaxException {
        log.debug("REST request to save TrxCart : {}", trxCartDTO);
        if (trxCartDTO.getId() != null) {
            throw new BadRequestAlertException("A new trxCart cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return trxCartService
            .save(trxCartDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/trx-carts/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /trx-carts/:id} : Updates an existing trxCart.
     *
     * @param id the id of the trxCartDTO to save.
     * @param trxCartDTO the trxCartDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trxCartDTO,
     * or with status {@code 400 (Bad Request)} if the trxCartDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the trxCartDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TrxCartDTO>> updateTrxCart(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TrxCartDTO trxCartDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TrxCart : {}, {}", id, trxCartDTO);
        if (trxCartDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trxCartDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trxCartRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return trxCartService
                    .update(trxCartDTO)
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
     * {@code PATCH  /trx-carts/:id} : Partial updates given fields of an existing trxCart, field will ignore if it is null
     *
     * @param id the id of the trxCartDTO to save.
     * @param trxCartDTO the trxCartDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trxCartDTO,
     * or with status {@code 400 (Bad Request)} if the trxCartDTO is not valid,
     * or with status {@code 404 (Not Found)} if the trxCartDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the trxCartDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TrxCartDTO>> partialUpdateTrxCart(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TrxCartDTO trxCartDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TrxCart partially : {}, {}", id, trxCartDTO);
        if (trxCartDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trxCartDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trxCartRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TrxCartDTO> result = trxCartService.partialUpdate(trxCartDTO);

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
     * {@code GET  /trx-carts} : get all the trxCarts.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of trxCarts in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<TrxCartDTO>>> getAllTrxCarts(
        TrxCartCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get TrxCarts by criteria: {}", criteria);
        return trxCartService
            .countByCriteria(criteria)
            .zipWith(trxCartService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /trx-carts/count} : count all the trxCarts.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countTrxCarts(TrxCartCriteria criteria) {
        log.debug("REST request to count TrxCarts by criteria: {}", criteria);
        return trxCartService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /trx-carts/:id} : get the "id" trxCart.
     *
     * @param id the id of the trxCartDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the trxCartDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TrxCartDTO>> getTrxCart(@PathVariable("id") Long id) {
        log.debug("REST request to get TrxCart : {}", id);
        Mono<TrxCartDTO> trxCartDTO = trxCartService.findOne(id);
        return ResponseUtil.wrapOrNotFound(trxCartDTO);
    }

    /**
     * {@code DELETE  /trx-carts/:id} : delete the "id" trxCart.
     *
     * @param id the id of the trxCartDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTrxCart(@PathVariable("id") Long id) {
        log.debug("REST request to delete TrxCart : {}", id);
        return trxCartService
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
     * {@code SEARCH  /trx-carts/_search?query=:query} : search for the trxCart corresponding
     * to the query.
     *
     * @param query the query of the trxCart search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<TrxCartDTO>>> searchTrxCarts(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of TrxCarts for query {}", query);
        return trxCartService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(trxCartService.search(query, pageable)));
    }
}
