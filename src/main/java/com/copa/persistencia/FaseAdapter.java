package com.copa.persistencia;

import com.copa.fase.Fase;
import com.copa.fase.FaseEliminatoria;
import com.copa.fase.FaseGrupos;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Adaptador Gson para a hierarquia polimorfica {@link Fase}.
 *
 * <p>Classe extra justificada: o Gson nao grava o tipo concreto de um objeto
 * referenciado por sua superclasse. Como {@code Torneio} possui uma
 * {@code List<Fase>} com instancias de {@link FaseGrupos} e
 * {@link FaseEliminatoria}, este adaptador grava um discriminador {@code "tipo"}
 * e o payload concreto em {@code "dados"}, e reconstroi a subclasse correta na
 * leitura. Serializa o payload pelo tipo concreto (que nao tem adaptador
 * registrado), evitando recursao infinita.</p>
 */
public class FaseAdapter implements JsonSerializer<Fase>, JsonDeserializer<Fase> {

    private static final String CAMPO_TIPO = "tipo";
    private static final String CAMPO_DADOS = "dados";

    @Override
    public JsonElement serialize(Fase src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject wrapper = new JsonObject();
        wrapper.addProperty(CAMPO_TIPO, src.getClass().getSimpleName());
        wrapper.add(CAMPO_DADOS, context.serialize(src, src.getClass()));
        return wrapper;
    }

    @Override
    public Fase deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject wrapper = json.getAsJsonObject();
        String tipo = wrapper.get(CAMPO_TIPO).getAsString();
        JsonElement dados = wrapper.get(CAMPO_DADOS);
        switch (tipo) {
            case "FaseGrupos":
                return context.deserialize(dados, FaseGrupos.class);
            case "FaseEliminatoria":
                return context.deserialize(dados, FaseEliminatoria.class);
            default:
                throw new JsonParseException("Tipo de Fase desconhecido: " + tipo);
        }
    }
}
