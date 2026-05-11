package cl.innovatech.analytics.controllers;

import cl.innovatech.analytics.services.AnalyticsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.innovatech.analytics.dtos.ProductivityKpiDTO;
import cl.innovatech.analytics.dtos.SystemHealthKpiDTO;

@RestController
@RequestMapping("/api/analytics/kpis")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/productivity")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE_PROYECTO')")
    public ProductivityKpiDTO getProductivityKpi() {
        return analyticsService.getProductivityKpi();
    }

    @GetMapping("/system-health")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE_PROYECTO')")
    public SystemHealthKpiDTO getSystemHealthKpi() {
        return analyticsService.getSystemHealthKpi();
    }
}
