package br.unipar.projetointegrador.frotisapi.controller;

import br.unipar.projetointegrador.frotisapi.dto.AlterarSenhaRequestDTO;
import br.unipar.projetointegrador.frotisapi.dto.auth.AuthRequestDTO;
import br.unipar.projetointegrador.frotisapi.dto.auth.AuthResponseDTO;
import br.unipar.projetointegrador.frotisapi.dto.auth.RegistroRequestDTO;
import br.unipar.projetointegrador.frotisapi.model.Instrutor;
import br.unipar.projetointegrador.frotisapi.model.Usuario;
import br.unipar.projetointegrador.frotisapi.repository.InstrutorRepository;
import br.unipar.projetointegrador.frotisapi.repository.UsuarioRepository;
import br.unipar.projetointegrador.frotisapi.service.JwtService;
import br.unipar.projetointegrador.frotisapi.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private InstrutorRepository instrutorRepository; // Injete se for associar

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserAuthService userAuthService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO authDTO) {
        // O AuthenticationManager usa o UserAuthService para validar o usuário
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authDTO.getLogin(), authDTO.getSenha())
        );

        // Se autenticado, gera o token
        var usuario = (Usuario) authentication.getPrincipal();
        String token = jwtService.generateToken(usuario);

        return ResponseEntity.ok(new AuthResponseDTO(token));
    }

    /**
     * Endpoint para registrar novos usuários.
     * EM PRODUÇÃO: Este endpoint DEVE ser protegido e acessível apenas por um GERENCIADOR.
     * (Você pode fazer isso no SecurityConfig: .requestMatchers("/auth/registrar").hasRole("GERENCIADOR"))
     */
    @PostMapping("/registrar")
    public ResponseEntity<String> registrar(@RequestBody RegistroRequestDTO registroDTO) {
        if (usuarioRepository.findByLogin(registroDTO.getLogin()).isPresent()) {
            return ResponseEntity.badRequest().body("Erro: Login já está em uso!");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setLogin(registroDTO.getLogin());
        novoUsuario.setSenha(passwordEncoder.encode(registroDTO.getSenha())); // Sempre codifique a senha!
        novoUsuario.setRole(registroDTO.getRole());

        // Se o ID do instrutor foi fornecido, associa o usuário a ele
        if (registroDTO.getInstrutorId() != null) {
            Instrutor instrutor = instrutorRepository.findById(registroDTO.getInstrutorId())
                    .orElseThrow(() -> new RuntimeException("Instrutor não encontrado"));
            novoUsuario.setInstrutor(instrutor);
        }

        usuarioRepository.save(novoUsuario);
        return ResponseEntity.ok("Usuário registrado com sucesso!");
    }

    // --- ADICIONE ESTE NOVO ENDPOINT ---
    @PostMapping("/alterar-senha")
    public ResponseEntity<String> alterarSenha(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestBody AlterarSenhaRequestDTO dto) {

        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado.");
        }

        try {
            userAuthService.alterarSenha(usuarioLogado, dto.getSenhaAtual(), dto.getNovaSenha());
            return ResponseEntity.ok("Senha alterada com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}