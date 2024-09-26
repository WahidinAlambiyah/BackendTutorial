package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.MstJobCriteria;
import com.mycompany.myapp.repository.MstJobRepository;
import com.mycompany.myapp.repository.search.MstJobSearchRepository;
import com.mycompany.myapp.service.MstJobService;
import com.mycompany.myapp.service.dto.MstJobDTO;
import com.mycompany.myapp.service.mapper.MstJobMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.MstJob}.
 */
@Service
@Transactional
public class MstJobServiceImpl implements MstJobService {

    private static final Logger log = LoggerFactory.getLogger(MstJobServiceImpl.class);

    private final MstJobRepository mstJobRepository;

    private final MstJobMapper mstJobMapper;

    private final MstJobSearchRepository mstJobSearchRepository;

    public MstJobServiceImpl(MstJobRepository mstJobRepository, MstJobMapper mstJobMapper, MstJobSearchRepository mstJobSearchRepository) {
        this.mstJobRepository = mstJobRepository;
        this.mstJobMapper = mstJobMapper;
        this.mstJobSearchRepository = mstJobSearchRepository;
    }

    @Override
    public Mono<MstJobDTO> save(MstJobDTO mstJobDTO) {
        log.debug("Request to save MstJob : {}", mstJobDTO);
        return mstJobRepository.save(mstJobMapper.toEntity(mstJobDTO)).flatMap(mstJobSearchRepository::save).map(mstJobMapper::toDto);
    }

    @Override
    public Mono<MstJobDTO> update(MstJobDTO mstJobDTO) {
        log.debug("Request to update MstJob : {}", mstJobDTO);
        return mstJobRepository.save(mstJobMapper.toEntity(mstJobDTO)).flatMap(mstJobSearchRepository::save).map(mstJobMapper::toDto);
    }

    @Override
    public Mono<MstJobDTO> partialUpdate(MstJobDTO mstJobDTO) {
        log.debug("Request to partially update MstJob : {}", mstJobDTO);

        return mstJobRepository
            .findById(mstJobDTO.getId())
            .map(existingMstJob -> {
                mstJobMapper.partialUpdate(existingMstJob, mstJobDTO);

                return existingMstJob;
            })
            .flatMap(mstJobRepository::save)
            .flatMap(savedMstJob -> {
                mstJobSearchRepository.save(savedMstJob);
                return Mono.just(savedMstJob);
            })
            .map(mstJobMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstJobDTO> findByCriteria(MstJobCriteria criteria, Pageable pageable) {
        log.debug("Request to get all MstJobs by Criteria");
        return mstJobRepository.findByCriteria(criteria, pageable).map(mstJobMapper::toDto);
    }

    /**
     * Find the count of mstJobs by criteria.
     * @param criteria filtering criteria
     * @return the count of mstJobs
     */
    public Mono<Long> countByCriteria(MstJobCriteria criteria) {
        log.debug("Request to get the count of all MstJobs by Criteria");
        return mstJobRepository.countByCriteria(criteria);
    }

    public Flux<MstJobDTO> findAllWithEagerRelationships(Pageable pageable) {
        return mstJobRepository.findAllWithEagerRelationships(pageable).map(mstJobMapper::toDto);
    }

    /**
     *  Get all the mstJobs where JobHistory is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<MstJobDTO> findAllWhereJobHistoryIsNull() {
        log.debug("Request to get all mstJobs where JobHistory is null");
        return mstJobRepository.findAllWhereJobHistoryIsNull().map(mstJobMapper::toDto);
    }

    public Mono<Long> countAll() {
        return mstJobRepository.count();
    }

    public Mono<Long> searchCount() {
        return mstJobSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MstJobDTO> findOne(Long id) {
        log.debug("Request to get MstJob : {}", id);
        return mstJobRepository.findOneWithEagerRelationships(id).map(mstJobMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete MstJob : {}", id);
        return mstJobRepository.deleteById(id).then(mstJobSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstJobDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MstJobs for query {}", query);
        return mstJobSearchRepository.search(query, pageable).map(mstJobMapper::toDto);
    }
}
