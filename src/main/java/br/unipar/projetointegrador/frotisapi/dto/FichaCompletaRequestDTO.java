package br.unipar.projetointegrador.frotisapi.dto;

import lombok.Data;
import java.util.List;

@Data
public class FichaCompletaRequestDTO {
    private Long alunoId;
    private Long instrutorId;
    private List<TreinoCompletoDTO> diasDeTreino; // A lista de dias (Seg, Ter...)
}