package vente.ticket.evenement.dto.commande;

import vente.ticket.evenement.dto.ticket.TicketResponse;
import vente.ticket.evenement.entity.StatutCommande;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CommandeResponse(
        Long id,
        LocalDateTime dateCommande,
        StatutCommande statut,
        BigDecimal montantTotal,
        List<TicketResponse> tickets
) {
}
