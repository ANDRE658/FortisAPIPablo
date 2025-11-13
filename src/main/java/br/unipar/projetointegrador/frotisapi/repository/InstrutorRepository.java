package br.unipar.projetointegrador.frotisapi.repository;

import br.unipar.projetointegrador.frotisapi.model.Instrutor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstrutorRepository extends JpaRepository<Instrutor, Long> {
    // Busca apenas onde ativo é true
    List<Instrutor> findAllByAtivoTrue();
}