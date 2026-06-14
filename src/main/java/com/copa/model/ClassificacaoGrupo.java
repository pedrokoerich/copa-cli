package com.copa.model;

import java.util.Comparator;

/**
 * Estatisticas acumuladas de um {@link Time} dentro de um grupo.
 *
 * <p>Usada para montar a tabela de classificacao de cada grupo. A ordenacao
 * segue o criterio da Copa: pontos, depois saldo de gols, depois gols pro.</p>
 */
public class ClassificacaoGrupo {

    private Time time;
    private int pontos;
    private int vitorias;
    private int empates;
    private int derrotas;
    private int golsPro;
    private int golsContra;

    /**
     * Comparador de classificacao (ordem decrescente de desempenho).
     */
    public static final Comparator<ClassificacaoGrupo> POR_DESEMPENHO =
            Comparator.comparingInt(ClassificacaoGrupo::getPontos)
                    .thenComparingInt(ClassificacaoGrupo::getSaldoGols)
                    .thenComparingInt(ClassificacaoGrupo::getGolsPro)
                    .reversed();

    public ClassificacaoGrupo() {
    }

    public ClassificacaoGrupo(Time time) {
        this.time = time;
    }

    /**
     * Contabiliza o resultado de uma partida do ponto de vista deste time.
     *
     * @param golsFeitos  gols marcados por este time na partida
     * @param golsSofridos gols sofridos por este time na partida
     */
    public void registrar(int golsFeitos, int golsSofridos) {
        golsPro += golsFeitos;
        golsContra += golsSofridos;
        if (golsFeitos > golsSofridos) {
            vitorias++;
            pontos += 3;
        } else if (golsFeitos == golsSofridos) {
            empates++;
            pontos += 1;
        } else {
            derrotas++;
        }
    }

    public int getSaldoGols() {
        return golsPro - golsContra;
    }

    public Time getTime() {
        return time;
    }

    public int getPontos() {
        return pontos;
    }

    public int getVitorias() {
        return vitorias;
    }

    public int getEmpates() {
        return empates;
    }

    public int getDerrotas() {
        return derrotas;
    }

    public int getGolsPro() {
        return golsPro;
    }

    public int getGolsContra() {
        return golsContra;
    }

    public int getJogos() {
        return vitorias + empates + derrotas;
    }
}
