package it.unibs.eps.laboratorio.model;

//Classe Strumento gestisce inizializzazione e modifiche agli strumenti del laboratorio
public class Strumento {

    private String id;
    private String tipo;
    private StatoStrumento stato;

    public Strumento (String id, String tipo ){
        this.id = id;
        this.tipo = tipo;
        this.stato = StatoStrumento.DISPONIBILE;
    }

    public Strumento (String id, String tipo, StatoStrumento stato ) {
        this.id = id;
        this.tipo = tipo;
        this.stato = stato;
    }

    //Metodi Get supplementari (VEDERE SE ELIMINARE IN CASO DI NON UTILIZZO)
    public String getId (){
        return this.id;
    }

    public String getTipo (){
        return this.tipo;
    }

    public StatoStrumento getStato (){
        return this.stato;
    }

    //Metodo per cambiare lo stato dello strumento
    public void setStato (StatoStrumento nuovoStato ) {
        this.stato = nuovoStato;
    }

    @Override
    public String toString () {
        return this.tipo + " (" + this.id + ") " + this.stato;
    }
}