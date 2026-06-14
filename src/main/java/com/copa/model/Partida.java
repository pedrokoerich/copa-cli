package com.copa.model;

/**
 * Confronto entre dois times.
 *
 * <p>Na fase de grupos um empate e um resultado valido. Na fase eliminatoria
 * nao pode haver empate: quando o tempo normal termina igual, a decisao por
 * penaltis e registrada em {@link #penaltisCasa}/{@link #penaltisVisitante} e
 * usada por {@link #getVencedor()}.</p>
 */
public class Partida {

    private Time timeCasa;
    private Time timeVisitante;
    private int golsCasa;
    private int golsVisitante;
    private int penaltisCasa;
    private int penaltisVisitante;
    private boolean tevePenaltis;
    private StatusPartida status;

    public Partida() {
        this.status = StatusPartida.AGENDADA;
    }

    public Partida(Time timeCasa, Time timeVisitante) {
        this.timeCasa = timeCasa;
        this.timeVisitante = timeVisitante;
        this.status = StatusPartida.AGENDADA;
    }

    /**
     * Registra o placar do tempo normal e encerra a partida.
     */
    public void registrarResultado(int golsCasa, int golsVisitante) {
        this.golsCasa = golsCasa;
        this.golsVisitante = golsVisitante;
        this.status = StatusPartida.ENCERRADA;
    }

    /**
     * Registra a decisao por penaltis (somente fase eliminatoria com empate).
     */
    public void registrarPenaltis(int penaltisCasa, int penaltisVisitante) {
        this.penaltisCasa = penaltisCasa;
        this.penaltisVisitante = penaltisVisitante;
        this.tevePenaltis = true;
    }

    /**
     * @return o time vencedor, decidindo por penaltis em caso de empate no
     *         tempo normal; {@code null} se a partida ainda terminou empatada
     *         (resultado valido apenas na fase de grupos).
     */
    public Time getVencedor() {
        if (status != StatusPartida.ENCERRADA) {
            return null;
        }
        if (golsCasa > golsVisitante) {
            return timeCasa;
        }
        if (golsVisitante > golsCasa) {
            return timeVisitante;
        }
        // Empate no tempo normal: decide pelos penaltis, se houver.
        if (tevePenaltis) {
            return penaltisCasa >= penaltisVisitante ? timeCasa : timeVisitante;
        }
        return null;
    }

    public boolean isEmpateTempoNormal() {
        return status == StatusPartida.ENCERRADA && golsCasa == golsVisitante;
    }

    public Time getTimeCasa() {
        return timeCasa;
    }

    public void setTimeCasa(Time timeCasa) {
        this.timeCasa = timeCasa;
    }

    public Time getTimeVisitante() {
        return timeVisitante;
    }

    public void setTimeVisitante(Time timeVisitante) {
        this.timeVisitante = timeVisitante;
    }

    public int getGolsCasa() {
        return golsCasa;
    }

    public int getGolsVisitante() {
        return golsVisitante;
    }

    public int getPenaltisCasa() {
        return penaltisCasa;
    }

    public int getPenaltisVisitante() {
        return penaltisVisitante;
    }

    public boolean isTevePenaltis() {
        return tevePenaltis;
    }

    public StatusPartida getStatus() {
        return status;
    }

    /**
     * Linha textual do confronto, ex.: "Brasil 2 x 1 Servia" ou, com penaltis,
     * "Brasil 1 (4) x (2) 1 Argentina".
     */
    public String descricao() {
        if (status == StatusPartida.AGENDADA) {
            return timeCasa.getNome() + " x " + timeVisitante.getNome() + "  [AGENDADA]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(timeCasa.getNome()).append(" ").append(golsCasa);
        if (tevePenaltis) {
            sb.append(" (").append(penaltisCasa).append(")");
        }
        sb.append(" x ");
        if (tevePenaltis) {
            sb.append("(").append(penaltisVisitante).append(") ");
        }
        sb.append(golsVisitante).append(" ").append(timeVisitante.getNome());
        Time vencedor = getVencedor();
        if (vencedor != null) {
            sb.append("  => ").append(vencedor.getNome());
        }
        return sb.toString();
    }
}
