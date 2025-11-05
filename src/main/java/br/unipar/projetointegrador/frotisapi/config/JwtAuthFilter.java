package br.unipar.projetointegrador.frotisapi.config;

// ... (imports)
import br.unipar.projetointegrador.frotisapi.service.JwtService;
import br.unipar.projetointegrador.frotisapi.service.UserAuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserAuthService userAuthService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userLogin;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7); // Remove "Bearer "

        // --- INÍCIO DA CORREÇÃO (BLOCO TRY...CATCH) ---
        try {
            userLogin = jwtService.extractUsername(jwt);

            if (userLogin != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userAuthService.loadUserByUsername(userLogin);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            // Se o token for válido, continua
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // Se o token for inválIDO (expirado, assinatura errada, etc.)
            System.err.println("ERRO: Falha ao processar o token (FILTRO ATUALIZADO): " + e.getMessage());

            // Retorna um erro 403 (Forbidden) e NÃO continua o filtro
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        // --- FIM DA CORREÇÃO ---
    }
}