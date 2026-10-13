package vente.ticket.evenement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vente.ticket.evenement.dto.commande.CommandeRequest;
import vente.ticket.evenement.dto.commande.CommandeResponse;
import vente.ticket.evenement.service.CommandeService;

@RestController
@RequestMapping("/api/commandes")
@RequiredArgsConstructor
public class CommandeController {

    private final CommandeService commandeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('PARTICIPANT','ADMIN')")
    public CommandeResponse acheter(@Valid @RequestBody CommandeRequest request) {
        return commandeService.acheter(request);
    }
}
