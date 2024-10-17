package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.MstProductCriteria;
import com.mycompany.myapp.repository.MstProductRepository;
import com.mycompany.myapp.service.MstProductService;
import com.mycompany.myapp.service.dto.MstProductDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.MstProduct}.
 */
@RestController
@RequestMapping("/api/mst-products")
public class MstProductResource {

    private static final Logger log = LoggerFactory.getLogger(MstProductResource.class);

    private static final String ENTITY_NAME = "mstProduct";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MstProductService mstProductService;

    private final MstProductRepository mstProductRepository;

    public MstProductResource(MstProductService mstProductService, MstProductRepository mstProductRepository) {
        this.mstProductService = mstProductService;
        this.mstProductRepository = mstProductRepository;
    }

    /**
     * {@code POST  /mst-products} : Create a new mstProduct.
     *
     * @param mstProductDTO the mstProductDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mstProductDTO, or with status {@code 400 (Bad Request)} if the mstProduct has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MstProductDTO>> createMstProduct(@Valid @RequestBody MstProductDTO mstProductDTO) throws URISyntaxException {
        log.debug("REST request to save MstProduct : {}", mstProductDTO);
        if (mstProductDTO.getId() != null) {
            throw new BadRequestAlertException("A new mstProduct cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return mstProductService
            .save(mstProductDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/mst-products/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /mst-products/:id} : Updates an existing mstProduct.
     *
     * @param id the id of the mstProductDTO to save.
     * @param mstProductDTO the mstProductDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstProductDTO,
     * or with status {@code 400 (Bad Request)} if the mstProductDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mstProductDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MstProductDTO>> updateMstProduct(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MstProductDTO mstProductDTO
    ) throws URISyntaxException {
        log.debug("REST request to update MstProduct : {}, {}", id, mstProductDTO);
        if (mstProductDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstProductDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstProductRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return mstProductService
                    .update(mstProductDTO)
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
     * {@code PATCH  /mst-products/:id} : Partial updates given fields of an existing mstProduct, field will ignore if it is null
     *
     * @param id the id of the mstProductDTO to save.
     * @param mstProductDTO the mstProductDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstProductDTO,
     * or with status {@code 400 (Bad Request)} if the mstProductDTO is not valid,
     * or with status {@code 404 (Not Found)} if the mstProductDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the mstProductDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MstProductDTO>> partialUpdateMstProduct(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MstProductDTO mstProductDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update MstProduct partially : {}, {}", id, mstProductDTO);
        if (mstProductDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstProductDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstProductRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<MstProductDTO> result = mstProductService.partialUpdate(mstProductDTO);

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
     * {@code GET  /mst-products} : get all the mstProducts.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mstProducts in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<MstProductDTO>>> getAllMstProducts(
        MstProductCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get MstProducts by criteria: {}", criteria);
        return mstProductService
            .countByCriteria(criteria)
            .zipWith(mstProductService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /mst-products/count} : count all the mstProducts.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countMstProducts(MstProductCriteria criteria) {
        log.debug("REST request to count MstProducts by criteria: {}", criteria);
        return mstProductService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /mst-products/:id} : get the "id" mstProduct.
     *
     * @param id the id of the mstProductDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mstProductDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MstProductDTO>> getMstProduct(@PathVariable("id") Long id) {
        log.debug("REST request to get MstProduct : {}", id);
        Mono<MstProductDTO> mstProductDTO = mstProductService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mstProductDTO);
    }

    /**
     * {@code DELETE  /mst-products/:id} : delete the "id" mstProduct.
     *
     * @param id the id of the mstProductDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMstProduct(@PathVariable("id") Long id) {
        log.debug("REST request to delete MstProduct : {}", id);
        return mstProductService
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
     * {@code SEARCH  /mst-products/_search?query=:query} : search for the mstProduct corresponding
     * to the query.
     *
     * @param query the query of the mstProduct search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<MstProductDTO>>> searchMstProducts(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of MstProducts for query {}", query);
        return mstProductService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(mstProductService.search(query, pageable)));
    }
}
