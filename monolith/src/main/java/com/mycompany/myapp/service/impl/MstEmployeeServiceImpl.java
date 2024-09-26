package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.MstEmployeeCriteria;
import com.mycompany.myapp.repository.MstEmployeeRepository;
import com.mycompany.myapp.repository.search.MstEmployeeSearchRepository;
import com.mycompany.myapp.service.MstEmployeeService;
import com.mycompany.myapp.service.dto.MstEmployeeDTO;
import com.mycompany.myapp.service.mapper.MstEmployeeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.MstEmployee}.
 */
@Service
@Transactional
public class MstEmployeeServiceImpl implements MstEmployeeService {

    private static final Logger log = LoggerFactory.getLogger(MstEmployeeServiceImpl.class);

    private final MstEmployeeRepository mstEmployeeRepository;

    private final MstEmployeeMapper mstEmployeeMapper;

    private final MstEmployeeSearchRepository mstEmployeeSearchRepository;

    public MstEmployeeServiceImpl(
        MstEmployeeRepository mstEmployeeRepository,
        MstEmployeeMapper mstEmployeeMapper,
        MstEmployeeSearchRepository mstEmployeeSearchRepository
    ) {
        this.mstEmployeeRepository = mstEmployeeRepository;
        this.mstEmployeeMapper = mstEmployeeMapper;
        this.mstEmployeeSearchRepository = mstEmployeeSearchRepository;
    }

    @Override
    public Mono<MstEmployeeDTO> save(MstEmployeeDTO mstEmployeeDTO) {
        log.debug("Request to save MstEmployee : {}", mstEmployeeDTO);
        return mstEmployeeRepository
            .save(mstEmployeeMapper.toEntity(mstEmployeeDTO))
            .flatMap(mstEmployeeSearchRepository::save)
            .map(mstEmployeeMapper::toDto);
    }

    @Override
    public Mono<MstEmployeeDTO> update(MstEmployeeDTO mstEmployeeDTO) {
        log.debug("Request to update MstEmployee : {}", mstEmployeeDTO);
        return mstEmployeeRepository
            .save(mstEmployeeMapper.toEntity(mstEmployeeDTO))
            .flatMap(mstEmployeeSearchRepository::save)
            .map(mstEmployeeMapper::toDto);
    }

    @Override
    public Mono<MstEmployeeDTO> partialUpdate(MstEmployeeDTO mstEmployeeDTO) {
        log.debug("Request to partially update MstEmployee : {}", mstEmployeeDTO);

        return mstEmployeeRepository
            .findById(mstEmployeeDTO.getId())
            .map(existingMstEmployee -> {
                mstEmployeeMapper.partialUpdate(existingMstEmployee, mstEmployeeDTO);

                return existingMstEmployee;
            })
            .flatMap(mstEmployeeRepository::save)
            .flatMap(savedMstEmployee -> {
                mstEmployeeSearchRepository.save(savedMstEmployee);
                return Mono.just(savedMstEmployee);
            })
            .map(mstEmployeeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstEmployeeDTO> findByCriteria(MstEmployeeCriteria criteria, Pageable pageable) {
        log.debug("Request to get all MstEmployees by Criteria");
        return mstEmployeeRepository.findByCriteria(criteria, pageable).map(mstEmployeeMapper::toDto);
    }

    /**
     * Find the count of mstEmployees by criteria.
     * @param criteria filtering criteria
     * @return the count of mstEmployees
     */
    public Mono<Long> countByCriteria(MstEmployeeCriteria criteria) {
        log.debug("Request to get the count of all MstEmployees by Criteria");
        return mstEmployeeRepository.countByCriteria(criteria);
    }

    /**
     *  Get all the mstEmployees where JobHistory is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<MstEmployeeDTO> findAllWhereJobHistoryIsNull() {
        log.debug("Request to get all mstEmployees where JobHistory is null");
        return mstEmployeeRepository.findAllWhereJobHistoryIsNull().map(mstEmployeeMapper::toDto);
    }

    public Mono<Long> countAll() {
        return mstEmployeeRepository.count();
    }

    public Mono<Long> searchCount() {
        return mstEmployeeSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MstEmployeeDTO> findOne(Long id) {
        log.debug("Request to get MstEmployee : {}", id);
        return mstEmployeeRepository.findById(id).map(mstEmployeeMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete MstEmployee : {}", id);
        return mstEmployeeRepository.deleteById(id).then(mstEmployeeSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstEmployeeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MstEmployees for query {}", query);
        return mstEmployeeSearchRepository.search(query, pageable).map(mstEmployeeMapper::toDto);
    }
}
