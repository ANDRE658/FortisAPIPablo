package br.unipar.projetointegrador.frotisapi.service;

import br.unipar.projetointegrador.frotisapi.dto.ItemTreinoRequestDTO;
import br.unipar.projetointegrador.frotisapi.model.Exercicio;
import br.unipar.projetointegrador.frotisapi.model.ItemTreino;
import br.unipar.projetointegrador.frotisapi.model.Treino;
import br.unipar.projetointegrador.frotisapi.repository.ExercicioRepository;
import br.unipar.projetointegrador.frotisapi.repository.ItemTreinoRepository;
import br.unipar.projetointegrador.frotisapi.repository.TreinoRepository;
import org.springframework.stereotype.Service;

@Service
public class ItemTreinoService { // Nota: O nome está "Iten" e não "Item"

    private final ItemTreinoRepository itemTreinoRepository;
    private final TreinoRepository treinoRepository;
    private final ExercicioRepository exercicioRepository;

    public ItemTreinoService(ItemTreinoRepository itemTreinoRepository, TreinoRepository treinoRepository, ExercicioRepository exercicioRepository) {
        this.itemTreinoRepository = itemTreinoRepository;
        this.treinoRepository = treinoRepository;
        this.exercicioRepository = exercicioRepository;
    }

    public ItemTreino salvar(Long treinoId, ItemTreinoRequestDTO dto) throws Exception {

        // 1. Busca o Treino (Dia)
        Treino treino = treinoRepository.findById(treinoId)
                .orElseThrow(() -> new Exception("Treino com ID " + treinoId + " não encontrado."));

        // 2. Busca o Exercício (do Catálogo)
        Exercicio exercicio = exercicioRepository.findById(dto.getExercicioId())
                .orElseThrow(() -> new Exception("Exercício com ID " + dto.getExercicioId() + " não encontrado."));

        // 3. Cria a nova entidade Item_treino
        ItemTreino novoItem = new ItemTreino();
        novoItem.setTreino(treino); // Linka ao "Dia"
        novoItem.setExercicio(exercicio); // Linka ao "Catálogo"

        // 4. Preenche os dados
        novoItem.setSeries(dto.getSeries());
        novoItem.setRepeticoes(dto.getRepeticoes());
        novoItem.setCarga(dto.getCarga());
        novoItem.setTempoDescansoSegundos(dto.getTempoDescansoSegundos());

        // 5. Salva no banco
        return itemTreinoRepository.save(novoItem);
    }
}