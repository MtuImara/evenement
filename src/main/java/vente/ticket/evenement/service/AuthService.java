package vente.ticket.evenement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vente.ticket.evenement.dto.auth.AuthResponse;
import vente.ticket.evenement.dto.auth.LoginRequest;
import vente.ticket.evenement.dto.auth.RegisterRequest;
import vente.ticket.evenement.entity.Role;
import vente.ticket.evenement.entity.Utilisateur;
import vente.ticket.evenement.exception.BusinessException;
import vente.ticket.evenement.repository.UtilisateurRepository;
import vente.ticket.evenement.security.JwtService;
import vente.ticket.evenement.security.UserPrincipal;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (request.role() == Role.ADMIN) {
            throw new BusinessException("Impossible de s'inscrire en tant qu'ADMIN");
        }
        if (utilisateurRepository.existsByEmail(request.email())) {
            throw new BusinessException("Cet email est déjà utilisé");
        }

        Utilisateur utilisateur = Utilisateur.builder()
                .nom(request.nom())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .actif(true)
                .build();

        utilisateurRepository.save(utilisateur);
        UserPrincipal principal = new UserPrincipal(utilisateur);
        return new AuthResponse(
                jwtService.generateToken(principal),
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getEmail(),
                utilisateur.getRole()
        );
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("Identifiants invalides"));

        UserPrincipal principal = new UserPrincipal(utilisateur);
        return new AuthResponse(
                jwtService.generateToken(principal),
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getEmail(),
                utilisateur.getRole()
        );
    }
}
