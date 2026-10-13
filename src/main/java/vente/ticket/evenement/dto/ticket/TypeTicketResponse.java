package vente.ticket.evenement.dto.ticket;

import java.math.BigDecimal;

public record TypeTicketResponse(
        Long id,
        String libelle,
        BigDecimal prix,
        Integer quantiteTotale,
        Integer quantiteVendue,
        Integer quantiteDisponible,
        Long evenementId
) {
}
