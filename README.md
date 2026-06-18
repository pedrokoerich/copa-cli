# copa-cli

Sistema de Gerenciamento de Copa do Mundo — aplicacao de **terminal interativo** em Java.
Trabalho universitario de POO com foco em arquitetura orientada a objetos: **heranca,
polimorfismo, encapsulamento e composicao**.

O sistema conduz uma Copa do Mundo completa: cadastro de 32 selecoes, fase de grupos com
tabela de classificacao e mata-mata (oitavas → quartas → semifinal → final), com persistencia
em arquivo JSON.

---

## Pre-requisitos

- **JDK 21+** (desenvolvido e testado com JDK 24)
- **Apache Maven 3.8+** (usado apenas para baixar o Gson e gerar o JAR executavel)

Verifique:

```bash
java -version
mvn -version
```

> Em sistemas onde o Maven nao acha o JDK, defina `JAVA_HOME` apontando para a pasta do JDK
> (ex.: `C:\Program Files\Java\jdk-24`).

---

## Compilacao

Na raiz do projeto (onde fica o `pom.xml`):

```bash
mvn clean package
```

Isso gera um JAR executavel com a dependencia Gson embutida em `target/copa-cli.jar`.

---

## Execucao

```bash
java -jar target/copa-cli.jar
```

Ao iniciar, o programa verifica se existe um `torneio.json` no diretorio atual:
carrega o torneio salvo ou cria um novo (pedindo o nome).

### Menu principal

```
[1] Gerenciar times          (cadastrar / listar / remover / carregar 32 selecoes padrao)
[2] Iniciar fase atual       (sorteia grupos e gera as partidas da fase de grupos)
[3] Registrar resultado de partida
[4] Ver status / tabela / bracket
[5] Avancar para proxima fase (so com todas as partidas encerradas)
[6] Salvar torneio
[0] Sair
```

### Fluxo tipico

1. `[1]` → `[4] Carregar 32 selecoes padrao` (ou cadastre manualmente os 32 times).
2. `[2]` Iniciar fase atual → os grupos A–H sao sorteados e as partidas geradas.
3. `[3]` Registrar resultados de todas as partidas da fase de grupos.
4. `[4]` Conferir as tabelas dos grupos (classificados marcados com `>`).
5. `[5]` Avancar → as oitavas sao montadas com os classificados.
6. Repita registrar/avancar ate a Final. Ao encerra-la, o campeao e exibido.
7. `[6]` Salvar a qualquer momento.

No mata-mata, se uma partida terminar empatada, o sistema solicita a **decisao por penaltis**.

---

## Arquitetura

```
com.copa
├── model
│   ├── Time                 selecao (nome, pais, grupo); equals/hashCode por nome+pais
│   ├── StatusPartida        enum AGENDADA | ENCERRADA
│   ├── Partida              placar + penaltis; getVencedor()
│   └── ClassificacaoGrupo   estatisticas do time no grupo + Comparator de ordenacao
├── fase
│   ├── Fase                 (abstract) contrato polimorfico das etapas
│   ├── FaseGrupos           8 grupos, round-robin, top-2 classificados
│   └── FaseEliminatoria     bracket de mata-mata (Oitavas..Final)
├── Torneio                  orquestrador: List<Fase> (composicao) + avancarFase()
├── persistencia
│   ├── ArquivoUtil          salvar/carregar Torneio em JSON (Gson)
│   └── FaseAdapter          adaptador Gson para o polimorfismo de Fase
└── ui
    └── Menu                 classe main + loop de menus (Scanner)
```

### Onde cada pilar de POO aparece

- **Heranca**: `FaseGrupos` e `FaseEliminatoria` estendem `Fase`.
- **Polimorfismo (nucleo do projeto)**: em `Torneio.avancarFase()` o torneio pega os
  classificados da fase atual e inicia a proxima **sem saber** se e grupo ou mata-mata:

  ```java
  List<Time> classificados = fases.get(faseAtual).getClassificados();
  faseAtual++;
  fases.get(faseAtual).iniciar(classificados);
  ```

  O `Menu` tambem usa polimorfismo ao chamar `fase.exibirStatus()` — imprime tabela de grupos
  ou bracket conforme o tipo concreto.
- **Encapsulamento**: todos os atributos sao privados/protegidos, acessados por getters/setters
  e por metodos de comportamento (`registrarResultado`, `registrar`, `getVencedor`).
- **Composicao**: `Torneio` é composto por `List<Fase>` e `List<Time>`; cada `Fase` é composta
  por `List<Partida>`.

### Chaveamento do mata-mata

`FaseGrupos.getClassificados()` devolve os 16 classificados ja na **ordem cruzada padrao FIFA**
(1A×2B, 1C×2D, …, 1H×2G). Assim, `FaseEliminatoria.iniciar()` apenas pareia confrontos
consecutivos e o bracket sai correto em todas as rodadas seguintes.

---

## Persistencia

O torneio completo e serializado em `torneio.json` (legivel) via **Gson**. Como `Torneio`
possui uma `List<Fase>` polimorfica, a classe `FaseAdapter` grava um discriminador de tipo
(`"tipo": "FaseGrupos" | "FaseEliminatoria"`) e reconstroi a subclasse correta na leitura —
o Gson, por padrao, nao preserva o tipo concreto de objetos referenciados pela superclasse.

> Observacao de projeto: como o Gson recria os objetos `Time` ao carregar (sem preservar
> identidade de referencia), `Time` define `equals`/`hashCode` por nome+pais e todo o sistema
> compara times por valor.

---

## Diagrama UML

O diagrama de classes completo (PlantUML) esta em [`docs/uml.puml`](docs/uml.puml).
Renderize em <https://www.plantuml.com/plantuml> ou com a extensao PlantUML do VS Code.
