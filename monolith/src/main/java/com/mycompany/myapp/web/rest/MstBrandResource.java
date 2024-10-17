package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.MstBrandCriteria;
import com.mycompany.myapp.repository.MstBrandRepository;
import com.mycompany.myapp.service.MstBrandService;
import com.mycompany.myapp.service.dto.MstBrandDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.MstBrand}.
 */
@RestController
@RequestMapping("/api/mst-brands")
public class MstBrandResource {

    private static final Logger log = LoggerFactory.getLogger(MstBrandResource.class);

    private static final String ENTITY_NAME = "mstBrand";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MstBrandService mstBrandService;

    private final MstBrandRepository mstBrandRepository;

    public MstBrandResource(MstBrandService mstBrandService, MstBrandRepository mstBrandRepository) {
        this.mstBrandService = mstBrandService;
        this.mstBrandRepository = mstBrandRepository;
    }

    /**
     * {@code POST  /mst-brands} : Create a new mstBrand.
     *
     * @param mstBrandDTO the mstBrandDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mstBrandDTO, or with status {@code 400 (Bad Request)} if the mstBrand has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MstBrandDTO>> createMstBrand(@Valid @RequestBody MstBrandDTO mstBrandDTO) throws URISyntaxException {
        log.debug("REST request to save MstBrand : {}", mstBrandDTO);
        if (mstBrandDTO.getId() != null) {
            throw new BadRequestAlertException("A new mstBrand cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return mstBrandService
            .save(mstBrandDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/mst-brands/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /mst-brands/:id} : Updates an existing mstBrand.
     *
     * @param id the id of the mstBrandDTO to save.
     * @param mstBrandDTO the mstBrandDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstBrandDTO,
     * or with status {@code 400 (Bad Request)} if the mstBrandDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mstBrandDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MstBrandDTO>> updateMstBrand(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MstBrandDTO mstBrandDTO
    ) throws URISyntaxException {
        log.debug("REST request to update MstBrand : {}, {}", id, mstBrandDTO);
        if (mstBrandDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstBrandDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstBrandRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return mstBrandService
                    .update(mstBrandDTO)
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
     * {@code PATCH  /mst-brands/:id} : Partial updates given fields of an existing mstBrand, field will ignore if it is null
     *
     * @param id the id of the mstBrandDTO to save.
     * @param mstBrandDTO the mstBrandDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstBrandDTO,
     * or with status {@code 400 (Bad Request)} if the mstBrandDTO is not valid,
     * or with status {@code 404 (Not Found)} if the mstBrandDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the mstBrandDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MstBrandDTO>> partialUpdateMstBrand(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MstBrandDTO mstBrandDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update MstBrand partially : {}, {}", id, mstBrandDTO);
        if (mstBrandDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstBrandDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstBrandRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<MstBrandDTO> result = mstBrandService.partialUpdate(mstBrandDTO);

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
     * {@code GET  /mst-brands} : get all the mstBrands.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mstBrands in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<MstBrandDTO>>> getAllMstBrands(
        MstBrandCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get MstBrands by criteria: {}", criteria);
        return mstBrandService
            .countByCriteria(criteria)
            .zipWith(mstBrandService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /mst-brands/count} : count all the mstBrands.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countMstBrands(MstBrandCriteria criteria) {
        log.debug("REST request to count MstBrands by criteria: {}", criteria);
        return mstBrandService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /mst-brands/:id} : get the "id" mstBrand.
     *
     * @param id the id of the mstBrandDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mstBrandDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MstBrandDTO>> getMstBrand(@PathVariable("id") Long id) {
        log.debug("REST request to get MstBrand : {}", id);
        Mono<MstBrandDTO> mstBrandDTO = mstBrandService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mstBrandDTO);
    }

    /**
     * {@code DELETE  /mst-brands/:id} : delete the "id" mstBrand.
     *
     * @param id the id of the mstBrandDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMstBrand(@PathVariable("id") Long id) {
        log.debug("REST request to delete MstBrand : {}", id);
        return mstBrandService
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
     * {@code SEARCH  /mst-brands/_search?query=:query} : search for the mstBrand corresponding
     * to the query.
     *
     * @param query the query of the mstBrand search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<MstBrandDTO>>> searchMstBrands(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of MstBrands for query {}", query);
        return mstBrandService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(mstBrandService.search(query, pageable)));
    }
}
