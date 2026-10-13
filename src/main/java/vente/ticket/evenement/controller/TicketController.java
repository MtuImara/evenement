package vente.ticket.evenement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vente.ticket.evenement.dto.ticket.TicketResponse;
import vente.ticket.evenement.service.CommandeService;
import vente.ticket.evenement.service.QrCodeService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final CommandeService commandeService;
    private final QrCodeService qrCodeService;

    @GetMapping("/mes-tickets")
    @PreAuthorize("hasAnyRole('PARTICIPANT','ADMIN')")
    public List<TicketResponse> mesTickets() {
        return commandeService.mesTickets();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public TicketResponse getTicket(@PathVariable Long id) {
        return commandeService.getTicket(id);
    }

    @GetMapping("/{id}/qrcode")
    @PreAuthorize("hasAnyRole('PARTICIPANT','ADMIN')")
    public Map<String, String> getQrCode(@PathVariable Long id) {
        TicketResponse ticket = commandeService.getTicket(id);
        String qrBase64 = qrCodeService.generateQrBase64(
                ticket.codeUnique(),
                ticket.evenementId(),
                ticket.signatureHmac()
        );
        return Map.of(
                "ticketId", String.valueOf(ticket.id()),
                "qrCodeBase64", qrBase64,
                "payload", "{\"code\":\"" + ticket.codeUnique()
                        + "\",\"eventId\":" + ticket.evenementId()
                        + ",\"sig\":\"" + ticket.signatureHmac() + "\"}"
        );
    }
}
