package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.TrxCouponCriteria;
import com.mycompany.myapp.repository.TrxCouponRepository;
import com.mycompany.myapp.service.TrxCouponService;
import com.mycompany.myapp.service.dto.TrxCouponDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.TrxCoupon}.
 */
@RestController
@RequestMapping("/api/trx-coupons")
public class TrxCouponResource {

    private static final Logger log = LoggerFactory.getLogger(TrxCouponResource.class);

    private static final String ENTITY_NAME = "trxCoupon";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TrxCouponService trxCouponService;

    private final TrxCouponRepository trxCouponRepository;

    public TrxCouponResource(TrxCouponService trxCouponService, TrxCouponRepository trxCouponRepository) {
        this.trxCouponService = trxCouponService;
        this.trxCouponRepository = trxCouponRepository;
    }

    /**
     * {@code POST  /trx-coupons} : Create a new trxCoupon.
     *
     * @param trxCouponDTO the trxCouponDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new trxCouponDTO, or with status {@code 400 (Bad Request)} if the trxCoupon has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<TrxCouponDTO>> createTrxCoupon(@Valid @RequestBody TrxCouponDTO trxCouponDTO) throws URISyntaxException {
        log.debug("REST request to save TrxCoupon : {}", trxCouponDTO);
        if (trxCouponDTO.getId() != null) {
            throw new BadRequestAlertException("A new trxCoupon cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return trxCouponService
            .save(trxCouponDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/trx-coupons/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /trx-coupons/:id} : Updates an existing trxCoupon.
     *
     * @param id the id of the trxCouponDTO to save.
     * @param trxCouponDTO the trxCouponDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trxCouponDTO,
     * or with status {@code 400 (Bad Request)} if the trxCouponDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the trxCouponDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TrxCouponDTO>> updateTrxCoupon(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TrxCouponDTO trxCouponDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TrxCoupon : {}, {}", id, trxCouponDTO);
        if (trxCouponDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trxCouponDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trxCouponRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return trxCouponService
                    .update(trxCouponDTO)
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
     * {@code PATCH  /trx-coupons/:id} : Partial updates given fields of an existing trxCoupon, field will ignore if it is null
     *
     * @param id the id of the trxCouponDTO to save.
     * @param trxCouponDTO the trxCouponDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trxCouponDTO,
     * or with status {@code 400 (Bad Request)} if the trxCouponDTO is not valid,
     * or with status {@code 404 (Not Found)} if the trxCouponDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the trxCouponDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TrxCouponDTO>> partialUpdateTrxCoupon(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TrxCouponDTO trxCouponDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TrxCoupon partially : {}, {}", id, trxCouponDTO);
        if (trxCouponDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trxCouponDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trxCouponRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TrxCouponDTO> result = trxCouponService.partialUpdate(trxCouponDTO);

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
     * {@code GET  /trx-coupons} : get all the trxCoupons.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of trxCoupons in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<TrxCouponDTO>>> getAllTrxCoupons(
        TrxCouponCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get TrxCoupons by criteria: {}", criteria);
        return trxCouponService
            .countByCriteria(criteria)
            .zipWith(trxCouponService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /trx-coupons/count} : count all the trxCoupons.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countTrxCoupons(TrxCouponCriteria criteria) {
        log.debug("REST request to count TrxCoupons by criteria: {}", criteria);
        return trxCouponService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /trx-coupons/:id} : get the "id" trxCoupon.
     *
     * @param id the id of the trxCouponDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the trxCouponDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TrxCouponDTO>> getTrxCoupon(@PathVariable("id") Long id) {
        log.debug("REST request to get TrxCoupon : {}", id);
        Mono<TrxCouponDTO> trxCouponDTO = trxCouponService.findOne(id);
        return ResponseUtil.wrapOrNotFound(trxCouponDTO);
    }

    /**
     * {@code DELETE  /trx-coupons/:id} : delete the "id" trxCoupon.
     *
     * @param id the id of the trxCouponDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTrxCoupon(@PathVariable("id") Long id) {
        log.debug("REST request to delete TrxCoupon : {}", id);
        return trxCouponService
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
     * {@code SEARCH  /trx-coupons/_search?query=:query} : search for the trxCoupon corresponding
     * to the query.
     *
     * @param query the query of the trxCoupon search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<TrxCouponDTO>>> searchTrxCoupons(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of TrxCoupons for query {}", query);
        return trxCouponService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(trxCouponService.search(query, pageable)));
    }
}
