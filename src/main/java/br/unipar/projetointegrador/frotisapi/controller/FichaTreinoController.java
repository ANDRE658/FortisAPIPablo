// CRIE O CONTROLLER: .../controller/FichaTreinoController.java
package br.unipar.projetointegrador.frotisapi.controller;

// ... (Imports: FichaTreino, FichaTreinoRequestDTO, FichaTreinoService, ...)
import br.unipar.projetointegrador.frotisapi.dto.FichaTreinoRequestDTO;
import br.unipar.projetointegrador.frotisapi.model.FichaTreino;
import br.unipar.projetointegrador.frotisapi.service.FichaTreinoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/ficha-treino")
public class FichaTreinoController {

    private final FichaTreinoService fichaTreinoService;

    public FichaTreinoController(FichaTreinoService fichaTreinoService) {
        this.fichaTreinoService = fichaTreinoService;
    }

    // Construtor com @Autowired...

    @PostMapping("/salvar")
    public ResponseEntity<FichaTreino> salvarFicha(@RequestBody FichaTreinoRequestDTO dto) {
        try {
            FichaTreino fichaSalva = fichaTreinoService.salvar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(fichaSalva);
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Se Aluno ou Instrutor não existirem
        }
    }
    @GetMapping("/buscar/{id}")
    public ResponseEntity<FichaTreino> buscarFichaCompleta(@PathVariable Long id) {
        FichaTreino ficha = fichaTreinoService.buscarCompletoPorId(id);
        if (ficha == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ficha);
    }
    @GetMapping("/listar")
    public ResponseEntity<List<FichaTreino>> listarFichas() {
        List<FichaTreino> fichas = fichaTreinoService.listarTodas();
        if (fichas.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 se a lista for vazia
        }
        return ResponseEntity.ok(fichas);
    }
}