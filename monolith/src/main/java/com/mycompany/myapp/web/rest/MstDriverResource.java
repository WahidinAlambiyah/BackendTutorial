package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.MstDriverCriteria;
import com.mycompany.myapp.repository.MstDriverRepository;
import com.mycompany.myapp.service.MstDriverService;
import com.mycompany.myapp.service.dto.MstDriverDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.MstDriver}.
 */
@RestController
@RequestMapping("/api/mst-drivers")
public class MstDriverResource {

    private static final Logger log = LoggerFactory.getLogger(MstDriverResource.class);

    private static final String ENTITY_NAME = "mstDriver";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MstDriverService mstDriverService;

    private final MstDriverRepository mstDriverRepository;

    public MstDriverResource(MstDriverService mstDriverService, MstDriverRepository mstDriverRepository) {
        this.mstDriverService = mstDriverService;
        this.mstDriverRepository = mstDriverRepository;
    }

    /**
     * {@code POST  /mst-drivers} : Create a new mstDriver.
     *
     * @param mstDriverDTO the mstDriverDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mstDriverDTO, or with status {@code 400 (Bad Request)} if the mstDriver has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MstDriverDTO>> createMstDriver(@Valid @RequestBody MstDriverDTO mstDriverDTO) throws URISyntaxException {
        log.debug("REST request to save MstDriver : {}", mstDriverDTO);
        if (mstDriverDTO.getId() != null) {
            throw new BadRequestAlertException("A new mstDriver cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return mstDriverService
            .save(mstDriverDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/mst-drivers/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /mst-drivers/:id} : Updates an existing mstDriver.
     *
     * @param id the id of the mstDriverDTO to save.
     * @param mstDriverDTO the mstDriverDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstDriverDTO,
     * or with status {@code 400 (Bad Request)} if the mstDriverDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mstDriverDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MstDriverDTO>> updateMstDriver(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MstDriverDTO mstDriverDTO
    ) throws URISyntaxException {
        log.debug("REST request to update MstDriver : {}, {}", id, mstDriverDTO);
        if (mstDriverDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstDriverDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstDriverRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return mstDriverService
                    .update(mstDriverDTO)
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
     * {@code PATCH  /mst-drivers/:id} : Partial updates given fields of an existing mstDriver, field will ignore if it is null
     *
     * @param id the id of the mstDriverDTO to save.
     * @param mstDriverDTO the mstDriverDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstDriverDTO,
     * or with status {@code 400 (Bad Request)} if the mstDriverDTO is not valid,
     * or with status {@code 404 (Not Found)} if the mstDriverDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the mstDriverDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MstDriverDTO>> partialUpdateMstDriver(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MstDriverDTO mstDriverDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update MstDriver partially : {}, {}", id, mstDriverDTO);
        if (mstDriverDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstDriverDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstDriverRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<MstDriverDTO> result = mstDriverService.partialUpdate(mstDriverDTO);

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
     * {@code GET  /mst-drivers} : get all the mstDrivers.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mstDrivers in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<MstDriverDTO>>> getAllMstDrivers(
        MstDriverCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get MstDrivers by criteria: {}", criteria);
        return mstDriverService
            .countByCriteria(criteria)
            .zipWith(mstDriverService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /mst-drivers/count} : count all the mstDrivers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countMstDrivers(MstDriverCriteria criteria) {
        log.debug("REST request to count MstDrivers by criteria: {}", criteria);
        return mstDriverService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /mst-drivers/:id} : get the "id" mstDriver.
     *
     * @param id the id of the mstDriverDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mstDriverDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MstDriverDTO>> getMstDriver(@PathVariable("id") Long id) {
        log.debug("REST request to get MstDriver : {}", id);
        Mono<MstDriverDTO> mstDriverDTO = mstDriverService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mstDriverDTO);
    }

    /**
     * {@code DELETE  /mst-drivers/:id} : delete the "id" mstDriver.
     *
     * @param id the id of the mstDriverDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMstDriver(@PathVariable("id") Long id) {
        log.debug("REST request to delete MstDriver : {}", id);
        return mstDriverService
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
     * {@code SEARCH  /mst-drivers/_search?query=:query} : search for the mstDriver corresponding
     * to the query.
     *
     * @param query the query of the mstDriver search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<MstDriverDTO>>> searchMstDrivers(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of MstDrivers for query {}", query);
        return mstDriverService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(mstDriverService.search(query, pageable)));
    }
}
