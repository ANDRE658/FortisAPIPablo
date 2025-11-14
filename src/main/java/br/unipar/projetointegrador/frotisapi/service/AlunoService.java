package br.unipar.projetointegrador.frotisapi.service;

import br.unipar.projetointegrador.frotisapi.dto.AlunoRequestDTO;
import br.unipar.projetointegrador.frotisapi.dto.DashboardStatsDTO;
import br.unipar.projetointegrador.frotisapi.model.*;
import br.unipar.projetointegrador.frotisapi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // 👇 **** 2. DECLARAÇÃO DOS CAMPOS (REPOSITÓRIOS) ****
    private final MatriculaRepository matriculaRepository;
    private final PlanoRepository planoRepository;
    private final InstrutorRepository instrutorRepository;

    @Autowired
    public AlunoService(AlunoRepository alunoRepository,
                        UsuarioRepository usuarioRepository,
                        PasswordEncoder passwordEncoder,
                        // 👇 **** 3. ATUALIZAÇÃO DO CONSTRUTOR ****
                        MatriculaRepository matriculaRepository,
                        PlanoRepository planoRepository, InstrutorRepository instrutorRepository) {
        this.alunoRepository = alunoRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.matriculaRepository = matriculaRepository; // O Spring injeta aqui
        this.planoRepository = planoRepository;         // E aqui
        this.instrutorRepository = instrutorRepository;
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

    /**
     * Lista alunos baseado na role do usuário logado.
     * GERENCIADOR: Vê todos os alunos ativos.
     * INSTRUTOR: Vê apenas os seus alunos ativos.
     */
    public List<Aluno> listarTodos(Usuario usuarioLogado) {

        if (usuarioLogado.getRole() == Role.ROLE_GERENCIADOR) {
            // Gerenciador vê todos os alunos ativos
            return alunoRepository.findAllWithMatriculasAndPlanos();

        } else if (usuarioLogado.getRole() == Role.ROLE_INSTRUTOR) {
            // Instrutor vê apenas os seus
            if (usuarioLogado.getInstrutor() == null) {
                return List.of(); // Se o usuário instrutor não está linkado a uma entidade instrutor
            }
            Long instrutorId = usuarioLogado.getInstrutor().getId();
            return alunoRepository.findAllAtivosByInstrutorIdWithMatriculas(instrutorId);
        }

        // Alunos ou outros não devem ver a lista
        return List.of();
    }


    /**
     * NOVO MÉTODO: Busca estatísticas filtradas por Instrutor
         */
    public DashboardStatsDTO buscarEstatisticasInstrutor(Long instrutorId) {
        // 1. Busca totais (filtrados)
        long ativos = alunoRepository.countAtivosByInstrutorId(instrutorId);
        long inativos = alunoRepository.countInativosByInstrutorId(instrutorId);

        // 2. Calcula a data de 30 dias atrás
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Date trintaDiasAtras = cal.getTime();

        // 3. Busca novos alunos (filtrados)
        long novos = alunoRepository.countNovosByInstrutorId(instrutorId, trintaDiasAtras);

        return new DashboardStatsDTO(ativos, inativos, novos);
    }

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


    //Salva um novo Aluno, cria seu Usuário e sua Matrícula.

    @Transactional
    public Aluno salvar(Aluno aluno, String senha, Long planoId, Long instrutorId) throws Exception {

        // --- 1. Limpa formatação (mantém só números) ---
        if (aluno.getCpf() != null) {
            aluno.setCpf(aluno.getCpf().replaceAll("[^0-9]", ""));
        }
        if (aluno.getTelefone() != null) {
            aluno.setTelefone(aluno.getTelefone().replaceAll("[^0-9]", ""));
        }

        // --- 2. Validação de Unicidade (CPF, Email, Telefone) ---
        validarUnicidade(aluno);

        // --- 3. Salva o Aluno (Define Padrões) ---
        aluno.setAtivo(true);
        aluno.setDataCadastro(new Date()); // Define a data de cadastro
        Aluno alunoSalvo = alunoRepository.save(aluno);

        // --- 4. Cria o Usuário de Acesso (Login) ---
        if (senha == null || senha.isEmpty()) {
            throw new IllegalArgumentException("A senha é obrigatória.");
        }
        Usuario novoUsuario = new Usuario();
        novoUsuario.setLogin(aluno.getEmail());
        novoUsuario.setSenha(passwordEncoder.encode(senha));
        novoUsuario.setRole(Role.ROLE_ALUNO);
        novoUsuario.setAluno(alunoSalvo);
        // (Opcional: Se Usuario tiver link para Aluno, setar aqui)
        usuarioRepository.save(novoUsuario);

        // --- 5. Cria a Matrícula (Vínculo com Plano e Instrutor) ---
        if (planoId != null && instrutorId != null) {
            Plano planoSelecionado = planoRepository.findById(planoId)
                    .orElseThrow(() -> new Exception("Plano com ID " + planoId + " não encontrado."));

            Instrutor instrutorSelecionado = instrutorRepository.findById(instrutorId)
                    .orElseThrow(() -> new Exception("Instrutor com ID " + instrutorId + " não encontrado."));

            Matricula novaMatricula = new Matricula();
            novaMatricula.setAluno(alunoSalvo);
            novaMatricula.setPlano(planoSelecionado);
            novaMatricula.setInstrutor(instrutorSelecionado);

            matriculaRepository.save(novaMatricula);
        } else {
            throw new IllegalArgumentException("O plano e o instrutor responsável são obrigatórios.");
        }

        return alunoSalvo;
    }


    //Atualiza um Aluno, seu Endereço e sua Matrícula (Plano/Instrutor).
    @Transactional
    public Aluno atualizar(Long id, AlunoRequestDTO dto) throws Exception {

        Aluno alunoExistente = buscarPorId(id);
        if (alunoExistente == null) {
            throw new Exception("Aluno com ID " + id + " não encontrado.");
        }

        // Pega os dados novos do DTO
        Aluno dadosNovos = dto.toEntity();

        // --- 1. Limpa formatação dos dados novos ---
        if (dadosNovos.getCpf() != null) {
            dadosNovos.setCpf(dadosNovos.getCpf().replaceAll("[^0-9]", ""));
        }
        if (dadosNovos.getTelefone() != null) {
            dadosNovos.setTelefone(dadosNovos.getTelefone().replaceAll("[^0-9]", ""));
        }

        // --- 2. Valida unicidade (ignorando o ID atual) ---
        validarUnicidadeNaAtualizacao(dadosNovos, id);

        // --- 3. Atualiza dados básicos ---
        alunoExistente.setNome(dadosNovos.getNome());
        alunoExistente.setCpf(dadosNovos.getCpf());
        alunoExistente.setEmail(dadosNovos.getEmail());
        alunoExistente.setTelefone(dadosNovos.getTelefone());
        alunoExistente.setSexo(dadosNovos.getSexo());
        alunoExistente.setDataNascimento(dadosNovos.getDataNascimento());
        alunoExistente.setAltura(dadosNovos.getAltura());
        alunoExistente.setPeso(dadosNovos.getPeso());

        // --- 4. Atualiza Endereço ---
        if (alunoExistente.getEndereco() != null && dadosNovos.getEndereco() != null) {
            alunoExistente.getEndereco().setRua(dadosNovos.getEndereco().getRua());
            alunoExistente.getEndereco().setCidade(dadosNovos.getEndereco().getCidade());
            alunoExistente.getEndereco().setEstado(dadosNovos.getEndereco().getEstado());
            alunoExistente.getEndereco().setCep(dadosNovos.getEndereco().getCep());
            alunoExistente.getEndereco().setBairro(dadosNovos.getEndereco().getBairro());
            alunoExistente.getEndereco().setNumero(dadosNovos.getEndereco().getNumero());
        } else if (dadosNovos.getEndereco() != null) {
            alunoExistente.setEndereco(dadosNovos.getEndereco());
        }

        // --- 5. Atualiza Matrícula (Plano e Instrutor) ---
        if (dto.getPlanoId() != null && dto.getInstrutorId() != null) {
            Plano novoPlano = planoRepository.findById(dto.getPlanoId())
                    .orElseThrow(() -> new Exception("Plano com ID " + dto.getPlanoId() + " não encontrado."));

            Instrutor novoInstrutor = instrutorRepository.findById(dto.getInstrutorId())
                    .orElseThrow(() -> new Exception("Instrutor com ID " + dto.getInstrutorId() + " não encontrado."));

            Matricula matriculaAtual;
            if (alunoExistente.getMatriculaList() != null && !alunoExistente.getMatriculaList().isEmpty()) {
                matriculaAtual = alunoExistente.getMatriculaList().get(0);
            } else {
                matriculaAtual = new Matricula();
                matriculaAtual.setAluno(alunoExistente);
            }

            matriculaAtual.setPlano(novoPlano);
            matriculaAtual.setInstrutor(novoInstrutor);
            matriculaRepository.save(matriculaAtual);
        }

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