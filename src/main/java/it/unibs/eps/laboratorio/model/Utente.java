package it.unibs.eps.laboratorio.model;

public class Utente {

    //L'utente è identificato unicamente dal nome
    private String name;

    public Utente (String name ) {
        this.name = name;
    }

    public String getName () {
        return this.name;
    }

}
