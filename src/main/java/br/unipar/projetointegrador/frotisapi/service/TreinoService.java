package br.unipar.projetointegrador.frotisapi.service;

import br.unipar.projetointegrador.frotisapi.dto.DiaTreinoRequestDTO; // Importe o DTO correto
import br.unipar.projetointegrador.frotisapi.dto.TreinoDTO;
import br.unipar.projetointegrador.frotisapi.model.FichaTreino; // Importe a nova Ficha
import br.unipar.projetointegrador.frotisapi.model.Treino;
import br.unipar.projetointegrador.frotisapi.repository.FichaTreinoRepository; // Importe o novo Repo
import br.unipar.projetointegrador.frotisapi.repository.TreinoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class TreinoService {

    private final TreinoRepository treinoRepository;

    // --- INÍCIO DA CORREÇÃO (PASSO 1: DECLARAR) ---
    private final FichaTreinoRepository fichaTreinoRepository;
    // (Os repositórios de Aluno e Instrutor não são mais necessários AQUI)
    // --- FIM DA CORREÇÃO ---


    // --- INÍCIO DA CORREÇÃO (PASSO 2: ATUALIZAR CONSTRUTOR) ---
    public TreinoService(TreinoRepository treinoRepository,
                         FichaTreinoRepository fichaTreinoRepository) { // Adicione aqui
        this.treinoRepository = treinoRepository;
        this.fichaTreinoRepository = fichaTreinoRepository; // E inicialize aqui
    }
    // --- FIM DA CORREÇÃO ---

    /**
     * Este é o novo método save.
     * Ele recebe o ID da Ficha-Pai e o Dia da Semana.
     */
    @Transactional
    public Treino save(DiaTreinoRequestDTO dto) throws Exception {
        // 1. Buscar a Ficha-Pai (AGORA VAI FUNCIONAR)
        FichaTreino ficha = fichaTreinoRepository.findById(dto.getFichaId())
                .orElseThrow(() -> new Exception("Ficha de Treino não encontrada"));

        // 2. Criar a nova entidade Treino (Dia)
        Treino novoDiaDeTreino = new Treino();
        novoDiaDeTreino.setDiaSemana(dto.getDiaSemana());
        novoDiaDeTreino.setFichaTreino(ficha); // Linka com a Ficha-Pai

        // 3. Salvar o novo dia
        return treinoRepository.save(novoDiaDeTreino);
    }


    public List<Treino> findAll() {
        return treinoRepository.findAllTreinosCompletos();
    }

    public void deleteById(Long id) {
        treinoRepository.deleteById(id);
    }

    public Treino findById(Long id) {
        return treinoRepository.findTreinoCompletoById(id).orElse(null);
    }

    public TreinoDTO getTreinoCompletoDTO(Long id) {
        Treino treino = treinoRepository.findTreinoCompletoById(id).orElse(null);
        if (treino != null) {
            return new TreinoDTO(treino);
        }
        return null;
    }

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