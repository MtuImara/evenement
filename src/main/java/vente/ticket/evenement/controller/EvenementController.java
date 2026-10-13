package vente.ticket.evenement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vente.ticket.evenement.dto.evenement.EvenementRequest;
import vente.ticket.evenement.dto.evenement.EvenementResponse;
import vente.ticket.evenement.dto.ticket.TypeTicketRequest;
import vente.ticket.evenement.dto.ticket.TypeTicketResponse;
import vente.ticket.evenement.service.EvenementService;

import java.util.List;

@RestController
@RequestMapping("/api/evenements")
@RequiredArgsConstructor
public class EvenementController {

    private final EvenementService evenementService;

    @GetMapping
    public List<EvenementResponse> listerPublies() {
        return evenementService.listerPublies();
    }

    @GetMapping("/mes-evenements")
    @PreAuthorize("hasAnyRole('ORGANISATEUR','ADMIN')")
    public List<EvenementResponse> mesEvenements() {
        return evenementService.listerMesEvenements();
    }

    @GetMapping("/{id}")
    public EvenementResponse getById(@PathVariable Long id) {
        return evenementService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ORGANISATEUR','ADMIN')")
    public EvenementResponse creer(@Valid @RequestBody EvenementRequest request) {
        return evenementService.creer(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANISATEUR','ADMIN')")
    public EvenementResponse modifier(@PathVariable Long id, @Valid @RequestBody EvenementRequest request) {
        return evenementService.modifier(id, request);
    }

    @PostMapping("/{id}/publier")
    @PreAuthorize("hasAnyRole('ORGANISATEUR','ADMIN')")
    public EvenementResponse publier(@PathVariable Long id) {
        return evenementService.publier(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ORGANISATEUR','ADMIN')")
    public void supprimer(@PathVariable Long id) {
        evenementService.supprimer(id);
    }

    @PostMapping("/{id}/types-tickets")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ORGANISATEUR','ADMIN')")
    public TypeTicketResponse ajouterType(
            @PathVariable Long id,
            @Valid @RequestBody TypeTicketRequest request) {
        return evenementService.ajouterTypeTicket(id, request);
    }

    @GetMapping("/{id}/types-tickets")
    public List<TypeTicketResponse> listerTypes(@PathVariable Long id) {
        return evenementService.listerTypes(id);
    }
}
