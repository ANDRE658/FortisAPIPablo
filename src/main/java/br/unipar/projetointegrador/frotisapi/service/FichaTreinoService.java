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

    @Transactional// É importante que seja transacional
    public FichaTreino buscarCompletoPorId(Long id) {

        // Passo 1: Busca a ficha e o primeiro "saco" (diasDeTreino)
        FichaTreino ficha = fichaTreinoRepository.findFichaComDiasById(id).orElse(null);

        // Se a ficha não for encontrada ou não tiver dias, podemos retornar
        if (ficha == null || ficha.getDiasDeTreino() == null || ficha.getDiasDeTreino().isEmpty()) {
            return ficha;
        }

        // Passo 2: Coleta os IDs dos dias de treino que encontramos
        List<Long> treinoIds = ficha.getDiasDeTreino().stream()
                .map(Treino::getId)
                .collect(Collectors.toList());

        // Passo 3: Busca o segundo "saco" (itensTreino) para todos os dias de uma vez
        List<Treino> diasComItensCarregados = treinoRepository.findTreinosCompletosByIds(treinoIds);

        // Passo 4: Define a lista de dias da ficha como a lista completa que acabamos de buscar
        ficha.setDiasDeTreino(diasComItensCarregados);

        return ficha; // Retorna a ficha agora "montada"
    }

    /**
     * NOVO MÉTODO: Salva a ficha, os dias e os itens, tudo em uma transação.
     */
    @Transactional
    public FichaTreino salvarFichaCompleta(FichaCompletaRequestDTO dto) throws Exception {

        // 1. Busca Aluno e Instrutor
        Aluno aluno = alunoRepository.findById(dto.getAlunoId())
                .orElseThrow(() -> new Exception("Aluno não encontrado"));
        Instrutor instrutor = instrutorRepository.findById(dto.getInstrutorId())
                .orElseThrow(() -> new Exception("Instrutor não encontrado"));

        // 2. Cria e Salva a "casca" da FichaTreino
        FichaTreino ficha = new FichaTreino();
        ficha.setAluno(aluno);
        ficha.setInstrutor(instrutor);
        FichaTreino fichaSalva = fichaTreinoRepository.save(ficha);

        // 3. Itera sobre os Dias de Treino (TreinoCompletoDTO)
        for (TreinoCompletoDTO diaDTO : dto.getDiasDeTreino()) {

            // 4. Cria e Salva a "casca" do Treino (Dia)
            Treino treino = new Treino();
            treino.setFichaTreino(fichaSalva); // Linka com a Ficha
            treino.setDiaSemana(diaDTO.getDiaSemana());
            treino.setNome(diaDTO.getNome());
            Treino treinoSalvo = treinoRepository.save(treino);

            // 5. Itera sobre os Itens de Treino (ItemTreinoRequestDTO)
            for (ItemTreinoRequestDTO itemDTO : diaDTO.getItensTreino()) {

                // 6. Busca o Exercicio (Ex: "Supino")
                Exercicio exercicio = exercicioRepository.findById(itemDTO.getExercicioId())
                        .orElseThrow(() -> new Exception("Exercício ID " + itemDTO.getExercicioId() + " não encontrado"));

                // 7. Cria e Salva o ItemTreino (Exercício do dia)
                ItemTreino item = new ItemTreino();
                item.setTreino(treinoSalvo); // Linka com o Treino (Dia)
                item.setExercicio(exercicio); // Linka com o Exercício

                item.setSeries(itemDTO.getSeries());
                item.setRepeticoes(itemDTO.getRepeticoes());
                item.setCarga(itemDTO.getCarga());
                item.setTempoDescansoSegundos(itemDTO.getTempoDescansoSegundos());

                itemTreinoRepository.save(item);
            }
        }

        // 8. Retorna a Ficha que foi salva
        return fichaSalva;
    }

    /**
     * NOVO MÉTODO: Atualiza uma ficha completa.
     * Ele usa a estratégia "delete-then-recreate" (apaga os filhos e recria)
     * graças ao 'orphanRemoval=true' nas entidades.
     */
    @Transactional
    public FichaTreino atualizarFichaCompleta(Long idFicha, FichaCompletaRequestDTO dto) throws Exception {


        // 1. Busca a Ficha (usando o findById padrão, que é LAZY)
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

        // 4. Reconstrói a lista de dias de treino (mesma lógica do 'salvar')
        for (TreinoCompletoDTO diaDTO : dto.getDiasDeTreino()) {
            for (TreinoCompletoDTO diaDTO : dto.getDiasDeTreino()) {
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

                        treino.getItensTreino().add(item); // Adiciona o item ao dia
                    }
                }
                ficha.getDiasDeTreino().add(treino); // Adiciona o dia à ficha
            }

            return fichaTreinoRepository.save(ficha);
        }
    }

    public List<FichaTreino> listarTodas(Usuario usuarioLogado) {

        // SE FOR GERENCIADOR OU ALUNO, retorna a lista completa
        // (O Aluno precisa da lista para filtrar no front-end e achar a si mesmo)
        if (usuarioLogado.getRole() == Role.ROLE_GERENCIADOR || usuarioLogado.getRole() == Role.ROLE_ALUNO) {
            return fichaTreinoRepository.findAllComAlunos();
        }

        // SE FOR INSTRUTOR, retorna apenas as suas
        else if (usuarioLogado.getRole() == Role.ROLE_INSTRUTOR) {
            if (usuarioLogado.getInstrutor() == null) {
                return List.of();
            }
            Long instrutorId = usuarioLogado.getInstrutor().getId();
            return fichaTreinoRepository.findAllByInstrutorId(instrutorId);
        }

        // Segurança: Se não for nenhuma das roles, não retorna nada
        return List.of();
    }
}