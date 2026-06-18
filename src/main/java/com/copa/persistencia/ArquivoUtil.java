package com.copa.persistencia;

import com.copa.Torneio;
import com.copa.fase.Fase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Persistencia do {@link Torneio} em JSON (Gson), com suporte a polimorfismo
 * de {@link Fase} via {@link FaseAdapter}.
 */
public final class ArquivoUtil {

    private ArquivoUtil() {
    }

    private static Gson criarGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Fase.class, new FaseAdapter())
                .create();
    }

    /**
     * Salva o torneio completo no caminho informado.
     */
    public static void salvar(Torneio torneio, String caminho) {
        Gson gson = criarGson();
        try (Writer writer = Files.newBufferedWriter(Path.of(caminho), StandardCharsets.UTF_8)) {
            gson.toJson(torneio, writer);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar o torneio em " + caminho + ": " + e.getMessage(), e);
        }
    }

    /**
     * Carrega o torneio do caminho informado.
     *
     * @return o torneio carregado, ou {@code null} se o arquivo nao existir.
     */
    public static Torneio carregar(String caminho) {
        Path path = Path.of(caminho);
        if (!Files.exists(path)) {
            return null;
        }
        Gson gson = criarGson();
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, Torneio.class);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao carregar o torneio de " + caminho + ": " + e.getMessage(), e);
        }
    }
}
