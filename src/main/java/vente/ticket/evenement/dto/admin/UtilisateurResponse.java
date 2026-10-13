package vente.ticket.evenement.dto.admin;

import vente.ticket.evenement.entity.Role;

public record UtilisateurResponse(
        Long id,
        String nom,
        String email,
        Role role,
        boolean actif
) {
}
