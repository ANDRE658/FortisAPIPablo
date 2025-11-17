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
    private String sexo;
    private Integer diaVencimento;

    private String senha;


    private Endereco endereco;

    private Long planoId;

    private Long instrutorId;

    public Aluno toEntity() {
        Aluno aluno = new Aluno();
        aluno.setNome(this.nome);


        // --- Limpa a formatação (mantém só números) ---
        aluno.setCpf(this.CPF != null ? this.CPF.replaceAll("[^0-9]", "") : null);
        aluno.setTelefone(this.telefone != null ? this.telefone.replaceAll("[^0-9]", "") : null);


        aluno.setEmail(this.email);
        aluno.setPeso(this.peso);
        aluno.setAltura(this.altura);
        aluno.setDataNascimento(this.dataNascimento);
        aluno.setSexo(this.sexo);
        aluno.setEndereco(this.endereco);
        aluno.setDiaVencimento(this.diaVencimento);
        return aluno;
    }
}