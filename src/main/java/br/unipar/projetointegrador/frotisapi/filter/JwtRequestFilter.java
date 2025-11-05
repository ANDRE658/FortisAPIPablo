package br.unipar.projetointegrador.frotisapi.filter;

import br.unipar.projetointegrador.frotisapi.service.UserAuthService;
import br.unipar.projetointegrador.frotisapi.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserAuthService userDetailsService;

    public JwtRequestFilter(JwtUtil jwtUtil, UserAuthService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Log para ver o que o filtro está recebendo
        System.out.println("\n--- Filtro JWT Interceptou Requisição ---");
        System.out.println("URI: " + request.getRequestURI());
        System.out.println("Header 'Authorization': " + authorizationHeader);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
                System.out.println("CPF extraído do token: " + username);
            } catch (ExpiredJwtException e) {
                System.out.println("ERRO: O token JWT expirou.");
            } catch (Exception e) {
                System.out.println("ERRO: Falha ao processar o token: " + e.getMessage());
            }
        } else {
            System.out.println("Header 'Authorization' não encontrado ou mal formatado.");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("Validando token para o usuário: " + username);
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwt, userDetails)) {
                System.out.println("SUCESSO: Token é válido. Autenticando usuário.");
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                System.out.println("FALHA: Validação do token falhou.");
            }
        }

        chain.doFilter(request, response);
    }
}