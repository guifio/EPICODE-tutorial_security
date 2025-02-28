package com.example.tutorialSpringSecurity.controller;

import com.example.tutorialSpringSecurity.model.exception.DuplicateEmailException;
import com.example.tutorialSpringSecurity.model.exception.DuplicateUsernameException;
import com.example.tutorialSpringSecurity.model.exception.RuoloException;
import com.example.tutorialSpringSecurity.model.payload.UtenteDTO;
import com.example.tutorialSpringSecurity.model.payload.request.LoginRequest;
import com.example.tutorialSpringSecurity.model.payload.response.LoginResponse;
import com.example.tutorialSpringSecurity.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/utente")
public class UtenteController {

    @Autowired
    UtenteService service;

    @PostMapping("/insert")
    public ResponseEntity<String> insertUtente(@Validated @RequestBody UtenteDTO nuovoUtente, BindingResult checkValidazione){

        if(checkValidazione.hasErrors()){
            StringBuilder erroriValidazione = new StringBuilder("Problemi nella validazione\n");
            for(ObjectError errore : checkValidazione.getAllErrors()){
                erroriValidazione.append(errore.getDefaultMessage());
            }

            return new ResponseEntity<>(erroriValidazione.toString(), HttpStatus.BAD_REQUEST);
        }


        try {
            String messaggio = service.insertUtente(nuovoUtente);
            return new ResponseEntity<>(messaggio, HttpStatus.OK);
        } catch (DuplicateEmailException e) {
            return new ResponseEntity<>("Email già presente nel sistema", HttpStatus.NOT_ACCEPTABLE);
        } catch (DuplicateUsernameException e) {
            return new ResponseEntity<>("Username già presente nel sistema", HttpStatus.NOT_ACCEPTABLE);
        } catch (RuoloException e) {
            return new ResponseEntity<>("Errore di gestione ruolo utente", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequest loginDTO, BindingResult checkValidazione){

        try {

            if (checkValidazione.hasErrors()) {
                StringBuilder erroriValidazione = new StringBuilder("Problemi nella validazione\n");
                for (ObjectError errore : checkValidazione.getAllErrors()) {
                    erroriValidazione.append(errore.getDefaultMessage());
                }

                return new ResponseEntity<>(erroriValidazione.toString(), HttpStatus.BAD_REQUEST);
            }


            LoginResponse response = service.login(loginDTO.getUsername(), loginDTO.getPassword());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Credenziali non valide", HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/hello")
    public ResponseEntity<String> helloProtected(){
        return new ResponseEntity<>("Benvenuto nel mondo protetto", HttpStatus.OK);
    }


}
