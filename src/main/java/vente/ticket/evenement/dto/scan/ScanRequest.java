package vente.ticket.evenement.dto.scan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ScanRequest(
        @NotBlank(message = "Le code du ticket est obligatoire")
        String code,

        @NotBlank(message = "La signature est obligatoire")
        String sig,

        @NotNull(message = "L'identifiant de l'événement est obligatoire")
        Long eventId
) {
}
