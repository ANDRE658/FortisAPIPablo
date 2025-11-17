package br.unipar.projetointegrador.frotisapi.controller;

import br.unipar.projetointegrador.frotisapi.model.Mensalidade;
import br.unipar.projetointegrador.frotisapi.service.MensalidadeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/mensalidade")
public class MensalidadeController {

    private final MensalidadeService mensalidadeService;

    public MensalidadeController(MensalidadeService mensalidadeService) {
        this.mensalidadeService = mensalidadeService;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Mensalidade>> listar() {
        return ResponseEntity.ok(mensalidadeService.listarTodas());
    }

    @PostMapping("/pagar/{id}")
    public ResponseEntity<Mensalidade> pagar(@PathVariable Long id) {
        Mensalidade m = mensalidadeService.pagar(id);
        return m != null ? ResponseEntity.ok(m) : ResponseEntity.notFound().build();
    }

    @PostMapping("/gerar-agora")
    public ResponseEntity<String> gerarManual() {
        mensalidadeService.forcarGeracao();
        return ResponseEntity.ok("Mensalidades geradas/verificadas com sucesso.");
    }
}