package com.copa.ui;

import com.copa.Torneio;
import com.copa.fase.Fase;
import com.copa.fase.FaseEliminatoria;
import com.copa.fase.FaseGrupos;
import com.copa.model.Partida;
import com.copa.model.Time;
import com.copa.persistencia.ArquivoUtil;

import java.util.List;
import java.util.Scanner;

/**
 * Interface de terminal do sistema. Classe principal: orquestra o loop de menus
 * e delega as operacoes de dominio ao {@link Torneio} e suas {@link Fase}s.
 */
public class Menu {

    private static final String ARQUIVO = "torneio.json";

    // 32 selecoes (nomes sem acento para evitar problemas no console Windows).
    private static final String[] SELECOES_PADRAO = {
            "Catar", "Equador", "Senegal", "Holanda",
            "Inglaterra", "Ira", "EUA", "Pais de Gales",
            "Argentina", "Arabia Saudita", "Mexico", "Polonia",
            "Franca", "Australia", "Dinamarca", "Tunisia",
            "Espanha", "Costa Rica", "Alemanha", "Japao",
            "Belgica", "Canada", "Marrocos", "Croacia",
            "Brasil", "Servia", "Suica", "Camaroes",
            "Portugal", "Gana", "Uruguai", "Coreia do Sul"
    };

    private final Scanner scanner = new Scanner(System.in);
    private Torneio torneio;

    public static void main(String[] args) {
        new Menu().executar();
    }

    private void executar() {
        System.out.println("=========================================");
        System.out.println("  COPA-CLI - Gerenciador de Copa do Mundo");
        System.out.println("=========================================");

        torneio = ArquivoUtil.carregar(ARQUIVO);
        if (torneio == null) {
            System.out.print("Nenhum torneio salvo. Nome do novo torneio: ");
            String nome = scanner.nextLine().trim();
            if (nome.isEmpty()) {
                nome = "Copa do Mundo";
            }
            torneio = new Torneio(nome);
            System.out.println("Torneio \"" + nome + "\" criado.");
        } else {
            System.out.println("Torneio \"" + torneio.getNome() + "\" carregado de " + ARQUIVO + ".");
        }

        boolean sair = false;
        while (!sair) {
            exibirMenuPrincipal();
            int opcao = lerInteiro("Opcao: ");
            switch (opcao) {
                case 1 -> menuTimes();
                case 2 -> menuFaseAtual();
                case 3 -> registrarResultado();
                case 4 -> menuClassificacao();
                case 5 -> avancarFase();
                case 6 -> salvar();
                case 0 -> sair = confirmarSaida();
                default -> System.out.println("Opcao invalida.");
            }
        }
        System.out.println("Ate logo!");
    }

    private void exibirMenuPrincipal() {
        Fase fase = torneio.getFaseAtual();
        System.out.println();
        System.out.println("----------------------------------------");
        System.out.println("Torneio: " + torneio.getNome()
                + " | Fase atual: " + fase.getNome()
                + (fase.isIniciada() ? "" : " (nao iniciada)"));
        System.out.println("Times cadastrados: " + torneio.getTimes().size());
        System.out.println("----------------------------------------");
        System.out.println("[1] Gerenciar times");
        System.out.println("[2] Iniciar fase atual");
        System.out.println("[3] Registrar resultado de partida");
        System.out.println("[4] Ver status / tabela / bracket");
        System.out.println("[5] Avancar para proxima fase");
        System.out.println("[6] Salvar torneio");
        System.out.println("[0] Sair");
    }

    // ----------------------------------------------------------------- Times

    private void menuTimes() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println();
            System.out.println("-- Gerenciar Times (" + torneio.getTimes().size() + ") --");
            System.out.println("[1] Cadastrar time");
            System.out.println("[2] Listar times");
            System.out.println("[3] Remover time");
            System.out.println("[4] Carregar 32 selecoes padrao");
            System.out.println("[0] Voltar");
            int opcao = lerInteiro("Opcao: ");
            switch (opcao) {
                case 1 -> cadastrarTime();
                case 2 -> listarTimes();
                case 3 -> removerTime();
                case 4 -> carregarSelecoesPadrao();
                case 0 -> voltar = true;
                default -> System.out.println("Opcao invalida.");
            }
        }
    }

    private boolean faseGruposIniciada() {
        Fase primeira = torneio.getFases().get(0);
        return primeira.isIniciada();
    }

    private void cadastrarTime() {
        if (faseGruposIniciada()) {
            System.out.println("A fase de grupos ja foi iniciada; nao e possivel alterar os times.");
            return;
        }
        if (torneio.getTimes().size() >= 32) {
            System.out.println("Ja ha 32 times cadastrados (limite da Copa).");
            return;
        }
        System.out.print("Nome do time: ");
        String nome = scanner.nextLine().trim();
        if (nome.isEmpty()) {
            System.out.println("Nome vazio. Operacao cancelada.");
            return;
        }
        System.out.print("Pais: ");
        String pais = scanner.nextLine().trim();
        if (pais.isEmpty()) {
            pais = nome;
        }
        Time novo = new Time(nome, pais);
        if (torneio.getTimes().contains(novo)) {
            System.out.println("Esse time ja esta cadastrado.");
            return;
        }
        torneio.adicionarTime(novo);
        System.out.println("Time cadastrado. Total: " + torneio.getTimes().size() + "/32.");
    }

    private void listarTimes() {
        List<Time> times = torneio.getTimes();
        if (times.isEmpty()) {
            System.out.println("Nenhum time cadastrado.");
            return;
        }
        System.out.println("Times (" + times.size() + "):");
        for (int i = 0; i < times.size(); i++) {
            Time t = times.get(i);
            String grupo = (t.getGrupo() == null || t.getGrupo().isEmpty()) ? "-" : t.getGrupo();
            System.out.printf("  [%2d] %-18s (%s)  grupo %s%n", i + 1, t.getNome(), t.getPais(), grupo);
        }
    }

    private void removerTime() {
        if (faseGruposIniciada()) {
            System.out.println("A fase de grupos ja foi iniciada; nao e possivel alterar os times.");
            return;
        }
        listarTimes();
        if (torneio.getTimes().isEmpty()) {
            return;
        }
        int idx = lerInteiro("Numero do time a remover (0 cancela): ");
        if (idx <= 0 || idx > torneio.getTimes().size()) {
            System.out.println("Cancelado.");
            return;
        }
        Time removido = torneio.getTimes().remove(idx - 1);
        System.out.println("Removido: " + removido.getNome() + ".");
    }

    private void carregarSelecoesPadrao() {
        if (faseGruposIniciada()) {
            System.out.println("A fase de grupos ja foi iniciada; nao e possivel alterar os times.");
            return;
        }
        torneio.getTimes().clear();
        for (String nome : SELECOES_PADRAO) {
            torneio.adicionarTime(new Time(nome, nome));
        }
        System.out.println(SELECOES_PADRAO.length + " selecoes carregadas.");
    }

    // ------------------------------------------------------------ Fase atual

    private void menuFaseAtual() {
        Fase fase = torneio.getFaseAtual();
        if (fase.isIniciada()) {
            System.out.println("A fase \"" + fase.getNome() + "\" ja esta em andamento.");
            return;
        }
        if (fase instanceof FaseGrupos) {
            if (torneio.getTimes().size() != 32) {
                System.out.println("E necessario ter exatamente 32 times (atual: "
                        + torneio.getTimes().size() + "). Use [1] Gerenciar times.");
                return;
            }
            torneio.iniciarPrimeiraFase();
            System.out.println("Fase de grupos iniciada! Grupos sorteados e partidas geradas.");
            fase.exibirStatus();
        } else {
            System.out.println("Esta fase e iniciada automaticamente ao avancar (opcao [5]).");
        }
    }

    // ----------------------------------------------------------- Resultados

    private void registrarResultado() {
        Fase fase = torneio.getFaseAtual();
        if (!fase.isIniciada()) {
            System.out.println("A fase atual ainda nao foi iniciada (opcao [2]).");
            return;
        }
        List<Partida> partidas = fase.getPartidas();
        System.out.println();
        System.out.println("Partidas de " + fase.getNome() + ":");
        for (int i = 0; i < partidas.size(); i++) {
            System.out.printf("  [%d] %s%n", i + 1, partidas.get(i).descricao());
        }
        int idx = lerInteiro("Numero da partida (0 cancela): ");
        if (idx <= 0 || idx > partidas.size()) {
            System.out.println("Cancelado.");
            return;
        }
        int posicao = idx - 1;
        Partida partida = partidas.get(posicao);
        int golsCasa = lerInteiro("Gols " + partida.getTimeCasa().getNome() + ": ");
        int golsVisitante = lerInteiro("Gols " + partida.getTimeVisitante().getNome() + ": ");
        if (golsCasa < 0 || golsVisitante < 0) {
            System.out.println("Placar invalido.");
            return;
        }
        fase.registrarResultado(posicao, golsCasa, golsVisitante);

        // No mata-mata um empate exige decisao por penaltis.
        if (fase instanceof FaseEliminatoria && partida.isEmpateTempoNormal()) {
            System.out.println("Empate no tempo normal. Decisao por penaltis:");
            int p1;
            int p2;
            do {
                p1 = lerInteiro("Penaltis " + partida.getTimeCasa().getNome() + ": ");
                p2 = lerInteiro("Penaltis " + partida.getTimeVisitante().getNome() + ": ");
                if (p1 == p2) {
                    System.out.println("Os penaltis nao podem empatar. Informe um vencedor.");
                }
            } while (p1 == p2 || p1 < 0 || p2 < 0);
            partida.registrarPenaltis(p1, p2);
        }
        System.out.println("Resultado registrado: " + partida.descricao());
    }

    // --------------------------------------------------------- Classificacao

    private void menuClassificacao() {
        Fase fase = torneio.getFaseAtual();
        if (!fase.isIniciada()) {
            System.out.println("A fase atual ainda nao foi iniciada (opcao [2]).");
            return;
        }
        System.out.println();
        fase.exibirStatus(); // chamada polimorfica: tabela (grupos) ou bracket (mata-mata)
    }

    // ------------------------------------------------------------ Avancar

    private void avancarFase() {
        Fase fase = torneio.getFaseAtual();
        if (!fase.isIniciada()) {
            System.out.println("A fase atual ainda nao foi iniciada (opcao [2]).");
            return;
        }
        if (!fase.todasEncerradas()) {
            System.out.println("Ainda ha partidas pendentes em " + fase.getNome() + ".");
            return;
        }
        if (torneio.isUltimaFase()) {
            Time campeao = torneio.getCampeao();
            if (campeao != null) {
                System.out.println();
                System.out.println("*****************************************");
                System.out.println("   CAMPEAO DA COPA: " + campeao.getNome());
                System.out.println("*****************************************");
            }
            return;
        }
        torneio.avancarFase();
        System.out.println("Avancou para: " + torneio.getFaseAtual().getNome() + ".");
        torneio.getFaseAtual().exibirStatus();
    }

    // --------------------------------------------------------------- Salvar

    private void salvar() {
        ArquivoUtil.salvar(torneio, ARQUIVO);
        System.out.println("Torneio salvo em " + ARQUIVO + ".");
    }

    private boolean confirmarSaida() {
        System.out.print("Deseja salvar antes de sair? [s/N]: ");
        String resp = scanner.nextLine().trim().toLowerCase();
        if (resp.equals("s") || resp.equals("sim")) {
            salvar();
        }
        return true;
    }

    // --------------------------------------------------------------- Helpers

    private int lerInteiro(String prompt) {
        System.out.print(prompt);
        String linha = scanner.nextLine().trim();
        try {
            return Integer.parseInt(linha);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
