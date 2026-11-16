package it.unibs.eps.laboratorio.model;

import java.util.List;

public interface LaboratorioListener {

    void onStatoAggiornato();       //Dice alla GUI di ricaricare il catalogo strumenti
    void onNuovoEvento(Evento evento); //Dice alla GUI di ricaricare la lista eventi

}
