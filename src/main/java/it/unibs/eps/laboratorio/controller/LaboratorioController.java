package it.unibs.eps.laboratorio.controller;

import it.unibs.eps.laboratorio.model.*;
import it.unibs.eps.laboratorio.monitor.MonitorLaboratorio;
import it.unibs.eps.laboratorio.simulation.UtenteTask;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LaboratorioController {

    private final Laboratorio laboratorio;
    private final MonitorLaboratorio monitor;
    private ExecutorService executor;

    public LaboratorioController(Laboratorio laboratorio, MonitorLaboratorio monitor ) {
        this.laboratorio = laboratorio;
        this.monitor = monitor;
        this.executor = Executors.newCachedThreadPool();
    }

    /**
     * Permette all'interfaccia di Swing di registrarsi come listener
     */
    public void registraListener (LaboratorioListener listener ) {
        laboratorio.nuovoListener(listener);
    }

    public void avviaRichiestaUtente (Utente utente, String tipoStrumento) {
        Runnable task = new UtenteTask(monitor, laboratorio, utente, tipoStrumento);
        executor.submit(task);
    }

    public List<Strumento> getListaStrumenti() {
        return laboratorio.getStrumenti();
    }

    public List<Evento> getStoricoEventi() {
        return laboratorio.getStoricoEventi();
    }

    public void interrompiSimulazione() {
        if (executor != null && !executor.isShutdown() ) {
            executor.shutdownNow();
        }
    }

    public void avviaSimulazioneDefault() {
        if(executor == null || executor.isShutdown() || executor.isTerminated()) {
            executor = Executors.newCachedThreadPool();
        }

        Utente u1 = new Utente("Ciri");
        Utente u2 = new Utente("Bette");
        Utente u3 = new Utente("Lofi");
        Utente u4 = new Utente("Matthias");
        Utente u5 = new Utente("Brescio");

        avviaRichiestaUtente(u1, "Oscilloscopio");
        avviaRichiestaUtente(u2, "Generatore");
        avviaRichiestaUtente(u3, "Multimetro");
        avviaRichiestaUtente(u4, "Multimetro");
        avviaRichiestaUtente(u5, "Oscilloscopio");
    }
}
