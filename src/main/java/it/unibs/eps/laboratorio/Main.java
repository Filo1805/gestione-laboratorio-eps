package it.unibs.eps.laboratorio;

import it.unibs.eps.laboratorio.controller.LaboratorioController;
import it.unibs.eps.laboratorio.graphics.LaboratorioGUI;
import it.unibs.eps.laboratorio.model.Laboratorio;
import it.unibs.eps.laboratorio.model.Utente;
import it.unibs.eps.laboratorio.monitor.MonitorLaboratorio;
import it.unibs.eps.laboratorio.simulation.UtenteTask;

import javax.swing.*;

public class Main {
    public static void main(String[] args){

        MonitorLaboratorio monitor = new MonitorLaboratorio();
        LaboratorioController controller = new LaboratorioController(monitor);

        SwingUtilities.invokeLater(() -> {
            LaboratorioGUI graphic = new LaboratorioGUI(controller);
            graphic.setVisible(true);
        });

        /**
        System.out.println("-Avvio del progetto Laboratorio EPS-");

        //1. Inizializzazio il laboratorio
        Laboratorio laboratorio = new Laboratorio();

        //2. Creazione del monitor condiviso dai thread
        MonitorLaboratorio monitor = new MonitorLaboratorio(laboratorio);

        //3. Creazione di 4 utenti di test
        Utente u1 = new Utente("Ciri");
        Utente u2 = new Utente("Betti");
        Utente u3 = new Utente( "Lofi");
        Utente u4 = new Utente("Mathias");

        //Creazione della pool di 4 thread
        ExecutorService executor = Executors.newFixedThreadPool(4);

        // Invio dei Task al pool
        executor.submit(new UtenteTask(monitor, laboratorio, u1, "Oscilloscopio"));
        executor.submit(new UtenteTask(monitor, laboratorio, u2, "Generatore"));
        executor.submit(new UtenteTask(monitor, laboratorio, u3, "Multimetro"));
        executor.submit(new UtenteTask(monitor, laboratorio, u4, "Multimetro"));

        //Chiusura ordinata del pool di thread
        executor.shutdown();

        try {

            if(!executor.awaitTermination(20, TimeUnit.SECONDS)) {

                System.err.println("[ATTENZIONE] La simulazione ha impiegato troppo tempo chiusura forzata dei thread");
                executor.shutdownNow();
            }

        }
        catch ( InterruptedException e ) {
            System.err.println("[ERRORE] Il thread principale è stato interrotto.");
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("=== SIMULAZIONE COMPLETATA ===");
        System.out.println("Controlla i file 'catalogo_strumenti.txt' e 'registro_eventi.txt' per verificare la persistenza.");
    */

    }
}
