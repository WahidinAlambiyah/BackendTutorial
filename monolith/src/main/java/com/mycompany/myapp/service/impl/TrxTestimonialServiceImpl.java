package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.TrxTestimonialCriteria;
import com.mycompany.myapp.repository.TrxTestimonialRepository;
import com.mycompany.myapp.repository.search.TrxTestimonialSearchRepository;
import com.mycompany.myapp.service.TrxTestimonialService;
import com.mycompany.myapp.service.dto.TrxTestimonialDTO;
import com.mycompany.myapp.service.mapper.TrxTestimonialMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.TrxTestimonial}.
 */
@Service
@Transactional
public class TrxTestimonialServiceImpl implements TrxTestimonialService {

    private static final Logger log = LoggerFactory.getLogger(TrxTestimonialServiceImpl.class);

    private final TrxTestimonialRepository trxTestimonialRepository;

    private final TrxTestimonialMapper trxTestimonialMapper;

    private final TrxTestimonialSearchRepository trxTestimonialSearchRepository;

    public TrxTestimonialServiceImpl(
        TrxTestimonialRepository trxTestimonialRepository,
        TrxTestimonialMapper trxTestimonialMapper,
        TrxTestimonialSearchRepository trxTestimonialSearchRepository
    ) {
        this.trxTestimonialRepository = trxTestimonialRepository;
        this.trxTestimonialMapper = trxTestimonialMapper;
        this.trxTestimonialSearchRepository = trxTestimonialSearchRepository;
    }

    @Override
    public Mono<TrxTestimonialDTO> save(TrxTestimonialDTO trxTestimonialDTO) {
        log.debug("Request to save TrxTestimonial : {}", trxTestimonialDTO);
        return trxTestimonialRepository
            .save(trxTestimonialMapper.toEntity(trxTestimonialDTO))
            .flatMap(trxTestimonialSearchRepository::save)
            .map(trxTestimonialMapper::toDto);
    }

    @Override
    public Mono<TrxTestimonialDTO> update(TrxTestimonialDTO trxTestimonialDTO) {
        log.debug("Request to update TrxTestimonial : {}", trxTestimonialDTO);
        return trxTestimonialRepository
            .save(trxTestimonialMapper.toEntity(trxTestimonialDTO))
            .flatMap(trxTestimonialSearchRepository::save)
            .map(trxTestimonialMapper::toDto);
    }

    @Override
    public Mono<TrxTestimonialDTO> partialUpdate(TrxTestimonialDTO trxTestimonialDTO) {
        log.debug("Request to partially update TrxTestimonial : {}", trxTestimonialDTO);

        return trxTestimonialRepository
            .findById(trxTestimonialDTO.getId())
            .map(existingTrxTestimonial -> {
                trxTestimonialMapper.partialUpdate(existingTrxTestimonial, trxTestimonialDTO);

                return existingTrxTestimonial;
            })
            .flatMap(trxTestimonialRepository::save)
            .flatMap(savedTrxTestimonial -> {
                trxTestimonialSearchRepository.save(savedTrxTestimonial);
                return Mono.just(savedTrxTestimonial);
            })
            .map(trxTestimonialMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrxTestimonialDTO> findByCriteria(TrxTestimonialCriteria criteria) {
        log.debug("Request to get all TrxTestimonials by Criteria");
        return trxTestimonialRepository.findByCriteria(criteria, null).map(trxTestimonialMapper::toDto);
    }

    /**
     * Find the count of trxTestimonials by criteria.
     * @param criteria filtering criteria
     * @return the count of trxTestimonials
     */
    public Mono<Long> countByCriteria(TrxTestimonialCriteria criteria) {
        log.debug("Request to get the count of all TrxTestimonials by Criteria");
        return trxTestimonialRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return trxTestimonialRepository.count();
    }

    public Mono<Long> searchCount() {
        return trxTestimonialSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TrxTestimonialDTO> findOne(Long id) {
        log.debug("Request to get TrxTestimonial : {}", id);
        return trxTestimonialRepository.findById(id).map(trxTestimonialMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete TrxTestimonial : {}", id);
        return trxTestimonialRepository.deleteById(id).then(trxTestimonialSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrxTestimonialDTO> search(String query) {
        log.debug("Request to search TrxTestimonials for query {}", query);
        try {
            return trxTestimonialSearchRepository.search(query).map(trxTestimonialMapper::toDto);
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
