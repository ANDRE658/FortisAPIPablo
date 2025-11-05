package br.unipar.projetointegrador.frotisapi.service;

import br.unipar.projetointegrador.frotisapi.dto.TreinoDTO;
import br.unipar.projetointegrador.frotisapi.dto.TreinoRequestDTO;
import br.unipar.projetointegrador.frotisapi.model.Aluno;
import br.unipar.projetointegrador.frotisapi.model.Exercicio;
import br.unipar.projetointegrador.frotisapi.model.Instrutor;
import br.unipar.projetointegrador.frotisapi.model.Treino;
import br.unipar.projetointegrador.frotisapi.repository.AlunoRepository;
import br.unipar.projetointegrador.frotisapi.repository.InstrutorRepository;
import br.unipar.projetointegrador.frotisapi.repository.TreinoRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TreinoService {

    private final TreinoRepository treinoRepository;
    private final AlunoRepository alunoRepository; // <-- ADICIONE
    private final InstrutorRepository instrutorRepository; // <-- ADICIONE

    public TreinoService(TreinoRepository treinoRepository, AlunoRepository alunoRepository, InstrutorRepository instrutorRepository) {
        this.treinoRepository = treinoRepository;
        this.alunoRepository = alunoRepository;
        this.instrutorRepository = instrutorRepository;
    }

    public Treino save(Treino treino) {
        if (treino.getExercicios() != null) {
            for (Exercicio exercicio : treino.getExercicios()) {
                exercicio.setTreino(treino);
            }
        }
        return treinoRepository.save(treino);
    }
    // NOVO MÉTODO: Crie este novo método 'save' que aceita o DTO
    public Treino save(TreinoRequestDTO dto) throws Exception {
        // 1. Buscar as entidades (Aluno e Instrutor) pelos IDs
        Aluno aluno = alunoRepository.findById(dto.getAlunoId())
                .orElseThrow(() -> new Exception("Aluno não encontrado"));

        Instrutor instrutor = instrutorRepository.findById(dto.getInstrutorId())
                .orElseThrow(() -> new Exception("Instrutor não encontrado"));

        // 2. Criar a nova entidade Treino
        Treino novoTreino = new Treino();
        novoTreino.setNome(dto.getNome());
        novoTreino.setDiaSemana(dto.getDiaSemana());
        novoTreino.setInstrutor(instrutor);

        // 3. Associar o aluno (lembrando que 'alunos' é um Set)
        Set<Aluno> alunos = new HashSet<>();
        alunos.add(aluno);
        novoTreino.setAlunos(alunos);

        // 4. Salvar o novo treino
        return treinoRepository.save(novoTreino);
    }

    /**
     * CORREÇÃO CERTEIRA:
     * Trocamos o "findAll()" padrão (preguiçoso) pelo nosso novo
     * "findAllTreinosCompletos()" (completo).
     * Isso corrige o bug "Falha ao buscar a lista de treinos."
     */
    public List<Treino> findAll() {
        return treinoRepository.findAllTreinosCompletos();
    }

    public void deleteById(Long id) {
        treinoRepository.deleteById(id);
    }

    // Este método busca a Entidade completa (corrigido)
    public Treino findById(Long id) {
        return treinoRepository.findTreinoCompletoById(id).orElse(null);
    }

    // Este método retorna o DTO "limpo" para o Controller (corrigido)
    public TreinoDTO getTreinoCompletoDTO(Long id) {
        Treino treino = treinoRepository.findTreinoCompletoById(id).orElse(null);
        if (treino != null) {
            return new TreinoDTO(treino);
        }
        return null;
    }

    // Este método retorna o DTO do treino de hoje (já estava correto)
    public TreinoDTO buscarTreinoDeHoje() {
        LocalDate hoje = LocalDate.now();
        DayOfWeek diaDaSemanaEnum = hoje.getDayOfWeek();
        String diaDaSemanaStr = converterDiaDaSemana(diaDaSemanaEnum);

        Treino treino = treinoRepository.findTreinoCompletoByDiaSemana(diaDaSemanaStr).orElse(null);

        if (treino != null) {
            return new TreinoDTO(treino);
        }
        return null;
    }

    // Método auxiliar para traduzir o dia da semana
    private String converterDiaDaSemana(DayOfWeek dia) {
        switch (dia) {
            case MONDAY: return "segunda";
            case TUESDAY: return "terca";
            case WEDNESDAY: return "quarta";
            case THURSDAY: return "quinta";
            case FRIDAY: return "sexta";
            case SATURDAY: return "sabado";
            case SUNDAY: return "domingo";
            default: return "";
        }
    }
}