package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.MstProvinceCriteria;
import com.mycompany.myapp.repository.MstProvinceRepository;
import com.mycompany.myapp.repository.search.MstProvinceSearchRepository;
import com.mycompany.myapp.service.MstProvinceService;
import com.mycompany.myapp.service.dto.MstProvinceDTO;
import com.mycompany.myapp.service.mapper.MstProvinceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.MstProvince}.
 */
@Service
@Transactional
public class MstProvinceServiceImpl implements MstProvinceService {

    private static final Logger log = LoggerFactory.getLogger(MstProvinceServiceImpl.class);

    private final MstProvinceRepository mstProvinceRepository;

    private final MstProvinceMapper mstProvinceMapper;

    private final MstProvinceSearchRepository mstProvinceSearchRepository;

    public MstProvinceServiceImpl(
        MstProvinceRepository mstProvinceRepository,
        MstProvinceMapper mstProvinceMapper,
        MstProvinceSearchRepository mstProvinceSearchRepository
    ) {
        this.mstProvinceRepository = mstProvinceRepository;
        this.mstProvinceMapper = mstProvinceMapper;
        this.mstProvinceSearchRepository = mstProvinceSearchRepository;
    }

    @Override
    public Mono<MstProvinceDTO> save(MstProvinceDTO mstProvinceDTO) {
        log.debug("Request to save MstProvince : {}", mstProvinceDTO);
        return mstProvinceRepository
            .save(mstProvinceMapper.toEntity(mstProvinceDTO))
            .flatMap(mstProvinceSearchRepository::save)
            .map(mstProvinceMapper::toDto);
    }

    @Override
    public Mono<MstProvinceDTO> update(MstProvinceDTO mstProvinceDTO) {
        log.debug("Request to update MstProvince : {}", mstProvinceDTO);
        return mstProvinceRepository
            .save(mstProvinceMapper.toEntity(mstProvinceDTO))
            .flatMap(mstProvinceSearchRepository::save)
            .map(mstProvinceMapper::toDto);
    }

    @Override
    public Mono<MstProvinceDTO> partialUpdate(MstProvinceDTO mstProvinceDTO) {
        log.debug("Request to partially update MstProvince : {}", mstProvinceDTO);

        return mstProvinceRepository
            .findById(mstProvinceDTO.getId())
            .map(existingMstProvince -> {
                mstProvinceMapper.partialUpdate(existingMstProvince, mstProvinceDTO);

                return existingMstProvince;
            })
            .flatMap(mstProvinceRepository::save)
            .flatMap(savedMstProvince -> {
                mstProvinceSearchRepository.save(savedMstProvince);
                return Mono.just(savedMstProvince);
            })
            .map(mstProvinceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstProvinceDTO> findByCriteria(MstProvinceCriteria criteria, Pageable pageable) {
        log.debug("Request to get all MstProvinces by Criteria");
        return mstProvinceRepository.findByCriteria(criteria, pageable).map(mstProvinceMapper::toDto);
    }

    /**
     * Find the count of mstProvinces by criteria.
     * @param criteria filtering criteria
     * @return the count of mstProvinces
     */
    public Mono<Long> countByCriteria(MstProvinceCriteria criteria) {
        log.debug("Request to get the count of all MstProvinces by Criteria");
        return mstProvinceRepository.countByCriteria(criteria);
    }

    public Flux<MstProvinceDTO> findAllWithEagerRelationships(Pageable pageable) {
        return mstProvinceRepository.findAllWithEagerRelationships(pageable).map(mstProvinceMapper::toDto);
    }

    public Mono<Long> countAll() {
        return mstProvinceRepository.count();
    }

    public Mono<Long> searchCount() {
        return mstProvinceSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MstProvinceDTO> findOne(Long id) {
        log.debug("Request to get MstProvince : {}", id);
        return mstProvinceRepository.findOneWithEagerRelationships(id).map(mstProvinceMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete MstProvince : {}", id);
        return mstProvinceRepository.deleteById(id).then(mstProvinceSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstProvinceDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MstProvinces for query {}", query);
        return mstProvinceSearchRepository.search(query, pageable).map(mstProvinceMapper::toDto);
    }
}
