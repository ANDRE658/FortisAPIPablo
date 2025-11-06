package br.unipar.projetointegrador.frotisapi.dto;

import br.unipar.projetointegrador.frotisapi.model.Treino;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class TreinoDTO {
    private Long id;
    private String nome;
    private String diaSemana;

    // --- INÍCIO DA CORREÇÃO ---
    // Mude o tipo da lista de ExercicioDTO para o novo ItemTreinoResponseDTO
    private List<ItemTreinoResponseDTO> itensTreino;
    // --- FIM DA CORREÇÃO ---


    // Construtor que converte a Entidade para DTO
    public TreinoDTO(Treino treino) {
        this.id = treino.getId();
        this.nome = treino.getNome();
        this.diaSemana = treino.getDiaSemana();

        // --- INÍCIO DA CORREÇÃO ---
        // Agora, converte a lista de Item_treino para uma lista de ItemTreinoResponseDTO
        this.itensTreino = treino.getItensTreino().stream() // <-- Use o novo getter: getItensTreino()
                .map(ItemTreinoResponseDTO::new) // <-- Use o novo DTO de resposta
                .collect(Collectors.toList());
        // --- FIM DA CORREÇÃO ---
    }
}