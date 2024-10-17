package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.criteria.StockCriteria;
import com.mycompany.myapp.repository.StockRepository;
import com.mycompany.myapp.repository.search.StockSearchRepository;
import com.mycompany.myapp.service.StockService;
import com.mycompany.myapp.service.dto.StockDTO;
import com.mycompany.myapp.service.mapper.StockMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Stock}.
 */
@Service
@Transactional
public class StockServiceImpl implements StockService {

    private static final Logger log = LoggerFactory.getLogger(StockServiceImpl.class);

    private final StockRepository stockRepository;

    private final StockMapper stockMapper;

    private final StockSearchRepository stockSearchRepository;

    public StockServiceImpl(StockRepository stockRepository, StockMapper stockMapper, StockSearchRepository stockSearchRepository) {
        this.stockRepository = stockRepository;
        this.stockMapper = stockMapper;
        this.stockSearchRepository = stockSearchRepository;
    }

    @Override
    public Mono<StockDTO> save(StockDTO stockDTO) {
        log.debug("Request to save Stock : {}", stockDTO);
        return stockRepository.save(stockMapper.toEntity(stockDTO)).flatMap(stockSearchRepository::save).map(stockMapper::toDto);
    }

    @Override
    public Mono<StockDTO> update(StockDTO stockDTO) {
        log.debug("Request to update Stock : {}", stockDTO);
        return stockRepository.save(stockMapper.toEntity(stockDTO)).flatMap(stockSearchRepository::save).map(stockMapper::toDto);
    }

    @Override
    public Mono<StockDTO> partialUpdate(StockDTO stockDTO) {
        log.debug("Request to partially update Stock : {}", stockDTO);

        return stockRepository
            .findById(stockDTO.getId())
            .map(existingStock -> {
                stockMapper.partialUpdate(existingStock, stockDTO);

                return existingStock;
            })
            .flatMap(stockRepository::save)
            .flatMap(savedStock -> {
                stockSearchRepository.save(savedStock);
                return Mono.just(savedStock);
            })
            .map(stockMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<StockDTO> findByCriteria(StockCriteria criteria, Pageable pageable) {
        log.debug("Request to get all Stocks by Criteria");
        return stockRepository.findByCriteria(criteria, pageable).map(stockMapper::toDto);
    }

    /**
     * Find the count of stocks by criteria.
     * @param criteria filtering criteria
     * @return the count of stocks
     */
    public Mono<Long> countByCriteria(StockCriteria criteria) {
        log.debug("Request to get the count of all Stocks by Criteria");
        return stockRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return stockRepository.count();
    }

    public Mono<Long> searchCount() {
        return stockSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<StockDTO> findOne(Long id) {
        log.debug("Request to get Stock : {}", id);
        return stockRepository.findById(id).map(stockMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Stock : {}", id);
        return stockRepository.deleteById(id).then(stockSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<StockDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Stocks for query {}", query);
        return stockSearchRepository.search(query, pageable).map(stockMapper::toDto);
    }
}
