package vente.ticket.evenement.dto.evenement;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import vente.ticket.evenement.validation.DateFinApresDebut;

import java.time.LocalDateTime;

@DateFinApresDebut
public record EvenementRequest(
        @NotBlank(message = "Le titre est obligatoire")
        String titre,

        String description,

        @NotBlank(message = "Le lieu est obligatoire")
        String lieu,

        @NotNull(message = "La date de début est obligatoire")
        @Future(message = "La date de début doit être dans le futur")
        LocalDateTime dateDebut,

        @NotNull(message = "La date de fin est obligatoire")
        LocalDateTime dateFin
) {
}
