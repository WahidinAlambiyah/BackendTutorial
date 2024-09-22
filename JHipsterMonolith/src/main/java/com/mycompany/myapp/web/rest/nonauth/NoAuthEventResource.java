package com.mycompany.myapp.web.rest.nonauth;

import com.mycompany.myapp.domain.Event;
import com.mycompany.myapp.repository.EventRepository;
import com.mycompany.myapp.repository.search.EventSearchRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Event}.
 */
@RestController
@RequestMapping("/noauth/events")
@Transactional
public class NoAuthEventResource {

    private static final Logger log = LoggerFactory.getLogger(NoAuthEventResource.class);

    private static final String ENTITY_NAME = "event";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EventRepository eventRepository;

    private final EventSearchRepository eventSearchRepository;

    public NoAuthEventResource(EventRepository eventRepository, EventSearchRepository eventSearchRepository) {
        this.eventRepository = eventRepository;
        this.eventSearchRepository = eventSearchRepository;
    }

    /**
     * {@code POST  /events} : Create a new event.
     *
     * @param event the event to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new event, or with status {@code 400 (Bad Request)} if the event has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Event>> createEvent(@Valid @RequestBody Event event) throws URISyntaxException {
        log.debug("REST request to save Event : {}", event);
        if (event.getId() != null) {
            throw new BadRequestAlertException("A new event cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return eventRepository
            .save(event)
            .flatMap(eventSearchRepository::save)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/events/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /events/:id} : Updates an existing event.
     *
     * @param id the id of the event to save.
     * @param event the event to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated event,
     * or with status {@code 400 (Bad Request)} if the event is not valid,
     * or with status {@code 500 (Internal Server Error)} if the event couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Event>> updateEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Event event
    ) throws URISyntaxException {
        log.debug("REST request to update Event : {}, {}", id, event);
        if (event.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, event.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return eventRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return eventRepository
                    .save(event)
                    .flatMap(eventSearchRepository::save)
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
     * {@code PATCH  /events/:id} : Partial updates given fields of an existing event, field will ignore if it is null
     *
     * @param id the id of the event to save.
     * @param event the event to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated event,
     * or with status {@code 400 (Bad Request)} if the event is not valid,
     * or with status {@code 404 (Not Found)} if the event is not found,
     * or with status {@code 500 (Internal Server Error)} if the event couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Event>> partialUpdateEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Event event
    ) throws URISyntaxException {
        log.debug("REST request to partial update Event partially : {}, {}", id, event);
        if (event.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, event.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return eventRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Event> result = eventRepository
                    .findById(event.getId())
                    .map(existingEvent -> {
                        if (event.getTitle() != null) {
                            existingEvent.setTitle(event.getTitle());
                        }
                        if (event.getDescription() != null) {
                            existingEvent.setDescription(event.getDescription());
                        }
                        if (event.getDate() != null) {
                            existingEvent.setDate(event.getDate());
                        }
                        if (event.getLocation() != null) {
                            existingEvent.setLocation(event.getLocation());
                        }
                        if (event.getCapacity() != null) {
                            existingEvent.setCapacity(event.getCapacity());
                        }
                        if (event.getPrice() != null) {
                            existingEvent.setPrice(event.getPrice());
                        }
                        if (event.getStatus() != null) {
                            existingEvent.setStatus(event.getStatus());
                        }

                        return existingEvent;
                    })
                    .flatMap(eventRepository::save)
                    .flatMap(savedEvent -> {
                        eventSearchRepository.save(savedEvent);
                        return Mono.just(savedEvent);
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
     * {@code GET  /events} : get all the events.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of events in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Event>> getAllEvents() {
        log.debug("REST request to get all Events");
        return eventRepository.findAll().collectList();
    }

    /**
     * {@code GET  /events} : get all the events as a stream.
     * @return the {@link Flux} of events.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Event> getAllEventsAsStream() {
        log.debug("REST request to get all Events as a stream");
        return eventRepository.findAll();
    }

    /**
     * {@code GET  /events/:id} : get the "id" event.
     *
     * @param id the id of the event to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the event, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Event>> getEvent(@PathVariable("id") Long id) {
        log.debug("REST request to get Event : {}", id);
        Mono<Event> event = eventRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(event);
    }

    /**
     * {@code DELETE  /events/:id} : delete the "id" event.
     *
     * @param id the id of the event to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteEvent(@PathVariable("id") Long id) {
        log.debug("REST request to delete Event : {}", id);
        return eventRepository
            .deleteById(id)
            .then(eventSearchRepository.deleteById(id))
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }

    /**
     * {@code SEARCH  /events/_search?query=:query} : search for the event corresponding
     * to the query.
     *
     * @param query the query of the event search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<List<Event>> searchEvents(@RequestParam("query") String query) {
        log.debug("REST request to search Events for query {}", query);
        try {
            return eventSearchRepository.search(query).collectList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
