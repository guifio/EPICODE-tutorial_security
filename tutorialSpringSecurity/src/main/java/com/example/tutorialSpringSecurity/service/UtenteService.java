package com.example.tutorialSpringSecurity.service;

import com.example.tutorialSpringSecurity.model.entities.Utente;
import com.example.tutorialSpringSecurity.model.exception.DuplicateEmailException;
import com.example.tutorialSpringSecurity.model.exception.DuplicateUsernameException;
import com.example.tutorialSpringSecurity.model.exception.RuoloException;
import com.example.tutorialSpringSecurity.model.payload.UtenteDTO;
import com.example.tutorialSpringSecurity.model.payload.response.LoginResponse;
import com.example.tutorialSpringSecurity.repository.UtenteRepository;
import com.example.tutorialSpringSecurity.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UtenteService {

    @Autowired
    UtenteRepository repo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtil;

    public String insertUtente(UtenteDTO dto) throws DuplicateEmailException, DuplicateUsernameException, RuoloException {

        checkDuplicateKeys(dto.getUsername(), dto.getEmail());

        // Impostazione ruolo di default
        if(dto.getRuolo()==null){
            dto.setRuolo("USER");
        }else{
            throw new RuoloException();
        }

        String pwdEncoded = passwordEncoder.encode(dto.getPassword());
        Utente user = dto_entity(dto);
        user.setPassword(pwdEncoded);

        Utente userDb = repo.save(user);
        return "L'utente " +userDb.getUsername() +" è stato inserito correttamente nel sistema";
    }

    public LoginResponse login(String username, String password){

        // 1. AUTENTICAZIONE DELL'UTENTE IN FASE DI LOGIN
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        // 2. INSERIMENTO DELL'AUTENTICAZIONE UTENTE NEL CONTESTO DELLA SICUREZZA
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. RECUPERO RUOLI --> String
        String ruolo=null;
        for(Object role :authentication.getAuthorities()){
            ruolo=role.toString();
            break;
        }

        // 4. GENERO L'UTENTE
        Utente user = new Utente();
        user.setUsername(username);
        user.setRuolo(ruolo);

        // 5. GENERO IL TOKEN
        String token = jwtUtil.creaToken(user);

        // 6. CREO L'OGGETTO DI RISPOSTA AL CLIENT
        return new LoginResponse(username, token);
    }




    /**
     * Metodo che effettua un check sui campi unique nel database (username e email)
     * @param username
     * @param email
     * @throws DuplicateUsernameException
     * @throws DuplicateEmailException
     */
    public void checkDuplicateKeys(String username, String email) throws DuplicateUsernameException, DuplicateEmailException {

        if(repo.existsByUsername(username)){
            throw new DuplicateUsernameException();
        }

        if(repo.existsByEmail(email)){
            throw new DuplicateEmailException();
        }

    }


    /**
     * Metodi di utilities
     * Entity --> DTO
     * @param utente
     * @return
     */
    public UtenteDTO entity_dto(Utente utente){
        UtenteDTO dto = new UtenteDTO();
        dto.setRuolo(utente.getRuolo());
        dto.setNome(utente.getNome());
        dto.setCognome(utente.getCognome());
        dto.setEmail(utente.getEmail());
        dto.setUsername(utente.getUsername());
        dto.setPassword(utente.getPassword());

        return dto;
    }

    /**
     * Metodi di utilities
     * DTO --> Entity
     * @param dto
     * @return
     */
    public Utente dto_entity(UtenteDTO dto){
        Utente utente = new Utente();
        utente.setCognome(dto.getCognome());
        utente.setEmail(dto.getEmail());
        utente.setRuolo(dto.getRuolo());
        utente.setUsername(dto.getUsername());
        utente.setPassword(dto.getPassword());
        utente.setNome(dto.getNome());

        return utente;
    }


}
