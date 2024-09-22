package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Testimonial;
import com.mycompany.myapp.repository.TestimonialRepository;
import com.mycompany.myapp.repository.search.TestimonialSearchRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Testimonial}.
 */
@RestController
@RequestMapping("/api/testimonials")
@Transactional
public class TestimonialResource {

    private static final Logger log = LoggerFactory.getLogger(TestimonialResource.class);

    private static final String ENTITY_NAME = "testimonial";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TestimonialRepository testimonialRepository;

    private final TestimonialSearchRepository testimonialSearchRepository;

    public TestimonialResource(TestimonialRepository testimonialRepository, TestimonialSearchRepository testimonialSearchRepository) {
        this.testimonialRepository = testimonialRepository;
        this.testimonialSearchRepository = testimonialSearchRepository;
    }

    /**
     * {@code POST  /testimonials} : Create a new testimonial.
     *
     * @param testimonial the testimonial to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new testimonial, or with status {@code 400 (Bad Request)} if the testimonial has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Testimonial>> createTestimonial(@Valid @RequestBody Testimonial testimonial) throws URISyntaxException {
        log.debug("REST request to save Testimonial : {}", testimonial);
        if (testimonial.getId() != null) {
            throw new BadRequestAlertException("A new testimonial cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return testimonialRepository
            .save(testimonial)
            .flatMap(testimonialSearchRepository::save)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/testimonials/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /testimonials/:id} : Updates an existing testimonial.
     *
     * @param id the id of the testimonial to save.
     * @param testimonial the testimonial to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated testimonial,
     * or with status {@code 400 (Bad Request)} if the testimonial is not valid,
     * or with status {@code 500 (Internal Server Error)} if the testimonial couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Testimonial>> updateTestimonial(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Testimonial testimonial
    ) throws URISyntaxException {
        log.debug("REST request to update Testimonial : {}, {}", id, testimonial);
        if (testimonial.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, testimonial.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return testimonialRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return testimonialRepository
                    .save(testimonial)
                    .flatMap(testimonialSearchRepository::save)
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
     * {@code PATCH  /testimonials/:id} : Partial updates given fields of an existing testimonial, field will ignore if it is null
     *
     * @param id the id of the testimonial to save.
     * @param testimonial the testimonial to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated testimonial,
     * or with status {@code 400 (Bad Request)} if the testimonial is not valid,
     * or with status {@code 404 (Not Found)} if the testimonial is not found,
     * or with status {@code 500 (Internal Server Error)} if the testimonial couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Testimonial>> partialUpdateTestimonial(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Testimonial testimonial
    ) throws URISyntaxException {
        log.debug("REST request to partial update Testimonial partially : {}, {}", id, testimonial);
        if (testimonial.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, testimonial.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return testimonialRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Testimonial> result = testimonialRepository
                    .findById(testimonial.getId())
                    .map(existingTestimonial -> {
                        if (testimonial.getName() != null) {
                            existingTestimonial.setName(testimonial.getName());
                        }
                        if (testimonial.getFeedback() != null) {
                            existingTestimonial.setFeedback(testimonial.getFeedback());
                        }
                        if (testimonial.getRating() != null) {
                            existingTestimonial.setRating(testimonial.getRating());
                        }
                        if (testimonial.getDate() != null) {
                            existingTestimonial.setDate(testimonial.getDate());
                        }

                        return existingTestimonial;
                    })
                    .flatMap(testimonialRepository::save)
                    .flatMap(savedTestimonial -> {
                        testimonialSearchRepository.save(savedTestimonial);
                        return Mono.just(savedTestimonial);
                    });

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
     * {@code GET  /testimonials} : get all the testimonials.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of testimonials in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Testimonial>> getAllTestimonials() {
        log.debug("REST request to get all Testimonials");
        return testimonialRepository.findAll().collectList();
    }

    /**
     * {@code GET  /testimonials} : get all the testimonials as a stream.
     * @return the {@link Flux} of testimonials.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Testimonial> getAllTestimonialsAsStream() {
        log.debug("REST request to get all Testimonials as a stream");
        return testimonialRepository.findAll();
    }

    /**
     * {@code GET  /testimonials/:id} : get the "id" testimonial.
     *
     * @param id the id of the testimonial to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the testimonial, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Testimonial>> getTestimonial(@PathVariable("id") Long id) {
        log.debug("REST request to get Testimonial : {}", id);
        Mono<Testimonial> testimonial = testimonialRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(testimonial);
    }

    /**
     * {@code DELETE  /testimonials/:id} : delete the "id" testimonial.
     *
     * @param id the id of the testimonial to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTestimonial(@PathVariable("id") Long id) {
        log.debug("REST request to delete Testimonial : {}", id);
        return testimonialRepository
            .deleteById(id)
            .then(testimonialSearchRepository.deleteById(id))
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }

    /**
     * {@code SEARCH  /testimonials/_search?query=:query} : search for the testimonial corresponding
     * to the query.
     *
     * @param query the query of the testimonial search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<List<Testimonial>> searchTestimonials(@RequestParam("query") String query) {
        log.debug("REST request to search Testimonials for query {}", query);
        try {
            return testimonialSearchRepository.search(query).collectList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
