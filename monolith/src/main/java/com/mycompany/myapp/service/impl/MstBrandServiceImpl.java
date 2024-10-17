package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.MstBrandCriteria;
import com.mycompany.myapp.repository.MstBrandRepository;
import com.mycompany.myapp.repository.search.MstBrandSearchRepository;
import com.mycompany.myapp.service.MstBrandService;
import com.mycompany.myapp.service.dto.MstBrandDTO;
import com.mycompany.myapp.service.mapper.MstBrandMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.MstBrand}.
 */
@Service
@Transactional
public class MstBrandServiceImpl implements MstBrandService {

    private static final Logger log = LoggerFactory.getLogger(MstBrandServiceImpl.class);

    private final MstBrandRepository mstBrandRepository;

    private final MstBrandMapper mstBrandMapper;

    private final MstBrandSearchRepository mstBrandSearchRepository;

    public MstBrandServiceImpl(
        MstBrandRepository mstBrandRepository,
        MstBrandMapper mstBrandMapper,
        MstBrandSearchRepository mstBrandSearchRepository
    ) {
        this.mstBrandRepository = mstBrandRepository;
        this.mstBrandMapper = mstBrandMapper;
        this.mstBrandSearchRepository = mstBrandSearchRepository;
    }

    @Override
    public Mono<MstBrandDTO> save(MstBrandDTO mstBrandDTO) {
        log.debug("Request to save MstBrand : {}", mstBrandDTO);
        return mstBrandRepository
            .save(mstBrandMapper.toEntity(mstBrandDTO))
            .flatMap(mstBrandSearchRepository::save)
            .map(mstBrandMapper::toDto);
    }

    @Override
    public Mono<MstBrandDTO> update(MstBrandDTO mstBrandDTO) {
        log.debug("Request to update MstBrand : {}", mstBrandDTO);
        return mstBrandRepository
            .save(mstBrandMapper.toEntity(mstBrandDTO))
            .flatMap(mstBrandSearchRepository::save)
            .map(mstBrandMapper::toDto);
    }

    @Override
    public Mono<MstBrandDTO> partialUpdate(MstBrandDTO mstBrandDTO) {
        log.debug("Request to partially update MstBrand : {}", mstBrandDTO);

        return mstBrandRepository
            .findById(mstBrandDTO.getId())
            .map(existingMstBrand -> {
                mstBrandMapper.partialUpdate(existingMstBrand, mstBrandDTO);

                return existingMstBrand;
            })
            .flatMap(mstBrandRepository::save)
            .flatMap(savedMstBrand -> {
                mstBrandSearchRepository.save(savedMstBrand);
                return Mono.just(savedMstBrand);
            })
            .map(mstBrandMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstBrandDTO> findByCriteria(MstBrandCriteria criteria, Pageable pageable) {
        log.debug("Request to get all MstBrands by Criteria");
        return mstBrandRepository.findByCriteria(criteria, pageable).map(mstBrandMapper::toDto);
    }

    /**
     * Find the count of mstBrands by criteria.
     * @param criteria filtering criteria
     * @return the count of mstBrands
     */
    public Mono<Long> countByCriteria(MstBrandCriteria criteria) {
        log.debug("Request to get the count of all MstBrands by Criteria");
        return mstBrandRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return mstBrandRepository.count();
    }

    public Mono<Long> searchCount() {
        return mstBrandSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MstBrandDTO> findOne(Long id) {
        log.debug("Request to get MstBrand : {}", id);
        return mstBrandRepository.findById(id).map(mstBrandMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete MstBrand : {}", id);
        return mstBrandRepository.deleteById(id).then(mstBrandSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstBrandDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MstBrands for query {}", query);
        return mstBrandSearchRepository.search(query, pageable).map(mstBrandMapper::toDto);
    }
}
