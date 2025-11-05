package br.unipar.projetointegrador.frotisapi.service;

import br.unipar.projetointegrador.frotisapi.dto.ExercicioDTO; // Importado
import br.unipar.projetointegrador.frotisapi.model.Exercicio;
import br.unipar.projetointegrador.frotisapi.model.Treino;
import br.unipar.projetointegrador.frotisapi.repository.ExercicioRepository;
import br.unipar.projetointegrador.frotisapi.repository.TreinoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors; // Importado

@Service
public class ExercicioService {

    private final ExercicioRepository exercicioRepository;
    private final TreinoRepository treinoRepository;

    public ExercicioService(ExercicioRepository exercicioRepository, TreinoRepository treinoRepository) {
        this.exercicioRepository = exercicioRepository;
        this.treinoRepository = treinoRepository;
    }

    /**
     * ATUALIZADO (Melhor Prática):
     * O Service agora retorna uma Lista de DTOs, não de Entidades.
     * Isso evita expor o modelo do banco e previne erros de Lazy Loading.
     */
    public List<ExercicioDTO> listarTodos() {
        List<Exercicio> entidades = exercicioRepository.findAll();

        // Converte a lista de Entidades para uma lista de DTOs
        return entidades.stream()
                .map(ExercicioDTO::new) // Chama o construtor ExercicioDTO(exercicio)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        // Adiciona verificação para evitar crash se o ID não existir
        if (!exercicioRepository.existsById(id)) {
            throw new RuntimeException("Exercício com ID " + id + " não encontrado.");
        }
        exercicioRepository.deleteById(id);
    }

    /**
     * ATUALIZADO (Melhor Prática):
     * O Service agora recebe o DTO do controller, converte para Entidade,
     * salva, e retorna o DTO da entidade salva.
     *
     * CORRIGIDO: Adicionada a linha setId(null)
     */
    public ExercicioDTO salvarExercicio(Long treinoId, ExercicioDTO exercicioDTO) throws Exception {
        // 1. Busca o "Treino pai"
        Treino treino = treinoRepository.findTreinoCompletoById(treinoId)
                .orElseThrow(() -> new Exception("Treino com ID " + treinoId + " não encontrado."));

        // 2. Converte o DTO para Entidade
        Exercicio exercicioParaSalvar = exercicioDTO.toEntity();

        // 3. (A CORREÇÃO!) Força o ID para nulo
        // Isso garante que o JPA faça um INSERT (novo) e não um UPDATE (merge)
        exercicioParaSalvar.setId(null);

        // 4. Vincula o exercício ao treino
        exercicioParaSalvar.setTreino(treino);

        // 5. Salva a nova Entidade (agora fará um INSERT)
        Exercicio exercicioSalvo = exercicioRepository.save(exercicioParaSalvar);

        // 6. Retorna o DTO da Entidade que foi salva (agora com ID)
        return new ExercicioDTO(exercicioSalvo);
    }
}