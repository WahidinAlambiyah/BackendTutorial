package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.JobCriteria;
import com.mycompany.myapp.repository.JobRepository;
import com.mycompany.myapp.repository.search.JobSearchRepository;
import com.mycompany.myapp.service.JobService;
import com.mycompany.myapp.service.dto.JobDTO;
import com.mycompany.myapp.service.mapper.JobMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Job}.
 */
@Service
@Transactional
public class JobServiceImpl implements JobService {

    private static final Logger log = LoggerFactory.getLogger(JobServiceImpl.class);

    private final JobRepository jobRepository;

    private final JobMapper jobMapper;

    private final JobSearchRepository jobSearchRepository;

    public JobServiceImpl(JobRepository jobRepository, JobMapper jobMapper, JobSearchRepository jobSearchRepository) {
        this.jobRepository = jobRepository;
        this.jobMapper = jobMapper;
        this.jobSearchRepository = jobSearchRepository;
    }

    @Override
    public Mono<JobDTO> save(JobDTO jobDTO) {
        log.debug("Request to save Job : {}", jobDTO);
        return jobRepository.save(jobMapper.toEntity(jobDTO)).flatMap(jobSearchRepository::save).map(jobMapper::toDto);
    }

    @Override
    public Mono<JobDTO> update(JobDTO jobDTO) {
        log.debug("Request to update Job : {}", jobDTO);
        return jobRepository.save(jobMapper.toEntity(jobDTO)).flatMap(jobSearchRepository::save).map(jobMapper::toDto);
    }

    @Override
    public Mono<JobDTO> partialUpdate(JobDTO jobDTO) {
        log.debug("Request to partially update Job : {}", jobDTO);

        return jobRepository
            .findById(jobDTO.getId())
            .map(existingJob -> {
                jobMapper.partialUpdate(existingJob, jobDTO);

                return existingJob;
            })
            .flatMap(jobRepository::save)
            .flatMap(savedJob -> {
                jobSearchRepository.save(savedJob);
                return Mono.just(savedJob);
            })
            .map(jobMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<JobDTO> findByCriteria(JobCriteria criteria, Pageable pageable) {
        log.debug("Request to get all Jobs by Criteria");
        return jobRepository.findByCriteria(criteria, pageable).map(jobMapper::toDto);
    }

    /**
     * Find the count of jobs by criteria.
     * @param criteria filtering criteria
     * @return the count of jobs
     */
    public Mono<Long> countByCriteria(JobCriteria criteria) {
        log.debug("Request to get the count of all Jobs by Criteria");
        return jobRepository.countByCriteria(criteria);
    }

    public Flux<JobDTO> findAllWithEagerRelationships(Pageable pageable) {
        return jobRepository.findAllWithEagerRelationships(pageable).map(jobMapper::toDto);
    }

    /**
     *  Get all the jobs where JobHistory is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<JobDTO> findAllWhereJobHistoryIsNull() {
        log.debug("Request to get all jobs where JobHistory is null");
        return jobRepository.findAllWhereJobHistoryIsNull().map(jobMapper::toDto);
    }

    public Mono<Long> countAll() {
        return jobRepository.count();
    }

    public Mono<Long> searchCount() {
        return jobSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<JobDTO> findOne(Long id) {
        log.debug("Request to get Job : {}", id);
        return jobRepository.findOneWithEagerRelationships(id).map(jobMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Job : {}", id);
        return jobRepository.deleteById(id).then(jobSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<JobDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Jobs for query {}", query);
        return jobSearchRepository.search(query, pageable).map(jobMapper::toDto);
    }
}
