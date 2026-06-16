package com.copa;

import com.copa.fase.Fase;
import com.copa.fase.FaseEliminatoria;
import com.copa.fase.FaseGrupos;
import com.copa.model.Time;

import java.util.ArrayList;
import java.util.List;

/**
 * Orquestrador do torneio. Mantem os times e a sequencia de fases por
 * <b>composicao</b> ({@code List<Fase>}) e avanca de uma fase para a outra
 * usando <b>polimorfismo</b>: nao conhece se a fase atual e grupo ou mata-mata.
 */
public class Torneio {

    private String nome;
    private List<Time> times = new ArrayList<>();
    private List<Fase> fases = new ArrayList<>();
    private int faseAtual;

    public Torneio() {
    }

    public Torneio(String nome) {
        this.nome = nome;
        montarFases();
    }

    /**
     * Sequencia fixa do torneio: grupos -> oitavas -> quartas -> semifinal -> final.
     */
    private void montarFases() {
        fases.add(new FaseGrupos());
        fases.add(new FaseEliminatoria("Oitavas de Final"));
        fases.add(new FaseEliminatoria("Quartas de Final"));
        fases.add(new FaseEliminatoria("Semifinal"));
        fases.add(new FaseEliminatoria("Final"));
        faseAtual = 0;
    }

    public Fase getFaseAtual() {
        return fases.get(faseAtual);
    }

    public boolean isUltimaFase() {
        return faseAtual >= fases.size() - 1;
    }

    /**
     * Inicia a primeira fase (grupos) com todos os times cadastrados.
     */
    public void iniciarPrimeiraFase() {
        fases.get(faseAtual).iniciar(times);
    }

    /**
     * Coracao do polimorfismo: pega os classificados da fase atual e inicia a
     * proxima fase, sem saber qual tipo concreto de {@link Fase} esta envolvido.
     */
    public void avancarFase() {
        if (isUltimaFase()) {
            throw new IllegalStateException("O torneio ja esta na fase final.");
        }
        if (!getFaseAtual().todasEncerradas()) {
            throw new IllegalStateException(
                    "Existem partidas pendentes na fase atual: " + getFaseAtual().getNome());
        }
        List<Time> classificados = fases.get(faseAtual).getClassificados();
        faseAtual++;
        fases.get(faseAtual).iniciar(classificados);
    }

    /**
     * @return o campeao quando a final estiver encerrada; senao {@code null}.
     */
    public Time getCampeao() {
        if (isUltimaFase() && getFaseAtual().todasEncerradas()) {
            List<Time> vencedores = getFaseAtual().getClassificados();
            if (!vencedores.isEmpty()) {
                return vencedores.get(0);
            }
        }
        return null;
    }

    public void adicionarTime(Time time) {
        times.add(time);
    }

    public boolean removerTime(Time time) {
        return times.remove(time);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Time> getTimes() {
        return times;
    }

    public List<Fase> getFases() {
        return fases;
    }

    public int getFaseAtualIndice() {
        return faseAtual;
    }
}
