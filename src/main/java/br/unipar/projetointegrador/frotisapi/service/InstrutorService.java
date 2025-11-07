package br.unipar.projetointegrador.frotisapi.service;

import br.unipar.projetointegrador.frotisapi.dto.InstrutorListDTO;
import br.unipar.projetointegrador.frotisapi.dto.InstrutorRequestDTO; // 👈 NOVO
import br.unipar.projetointegrador.frotisapi.dto.InstrutorResponseDTO;
import br.unipar.projetointegrador.frotisapi.model.Instrutor;
import br.unipar.projetointegrador.frotisapi.model.Role; // 👈 NOVO
import br.unipar.projetointegrador.frotisapi.model.Usuario; // 👈 NOVO
import br.unipar.projetointegrador.frotisapi.repository.InstrutorRepository;
import br.unipar.projetointegrador.frotisapi.repository.UsuarioRepository; // 👈 NOVO
import org.springframework.beans.factory.annotation.Autowired; // 👈 NOVO
import org.springframework.security.crypto.password.PasswordEncoder; // 👈 NOVO
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 👈 NOVO

import java.util.List;
import java.util.stream.Collectors;

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

    // 👇 **** ATUALIZE ESTE MÉTODO ****
    public InstrutorResponseDTO buscarPorId(Long id) {
        Instrutor instrutor = instrutorRepository.findById(id).orElse(null);
        if (instrutor != null) {
            return new InstrutorResponseDTO(instrutor); // Retorna o DTO
        }
        return null; // Retorna null se não encontrar
    }

    // 👇 **** ADICIONE ESTE MÉTODO NOVO ****
    @Transactional
    public Instrutor atualizar(Long id, InstrutorRequestDTO dto) {
        Instrutor instrutorExistente = instrutorRepository.findById(id).orElse(null);

        if (instrutorExistente == null) {
            return null; // Ou lançar uma exceção
        }

        // Atualiza os dados do instrutor com base no DTO
        // (Não atualizamos CPF nem senha aqui)
        instrutorExistente.setNome(dto.getNome());
        instrutorExistente.setEmail(dto.getEmail());
        instrutorExistente.setDataNascimento(dto.getDataNascimento());
        instrutorExistente.setTelefone(dto.getTelefone());
        instrutorExistente.setSexo(dto.getSexo());

        // Atualiza o endereço (se ele existir)
        if (dto.getEndereco() != null) {
            if (instrutorExistente.getEndereco() != null) {
                // Atualiza o endereço existente
                instrutorExistente.getEndereco().setCep(dto.getEndereco().getCep());
                instrutorExistente.getEndereco().setRua(dto.getEndereco().getRua());
                instrutorExistente.getEndereco().setCidade(dto.getEndereco().getCidade());
                instrutorExistente.getEndereco().setEstado(dto.getEndereco().getEstado());
                // (Note: O Endereco não tem "bairro" no Model)
            } else {
                // Cria um novo endereço se não existia
                instrutorExistente.setEndereco(dto.getEndereco());
            }
        }

        return instrutorRepository.save(instrutorExistente);
    }

    public void deletar(Long id) {
        instrutorRepository.deleteById(id);
    }

    public List<InstrutorListDTO> listarTodos() {
        List<Instrutor> instrutores = instrutorRepository.findAll();

        // Converte a lista de Entidades (Instrutor) para DTOs (InstrutorListDTO)
        return instrutores.stream()
                .map(InstrutorListDTO::new)
                .collect(Collectors.toList());
    }
}