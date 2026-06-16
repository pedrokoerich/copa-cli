package com.copa.fase;

import com.copa.model.Partida;
import com.copa.model.StatusPartida;
import com.copa.model.Time;

import java.util.ArrayList;
import java.util.List;

/**
 * Fase eliminatoria (mata-mata): "Oitavas", "Quartas", "Semifinal" ou "Final".
 *
 * <p>Os times chegam ja ordenados pela fase anterior, portanto basta parear
 * confrontos consecutivos (0x1, 2x3, ...) para montar o bracket correto. Os
 * vencedores sao devolvidos na ordem dos confrontos, alimentando ja ordenada a
 * proxima fase eliminatoria.</p>
 */
public class FaseEliminatoria extends Fase {

    private String nomeFase;

    public FaseEliminatoria() {
        super("Fase Eliminatoria");
    }

    public FaseEliminatoria(String nomeFase) {
        super(nomeFase);
        this.nomeFase = nomeFase;
    }

    @Override
    public void iniciar(List<Time> times) {
        if (times.size() < 2 || times.size() % 2 != 0) {
            throw new IllegalArgumentException(
                    nomeFase + " exige um numero par de times (recebidos: " + times.size() + ").");
        }
        partidas.clear();
        for (int i = 0; i < times.size(); i += 2) {
            partidas.add(new Partida(times.get(i), times.get(i + 1)));
        }
    }

    @Override
    public void exibirStatus() {
        System.out.println("==== " + nomeFase + " ====");
        if (partidas.isEmpty()) {
            System.out.println("(fase ainda nao iniciada)");
            return;
        }
        System.out.println();
        for (int i = 0; i < partidas.size(); i++) {
            Partida p = partidas.get(i);
            System.out.printf("  [%d] %s%n", i + 1, p.descricao());
        }
        System.out.println();
        if (todasEncerradas()) {
            List<Time> vencedores = getClassificados();
            if (vencedores.size() == 1) {
                System.out.println("CAMPEAO: " + vencedores.get(0).getNome());
            } else {
                System.out.print("Avancam: ");
                List<String> nomes = new ArrayList<>();
                for (Time t : vencedores) {
                    nomes.add(t.getNome());
                }
                System.out.println(String.join(", ", nomes));
            }
        }
    }

    @Override
    public List<Time> getClassificados() {
        List<Time> vencedores = new ArrayList<>();
        for (Partida p : partidas) {
            if (p.getStatus() == StatusPartida.ENCERRADA) {
                Time v = p.getVencedor();
                if (v != null) {
                    vencedores.add(v);
                }
            }
        }
        return vencedores;
    }

    public String getNomeFase() {
        return nomeFase;
    }
}
