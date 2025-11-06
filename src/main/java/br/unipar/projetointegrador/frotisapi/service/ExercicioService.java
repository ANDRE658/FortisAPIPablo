package br.unipar.projetointegrador.frotisapi.service;

import br.unipar.projetointegrador.frotisapi.dto.ExercicioDTO;
import br.unipar.projetointegrador.frotisapi.model.Exercicio;
// Remova as importações de Treino e TreinoRepository
import br.unipar.projetointegrador.frotisapi.repository.ExercicioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExercicioService {

    private final ExercicioRepository exercicioRepository;

    // --- CORREÇÃO ---
    // Remova o TreinoRepository do construtor
    public ExercicioService(ExercicioRepository exercicioRepository) {
        this.exercicioRepository = exercicioRepository;
    }

    // OK: Lista o catálogo
    public List<ExercicioDTO> listarTodos() {
        List<Exercicio> entidades = exercicioRepository.findAll();
        return entidades.stream()
                .map(ExercicioDTO::new)
                .collect(Collectors.toList());
    }

    // OK: Deleta do catálogo
    public void deleteById(Long id) {
        if (!exercicioRepository.existsById(id)) {
            throw new RuntimeException("Exercício com ID " + id + " não encontrado.");
        }
        exercicioRepository.deleteById(id);
    }

    // --- CORREÇÃO ---
    // Remova o método 'salvarExercicio(Long treinoId, ...)'

    /**
     * Este é o método CORRETO para salvar um novo exercício no CATÁLOGO.
     * (Usado pelo CadastroExercicio.html)
     */
    public Exercicio salvarNovoExercicioNoCatalogo(ExercicioDTO dto) {
        Exercicio novoExercicio = new Exercicio();
        novoExercicio.setNome(dto.getNome()); // Salva apenas o nome
        return exercicioRepository.save(novoExercicio);
    }
}