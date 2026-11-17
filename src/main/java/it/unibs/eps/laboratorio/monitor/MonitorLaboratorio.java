package it.unibs.eps.laboratorio.monitor;

import it.unibs.eps.laboratorio.model.*;

import java.util.ArrayList;
import java.util.List;

/*
 * Classe Monitor che gestisce le prenotazioni degli strumenti tramite thread
 */
public class MonitorLaboratorio {

    //In questo modo il Laboratorio può essere raggiunto solo passando dal Monitor
    private final Laboratorio laboratorio;
    private int richiestaInAttesa = 0;

    public MonitorLaboratorio() {
        this.laboratorio = new Laboratorio();
    }

    /* Metodo per la richiesta di un tipo di strumento, se lo strumento non è disponibile
     * il thread si mette in attesa
     */
    public synchronized Strumento richiediStrumento(String tipoStrumento, Utente utente ) throws InterruptedException {


        Strumento strumentoDisponibile;
        boolean inAttesa = false;

        try {
            while ((strumentoDisponibile = laboratorio.trovaStrumentoDisponibile(tipoStrumento)) == null) {

                if(!inAttesa) {

                    inAttesa = true;
                    richiestaInAttesa++;
                    laboratorio.nuovoEvento(new Evento("L'utente " + utente.getName() + " è in attesa dello strumento: " + tipoStrumento));
                    //System.out.println("[MONITOR] L'utente " + utente.getName() + " è in attesa dello strumento: " + tipoStrumento);

                }
                wait();
            }
        }
        catch ( InterruptedException e ) {
            richiestaInAttesa--;
            laboratorio.nuovoEvento(new Evento("L'utente " + utente.getName() + " ha annullato la richiesta di: " + tipoStrumento ));
            throw e;
        }

        if(inAttesa) {
            richiestaInAttesa--; //Arrivato a questo punto l'utente non è più in attesa
        }

        //Assegnazione dello strumento
        strumentoDisponibile.setStato(StatoStrumento.IN_PRESTITO);
        laboratorio.nuovoEvento(new Evento("L'utente " + utente.getName() + " ha preso in prestito lo strumento: " + tipoStrumento));
        //System.out.println("[MONITOR] SUCCESS: " + utente.getName() + " ha preso in prestito lo strumento: " + tipoStrumento);

        return strumentoDisponibile;


    }

    /*
     * Metodo sincronizzato per aggiornare i thread quando uno strumento ritorna dal prestito
     */
    public synchronized void restituisciStrumento (Strumento strumento, Utente utente) {
        strumento.setStato(StatoStrumento.DISPONIBILE);
        laboratorio.nuovoEvento(new Evento("L'utente " + utente.getName() + " ha restituito lo strumento: " + strumento.getTipo()) );
        //System.out.println("[MONITOR] INFO: " + utente.getName() + " ha restituito lo strumento: " + strumento.getTipo() );

        notifyAll();

    }

    /* Copia degli strumenti */
    public synchronized List<Strumento> getStrumenti () {
        List<Strumento> copia = new ArrayList<>();
        for (Strumento s : laboratorio.getStrumenti()) {
            copia.add(new Strumento(s.getId(), s.getTipo(), s.getStato()));
        }
        return copia;
    }

    /* Copia dello storico eventi */
    public synchronized List<Evento> getStoricoEventi () {
        return new ArrayList<>(laboratorio.getStoricoEventi());
    }

    public synchronized void registraListener (LaboratorioListener listener) {
        laboratorio.nuovoListener(listener);
    }

    //Restituisce il numero corrente di richieste in attesa
    public synchronized int getRichiestaInAttesa() {
        return richiestaInAttesa;
    }


    /*
     * Sostituisce catologo e storico con quelli ricaricati da file
     * none è possibile se qualcuno sta usando o aspettando uno strumento
     * */
    public synchronized boolean carica ( List<Strumento> strumenti, List<Evento> eventi ) {

        if ( richiestaInAttesa > 0 ){
            return false;
        }
        for ( Strumento s: laboratorio.getStrumenti() ) {
            if ( s.getStato().equals(StatoStrumento.IN_PRESTITO) ) {
                return false;
            }
        }
        laboratorio.setStrumenti(strumenti);
        laboratorio.setStoricoEventi(eventi);
        laboratorio.notificaStatoAggiornato();
        return true;
    }
}