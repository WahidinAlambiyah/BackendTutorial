package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.DepartmentCriteria;
import com.mycompany.myapp.repository.DepartmentRepository;
import com.mycompany.myapp.repository.search.DepartmentSearchRepository;
import com.mycompany.myapp.service.DepartmentService;
import com.mycompany.myapp.service.dto.DepartmentDTO;
import com.mycompany.myapp.service.mapper.DepartmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Department}.
 */
@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private static final Logger log = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    private final DepartmentRepository departmentRepository;

    private final DepartmentMapper departmentMapper;

    private final DepartmentSearchRepository departmentSearchRepository;

    public DepartmentServiceImpl(
        DepartmentRepository departmentRepository,
        DepartmentMapper departmentMapper,
        DepartmentSearchRepository departmentSearchRepository
    ) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
        this.departmentSearchRepository = departmentSearchRepository;
    }

    @Override
    public Mono<DepartmentDTO> save(DepartmentDTO departmentDTO) {
        log.debug("Request to save Department : {}", departmentDTO);
        return departmentRepository
            .save(departmentMapper.toEntity(departmentDTO))
            .flatMap(departmentSearchRepository::save)
            .map(departmentMapper::toDto);
    }

    @Override
    public Mono<DepartmentDTO> update(DepartmentDTO departmentDTO) {
        log.debug("Request to update Department : {}", departmentDTO);
        return departmentRepository
            .save(departmentMapper.toEntity(departmentDTO))
            .flatMap(departmentSearchRepository::save)
            .map(departmentMapper::toDto);
    }

    @Override
    public Mono<DepartmentDTO> partialUpdate(DepartmentDTO departmentDTO) {
        log.debug("Request to partially update Department : {}", departmentDTO);

        return departmentRepository
            .findById(departmentDTO.getId())
            .map(existingDepartment -> {
                departmentMapper.partialUpdate(existingDepartment, departmentDTO);

                return existingDepartment;
            })
            .flatMap(departmentRepository::save)
            .flatMap(savedDepartment -> {
                departmentSearchRepository.save(savedDepartment);
                return Mono.just(savedDepartment);
            })
            .map(departmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DepartmentDTO> findByCriteria(DepartmentCriteria criteria) {
        log.debug("Request to get all Departments by Criteria");
        return departmentRepository.findByCriteria(criteria, null).map(departmentMapper::toDto);
    }

    /**
     * Find the count of departments by criteria.
     * @param criteria filtering criteria
     * @return the count of departments
     */
    public Mono<Long> countByCriteria(DepartmentCriteria criteria) {
        log.debug("Request to get the count of all Departments by Criteria");
        return departmentRepository.countByCriteria(criteria);
    }

    /**
     *  Get all the departments where JobHistory is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<DepartmentDTO> findAllWhereJobHistoryIsNull() {
        log.debug("Request to get all departments where JobHistory is null");
        return departmentRepository.findAllWhereJobHistoryIsNull().map(departmentMapper::toDto);
    }

    public Mono<Long> countAll() {
        return departmentRepository.count();
    }

    public Mono<Long> searchCount() {
        return departmentSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DepartmentDTO> findOne(Long id) {
        log.debug("Request to get Department : {}", id);
        return departmentRepository.findById(id).map(departmentMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Department : {}", id);
        return departmentRepository.deleteById(id).then(departmentSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DepartmentDTO> search(String query) {
        log.debug("Request to search Departments for query {}", query);
        try {
            return departmentSearchRepository.search(query).map(departmentMapper::toDto);
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
