package br.unipar.projetointegrador.frotisapi.controller;

import br.unipar.projetointegrador.frotisapi.dto.ExercicioDTO;
import br.unipar.projetointegrador.frotisapi.model.Exercicio; // <-- IMPORTE O MODEL
import br.unipar.projetointegrador.frotisapi.service.ExercicioService;
import org.springframework.http.HttpStatus; // <-- IMPORTE O STATUS
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*")
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
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<ExercicioDTO>> listarExercicios() {
        return ResponseEntity.ok(exercicioService.listarTodos());
    }

    // --- INÍCIO DA CORREÇÃO ---
    // Este é o endpoint para 'CadastroExercicio.html'
    @PostMapping("/salvar")
    public ResponseEntity<Exercicio> salvarExercicioNoCatalogo(@RequestBody ExercicioDTO dto) {
        try {
            // Chama o método que salva SÓ o nome
            Exercicio novoExercicio = exercicioService.salvarNovoExercicioNoCatalogo(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoExercicio);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


}