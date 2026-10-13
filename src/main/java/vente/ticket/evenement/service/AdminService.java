package vente.ticket.evenement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vente.ticket.evenement.dto.admin.StatistiquesResponse;
import vente.ticket.evenement.dto.admin.UtilisateurResponse;
import vente.ticket.evenement.entity.StatutTicket;
import vente.ticket.evenement.entity.Utilisateur;
import vente.ticket.evenement.exception.ResourceNotFoundException;
import vente.ticket.evenement.repository.EvenementRepository;
import vente.ticket.evenement.repository.TicketRepository;
import vente.ticket.evenement.repository.UtilisateurRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UtilisateurRepository utilisateurRepository;
    private final EvenementRepository evenementRepository;
    private final TicketRepository ticketRepository;

    @Transactional(readOnly = true)
    public List<UtilisateurResponse> listerUtilisateurs() {
        return utilisateurRepository.findAll().stream()
                .map(u -> new UtilisateurResponse(u.getId(), u.getNom(), u.getEmail(), u.getRole(), u.isActif()))
                .toList();
    }

    @Transactional
    public UtilisateurResponse changerStatut(Long id, boolean actif) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        utilisateur.setActif(actif);
        return new UtilisateurResponse(
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getEmail(),
                utilisateur.getRole(),
                utilisateur.isActif()
        );
    }

    @Transactional(readOnly = true)
    public StatistiquesResponse statistiques() {
        return new StatistiquesResponse(
                utilisateurRepository.count(),
                evenementRepository.count(),
                ticketRepository.count(),
                ticketRepository.countByStatut(StatutTicket.UTILISE),
                ticketRepository.countByStatut(StatutTicket.VALIDE)
        );
    }
}
