package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.MstCustomerCriteria;
import com.mycompany.myapp.repository.MstCustomerRepository;
import com.mycompany.myapp.service.MstCustomerService;
import com.mycompany.myapp.service.dto.MstCustomerDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.MstCustomer}.
 */
@RestController
@RequestMapping("/api/mst-customers")
public class MstCustomerResource {

    private static final Logger log = LoggerFactory.getLogger(MstCustomerResource.class);

    private static final String ENTITY_NAME = "mstCustomer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MstCustomerService mstCustomerService;

    private final MstCustomerRepository mstCustomerRepository;

    public MstCustomerResource(MstCustomerService mstCustomerService, MstCustomerRepository mstCustomerRepository) {
        this.mstCustomerService = mstCustomerService;
        this.mstCustomerRepository = mstCustomerRepository;
    }

    /**
     * {@code POST  /mst-customers} : Create a new mstCustomer.
     *
     * @param mstCustomerDTO the mstCustomerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mstCustomerDTO, or with status {@code 400 (Bad Request)} if the mstCustomer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MstCustomerDTO>> createMstCustomer(@Valid @RequestBody MstCustomerDTO mstCustomerDTO)
        throws URISyntaxException {
        log.debug("REST request to save MstCustomer : {}", mstCustomerDTO);
        if (mstCustomerDTO.getId() != null) {
            throw new BadRequestAlertException("A new mstCustomer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return mstCustomerService
            .save(mstCustomerDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/mst-customers/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /mst-customers/:id} : Updates an existing mstCustomer.
     *
     * @param id the id of the mstCustomerDTO to save.
     * @param mstCustomerDTO the mstCustomerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstCustomerDTO,
     * or with status {@code 400 (Bad Request)} if the mstCustomerDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mstCustomerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MstCustomerDTO>> updateMstCustomer(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MstCustomerDTO mstCustomerDTO
    ) throws URISyntaxException {
        log.debug("REST request to update MstCustomer : {}, {}", id, mstCustomerDTO);
        if (mstCustomerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstCustomerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstCustomerRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return mstCustomerService
                    .update(mstCustomerDTO)
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
     * {@code PATCH  /mst-customers/:id} : Partial updates given fields of an existing mstCustomer, field will ignore if it is null
     *
     * @param id the id of the mstCustomerDTO to save.
     * @param mstCustomerDTO the mstCustomerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstCustomerDTO,
     * or with status {@code 400 (Bad Request)} if the mstCustomerDTO is not valid,
     * or with status {@code 404 (Not Found)} if the mstCustomerDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the mstCustomerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MstCustomerDTO>> partialUpdateMstCustomer(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MstCustomerDTO mstCustomerDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update MstCustomer partially : {}, {}", id, mstCustomerDTO);
        if (mstCustomerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstCustomerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstCustomerRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<MstCustomerDTO> result = mstCustomerService.partialUpdate(mstCustomerDTO);

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
     * {@code GET  /mst-customers} : get all the mstCustomers.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mstCustomers in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<MstCustomerDTO>>> getAllMstCustomers(
        MstCustomerCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get MstCustomers by criteria: {}", criteria);
        return mstCustomerService
            .countByCriteria(criteria)
            .zipWith(mstCustomerService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /mst-customers/count} : count all the mstCustomers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countMstCustomers(MstCustomerCriteria criteria) {
        log.debug("REST request to count MstCustomers by criteria: {}", criteria);
        return mstCustomerService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /mst-customers/:id} : get the "id" mstCustomer.
     *
     * @param id the id of the mstCustomerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mstCustomerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MstCustomerDTO>> getMstCustomer(@PathVariable("id") Long id) {
        log.debug("REST request to get MstCustomer : {}", id);
        Mono<MstCustomerDTO> mstCustomerDTO = mstCustomerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mstCustomerDTO);
    }

    /**
     * {@code DELETE  /mst-customers/:id} : delete the "id" mstCustomer.
     *
     * @param id the id of the mstCustomerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMstCustomer(@PathVariable("id") Long id) {
        log.debug("REST request to delete MstCustomer : {}", id);
        return mstCustomerService
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
     * {@code SEARCH  /mst-customers/_search?query=:query} : search for the mstCustomer corresponding
     * to the query.
     *
     * @param query the query of the mstCustomer search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<MstCustomerDTO>>> searchMstCustomers(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of MstCustomers for query {}", query);
        return mstCustomerService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(mstCustomerService.search(query, pageable)));
    }
}
