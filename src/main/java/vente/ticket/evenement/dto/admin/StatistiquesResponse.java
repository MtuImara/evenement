package vente.ticket.evenement.dto.admin;

public record StatistiquesResponse(
        long nombreUtilisateurs,
        long nombreEvenements,
        long nombreTickets,
        long ticketsUtilises,
        long ticketsValides
) {
}
