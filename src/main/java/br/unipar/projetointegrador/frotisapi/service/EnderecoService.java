package br.unipar.projetointegrador.frotisapi.service;

import br.unipar.projetointegrador.frotisapi.dto.EnderecoViaCepDTO;
import br.unipar.projetointegrador.frotisapi.model.Endereco;
import br.unipar.projetointegrador.frotisapi.repository.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired; // 1. Importe a anotação
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository; // 2. Declare como 'final'

    /**
     * 3. Adicione este construtor com @Autowired.
     * Isso garante que o Spring injete o repositório quando o serviço for criado.
     */
    @Autowired
    public EnderecoService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }

    public Endereco salvar(Endereco endereco) {
        return enderecoRepository.save(endereco);
    }

    public Endereco buscarPorId(Long id) {
        return enderecoRepository.findById(id).orElse(null);
    }

    public void deletar(Long id) {
        enderecoRepository.deleteById(id);
    }

    public List<Endereco> listarTodos() {
        // Agora o enderecoRepository não será mais null
        return enderecoRepository.findAll();
    }

}