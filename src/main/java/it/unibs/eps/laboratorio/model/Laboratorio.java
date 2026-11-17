package it.unibs.eps.laboratorio.model;

import it.unibs.eps.laboratorio.fileManager.FilePersistenceManager;

import java.util.ArrayList;
import java.util.List;

public class Laboratorio {

    private List<Strumento> strumenti;
    private FilePersistenceManager fileManager;
    private List<Evento> storicoEventi = new ArrayList<>();

    //Lista degli ascoltatori
    private final List<LaboratorioListener> listeners = new ArrayList<>();

    public Laboratorio () {
        this.fileManager = new FilePersistenceManager();
        //All'avvio la classe Laboratorio utilizza il fileManager per caricare i dati (da file o di default)
        this.strumenti = fileManager.creaCatalogo();
        this.storicoEventi = fileManager.caricaEventi();
    }

    //Metodo per salvare lo stato corrente del laboratorio
    public void salvaStatoLaboratorio () {
        fileManager.salvaCatalogo(strumenti);
    }

    //Metodi Get resi synchronized per essere utilizzati anche dalla GUI in modo da essere aggiornati allo stato corrente
    public synchronized List<Strumento> getStrumenti () {
        return strumenti;
    }


    public void setStrumenti (List<Strumento> strumenti) {
        this.strumenti = strumenti;
    }

    public void setStoricoEventi (List<Evento> eventi) {
        this.storicoEventi = eventi;
    }

    public Strumento trovaStrumentoDisponibile (String tipoStrumento ) {

        Strumento strumentoDisp = null;

        for(Strumento s : strumenti ) {
            if ( s.getTipo().equals( tipoStrumento ) && s.getStato().equals(StatoStrumento.DISPONIBILE) ) {
                strumentoDisp = s;
            }
        }

        return strumentoDisp;

    }

    public synchronized void nuovoEvento (Evento evento ) {

        storicoEventi.add(evento);
        notificaStatoAggiornato();
        notificaNuovoEvento(evento);
    }

    //Metodi Get resi synchronized per essere utilizzati anche dalla GUI in modo da essere aggiornati allo stato corrente
    public synchronized List<Evento> getStoricoEventi () {
        return storicoEventi;
    }

    //Permette all'interfaccia grafica di registrarsi come ascoltatore
    public synchronized void nuovoListener (LaboratorioListener listener) {
        listeners.add(listener);
    }

    //Aggiornamenti interni
    public synchronized void notificaStatoAggiornato () {
        for( LaboratorioListener listener : listeners ) {
            listener.onStatoAggiornato();
        }
    }

    public synchronized void notificaNuovoEvento (Evento evento) {
        for( LaboratorioListener listener : listeners ) {
            listener.onNuovoEvento(evento);
        }
    }


}

