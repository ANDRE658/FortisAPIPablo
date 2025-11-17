package br.unipar.projetointegrador.frotisapi.repository;

import br.unipar.projetointegrador.frotisapi.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    Optional<Aluno> findByCpf(String cpf);
    // Conta alunos ativos
    long countByAtivoTrue();

    // Conta alunos inativos
    long countByAtivoFalse();

    // Conta alunos cadastrados DEPOIS de uma certa data
    long countByDataCadastroAfter(java.util.Date data);

    Optional<Aluno> findByEmail(String email);

    Optional<Aluno> findByTelefone(String telefone);

    // --- INÍCIO DAS NOVAS QUERIES PARA DASHBOARD DO INSTRUTOR ---

    @Query("SELECT COUNT(a) FROM Aluno a JOIN a.matriculaList m WHERE m.instrutor.id = :instrutorId AND a.ativo = true")
    long countAtivosByInstrutorId(@Param("instrutorId") Long instrutorId);

    @Query("SELECT COUNT(a) FROM Aluno a JOIN a.matriculaList m WHERE m.instrutor.id = :instrutorId AND a.ativo = false")
    long countInativosByInstrutorId(@Param("instrutorId") Long instrutorId);

    @Query("SELECT COUNT(a) FROM Aluno a JOIN a.matriculaList m WHERE m.instrutor.id = :instrutorId AND a.dataCadastro > :data")
    long countNovosByInstrutorId(@Param("instrutorId") Long instrutorId, @Param("data") java.util.Date data);

    // ATUALIZADO: Adicionado "WHERE a.ativo = true"
    @Query("SELECT DISTINCT a FROM Aluno a " +
            "LEFT JOIN FETCH a.matriculaList m " +
            "LEFT JOIN FETCH m.plano " +
            "WHERE a.ativo = true")
    List<Aluno> findAllWithMatriculasAndPlanos();


    // Traz Aluno + Endereço + Matrículas + Plano
    @Query("SELECT a FROM Aluno a " +
            "LEFT JOIN FETCH a.endereco " +
            "LEFT JOIN FETCH a.matriculaList m " +
            "LEFT JOIN FETCH m.plano " +
            "WHERE a.id = :id")
    Optional<Aluno> findByIdWithMatriculas(@Param("id") Long id);

    // Busca alunos ATIVOS de um instrutor específico, já trazendo os planos
    @Query("SELECT DISTINCT a FROM Aluno a " +
            "LEFT JOIN FETCH a.matriculaList m " +
            "LEFT JOIN FETCH m.plano p " +
            "WHERE m.instrutor.id = :instrutorId AND a.ativo = true")
    List<Aluno> findAllAtivosByInstrutorIdWithMatriculas(@Param("instrutorId") Long instrutorId);

    // Traz TODOS (Ativos e Inativos) para o relatório
    @Query("SELECT DISTINCT a FROM Aluno a " +
            "LEFT JOIN FETCH a.matriculaList m " +
            "LEFT JOIN FETCH m.plano")
    List<Aluno> findAllParaRelatorio();

}