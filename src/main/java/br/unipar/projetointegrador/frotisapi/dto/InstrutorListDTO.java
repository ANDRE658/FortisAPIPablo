package br.unipar.projetointegrador.frotisapi.dto;

import br.unipar.projetointegrador.frotisapi.model.Instrutor;
import lombok.Data;

@Data
public class InstrutorListDTO {

    private Long id;
    private String nome;
    private String cpf;

    // Construtor que converte a Entidade (Model) para o DTO
    public InstrutorListDTO(Instrutor instrutor) {
        this.id = instrutor.getId();
        this.nome = instrutor.getNome();
        this.cpf = instrutor.getCPF();
    }
}