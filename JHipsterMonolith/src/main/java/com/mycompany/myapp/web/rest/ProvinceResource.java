package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.ProvinceCriteria;
import com.mycompany.myapp.repository.ProvinceRepository;
import com.mycompany.myapp.service.ProvinceService;
import com.mycompany.myapp.service.dto.ProvinceDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Province}.
 */
@RestController
@RequestMapping("/api/provinces")
public class ProvinceResource {

    private static final Logger log = LoggerFactory.getLogger(ProvinceResource.class);

    private static final String ENTITY_NAME = "province";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProvinceService provinceService;

    private final ProvinceRepository provinceRepository;

    public ProvinceResource(ProvinceService provinceService, ProvinceRepository provinceRepository) {
        this.provinceService = provinceService;
        this.provinceRepository = provinceRepository;
    }

    /**
     * {@code POST  /provinces} : Create a new province.
     *
     * @param provinceDTO the provinceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new provinceDTO, or with status {@code 400 (Bad Request)} if the province has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<ProvinceDTO>> createProvince(@Valid @RequestBody ProvinceDTO provinceDTO) throws URISyntaxException {
        log.debug("REST request to save Province : {}", provinceDTO);
        if (provinceDTO.getId() != null) {
            throw new BadRequestAlertException("A new province cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return provinceService
            .save(provinceDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/provinces/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /provinces/:id} : Updates an existing province.
     *
     * @param id the id of the provinceDTO to save.
     * @param provinceDTO the provinceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated provinceDTO,
     * or with status {@code 400 (Bad Request)} if the provinceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the provinceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ProvinceDTO>> updateProvince(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProvinceDTO provinceDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Province : {}, {}", id, provinceDTO);
        if (provinceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, provinceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return provinceRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return provinceService
                    .update(provinceDTO)
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
     * {@code PATCH  /provinces/:id} : Partial updates given fields of an existing province, field will ignore if it is null
     *
     * @param id the id of the provinceDTO to save.
     * @param provinceDTO the provinceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated provinceDTO,
     * or with status {@code 400 (Bad Request)} if the provinceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the provinceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the provinceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ProvinceDTO>> partialUpdateProvince(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProvinceDTO provinceDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Province partially : {}, {}", id, provinceDTO);
        if (provinceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, provinceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return provinceRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ProvinceDTO> result = provinceService.partialUpdate(provinceDTO);

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
     * {@code GET  /provinces} : get all the provinces.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of provinces in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<ProvinceDTO>>> getAllProvinces(
        ProvinceCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get Provinces by criteria: {}", criteria);
        return provinceService
            .countByCriteria(criteria)
            .zipWith(provinceService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /provinces/count} : count all the provinces.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countProvinces(ProvinceCriteria criteria) {
        log.debug("REST request to count Provinces by criteria: {}", criteria);
        return provinceService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /provinces/:id} : get the "id" province.
     *
     * @param id the id of the provinceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the provinceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProvinceDTO>> getProvince(@PathVariable("id") Long id) {
        log.debug("REST request to get Province : {}", id);
        Mono<ProvinceDTO> provinceDTO = provinceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(provinceDTO);
    }

    /**
     * {@code DELETE  /provinces/:id} : delete the "id" province.
     *
     * @param id the id of the provinceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProvince(@PathVariable("id") Long id) {
        log.debug("REST request to delete Province : {}", id);
        return provinceService
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
     * {@code SEARCH  /provinces/_search?query=:query} : search for the province corresponding
     * to the query.
     *
     * @param query the query of the province search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<ProvinceDTO>>> searchProvinces(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of Provinces for query {}", query);
        return provinceService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(provinceService.search(query, pageable)));
    }
}
