package br.unipar.projetointegrador.frotisapi.controller;

// Importações de Exercicio/ExercicioDTO foram removidas
import br.unipar.projetointegrador.frotisapi.model.Treino;
import br.unipar.projetointegrador.frotisapi.service.TreinoService;
import br.unipar.projetointegrador.frotisapi.dto.TreinoDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/treino")
public class TreinoController {

    private final TreinoService treinoService;

    public TreinoController(TreinoService treinoService) {
        this.treinoService = treinoService;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Treino>> listarTreinos() {
        // CORREÇÃO: Usar o Service para chamar o método que busca TUDO
        List<Treino> treinos = treinoService.findAll();
        return ResponseEntity.ok(treinos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TreinoDTO> getTreinoById(@PathVariable Long id) {
        TreinoDTO treinoDTO = treinoService.getTreinoCompletoDTO(id);
        if (treinoDTO != null) {
            return ResponseEntity.ok(treinoDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/salvar")
    public ResponseEntity<Treino> salvarTreino(@RequestBody Treino treino) {
        Treino treinoSalvo = treinoService.save(treino);
        return ResponseEntity.status(HttpStatus.CREATED).body(treinoSalvo);
    }

    @GetMapping("/hoje")
    public ResponseEntity<TreinoDTO> getTreinoDeHoje() {
        TreinoDTO treinoDTO = treinoService.buscarTreinoDeHoje();
        if (treinoDTO != null) {
            return ResponseEntity.ok(treinoDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTreino(@PathVariable Long id) {
        try {
            treinoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // O MÉTODO "salvarExercicio" FOI REMOVIDO DAQUI.
    // O erro "cannot find symbol: exercicioService" está corrigido.
}