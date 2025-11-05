package br.unipar.projetointegrador.frotisapi.controller;



import br.unipar.projetointegrador.frotisapi.model.Endereco;
import br.unipar.projetointegrador.frotisapi.service.EnderecoService;
import org.springframework.http.HttpStatus; // Importe o HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Importe o @RequestBody

import java.util.List;

@RestController
@RequestMapping("/endereco")
public class EnderecoController {

    private final EnderecoService enderecoService;

    public EnderecoController(EnderecoService enderecoService) {
        this.enderecoService = enderecoService;
    }


    @GetMapping("/listar")
    public ResponseEntity<List<Endereco>> listarEnderecos() {
        List<Endereco> enderecos = enderecoService.listarTodos();

        if (enderecos.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna status 204 No Content
        }
        return ResponseEntity.ok(enderecos); // Retorna status 200 OK com a lista no corpo
    }

    @PostMapping("/salvar")
    public ResponseEntity<Endereco> salvarEndereco(@RequestBody Endereco endereco) {
        Endereco enderecoSalvo = enderecoService.salvar(endereco);
        return ResponseEntity.status(HttpStatus.CREATED).body(enderecoSalvo); // Retorna status
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Endereco> buscarEnderecoPorID(@PathVariable Long id) {
        Endereco endereco = enderecoService.buscarPorId(id);
        if (endereco == null) {
            return ResponseEntity.notFound().build(); // Retorna status 404 Not Found
        }
        return ResponseEntity.ok(endereco); // Retorna status 200 OK com o endereço no corpo
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarEndereco(@PathVariable Long id) {
        enderecoService.deletar(id);
        return ResponseEntity.noContent().build(); // Retorna status 204 No Content
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Endereco> atualizarEndereco(@PathVariable Long id, @RequestBody Endereco enderecoAtualizado) {
        Endereco endereco = enderecoService.buscarPorId(id);
        if (endereco == null) {
            return ResponseEntity.notFound().build(); // Retorna status 404 Not Found
        }
        enderecoAtualizado.setId(id); // Garante que o ID do endereço atualizado seja o mesmo
        Endereco enderecoAtualizadoSalvo = enderecoService.salvar(enderecoAtualizado);
        return ResponseEntity.ok(enderecoAtualizadoSalvo); // Retorna status 200 OK
    }



}