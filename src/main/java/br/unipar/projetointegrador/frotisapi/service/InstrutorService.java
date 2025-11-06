package br.unipar.projetointegrador.frotisapi.service;

import br.unipar.projetointegrador.frotisapi.dto.InstrutorRequestDTO; // 👈 NOVO
import br.unipar.projetointegrador.frotisapi.model.Instrutor;
import br.unipar.projetointegrador.frotisapi.model.Role; // 👈 NOVO
import br.unipar.projetointegrador.frotisapi.model.Usuario; // 👈 NOVO
import br.unipar.projetointegrador.frotisapi.repository.InstrutorRepository;
import br.unipar.projetointegrador.frotisapi.repository.UsuarioRepository; // 👈 NOVO
import org.springframework.beans.factory.annotation.Autowired; // 👈 NOVO
import org.springframework.security.crypto.password.PasswordEncoder; // 👈 NOVO
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 👈 NOVO

@Service
public class InstrutorService {

    private final InstrutorRepository instrutorRepository;
    private final UsuarioRepository usuarioRepository; // 👈 NOVO
    private final PasswordEncoder passwordEncoder; // 👈 NOVO

    @Autowired // 👈 ATUALIZADO
    public InstrutorService(InstrutorRepository instrutorRepository,
                            UsuarioRepository usuarioRepository,
                            PasswordEncoder passwordEncoder) {
        this.instrutorRepository = instrutorRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Salva um novo Instrutor e cria um Usuário associado a ele.
     */
    @Transactional // 👈 NOVO
    public Instrutor salvar(InstrutorRequestDTO dto) {
        Instrutor instrutor = dto.toEntity();

        // 1. Salva o Instrutor primeiro
        Instrutor instrutorSalvo = instrutorRepository.save(instrutor);

        // 2. Verifica se a senha foi fornecida
        if (dto.getSenha() == null || dto.getSenha().isEmpty()) {
            throw new IllegalArgumentException("A senha é obrigatória para criar o usuário do instrutor.");
        }

        // 3. Cria a entidade Usuario
        Usuario novoUsuario = new Usuario();
        novoUsuario.setLogin(instrutor.getEmail()); // Usa o email do instrutor como login
        novoUsuario.setSenha(passwordEncoder.encode(dto.getSenha())); // Codifica a senha
        novoUsuario.setRole(Role.ROLE_INSTRUTOR); // Define o papel
        novoUsuario.setInstrutor(instrutorSalvo); // Linka o usuário ao instrutor salvo

        usuarioRepository.save(novoUsuario);

        return instrutorSalvo;
    }

    public Instrutor buscarPorId(Long id) {
        return instrutorRepository.findById(id).orElse(null);
    }

    public void deletar(Long id) {
        instrutorRepository.deleteById(id);
    }
}