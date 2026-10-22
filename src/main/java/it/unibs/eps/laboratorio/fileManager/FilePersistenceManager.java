package it.unibs.eps.laboratorio.fileManager;

import it.unibs.eps.laboratorio.model.StatoStrumento;
import it.unibs.eps.laboratorio.model.Strumento;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
*Classe FilePercistenceManager responsabile delle operazioni di persistenza (salvataggio e aggiornamento)
* del catalogo degli strumenti su file di testo.
* */



public class FilePersistenceManager {

    //Nome del file in cui salvare i dati del catalogo
    private static final String FILE_NAME = "catalogo_strumenti.txt";

    /**
     * Compiti della classe
     *
     * Carica la lista degli strumenti dal file seguendo i seguenti passaggi:
     * 1) Controlla l'esistenza del file
     * 2) Controlla che il file non sia vuoto
     * 3) Legge i dati e li inserisce nell'ArrayList
     *
     * @return una lista di strumenti ( caricata dal file o predefinita dal programma)
     */

    public List<Strumento> creaCatalogo () {

        File file = new File(FILE_NAME);
        List<Strumento> strumentiCaricati = new ArrayList<>();

        //Passo 1) Controlla se il file esiste e non è vuoto
        if ( file.exists() && file.length() > 0 ) {

            //Utilizzo di try e catch per chiudere automaticamente il file e rilevare errori
            try (Scanner scanner = new Scanner(file)){

                while (scanner.hasNextLine()){

                    String riga = scanner.nextLine().trim();

                    //Salta evenutali righe vuote
                    if (riga.isEmpty()) continue;

                    //Formato atteso: ID;TIPO;STATO
                    String[] parti = riga.split(";");
                    if ( parti.length == 3 ) {
                        String id = parti[0];
                        String tipo = parti[1];
                        StatoStrumento stato = StatoStrumento.valueOf(parti[2].trim());

                        strumentiCaricati.add( new Strumento( id, tipo, stato));
                    }
                }

                //Se sono stati caricati elementi validi restituire la lista
                if(!strumentiCaricati.isEmpty()) {
                    System.out.println("[INFO] Catalogo strumenti caricato correttamente.");
                    return strumentiCaricati;
                }

            }
            catch(FileNotFoundException e ){
                System.err.println("[ERRORE] File non trovato: " + e.getMessage());
            }
            catch(IllegalArgumentException e){
                System.err.println("[ERRORE] Stato dello strumento non riconosciuto nel file: " + e.getMessage());
            }


        }

        //Passo 2) Se File non trovato, vuoto o se ci sono stati errori
        //Inizializza il catalogo di default e lo salva su File
        System.out.println("[INFO] File non disponibile o vuoto. Inizializzazione dei dati predefiniti.");
        List<Strumento> strumentiPredefiniti = creaStrumentiPredefiniti();
        salvaCatalogo(strumentiPredefiniti);

        return strumentiPredefiniti;
    }

    /**
     * Salva la lista degli strumenti nel file di testo utilizzandto PrintWriter
     * Ogni strumento viene scritto su una linea separata nel formato ID;TIPO;STATO
     *
     * @param strumenti la lista degli strumenti da memorizzare
     */
    public void salvaCatalogo(List<Strumento> strumenti ) {

        //Oggetto PrintWriter per la scrittura su file
        try (PrintWriter writer = new PrintWriter(new File(FILE_NAME))) {
            for(Strumento s: strumenti ) {
                //Costruisce la riga con separatore ";"
                String riga = s.getId() + ";" + s.getTipo() + ";" + s.getStato().name();
                writer.println(riga);
            }

            System.out.println("[INFO] Catalogo salvato correttamente su file.");

        }
        catch( FileNotFoundException e ) {
            System.err.println("[ERRORE] Impossibile creare o scrivere su file di testo: " + e.getMessage());
        }
    }

    /**
     * Crea una lista di strumenti predefiniti
     *
     * @return lista strumenti predefiniti
     */
    private List<Strumento> creaStrumentiPredefiniti () {
        List<Strumento> defaultList = new ArrayList<>();

        defaultList.add(new Strumento("OSC-01", "Oscilloscopio"));
        defaultList.add(new Strumento("MUL-01", "Multimetro"));
        defaultList.add(new Strumento("GEN-01", "Generatore"));
        defaultList.add(new Strumento("MUL-02", "Multimetro"));

        return defaultList;

    }
}
