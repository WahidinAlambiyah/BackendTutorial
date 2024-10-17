package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.MstDepartmentCriteria;
import com.mycompany.myapp.repository.MstDepartmentRepository;
import com.mycompany.myapp.repository.search.MstDepartmentSearchRepository;
import com.mycompany.myapp.service.MstDepartmentService;
import com.mycompany.myapp.service.dto.MstDepartmentDTO;
import com.mycompany.myapp.service.mapper.MstDepartmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.MstDepartment}.
 */
@Service
@Transactional
public class MstDepartmentServiceImpl implements MstDepartmentService {

    private static final Logger log = LoggerFactory.getLogger(MstDepartmentServiceImpl.class);

    private final MstDepartmentRepository mstDepartmentRepository;

    private final MstDepartmentMapper mstDepartmentMapper;

    private final MstDepartmentSearchRepository mstDepartmentSearchRepository;

    public MstDepartmentServiceImpl(
        MstDepartmentRepository mstDepartmentRepository,
        MstDepartmentMapper mstDepartmentMapper,
        MstDepartmentSearchRepository mstDepartmentSearchRepository
    ) {
        this.mstDepartmentRepository = mstDepartmentRepository;
        this.mstDepartmentMapper = mstDepartmentMapper;
        this.mstDepartmentSearchRepository = mstDepartmentSearchRepository;
    }

    @Override
    public Mono<MstDepartmentDTO> save(MstDepartmentDTO mstDepartmentDTO) {
        log.debug("Request to save MstDepartment : {}", mstDepartmentDTO);
        return mstDepartmentRepository
            .save(mstDepartmentMapper.toEntity(mstDepartmentDTO))
            .flatMap(mstDepartmentSearchRepository::save)
            .map(mstDepartmentMapper::toDto);
    }

    @Override
    public Mono<MstDepartmentDTO> update(MstDepartmentDTO mstDepartmentDTO) {
        log.debug("Request to update MstDepartment : {}", mstDepartmentDTO);
        return mstDepartmentRepository
            .save(mstDepartmentMapper.toEntity(mstDepartmentDTO))
            .flatMap(mstDepartmentSearchRepository::save)
            .map(mstDepartmentMapper::toDto);
    }

    @Override
    public Mono<MstDepartmentDTO> partialUpdate(MstDepartmentDTO mstDepartmentDTO) {
        log.debug("Request to partially update MstDepartment : {}", mstDepartmentDTO);

        return mstDepartmentRepository
            .findById(mstDepartmentDTO.getId())
            .map(existingMstDepartment -> {
                mstDepartmentMapper.partialUpdate(existingMstDepartment, mstDepartmentDTO);

                return existingMstDepartment;
            })
            .flatMap(mstDepartmentRepository::save)
            .flatMap(savedMstDepartment -> {
                mstDepartmentSearchRepository.save(savedMstDepartment);
                return Mono.just(savedMstDepartment);
            })
            .map(mstDepartmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstDepartmentDTO> findByCriteria(MstDepartmentCriteria criteria, Pageable pageable) {
        log.debug("Request to get all MstDepartments by Criteria");
        return mstDepartmentRepository.findByCriteria(criteria, pageable).map(mstDepartmentMapper::toDto);
    }

    /**
     * Find the count of mstDepartments by criteria.
     * @param criteria filtering criteria
     * @return the count of mstDepartments
     */
    public Mono<Long> countByCriteria(MstDepartmentCriteria criteria) {
        log.debug("Request to get the count of all MstDepartments by Criteria");
        return mstDepartmentRepository.countByCriteria(criteria);
    }

    /**
     *  Get all the mstDepartments where JobHistory is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<MstDepartmentDTO> findAllWhereJobHistoryIsNull() {
        log.debug("Request to get all mstDepartments where JobHistory is null");
        return mstDepartmentRepository.findAllWhereJobHistoryIsNull().map(mstDepartmentMapper::toDto);
    }

    public Mono<Long> countAll() {
        return mstDepartmentRepository.count();
    }

    public Mono<Long> searchCount() {
        return mstDepartmentSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MstDepartmentDTO> findOne(Long id) {
        log.debug("Request to get MstDepartment : {}", id);
        return mstDepartmentRepository.findById(id).map(mstDepartmentMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete MstDepartment : {}", id);
        return mstDepartmentRepository.deleteById(id).then(mstDepartmentSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstDepartmentDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MstDepartments for query {}", query);
        return mstDepartmentSearchRepository.search(query, pageable).map(mstDepartmentMapper::toDto);
    }
}
