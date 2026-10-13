package vente.ticket.evenement.dto.commande;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CommandeRequest(
        @NotNull(message = "Le type de ticket est obligatoire")
        Long typeTicketId,

        @NotNull(message = "La quantité est obligatoire")
        @Min(value = 1, message = "La quantité doit être au moins 1")
        Integer quantite
) {
}
