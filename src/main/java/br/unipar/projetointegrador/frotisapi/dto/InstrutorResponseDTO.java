package br.unipar.projetointegrador.frotisapi.dto;

import br.unipar.projetointegrador.frotisapi.model.Endereco;
import br.unipar.projetointegrador.frotisapi.model.Instrutor;
import lombok.Data;
import java.util.Date;

@Data
public class InstrutorResponseDTO {

    // Todos os campos do Instrutor, exceto os que causam loop
    private Long id;
    private String nome;
    private String cpf;
    private Date dataNascimento;
    private String telefone;
    private String email;
    private String sexo;
    private Endereco endereco;

    // Construtor que converte a Entidade para este DTO
    public InstrutorResponseDTO(Instrutor instrutor) {
        this.id = instrutor.getId();
        this.nome = instrutor.getNome();
        this.cpf = instrutor.getCPF();
        this.dataNascimento = instrutor.getDataNascimento();
        this.telefone = instrutor.getTelefone();
        this.email = instrutor.getEmail();
        this.sexo = instrutor.getSexo();
        this.endereco = instrutor.getEndereco();
    }
}