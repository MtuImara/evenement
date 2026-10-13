package vente.ticket.evenement.dto.scan;

public record ScanResponse(
        boolean autorise,
        String message,
        Long ticketId,
        String participantNom
) {
}
