package vente.ticket.evenement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vente.ticket.evenement.dto.evenement.EvenementRequest;
import vente.ticket.evenement.dto.evenement.EvenementResponse;
import vente.ticket.evenement.dto.ticket.TypeTicketRequest;
import vente.ticket.evenement.dto.ticket.TypeTicketResponse;
import vente.ticket.evenement.entity.*;
import vente.ticket.evenement.exception.BusinessException;
import vente.ticket.evenement.exception.ResourceNotFoundException;
import vente.ticket.evenement.repository.EvenementRepository;
import vente.ticket.evenement.repository.TypeTicketRepository;
import vente.ticket.evenement.repository.UtilisateurRepository;
import vente.ticket.evenement.security.SecurityUtils;
import vente.ticket.evenement.security.UserPrincipal;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvenementService {

    private final EvenementRepository evenementRepository;
    private final TypeTicketRepository typeTicketRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Transactional(readOnly = true)
    public List<EvenementResponse> listerPublies() {
        return evenementRepository.findByStatut(StatutEvenement.PUBLIE).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EvenementResponse> listerMesEvenements() {
        UserPrincipal current = SecurityUtils.currentUser();
        return evenementRepository.findByOrganisateurId(current.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EvenementResponse getById(Long id) {
        return toResponse(findEvenement(id));
    }

    @Transactional
    public EvenementResponse creer(EvenementRequest request) {
        UserPrincipal current = SecurityUtils.currentUser();
        Utilisateur organisateur = utilisateurRepository.findById(current.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Organisateur introuvable"));

        Evenement evenement = Evenement.builder()
                .titre(request.titre())
                .description(request.description())
                .lieu(request.lieu())
                .dateDebut(request.dateDebut())
                .dateFin(request.dateFin())
                .statut(StatutEvenement.BROUILLON)
                .organisateur(organisateur)
                .build();

        return toResponse(evenementRepository.save(evenement));
    }

    @Transactional
    public EvenementResponse modifier(Long id, EvenementRequest request) {
        Evenement evenement = findEvenement(id);
        verifierProprietaireOuAdmin(evenement);

        evenement.setTitre(request.titre());
        evenement.setDescription(request.description());
        evenement.setLieu(request.lieu());
        evenement.setDateDebut(request.dateDebut());
        evenement.setDateFin(request.dateFin());

        return toResponse(evenement);
    }

    @Transactional
    public EvenementResponse publier(Long id) {
        Evenement evenement = findEvenement(id);
        verifierProprietaireOuAdmin(evenement);
        if (evenement.getTypesTickets().isEmpty()) {
            throw new BusinessException("Ajoutez au moins un type de ticket avant publication");
        }
        evenement.setStatut(StatutEvenement.PUBLIE);
        return toResponse(evenement);
    }

    @Transactional
    public void supprimer(Long id) {
        Evenement evenement = findEvenement(id);
        verifierProprietaireOuAdmin(evenement);
        evenementRepository.delete(evenement);
    }

    @Transactional
    public TypeTicketResponse ajouterTypeTicket(Long evenementId, TypeTicketRequest request) {
        Evenement evenement = findEvenement(evenementId);
        verifierProprietaireOuAdmin(evenement);

        TypeTicket typeTicket = TypeTicket.builder()
                .libelle(request.libelle())
                .prix(request.prix())
                .quantiteTotale(request.quantiteTotale())
                .quantiteVendue(0)
                .evenement(evenement)
                .build();

        typeTicketRepository.save(typeTicket);
        return toTypeResponse(typeTicket);
    }

    @Transactional(readOnly = true)
    public List<TypeTicketResponse> listerTypes(Long evenementId) {
        findEvenement(evenementId);
        return typeTicketRepository.findByEvenementId(evenementId).stream()
                .map(this::toTypeResponse)
                .toList();
    }

    public Evenement findEvenement(Long id) {
        return evenementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Événement introuvable: " + id));
    }

    private void verifierProprietaireOuAdmin(Evenement evenement) {
        UserPrincipal current = SecurityUtils.currentUser();
        boolean admin = current.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!admin && !evenement.getOrganisateur().getId().equals(current.getId())) {
            throw new BusinessException("Vous n'êtes pas propriétaire de cet événement");
        }
    }

    private EvenementResponse toResponse(Evenement e) {
        return new EvenementResponse(
                e.getId(),
                e.getTitre(),
                e.getDescription(),
                e.getLieu(),
                e.getDateDebut(),
                e.getDateFin(),
                e.getStatut(),
                e.getOrganisateur().getId(),
                e.getOrganisateur().getNom()
        );
    }

    private TypeTicketResponse toTypeResponse(TypeTicket t) {
        return new TypeTicketResponse(
                t.getId(),
                t.getLibelle(),
                t.getPrix(),
                t.getQuantiteTotale(),
                t.getQuantiteVendue(),
                t.quantiteDisponible(),
                t.getEvenement().getId()
        );
    }
}
