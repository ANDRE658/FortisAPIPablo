package br.unipar.projetointegrador.frotisapi.dto;

import br.unipar.projetointegrador.frotisapi.model.Aluno;
import lombok.Data;
import java.util.Date;

@Data
public class AlunoVisualizacaoDTO {

    private Long id;
    private String nome;
    private int idade;
    private String sexo;
    private float altura;
    private float peso;
    private double imc;
    private Date dataInicio;

    // Construtor que converte a Entidade Aluno para este DTO
    public AlunoVisualizacaoDTO(Aluno aluno) {
        this.id = aluno.getId();
        this.nome = aluno.getNome();
        this.idade = aluno.getIdade(); // Usa o método que criamos
        this.sexo = aluno.getSexo();
        this.altura = aluno.getAltura();
        this.peso = aluno.getPeso();
        this.imc = aluno.getImc(); // Usa o método que criamos
        this.dataInicio = aluno.getDataCadastro();
    }
}