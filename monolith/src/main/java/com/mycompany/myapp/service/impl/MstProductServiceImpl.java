package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.MstProductCriteria;
import com.mycompany.myapp.repository.MstProductRepository;
import com.mycompany.myapp.repository.search.MstProductSearchRepository;
import com.mycompany.myapp.service.MstProductService;
import com.mycompany.myapp.service.dto.MstProductDTO;
import com.mycompany.myapp.service.mapper.MstProductMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.MstProduct}.
 */
@Service
@Transactional
public class MstProductServiceImpl implements MstProductService {

    private static final Logger log = LoggerFactory.getLogger(MstProductServiceImpl.class);

    private final MstProductRepository mstProductRepository;

    private final MstProductMapper mstProductMapper;

    private final MstProductSearchRepository mstProductSearchRepository;

    public MstProductServiceImpl(
        MstProductRepository mstProductRepository,
        MstProductMapper mstProductMapper,
        MstProductSearchRepository mstProductSearchRepository
    ) {
        this.mstProductRepository = mstProductRepository;
        this.mstProductMapper = mstProductMapper;
        this.mstProductSearchRepository = mstProductSearchRepository;
    }

    @Override
    public Mono<MstProductDTO> save(MstProductDTO mstProductDTO) {
        log.debug("Request to save MstProduct : {}", mstProductDTO);
        return mstProductRepository
            .save(mstProductMapper.toEntity(mstProductDTO))
            .flatMap(mstProductSearchRepository::save)
            .map(mstProductMapper::toDto);
    }

    @Override
    public Mono<MstProductDTO> update(MstProductDTO mstProductDTO) {
        log.debug("Request to update MstProduct : {}", mstProductDTO);
        return mstProductRepository
            .save(mstProductMapper.toEntity(mstProductDTO))
            .flatMap(mstProductSearchRepository::save)
            .map(mstProductMapper::toDto);
    }

    @Override
    public Mono<MstProductDTO> partialUpdate(MstProductDTO mstProductDTO) {
        log.debug("Request to partially update MstProduct : {}", mstProductDTO);

        return mstProductRepository
            .findById(mstProductDTO.getId())
            .map(existingMstProduct -> {
                mstProductMapper.partialUpdate(existingMstProduct, mstProductDTO);

                return existingMstProduct;
            })
            .flatMap(mstProductRepository::save)
            .flatMap(savedMstProduct -> {
                mstProductSearchRepository.save(savedMstProduct);
                return Mono.just(savedMstProduct);
            })
            .map(mstProductMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstProductDTO> findByCriteria(MstProductCriteria criteria, Pageable pageable) {
        log.debug("Request to get all MstProducts by Criteria");
        return mstProductRepository.findByCriteria(criteria, pageable).map(mstProductMapper::toDto);
    }

    /**
     * Find the count of mstProducts by criteria.
     * @param criteria filtering criteria
     * @return the count of mstProducts
     */
    public Mono<Long> countByCriteria(MstProductCriteria criteria) {
        log.debug("Request to get the count of all MstProducts by Criteria");
        return mstProductRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return mstProductRepository.count();
    }

    public Mono<Long> searchCount() {
        return mstProductSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MstProductDTO> findOne(Long id) {
        log.debug("Request to get MstProduct : {}", id);
        return mstProductRepository.findById(id).map(mstProductMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete MstProduct : {}", id);
        return mstProductRepository.deleteById(id).then(mstProductSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstProductDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MstProducts for query {}", query);
        return mstProductSearchRepository.search(query, pageable).map(mstProductMapper::toDto);
    }
}
