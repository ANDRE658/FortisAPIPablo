package br.unipar.projetointegrador.frotisapi.service;

import br.unipar.projetointegrador.frotisapi.model.Usuario;
import br.unipar.projetointegrador.frotisapi.repository.UsuarioRepository; // <-- CORRETO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserAuthService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository; // <-- DEVE USAR UsuarioRepository

    // --- ADICIONE ESTE AUTOWIRED ---
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Deve buscar por 'findByLogin' no repositório de USUÁRIO
        return usuarioRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o login: " + username));
    }

    // --- ADICIONE ESTE NOVO MÉTODO ---
    public void alterarSenha(Usuario usuario, String senhaAtual, String novaSenha) throws Exception {
        // 1. Verifica se a senha atual está correta
        if (!passwordEncoder.matches(senhaAtual, usuario.getPassword())) {
            throw new Exception("A senha atual está incorreta.");
        }

        // 2. Valida a nova senha (ex: mínimo 6 caracteres)
        if (novaSenha == null || novaSenha.length() < 6) {
            throw new Exception("A nova senha deve ter pelo menos 6 caracteres.");
        }

        // 3. Codifica e salva a nova senha
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }
}