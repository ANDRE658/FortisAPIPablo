package br.unipar.projetointegrador.frotisapi.service;

import br.unipar.projetointegrador.frotisapi.dto.AlunoRequestDTO;
import br.unipar.projetointegrador.frotisapi.dto.DashboardStatsDTO;
import br.unipar.projetointegrador.frotisapi.model.Aluno;
import br.unipar.projetointegrador.frotisapi.model.Role;
import br.unipar.projetointegrador.frotisapi.model.Usuario;
import br.unipar.projetointegrador.frotisapi.repository.AlunoRepository;
import br.unipar.projetointegrador.frotisapi.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
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

    public Aluno buscarPorId(Long id) {
        // MUDANÇA AQUI: Usa o método que carrega Plano e Endereço junto
        return alunoRepository.findByIdWithMatriculas(id).orElse(null);
    }

    // Método renomeado/atualizado para EXCLUIR logicamente
    public void excluir(Long id) throws Exception {
        Aluno aluno = alunoRepository.findById(id).orElse(null);

        if (aluno == null) {
            throw new Exception("Aluno não encontrado.");
        }

        aluno.setAtivo(false); // Marca como inativo
        alunoRepository.save(aluno); // Salva a alteração
    }

    public List<Aluno> listarTodos() {
        return alunoRepository.findAllWithMatriculasAndPlanos(); // Para isto
    }//    }



    public DashboardStatsDTO buscarEstatisticas() {
        // 1. Busca totais de ativos e inativos
        long ativos = alunoRepository.countByAtivoTrue();
        long inativos = alunoRepository.countByAtivoFalse();

        // 2. Calcula a data de 30 dias atrás
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Date trintaDiasAtras = cal.getTime();

        // 3. Busca novos alunos
        long novos = alunoRepository.countByDataCadastroAfter(trintaDiasAtras);

        return new DashboardStatsDTO(ativos, inativos, novos);
    }

    @Transactional
    public Aluno salvar(Aluno aluno, String senha, Long planoId) throws Exception {

        // --- 1. Limpeza de dados (Remove formatação, mantendo apenas números) ---
        if (aluno.getCpf() != null) {
            aluno.setCpf(aluno.getCpf().replaceAll("[^0-9]", ""));
        }
        if (aluno.getTelefone() != null) {
            aluno.setTelefone(aluno.getTelefone().replaceAll("[^0-9]", ""));
        }

        // --- 2. Validação de Unicidade (Verifica se já existe no banco) ---
        validarUnicidade(aluno);

        // --- 3. Salva o Aluno ---
        Aluno alunoSalvo = alunoRepository.save(aluno);

        // --- 4. Cria o Usuário de Acesso (Login) ---
        if (senha == null || senha.isEmpty()) {
            throw new IllegalArgumentException("A senha é obrigatória.");
        }
        Usuario novoUsuario = new Usuario();
        novoUsuario.setLogin(aluno.getEmail()); // O login é o email
        novoUsuario.setSenha(passwordEncoder.encode(senha));
        novoUsuario.setRole(Role.ROLE_ALUNO);

        // Se você tiver adicionado um campo 'aluno' na entidade Usuario, descomente abaixo:
        // novoUsuario.setAluno(alunoSalvo);

        usuarioRepository.save(novoUsuario);

        // --- 5. Cria a Matrícula (Vínculo com o Plano) ---
        if (planoId != null) {
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

    @Transactional
    public Aluno atualizar(Long id, AlunoRequestDTO dto) throws Exception {
        // 1. Busca o aluno existente (com matrículas, graças à mudança anterior)
        Aluno alunoExistente = buscarPorId(id);

        if (alunoExistente == null) {
            return null;
        }

        // 2. Converte o DTO para pegar os dados novos (CPF, Nome, etc.)
        // Podemos usar o toEntity() para criar um objeto temporário e copiar os dados
        Aluno dadosNovos = dto.toEntity();

        // 3. Limpeza e Validação (Igual fizemos antes)
        if (dadosNovos.getCpf() != null) {
            dadosNovos.setCpf(dadosNovos.getCpf().replaceAll("[^0-9]", ""));
        }
        if (dadosNovos.getTelefone() != null) {
            dadosNovos.setTelefone(dadosNovos.getTelefone().replaceAll("[^0-9]", ""));
        }

        validarUnicidadeNaAtualizacao(dadosNovos, id);

        // 4. Atualiza os dados básicos
        alunoExistente.setNome(dadosNovos.getNome());
        alunoExistente.setCpf(dadosNovos.getCpf());
        alunoExistente.setEmail(dadosNovos.getEmail());
        alunoExistente.setTelefone(dadosNovos.getTelefone());
        alunoExistente.setSexo(dadosNovos.getSexo());
        alunoExistente.setDataNascimento(dadosNovos.getDataNascimento());
        alunoExistente.setAltura(dadosNovos.getAltura());
        alunoExistente.setPeso(dadosNovos.getPeso());

        // 5. Atualiza o Endereço
        if (alunoExistente.getEndereco() != null && dadosNovos.getEndereco() != null) {
            alunoExistente.getEndereco().setRua(dadosNovos.getEndereco().getRua());
            alunoExistente.getEndereco().setCidade(dadosNovos.getEndereco().getCidade());
            alunoExistente.getEndereco().setEstado(dadosNovos.getEndereco().getEstado());
            alunoExistente.getEndereco().setCep(dadosNovos.getEndereco().getCep());
            alunoExistente.getEndereco().setBairro(dadosNovos.getEndereco().getBairro());
        } else if (dadosNovos.getEndereco() != null) {
            alunoExistente.setEndereco(dadosNovos.getEndereco());
        }

        // --- 6. ATUALIZA O PLANO (LÓGICA NOVA) ---
        if (dto.getPlanoId() != null) {
            // Busca o novo plano no banco
            Plano novoPlano = planoRepository.findById(dto.getPlanoId())
                    .orElseThrow(() -> new Exception("Plano com ID " + dto.getPlanoId() + " não encontrado."));

            // Verifica se o aluno já tem matrícula
            if (alunoExistente.getMatriculaList() != null && !alunoExistente.getMatriculaList().isEmpty()) {
                // Pega a primeira matrícula (assumindo 1 por aluno) e atualiza o plano
                Matricula matriculaAtual = alunoExistente.getMatriculaList().get(0);
                matriculaAtual.setPlano(novoPlano);

                // Opcional: Se quiser salvar explicitamente, embora o Cascade já resolva
                matriculaRepository.save(matriculaAtual);
            } else {
                // Se por algum motivo o aluno estava sem matrícula, cria uma nova
                Matricula novaMatricula = new Matricula();
                novaMatricula.setAluno(alunoExistente);
                novaMatricula.setPlano(novoPlano);
                matriculaRepository.save(novaMatricula);
            }
        }
        // -----------------------------------------

        return alunoRepository.save(alunoExistente);
    }

    // --- MÉTODOS AUXILIARES DE VALIDAÇÃO ---

    private void validarUnicidade(Aluno aluno) {
        if (alunoRepository.findByCpf(aluno.getCpf()).isPresent()) {
            throw new IllegalArgumentException("Já existe um aluno cadastrado com este CPF.");
        }
        if (alunoRepository.findByEmail(aluno.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Já existe um aluno cadastrado com este E-mail.");
        }
        if (alunoRepository.findByTelefone(aluno.getTelefone()).isPresent()) {
            throw new IllegalArgumentException("Já existe um aluno cadastrado com este Telefone.");
        }
    }

    private void validarUnicidadeNaAtualizacao(Aluno novosDados, Long idDoAluno) {
        // Verifica CPF
        var alunoComCpf = alunoRepository.findByCpf(novosDados.getCpf());
        if (alunoComCpf.isPresent() && !alunoComCpf.get().getId().equals(idDoAluno)) {
            throw new IllegalArgumentException("Este CPF já pertence a outro aluno.");
        }

        // Verifica Email
        var alunoComEmail = alunoRepository.findByEmail(novosDados.getEmail());
        if (alunoComEmail.isPresent() && !alunoComEmail.get().getId().equals(idDoAluno)) {
            throw new IllegalArgumentException("Este E-mail já pertence a outro aluno.");
        }

        // Verifica Telefone
        var alunoComTel = alunoRepository.findByTelefone(novosDados.getTelefone());
        if (alunoComTel.isPresent() && !alunoComTel.get().getId().equals(idDoAluno)) {
            throw new IllegalArgumentException("Este Telefone já pertence a outro aluno.");
        }
    }
}