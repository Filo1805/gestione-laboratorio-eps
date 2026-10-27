package it.unibs.eps.laboratorio.model;

import it.unibs.eps.laboratorio.fileManager.FilePersistenceManager;

import java.util.ArrayList;
import java.util.List;

public class Laboratorio {

    private List<Strumento> strumenti;
    private FilePersistenceManager fileManager;
    private List<Evento> storicoEventi = new ArrayList<>();

    public Laboratorio () {
        this.fileManager = new FilePersistenceManager();
        //All'avvio la classe Laboratorio utilizza il fileManager per caricare i dati (da file o di default)
        this.strumenti = fileManager.creaCatalogo();
    }

    //Metodo per salvare lo stato corrente del laboratorio
    public void salvaStatoLaboratorio () {
        fileManager.salvaCatalogo(strumenti);
    }

    public List<Strumento> getStrumenti () {
        return strumenti;
    }

    public Strumento trovaStrumentoDisponibile (String tipoStrumento ) {

        Strumento strumentoDisp = null;

        for(Strumento s : strumenti ) {
            if ( s.getTipo().equals( tipoStrumento ) && s.getStato().equals(StatoStrumento.DISPONIBILE) ) {
                strumentoDisp = s;
            }
        }

        return strumentoDisp;

    }

    public synchronized void nuovoEvento (Evento evento ) {
        storicoEventi.add(evento);
    }

    public List<Evento> getStoricoEventi () {
        return storicoEventi;
    }
}
