package vente.ticket.evenement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vente.ticket.evenement.dto.scan.ScanRequest;
import vente.ticket.evenement.dto.scan.ScanResponse;
import vente.ticket.evenement.service.ScanService;

@RestController
@RequestMapping("/api/scan")
@RequiredArgsConstructor
public class ScanController {

    private final ScanService scanService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ORGANISATEUR','ADMIN')")
    public ScanResponse scanner(@Valid @RequestBody ScanRequest request) {
        return scanService.scanner(request);
    }
}
