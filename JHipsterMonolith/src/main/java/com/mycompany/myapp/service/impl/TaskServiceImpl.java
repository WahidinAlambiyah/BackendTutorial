package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.TaskCriteria;
import com.mycompany.myapp.repository.TaskRepository;
import com.mycompany.myapp.repository.search.TaskSearchRepository;
import com.mycompany.myapp.service.TaskService;
import com.mycompany.myapp.service.dto.TaskDTO;
import com.mycompany.myapp.service.mapper.TaskMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Task}.
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    private final TaskSearchRepository taskSearchRepository;

    public TaskServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper, TaskSearchRepository taskSearchRepository) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.taskSearchRepository = taskSearchRepository;
    }

    @Override
    public Mono<TaskDTO> save(TaskDTO taskDTO) {
        log.debug("Request to save Task : {}", taskDTO);
        return taskRepository.save(taskMapper.toEntity(taskDTO)).flatMap(taskSearchRepository::save).map(taskMapper::toDto);
    }

    @Override
    public Mono<TaskDTO> update(TaskDTO taskDTO) {
        log.debug("Request to update Task : {}", taskDTO);
        return taskRepository.save(taskMapper.toEntity(taskDTO)).flatMap(taskSearchRepository::save).map(taskMapper::toDto);
    }

    @Override
    public Mono<TaskDTO> partialUpdate(TaskDTO taskDTO) {
        log.debug("Request to partially update Task : {}", taskDTO);

        return taskRepository
            .findById(taskDTO.getId())
            .map(existingTask -> {
                taskMapper.partialUpdate(existingTask, taskDTO);

                return existingTask;
            })
            .flatMap(taskRepository::save)
            .flatMap(savedTask -> {
                taskSearchRepository.save(savedTask);
                return Mono.just(savedTask);
            })
            .map(taskMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TaskDTO> findByCriteria(TaskCriteria criteria) {
        log.debug("Request to get all Tasks by Criteria");
        return taskRepository.findByCriteria(criteria, null).map(taskMapper::toDto);
    }

    /**
     * Find the count of tasks by criteria.
     * @param criteria filtering criteria
     * @return the count of tasks
     */
    public Mono<Long> countByCriteria(TaskCriteria criteria) {
        log.debug("Request to get the count of all Tasks by Criteria");
        return taskRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return taskRepository.count();
    }

    public Mono<Long> searchCount() {
        return taskSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TaskDTO> findOne(Long id) {
        log.debug("Request to get Task : {}", id);
        return taskRepository.findById(id).map(taskMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Task : {}", id);
        return taskRepository.deleteById(id).then(taskSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TaskDTO> search(String query) {
        log.debug("Request to search Tasks for query {}", query);
        try {
            return taskSearchRepository.search(query).map(taskMapper::toDto);
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
