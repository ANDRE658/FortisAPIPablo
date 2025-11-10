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
    private String diaSemana;


    private List<ItemTreinoResponseDTO> itensTreino;

    public TreinoDTO(Treino treino) {
        this.id = treino.getId();
        this.diaSemana = treino.getDiaSemana();

        // Agora, converte a lista de Item_treino para uma lista de ItemTreinoResponseDTO
        this.itensTreino = treino.getItensTreino().stream() // <-- Use o novo getter: getItensTreino()
                .map(ItemTreinoResponseDTO::new) // <-- Use o novo DTO de resposta
                .collect(Collectors.toList());

    }
}