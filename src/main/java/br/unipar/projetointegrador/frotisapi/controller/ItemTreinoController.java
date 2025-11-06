package br.unipar.projetointegrador.frotisapi.controller;

import br.unipar.projetointegrador.frotisapi.dto.ItemTreinoRequestDTO;
import br.unipar.projetointegrador.frotisapi.model.ItemTreino;
import br.unipar.projetointegrador.frotisapi.service.ItemTreinoService; // Cuidado com o "Iten"
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/item-treino")
public class ItemTreinoController {

    private final ItemTreinoService itemTreinoService;

    public ItemTreinoController(ItemTreinoService itemTreinoService) {
        this.itemTreinoService = itemTreinoService;
    }

    @PostMapping("/salvar/{treinoId}")
    public ResponseEntity<ItemTreino> salvarItem(
            @PathVariable Long treinoId,
            @RequestBody ItemTreinoRequestDTO dto) {

        try {
            ItemTreino itemSalvo = itemTreinoService.salvar(treinoId, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(itemSalvo);
        } catch (Exception e) {
            // Se o Treino ou Exercicio não forem encontrados
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}