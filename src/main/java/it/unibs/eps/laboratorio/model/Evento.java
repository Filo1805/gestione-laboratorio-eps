package it.unibs.eps.laboratorio.model;

/*
 * La classe serve a rappresentare un unico evento registrato dallo storico del laboratorio
 * ( es. Richieste, attese, prelievi e restituzioni )
 */

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Evento {

    private String time; //segna l'orario in cui è avvenuto l'evento
    private String descrizione;

    public Evento (String descrizione ) {
        //Formatta data e ora in modo leggibile
        this.time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        this.descrizione = descrizione;

    }

    public String getTime () { return time; }

    public String getDescrizione () { return descrizione; }

    @Override
    public String toString() {
        return "[" + time + "]" + descrizione;
    }
}
