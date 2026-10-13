package vente.ticket.evenement.dto.auth;

import vente.ticket.evenement.entity.Role;

public record AuthResponse(
        String token,
        Long userId,
        String nom,
        String email,
        Role role
) {
}
