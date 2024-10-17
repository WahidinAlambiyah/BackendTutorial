package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.MstLoyaltyProgramCriteria;
import com.mycompany.myapp.repository.MstLoyaltyProgramRepository;
import com.mycompany.myapp.repository.search.MstLoyaltyProgramSearchRepository;
import com.mycompany.myapp.service.MstLoyaltyProgramService;
import com.mycompany.myapp.service.dto.MstLoyaltyProgramDTO;
import com.mycompany.myapp.service.mapper.MstLoyaltyProgramMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.MstLoyaltyProgram}.
 */
@Service
@Transactional
public class MstLoyaltyProgramServiceImpl implements MstLoyaltyProgramService {

    private static final Logger log = LoggerFactory.getLogger(MstLoyaltyProgramServiceImpl.class);

    private final MstLoyaltyProgramRepository mstLoyaltyProgramRepository;

    private final MstLoyaltyProgramMapper mstLoyaltyProgramMapper;

    private final MstLoyaltyProgramSearchRepository mstLoyaltyProgramSearchRepository;

    public MstLoyaltyProgramServiceImpl(
        MstLoyaltyProgramRepository mstLoyaltyProgramRepository,
        MstLoyaltyProgramMapper mstLoyaltyProgramMapper,
        MstLoyaltyProgramSearchRepository mstLoyaltyProgramSearchRepository
    ) {
        this.mstLoyaltyProgramRepository = mstLoyaltyProgramRepository;
        this.mstLoyaltyProgramMapper = mstLoyaltyProgramMapper;
        this.mstLoyaltyProgramSearchRepository = mstLoyaltyProgramSearchRepository;
    }

    @Override
    public Mono<MstLoyaltyProgramDTO> save(MstLoyaltyProgramDTO mstLoyaltyProgramDTO) {
        log.debug("Request to save MstLoyaltyProgram : {}", mstLoyaltyProgramDTO);
        return mstLoyaltyProgramRepository
            .save(mstLoyaltyProgramMapper.toEntity(mstLoyaltyProgramDTO))
            .flatMap(mstLoyaltyProgramSearchRepository::save)
            .map(mstLoyaltyProgramMapper::toDto);
    }

    @Override
    public Mono<MstLoyaltyProgramDTO> update(MstLoyaltyProgramDTO mstLoyaltyProgramDTO) {
        log.debug("Request to update MstLoyaltyProgram : {}", mstLoyaltyProgramDTO);
        return mstLoyaltyProgramRepository
            .save(mstLoyaltyProgramMapper.toEntity(mstLoyaltyProgramDTO))
            .flatMap(mstLoyaltyProgramSearchRepository::save)
            .map(mstLoyaltyProgramMapper::toDto);
    }

    @Override
    public Mono<MstLoyaltyProgramDTO> partialUpdate(MstLoyaltyProgramDTO mstLoyaltyProgramDTO) {
        log.debug("Request to partially update MstLoyaltyProgram : {}", mstLoyaltyProgramDTO);

        return mstLoyaltyProgramRepository
            .findById(mstLoyaltyProgramDTO.getId())
            .map(existingMstLoyaltyProgram -> {
                mstLoyaltyProgramMapper.partialUpdate(existingMstLoyaltyProgram, mstLoyaltyProgramDTO);

                return existingMstLoyaltyProgram;
            })
            .flatMap(mstLoyaltyProgramRepository::save)
            .flatMap(savedMstLoyaltyProgram -> {
                mstLoyaltyProgramSearchRepository.save(savedMstLoyaltyProgram);
                return Mono.just(savedMstLoyaltyProgram);
            })
            .map(mstLoyaltyProgramMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstLoyaltyProgramDTO> findByCriteria(MstLoyaltyProgramCriteria criteria, Pageable pageable) {
        log.debug("Request to get all MstLoyaltyPrograms by Criteria");
        return mstLoyaltyProgramRepository.findByCriteria(criteria, pageable).map(mstLoyaltyProgramMapper::toDto);
    }

    /**
     * Find the count of mstLoyaltyPrograms by criteria.
     * @param criteria filtering criteria
     * @return the count of mstLoyaltyPrograms
     */
    public Mono<Long> countByCriteria(MstLoyaltyProgramCriteria criteria) {
        log.debug("Request to get the count of all MstLoyaltyPrograms by Criteria");
        return mstLoyaltyProgramRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return mstLoyaltyProgramRepository.count();
    }

    public Mono<Long> searchCount() {
        return mstLoyaltyProgramSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MstLoyaltyProgramDTO> findOne(Long id) {
        log.debug("Request to get MstLoyaltyProgram : {}", id);
        return mstLoyaltyProgramRepository.findById(id).map(mstLoyaltyProgramMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete MstLoyaltyProgram : {}", id);
        return mstLoyaltyProgramRepository.deleteById(id).then(mstLoyaltyProgramSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MstLoyaltyProgramDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MstLoyaltyPrograms for query {}", query);
        return mstLoyaltyProgramSearchRepository.search(query, pageable).map(mstLoyaltyProgramMapper::toDto);
    }
}
