package it.unibs.eps.laboratorio.model;

/**
 * La richiesta di uno strumento fatta da un utente, con il suo stato attuale.
 * Ogni richiesta è una riga della tabella richieste.
 */
public class Richiesta {

    private final Utente utente;
    private final String tipoStrumentoRichiesto;
    private StatoRichiesta statoRichiesta;

    /** Crea una richiesta appena fatta, quindi IN_ATTESA */
    public Richiesta(Utente utente, String tipoStrumentoRichiesto ) {
        this.utente = utente;
        this.tipoStrumentoRichiesto = tipoStrumentoRichiesto;
        this.statoRichiesta = StatoRichiesta.IN_ATTESA;
    }

    /** Crea una richiesta con uno stato specifico per le copie consegnate alla GUI */
    public Richiesta ( Utente utente, String tipoStrumentoRichiesto, StatoRichiesta statoRichiesta ) {
        this.utente = utente;
        this.tipoStrumentoRichiesto = tipoStrumentoRichiesto;
        this.statoRichiesta = statoRichiesta;
    }

    public Utente getUtente() { return utente; }

    public String getTipoStrumentoRichiesto() { return tipoStrumentoRichiesto; }

    public StatoRichiesta getStatoRichiesta() { return statoRichiesta; }

    /** Cambia lo stato della richiesta */
    public void setStatoRichiesta (StatoRichiesta statoRichiesta) { this.statoRichiesta = statoRichiesta; }

}
