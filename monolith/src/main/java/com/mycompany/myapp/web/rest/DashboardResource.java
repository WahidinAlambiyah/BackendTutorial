package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.DashboardService;
import com.mycompany.myapp.service.dto.DashboardStatsDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class DashboardResource {

    private final DashboardService dashboardService;

    public DashboardResource(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/dashboard-stats")
    public Mono<ResponseEntity<DashboardStatsDTO>> getDashboardStats() {
        return dashboardService.getDashboardStats()
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
