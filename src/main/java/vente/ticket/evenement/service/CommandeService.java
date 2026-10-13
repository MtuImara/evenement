package vente.ticket.evenement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vente.ticket.evenement.dto.commande.CommandeRequest;
import vente.ticket.evenement.dto.commande.CommandeResponse;
import vente.ticket.evenement.dto.ticket.TicketResponse;
import vente.ticket.evenement.entity.*;
import vente.ticket.evenement.exception.BusinessException;
import vente.ticket.evenement.exception.ResourceNotFoundException;
import vente.ticket.evenement.repository.CommandeRepository;
import vente.ticket.evenement.repository.TicketRepository;
import vente.ticket.evenement.repository.TypeTicketRepository;
import vente.ticket.evenement.repository.UtilisateurRepository;
import vente.ticket.evenement.security.SecurityUtils;
import vente.ticket.evenement.security.UserPrincipal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final TypeTicketRepository typeTicketRepository;
    private final TicketRepository ticketRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final QrCodeService qrCodeService;

    @Transactional
    public CommandeResponse acheter(CommandeRequest request) {
        UserPrincipal current = SecurityUtils.currentUser();
        Utilisateur participant = utilisateurRepository.findById(current.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Participant introuvable"));

        TypeTicket typeTicket = typeTicketRepository.findById(request.typeTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("Type de ticket introuvable"));

        Evenement evenement = typeTicket.getEvenement();
        if (evenement.getStatut() != StatutEvenement.PUBLIE) {
            throw new BusinessException("Cet événement n'est pas ouvert à la vente");
        }
        if (typeTicket.quantiteDisponible() < request.quantite()) {
            throw new BusinessException("Stock insuffisant");
        }

        BigDecimal montant = typeTicket.getPrix().multiply(BigDecimal.valueOf(request.quantite()));
        Commande commande = Commande.builder()
                .participant(participant)
                .montantTotal(montant)
                .statut(StatutCommande.PAYEE)
                .tickets(new ArrayList<>())
                .build();

        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < request.quantite(); i++) {
            String code = UUID.randomUUID().toString().replace("-", "");
            String signature = qrCodeService.sign(code, evenement.getId(), participant.getId());

            Ticket ticket = Ticket.builder()
                    .codeUnique(code)
                    .signatureHmac(signature)
                    .statut(StatutTicket.VALIDE)
                    .typeTicket(typeTicket)
                    .commande(commande)
                    .evenement(evenement)
                    .participant(participant)
                    .build();
            tickets.add(ticket);
        }

        commande.setTickets(tickets);
        typeTicket.setQuantiteVendue(typeTicket.getQuantiteVendue() + request.quantite());
        commandeRepository.save(commande);

        return toResponse(commande);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> mesTickets() {
        UserPrincipal current = SecurityUtils.currentUser();
        return ticketRepository.findByParticipantId(current.getId()).stream()
                .map(this::toTicketResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TicketResponse getTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket introuvable"));
        UserPrincipal current = SecurityUtils.currentUser();
        boolean admin = current.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!admin && !ticket.getParticipant().getId().equals(current.getId())) {
            throw new BusinessException("Ce ticket ne vous appartient pas");
        }
        return toTicketResponse(ticket);
    }

    private CommandeResponse toResponse(Commande commande) {
        return new CommandeResponse(
                commande.getId(),
                commande.getDateCommande(),
                commande.getStatut(),
                commande.getMontantTotal(),
                commande.getTickets().stream().map(this::toTicketResponse).toList()
        );
    }

    private TicketResponse toTicketResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getCodeUnique(),
                ticket.getSignatureHmac(),
                ticket.getStatut(),
                ticket.getDateScan(),
                ticket.getEvenement().getId(),
                ticket.getEvenement().getTitre(),
                ticket.getTypeTicket().getLibelle()
        );
    }
}
