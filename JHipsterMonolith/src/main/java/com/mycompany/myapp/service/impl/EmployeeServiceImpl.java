package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.EmployeeCriteria;
import com.mycompany.myapp.repository.EmployeeRepository;
import com.mycompany.myapp.repository.search.EmployeeSearchRepository;
import com.mycompany.myapp.service.EmployeeService;
import com.mycompany.myapp.service.dto.EmployeeDTO;
import com.mycompany.myapp.service.mapper.EmployeeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Employee}.
 */
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;

    private final EmployeeMapper employeeMapper;

    private final EmployeeSearchRepository employeeSearchRepository;

    public EmployeeServiceImpl(
        EmployeeRepository employeeRepository,
        EmployeeMapper employeeMapper,
        EmployeeSearchRepository employeeSearchRepository
    ) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.employeeSearchRepository = employeeSearchRepository;
    }

    @Override
    public Mono<EmployeeDTO> save(EmployeeDTO employeeDTO) {
        log.debug("Request to save Employee : {}", employeeDTO);
        return employeeRepository
            .save(employeeMapper.toEntity(employeeDTO))
            .flatMap(employeeSearchRepository::save)
            .map(employeeMapper::toDto);
    }

    @Override
    public Mono<EmployeeDTO> update(EmployeeDTO employeeDTO) {
        log.debug("Request to update Employee : {}", employeeDTO);
        return employeeRepository
            .save(employeeMapper.toEntity(employeeDTO))
            .flatMap(employeeSearchRepository::save)
            .map(employeeMapper::toDto);
    }

    @Override
    public Mono<EmployeeDTO> partialUpdate(EmployeeDTO employeeDTO) {
        log.debug("Request to partially update Employee : {}", employeeDTO);

        return employeeRepository
            .findById(employeeDTO.getId())
            .map(existingEmployee -> {
                employeeMapper.partialUpdate(existingEmployee, employeeDTO);

                return existingEmployee;
            })
            .flatMap(employeeRepository::save)
            .flatMap(savedEmployee -> {
                employeeSearchRepository.save(savedEmployee);
                return Mono.just(savedEmployee);
            })
            .map(employeeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EmployeeDTO> findByCriteria(EmployeeCriteria criteria, Pageable pageable) {
        log.debug("Request to get all Employees by Criteria");
        return employeeRepository.findByCriteria(criteria, pageable).map(employeeMapper::toDto);
    }

    /**
     * Find the count of employees by criteria.
     * @param criteria filtering criteria
     * @return the count of employees
     */
    public Mono<Long> countByCriteria(EmployeeCriteria criteria) {
        log.debug("Request to get the count of all Employees by Criteria");
        return employeeRepository.countByCriteria(criteria);
    }

    /**
     *  Get all the employees where JobHistory is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<EmployeeDTO> findAllWhereJobHistoryIsNull() {
        log.debug("Request to get all employees where JobHistory is null");
        return employeeRepository.findAllWhereJobHistoryIsNull().map(employeeMapper::toDto);
    }

    public Mono<Long> countAll() {
        return employeeRepository.count();
    }

    public Mono<Long> searchCount() {
        return employeeSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<EmployeeDTO> findOne(Long id) {
        log.debug("Request to get Employee : {}", id);
        return employeeRepository.findById(id).map(employeeMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Employee : {}", id);
        return employeeRepository.deleteById(id).then(employeeSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EmployeeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Employees for query {}", query);
        return employeeSearchRepository.search(query, pageable).map(employeeMapper::toDto);
    }
}
