package br.unipar.projetointegrador.frotisapi.dto;

import br.unipar.projetointegrador.frotisapi.model.Endereco;
import br.unipar.projetointegrador.frotisapi.model.Instrutor;
import lombok.Data;

import java.util.Date;

@Data
public class InstrutorRequestDTO {
    // Campos do Instrutor
    private String nome;
    private String CPF;
    private Date dataNascimento;
    private String telefone;
    private String email;
    private String sexo;

    // Campo do Endereco
    private Endereco endereco; // O frontend enviará rua, cidade, estado, cep

    // Campo de Senha
    private String senha;

    // Converte o DTO para a entidade Instrutor
    public Instrutor toEntity() {
        Instrutor instrutor = new Instrutor();
        instrutor.setNome(this.nome);
        instrutor.setCPF(this.CPF);
        instrutor.setDataNascimento(this.dataNascimento);
        instrutor.setTelefone(this.telefone);
        instrutor.setEmail(this.email);
        instrutor.setSexo(this.sexo);
        instrutor.setEndereco(this.endereco);
        return instrutor;
    }
}