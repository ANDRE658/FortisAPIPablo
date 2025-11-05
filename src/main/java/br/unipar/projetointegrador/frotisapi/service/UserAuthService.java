package br.unipar.projetointegrador.frotisapi.service;

import br.unipar.projetointegrador.frotisapi.repository.UsuarioRepository; // <-- CORRETO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserAuthService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository; // <-- DEVE USAR UsuarioRepository

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Deve buscar por 'findByLogin' no repositório de USUÁRIO
        return usuarioRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o login: " + username));
    }
}