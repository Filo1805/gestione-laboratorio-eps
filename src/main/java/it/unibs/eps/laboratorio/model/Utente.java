package it.unibs.eps.laboratorio.model;

/**
 * Utente del laboratorio che richiede strumenti.
 * Nella simulazione ogni utente è rappresentato da un thread distinto (vedi UtenteTask)
 * L'utente è identificato unicamente dal nome.
 */
public class Utente {

    private final String name;

    /** @param name */
    public Utente (String name ) {
        this.name = name;
    }

    public String getName () {
        return this.name;
    }

}
