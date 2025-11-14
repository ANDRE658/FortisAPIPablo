package br.unipar.projetointegrador.frotisapi.dto;
import lombok.Data;

@Data
public class AlterarSenhaRequestDTO {
    private String senhaAtual;
    private String novaSenha;
}