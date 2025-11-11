package br.unipar.projetointegrador.frotisapi.dto;

import lombok.Data;

@Data
public class DiaTreinoRequestDTO {
    private Long fichaId; // O ID da Ficha-Pai
    private String diaSemana; // "SEGUNDA", "TERCA", etc.
    private String nome; // <-- ADICIONE ESTA LINHA
}