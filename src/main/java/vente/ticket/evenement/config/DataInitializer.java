package vente.ticket.evenement.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vente.ticket.evenement.entity.Role;
import vente.ticket.evenement.entity.Utilisateur;
import vente.ticket.evenement.repository.UtilisateurRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!utilisateurRepository.existsByEmail("admin@evenement.local")) {
            utilisateurRepository.save(Utilisateur.builder()
                    .nom("Administrateur")
                    .email("admin@evenement.local")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .actif(true)
                    .build());
        }
    }
}
