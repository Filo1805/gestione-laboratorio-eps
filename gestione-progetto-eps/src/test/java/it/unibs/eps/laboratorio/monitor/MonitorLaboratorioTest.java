package it.unibs.eps.laboratorio.monitor;

import it.unibs.eps.laboratorio.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test del {@link MonitorLaboratorio}, il nucleo della sincronizzazione.
 *
 * I test non usano file: il monitor viene creato su un Laboratorio con strumenti scelti dal test.
 * Il timeout di classe fa fallire i test che restano bloccati (per esempio per un deadlock o per
 * un notifyAll() mancante), invece di lasciarli in attesa per sempre.
 *
 * Nei test con più thread si aspetta che un thread sia davvero in attesa controllando lo stato del
 * monitor (getRichiestaInAttesa), non con un sleep() di durata fissa: la correttezza non dipende
 * dai tempi. Il breve sleep() in attendiFinche serve solo a non occupare la CPU mentre il test controlla.
 */
@Timeout(30)
class MonitorLaboratorioTest {

    private static final String OSC = "Oscilloscopio";
    private static final String MUL = "Multimetro";

    // ---------------------------------------------------------------- utilità

    /** Crea un monitor su un laboratorio con gli strumenti indicati (tutti DISPONIBILI). */
    private static MonitorLaboratorio creaMonitor(Strumento... strumenti) {
        return new MonitorLaboratorio(new Laboratorio(new ArrayList<>(Arrays.asList(strumenti))));
    }

    /** Aspetta (al massimo 5 secondi) che la condizione diventi vera; restituisce false se non succede. */
    private static boolean attendiFinche(BooleanSupplier condizione) throws InterruptedException {
        long scadenza = System.currentTimeMillis() + 5000;
        while (!condizione.getAsBoolean()) {
            if (System.currentTimeMillis() > scadenza) {
                return false;
            }
            Thread.sleep(2);
        }
        return true;
    }

    /** Stato dell'ultima richiesta fatta dall'utente con quel nome. */
    private static StatoRichiesta statoRichiesta(MonitorLaboratorio monitor, String nomeUtente) {
        StatoRichiesta stato = null;
        for (Richiesta r : monitor.getRichieste()) {
            if (r.getUtente().getName().equals(nomeUtente)) {
                stato = r.getStatoRichiesta();
            }
        }
        return stato;
    }

    /** Thread che fa una richiesta al monitor e ricorda come è andata. */
    private static class RichiestaInThread extends Thread {
        private final MonitorLaboratorio monitor;
        private final String tipo;
        private final Utente utente;
        volatile Strumento ottenuto;        //strumento ricevuto (null finché non lo ottiene)
        volatile boolean interrotto;        //true se è stato interrotto mentre aspettava
        volatile Throwable errore;          //eventuale errore imprevisto

        RichiestaInThread(MonitorLaboratorio monitor, String tipo, String nomeUtente) {
            this.monitor = monitor;
            this.tipo = tipo;
            this.utente = new Utente(nomeUtente);
        }

        @Override
        public void run() {
            try {
                ottenuto = monitor.richiediStrumento(tipo, utente);
            } catch (InterruptedException e) {
                interrotto = true;
            } catch (Throwable t) {
                errore = t;
            }
        }
    }

    // ---------------------------------------------------------------- richiesta e restituzione

    @Test
    void richiestaConStrumentoLibero_loAssegna() throws Exception {
        MonitorLaboratorio monitor = creaMonitor(new Strumento("OSC-01", OSC));

        Strumento s = monitor.richiediStrumento(OSC, new Utente("Anna"));

        assertEquals("OSC-01", s.getId());
        assertEquals(StatoStrumento.IN_PRESTITO, s.getStato());
        assertEquals(StatoStrumento.IN_PRESTITO, monitor.getStrumenti().get(0).getStato());
        assertEquals(0, monitor.getRichiestaInAttesa());
        assertEquals(StatoRichiesta.IN_PRESTITO, statoRichiesta(monitor, "Anna"));
    }

    @Test
    void restituzione_rendeLoStrumentoDisponibile() throws Exception {
        MonitorLaboratorio monitor = creaMonitor(new Strumento("OSC-01", OSC));
        Utente anna = new Utente("Anna");
        Strumento s = monitor.richiediStrumento(OSC, anna);

        monitor.restituisciStrumento(s, anna);

        assertEquals(StatoStrumento.DISPONIBILE, monitor.getStrumenti().get(0).getStato());
        assertEquals(StatoRichiesta.RESTITUITA, statoRichiesta(monitor, "Anna"));
    }

    @Test
    void duePiuStrumentiDelloStessoTipo_vannoAUtentiDiversi() throws Exception {
        MonitorLaboratorio monitor = creaMonitor(new Strumento("OSC-01", OSC), new Strumento("OSC-02", OSC));

        Strumento perAnna = monitor.richiediStrumento(OSC, new Utente("Anna"));
        Strumento perBruno = monitor.richiediStrumento(OSC, new Utente("Bruno"));

        assertNotSame(perAnna, perBruno);
        assertNotEquals(perAnna.getId(), perBruno.getId());
    }

    @Test
    void tipiDiversi_nonSiBloccanoTraLoro() throws Exception {
        MonitorLaboratorio monitor = creaMonitor(new Strumento("OSC-01", OSC), new Strumento("MUL-01", MUL));
        monitor.richiediStrumento(OSC, new Utente("Anna"));   //l'oscilloscopio è occupato

        //Il multimetro è libero: la richiesta non deve attendere (altrimenti scatta il timeout)
        Strumento s = monitor.richiediStrumento(MUL, new Utente("Bruno"));

        assertEquals("MUL-01", s.getId());
    }

    // ---------------------------------------------------------------- attesa e ripresa

    @Test
    void utenteAspetta_eRiprendeDopoLaRestituzione() throws Exception {
        MonitorLaboratorio monitor = creaMonitor(new Strumento("OSC-01", OSC));
        Utente anna = new Utente("Anna");
        Strumento s = monitor.richiediStrumento(OSC, anna);

        RichiestaInThread bruno = new RichiestaInThread(monitor, OSC, "Bruno");
        bruno.start();

        //Bruno deve restare in attesa finché lo strumento non viene restituito
        assertTrue(attendiFinche(() -> monitor.getRichiestaInAttesa() == 1), "Bruno doveva mettersi in attesa");
        assertTrue(bruno.isAlive());
        assertNull(bruno.ottenuto);
        assertEquals(StatoRichiesta.IN_ATTESA, statoRichiesta(monitor, "Bruno"));

        monitor.restituisciStrumento(s, anna);

        bruno.join(5000);
        assertFalse(bruno.isAlive(), "Bruno doveva riprendere dopo la restituzione");
        assertNull(bruno.errore);
        assertNotNull(bruno.ottenuto);
        assertEquals(0, monitor.getRichiestaInAttesa());
        assertEquals(StatoRichiesta.IN_PRESTITO, statoRichiesta(monitor, "Bruno"));
        assertEquals(StatoStrumento.IN_PRESTITO, monitor.getStrumenti().get(0).getStato());
    }

    // ---------------------------------------------------------------- interruzione

    @Test
    void interruzioneInAttesa_annullaLaRichiestaELoStatoRestaCoerente() throws Exception {
        MonitorLaboratorio monitor = creaMonitor(new Strumento("OSC-01", OSC));
        Utente anna = new Utente("Anna");
        Strumento s = monitor.richiediStrumento(OSC, anna);

        RichiestaInThread bruno = new RichiestaInThread(monitor, OSC, "Bruno");
        bruno.start();
        assertTrue(attendiFinche(() -> monitor.getRichiestaInAttesa() == 1));

        bruno.interrupt();
        bruno.join(5000);

        //Bruno è uscito senza ottenere nulla e senza lasciare attese pendenti
        assertFalse(bruno.isAlive());
        assertTrue(bruno.interrotto);
        assertNull(bruno.ottenuto);
        assertNull(bruno.errore);
        assertEquals(0, monitor.getRichiestaInAttesa());
        assertEquals(StatoRichiesta.ANNULLATA, statoRichiesta(monitor, "Bruno"));
        //Lo strumento di Anna non è stato toccato
        assertEquals(StatoStrumento.IN_PRESTITO, monitor.getStrumenti().get(0).getStato());

        //Il laboratorio continua a funzionare: dopo la restituzione un altro utente lo ottiene
        monitor.restituisciStrumento(s, anna);
        Strumento perCarla = monitor.richiediStrumento(OSC, new Utente("Carla"));
        assertEquals("OSC-01", perCarla.getId());
    }

}
