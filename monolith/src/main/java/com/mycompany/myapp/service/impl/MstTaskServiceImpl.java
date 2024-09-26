package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.MstTaskCriteria;
import com.mycompany.myapp.repository.MstTaskRepository;
import com.mycompany.myapp.repository.search.MstTaskSearchRepository;
import com.mycompany.myapp.service.MstTaskService;
import com.mycompany.myapp.service.dto.MstTaskDTO;
import com.mycompany.myapp.service.mapper.MstTaskMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.MstTask}.
 */
@Service
@Transactional
public class MstTaskServiceImpl implements MstTaskService {

    private static final Logger log = LoggerFactory.getLogger(MstTaskServiceImpl.class);

    private final MstTaskRepository mstTaskRepository;

    private final MstTaskMapper mstTaskMapper;

    private final MstTaskSearchRepository mstTaskSearchRepository;

    public MstTaskServiceImpl(
        MstTaskRepository mstTaskRepository,
        MstTaskMapper mstTaskMapper,
        MstTaskSearchRepository mstTaskSearchRepository
    ) {
        this.mstTaskRepository = mstTaskRepository;
        this.mstTaskMapper = mstTaskMapper;
        this.mstTaskSearchRepository = mstTaskSearchRepository;
    }

    @Override
    public Mono<MstTaskDTO> save(MstTaskDTO mstTaskDTO) {
        log.debug("Request to save MstTask : {}", mstTaskDTO);
        return mstTaskRepository.save(mstTaskMapper.toEntity(mstTaskDTO)).flatMap(mstTaskSearchRepository::save).map(mstTaskMapper::toDto);
    }

    @Override
    public Mono<MstTaskDTO> update(MstTaskDTO mstTaskDTO) {
        log.debug("Request to update MstTask : {}", mstTaskDTO);
        return mstTaskRepository.save(mstTaskMapper.toEntity(mstTaskDTO)).flatMap(mstTaskSearchRepository::save).map(mstTaskMapper::toDto);
    }

    @Override
    public Mono<MstTaskDTO> partialUpdate(MstTaskDTO mstTaskDTO) {
        log.debug("Request to partially update MstTask : {}", mstTaskDTO);

        return mstTaskRepository
            .findById(mstTaskDTO.getId())
            .map(existingMstTask -> {
                mstTaskMapper.partialUpdate(existingMstTask, mstTaskDTO);

                return existingMstTask;
            })
            .flatMap(mstTaskRepository::save)
            .flatMap(savedMstTask -> {
                mstTaskSearchRepository.save(savedMstTask);
                return Mono.just(savedMstTask);
            })
            .map(mstTaskMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstTaskDTO> findByCriteria(MstTaskCriteria criteria) {
        log.debug("Request to get all MstTasks by Criteria");
        return mstTaskRepository.findByCriteria(criteria, null).map(mstTaskMapper::toDto);
    }

    /**
     * Find the count of mstTasks by criteria.
     * @param criteria filtering criteria
     * @return the count of mstTasks
     */
    public Mono<Long> countByCriteria(MstTaskCriteria criteria) {
        log.debug("Request to get the count of all MstTasks by Criteria");
        return mstTaskRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return mstTaskRepository.count();
    }

    public Mono<Long> searchCount() {
        return mstTaskSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MstTaskDTO> findOne(Long id) {
        log.debug("Request to get MstTask : {}", id);
        return mstTaskRepository.findById(id).map(mstTaskMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete MstTask : {}", id);
        return mstTaskRepository.deleteById(id).then(mstTaskSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstTaskDTO> search(String query) {
        log.debug("Request to search MstTasks for query {}", query);
        try {
            return mstTaskSearchRepository.search(query).map(mstTaskMapper::toDto);
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
