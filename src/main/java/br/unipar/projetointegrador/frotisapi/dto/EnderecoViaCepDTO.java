package br.unipar.projetointegrador.frotisapi.dto; // Ou o pacote que preferir para DTOs

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data // Anotação do Lombok para gerar Getters, Setters, etc.
@JsonIgnoreProperties(ignoreUnknown = true) // Ignora campos do JSON que não temos na classe
public class EnderecoViaCepDTO {

    private String cep;
    private String logradouro;
    private String complemento;
    private String bairro;
    private String localidade; // Cidade
    private String uf;         // Estado
    private String ddd;

}
