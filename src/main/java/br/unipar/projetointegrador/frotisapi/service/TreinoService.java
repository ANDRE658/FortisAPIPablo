package br.unipar.projetointegrador.frotisapi.service;

import br.unipar.projetointegrador.frotisapi.dto.TreinoDTO;
import br.unipar.projetointegrador.frotisapi.dto.TreinoRequestDTO; // Importe este
import br.unipar.projetointegrador.frotisapi.model.Aluno;         // Importe este
import br.unipar.projetointegrador.frotisapi.model.Instrutor;   // Importe este
import br.unipar.projetointegrador.frotisapi.model.Treino;
import br.unipar.projetointegrador.frotisapi.repository.AlunoRepository;      // Importe este
import br.unipar.projetointegrador.frotisapi.repository.InstrutorRepository;  // Importe este
import br.unipar.projetointegrador.frotisapi.repository.TreinoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet; // Importe este
import java.util.List;
import java.util.Set; // Importe este

@Service
public class TreinoService {

    private final TreinoRepository treinoRepository;
    private final AlunoRepository alunoRepository;         // Mantenha este
    private final InstrutorRepository instrutorRepository; // Mantenha este

    // Mantenha o construtor completo
    public TreinoService(TreinoRepository treinoRepository,
                         AlunoRepository alunoRepository,
                         InstrutorRepository instrutorRepository) {
        this.treinoRepository = treinoRepository;
        this.alunoRepository = alunoRepository;
        this.instrutorRepository = instrutorRepository;
    }

    // Este é o método que você está usando na API (está correto)
    @Transactional // É bom adicionar isso
    public Treino save(TreinoRequestDTO dto) throws Exception {
        // 1. Buscar as entidades (Aluno e Instrutor) pelos IDs
        Aluno aluno = alunoRepository.findById(dto.getAlunoId())
                .orElseThrow(() -> new Exception("Aluno não encontrado"));

        Instrutor instrutor = instrutorRepository.findById(dto.getInstrutorId())
                .orElseThrow(() -> new Exception("Instrutor não encontrado"));

        // 2. Criar a nova entidade Treino (Ficha/Dia)
        Treino novoTreino = new Treino();
        novoTreino.setNome(dto.getNome());
        novoTreino.setDiaSemana(dto.getDiaSemana());
        novoTreino.setInstrutor(instrutor);

        // 3. Associar o aluno
        Set<Aluno> alunos = new HashSet<>();
        alunos.add(aluno);
        novoTreino.setAlunos(alunos);

        // 4. Salvar o novo treino
        return treinoRepository.save(novoTreino);
    }

    // --- INÍCIO DA CORREÇÃO ---
    // Este é o método que está causando o erro de compilação
    public Treino save(Treino treino) {

        // Remova este bloco inteiro, pois 'getExercicios' não existe mais
        /*
        if (treino.getExercicios() != null) {
            for (Exercicio exercicio : treino.getExercicios()) {
                exercicio.setTreino(treino);
            }
        }
        */

        // O método agora apenas salva
        return treinoRepository.save(treino);
    }
    // --- FIM DA CORREÇÃO ---


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