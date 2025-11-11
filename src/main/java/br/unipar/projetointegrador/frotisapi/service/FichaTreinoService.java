// CRIE O SERVICE: .../service/FichaTreinoService.java
package br.unipar.projetointegrador.frotisapi.service;

// ... (Imports: FichaTreino, FichaTreinoRequestDTO, FichaTreinoRepository, AlunoRepository, InstrutorRepository, ...)
import br.unipar.projetointegrador.frotisapi.dto.FichaTreinoRequestDTO;
import br.unipar.projetointegrador.frotisapi.model.Aluno;
import br.unipar.projetointegrador.frotisapi.model.FichaTreino;
import br.unipar.projetointegrador.frotisapi.model.Instrutor;
import br.unipar.projetointegrador.frotisapi.model.Treino;
import br.unipar.projetointegrador.frotisapi.repository.AlunoRepository;
import br.unipar.projetointegrador.frotisapi.repository.FichaTreinoRepository;
import br.unipar.projetointegrador.frotisapi.repository.InstrutorRepository;
import br.unipar.projetointegrador.frotisapi.repository.TreinoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FichaTreinoService {

    private final FichaTreinoRepository fichaTreinoRepository;
    private final AlunoRepository alunoRepository;
    private InstrutorRepository instrutorRepository = null;
    private final TreinoRepository treinoRepository; // <-- 1. ADICIONE ESTE CAMPO

    public FichaTreinoService(FichaTreinoRepository fichaTreinoRepository,
                              AlunoRepository alunoRepository,
                              InstrutorRepository instrutorRepository, TreinoRepository treinoRepository) {
        this.fichaTreinoRepository = fichaTreinoRepository;
        this.alunoRepository = alunoRepository;
        this.instrutorRepository = instrutorRepository;
        this.treinoRepository = treinoRepository;
    }

    // Construtor com @Autowired...

    @Transactional
    public FichaTreino salvar(FichaTreinoRequestDTO dto) throws Exception {
        Aluno aluno = alunoRepository.findById(dto.getAlunoId())
                .orElseThrow(() -> new Exception("Aluno não encontrado"));
        Instrutor instrutor = instrutorRepository.findById(dto.getInstrutorId())
                .orElseThrow(() -> new Exception("Instrutor não encontrado"));

        FichaTreino ficha = new FichaTreino();
        ficha.setAluno(aluno);
        ficha.setInstrutor(instrutor);

        return fichaTreinoRepository.save(ficha);
    }

    @Transactional// É importante que seja transacional
    public FichaTreino buscarCompletoPorId(Long id) {

        // Passo 1: Busca a ficha e o primeiro "saco" (diasDeTreino)
        FichaTreino ficha = fichaTreinoRepository.findFichaComDiasById(id).orElse(null);

        // Se a ficha não for encontrada ou não tiver dias, podemos retornar
        if (ficha == null || ficha.getDiasDeTreino() == null || ficha.getDiasDeTreino().isEmpty()) {
            return ficha;
        }

        // Passo 2: Coleta os IDs dos dias de treino que encontramos
        List<Long> treinoIds = ficha.getDiasDeTreino().stream()
                .map(Treino::getId)
                .collect(Collectors.toList());

        // Passo 3: Busca o segundo "saco" (itensTreino) para todos os dias de uma vez
        List<Treino> diasComItensCarregados = treinoRepository.findTreinosCompletosByIds(treinoIds);

        // Passo 4: Define a lista de dias da ficha como a lista completa que acabamos de buscar
        ficha.setDiasDeTreino(diasComItensCarregados);

        return ficha; // Retorna a ficha agora "montada"
    }

    public List<FichaTreino> listarTodas() {
        return fichaTreinoRepository.findAllComAlunos();
    }
}