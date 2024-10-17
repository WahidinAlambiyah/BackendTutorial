package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.TrxDeliveryCriteria;
import com.mycompany.myapp.repository.TrxDeliveryRepository;
import com.mycompany.myapp.repository.search.TrxDeliverySearchRepository;
import com.mycompany.myapp.service.TrxDeliveryService;
import com.mycompany.myapp.service.dto.TrxDeliveryDTO;
import com.mycompany.myapp.service.mapper.TrxDeliveryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.TrxDelivery}.
 */
@Service
@Transactional
public class TrxDeliveryServiceImpl implements TrxDeliveryService {

    private static final Logger log = LoggerFactory.getLogger(TrxDeliveryServiceImpl.class);

    private final TrxDeliveryRepository trxDeliveryRepository;

    private final TrxDeliveryMapper trxDeliveryMapper;

    private final TrxDeliverySearchRepository trxDeliverySearchRepository;

    public TrxDeliveryServiceImpl(
        TrxDeliveryRepository trxDeliveryRepository,
        TrxDeliveryMapper trxDeliveryMapper,
        TrxDeliverySearchRepository trxDeliverySearchRepository
    ) {
        this.trxDeliveryRepository = trxDeliveryRepository;
        this.trxDeliveryMapper = trxDeliveryMapper;
        this.trxDeliverySearchRepository = trxDeliverySearchRepository;
    }

    @Override
    public Mono<TrxDeliveryDTO> save(TrxDeliveryDTO trxDeliveryDTO) {
        log.debug("Request to save TrxDelivery : {}", trxDeliveryDTO);
        return trxDeliveryRepository
            .save(trxDeliveryMapper.toEntity(trxDeliveryDTO))
            .flatMap(trxDeliverySearchRepository::save)
            .map(trxDeliveryMapper::toDto);
    }

    @Override
    public Mono<TrxDeliveryDTO> update(TrxDeliveryDTO trxDeliveryDTO) {
        log.debug("Request to update TrxDelivery : {}", trxDeliveryDTO);
        return trxDeliveryRepository
            .save(trxDeliveryMapper.toEntity(trxDeliveryDTO))
            .flatMap(trxDeliverySearchRepository::save)
            .map(trxDeliveryMapper::toDto);
    }

    @Override
    public Mono<TrxDeliveryDTO> partialUpdate(TrxDeliveryDTO trxDeliveryDTO) {
        log.debug("Request to partially update TrxDelivery : {}", trxDeliveryDTO);

        return trxDeliveryRepository
            .findById(trxDeliveryDTO.getId())
            .map(existingTrxDelivery -> {
                trxDeliveryMapper.partialUpdate(existingTrxDelivery, trxDeliveryDTO);

                return existingTrxDelivery;
            })
            .flatMap(trxDeliveryRepository::save)
            .flatMap(savedTrxDelivery -> {
                trxDeliverySearchRepository.save(savedTrxDelivery);
                return Mono.just(savedTrxDelivery);
            })
            .map(trxDeliveryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrxDeliveryDTO> findByCriteria(TrxDeliveryCriteria criteria, Pageable pageable) {
        log.debug("Request to get all TrxDeliveries by Criteria");
        return trxDeliveryRepository.findByCriteria(criteria, pageable).map(trxDeliveryMapper::toDto);
    }

    /**
     * Find the count of trxDeliveries by criteria.
     * @param criteria filtering criteria
     * @return the count of trxDeliveries
     */
    public Mono<Long> countByCriteria(TrxDeliveryCriteria criteria) {
        log.debug("Request to get the count of all TrxDeliveries by Criteria");
        return trxDeliveryRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return trxDeliveryRepository.count();
    }

    public Mono<Long> searchCount() {
        return trxDeliverySearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TrxDeliveryDTO> findOne(Long id) {
        log.debug("Request to get TrxDelivery : {}", id);
        return trxDeliveryRepository.findById(id).map(trxDeliveryMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete TrxDelivery : {}", id);
        return trxDeliveryRepository.deleteById(id).then(trxDeliverySearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrxDeliveryDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TrxDeliveries for query {}", query);
        return trxDeliverySearchRepository.search(query, pageable).map(trxDeliveryMapper::toDto);
    }
}
