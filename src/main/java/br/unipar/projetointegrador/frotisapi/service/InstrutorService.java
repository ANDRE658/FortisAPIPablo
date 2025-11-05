package br.unipar.projetointegrador.frotisapi.service;

import br.unipar.projetointegrador.frotisapi.model.Instrutor;
import br.unipar.projetointegrador.frotisapi.repository.InstrutorRepository;
import org.springframework.stereotype.Service;

@Service
public class InstrutorService {

    private InstrutorRepository instrutorRepository;

    public Instrutor salvar(Instrutor instrutor) {
        return instrutorRepository.save(instrutor);
    }

    public Instrutor buscarPorId(Long id) {
        return instrutorRepository.findById(id).orElse(null);
    }

    public void deletar(Long id) {
        instrutorRepository.deleteById(id);
    }


}
