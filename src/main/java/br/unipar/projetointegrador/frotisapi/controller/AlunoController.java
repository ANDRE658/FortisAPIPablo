package br.unipar.projetointegrador.frotisapi.controller;

import br.unipar.projetointegrador.frotisapi.dto.AlunoRequestDTO;
import br.unipar.projetointegrador.frotisapi.dto.DashboardStatsDTO;
import br.unipar.projetointegrador.frotisapi.model.Aluno;
import br.unipar.projetointegrador.frotisapi.model.Usuario;
import br.unipar.projetointegrador.frotisapi.service.AlunoService;
import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<List<Aluno>> listarAlunos(@AuthenticationPrincipal Usuario usuarioLogado) {
        // Passa o usuário logado para o serviço, que fará o filtro
        List<Aluno> alunos = alunoService.listarTodos(usuarioLogado);

        if (alunos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(alunos);
    }

    @PostMapping("/salvar")
    public ResponseEntity<?> salvarAluno(@RequestBody AlunoRequestDTO dto) {
        try {
            Aluno aluno = dto.toEntity();
            String senha = dto.getSenha();
            Long planoId = dto.getPlanoId();
            Long instrutorId = dto.getInstrutorId(); // <-- NOVO

            Aluno alunoSalvo = alunoService.salvar(aluno, senha, planoId, instrutorId); // <-- NOVO
            return ResponseEntity.status(HttpStatus.CREATED).body(alunoSalvo);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno: " + e.getMessage());
        }
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<?> atualizarAluno(@PathVariable Long id, @RequestBody AlunoRequestDTO dto) {
        try {
            // Passamos o DTO direto para o serviço
            Aluno aluno = alunoService.atualizar(id, dto);

            if (aluno == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(aluno);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao atualizar: " + e.getMessage());
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

    @DeleteMapping("/excluir/{id}") // NOVO ENDPOINT DE EXCLUSÃO
    public ResponseEntity<String> excluirAluno(@PathVariable Long id) {
        try {
            alunoService.excluir(id);
            return ResponseEntity.ok("Aluno excluído com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    //Busca os dados do ALUNO logado.
    @GetMapping("/me")
    public ResponseEntity<Aluno> getMeuAluno(@AuthenticationPrincipal Usuario usuarioLogado) {
        if (usuarioLogado.getAluno() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // Não é um aluno
        }
        Long alunoId = usuarioLogado.getAluno().getId();
        Aluno aluno = alunoService.buscarPorId(alunoId);
        if (aluno == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(aluno);
    }


    //Atualiza os dados do ALUNO logado.
    @PutMapping("/me")
    public ResponseEntity<?> updateMeuAluno(@AuthenticationPrincipal Usuario usuarioLogado, @RequestBody AlunoRequestDTO dto) {
        if (usuarioLogado.getAluno() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuário não é um aluno.");
        }
        Long alunoId = usuarioLogado.getAluno().getId();

        try {
            Aluno aluno = alunoService.atualizar(alunoId, dto);
            return ResponseEntity.ok(aluno);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar: " + e.getMessage());
        }
    }

    @GetMapping("/estatisticas")
    public ResponseEntity<DashboardStatsDTO> getEstatisticas() {
        return ResponseEntity.ok(alunoService.buscarEstatisticas());
    }
    @GetMapping("/estatisticas/instrutor/{instrutorId}")
    public ResponseEntity<DashboardStatsDTO> getEstatisticasInstrutor(@PathVariable Long instrutorId) {
        return ResponseEntity.ok(alunoService.buscarEstatisticasInstrutor(instrutorId));
    }

}
