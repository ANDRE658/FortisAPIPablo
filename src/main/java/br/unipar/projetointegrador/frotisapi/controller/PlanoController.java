package br.unipar.projetointegrador.frotisapi.controller;

import br.unipar.projetointegrador.frotisapi.model.Plano;
import br.unipar.projetointegrador.frotisapi.service.PlanoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*") // Permite acesso do frontend
@RestController
@RequestMapping("/plano")
public class PlanoController {

    private final PlanoService planoService;

    public PlanoController(PlanoService planoService) {
        this.planoService = planoService;
    }

    @GetMapping("/listar-todos")
    public ResponseEntity<List<Plano>> listarTodosParaRelatorio() {
        return ResponseEntity.ok(planoService.listarTodosParaRelatorio());
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Plano>> listarPlanos() {
        List<Plano> planos = planoService.listarTodos();
        if (planos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(planos);
    }

    @PostMapping("/salvar")
    public ResponseEntity<Plano> salvarPlano(@RequestBody Plano plano) {
        Plano planoSalvo = planoService.salvar(plano);
        return ResponseEntity.status(HttpStatus.CREATED).body(planoSalvo);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Plano> buscarPlanoPorID(@PathVariable Long id) {
        Plano plano = planoService.buscarPorId(id);
        if (plano == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(plano);
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarPlano(@PathVariable Long id) {
        planoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // --- MÉTODO ATUALIZAR QUE FALTAVA ---
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Plano> atualizarPlano(@PathVariable Long id, @RequestBody Plano planoAtualizado) {
        // Esta chamada agora vai funcionar, pois o PlanoService (Correção 2)
        // tem o método (Long, Plano)
        Plano plano = planoService.atualizar(id, planoAtualizado);

        if (plano == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(plano);
    }
}