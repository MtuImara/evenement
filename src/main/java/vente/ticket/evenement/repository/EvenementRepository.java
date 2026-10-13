package vente.ticket.evenement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vente.ticket.evenement.entity.Evenement;
import vente.ticket.evenement.entity.StatutEvenement;

import java.util.List;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {
    List<Evenement> findByStatut(StatutEvenement statut);
    List<Evenement> findByOrganisateurId(Long organisateurId);
}
