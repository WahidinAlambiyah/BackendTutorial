package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.MstCategoryCriteria;
import com.mycompany.myapp.repository.MstCategoryRepository;
import com.mycompany.myapp.repository.search.MstCategorySearchRepository;
import com.mycompany.myapp.service.MstCategoryService;
import com.mycompany.myapp.service.dto.MstCategoryDTO;
import com.mycompany.myapp.service.mapper.MstCategoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.MstCategory}.
 */
@Service
@Transactional
public class MstCategoryServiceImpl implements MstCategoryService {

    private static final Logger log = LoggerFactory.getLogger(MstCategoryServiceImpl.class);

    private final MstCategoryRepository mstCategoryRepository;

    private final MstCategoryMapper mstCategoryMapper;

    private final MstCategorySearchRepository mstCategorySearchRepository;

    public MstCategoryServiceImpl(
        MstCategoryRepository mstCategoryRepository,
        MstCategoryMapper mstCategoryMapper,
        MstCategorySearchRepository mstCategorySearchRepository
    ) {
        this.mstCategoryRepository = mstCategoryRepository;
        this.mstCategoryMapper = mstCategoryMapper;
        this.mstCategorySearchRepository = mstCategorySearchRepository;
    }

    @Override
    public Mono<MstCategoryDTO> save(MstCategoryDTO mstCategoryDTO) {
        log.debug("Request to save MstCategory : {}", mstCategoryDTO);
        return mstCategoryRepository
            .save(mstCategoryMapper.toEntity(mstCategoryDTO))
            .flatMap(mstCategorySearchRepository::save)
            .map(mstCategoryMapper::toDto);
    }

    @Override
    public Mono<MstCategoryDTO> update(MstCategoryDTO mstCategoryDTO) {
        log.debug("Request to update MstCategory : {}", mstCategoryDTO);
        return mstCategoryRepository
            .save(mstCategoryMapper.toEntity(mstCategoryDTO))
            .flatMap(mstCategorySearchRepository::save)
            .map(mstCategoryMapper::toDto);
    }

    @Override
    public Mono<MstCategoryDTO> partialUpdate(MstCategoryDTO mstCategoryDTO) {
        log.debug("Request to partially update MstCategory : {}", mstCategoryDTO);

        return mstCategoryRepository
            .findById(mstCategoryDTO.getId())
            .map(existingMstCategory -> {
                mstCategoryMapper.partialUpdate(existingMstCategory, mstCategoryDTO);

                return existingMstCategory;
            })
            .flatMap(mstCategoryRepository::save)
            .flatMap(savedMstCategory -> {
                mstCategorySearchRepository.save(savedMstCategory);
                return Mono.just(savedMstCategory);
            })
            .map(mstCategoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstCategoryDTO> findByCriteria(MstCategoryCriteria criteria, Pageable pageable) {
        log.debug("Request to get all MstCategories by Criteria");
        return mstCategoryRepository.findByCriteria(criteria, pageable).map(mstCategoryMapper::toDto);
    }

    /**
     * Find the count of mstCategories by criteria.
     * @param criteria filtering criteria
     * @return the count of mstCategories
     */
    public Mono<Long> countByCriteria(MstCategoryCriteria criteria) {
        log.debug("Request to get the count of all MstCategories by Criteria");
        return mstCategoryRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return mstCategoryRepository.count();
    }

    public Mono<Long> searchCount() {
        return mstCategorySearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MstCategoryDTO> findOne(Long id) {
        log.debug("Request to get MstCategory : {}", id);
        return mstCategoryRepository.findById(id).map(mstCategoryMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete MstCategory : {}", id);
        return mstCategoryRepository.deleteById(id).then(mstCategorySearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstCategoryDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MstCategories for query {}", query);
        return mstCategorySearchRepository.search(query, pageable).map(mstCategoryMapper::toDto);
    }
}
