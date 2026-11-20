package it.unibs.eps.laboratorio;

import it.unibs.eps.laboratorio.controller.LaboratorioController;
import it.unibs.eps.laboratorio.graphics.LaboratorioGUI;
import it.unibs.eps.laboratorio.monitor.MonitorLaboratorio;

import javax.swing.*;

/**
 *  Punto di ingresso dell'applicazione
 *
 *  Costruisce i tre oggetti principali e li collega tra loro:
 *
 *  MonitorLaboratorio (stato condiviso + sincronizzazione)
 *         ^
 *  Laboratorio Controller (collega interfaccia grafica e simulazione al monitor
 *         ^
 *  LaboratorioGUI (interfaccia Swing, creata nel thread grafico)
 */

public class Main {

    /** Versione corrente del programma, mostrata anche nel titolo della finestra di interfaccia grafica. **/
    public static final String version = "v2.4.0";

    public static void main(String[] args){

        //Monitor crea e protegge lo stato del laboratorio (catalogo_strumenti e storico_eventi caricati da file)
        MonitorLaboratorio monitor = new MonitorLaboratorio();
        LaboratorioController controller = new LaboratorioController(monitor);

        //Finestra Swing creata e usata solo nel thread grafico
        SwingUtilities.invokeLater(() -> {
            LaboratorioGUI graphic = new LaboratorioGUI(controller);
            graphic.setVisible(true);
        });

        /**
         * TUTTO QUELLO CHE È COMMENTATO QUA SOTTO FA PARTE DI UNA BETA DI TEST DEL PROGRAMMA
         * PER TESTARE IL PROGRAMMA CON UNA SIMULAZIONE STATICA 5 UTENTI CON RESPONSO IN TERMINALE
         * DELL'IDE

        System.out.println("-Avvio del progetto Laboratorio EPS-");

        //1. Inizializzazio il laboratorio
        Laboratorio laboratorio = new Laboratorio();

        //2. Creazione del monitor condiviso dai thread
        MonitorLaboratorio monitor = new MonitorLaboratorio(laboratorio);

        //3. Creazione di 4 utenti di test
        Utente u1 = new Utente("Ciri");
        Utente u2 = new Utente("Betti");
        Utente u3 = new Utente("Lofi");
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
        catch (InterruptedException e) {
            System.err.println("[ERRORE] Il thread principale è stato interrotto.");
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("=== SIMULAZIONE COMPLETATA ===");
        System.out.println("Controlla i file 'catalogo_strumenti.txt' e 'registro_eventi.txt' per verificare la persistenza.");
    */

    }
}
