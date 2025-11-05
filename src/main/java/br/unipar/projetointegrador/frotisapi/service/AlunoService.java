package br.unipar.projetointegrador.frotisapi.service;

import br.unipar.projetointegrador.frotisapi.model.Aluno;
import br.unipar.projetointegrador.frotisapi.model.Role;
import br.unipar.projetointegrador.frotisapi.model.Usuario;
import br.unipar.projetointegrador.frotisapi.repository.AlunoRepository;
import br.unipar.projetointegrador.frotisapi.repository.UsuarioRepository; // 1. IMPORTE
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // 2. IMPORTE
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 3. IMPORTE

import java.util.List;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final UsuarioRepository usuarioRepository;     // 4. INJETE
    private final PasswordEncoder passwordEncoder;     // 5. INJETE

    @Autowired
    public AlunoService(AlunoRepository alunoRepository,
                        UsuarioRepository usuarioRepository,
                        PasswordEncoder passwordEncoder) { // 6. ATUALIZE O CONSTRUTOR
        this.alunoRepository = alunoRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Agora este método salva o Aluno E cria um Usuário para ele.
     * @Transactional garante que ou os dois são salvos, ou nenhum é.
     */
    @Transactional
    public Aluno salvar(Aluno aluno, String senha) { // 7. PEÇA A SENHA

        // 8. Salva o Aluno primeiro (sem a senha)
        // O modelo Aluno.java não deve ter o campo "senha"
        Aluno alunoSalvo = alunoRepository.save(aluno);

        // 9. Verifica se a senha foi fornecida
        if (senha == null || senha.isEmpty()) {
            throw new IllegalArgumentException("A senha é obrigatória para criar o usuário do aluno.");
        }

        // 10. Cria a entidade Usuario
        Usuario novoUsuario = new Usuario();
        novoUsuario.setLogin(aluno.getEmail()); // Usa o email do aluno como login
        novoUsuario.setSenha(passwordEncoder.encode(senha)); // Codifica a senha
        novoUsuario.setRole(Role.ROLE_ALUNO); // Define o papel
        // novoUsuario.setAluno(alunoSalvo); // Descomente se você adicionou o @OneToOne no Usuario.java

        usuarioRepository.save(novoUsuario);

        return alunoSalvo;
    }

    public Aluno buscarPorId(Long id) {
        return alunoRepository.findById(id).orElse(null);
    }

    public void deletar(Long id) {
        // (Lógica futura: talvez deletar o 'Usuario' associado também)
        alunoRepository.deleteById(id);
    }

    public List<Aluno> listarTodos() {
        return alunoRepository.findAll();
    }

    public Aluno atualizar(Long id, Aluno alunoAtualizado) {
        Aluno alunoExistente = buscarPorId(id);

        if (alunoExistente != null) {
            alunoExistente.setNome(alunoAtualizado.getNome());
            alunoExistente.setEmail(alunoAtualizado.getEmail());
            alunoExistente.setTelefone(alunoAtualizado.getTelefone());
            // (Lógica futura: talvez atualizar o login no 'Usuario' se o email mudar)
            return alunoRepository.save(alunoExistente);
        } else {
            return null;
        }
    }



}