package com.copa.fase;

import com.copa.model.ClassificacaoGrupo;
import com.copa.model.Partida;
import com.copa.model.StatusPartida;
import com.copa.model.Time;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Fase de grupos: distribui 32 times em 8 grupos (A..H), com turno unico
 * (todos contra todos) dentro de cada grupo, e classifica os 2 melhores de cada.
 */
public class FaseGrupos extends Fase {

    private static final String[] LETRAS = {"A", "B", "C", "D", "E", "F", "G", "H"};
    private static final int TIMES_POR_GRUPO = 4;

    private Map<String, List<Time>> grupos = new LinkedHashMap<>();
    private Map<String, List<ClassificacaoGrupo>> tabelas = new LinkedHashMap<>();

    public FaseGrupos() {
        super("Fase de Grupos");
    }

    @Override
    public void iniciar(List<Time> times) {
        if (times.size() != LETRAS.length * TIMES_POR_GRUPO) {
            throw new IllegalArgumentException(
                    "A fase de grupos exige exatamente 32 times (recebidos: " + times.size() + ").");
        }
        grupos.clear();
        tabelas.clear();
        partidas.clear();

        // Distribui 4 times por grupo, na ordem recebida.
        for (int g = 0; g < LETRAS.length; g++) {
            String letra = LETRAS[g];
            List<Time> doGrupo = new ArrayList<>();
            for (int i = 0; i < TIMES_POR_GRUPO; i++) {
                Time time = times.get(g * TIMES_POR_GRUPO + i);
                time.setGrupo(letra);
                doGrupo.add(time);
            }
            grupos.put(letra, doGrupo);

            // Round-robin: 6 partidas por grupo.
            for (int i = 0; i < doGrupo.size(); i++) {
                for (int j = i + 1; j < doGrupo.size(); j++) {
                    partidas.add(new Partida(doGrupo.get(i), doGrupo.get(j)));
                }
            }
        }
        recalcularTabelas();
    }

    /**
     * Reconstroi as tabelas de classificacao a partir das partidas encerradas.
     */
    private void recalcularTabelas() {
        tabelas.clear();
        Map<Time, ClassificacaoGrupo> indice = new LinkedHashMap<>();
        for (Map.Entry<String, List<Time>> e : grupos.entrySet()) {
            List<ClassificacaoGrupo> tabela = new ArrayList<>();
            for (Time t : e.getValue()) {
                ClassificacaoGrupo c = new ClassificacaoGrupo(t);
                tabela.add(c);
                indice.put(t, c);
            }
            tabelas.put(e.getKey(), tabela);
        }
        for (Partida p : partidas) {
            if (p.getStatus() != StatusPartida.ENCERRADA) {
                continue;
            }
            indice.get(p.getTimeCasa()).registrar(p.getGolsCasa(), p.getGolsVisitante());
            indice.get(p.getTimeVisitante()).registrar(p.getGolsVisitante(), p.getGolsCasa());
        }
        for (List<ClassificacaoGrupo> tabela : tabelas.values()) {
            tabela.sort(ClassificacaoGrupo.POR_DESEMPENHO);
        }
    }

    @Override
    public void exibirStatus() {
        recalcularTabelas();
        System.out.println("==== " + nome + " ====");
        for (Map.Entry<String, List<ClassificacaoGrupo>> e : tabelas.entrySet()) {
            System.out.println();
            System.out.println("Grupo " + e.getKey());
            System.out.printf("  %-18s %3s %3s %3s %3s %3s %3s %3s %4s%n",
                    "Time", "P", "J", "V", "E", "D", "GP", "GC", "SG");
            int pos = 1;
            for (ClassificacaoGrupo c : e.getValue()) {
                String marcador = pos <= 2 ? ">" : " ";
                System.out.printf("%s %d %-16s %3d %3d %3d %3d %3d %3d %3d %4d%n",
                        marcador, pos, c.getTime().getNome(),
                        c.getPontos(), c.getJogos(), c.getVitorias(), c.getEmpates(),
                        c.getDerrotas(), c.getGolsPro(), c.getGolsContra(), c.getSaldoGols());
                pos++;
            }
        }
        System.out.println();
        System.out.println("(> = classificado para as oitavas)");
    }

    /**
     * Top-2 de cada grupo, devolvidos na ordem cruzada de chaveamento (padrao FIFA):
     * 1A,2B, 1C,2D, 1E,2F, 1G,2H, 1B,2A, 1D,2C, 1F,2E, 1H,2G. Assim o pareamento
     * consecutivo na fase eliminatoria produz os cruzamentos corretos.
     */
    @Override
    public List<Time> getClassificados() {
        recalcularTabelas();
        // Indexa, por letra, o 1o e o 2o colocado.
        Map<String, Time> primeiros = new LinkedHashMap<>();
        Map<String, Time> segundos = new LinkedHashMap<>();
        for (Map.Entry<String, List<ClassificacaoGrupo>> e : tabelas.entrySet()) {
            primeiros.put(e.getKey(), e.getValue().get(0).getTime());
            segundos.put(e.getKey(), e.getValue().get(1).getTime());
        }
        // Pares de cruzamento (vencedor de X x vice de Y).
        String[][] cruzamentos = {
                {"A", "B"}, {"C", "D"}, {"E", "F"}, {"G", "H"},
                {"B", "A"}, {"D", "C"}, {"F", "E"}, {"H", "G"}
        };
        List<Time> classificados = new ArrayList<>();
        for (String[] par : cruzamentos) {
            classificados.add(primeiros.get(par[0]));
            classificados.add(segundos.get(par[1]));
        }
        return classificados;
    }

    public Map<String, List<Time>> getGrupos() {
        return grupos;
    }

    public Map<String, List<ClassificacaoGrupo>> getTabelas() {
        return tabelas;
    }
}
