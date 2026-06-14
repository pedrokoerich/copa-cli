package com.copa.model;

import java.util.Objects;

/**
 * Representa uma selecao participante da Copa.
 *
 * <p>O {@link #equals(Object)}/{@link #hashCode()} sao definidos por nome+pais
 * porque a persistencia via Gson recria os objetos {@code Time} ao carregar
 * (nao preserva identidade de referencia). Por isso todo o sistema compara
 * times por {@code equals}, nunca por {@code ==}.</p>
 */
public class Time {

    private String nome;
    private String pais;
    private String grupo; // "A".."H"; nulo/vazio enquanto nao sorteado

    public Time() {
    }

    public Time(String nome, String pais) {
        this.nome = nome;
        this.pais = pais;
    }

    public Time(String nome, String pais, String grupo) {
        this.nome = nome;
        this.pais = pais;
        this.grupo = grupo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Time)) {
            return false;
        }
        Time outro = (Time) o;
        return Objects.equals(nome, outro.nome) && Objects.equals(pais, outro.pais);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, pais);
    }

    @Override
    public String toString() {
        return nome;
    }
}
