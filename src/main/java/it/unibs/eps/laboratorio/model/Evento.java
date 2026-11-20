package it.unibs.eps.laboratorio.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Un singolo evento dello storico del laboratorio
 * (richiesta in attesa, assegnazione, restituzione, annullamento).
 * È composto da un orario già formattato e da una descrizione testuale.
 */

public class Evento {

    private final String time; //segna l'orario in cui è avvenuto l'evento
    private final String descrizione;

    /** Crea un evento che porta l'orario attuale. */
    public Evento (String descrizione ) {
        //Formatta data e ora in modo leggibile
        this.time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        this.descrizione = descrizione;

    }

    /** Crea un evento con un orario già noto, usato quando lo storico viene ricaricato da file.*/
    public Evento (String time, String descrizione ) {
        this.time = time;
        this.descrizione = descrizione;
    }

    public String getTime () { return time; }

    public String getDescrizione () { return descrizione; }

    @Override
    public String toString() {
        return "[" + time + "]" + descrizione;
    }
}