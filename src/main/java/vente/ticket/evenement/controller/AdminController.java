package vente.ticket.evenement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vente.ticket.evenement.dto.admin.StatistiquesResponse;
import vente.ticket.evenement.dto.admin.UtilisateurResponse;
import vente.ticket.evenement.service.AdminService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/utilisateurs")
    public List<UtilisateurResponse> utilisateurs() {
        return adminService.listerUtilisateurs();
    }

    @PatchMapping("/utilisateurs/{id}/statut")
    public UtilisateurResponse changerStatut(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        Boolean actif = body.get("actif");
        if (actif == null) {
            throw new IllegalArgumentException("Le champ 'actif' est obligatoire");
        }
        return adminService.changerStatut(id, actif);
    }

    @GetMapping("/statistiques")
    public StatistiquesResponse statistiques() {
        return adminService.statistiques();
    }
}
