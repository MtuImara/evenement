package vente.ticket.evenement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vente.ticket.evenement.entity.Commande;

import java.util.List;

public interface CommandeRepository extends JpaRepository<Commande, Long> {
    List<Commande> findByParticipantId(Long participantId);
}
