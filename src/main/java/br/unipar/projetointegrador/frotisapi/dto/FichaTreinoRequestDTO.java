package br.unipar.projetointegrador.frotisapi.dto;

import lombok.Data;

@Data
public class FichaTreinoRequestDTO {
    private String nome;
    private Long alunoId;
    private Long instrutorId;
}