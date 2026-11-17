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

public class LaboratorioController {

    private final MonitorLaboratorio monitor;
    private final FilePersistenceManager fileManager = new FilePersistenceManager();
    private ExecutorService executor;
    private volatile boolean interrotta = false;

    public LaboratorioController(MonitorLaboratorio monitor ) {
        this.monitor = monitor;
        this.executor = Executors.newCachedThreadPool();
    }

    /**
     * Permette all'interfaccia di Swing di registrarsi come listener
     */
    public void registraListener (LaboratorioListener listener ) {
         monitor.registraListener(listener);
    }

    public void avviaRichiestaUtente (Utente utente, String tipoStrumento, int durataPrestito) {
        Runnable task = new UtenteTask(monitor, utente, tipoStrumento, durataPrestito);
        executor.submit(task);
    }

    public List<Strumento> getListaStrumenti() {
        return monitor.getStrumenti();
    }

    public List<Evento> getStoricoEventi() {
        return monitor.getStoricoEventi();
    }

    public void interrompiSimulazione() {
        interrotta = true;

        if (executor != null && !executor.isTerminated() ) {
            executor.shutdownNow();
            try {
                executor.awaitTermination(5, TimeUnit.SECONDS);

            }
            catch ( InterruptedException e ) {
                Thread.currentThread().interrupt();
            }
        }
    }


    public void avviaSimulazione (int numeroUtenti, int durataSecondi, Runnable allaFine) {
        if(executor == null || executor.isShutdown() || executor.isTerminated()) {
            executor = Executors.newCachedThreadPool();
        }

        interrotta = false;

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

    //Metodo che riprende il numero delle richieste in attesa
    public int getRichiesteInAttesa () {
        return monitor.getRichiestaInAttesa();
    }

    //Metodi per i pulsanti di salva e carica dati
    public void salvaDati () {
        fileManager.salvaCatalogo(monitor.getStrumenti());
        fileManager.salvaEventi(monitor.getStoricoEventi());
    }

    public boolean caricaDati () {
        List<Strumento> strumenti = fileManager.creaCatalogo();
        List<Evento> eventi = fileManager.caricaEventi();
        return monitor.carica(strumenti, eventi);
    }

}