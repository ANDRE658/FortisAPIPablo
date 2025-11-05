package br.unipar.projetointegrador.frotisapi.controller;

import br.unipar.projetointegrador.frotisapi.dto.ExercicioDTO;
import br.unipar.projetointegrador.frotisapi.service.ExercicioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*") // 2. ADICIONE ESTA LINHA

@RestController
@RequestMapping("/exercicio")
public class ExercicioController {

    private final ExercicioService exercicioService;

    public ExercicioController(ExercicioService exercicioService) {
        this.exercicioService = exercicioService;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarExercicio(@PathVariable Long id) {
        try {
            exercicioService.deleteById(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (Exception e) {
            // Se o ID não for encontrado (baseado na lógica do service)
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    /**
     * ATUALIZADO (Melhor Prática):
     * O Controller agora retorna uma Lista de DTOs.
     */
    @GetMapping("/listar")
    public ResponseEntity<List<ExercicioDTO>> listarExercicios() {
        return ResponseEntity.ok(exercicioService.listarTodos());
    }

    /**
     * ATUALIZADO (Melhor Prática):
     * O Controller agora recebe um DTO do cliente e já retorna o DTO
     * que o serviço preparou.
     */
    @PostMapping("/salvar/{treinoId}")
    public ResponseEntity<ExercicioDTO> salvarExercicio(
            @PathVariable Long treinoId,
            @RequestBody ExercicioDTO exercicioDTO) { // <-- Recebe DTO
        try {
            // O serviço agora recebe DTO e retorna DTO
            ExercicioDTO novoExercicioDTO = exercicioService.salvarExercicio(treinoId, exercicioDTO);
            return ResponseEntity.status(201).body(novoExercicioDTO); // 201 Created
        } catch (Exception e) {
            // Se o Treino não for encontrado
            return ResponseEntity.notFound().build(); // Retorna 404
        }
    }
}
