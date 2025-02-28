package com.example.tutorialSpringSecurity.repository;

import com.example.tutorialSpringSecurity.model.entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtenteRepository extends JpaRepository<Utente, Long> {

    // RECUPERO UTENTE DA USERNAME
    Optional<Utente> findByUsername(String username);

    // CHECK LOGIN
    public boolean existsByUsernameAndPassword(String username, String password);

    // CHECK CHIAVI DUPLICATE IN FASE DI REGISTRAZIONE
    public boolean existsByUsername(String username);
    public boolean existsByEmail(String email);

}
