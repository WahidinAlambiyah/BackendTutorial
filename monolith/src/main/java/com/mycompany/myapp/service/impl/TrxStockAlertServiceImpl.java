package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.TrxStockAlertCriteria;
import com.mycompany.myapp.repository.TrxStockAlertRepository;
import com.mycompany.myapp.repository.search.TrxStockAlertSearchRepository;
import com.mycompany.myapp.service.TrxStockAlertService;
import com.mycompany.myapp.service.dto.TrxStockAlertDTO;
import com.mycompany.myapp.service.mapper.TrxStockAlertMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.TrxStockAlert}.
 */
@Service
@Transactional
public class TrxStockAlertServiceImpl implements TrxStockAlertService {

    private static final Logger log = LoggerFactory.getLogger(TrxStockAlertServiceImpl.class);

    private final TrxStockAlertRepository trxStockAlertRepository;

    private final TrxStockAlertMapper trxStockAlertMapper;

    private final TrxStockAlertSearchRepository trxStockAlertSearchRepository;

    public TrxStockAlertServiceImpl(
        TrxStockAlertRepository trxStockAlertRepository,
        TrxStockAlertMapper trxStockAlertMapper,
        TrxStockAlertSearchRepository trxStockAlertSearchRepository
    ) {
        this.trxStockAlertRepository = trxStockAlertRepository;
        this.trxStockAlertMapper = trxStockAlertMapper;
        this.trxStockAlertSearchRepository = trxStockAlertSearchRepository;
    }

    @Override
    public Mono<TrxStockAlertDTO> save(TrxStockAlertDTO trxStockAlertDTO) {
        log.debug("Request to save TrxStockAlert : {}", trxStockAlertDTO);
        return trxStockAlertRepository
            .save(trxStockAlertMapper.toEntity(trxStockAlertDTO))
            .flatMap(trxStockAlertSearchRepository::save)
            .map(trxStockAlertMapper::toDto);
    }

    @Override
    public Mono<TrxStockAlertDTO> update(TrxStockAlertDTO trxStockAlertDTO) {
        log.debug("Request to update TrxStockAlert : {}", trxStockAlertDTO);
        return trxStockAlertRepository
            .save(trxStockAlertMapper.toEntity(trxStockAlertDTO))
            .flatMap(trxStockAlertSearchRepository::save)
            .map(trxStockAlertMapper::toDto);
    }

    @Override
    public Mono<TrxStockAlertDTO> partialUpdate(TrxStockAlertDTO trxStockAlertDTO) {
        log.debug("Request to partially update TrxStockAlert : {}", trxStockAlertDTO);

        return trxStockAlertRepository
            .findById(trxStockAlertDTO.getId())
            .map(existingTrxStockAlert -> {
                trxStockAlertMapper.partialUpdate(existingTrxStockAlert, trxStockAlertDTO);

                return existingTrxStockAlert;
            })
            .flatMap(trxStockAlertRepository::save)
            .flatMap(savedTrxStockAlert -> {
                trxStockAlertSearchRepository.save(savedTrxStockAlert);
                return Mono.just(savedTrxStockAlert);
            })
            .map(trxStockAlertMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrxStockAlertDTO> findByCriteria(TrxStockAlertCriteria criteria, Pageable pageable) {
        log.debug("Request to get all TrxStockAlerts by Criteria");
        return trxStockAlertRepository.findByCriteria(criteria, pageable).map(trxStockAlertMapper::toDto);
    }

    /**
     * Find the count of trxStockAlerts by criteria.
     * @param criteria filtering criteria
     * @return the count of trxStockAlerts
     */
    public Mono<Long> countByCriteria(TrxStockAlertCriteria criteria) {
        log.debug("Request to get the count of all TrxStockAlerts by Criteria");
        return trxStockAlertRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return trxStockAlertRepository.count();
    }

    public Mono<Long> searchCount() {
        return trxStockAlertSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TrxStockAlertDTO> findOne(Long id) {
        log.debug("Request to get TrxStockAlert : {}", id);
        return trxStockAlertRepository.findById(id).map(trxStockAlertMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete TrxStockAlert : {}", id);
        return trxStockAlertRepository.deleteById(id).then(trxStockAlertSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrxStockAlertDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TrxStockAlerts for query {}", query);
        return trxStockAlertSearchRepository.search(query, pageable).map(trxStockAlertMapper::toDto);
    }
}
