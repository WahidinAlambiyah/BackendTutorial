package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.PostalCodeCriteria;
import com.mycompany.myapp.repository.PostalCodeRepository;
import com.mycompany.myapp.service.PostalCodeService;
import com.mycompany.myapp.service.dto.PostalCodeDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.PostalCode}.
 */
@RestController
@RequestMapping("/api/postal-codes")
public class PostalCodeResource {

    private static final Logger log = LoggerFactory.getLogger(PostalCodeResource.class);

    private static final String ENTITY_NAME = "postalCode";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PostalCodeService postalCodeService;

    private final PostalCodeRepository postalCodeRepository;

    public PostalCodeResource(PostalCodeService postalCodeService, PostalCodeRepository postalCodeRepository) {
        this.postalCodeService = postalCodeService;
        this.postalCodeRepository = postalCodeRepository;
    }

    /**
     * {@code POST  /postal-codes} : Create a new postalCode.
     *
     * @param postalCodeDTO the postalCodeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new postalCodeDTO, or with status {@code 400 (Bad Request)} if the postalCode has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<PostalCodeDTO>> createPostalCode(@Valid @RequestBody PostalCodeDTO postalCodeDTO) throws URISyntaxException {
        log.debug("REST request to save PostalCode : {}", postalCodeDTO);
        if (postalCodeDTO.getId() != null) {
            throw new BadRequestAlertException("A new postalCode cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return postalCodeService
            .save(postalCodeDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/postal-codes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /postal-codes/:id} : Updates an existing postalCode.
     *
     * @param id the id of the postalCodeDTO to save.
     * @param postalCodeDTO the postalCodeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated postalCodeDTO,
     * or with status {@code 400 (Bad Request)} if the postalCodeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the postalCodeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<PostalCodeDTO>> updatePostalCode(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PostalCodeDTO postalCodeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update PostalCode : {}, {}", id, postalCodeDTO);
        if (postalCodeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, postalCodeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return postalCodeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return postalCodeService
                    .update(postalCodeDTO)
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
     * {@code PATCH  /postal-codes/:id} : Partial updates given fields of an existing postalCode, field will ignore if it is null
     *
     * @param id the id of the postalCodeDTO to save.
     * @param postalCodeDTO the postalCodeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated postalCodeDTO,
     * or with status {@code 400 (Bad Request)} if the postalCodeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the postalCodeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the postalCodeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<PostalCodeDTO>> partialUpdatePostalCode(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PostalCodeDTO postalCodeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update PostalCode partially : {}, {}", id, postalCodeDTO);
        if (postalCodeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, postalCodeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return postalCodeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<PostalCodeDTO> result = postalCodeService.partialUpdate(postalCodeDTO);

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
     * {@code GET  /postal-codes} : get all the postalCodes.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of postalCodes in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<PostalCodeDTO>>> getAllPostalCodes(
        PostalCodeCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get PostalCodes by criteria: {}", criteria);
        return postalCodeService
            .countByCriteria(criteria)
            .zipWith(postalCodeService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /postal-codes/count} : count all the postalCodes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countPostalCodes(PostalCodeCriteria criteria) {
        log.debug("REST request to count PostalCodes by criteria: {}", criteria);
        return postalCodeService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /postal-codes/:id} : get the "id" postalCode.
     *
     * @param id the id of the postalCodeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the postalCodeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<PostalCodeDTO>> getPostalCode(@PathVariable("id") Long id) {
        log.debug("REST request to get PostalCode : {}", id);
        Mono<PostalCodeDTO> postalCodeDTO = postalCodeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(postalCodeDTO);
    }

    /**
     * {@code DELETE  /postal-codes/:id} : delete the "id" postalCode.
     *
     * @param id the id of the postalCodeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePostalCode(@PathVariable("id") Long id) {
        log.debug("REST request to delete PostalCode : {}", id);
        return postalCodeService
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
     * {@code SEARCH  /postal-codes/_search?query=:query} : search for the postalCode corresponding
     * to the query.
     *
     * @param query the query of the postalCode search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<PostalCodeDTO>>> searchPostalCodes(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of PostalCodes for query {}", query);
        return postalCodeService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(postalCodeService.search(query, pageable)));
    }
}
