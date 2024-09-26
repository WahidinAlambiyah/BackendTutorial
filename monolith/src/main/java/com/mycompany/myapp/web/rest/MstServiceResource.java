package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.MstServiceCriteria;
import com.mycompany.myapp.repository.MstServiceRepository;
import com.mycompany.myapp.service.MstServiceService;
import com.mycompany.myapp.service.dto.MstServiceDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import com.mycompany.myapp.web.rest.errors.ElasticsearchExceptionMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.MstService}.
 */
@RestController
@RequestMapping("/api/mst-services")
public class MstServiceResource {

    private static final Logger log = LoggerFactory.getLogger(MstServiceResource.class);

    private static final String ENTITY_NAME = "mstService";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MstServiceService mstServiceService;

    private final MstServiceRepository mstServiceRepository;

    public MstServiceResource(MstServiceService mstServiceService, MstServiceRepository mstServiceRepository) {
        this.mstServiceService = mstServiceService;
        this.mstServiceRepository = mstServiceRepository;
    }

    /**
     * {@code POST  /mst-services} : Create a new mstService.
     *
     * @param mstServiceDTO the mstServiceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mstServiceDTO, or with status {@code 400 (Bad Request)} if the mstService has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MstServiceDTO>> createMstService(@Valid @RequestBody MstServiceDTO mstServiceDTO) throws URISyntaxException {
        log.debug("REST request to save MstService : {}", mstServiceDTO);
        if (mstServiceDTO.getId() != null) {
            throw new BadRequestAlertException("A new mstService cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return mstServiceService
            .save(mstServiceDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/mst-services/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /mst-services/:id} : Updates an existing mstService.
     *
     * @param id the id of the mstServiceDTO to save.
     * @param mstServiceDTO the mstServiceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstServiceDTO,
     * or with status {@code 400 (Bad Request)} if the mstServiceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mstServiceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MstServiceDTO>> updateMstService(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MstServiceDTO mstServiceDTO
    ) throws URISyntaxException {
        log.debug("REST request to update MstService : {}, {}", id, mstServiceDTO);
        if (mstServiceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstServiceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstServiceRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return mstServiceService
                    .update(mstServiceDTO)
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
     * {@code PATCH  /mst-services/:id} : Partial updates given fields of an existing mstService, field will ignore if it is null
     *
     * @param id the id of the mstServiceDTO to save.
     * @param mstServiceDTO the mstServiceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstServiceDTO,
     * or with status {@code 400 (Bad Request)} if the mstServiceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the mstServiceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the mstServiceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MstServiceDTO>> partialUpdateMstService(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MstServiceDTO mstServiceDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update MstService partially : {}, {}", id, mstServiceDTO);
        if (mstServiceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstServiceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstServiceRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<MstServiceDTO> result = mstServiceService.partialUpdate(mstServiceDTO);

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
     * {@code GET  /mst-services} : get all the mstServices.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mstServices in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<MstServiceDTO> getAllMstServices(MstServiceCriteria criteria) {
        log.debug("REST request to get MstServices by criteria: {}", criteria);
        return mstServiceService.findByCriteria(criteria);
    }

    /**
     * {@code GET  /mst-services/count} : count all the mstServices.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countMstServices(MstServiceCriteria criteria) {
        log.debug("REST request to count MstServices by criteria: {}", criteria);
        return mstServiceService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /mst-services/:id} : get the "id" mstService.
     *
     * @param id the id of the mstServiceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mstServiceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MstServiceDTO>> getMstService(@PathVariable("id") Long id) {
        log.debug("REST request to get MstService : {}", id);
        Mono<MstServiceDTO> mstServiceDTO = mstServiceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mstServiceDTO);
    }

    /**
     * {@code DELETE  /mst-services/:id} : delete the "id" mstService.
     *
     * @param id the id of the mstServiceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMstService(@PathVariable("id") Long id) {
        log.debug("REST request to delete MstService : {}", id);
        return mstServiceService
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
     * {@code SEARCH  /mst-services/_search?query=:query} : search for the mstService corresponding
     * to the query.
     *
     * @param query the query of the mstService search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<List<MstServiceDTO>> searchMstServices(@RequestParam("query") String query) {
        log.debug("REST request to search MstServices for query {}", query);
        try {
            return mstServiceService.search(query).collectList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
