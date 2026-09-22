package com.interviewstack.dashboard;

import com.interviewstack.dashboard.DashboardService;

import com.interviewstack.dashboard.dto.DashboardSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public DashboardSummaryResponse get(@AuthenticationPrincipal UserDetails principal) {
        return dashboardService.getSummary(principal.getUsername());
    }
}
