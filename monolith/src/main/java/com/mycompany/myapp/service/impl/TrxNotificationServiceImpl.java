package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.TrxNotificationCriteria;
import com.mycompany.myapp.repository.TrxNotificationRepository;
import com.mycompany.myapp.repository.search.TrxNotificationSearchRepository;
import com.mycompany.myapp.service.TrxNotificationService;
import com.mycompany.myapp.service.dto.TrxNotificationDTO;
import com.mycompany.myapp.service.mapper.TrxNotificationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.TrxNotification}.
 */
@Service
@Transactional
public class TrxNotificationServiceImpl implements TrxNotificationService {

    private static final Logger log = LoggerFactory.getLogger(TrxNotificationServiceImpl.class);

    private final TrxNotificationRepository trxNotificationRepository;

    private final TrxNotificationMapper trxNotificationMapper;

    private final TrxNotificationSearchRepository trxNotificationSearchRepository;

    public TrxNotificationServiceImpl(
        TrxNotificationRepository trxNotificationRepository,
        TrxNotificationMapper trxNotificationMapper,
        TrxNotificationSearchRepository trxNotificationSearchRepository
    ) {
        this.trxNotificationRepository = trxNotificationRepository;
        this.trxNotificationMapper = trxNotificationMapper;
        this.trxNotificationSearchRepository = trxNotificationSearchRepository;
    }

    @Override
    public Mono<TrxNotificationDTO> save(TrxNotificationDTO trxNotificationDTO) {
        log.debug("Request to save TrxNotification : {}", trxNotificationDTO);
        return trxNotificationRepository
            .save(trxNotificationMapper.toEntity(trxNotificationDTO))
            .flatMap(trxNotificationSearchRepository::save)
            .map(trxNotificationMapper::toDto);
    }

    @Override
    public Mono<TrxNotificationDTO> update(TrxNotificationDTO trxNotificationDTO) {
        log.debug("Request to update TrxNotification : {}", trxNotificationDTO);
        return trxNotificationRepository
            .save(trxNotificationMapper.toEntity(trxNotificationDTO))
            .flatMap(trxNotificationSearchRepository::save)
            .map(trxNotificationMapper::toDto);
    }

    @Override
    public Mono<TrxNotificationDTO> partialUpdate(TrxNotificationDTO trxNotificationDTO) {
        log.debug("Request to partially update TrxNotification : {}", trxNotificationDTO);

        return trxNotificationRepository
            .findById(trxNotificationDTO.getId())
            .map(existingTrxNotification -> {
                trxNotificationMapper.partialUpdate(existingTrxNotification, trxNotificationDTO);

                return existingTrxNotification;
            })
            .flatMap(trxNotificationRepository::save)
            .flatMap(savedTrxNotification -> {
                trxNotificationSearchRepository.save(savedTrxNotification);
                return Mono.just(savedTrxNotification);
            })
            .map(trxNotificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrxNotificationDTO> findByCriteria(TrxNotificationCriteria criteria, Pageable pageable) {
        log.debug("Request to get all TrxNotifications by Criteria");
        return trxNotificationRepository.findByCriteria(criteria, pageable).map(trxNotificationMapper::toDto);
    }

    /**
     * Find the count of trxNotifications by criteria.
     * @param criteria filtering criteria
     * @return the count of trxNotifications
     */
    public Mono<Long> countByCriteria(TrxNotificationCriteria criteria) {
        log.debug("Request to get the count of all TrxNotifications by Criteria");
        return trxNotificationRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return trxNotificationRepository.count();
    }

    public Mono<Long> searchCount() {
        return trxNotificationSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TrxNotificationDTO> findOne(Long id) {
        log.debug("Request to get TrxNotification : {}", id);
        return trxNotificationRepository.findById(id).map(trxNotificationMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete TrxNotification : {}", id);
        return trxNotificationRepository.deleteById(id).then(trxNotificationSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrxNotificationDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TrxNotifications for query {}", query);
        return trxNotificationSearchRepository.search(query, pageable).map(trxNotificationMapper::toDto);
    }
}
