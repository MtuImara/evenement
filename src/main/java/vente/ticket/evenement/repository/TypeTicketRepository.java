package vente.ticket.evenement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vente.ticket.evenement.entity.TypeTicket;

import java.util.List;

public interface TypeTicketRepository extends JpaRepository<TypeTicket, Long> {
    List<TypeTicket> findByEvenementId(Long evenementId);
}
