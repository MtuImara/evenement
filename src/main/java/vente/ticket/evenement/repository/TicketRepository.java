package vente.ticket.evenement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vente.ticket.evenement.entity.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByCodeUnique(String codeUnique);
    List<Ticket> findByParticipantId(Long participantId);
    long countByEvenementId(Long evenementId);
    long countByStatut(vente.ticket.evenement.entity.StatutTicket statut);
}
