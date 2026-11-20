package it.unibs.eps.laboratorio.model;

/**
 * Interfaccia del pattern Observer: chi la implementa (la GUI) viene avvisato quando
 * lo stato del laboratorio cambia, senza che il laboratorio conosca la grafica.
 */

public interface LaboratorioListener {

    void onStatoAggiornato();          //Dice alla GUI di ricaricare il catalogo strumenti
    void onNuovoEvento(Evento evento); //Dice alla GUI di ricaricare la lista eventi

}
