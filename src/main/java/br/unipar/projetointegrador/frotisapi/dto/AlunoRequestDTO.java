package br.unipar.projetointegrador.frotisapi.dto;

import br.unipar.projetointegrador.frotisapi.model.Aluno;
import br.unipar.projetointegrador.frotisapi.model.Endereco;
import lombok.Data;

import java.util.Date;

@Data
public class AlunoRequestDTO {
    // Campos do Aluno
    private String nome;
    private String CPF;
    private String telefone; // <-- CAMPO NOVO
    private String email;
    private float peso;
    private float altura;
    private Date dataNascimento;
    private String sexo; // <-- CAMPO NOVO

    // Campo do Endereco
    private Endereco endereco; // O frontend enviará rua, cidade, estado, cep

    // Campo de Senha
    private String senha;

    // Converte o DTO para a entidade Aluno
    public Aluno toEntity() {
        Aluno aluno = new Aluno();
        aluno.setNome(this.nome);
        aluno.setCPF(this.CPF);
        aluno.setTelefone(this.telefone); // <-- CAMPO NOVO
        aluno.setEmail(this.email);
        aluno.setPeso(this.peso);
        aluno.setAltura(this.altura);
        aluno.setDataNascimento(this.dataNascimento);
        aluno.setSexo(this.sexo); // <-- CAMPO NOVO
        aluno.setEndereco(this.endereco);
        return aluno;
    }
}