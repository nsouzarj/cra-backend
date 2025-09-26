package br.adv.cra.controller;

import br.adv.cra.dto.DashboardDTO;
import br.adv.cra.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Gets global dashboard data for administrators and general users
     * 
     * @return Dashboard data with global statistics
     */
    @GetMapping
    public ResponseEntity<DashboardDTO> getDashboardData() {
        try {
            DashboardDTO dashboardData = dashboardService.getDashboardData();
            return ResponseEntity.ok(dashboardData);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Gets dashboard data specific to a correspondent
     * 
     * @param correspondenteId The ID of the correspondent
     * @return Dashboard data with statistics specific to the correspondent
     */
    @GetMapping("/{correspondenteId}")
    public ResponseEntity<DashboardDTO> getDashboardDataForCorrespondente(@PathVariable Long correspondenteId) {
        try {
            DashboardDTO dashboardData = dashboardService.getDashboardDataForCorrespondente(correspondenteId);
            return ResponseEntity.ok(dashboardData);
        } catch (RuntimeException e) {
            // Correspondente not found
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // Other errors
            return ResponseEntity.status(500).build();
        }
    }
}