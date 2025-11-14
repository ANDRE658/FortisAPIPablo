package br.unipar.projetointegrador.frotisapi.controller;

import br.unipar.projetointegrador.frotisapi.dto.InstrutorListDTO;
import br.unipar.projetointegrador.frotisapi.dto.InstrutorRequestDTO;
import br.unipar.projetointegrador.frotisapi.dto.InstrutorResponseDTO;
import br.unipar.projetointegrador.frotisapi.model.Instrutor;
import br.unipar.projetointegrador.frotisapi.model.Usuario;
import br.unipar.projetointegrador.frotisapi.service.InstrutorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/listar")
    public ResponseEntity<List<InstrutorListDTO>> listarInstrutores() {
        // Agora o service retorna o DTO, quebrando o loop
        List<InstrutorListDTO> instrutores = instrutorService.listarTodos();

        if (instrutores.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(instrutores);
    }

    // 👇 **** ADICIONE ESTES DOIS MÉTODOS NOVOS ****

    /**
     * Busca um instrutor por ID para preencher o formulário de edição
     */
    @GetMapping("/buscar/{id}")
    public ResponseEntity<InstrutorResponseDTO> buscarInstrutorPorID(@PathVariable Long id) {
        InstrutorResponseDTO dto = instrutorService.buscarPorId(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    /**
     * Atualiza um instrutor existente
     */
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Instrutor> atualizarInstrutor(
            @PathVariable Long id,
            @RequestBody InstrutorRequestDTO dto) {

        Instrutor instrutorAtualizado = instrutorService.atualizar(id, dto);
        if (instrutorAtualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(instrutorAtualizado);
    }
    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Void> excluirInstrutor(@PathVariable Long id) {
        try {
            instrutorService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Busca os dados do INSTRUTOR logado.
     */
    @GetMapping("/me")
    public ResponseEntity<InstrutorResponseDTO> getMeuInstrutor(@AuthenticationPrincipal Usuario usuarioLogado) {
        if (usuarioLogado.getInstrutor() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // Não é um instrutor
        }
        Long instrutorId = usuarioLogado.getInstrutor().getId();
        InstrutorResponseDTO dto = instrutorService.buscarPorId(instrutorId);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    //atualiza os dados do INSTRUTOR logado.
    @PutMapping("/me")
    public ResponseEntity<Object> updateMeuInstrutor(@AuthenticationPrincipal Usuario usuarioLogado, @RequestBody InstrutorRequestDTO dto) {
        // Deve ser ResponseEntity<Object>, não ResponseEntity<?>
        if (usuarioLogado.getInstrutor() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuário não é um instrutor.");
        }
        Long instrutorId = usuarioLogado.getInstrutor().getId();

        try {
            Instrutor instrutor = instrutorService.atualizar(instrutorId, dto);
            return ResponseEntity.ok(instrutor);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar: " + e.getMessage());
        }
    }
}