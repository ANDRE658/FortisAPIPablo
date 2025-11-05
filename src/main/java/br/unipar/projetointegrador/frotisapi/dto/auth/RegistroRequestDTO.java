package br.unipar.projetointegrador.frotisapi.dto.auth;
import br.unipar.projetointegrador.frotisapi.model.Role;
import lombok.Data;
@Data
public class RegistroRequestDTO {
    private String login;
    private String senha;
    private Role role;
    private Long instrutorId; // Opcional: ID do instrutor associado
}