package vente.ticket.evenement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vente.ticket.evenement.dto.scan.ScanRequest;
import vente.ticket.evenement.dto.scan.ScanResponse;
import vente.ticket.evenement.entity.*;
import vente.ticket.evenement.exception.ResourceNotFoundException;
import vente.ticket.evenement.repository.ScanLogRepository;
import vente.ticket.evenement.repository.TicketRepository;
import vente.ticket.evenement.repository.UtilisateurRepository;
import vente.ticket.evenement.security.SecurityUtils;
import vente.ticket.evenement.security.UserPrincipal;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ScanService {

    private final TicketRepository ticketRepository;
    private final ScanLogRepository scanLogRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final EvenementService evenementService;
    private final QrCodeService qrCodeService;

    @Transactional
    public ScanResponse scanner(ScanRequest request) {
        UserPrincipal current = SecurityUtils.currentUser();
        Utilisateur scanneur = utilisateurRepository.findById(current.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        Evenement evenement = evenementService.findEvenement(request.eventId());

        boolean admin = current.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!admin && !evenement.getOrganisateur().getId().equals(current.getId())) {
            return enregistrerEchec(null, evenement, scanneur, "Vous n'êtes pas autorisé à scanner pour cet événement");
        }

        Ticket ticket = ticketRepository.findByCodeUnique(request.code()).orElse(null);
        if (ticket == null) {
            return enregistrerEchec(null, evenement, scanneur, "Ticket introuvable");
        }

        if (!ticket.getEvenement().getId().equals(evenement.getId())) {
            return enregistrerEchec(ticket, evenement, scanneur, "Ticket non valide pour cet événement");
        }

        if (!qrCodeService.verify(
                ticket.getCodeUnique(),
                ticket.getEvenement().getId(),
                ticket.getParticipant().getId(),
                request.sig())) {
            return enregistrerEchec(ticket, evenement, scanneur, "Signature QR invalide (fraude suspectée)");
        }

        if (ticket.getStatut() == StatutTicket.UTILISE) {
            return enregistrerEchec(ticket, evenement, scanneur, "Ticket déjà utilisé (double scan)");
        }

        if (ticket.getStatut() == StatutTicket.ANNULE || ticket.getStatut() == StatutTicket.EXPIRE) {
            return enregistrerEchec(ticket, evenement, scanneur, "Ticket " + ticket.getStatut().name().toLowerCase());
        }

        if (LocalDateTime.now().isAfter(evenement.getDateFin())) {
            ticket.setStatut(StatutTicket.EXPIRE);
            return enregistrerEchec(ticket, evenement, scanneur, "Événement terminé, ticket expiré");
        }

        ticket.setStatut(StatutTicket.UTILISE);
        ticket.setDateScan(LocalDateTime.now());

        ScanLog log = ScanLog.builder()
                .succes(true)
                .ticket(ticket)
                .scanneur(scanneur)
                .evenement(evenement)
                .build();
        scanLogRepository.save(log);

        return new ScanResponse(true, "Accès autorisé", ticket.getId(), ticket.getParticipant().getNom());
    }

    private ScanResponse enregistrerEchec(Ticket ticket, Evenement evenement, Utilisateur scanneur, String motif) {
        ScanLog log = ScanLog.builder()
                .succes(false)
                .motifEchec(motif)
                .ticket(ticket)
                .scanneur(scanneur)
                .evenement(evenement)
                .build();
        scanLogRepository.save(log);
        return new ScanResponse(false, motif, ticket != null ? ticket.getId() : null,
                ticket != null ? ticket.getParticipant().getNom() : null);
    }
}
