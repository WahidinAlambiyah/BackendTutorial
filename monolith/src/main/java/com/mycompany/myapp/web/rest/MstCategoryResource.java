package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.MstCategoryCriteria;
import com.mycompany.myapp.repository.MstCategoryRepository;
import com.mycompany.myapp.service.MstCategoryService;
import com.mycompany.myapp.service.dto.MstCategoryDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.MstCategory}.
 */
@RestController
@RequestMapping("/api/mst-categories")
public class MstCategoryResource {

    private static final Logger log = LoggerFactory.getLogger(MstCategoryResource.class);

    private static final String ENTITY_NAME = "mstCategory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MstCategoryService mstCategoryService;

    private final MstCategoryRepository mstCategoryRepository;

    public MstCategoryResource(MstCategoryService mstCategoryService, MstCategoryRepository mstCategoryRepository) {
        this.mstCategoryService = mstCategoryService;
        this.mstCategoryRepository = mstCategoryRepository;
    }

    /**
     * {@code POST  /mst-categories} : Create a new mstCategory.
     *
     * @param mstCategoryDTO the mstCategoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mstCategoryDTO, or with status {@code 400 (Bad Request)} if the mstCategory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MstCategoryDTO>> createMstCategory(@Valid @RequestBody MstCategoryDTO mstCategoryDTO)
        throws URISyntaxException {
        log.debug("REST request to save MstCategory : {}", mstCategoryDTO);
        if (mstCategoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new mstCategory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return mstCategoryService
            .save(mstCategoryDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/mst-categories/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /mst-categories/:id} : Updates an existing mstCategory.
     *
     * @param id the id of the mstCategoryDTO to save.
     * @param mstCategoryDTO the mstCategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstCategoryDTO,
     * or with status {@code 400 (Bad Request)} if the mstCategoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mstCategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MstCategoryDTO>> updateMstCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MstCategoryDTO mstCategoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to update MstCategory : {}, {}", id, mstCategoryDTO);
        if (mstCategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstCategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstCategoryRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return mstCategoryService
                    .update(mstCategoryDTO)
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
     * {@code PATCH  /mst-categories/:id} : Partial updates given fields of an existing mstCategory, field will ignore if it is null
     *
     * @param id the id of the mstCategoryDTO to save.
     * @param mstCategoryDTO the mstCategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstCategoryDTO,
     * or with status {@code 400 (Bad Request)} if the mstCategoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the mstCategoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the mstCategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MstCategoryDTO>> partialUpdateMstCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MstCategoryDTO mstCategoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update MstCategory partially : {}, {}", id, mstCategoryDTO);
        if (mstCategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstCategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstCategoryRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<MstCategoryDTO> result = mstCategoryService.partialUpdate(mstCategoryDTO);

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
     * {@code GET  /mst-categories} : get all the mstCategories.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mstCategories in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<MstCategoryDTO>>> getAllMstCategories(
        MstCategoryCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get MstCategories by criteria: {}", criteria);
        return mstCategoryService
            .countByCriteria(criteria)
            .zipWith(mstCategoryService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /mst-categories/count} : count all the mstCategories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countMstCategories(MstCategoryCriteria criteria) {
        log.debug("REST request to count MstCategories by criteria: {}", criteria);
        return mstCategoryService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /mst-categories/:id} : get the "id" mstCategory.
     *
     * @param id the id of the mstCategoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mstCategoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MstCategoryDTO>> getMstCategory(@PathVariable("id") Long id) {
        log.debug("REST request to get MstCategory : {}", id);
        Mono<MstCategoryDTO> mstCategoryDTO = mstCategoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mstCategoryDTO);
    }

    /**
     * {@code DELETE  /mst-categories/:id} : delete the "id" mstCategory.
     *
     * @param id the id of the mstCategoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMstCategory(@PathVariable("id") Long id) {
        log.debug("REST request to delete MstCategory : {}", id);
        return mstCategoryService
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
     * {@code SEARCH  /mst-categories/_search?query=:query} : search for the mstCategory corresponding
     * to the query.
     *
     * @param query the query of the mstCategory search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<MstCategoryDTO>>> searchMstCategories(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of MstCategories for query {}", query);
        return mstCategoryService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(mstCategoryService.search(query, pageable)));
    }
}
