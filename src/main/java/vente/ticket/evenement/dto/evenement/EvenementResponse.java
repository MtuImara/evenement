package vente.ticket.evenement.dto.evenement;

import vente.ticket.evenement.entity.StatutEvenement;

import java.time.LocalDateTime;

public record EvenementResponse(
        Long id,
        String titre,
        String description,
        String lieu,
        LocalDateTime dateDebut,
        LocalDateTime dateFin,
        StatutEvenement statut,
        Long organisateurId,
        String organisateurNom
) {
}
