package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.SubDistrict;
import com.mycompany.myapp.repository.SubDistrictRepository;
import com.mycompany.myapp.service.SubDistrictService;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.SubDistrict}.
 */
@RestController
@RequestMapping("/api/sub-districts")
public class SubDistrictResource {

    private static final Logger log = LoggerFactory.getLogger(SubDistrictResource.class);

    private static final String ENTITY_NAME = "subDistrict";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SubDistrictService subDistrictService;

    private final SubDistrictRepository subDistrictRepository;

    public SubDistrictResource(SubDistrictService subDistrictService, SubDistrictRepository subDistrictRepository) {
        this.subDistrictService = subDistrictService;
        this.subDistrictRepository = subDistrictRepository;
    }

    /**
     * {@code POST  /sub-districts} : Create a new subDistrict.
     *
     * @param subDistrict the subDistrict to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new subDistrict, or with status {@code 400 (Bad Request)} if the subDistrict has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<SubDistrict>> createSubDistrict(@Valid @RequestBody SubDistrict subDistrict) throws URISyntaxException {
        log.debug("REST request to save SubDistrict : {}", subDistrict);
        if (subDistrict.getId() != null) {
            throw new BadRequestAlertException("A new subDistrict cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return subDistrictService
            .save(subDistrict)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/sub-districts/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /sub-districts/:id} : Updates an existing subDistrict.
     *
     * @param id the id of the subDistrict to save.
     * @param subDistrict the subDistrict to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subDistrict,
     * or with status {@code 400 (Bad Request)} if the subDistrict is not valid,
     * or with status {@code 500 (Internal Server Error)} if the subDistrict couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<SubDistrict>> updateSubDistrict(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SubDistrict subDistrict
    ) throws URISyntaxException {
        log.debug("REST request to update SubDistrict : {}, {}", id, subDistrict);
        if (subDistrict.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, subDistrict.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return subDistrictRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return subDistrictService
                    .update(subDistrict)
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
     * {@code PATCH  /sub-districts/:id} : Partial updates given fields of an existing subDistrict, field will ignore if it is null
     *
     * @param id the id of the subDistrict to save.
     * @param subDistrict the subDistrict to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subDistrict,
     * or with status {@code 400 (Bad Request)} if the subDistrict is not valid,
     * or with status {@code 404 (Not Found)} if the subDistrict is not found,
     * or with status {@code 500 (Internal Server Error)} if the subDistrict couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<SubDistrict>> partialUpdateSubDistrict(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SubDistrict subDistrict
    ) throws URISyntaxException {
        log.debug("REST request to partial update SubDistrict partially : {}, {}", id, subDistrict);
        if (subDistrict.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, subDistrict.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return subDistrictRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<SubDistrict> result = subDistrictService.partialUpdate(subDistrict);

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
     * {@code GET  /sub-districts} : get all the subDistricts.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of subDistricts in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<SubDistrict>>> getAllSubDistricts(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of SubDistricts");
        return subDistrictService
            .countAll()
            .zipWith(subDistrictService.findAll(pageable).collectList())
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
     * {@code GET  /sub-districts/:id} : get the "id" subDistrict.
     *
     * @param id the id of the subDistrict to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the subDistrict, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<SubDistrict>> getSubDistrict(@PathVariable("id") Long id) {
        log.debug("REST request to get SubDistrict : {}", id);
        Mono<SubDistrict> subDistrict = subDistrictService.findOne(id);
        return ResponseUtil.wrapOrNotFound(subDistrict);
    }

    /**
     * {@code DELETE  /sub-districts/:id} : delete the "id" subDistrict.
     *
     * @param id the id of the subDistrict to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteSubDistrict(@PathVariable("id") Long id) {
        log.debug("REST request to delete SubDistrict : {}", id);
        return subDistrictService
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
     * {@code SEARCH  /sub-districts/_search?query=:query} : search for the subDistrict corresponding
     * to the query.
     *
     * @param query the query of the subDistrict search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<SubDistrict>>> searchSubDistricts(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of SubDistricts for query {}", query);
        return subDistrictService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(subDistrictService.search(query, pageable)));
    }
}
