package br.unipar.projetointegrador.frotisapi.controller;

import br.unipar.projetointegrador.frotisapi.dto.InstrutorRequestDTO;
import br.unipar.projetointegrador.frotisapi.model.Instrutor;
import br.unipar.projetointegrador.frotisapi.service.InstrutorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*") // Permite acesso do frontend
@RestController
@RequestMapping("/instrutor")
public class InstrutorController {

    private final InstrutorService instrutorService;

    public InstrutorController(InstrutorService instrutorService) {
        this.instrutorService = instrutorService;
    }

    @PostMapping("/salvar")
    public ResponseEntity<Instrutor> salvarInstrutor(@RequestBody InstrutorRequestDTO dto) {
        try {
            Instrutor instrutorSalvo = instrutorService.salvar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(instrutorSalvo);
        } catch (IllegalArgumentException e) {
            // Retorna 400 Bad Request se a senha estiver faltando
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            // Outros erros (ex: email/cpf duplicado)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}