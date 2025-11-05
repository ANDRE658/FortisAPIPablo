package br.unipar.projetointegrador.frotisapi.dto;

public class LoginResponse {
    private String token;

    public LoginResponse(String token) {
        this.token = token;
    }

    // Getter e Setter
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}