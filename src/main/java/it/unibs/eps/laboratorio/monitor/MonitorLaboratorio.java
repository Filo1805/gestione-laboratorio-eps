package it.unibs.eps.laboratorio.monitor;

import it.unibs.eps.laboratorio.model.*;

/*
 * Classe Monitor che gestisce le prenotazioni degli strumenti tramite thread
 */
public class MonitorLaboratorio {

    private Laboratorio laboratorio;

    public MonitorLaboratorio(Laboratorio laboratorio) {

        this.laboratorio = laboratorio;
    }

    /* Metodo per la richiesta di un tipo di strumento, se lo strumento non è disponibile
     * il thread si mette in attesa
     */
    public synchronized Strumento richiediStrumento(String tipoStrumento, Utente utente ) throws InterruptedException {

        Strumento strumentoDisponibile = null;

        while( (strumentoDisponibile = laboratorio.trovaStrumentoDisponibile(tipoStrumento)) == null ) {

            laboratorio.nuovoEvento(new Evento( "L'utente" + utente.getName() + " è in attesa dello strumento: " + tipoStrumento ));
            System.out.println("[MONITOR] L'utente " + utente.getName() + " è in attesa dello strumento: " + tipoStrumento );
            wait();

        }

        //Assegnazione dello strumento
        strumentoDisponibile.setStato(StatoStrumento.IN_PRESTITO);
        laboratorio.nuovoEvento(new Evento("L'utente" + utente.getName() + " ha preso in prestito lo strumento: " + tipoStrumento ));
        System.out.println("[MONITOR] SUCCESS: " + utente.getName() + " ha preso in prestito lo strumento: " + tipoStrumento );

        return strumentoDisponibile;

    }

    /*
     * Metodo sincronizzato per aggiornare i thread quando uno strumento ritorna dal prestito
     */
    public synchronized void restituisciStrumento (Strumento strumento, Utente utente) {
        strumento.setStato(StatoStrumento.DISPONIBILE);
        laboratorio.nuovoEvento(new Evento("L'utente" + utente.getName() + " ha restituito lo strumento: " + strumento.getTipo()) );
        System.out.println("[MONITOR] INFO: " + utente.getName() + " ha restituito lo strumento: " + strumento.getTipo() );

        notifyAll();

    }
}