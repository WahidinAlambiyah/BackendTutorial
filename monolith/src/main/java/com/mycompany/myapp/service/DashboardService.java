package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.DashboardStatsDTO;

import reactor.core.publisher.Mono;

/**
 * Service Interface for fetching the dashboard statistics.
 */
public interface DashboardService {
    Mono<DashboardStatsDTO> getDashboardStats();
}
