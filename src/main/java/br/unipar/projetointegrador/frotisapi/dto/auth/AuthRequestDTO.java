package br.unipar.projetointegrador.frotisapi.dto.auth;
import lombok.Data;
@Data
public class AuthRequestDTO {
    private String login;
    private String senha;
}