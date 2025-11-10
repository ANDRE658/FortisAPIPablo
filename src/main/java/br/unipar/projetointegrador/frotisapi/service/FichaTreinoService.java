// CRIE O SERVICE: .../service/FichaTreinoService.java
package br.unipar.projetointegrador.frotisapi.service;

// ... (Imports: FichaTreino, FichaTreinoRequestDTO, FichaTreinoRepository, AlunoRepository, InstrutorRepository, ...)
import br.unipar.projetointegrador.frotisapi.dto.FichaTreinoRequestDTO;
import br.unipar.projetointegrador.frotisapi.model.Aluno;
import br.unipar.projetointegrador.frotisapi.model.FichaTreino;
import br.unipar.projetointegrador.frotisapi.model.Instrutor;
import br.unipar.projetointegrador.frotisapi.repository.AlunoRepository;
import br.unipar.projetointegrador.frotisapi.repository.FichaTreinoRepository;
import br.unipar.projetointegrador.frotisapi.repository.InstrutorRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FichaTreinoService {

    private final FichaTreinoRepository fichaTreinoRepository;
    private final AlunoRepository alunoRepository;
    private final InstrutorRepository instrutorRepository;

    public FichaTreinoService(FichaTreinoRepository fichaTreinoRepository, AlunoRepository alunoRepository, InstrutorRepository instrutorRepository) {
        this.fichaTreinoRepository = fichaTreinoRepository;
        this.alunoRepository = alunoRepository;
        this.instrutorRepository = instrutorRepository;
    }

    // Construtor com @Autowired...

    @Transactional
    public FichaTreino salvar(FichaTreinoRequestDTO dto) throws Exception {
        Aluno aluno = alunoRepository.findById(dto.getAlunoId())
                .orElseThrow(() -> new Exception("Aluno não encontrado"));
        Instrutor instrutor = instrutorRepository.findById(dto.getInstrutorId())
                .orElseThrow(() -> new Exception("Instrutor não encontrado"));

        FichaTreino ficha = new FichaTreino();
        ficha.setNome(dto.getNome());
        ficha.setAluno(aluno);
        ficha.setInstrutor(instrutor);

        return fichaTreinoRepository.save(ficha);
    }

    // --- ADICIONE ESTE MÉTODO ---
    public FichaTreino buscarCompletoPorId(Long id) {
        return fichaTreinoRepository.findFichaCompletaById(id).orElse(null);
    }

    public List<FichaTreino> listarTodas() {
        return fichaTreinoRepository.findAllComAlunos();
    }
}