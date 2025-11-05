package br.unipar.projetointegrador.frotisapi.controller;

import br.unipar.projetointegrador.frotisapi.dto.EnderecoViaCepDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/consulta-cep")
public class ConsultaCepController {

    private final RestTemplate restTemplate;

    // Injetando o RestTemplate que configuramos
    public ConsultaCepController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/{cep}")
    public ResponseEntity<EnderecoViaCepDTO> consultaCep(@PathVariable String cep) {
        // Monta a URL da API ViaCEP
        String url = "https://viacep.com.br/ws/" + cep + "/json/";

        try {
            // Faz a requisição GET e já converte a resposta para nosso DTO
            EnderecoViaCepDTO endereco = restTemplate.getForObject(url, EnderecoViaCepDTO.class);

            // Verifica se o CEP retornado não é nulo (caso de CEP inválido)
            if (endereco != null && endereco.getCep() != null) {
                return ResponseEntity.ok(endereco);
            } else {
                // Se o CEP for inválido, o ViaCEP retorna um DTO com campos nulos
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            // Em caso de erro na chamada (ex: timeout), retorna um erro interno do servidor
            return ResponseEntity.internalServerError().build();
        }
    }


}