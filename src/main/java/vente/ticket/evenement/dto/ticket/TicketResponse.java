package vente.ticket.evenement.dto.ticket;

import vente.ticket.evenement.entity.StatutTicket;

import java.time.LocalDateTime;

public record TicketResponse(
        Long id,
        String codeUnique,
        String signatureHmac,
        StatutTicket statut,
        LocalDateTime dateScan,
        Long evenementId,
        String evenementTitre,
        String typeTicketLibelle
) {
}
