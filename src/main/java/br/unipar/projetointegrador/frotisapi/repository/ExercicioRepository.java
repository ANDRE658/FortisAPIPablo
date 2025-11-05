package br.unipar.projetointegrador.frotisapi.repository;

import br.unipar.projetointegrador.frotisapi.model.Exercicio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExercicioRepository extends JpaRepository<Exercicio, Long> {
}
