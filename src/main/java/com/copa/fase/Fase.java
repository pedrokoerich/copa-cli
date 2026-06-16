package com.copa.fase;

import com.copa.model.Partida;
import com.copa.model.StatusPartida;
import com.copa.model.Time;

import java.util.ArrayList;
import java.util.List;

/**
 * Etapa do torneio. Classe base abstrata que define o contrato polimorfico
 * usado por {@link com.copa.Torneio}: a cada etapa o torneio apenas chama
 * {@link #iniciar(List)}, {@link #exibirStatus()} e {@link #getClassificados()}
 * sem conhecer se a etapa concreta e fase de grupos ou mata-mata.
 */
public abstract class Fase {

    protected String nome;
    protected List<Partida> partidas = new ArrayList<>();

    protected Fase(String nome) {
        this.nome = nome;
    }

    /**
     * Monta os confrontos da fase a partir dos times recebidos da etapa anterior.
     */
    public abstract void iniciar(List<Time> times);

    /**
     * Imprime no terminal o estado atual da fase (tabela de grupos ou bracket).
     */
    public abstract void exibirStatus();

    /**
     * @return os times que avancam desta fase para a proxima.
     */
    public abstract List<Time> getClassificados();

    /**
     * Registra o placar de uma das partidas da fase.
     */
    public void registrarResultado(int indicePartida, int golsCasa, int golsVisitante) {
        if (indicePartida < 0 || indicePartida >= partidas.size()) {
            throw new IndexOutOfBoundsException("Partida inexistente: " + (indicePartida + 1));
        }
        partidas.get(indicePartida).registrarResultado(golsCasa, golsVisitante);
    }

    /**
     * @return {@code true} se a fase ja foi iniciada e todas as partidas estao encerradas.
     */
    public boolean todasEncerradas() {
        if (partidas.isEmpty()) {
            return false;
        }
        return partidas.stream().allMatch(p -> p.getStatus() == StatusPartida.ENCERRADA);
    }

    public boolean isIniciada() {
        return !partidas.isEmpty();
    }

    public String getNome() {
        return nome;
    }

    public List<Partida> getPartidas() {
        return partidas;
    }
}
