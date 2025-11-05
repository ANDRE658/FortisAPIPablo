package br.unipar.projetointegrador.frotisapi.controller;

import br.unipar.projetointegrador.frotisapi.dto.AlunoRequestDTO;
import br.unipar.projetointegrador.frotisapi.model.Aluno;
import br.unipar.projetointegrador.frotisapi.service.AlunoService;
import org.apache.catalina.connector.Response;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/aluno")
public class AlunoController {

    private AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Aluno>> listarAlunos() {
        List<Aluno> alunos = alunoService.listarTodos();

        if (alunos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(alunos);
    }

    @PostMapping("/salvar")
    public ResponseEntity<Aluno> salvarAluno(@RequestBody AlunoRequestDTO dto) { // 2. MUDE O PARÂMETRO
        // 3. Converta o DTO e passe a senha para o serviço
        Aluno aluno = dto.toEntity();
        String senha = dto.getSenha();

        try {
            Aluno alunoSalvo = alunoService.salvar(aluno, senha); // 4. CHAME O NOVO MÉTODO
            return ResponseEntity.status(Response.SC_CREATED).body(alunoSalvo);
        } catch (IllegalArgumentException e) {
            // Retorna 400 Bad Request se a senha estiver faltando
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Aluno> buscarAlunoPorID(@PathVariable Long id) {
        Aluno aluno = alunoService.buscarPorId(id);

        if (aluno == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(aluno);
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarAluno(@PathVariable Long id) {
        alunoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Aluno> atualizarAluno(@PathVariable Long id, @RequestBody Aluno alunoAtualizado) {
        Aluno aluno = alunoService.atualizar(id, alunoAtualizado);

        if (aluno == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(aluno);
    }


}
