package br.unipar.projetointegrador.frotisapi.service;

import br.unipar.projetointegrador.frotisapi.dto.ExercicioDTO;
import br.unipar.projetointegrador.frotisapi.model.Exercicio;
// Remova a importação de Treino
import br.unipar.projetointegrador.frotisapi.repository.ExercicioRepository;
import br.unipar.projetointegrador.frotisapi.repository.TreinoRepository; // Pode remover esta importação
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExercicioService {

    private final ExercicioRepository exercicioRepository;
    // Remova o TreinoRepository daqui
    // private final TreinoRepository treinoRepository;

    // Atualize o construtor
    public ExercicioService(ExercicioRepository exercicioRepository) {
        this.exercicioRepository = exercicioRepository;
        // this.treinoRepository = treinoRepository; // Remova esta linha
    }

    /**
     * OK: Este método lista o CATÁLOGO de exercícios. Está correto.
     */
    public List<ExercicioDTO> listarTodos() {
        List<Exercicio> entidades = exercicioRepository.findAll();

        return entidades.stream()
                .map(ExercicioDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * OK: Este método deleta um exercício do CATÁLOGO. Está correto.
     */
    public void deleteById(Long id) {
        if (!exercicioRepository.existsById(id)) {
            throw new RuntimeException("Exercício com ID " + id + " não encontrado.");
        }
        exercicioRepository.deleteById(id);
    }

    /**
     * Opcional: Adicione um método para salvar NOVOS exercícios
     * no CATÁLOGO (ex: "Rosca Martelo"), se precisar.
     */
    public Exercicio salvarNovoExercicioNoCatalogo(ExercicioDTO dto) {
        Exercicio novoExercicio = new Exercicio();
        novoExercicio.setNome(dto.getNome());
        // (Não definimos series/reps aqui, pois é um catálogo)
        return exercicioRepository.save(novoExercicio);
    }
}