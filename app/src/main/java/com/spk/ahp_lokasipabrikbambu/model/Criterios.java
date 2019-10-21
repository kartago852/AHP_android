package com.spk.ahp_lokasipabrikbambu.model;

public class Criterios {
    private String texto;

    public Criterios(String id, String texto) {

    }

    public Criterios(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
