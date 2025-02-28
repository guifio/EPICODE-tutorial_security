package com.example.tutorialSpringSecurity.security;

import com.example.tutorialSpringSecurity.model.entities.Utente;
import com.example.tutorialSpringSecurity.model.exception.CreateTokenException;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    // possono essere inserite anche nel properties
    private final String JWTSECRET ="a09c50b504d4e92b5e445fbcae55eb7fe36353c3ad798c87e7c6eb9398675407e590b823625ebd58a60b27fbd667b2277c5ffdc33cb658c3337c920aff16190bfa6f7767ebd09ec8b0d1fdb539f3f0f99b3f87eab963bfefff3f96d06be0cf742e4408f89f36ab082f693275c68e911891c05540e9b54a0e80e2bff5d03ef0f91ac5b0038b8a458d86f58aff075169b37a7c320a64932cd2b473d8c932102a628b9eaa24e099df3cc13baa58dd654c9c2ef7246afcd15bbbe3bf6c16bd6ecb985c4b668fae3a0fc19c51b81b2eaeae64c47d8f2c6f816e1f252a2d5ac89c95e6352f46a828bac05dfed085768c1c82c62c579ba5001063181b4fc8bb34dfb235";
    private long scadenza = 15;
    private final String TOKEN_HEADER ="Authorization";
    private final String TOKEN_PREFIX ="Bearer ";

    // Oggetto che occorre per la validazione
    private final JwtParser JWTPARSER;

    public JwtUtil(){
        JWTPARSER = Jwts.parser().setSigningKey(JWTSECRET);
    }

    /**
     * Metodo di creazione Token.
     * Recupera le info da Utente e le inserisce nel Token finale in formato String
     * @param utente
     * @return il token in formato String
     */
    public String creaToken(Utente utente){
        // Impostazione del Claims (Payload)
        Claims claims = Jwts.claims().setSubject(utente.getUsername());
        claims.put("roles", utente.getRuolo());
        claims.put("firstname", utente.getNome());
        claims.put("lastname", utente.getCognome());
        Date dataCreazioneToken = new Date();
        Date dataScadenza = new Date(dataCreazioneToken.getTime() + TimeUnit.MINUTES.toMillis(scadenza));

        // CREAZIONE TOKEN : claims, data expiration, firma con tipologi algoritmo e la chiave
        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(dataScadenza)
                .signWith(SignatureAlgorithm.HS256, JWTSECRET)
                .compact();

        return token;
    }

    /**
     * Estrazione del TOKEN in arrivo all'interno della request
     * @param request
     * @return
     */
    public String recuperoToken(HttpServletRequest request) throws CreateTokenException {

        // Recupero dall'header della richiesta il token con prefisso
        String bearerToken = request.getHeader(TOKEN_HEADER);

        // Il token è presente? Inizia con "Bearer " ?
        if(bearerToken!=null && bearerToken.startsWith(TOKEN_PREFIX)){
            // Ritorna il token senza prefisso
            return bearerToken.substring(TOKEN_PREFIX.length());
        }

        throw new CreateTokenException();
    }

    /**
     * Metodo di validazione del Token.
     * Recupera il Token e ne estra solo il payload.
     * @param request
     * @return
     * @throws CreateTokenException
     */
    public Claims validaClaims(HttpServletRequest request) throws CreateTokenException {
        try {
            String token = recuperoToken(request);
            return JWTPARSER.parseClaimsJws(token).getBody();
        }catch(ExpiredJwtException ex){
            request.setAttribute("expired", ex.getMessage());
            throw ex;
        }catch(Exception ex){
            request.setAttribute("token invalido", ex.getMessage());
            throw ex;
        }
    }

    /**
     * Metodo che controlla la scadenza del Token
     * @param claims
     * @return
     */
    public boolean checkExpiration(Claims claims){
        try{
            return claims.getExpiration().after(new Date());
        }catch(Exception ex){
            throw ex;
        }

    }

}
