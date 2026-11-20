package it.unibs.eps.laboratorio.monitor;

import it.unibs.eps.laboratorio.model.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Monitor che gestisce la disponibilità degli strumenti: nucleo della sincronizzazione.
 *
 * STATO CONDIVISO: Il monitor crea e possiede il Laboratorio e l'elenco degli utenti in attesa.
 * Nessun altro raggiunge uno di questi dati: la GUI e il salvataggio su file ricevono delle copie.
 * Tutto lo stato è quindi protetto da un univo lock, quello del Monitor.
 *
 * COME FUNZIONA LA SINCRONIZZAZIONE:
 *      1. RichiediStrumento: la ricerca di uno strumento libero e la sua assegnazione avvengono nello stesso
 *         metodo synchronized, quindi un solo utente alla volta può prendere uno strumento. Se non ce ne sono,
 *         il thread chiama wait() dentro un ciclo while: al risveglio la condizione viene ricontrollata.
 *
 *      2. RestituisciStrumento: rende lo strumento disponibile e chiama notifyAll(), così le richieste in attesa
 *         possono riprovare.
 *
 *      3. Se un thread in attesa viene interrotto, wait() lancia InterruptedException: a quel punto
 *         non gli era stato assegnato nulla, quindi basta toglierlo dalle richieste in attesa.
 *
 *
 */
public class MonitorLaboratorio {

    //In questo modo il Laboratorio può essere raggiunto solo passando dal Monitor
    private final Laboratorio laboratorio;

    //Richieste della simulazione corrente, in ordine di arrivo
    //Gli utenti sono identificate dal nome, che deve essere univoco
    private final List<Richiesta> richieste = new ArrayList<>();

    /** Crea il monitor e il laboratorio (carica catalogo e storico da file) **/
    public MonitorLaboratorio() {
        this.laboratorio = new Laboratorio();
    }


    /**
     * Richiede uno strumento del tipo indicato. Se tutti gli strumenti di quel tipo sono occupati
     * il thread rimane in attesa finché uno viene restituito.
     *
     * @param tipoStrumento
     * @param utente
     * @return lo strumento assegnato, ora IN_PRESTITO
     * @throws InterruptedException
     */
    public synchronized Strumento richiediStrumento(String tipoStrumento, Utente utente ) throws InterruptedException {


        Richiesta richiesta = new Richiesta(utente, tipoStrumento); //Richiesta nasce IN_ATTESA
        richieste.add(richiesta);

        Strumento strumentoDisponibile;
        boolean inAttesa = false; //Serve per registrare l'attesa una alla volta.

        try {
            //Il while ricontrolla la condizione a ogni risveglio.
            while ((strumentoDisponibile = laboratorio.trovaStrumentoDisponibile(tipoStrumento)) == null) {

                if(!inAttesa) {

                    inAttesa = true;

                    laboratorio.nuovoEvento(new Evento("L'utente " + utente.getName() + " è in attesa dello strumento: " + tipoStrumento));
                    System.out.println("[MONITOR] L'utente " + utente.getName() + " è in attesa dello strumento: " + tipoStrumento);

                }
                wait();     //Rilascia il lock del monitor finché qualcuno non fa notifyAll();
            }
        }
        catch ( InterruptedException e ) {
            //Interrotto durante l'attesa: nessuno strumento era stato assegnato, si annulla solo la richiesta
            richiesta.setStatoRichiesta(StatoRichiesta.ANNULLATA);
            laboratorio.nuovoEvento(new Evento("L'utente " + utente.getName() + " ha annullato la richiesta di: " + tipoStrumento ));
            throw e;
        }

        //Assegnazione dello strumento
        strumentoDisponibile.setStato(StatoStrumento.IN_PRESTITO);
        richiesta.setStatoRichiesta(StatoRichiesta.IN_PRESTITO);
        laboratorio.nuovoEvento(new Evento("L'utente " + utente.getName() + " ha preso in prestito lo strumento: " + tipoStrumento));
        System.out.println("[MONITOR] SUCCESS: " + utente.getName() + " ha preso in prestito lo strumento: " + tipoStrumento);

        return strumentoDisponibile;


    }

    /**
     * Restituisce lo strumento al laboratorio e risveglia tutti i thread in attesa
     * (ognuno ricontrolla se ora c'è uno strumento del tipo che gli serve).
     *
     * @param strumento
     * @param utente
     */
    public synchronized void restituisciStrumento (Strumento strumento, Utente utente) {

        strumento.setStato(StatoStrumento.DISPONIBILE);

        //la richiesta in prestito di questo utente diventa restituita
        for(Richiesta r : richieste) {
            if ( r.getUtente().getName().equals(utente.getName()) && r.getStatoRichiesta() == StatoRichiesta.IN_PRESTITO ) {
                r.setStatoRichiesta(StatoRichiesta.RESTITUITA);
            }
        }

        laboratorio.nuovoEvento(new Evento("L'utente " + utente.getName() + " ha restituito lo strumento: " + strumento.getTipo()) );
        System.out.println("[MONITOR] INFO: " + utente.getName() + " ha restituito lo strumento: " + strumento.getTipo() );

        notifyAll();

    }

    /** @return una copia del catalogo: */
    public synchronized List<Strumento> getStrumenti () {
        List<Strumento> copia = new ArrayList<>();
        for (Strumento s : laboratorio.getStrumenti()) {
            copia.add(new Strumento(s.getId(), s.getTipo(), s.getStato()));
        }
        return copia;
    }

    /** @return una copia di tutte le richieste con il loro stato attuale, in ordine di arrivo */
    public synchronized List<Richiesta> getRichieste() {
        List<Richiesta> copia = new ArrayList<>();
        for (Richiesta r : richieste) {
            copia.add(new Richiesta(r.getUtente(), r.getTipoStrumentoRichiesto(), r.getStatoRichiesta()));
        }

        return copia;
    }

    /** @return una copia dello storico degli eventi */
    public synchronized List<Evento> getStoricoEventi () {
        return new ArrayList<>(laboratorio.getStoricoEventi());
    }

    /** Registra un listener (la GUI) che verrà avvisato dei cambiamenti. */
    public synchronized void registraListener (LaboratorioListener listener) {
        laboratorio.nuovoListener(listener);
    }

    /** @return il numero di richieste attualmente in attesa */
    public synchronized int getRichiestaInAttesa () {
        int inAttesa = 0;
        for (Richiesta r : richieste) {
            if ( r.getStatoRichiesta().equals(StatoRichiesta.IN_ATTESA) ) {
                inAttesa++;
            }
        }

        return inAttesa;
    }

    /** Pulisce la lista dalle richieste concluse */
    public void rimuoviRichiesteConcluse () { richieste.clear();}

    /**
     * Sostituisce catalogo e storico con quelli ricaricati da file.
     * Non è possibile se qualcuno sta usando o aspettando uno strumento: lo stato andrebbe in conflitto.
     * @param strumenti
     * @param eventi
     * @return true se il caricamento è stato eseguito, false se è stato rifiutato.
     */
    public synchronized boolean carica ( List<Strumento> strumenti, List<Evento> eventi ) {

        for (Richiesta r : richieste) {
            if ( r.getStatoRichiesta().equals(StatoRichiesta.IN_ATTESA) ) {
                return false;
            }
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