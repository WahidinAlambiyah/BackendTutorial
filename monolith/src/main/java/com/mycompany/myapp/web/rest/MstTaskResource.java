package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.criteria.MstTaskCriteria;
import com.mycompany.myapp.repository.MstTaskRepository;
import com.mycompany.myapp.service.MstTaskService;
import com.mycompany.myapp.service.dto.MstTaskDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import com.mycompany.myapp.web.rest.errors.ElasticsearchExceptionMapper;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.MstTask}.
 */
@RestController
@RequestMapping("/api/mst-tasks")
public class MstTaskResource {

    private static final Logger log = LoggerFactory.getLogger(MstTaskResource.class);

    private static final String ENTITY_NAME = "mstTask";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MstTaskService mstTaskService;

    private final MstTaskRepository mstTaskRepository;

    public MstTaskResource(MstTaskService mstTaskService, MstTaskRepository mstTaskRepository) {
        this.mstTaskService = mstTaskService;
        this.mstTaskRepository = mstTaskRepository;
    }

    /**
     * {@code POST  /mst-tasks} : Create a new mstTask.
     *
     * @param mstTaskDTO the mstTaskDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mstTaskDTO, or with status {@code 400 (Bad Request)} if the mstTask has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MstTaskDTO>> createMstTask(@RequestBody MstTaskDTO mstTaskDTO) throws URISyntaxException {
        log.debug("REST request to save MstTask : {}", mstTaskDTO);
        if (mstTaskDTO.getId() != null) {
            throw new BadRequestAlertException("A new mstTask cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return mstTaskService
            .save(mstTaskDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/mst-tasks/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /mst-tasks/:id} : Updates an existing mstTask.
     *
     * @param id the id of the mstTaskDTO to save.
     * @param mstTaskDTO the mstTaskDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstTaskDTO,
     * or with status {@code 400 (Bad Request)} if the mstTaskDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mstTaskDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MstTaskDTO>> updateMstTask(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MstTaskDTO mstTaskDTO
    ) throws URISyntaxException {
        log.debug("REST request to update MstTask : {}, {}", id, mstTaskDTO);
        if (mstTaskDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstTaskDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstTaskRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return mstTaskService
                    .update(mstTaskDTO)
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
     * {@code PATCH  /mst-tasks/:id} : Partial updates given fields of an existing mstTask, field will ignore if it is null
     *
     * @param id the id of the mstTaskDTO to save.
     * @param mstTaskDTO the mstTaskDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mstTaskDTO,
     * or with status {@code 400 (Bad Request)} if the mstTaskDTO is not valid,
     * or with status {@code 404 (Not Found)} if the mstTaskDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the mstTaskDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MstTaskDTO>> partialUpdateMstTask(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MstTaskDTO mstTaskDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update MstTask partially : {}, {}", id, mstTaskDTO);
        if (mstTaskDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mstTaskDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mstTaskRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<MstTaskDTO> result = mstTaskService.partialUpdate(mstTaskDTO);

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
     * {@code GET  /mst-tasks} : get all the mstTasks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mstTasks in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<MstTaskDTO> getAllMstTasks(MstTaskCriteria criteria) {
        log.debug("REST request to get MstTasks by criteria: {}", criteria);
        return mstTaskService.findByCriteria(criteria);
    }

    /**
     * {@code GET  /mst-tasks/count} : count all the mstTasks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countMstTasks(MstTaskCriteria criteria) {
        log.debug("REST request to count MstTasks by criteria: {}", criteria);
        return mstTaskService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /mst-tasks/:id} : get the "id" mstTask.
     *
     * @param id the id of the mstTaskDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mstTaskDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MstTaskDTO>> getMstTask(@PathVariable("id") Long id) {
        log.debug("REST request to get MstTask : {}", id);
        Mono<MstTaskDTO> mstTaskDTO = mstTaskService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mstTaskDTO);
    }

    /**
     * {@code DELETE  /mst-tasks/:id} : delete the "id" mstTask.
     *
     * @param id the id of the mstTaskDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMstTask(@PathVariable("id") Long id) {
        log.debug("REST request to delete MstTask : {}", id);
        return mstTaskService
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
     * {@code SEARCH  /mst-tasks/_search?query=:query} : search for the mstTask corresponding
     * to the query.
     *
     * @param query the query of the mstTask search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<List<MstTaskDTO>> searchMstTasks(@RequestParam("query") String query) {
        log.debug("REST request to search MstTasks for query {}", query);
        try {
            return mstTaskService.search(query).collectList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
