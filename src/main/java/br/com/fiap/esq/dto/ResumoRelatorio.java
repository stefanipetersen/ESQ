package br.com.fiap.esq.dto;

import java.math.BigDecimal;
import java.util.List;

//Dados prontos para o dashboard e para a tela de relatórios
public record ResumoRelatorio(
        int mes,
        int ano,
        BigDecimal saldoDisponivel,
        BigDecimal variacaoSaldo,
        BigDecimal totalReceitas,
        BigDecimal totalDespesas,
        BigDecimal economia,
        BigDecimal percentualEconomia,
        List<GastoCategoria> gastosPorCategoria,
        List<FluxoDia> fluxoCaixa,
        BigDecimal totalGuardado,
        BigDecimal totalObjetivo,
        List<EvolucaoMes> evolucaoMetas) {

    public record GastoCategoria(int idCategoria, String categoria, BigDecimal valor, BigDecimal percentual) {}

    //Receitas e despesas acumuladas até o dia
    public record FluxoDia(int dia, BigDecimal receitas, BigDecimal despesas) {}

    public record EvolucaoMes(int ano, int mes, BigDecimal valor) {}
}
