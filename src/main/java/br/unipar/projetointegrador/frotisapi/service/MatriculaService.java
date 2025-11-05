package br.unipar.projetointegrador.frotisapi.service;

import br.unipar.projetointegrador.frotisapi.model.Matricula;
import br.unipar.projetointegrador.frotisapi.repository.MatriculaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatriculaService {

    private MatriculaRepository matriculaRepository;

    public Matricula salvar(Matricula matricula) {
        // Lógica para salvar a matrícula
        return matriculaRepository.save(matricula);
    }

    public Matricula buscarPorId(Long id) {
        // Lógica para buscar a matrícula por ID
        return matriculaRepository.findById(id).orElse(null);
    }

    public void deletar(Long id) {
        // Lógica para deletar a matrícula
        matriculaRepository.deleteById(id);
    }

    public List<Matricula> listarTodos() {
        // Lógica para listar todas as matrículas
        return matriculaRepository.findAll();
    }
}
