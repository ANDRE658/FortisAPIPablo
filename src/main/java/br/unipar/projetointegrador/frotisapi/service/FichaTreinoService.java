// CRIE O SERVICE: .../service/FichaTreinoService.java
package br.unipar.projetointegrador.frotisapi.service;

// ... (Imports: FichaTreino, FichaTreinoRequestDTO, FichaTreinoRepository, AlunoRepository, InstrutorRepository, ...)
import br.unipar.projetointegrador.frotisapi.dto.FichaCompletaRequestDTO;
import br.unipar.projetointegrador.frotisapi.dto.FichaTreinoRequestDTO;
import br.unipar.projetointegrador.frotisapi.dto.ItemTreinoRequestDTO;
import br.unipar.projetointegrador.frotisapi.dto.TreinoCompletoDTO;
import br.unipar.projetointegrador.frotisapi.model.*;
import br.unipar.projetointegrador.frotisapi.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FichaTreinoService {

    private final FichaTreinoRepository fichaTreinoRepository;
    private final AlunoRepository alunoRepository;
    private InstrutorRepository instrutorRepository = null;
    private final TreinoRepository treinoRepository; // <-- 1. ADICIONE ESTE CAMPO
    private final ItemTreinoRepository itemTreinoRepository; // <-- NOVO
    private final ExercicioRepository exercicioRepository;

    public FichaTreinoService(FichaTreinoRepository fichaTreinoRepository,
                              AlunoRepository alunoRepository,
                              InstrutorRepository instrutorRepository, TreinoRepository treinoRepository, ItemTreinoRepository itemTreinoRepository, ExercicioRepository exercicioRepository) {
        this.fichaTreinoRepository = fichaTreinoRepository;
        this.alunoRepository = alunoRepository;
        this.instrutorRepository = instrutorRepository;
        this.treinoRepository = treinoRepository;
        this.itemTreinoRepository = itemTreinoRepository;
        this.exercicioRepository = exercicioRepository;
    }

    // Construtor com @Autowired...

    @Transactional
    public FichaTreino salvar(FichaTreinoRequestDTO dto) throws Exception {
        Aluno aluno = alunoRepository.findById(dto.getAlunoId())
                .orElseThrow(() -> new Exception("Aluno não encontrado"));
        Instrutor instrutor = instrutorRepository.findById(dto.getInstrutorId())
                .orElseThrow(() -> new Exception("Instrutor não encontrado"));

        FichaTreino ficha = new FichaTreino();
        ficha.setAluno(aluno);
        ficha.setInstrutor(instrutor);

        return fichaTreinoRepository.save(ficha);
    }

    @Transactional // Mantenha transacional
    public FichaTreino buscarCompletoPorId(Long id) {

        // Esta query agora vai funcionar, pois mudamos List para Set
        FichaTreino fichaCompleta = fichaTreinoRepository.findByIdCompleta(id).orElse(null);

        return fichaCompleta;
    }

    /**
     * MÉTODO CORRIGIDO: Monta o grafo de objetos na memória e salva tudo de uma vez (Cascade).
     */
    @Transactional
    public FichaTreino salvarFichaCompleta(FichaCompletaRequestDTO dto) throws Exception {

        // 1. Busca Aluno e Instrutor
        Aluno aluno = alunoRepository.findById(dto.getAlunoId())
                .orElseThrow(() -> new Exception("Aluno não encontrado"));
        Instrutor instrutor = instrutorRepository.findById(dto.getInstrutorId())
                .orElseThrow(() -> new Exception("Instrutor não encontrado"));

        // 2. Cria a "casca" da FichaTreino (SEM SALVAR AINDA)
        FichaTreino ficha = new FichaTreino();
        ficha.setAluno(aluno);
        ficha.setInstrutor(instrutor);

        // 3. Itera sobre os Dias de Treino e monta a estrutura
        for (TreinoCompletoDTO diaDTO : dto.getDiasDeTreino()) {

            Treino treino = new Treino();
            treino.setFichaTreino(ficha); // Linka com a Ficha
            treino.setDiaSemana(diaDTO.getDiaSemana());
            treino.setNome(diaDTO.getNome());

            // Itera sobre os Itens de Treino
            if (diaDTO.getItensTreino() != null) {
                for (ItemTreinoRequestDTO itemDTO : diaDTO.getItensTreino()) {
                    Exercicio exercicio = exercicioRepository.findById(itemDTO.getExercicioId())
                            .orElseThrow(() -> new Exception("Exercício ID " + itemDTO.getExercicioId() + " não encontrado"));

                    ItemTreino item = new ItemTreino();
                    item.setTreino(treino); // Linka com o Treino (Dia)
                    item.setExercicio(exercicio);
                    item.setSeries(itemDTO.getSeries());
                    item.setRepeticoes(itemDTO.getRepeticoes());
                    item.setCarga(itemDTO.getCarga());
                    item.setTempoDescansoSegundos(itemDTO.getTempoDescansoSegundos());

                    // IMPORTANTE: Adiciona o item na lista do Treino
                    treino.getItensTreino().add(item);
                }
            }

            // IMPORTANTE: Adiciona o treino na lista da Ficha
            ficha.getDiasDeTreino().add(treino);
        }

        // 4. Salva TUDO de uma vez.
        // O CascadeType.ALL na entidade FichaTreino fará o trabalho de salvar os Treinos e Itens.
        return fichaTreinoRepository.save(ficha);
    }

    /**
     * NOVO MÉTODO: Atualiza uma ficha completa.
     * Ele usa a estratégia "delete-then-recreate" (apaga os filhos e recria)
     * graças ao 'orphanRemoval=true' nas entidades.
     */
    @Transactional
    public FichaTreino atualizarFichaCompleta(Long idFicha, FichaCompletaRequestDTO dto) throws Exception {

        // 1. Busca a Ficha (usando a nova query que FORÇA o carregamento de diasDeTreino)
        FichaTreino ficha = fichaTreinoRepository.findByIdWithDiasDeTreino(idFicha)
                .orElseThrow(() -> new Exception("Ficha ID " + idFicha + " não encontrada"));

        Aluno aluno = alunoRepository.findById(dto.getAlunoId())
                .orElseThrow(() -> new Exception("Aluno ID " + dto.getAlunoId() + " não encontrado"));

        // 2. Atualiza os dados da Ficha (ex: se o aluno mudou)
        ficha.setAluno(aluno);

        // 3. LIMPA a lista de dias de treino antiga.
        // O 'orphanRemoval=true' no model FichaTreino fará o Hibernate deletar
        // todos os Treinos (e seus Itens) antigos associados a esta ficha.
        ficha.getDiasDeTreino().clear();

        // 4. Reconstrói a lista de dias de treino (CORRIGIDO)
        for (TreinoCompletoDTO diaDTO : dto.getDiasDeTreino()) {

            // (O loop interno duplicado foi removido)

            Treino treino = new Treino();
            treino.setFichaTreino(ficha);
            treino.setDiaSemana(diaDTO.getDiaSemana());
            treino.setNome(diaDTO.getNome());

            if (diaDTO.getItensTreino() != null) { // Proteção extra
                for (ItemTreinoRequestDTO itemDTO : diaDTO.getItensTreino()) {
                    Exercicio exercicio = exercicioRepository.findById(itemDTO.getExercicioId())
                            .orElseThrow(() -> new Exception("Exercício ID " + itemDTO.getExercicioId() + " não encontrado"));

                    ItemTreino item = new ItemTreino();
                    item.setTreino(treino);
                    item.setExercicio(exercicio);
                    item.setSeries(itemDTO.getSeries());
                    item.setRepeticoes(itemDTO.getRepeticoes());
                    item.setCarga(itemDTO.getCarga());
                    item.setTempoDescansoSegundos(itemDTO.getTempoDescansoSegundos());

                    // Se a lista 'itensTreino' em Treino.java não foi inicializada (ex: = new ArrayList<>())
                    // você pode ter um NullPointerException aqui. (Veja recomendação abaixo)
                    treino.getItensTreino().add(item); // Adiciona o item ao dia
                }
            }
            ficha.getDiasDeTreino().add(treino); // Adiciona o dia à ficha
        }

        // 5. Salva a ficha DEPOIS que o loop terminar
        return fichaTreinoRepository.save(ficha);
    }

    public List<FichaTreino> listarTodas(Usuario usuarioLogado) {

        // Se for GERENCIADOR, vê tudo
        if (usuarioLogado.getRole() == Role.ROLE_GERENCIADOR) {
            return fichaTreinoRepository.findAllComAlunos();
        }

        // Se for ALUNO, vê APENAS A SUA ficha
        else if (usuarioLogado.getRole() == Role.ROLE_ALUNO) {
            if (usuarioLogado.getAluno() == null) return List.of();

            // Filtra a lista completa para achar a do aluno (solução rápida sem mudar o Repository agora)
            return fichaTreinoRepository.findAllComAlunos().stream()
                    .filter(f -> f.getAluno().getId().equals(usuarioLogado.getAluno().getId()))
                    .collect(Collectors.toList());
        }

        // SE FOR INSTRUTOR, retorna apenas as suas
        else if (usuarioLogado.getRole() == Role.ROLE_INSTRUTOR) {
            if (usuarioLogado.getInstrutor() == null) {
                return List.of();
            }
            Long instrutorId = usuarioLogado.getInstrutor().getId();
            return fichaTreinoRepository.findAllByInstrutorId(instrutorId);
        }

        return List.of();
    }
}