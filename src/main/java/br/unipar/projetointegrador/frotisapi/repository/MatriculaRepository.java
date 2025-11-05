package br.unipar.projetointegrador.frotisapi.repository;

import br.unipar.projetointegrador.frotisapi.model.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
}
