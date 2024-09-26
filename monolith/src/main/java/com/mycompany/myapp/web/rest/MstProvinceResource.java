package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.MstProvinceCriteria;
import com.mycompany.myapp.repository.MstProvinceRepository;
import com.mycompany.myapp.service.MstProvinceService;
import com.mycompany.myapp.service.dto.MstProvinceDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.MstProvince}.
 */
@RestController
@RequestMapping("/api/mst-provinces")
public class MstProvinceResource {

    private static final Logger log = LoggerFactory.getLogger(MstProvinceResource.class);

    private static final String ENTITY_NAME = "mstProvince";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MstProvinceService mstProvinceService;

    private final MstProvinceRepository mstProvinceRepository;

    public MstProvinceResource(MstProvinceService mstProvinceService, MstProvinceRepository mstProvinceRepository) {
        this.mstProvinceService = mstProvinceService;
        this.mstProvinceRepository = mstProvinceRepository;
    }

    /**
     * {@code POST  /mst-provinces} : Create a new mstProvince.
     *
     * @param mstProvinceDTO the mstProvinceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mstProvinceDTO, or with status {@code 400 (Bad Request)} if the mstProvince has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MstProvinceDTO>> createMstProvince(@Valid @RequestBody MstProvinceDTO mstProvinceDTO)
        throws URISyntaxException {
        log.debug("REST request to save MstProvince : {}", mstProvinceDTO);
        if (mstProvinceDTO.getId() != null) {
            throw new BadRequestAlertException("A new mstProvince cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return mstProvinceService
            .save(mstProvinceDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/mst-provinces/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /mst-provinces/:id} : Updates an existing mstProvince.
     *
     * @param id the id of the mstProvinceDTO to save.
     * @param mstProvinceDTO the mstProvinceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstProvinceDTO,
     * or with status {@code 400 (Bad Request)} if the mstProvinceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mstProvinceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MstProvinceDTO>> updateMstProvince(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MstProvinceDTO mstProvinceDTO
    ) throws URISyntaxException {
        log.debug("REST request to update MstProvince : {}, {}", id, mstProvinceDTO);
        if (mstProvinceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstProvinceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstProvinceRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return mstProvinceService
                    .update(mstProvinceDTO)
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
     * {@code PATCH  /mst-provinces/:id} : Partial updates given fields of an existing mstProvince, field will ignore if it is null
     *
     * @param id the id of the mstProvinceDTO to save.
     * @param mstProvinceDTO the mstProvinceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstProvinceDTO,
     * or with status {@code 400 (Bad Request)} if the mstProvinceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the mstProvinceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the mstProvinceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MstProvinceDTO>> partialUpdateMstProvince(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MstProvinceDTO mstProvinceDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update MstProvince partially : {}, {}", id, mstProvinceDTO);
        if (mstProvinceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstProvinceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstProvinceRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<MstProvinceDTO> result = mstProvinceService.partialUpdate(mstProvinceDTO);

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
     * {@code GET  /mst-provinces} : get all the mstProvinces.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mstProvinces in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<MstProvinceDTO>>> getAllMstProvinces(
        MstProvinceCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get MstProvinces by criteria: {}", criteria);
        return mstProvinceService
            .countByCriteria(criteria)
            .zipWith(mstProvinceService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /mst-provinces/count} : count all the mstProvinces.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countMstProvinces(MstProvinceCriteria criteria) {
        log.debug("REST request to count MstProvinces by criteria: {}", criteria);
        return mstProvinceService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /mst-provinces/:id} : get the "id" mstProvince.
     *
     * @param id the id of the mstProvinceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mstProvinceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MstProvinceDTO>> getMstProvince(@PathVariable("id") Long id) {
        log.debug("REST request to get MstProvince : {}", id);
        Mono<MstProvinceDTO> mstProvinceDTO = mstProvinceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mstProvinceDTO);
    }

    /**
     * {@code DELETE  /mst-provinces/:id} : delete the "id" mstProvince.
     *
     * @param id the id of the mstProvinceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMstProvince(@PathVariable("id") Long id) {
        log.debug("REST request to delete MstProvince : {}", id);
        return mstProvinceService
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
     * {@code SEARCH  /mst-provinces/_search?query=:query} : search for the mstProvince corresponding
     * to the query.
     *
     * @param query the query of the mstProvince search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<MstProvinceDTO>>> searchMstProvinces(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of MstProvinces for query {}", query);
        return mstProvinceService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(mstProvinceService.search(query, pageable)));
    }
}
