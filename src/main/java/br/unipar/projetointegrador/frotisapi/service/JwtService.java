package br.unipar.projetointegrador.frotisapi.service;

import br.unipar.projetointegrador.frotisapi.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // --- INÍCIO DA CORREÇÃO ---
    // Coloque a mesma chave que está no seu JwtUtil.java
    // Esta chave precisa ser decodificada de Base64
    private static final String SECRET_KEY = "Zm9ydGlzX3NlY3JldF9rZXlfc3VwZXJfc2VndXJhXzEyMzQ1X3ByZWNpc2Ffc2VyX211aXRvX2xvbmdhX3BhcmFfZnVuY2lvbmFyX2NvcnJldGFtZW50ZQ==";
    // A string acima é a sua chave do JwtUtil ("fortis_secret_key...") convertida para Base64, que é o que o método getSignInKey() espera.
    // --- FIM DA CORREÇÃO ---

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24 horas

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        // Adiciona os papéis (roles) como uma claim no token
        extraClaims.put("roles", userDetails.getAuthorities());

        // ----------------- ADICIONE ESTAS LINHAS -----------------
        // Converte UserDetails de volta para Usuario
        if (userDetails instanceof Usuario) {
            Usuario usuario = (Usuario) userDetails;
            // Adiciona o ID do Instrutor ao token (se ele existir)
            if (usuario.getInstrutor() != null) {
                extraClaims.put("instrutorId", usuario.getInstrutor().getId());
            }
        }
        // ----------------- FIM DA ADIÇÃO -----------------

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}