package br.unipar.projetointegrador.frotisapi.config;

import br.unipar.projetointegrador.frotisapi.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Permite usar @PreAuthorize nos controllers (opcional)
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desabilita CSRF para REST APIs
                .cors(Customizer.withDefaults()) // <--- ADICIONE ESTA LINHA
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sessão sem estado (stateless)
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Rotas Públicas:
                        .requestMatchers("/auth/**").permitAll() // Endpoints de login e registro
                        .requestMatchers("/consulta-cep/**").permitAll() // Sua rota de CEP é pública
                        .requestMatchers(HttpMethod.POST, "/instrutor/salvar").permitAll() // Permite o cadastro público
                        // --- REGRAS (MEUS DADOS) ---
                        // Qualquer usuário AUTENTICADO pode alterar a própria senha
                        .requestMatchers(HttpMethod.POST, "/auth/alterar-senha").authenticated()
                        // Um ALUNO pode ver/atualizar SEUS DADOS
                        .requestMatchers(HttpMethod.GET, "/aluno/me").hasRole("ALUNO")
                        .requestMatchers(HttpMethod.PUT, "/aluno/me").hasRole("ALUNO")
                        // Um INSTRUTOR pode ver/atualizar SEUS DADOS
                        .requestMatchers(HttpMethod.GET, "/instrutor/me").hasRole("INSTRUTOR")
                        .requestMatchers(HttpMethod.PUT, "/instrutor/me").hasRole("INSTRUTOR")

                        .requestMatchers(HttpMethod.GET, "/aluno/visualizar/**").hasAnyRole("INSTRUTOR", "GERENCIADOR")
                        .requestMatchers(HttpMethod.PUT, "/aluno/atualizar-peso/**").hasAnyRole("INSTRUTOR", "GERENCIADOR")

                        // Rotas do Gerenciador (Exemplos):
                        .requestMatchers(HttpMethod.DELETE, "/instrutor/**").hasRole("GERENCIADOR") // Só Gerenciador deleta
                        .requestMatchers(HttpMethod.DELETE, "/aluno/**").hasRole("GERENCIADOR") // Só Gerenciador deleta aluno
                        .requestMatchers(HttpMethod.DELETE, "/plano/**").hasRole("GERENCIADOR") // Só Gerenciador deleta plano

                        // Rotas do Instrutor ou Gerenciador (Exemplos):
                        .requestMatchers("/instrutor/**").hasAnyRole("INSTRUTOR", "GERENCIADOR") // Instrutor e Gerenciador podem listar/ver
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
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }



    // 👇 **** ADICIONE ESTE MÉTODO INTEIRO NO FINAL DA CLASSE ****
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); // Permite todas as origens (ex: seu frontend)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")); // Permite o PUT
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control")); // Permite o Authorization

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica a todos os caminhos
        return source;
    }
}