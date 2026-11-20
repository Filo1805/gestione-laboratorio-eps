package it.unibs.eps.laboratorio.controller;

import it.unibs.eps.laboratorio.fileManager.FilePersistenceManager;
import it.unibs.eps.laboratorio.model.*;
import it.unibs.eps.laboratorio.monitor.MonitorLaboratorio;
import it.unibs.eps.laboratorio.simulation.UtenteTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Controller: fa da intermediario tra l'interfaccia grafica e il resto del programma.
 *
 * La GUI non conosce il monitor né i thread: chiede al controller di avviare o interrompere la simulazione,
 * di salvare e caricare i dati e di leggere lo stato da mostrare.
 * Il controller avvia i thread degli utenti con un ExecutorServic e legge i dati sempre tramite il monitor (che restituisce copie).
 */

public class LaboratorioController {

    private final MonitorLaboratorio monitor;
    private final FilePersistenceManager fileManager = new FilePersistenceManager();
    private ExecutorService executor;                   //Pool dei thread della simulazione in corso
    private volatile boolean interrotta = false;        //true se la simulazione è stata fermata con Interrompi

    public LaboratorioController(MonitorLaboratorio monitor ) {
        this.monitor = monitor;
        this.executor = Executors.newCachedThreadPool();
    }

    /** Permette all'interfaccia di Swing di registrarsi come listener del laboratorio */
    public void registraListener (LaboratorioListener listener ) {
         monitor.registraListener(listener);
    }

    /** Avvia il thread di un singolo utente che richiede un tipo di strumento per la durata indicata */
    public void avviaRichiestaUtente (Utente utente, String tipoStrumento, int durataPrestito) {
        Runnable task = new UtenteTask(monitor, utente, tipoStrumento, durataPrestito);
        executor.submit(task);
    }

    /** @return copia del catalogo degli strumenti con il loro stato attuale */
    public List<Strumento> getListaStrumenti() { return monitor.getStrumenti(); }

    /** @return copia dello storico degli eventi */
    public List<Evento> getStoricoEventi() { return monitor.getStoricoEventi(); }

    /** @return la lista delle richieste */
    public List<Richiesta>  getListaRichieste() { return monitor.getRichieste(); }

    /** @return il numero di richieste in attesa */
    public int getRichiesteInAttesa() { return monitor.getRichiestaInAttesa(); }
    /**
     * Interrompe la simulazione: interrompe i thread e aspetta (al massimo 5 secondi) che terminino.
     * Va chiamato fuori dal thread grafico perchè può bloccarsi per un attimo.
     */
    public void interrompiSimulazione() {
        interrotta = true;

        if (executor != null && !executor.isTerminated() ) {
            executor.shutdownNow(); //Interrompe tutti i thread, anche quelli in wait() o sleep()
            try {
                executor.awaitTermination(5, TimeUnit.SECONDS);

            }
            catch ( InterruptedException e ) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Avvia la simulazione: crea un thread per ogni utente. I tipi di strumento del catalogo
     * vengono assegnati a turno agli utenti. Quando tutti i thread hanno finito (senza interruzione)
     * viene eseguito allaFine.
     *
     * @param numeroUtenti      numero di utenti (thread) da avvviare
     * @param durataSecondi     durata del prestito di ogni utente, in secondi
     * @param allaFine          azione da eseguire a simulazione concluda (la GUI vi scrive il messaggio di fine simulazione)
     */
    public void avviaSimulazione (int numeroUtenti, int durataSecondi, Runnable allaFine) {
        if(executor == null || executor.isShutdown() || executor.isTerminated()) {
            executor = Executors.newCachedThreadPool();
        }

        interrotta = false;

        //Una nuova simulazione parte con la tabella richieste pulita
        monitor.rimuoviRichiesteConcluse();


        //Tipi di strumento presenti nel catalogo
        List<String> tipiStrumenti = new ArrayList<>();
        for (Strumento s: monitor.getStrumenti()) {
            if(!tipiStrumenti.contains(s.getTipo())) {
                tipiStrumenti.add(s.getTipo());
            }
        }

        //Creazione di Utente + "n" e richiesta
        for(int i = 0; i < numeroUtenti; i++ ) {
            Utente utente = new Utente("Utente" + (i + 1));
            avviaRichiestaUtente(utente, tipiStrumenti.get(i % tipiStrumenti.size()), durataSecondi*1000);
        }

        //Se non arrivano altri task l'executor termina quando hanno finito
        ExecutorService esecuzione = executor;
        esecuzione.shutdown();

        //Thread osservatore: aspetta la fine della simulazione e poi avvisa chi l'ha avviata
        Thread osservatore = new Thread(() -> {
            try {
                esecuzione.awaitTermination(1, TimeUnit.DAYS);
            }
            catch(InterruptedException e ) {
                return;
            }
            if(!interrotta) {
                allaFine.run();
            }
        });

        osservatore.setDaemon(true);
        osservatore.start();
    }

    /** Salva dati su file catalogo e storico (pulsante Salva Dati) */
    public void salvaDati () {
        fileManager.salvaCatalogo(monitor.getStrumenti());
        fileManager.salvaEventi(monitor.getStoricoEventi());
    }

    /** Ricarica da file il catalogo e storico (pulsante Carica Dati)
     * @return  false se il caricamento è stato rifiutato perchè ci sono prestiti o attese in corso
     */
    public boolean caricaDati () {
        List<Strumento> strumenti = fileManager.creaCatalogo();
        List<Evento> eventi = fileManager.caricaEventi();
        return monitor.carica(strumenti, eventi);
    }

}