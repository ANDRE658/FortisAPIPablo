package br.unipar.projetointegrador.frotisapi.dto;

import lombok.Data;
import java.util.List;

@Data
public class TreinoCompletoDTO {
    private String diaSemana; // "SEGUNDA", "TERCA", etc.
    private String nome; // "Peito e Tríceps"
    private List<ItemTreinoRequestDTO> itensTreino; // A lista de exercícios
}