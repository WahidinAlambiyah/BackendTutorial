package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.JobHistoryCriteria;
import com.mycompany.myapp.repository.JobHistoryRepository;
import com.mycompany.myapp.repository.search.JobHistorySearchRepository;
import com.mycompany.myapp.service.JobHistoryService;
import com.mycompany.myapp.service.dto.JobHistoryDTO;
import com.mycompany.myapp.service.mapper.JobHistoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.JobHistory}.
 */
@Service
@Transactional
public class JobHistoryServiceImpl implements JobHistoryService {

    private static final Logger log = LoggerFactory.getLogger(JobHistoryServiceImpl.class);

    private final JobHistoryRepository jobHistoryRepository;

    private final JobHistoryMapper jobHistoryMapper;

    private final JobHistorySearchRepository jobHistorySearchRepository;

    public JobHistoryServiceImpl(
        JobHistoryRepository jobHistoryRepository,
        JobHistoryMapper jobHistoryMapper,
        JobHistorySearchRepository jobHistorySearchRepository
    ) {
        this.jobHistoryRepository = jobHistoryRepository;
        this.jobHistoryMapper = jobHistoryMapper;
        this.jobHistorySearchRepository = jobHistorySearchRepository;
    }

    @Override
    public Mono<JobHistoryDTO> save(JobHistoryDTO jobHistoryDTO) {
        log.debug("Request to save JobHistory : {}", jobHistoryDTO);
        return jobHistoryRepository
            .save(jobHistoryMapper.toEntity(jobHistoryDTO))
            .flatMap(jobHistorySearchRepository::save)
            .map(jobHistoryMapper::toDto);
    }

    @Override
    public Mono<JobHistoryDTO> update(JobHistoryDTO jobHistoryDTO) {
        log.debug("Request to update JobHistory : {}", jobHistoryDTO);
        return jobHistoryRepository
            .save(jobHistoryMapper.toEntity(jobHistoryDTO))
            .flatMap(jobHistorySearchRepository::save)
            .map(jobHistoryMapper::toDto);
    }

    @Override
    public Mono<JobHistoryDTO> partialUpdate(JobHistoryDTO jobHistoryDTO) {
        log.debug("Request to partially update JobHistory : {}", jobHistoryDTO);

        return jobHistoryRepository
            .findById(jobHistoryDTO.getId())
            .map(existingJobHistory -> {
                jobHistoryMapper.partialUpdate(existingJobHistory, jobHistoryDTO);

                return existingJobHistory;
            })
            .flatMap(jobHistoryRepository::save)
            .flatMap(savedJobHistory -> {
                jobHistorySearchRepository.save(savedJobHistory);
                return Mono.just(savedJobHistory);
            })
            .map(jobHistoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<JobHistoryDTO> findByCriteria(JobHistoryCriteria criteria, Pageable pageable) {
        log.debug("Request to get all JobHistories by Criteria");
        return jobHistoryRepository.findByCriteria(criteria, pageable).map(jobHistoryMapper::toDto);
    }

    /**
     * Find the count of jobHistories by criteria.
     * @param criteria filtering criteria
     * @return the count of jobHistories
     */
    public Mono<Long> countByCriteria(JobHistoryCriteria criteria) {
        log.debug("Request to get the count of all JobHistories by Criteria");
        return jobHistoryRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return jobHistoryRepository.count();
    }

    public Mono<Long> searchCount() {
        return jobHistorySearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<JobHistoryDTO> findOne(Long id) {
        log.debug("Request to get JobHistory : {}", id);
        return jobHistoryRepository.findById(id).map(jobHistoryMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete JobHistory : {}", id);
        return jobHistoryRepository.deleteById(id).then(jobHistorySearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<JobHistoryDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of JobHistories for query {}", query);
        return jobHistorySearchRepository.search(query, pageable).map(jobHistoryMapper::toDto);
    }
}
