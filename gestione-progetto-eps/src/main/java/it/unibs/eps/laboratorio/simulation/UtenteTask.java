package it.unibs.eps.laboratorio.simulation;

import it.unibs.eps.laboratorio.fileManager.FilePersistenceManager;
import it.unibs.eps.laboratorio.model.Strumento;
import it.unibs.eps.laboratorio.model.Utente;
import it.unibs.eps.laboratorio.monitor.MonitorLaboratorio;

/**
 * Task eseguita da un thread della simulazione: rappresenta un singolo utente
 *
 * Ciclo di vita: richiede uno strumento al monitor, se non disponibile resta in attesa, lo usa per un
 * tempo simulato da sleep() e lo restituisce.
 *
 * La restituzione è nel blocco finally: lo strumento viene sempre restituito, anche se il thread viene interrotto
 * durante l'uso, così lo stato del laboratorio resta coerente.
 * Il salvataggio su file avviene qui, fuori dal monitor.
 *
 */

public class UtenteTask implements Runnable {

    private final MonitorLaboratorio monitor;
    private final Utente utente;
    private final String tipoStrumentoRichiesto;
    private final int durataPrestito;

    //Un solo manager condiviso da tutti i task: il synchronized dei suoi metodi evita scritture sovrapposte
    private static final FilePersistenceManager fileManager = new FilePersistenceManager();;

    /**
     *
     * @param monitor                   monitor condiviso da tutti gli utenti
     * @param utente                    utente rappresentato da questo thread
     * @param tipoStrumentoRichiesto    tipo di strumento da richiedere
     * @param durataPrestito            durata simulata del prestito, in millisecondi
     */
    public UtenteTask(MonitorLaboratorio monitor, Utente utente, String tipoStrumentoRichiesto, int durataPrestito ) {
        this.monitor = monitor;
        this.utente = utente;
        this.durataPrestito = durataPrestito;
        this.tipoStrumentoRichiesto = tipoStrumentoRichiesto;

    }

    @Override
    public void run() {

        Strumento strumentoOttenuto = null;

        try {

            System.out.println("[THREAD] L'utente " + utente.getName() + " tenta di richiedere: " + tipoStrumentoRichiesto );

            //1. Richiesta dello strumento tramite Monitor (può bloccarsi in attesa)
            strumentoOttenuto = monitor.richiediStrumento(tipoStrumentoRichiesto, utente);

            // Salvataggio aggiornato dello stato e degli eventi su file
            fileManager.salvaCatalogo(monitor.getStrumenti());
            fileManager.salvaEventi(monitor.getStoricoEventi());

            //2. Simulazione dell'utilizzo dello strumento
            System.out.println("[UTILIZZO] " + utente.getName() + " sta usando lo strumento " + strumentoOttenuto.getId() + " per: " + (durataPrestito/1000) + " secondi... ");
            Thread.sleep(durataPrestito);
        }
        catch (InterruptedException e ) {
            System.out.println("[INTERROTTO] Il thread dell'utente " + utente.getName() + " é stato interrotto.");
            Thread.currentThread().interrupt();
        }
        finally {

            if(strumentoOttenuto != null ) {

                //3. Restituzione dello strumento
                monitor.restituisciStrumento(strumentoOttenuto, utente);

                //4. Nuovo salvataggio su file dopo la restituzione
                fileManager.salvaCatalogo(monitor.getStrumenti());
                fileManager.salvaEventi(monitor.getStoricoEventi());
            }

        }

    }
}

