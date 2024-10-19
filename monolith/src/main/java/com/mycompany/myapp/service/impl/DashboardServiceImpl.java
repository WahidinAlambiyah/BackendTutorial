package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.TrxEvent;
import com.mycompany.myapp.repository.TrxEventRepository;
import com.mycompany.myapp.service.DashboardService;
import com.mycompany.myapp.service.dto.DashboardStatsDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing dashboard statistics.
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    private final TrxEventRepository trxEventRepository;

    public DashboardServiceImpl(TrxEventRepository trxEventRepository) {
        this.trxEventRepository = trxEventRepository;
    }

    @Override
    public Mono<DashboardStatsDTO> getDashboardStats() {
        // First, fetch all events using Flux
        return trxEventRepository.findAll().collectList().flatMap(events -> {
            // Initialize stats
            DashboardStatsDTO stats = new DashboardStatsDTO();
            stats.setTotalEvents((long) events.size());
            stats.setTotalParticipants(events.stream().mapToLong(TrxEvent::getCapacity).sum());
            stats.setUpcomingEvents(events.stream().filter(event -> event.getStatus().equals("UPCOMING")).count());
            stats.setOngoingEvents(events.stream().filter(event -> event.getStatus().equals("ONGOING")).count());
            stats.setCompletedEvents(events.stream().filter(event -> event.getStatus().equals("COMPLETED")).count());

            return Mono.just(stats);
        });
    }
}
