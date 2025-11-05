package br.unipar.projetointegrador.frotisapi.service;

import br.unipar.projetointegrador.frotisapi.model.Plano;
import br.unipar.projetointegrador.frotisapi.repository.PlanoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanoService {

    private final PlanoRepository planoRepository;

    @Autowired
    public PlanoService(PlanoRepository planoRepository) {
        this.planoRepository = planoRepository;
    }

    public Plano salvar(Plano plano) {
        return planoRepository.save(plano);
    }

    public List<Plano> listarTodos() {
        return planoRepository.findAll();
    }

    public Plano buscarPorId(Long id) {
        return planoRepository.findById(id).orElse(null);
    }

    public void deletar(Long id) {
        planoRepository.deleteById(id);
    }

    /**
     * ✅ MÉTODO ATUALIZAR CORRIGIDO
     * Garante que seu método aceita os dois parâmetros (Long id, Plano plano)
     */
    public Plano atualizar(Long id, Plano planoAtualizado) {
        // 1. Verifica se o plano com o ID fornecido realmente existe
        if (planoRepository.existsById(id)) {
            // 2. Garante que estamos atualizando o objeto correto setando o ID
            planoAtualizado.setId(id);
            // 3. Salva as alterações
            return planoRepository.save(planoAtualizado);
        }
        // 4. Se não existir, retorna nulo
        return null;
    }
}