package com.example.tutorialSpringSecurity.model.payload;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UtenteDTO {

    private String nome;
    private String cognome;

    @NotBlank(message = "Il campo username è obbligatorio")
    @Size(min = 3, max = 20, message = "Il campo username deve contenere minimo 3 caratteri ed un massimo di 20")
    private String username;

    @NotBlank(message = "Il campo password è obbligatoria")
    @Size(min = 3, max = 20, message = "Il campo username deve contenere minimo 7 caratteri ed un massimo di 20")
    private String password;

    @NotBlank(message = "Il campo email è obbligatorio")
    @Email(message="Il formato della email non è corretto")
    private String email;

    private String ruolo;


}
