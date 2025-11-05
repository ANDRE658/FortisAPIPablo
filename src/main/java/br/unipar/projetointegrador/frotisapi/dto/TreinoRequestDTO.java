package br.unipar.projetointegrador.frotisapi.dto;

import lombok.Data;

@Data
public class TreinoRequestDTO {
    private String nome;
    private String diaSemana;
    private Long alunoId;
    private Long instrutorId;
}