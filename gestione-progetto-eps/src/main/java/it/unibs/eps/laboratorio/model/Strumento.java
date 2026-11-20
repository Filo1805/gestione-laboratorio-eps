package it.unibs.eps.laboratorio.model;

/**
 * Uno strumento del catalogo laboratorio, con identificativo univoco, tipo e stato.
 *
 */
public class Strumento {

    private String id;      //Identificativo univoco
    private String tipo;    //tipo dello strumento: gli utenti richiedono il tipo
    private StatoStrumento stato;

    /** Crea uno strumento inizialmente DISPONIBILE */
    public Strumento (String id, String tipo ){
        this.id = id;
        this.tipo = tipo;
        this.stato = StatoStrumento.DISPONIBILE;
    }

    /** Crea uno strumento con uno stato specifico (usato per le copie e caricamento su file) */
    public Strumento (String id, String tipo, StatoStrumento stato ) {
        this.id = id;
        this.tipo = tipo;
        this.stato = stato;
    }


    public String getId (){
        return this.id;
    }

    public String getTipo (){
        return this.tipo;
    }

    public StatoStrumento getStato (){
        return this.stato;
    }

    /** Cambia lo stato dello strumento */
    public void setStato (StatoStrumento nuovoStato ) {
        this.stato = nuovoStato;
    }

    @Override
    public String toString () {
        return this.tipo + " (" + this.id + ") " + this.stato;
    }
}