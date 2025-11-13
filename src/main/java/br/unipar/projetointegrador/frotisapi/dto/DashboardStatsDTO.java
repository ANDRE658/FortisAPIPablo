package br.unipar.projetointegrador.frotisapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardStatsDTO {
    private long ativos;
    private long inativos;
    private long novos;
}