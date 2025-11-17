package br.unipar.projetointegrador.frotisapi.service;

import br.unipar.projetointegrador.frotisapi.model.Aluno;
import br.unipar.projetointegrador.frotisapi.model.Mensalidade;
import br.unipar.projetointegrador.frotisapi.repository.AlunoRepository;
import br.unipar.projetointegrador.frotisapi.repository.MensalidadeRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class MensalidadeService {

    private final MensalidadeRepository mensalidadeRepository;
    private final AlunoRepository alunoRepository;

    public MensalidadeService(MensalidadeRepository mensalidadeRepository, AlunoRepository alunoRepository) {
        this.mensalidadeRepository = mensalidadeRepository;
        this.alunoRepository = alunoRepository;
    }

    public List<Mensalidade> listarTodas() {
        return mensalidadeRepository.findAll();
    }

    @Transactional
    public Mensalidade pagar(Long id) {
        Mensalidade mensalidade = mensalidadeRepository.findById(id).orElse(null);
        if (mensalidade != null) {
            mensalidade.setPago(true);
            mensalidade.setDataPagamento(new Date()); // Data de hoje
            return mensalidadeRepository.save(mensalidade);
        }
        return null;
    }

    // --- AUTOMATIZAÇÃO ---
    // Roda todos os dias à meia-noite para verificar se precisa gerar mensalidade para o MÊS ATUAL
    @Scheduled(cron = "0 0 0 * * ?")
    public void gerarMensalidadesAutomaticas() {
        List<Aluno> alunosAtivos = alunoRepository.findAllWithMatriculasAndPlanos();

        LocalDate hoje = LocalDate.now();
        int mesAtual = hoje.getMonthValue();
        int anoAtual = hoje.getYear();

        // Para facilitar a comparação de meses
        YearMonth mesAnoAtual = YearMonth.from(hoje);

        for (Aluno aluno : alunosAtivos) {
            // Só processa se tiver dia de vencimento configurado e uma matrícula ativa
            if (aluno.getDiaVencimento() != null && !aluno.getMatriculaList().isEmpty()) {

                // --- NOVA LÓGICA DE CARÊNCIA (MÊS SEGUINTE) ---
                LocalDate dataCadastro = aluno.getDataCadastro().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                YearMonth mesAnoCadastro = YearMonth.from(dataCadastro);

                // Se o mês atual for IGUAL ou ANTERIOR ao mês de cadastro, NÃO gera nada.
                // Ex: Cadastro em 10/Nov. Hoje é 15/Nov. (Nov == Nov) -> Pula.
                // Ex: Cadastro em 10/Nov. Hoje é 01/Dez. (Dez > Nov) -> Gera.
                if (!mesAnoAtual.isAfter(mesAnoCadastro)) {
                    continue;
                }

                // Verifica se JÁ EXISTE uma mensalidade gerada para este aluno neste mês/ano
                boolean existe = mensalidadeRepository.existsByAlunoAndMesAno(aluno.getId(), mesAtual, anoAtual);

                if (!existe) {
                    Mensalidade nova = new Mensalidade();
                    nova.setAluno(aluno);

                    // Pega o valor do plano atual da primeira matrícula
                    Double valorPlano = aluno.getMatriculaList().get(0).getPlano().getValor();
                    nova.setValor(valorPlano);

                    // Define a data de vencimento para o mês atual
                    // O Math.min garante que não quebre em fevereiro (dia 30 vira dia 28)
                    int diaVencimento = Math.min(aluno.getDiaVencimento(), hoje.lengthOfMonth());
                    LocalDate dataVenc = LocalDate.of(anoAtual, mesAtual, diaVencimento);

                    nova.setDataVencimento(Date.from(dataVenc.atStartOfDay(ZoneId.systemDefault()).toInstant()));

                    mensalidadeRepository.save(nova);
                }
            }
        }
    }

    // Método para botão "Gerar Agora" (Testes manuais)
    public void forcarGeracao() {
        gerarMensalidadesAutomaticas();
    }
}