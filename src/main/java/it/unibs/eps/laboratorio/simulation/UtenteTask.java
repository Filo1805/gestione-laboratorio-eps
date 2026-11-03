package it.unibs.eps.laboratorio.simulation;

import it.unibs.eps.laboratorio.fileManager.FilePersistenceManager;
import it.unibs.eps.laboratorio.model.Laboratorio;
import it.unibs.eps.laboratorio.model.Strumento;
import it.unibs.eps.laboratorio.model.Utente;
import it.unibs.eps.laboratorio.monitor.MonitorLaboratorio;

public class UtenteTask implements Runnable {

    private final MonitorLaboratorio monitor;
    private final Laboratorio laboratorio;
    private final Utente utente;
    private final String tipoStrumentoRichiesto;
    private final FilePersistenceManager fileManager;

    public UtenteTask(MonitorLaboratorio monitor, Laboratorio laboratorio, Utente utente, String tipoStrumentoRichiesto ) {
        this.monitor = monitor;
        this.laboratorio = laboratorio;
        this.utente = utente;
        this.tipoStrumentoRichiesto = tipoStrumentoRichiesto;
        this.fileManager = new FilePersistenceManager();
    }

    @Override
    public void run() {

        try {
            System.out.println("[THREAD] L'utente " + utente.getName() + " tenta di richiedere: " + tipoStrumentoRichiesto );

            //1. Richiesta dello strumento tramite Monitor
            Strumento strumentoOttenuto = monitor.richiediStrumento(tipoStrumentoRichiesto, utente);

            // Salvataggio aggiornato dello stato e degli eventi su file
            fileManager.salvaCatalogo(laboratorio.getStrumenti());
            fileManager.salvaEventi(laboratorio.getStoricoEventi());

            //2. Simulazione dell'utilizzo dello strumento
            int tempoUtilizzo = 3000;
            System.out.println("[UTILIZZO] " + utente.getName() + " sta usando lo strumento " + strumentoOttenuto.getId() + " per: " + (tempoUtilizzo/1000) + " secondi... ");
            Thread.sleep(tempoUtilizzo);

            //3. Restituzione dello strumento
            monitor.restituisciStrumento(strumentoOttenuto, utente);

            //4. Nuovo salvataggio su file dopo la restituzione
            fileManager.salvaCatalogo(laboratorio.getStrumenti());
            fileManager.salvaEventi(laboratorio.getStoricoEventi());

        }
        catch (InterruptedException e ) {
            System.out.println("[ERRORE] Il thread dell'utente " + utente.getName() + " é stato interrotto.");
            Thread.currentThread().interrupt();
        }

    }
}
