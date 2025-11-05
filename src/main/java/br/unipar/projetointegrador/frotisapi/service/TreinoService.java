package br.unipar.projetointegrador.frotisapi.service;

import br.unipar.projetointegrador.frotisapi.dto.TreinoDTO;
import br.unipar.projetointegrador.frotisapi.model.Exercicio;
import br.unipar.projetointegrador.frotisapi.model.Treino;
import br.unipar.projetointegrador.frotisapi.repository.TreinoRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class TreinoService {

    private final TreinoRepository treinoRepository;

    public TreinoService(TreinoRepository treinoRepository) {
        this.treinoRepository = treinoRepository;
    }

    public Treino save(Treino treino) {
        if (treino.getExercicios() != null) {
            for (Exercicio exercicio : treino.getExercicios()) {
                exercicio.setTreino(treino);
            }
        }
        return treinoRepository.save(treino);
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