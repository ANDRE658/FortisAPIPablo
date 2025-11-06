package br.unipar.projetointegrador.frotisapi.service;

import br.unipar.projetointegrador.frotisapi.model.Aluno;
import br.unipar.projetointegrador.frotisapi.model.Role;
import br.unipar.projetointegrador.frotisapi.model.Usuario;
import br.unipar.projetointegrador.frotisapi.repository.AlunoRepository;
import br.unipar.projetointegrador.frotisapi.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 👇 **** 1. IMPORTS ADICIONAIS ****
import br.unipar.projetointegrador.frotisapi.model.Matricula;
import br.unipar.projetointegrador.frotisapi.model.Plano;
import br.unipar.projetointegrador.frotisapi.repository.MatriculaRepository;
import br.unipar.projetointegrador.frotisapi.repository.PlanoRepository;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // 👇 **** 2. DECLARAÇÃO DOS CAMPOS (REPOSITÓRIOS) ****
    private final MatriculaRepository matriculaRepository;
    private final PlanoRepository planoRepository;

    @Autowired
    public AlunoService(AlunoRepository alunoRepository,
                        UsuarioRepository usuarioRepository,
                        PasswordEncoder passwordEncoder,
                        // 👇 **** 3. ATUALIZAÇÃO DO CONSTRUTOR ****
                        MatriculaRepository matriculaRepository,
                        PlanoRepository planoRepository) {
        this.alunoRepository = alunoRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.matriculaRepository = matriculaRepository; // O Spring injeta aqui
        this.planoRepository = planoRepository;         // E aqui
    }

    /**
     * Agora este método salva o Aluno, o Usuário e a Matrícula.
     * @Transactional garante que ou os três são salvos, ou nenhum é.
     */
    @Transactional
    // 👇 **** 4. ATUALIZAÇÃO DO MÉTODO 'salvar' ****
    public Aluno salvar(Aluno aluno, String senha, Long planoId) throws Exception {

        // 1. Salva o Aluno
        Aluno alunoSalvo = alunoRepository.save(aluno);

        // 2. Cria o Usuário
        if (senha == null || senha.isEmpty()) {
            throw new IllegalArgumentException("A senha é obrigatória.");
        }
        Usuario novoUsuario = new Usuario();
        novoUsuario.setLogin(aluno.getEmail());
        novoUsuario.setSenha(passwordEncoder.encode(senha));
        novoUsuario.setRole(Role.ROLE_ALUNO);
        usuarioRepository.save(novoUsuario);

        // 3. Cria a Matrícula
        if (planoId != null) {
            // Busca o plano no banco (AGORA O 'planoRepository' EXISTE)
            Plano planoSelecionado = planoRepository.findById(planoId)
                    .orElseThrow(() -> new Exception("Plano com ID " + planoId + " não encontrado."));

            Matricula novaMatricula = new Matricula();
            novaMatricula.setAluno(alunoSalvo);
            novaMatricula.setPlano(planoSelecionado);

            matriculaRepository.save(novaMatricula);
        } else {
            throw new IllegalArgumentException("O plano é obrigatório.");
        }

        return alunoSalvo;
    }

    public Aluno buscarPorId(Long id) {
        return alunoRepository.findById(id).orElse(null);
    }

    public void deletar(Long id) {
        alunoRepository.deleteById(id);
    }

    public List<Aluno> listarTodos() {
        return alunoRepository.findAllWithMatriculasAndPlanos(); // Para isto
    }//    }

    // Este método 'atualizar' ainda não mexe na matrícula,
    // mas a lógica de 'salvar' (novo cadastro) está completa.
    // (Ele foi corrigido nas etapas anteriores para salvar os campos do aluno)
    @Transactional
    public Aluno atualizar(Long id, Aluno alunoAtualizado) {
        Aluno alunoExistente = buscarPorId(id);

        if (alunoExistente != null) {
            alunoExistente.setNome(alunoAtualizado.getNome());
            alunoExistente.setEmail(alunoAtualizado.getEmail());
            alunoExistente.setTelefone(alunoAtualizado.getTelefone());
            alunoExistente.setSexo(alunoAtualizado.getSexo());
            alunoExistente.setDataNascimento(alunoAtualizado.getDataNascimento());
            alunoExistente.setAltura(alunoAtualizado.getAltura());
            alunoExistente.setPeso(alunoAtualizado.getPeso());

            if (alunoExistente.getEndereco() != null && alunoAtualizado.getEndereco() != null) {
                // (Lógica do endereço...)
            } else if (alunoAtualizado.getEndereco() != null) {
                alunoExistente.setEndereco(alunoAtualizado.getEndereco());
            }

            return alunoRepository.save(alunoExistente);
        } else {
            return null;
        }
    }
}