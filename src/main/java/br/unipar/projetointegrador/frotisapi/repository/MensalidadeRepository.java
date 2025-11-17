package br.unipar.projetointegrador.frotisapi.repository;

import br.unipar.projetointegrador.frotisapi.model.Mensalidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Date;
import java.util.List;

public interface MensalidadeRepository extends JpaRepository<Mensalidade, Long> {

    // Busca mensalidades de um aluno específico
    List<Mensalidade> findByAlunoId(Long alunoId);

    // Verifica se já existe mensalidade para o aluno naquele mês/ano (para não duplicar)
    @Query("SELECT COUNT(m) > 0 FROM Mensalidade m WHERE m.aluno.id = :alunoId AND EXTRACT(MONTH FROM m.dataVencimento) = :mes AND EXTRACT(YEAR FROM m.dataVencimento) = :ano")
    boolean existsByAlunoAndMesAno(@Param("alunoId") Long alunoId, @Param("mes") int mes, @Param("ano") int ano);

    // Busca todas (pode adicionar filtros customizados aqui se o findAll não bastar)
    List<Mensalidade> findAll();
}