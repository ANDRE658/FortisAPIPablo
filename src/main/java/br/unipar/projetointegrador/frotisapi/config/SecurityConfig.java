package br.unipar.projetointegrador.frotisapi.config;

import br.unipar.projetointegrador.frotisapi.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Permite usar @PreAuthorize nos controllers (opcional)
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private UserAuthService userAuthService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desabilita CSRF para REST APIs
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sessão sem estado (stateless)
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Rotas Públicas:
                        .requestMatchers("/auth/**").permitAll() // Endpoints de login e registro
                        .requestMatchers("/consulta-cep/**").permitAll() // Sua rota de CEP é pública

                        // Rotas do Gerenciador (Exemplos):
                        .requestMatchers("/instrutor/**").hasRole("GERENCIADOR") // Só Gerenciador mexe em instrutor
                        .requestMatchers(HttpMethod.DELETE, "/aluno/**").hasRole("GERENCIADOR") // Só Gerenciador deleta aluno
                        .requestMatchers(HttpMethod.DELETE, "/plano/**").hasRole("GERENCIADOR") // Só Gerenciador deleta plano

                        // Rotas do Instrutor ou Gerenciador (Exemplos):
                        .requestMatchers("/aluno/**").hasAnyRole("INSTRUTOR", "GERENCIADOR") //
                        .requestMatchers("/treino/**").hasAnyRole("INSTRUTOR", "GERENCIADOR") //
                        .requestMatchers("/exercicio/**").hasAnyRole("INSTRUTOR", "GERENCIADOR") //
                        .requestMatchers("/plano/**").hasAnyRole("INSTRUTOR", "GERENCIADOR") //
                        .requestMatchers("/matricula/**").hasAnyRole("INSTRUTOR", "GERENCIADOR") //

                        // Restante:
                        .anyRequest().authenticated() // Todas as outras rotas exigem autenticação
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Adiciona o filtro JWT

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userAuthService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}