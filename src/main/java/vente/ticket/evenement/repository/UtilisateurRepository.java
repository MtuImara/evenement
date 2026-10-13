package vente.ticket.evenement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vente.ticket.evenement.entity.Utilisateur;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByEmail(String email);
}
