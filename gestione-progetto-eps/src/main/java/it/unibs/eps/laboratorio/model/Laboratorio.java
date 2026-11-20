package it.unibs.eps.laboratorio.model;

import it.unibs.eps.laboratorio.fileManager.FilePersistenceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Stato del laboratorio: catalogo degli strumenti, storico degli eventi e osservatori (listener).
 *
 * All'avvio carica catalogo e storico da file tramite FilePersistenceManager.
 *
 * La sincronizzazione è gestita nella classe MonitorLaboratorio.
 */
public class Laboratorio {

    private List<Strumento> strumenti;
    private FilePersistenceManager fileManager;
    private List<Evento> storicoEventi;

    //Osservatori da avvisare quando qualcosa cambia (la GUI)
    private final List<LaboratorioListener> listeners = new ArrayList<>();

    /** Carica catalogo e storico da file*/
    public Laboratorio () {
        this.fileManager = new FilePersistenceManager();
        this.strumenti = fileManager.creaCatalogo();
        this.storicoEventi = fileManager.caricaEventi();
    }

    /** Costruttore creato solo per i test */
    public Laboratorio(List<Strumento> strumenti) {
        this.strumenti = strumenti;
        this.fileManager = new FilePersistenceManager();
        this.storicoEventi = fileManager.caricaEventi();
    }
    /** Metodi get */
    public synchronized List<Strumento> getStrumenti () {
        return strumenti;
    }

    public synchronized List<Evento> getStoricoEventi () {
        return storicoEventi;
    }
    /**-------------------------*/


    /** Sostituisce il catalogo (usato dal caricamento da file)*/
    public void setStrumenti (List<Strumento> strumenti) {
        this.strumenti = strumenti;
    }

    /** Sostituisce lo storico (usato dal caricamento da file)*/
    public void setStoricoEventi (List<Evento> eventi) {
        this.storicoEventi = eventi;
    }

    /**
     * Cerca uno strumento DISPONIBILE del tipo richiesto.
     *
     * @param tipoStrumento
     * @return uno strumento libero del tipo richiesto, oppure null se sono tutti occupati.
     */
    public Strumento trovaStrumentoDisponibile (String tipoStrumento ) {

        Strumento strumentoDisp = null;

        for(Strumento s : strumenti ) {
            if ( s.getTipo().equals( tipoStrumento ) && s.getStato().equals(StatoStrumento.DISPONIBILE) ) {
                strumentoDisp = s;
            }
        }

        return strumentoDisp;

    }

    /** Aggiunge l'evento allo storico e avvisa i listener */
    public synchronized void nuovoEvento (Evento evento ) {

        storicoEventi.add(evento);
        notificaStatoAggiornato();
        notificaNuovoEvento(evento);
    }

    /** Permette all'interfaccia grafica di registrarsi come ascoltatore */
    public synchronized void nuovoListener (LaboratorioListener listener) {
        listeners.add(listener);
    }

    /** Avvisa tutti i listener che lo stato è cambiato */
    public synchronized void notificaStatoAggiornato () {
        for( LaboratorioListener listener : listeners ) {
            listener.onStatoAggiornato();
        }
    }

    /** Avvisa tutti i listener che c'è un nuovo evento nello storico.*/
    public synchronized void notificaNuovoEvento (Evento evento) {
        for( LaboratorioListener listener : listeners ) {
            listener.onNuovoEvento(evento);
        }
    }


}

